package com.tallerwebi.dominio;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioMercadoPago")
@Transactional
public class ServicioMercadoPagoImpl implements ServicioMercadoPago {

  private static final String URL_ORDERS = "https://api.mercadopago.com/v1/orders";

  private static final String URL_RESULTADO = "http://localhost:8080/spring/pago/resultado";
  private static final String ESTADO_ORDEN_PROCESADA = "processed";
  private final RepositorioPago repositorioPago;
  private final HttpClient httpClient;
  private final String tokenAcceso;
  private final ObjectMapper objectMapper;

  @Autowired
  public ServicioMercadoPagoImpl(RepositorioPago repositorioPago) {
    this(repositorioPago, HttpClient.newHttpClient(), System.getenv("MERCADOPAGO_ACCESS_TOKEN"));
  }

  public ServicioMercadoPagoImpl(
    RepositorioPago repositorioPago,
    HttpClient httpClient,
    String tokenAcceso
  ) {
    this.repositorioPago = repositorioPago;
    this.httpClient = httpClient;
    this.tokenAcceso = tokenAcceso;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public String crearCheckout(Reserva reserva, Usuario usuario) {
    validarDatos(reserva, usuario);

    Pago pagoExistente = repositorioPago.buscarPorReserva(reserva.getId());

    if (tieneCheckout(pagoExistente)) {
      return pagoExistente.getCheckoutUrl();
    }

    String accessToken = obtenerAccessToken();
    String importe = formatearImporte(reserva);
    HttpRequest peticion = construirPeticion(reserva, usuario, importe, accessToken);

    JsonNode respuesta = enviarPeticion(peticion);
    return guardarPago(reserva, pagoExistente, respuesta);
  }

  @Override
  public EstadoReserva sincronizarPorReserva(Long idReserva, Usuario usuario) {
    Pago pago = repositorioPago.buscarPorReserva(idReserva);
    if (
      pago == null ||
      usuario == null ||
      !pago.getReserva().getUsuario().getId().equals(usuario.getId())
    ) {
      throw new IllegalArgumentException("No se encontró un pago para esa reserva.");
    }
    return sincronizar(pago);
  }

  @Override
  public void sincronizarPorOrden(String orderId) {
    Pago pago = repositorioPago.buscarPorMercadoPagoOrderId(orderId);
    if (pago != null) {
      sincronizar(pago);
    }
  }

  private EstadoReserva sincronizar(Pago pago) {
    JsonNode orden = enviarPeticion(construirConsulta(pago.getMercadoPagoOrderId()));
    Reserva reserva = pago.getReserva();
    if (ESTADO_ORDEN_PROCESADA.equals(orden.path("status").asText())) {
      reserva.setEstado(EstadoReserva.PAGADA);
    }
    return reserva.getEstado();
  }

  private HttpRequest construirConsulta(String orderId) {
    return HttpRequest
      .newBuilder()
      .uri(URI.create(URL_ORDERS + "/" + orderId))
      .header("Authorization", "Bearer " + obtenerAccessToken())
      .GET()
      .build();
  }

  private boolean tieneCheckout(Pago pago) {
    return pago != null && pago.getCheckoutUrl() != null;
  }

  private String obtenerAccessToken() {
    if (tokenAcceso == null || tokenAcceso.isBlank()) {
      throw new IllegalStateException("Falta configurar MERCADOPAGO_ACCESS_TOKEN.");
    }

    return tokenAcceso;
  }

  private String formatearImporte(Reserva reserva) {
    return String.format(Locale.US, "%.2f", reserva.getTotal());
  }

  private HttpRequest construirPeticion(
    Reserva reserva,
    Usuario usuario,
    String importe,
    String accessToken
  ) {
    ObjectNode solicitud = construirSolicitud(reserva, usuario, importe);

    String claveIdempotencia = UUID
      .nameUUIDFromBytes(
        ("flynow-reserva-" + reserva.getId() + "-" + reserva.getFechaCreacion()).getBytes(
            StandardCharsets.UTF_8
          )
      )
      .toString();

    try {
      return HttpRequest
        .newBuilder()
        .uri(URI.create(URL_ORDERS))
        .header("Authorization", "Bearer " + accessToken)
        .header("Content-Type", "application/json")
        .header("X-Idempotency-Key", claveIdempotencia)
        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(solicitud)))
        .build();
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo preparar la solicitud de pago.", e);
    }
  }

  private ObjectNode construirSolicitud(Reserva reserva, Usuario usuario, String importe) {
    ObjectNode solicitud = objectMapper.createObjectNode();
    solicitud.put("type", "online");
    solicitud.put("processing_mode", "manual");
    solicitud.put("total_amount", importe);
    solicitud.put("external_reference", reserva.getId().toString());
    solicitud.put("description", "Reserva FlyNow");

    ObjectNode online = solicitud.putObject("config").putObject("online");
    String urlRetorno = URL_RESULTADO + "?reserva=" + reserva.getId();
    online.put("success_url", urlRetorno);
    online.put("failure_url", urlRetorno);
    online.put("pending_url", urlRetorno);
    online.put("auto_return", "approved");

    ObjectNode comprador = solicitud.putObject("payer");
    comprador.put("email", usuario.getEmail());

    ObjectNode item = solicitud.putArray("items").addObject();
    item.put("title", "Reserva FlyNow #" + reserva.getId());
    item.put("quantity", 1);
    item.put("unit_price", importe);

    return solicitud;
  }

  private JsonNode enviarPeticion(HttpRequest peticion) {
    try {
      HttpResponse<String> respuesta = httpClient.send(
        peticion,
        HttpResponse.BodyHandlers.ofString()
      );

      validarRespuestaHttp(respuesta);
      return objectMapper.readTree(respuesta.body());
    } catch (IOException e) {
      throw new IllegalStateException("No se pudo comunicar con Mercado Pago.", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Se interrumpió la comunicación con Mercado Pago.", e);
    }
  }

  private void validarRespuestaHttp(HttpResponse<String> respuesta) {
    if (respuesta.statusCode() < 200 || respuesta.statusCode() >= 300) {
      throw new IllegalStateException(
        "Mercado Pago rechazó la creación de la orden. " +
        "Código HTTP: " +
        respuesta.statusCode() +
        ". Respuesta: " +
        respuesta.body()
      );
    }
  }

  private String guardarPago(Reserva reserva, Pago pagoExistente, JsonNode respuesta) {
    String orderId = textoRequerido(respuesta, "id");
    String checkoutUrl = textoRequerido(respuesta, "checkout_url");

    Pago pago = pagoExistente;

    if (pago == null) {
      pago = new Pago();
      pago.setReserva(reserva);
      repositorioPago.guardar(pago);
    }

    pago.setMercadoPagoOrderId(orderId);
    pago.setCheckoutUrl(checkoutUrl);

    return checkoutUrl;
  }

  private void validarDatos(Reserva reserva, Usuario usuario) {
    validarReserva(reserva);
    validarUsuario(usuario);
    validarImporte(reserva);
    validarPropietario(reserva, usuario);
  }

  private void validarReserva(Reserva reserva) {
    if (reserva == null || reserva.getId() == null) {
      throw new IllegalArgumentException(
        "La reserva debe estar guardada antes de iniciar el pago."
      );
    }
  }

  private void validarUsuario(Usuario usuario) {
    if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()) {
      throw new IllegalArgumentException("El usuario debe tener un email para iniciar el pago.");
    }

    if (usuario.getId() == null) {
      throw new IllegalArgumentException(
        "El usuario debe estar identificado para iniciar el pago."
      );
    }
  }

  private void validarImporte(Reserva reserva) {
    if (reserva.getTotal() == null || reserva.getTotal() <= 0) {
      throw new IllegalArgumentException("El importe de la reserva debe ser mayor que cero.");
    }
  }

  private void validarPropietario(Reserva reserva, Usuario usuario) {
    if (
      reserva.getUsuario() == null ||
      reserva.getUsuario().getId() == null ||
      !reserva.getUsuario().getId().equals(usuario.getId())
    ) {
      throw new IllegalArgumentException("La reserva no pertenece al usuario actual.");
    }
  }

  private String textoRequerido(JsonNode nodo, String campo) {
    JsonNode valor = nodo.get(campo);

    if (valor == null || valor.isNull() || valor.asText().isBlank()) {
      throw new IllegalStateException("Mercado Pago no devolvió el campo esperado: " + campo);
    }

    return valor.asText();
  }
}
