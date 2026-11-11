package com.tallerwebi.infraestructura;

import com.fasterxml.jackson.databind.JsonNode;
import com.tallerwebi.dominio.Hotel;
import com.tallerwebi.dominio.ProveedorHoteles;
import com.tallerwebi.dominio.excepcion.HotelesNoDisponiblesException;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProveedorHotelesSerpApi implements ProveedorHoteles {

  private static final String URL_BUSQUEDA = "https://serpapi.com/search.json";
  private static final String MOTOR_HOTELES = "google_hotels";
  private static final String MONEDA = "USD";
  private static final String IDIOMA = "es";
  private static final String MENSAJE_SIN_RESULTADOS = "hasn't returned any results";
  private static final Duration DURACION_RESULTADOS = Duration.ofMinutes(60);
  private static final int TIMEOUT_CONEXION_MS = 5000;
  private static final int TIMEOUT_LECTURA_MS = 15000;

  private final RestTemplate restTemplate;
  private final String claveApi;
  private final Clock reloj;
  private final Map<String, ResultadoGuardado> resultadosGuardados = new ConcurrentHashMap<>();

  @Autowired
  public ProveedorHotelesSerpApi() {
    this(crearRestTemplate(), System.getenv("SERPAPI_API_KEY"), Clock.systemUTC());
  }

  public ProveedorHotelesSerpApi(RestTemplate restTemplate, String claveApi, Clock reloj) {
    this.restTemplate = restTemplate;
    this.claveApi = claveApi;
    this.reloj = reloj;
  }

  @Override
  public List<Hotel> buscar(String lugar, String fechaEntrada, String fechaSalida, int maximo) {
    validarClaveApi();

    String clave = armarClave(lugar, fechaEntrada, fechaSalida, maximo);
    List<Hotel> guardados = buscarGuardados(clave);
    if (!guardados.isEmpty()) {
      return guardados;
    }

    JsonNode respuesta = consultar(armarUri(lugar, fechaEntrada, fechaSalida));
    if (esRespuestaSinResultados(respuesta)) {
      return new ArrayList<>();
    }
    validarRespuesta(respuesta);

    List<Hotel> hoteles = convertir(respuesta, maximo);
    guardar(clave, hoteles);
    return hoteles;
  }

  private static RestTemplate crearRestTemplate() {
    SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
    fabrica.setConnectTimeout(TIMEOUT_CONEXION_MS);
    fabrica.setReadTimeout(TIMEOUT_LECTURA_MS);
    return new RestTemplate(fabrica);
  }

  private void validarClaveApi() {
    if (claveApi == null || claveApi.isBlank()) {
      throw new HotelesNoDisponiblesException("El servicio de hoteles no está configurado");
    }
  }

  private String armarClave(String lugar, String fechaEntrada, String fechaSalida, int maximo) {
    return String.join(
      "|",
      lugar.trim().toLowerCase(Locale.ROOT),
      fechaEntrada,
      fechaSalida,
      String.valueOf(maximo)
    );
  }

  private List<Hotel> buscarGuardados(String clave) {
    ResultadoGuardado guardado = resultadosGuardados.get(clave);
    if (guardado == null) {
      return new ArrayList<>();
    }
    if (guardado.vencimiento.isBefore(reloj.instant())) {
      resultadosGuardados.remove(clave);
      return new ArrayList<>();
    }
    return guardado.hoteles;
  }

  private void guardar(String clave, List<Hotel> hoteles) {
    if (!hoteles.isEmpty()) {
      resultadosGuardados.put(
        clave,
        new ResultadoGuardado(List.copyOf(hoteles), reloj.instant().plus(DURACION_RESULTADOS))
      );
    }
  }

  private URI armarUri(String lugar, String fechaEntrada, String fechaSalida) {
    return UriComponentsBuilder
      .fromUriString(URL_BUSQUEDA)
      .queryParam("engine", MOTOR_HOTELES)
      .queryParam("q", "Hoteles en " + lugar.trim())
      .queryParam("check_in_date", fechaEntrada)
      .queryParam("check_out_date", fechaSalida)
      .queryParam("currency", MONEDA)
      .queryParam("hl", IDIOMA)
      .queryParam("api_key", claveApi)
      .build()
      .encode()
      .toUri();
  }

  private JsonNode consultar(URI uri) {
    try {
      return restTemplate.getForObject(uri, JsonNode.class);
    } catch (RestClientException e) {
      throw new HotelesNoDisponiblesException("No se pudo consultar el servicio de hoteles", e);
    }
  }

  private boolean esRespuestaSinResultados(JsonNode respuesta) {
    return (
      respuesta != null && respuesta.path("error").asText("").contains(MENSAJE_SIN_RESULTADOS)
    );
  }

  private void validarRespuesta(JsonNode respuesta) {
    if (respuesta != null && respuesta.hasNonNull("error")) {
      throw new HotelesNoDisponiblesException("El servicio de hoteles devolvió un error");
    }
  }

  private List<Hotel> convertir(JsonNode respuesta, int maximo) {
    List<Hotel> hoteles = new ArrayList<>();
    if (respuesta == null) {
      return hoteles;
    }

    Set<String> nombresVistos = new HashSet<>();
    for (JsonNode propiedad : respuesta.path("properties")) {
      if (hoteles.size() >= maximo) {
        break;
      }
      Hotel hotel = convertirPropiedad(propiedad);
      if (hotel != null && nombresVistos.add(hotel.getNombre().toLowerCase(Locale.ROOT))) {
        hoteles.add(hotel);
      }
    }
    return hoteles;
  }

  private Hotel convertirPropiedad(JsonNode propiedad) {
    String nombre = propiedad.path("name").asText("").trim();
    JsonNode precio = obtenerPrecio(propiedad.path("rate_per_night"));
    if (nombre.isEmpty() || precio == null) {
      return null;
    }

    Hotel hotel = new Hotel(nombre, precio.asDouble());
    String imagen = propiedad.path("images").path(0).path("thumbnail").asText("");
    if (!imagen.isBlank()) {
      hotel.setImagenUrl(imagen);
    }
    if (propiedad.hasNonNull("extracted_hotel_class")) {
      hotel.setEstrellas(propiedad.get("extracted_hotel_class").asInt());
    }
    if (propiedad.hasNonNull("overall_rating")) {
      hotel.setPuntaje(propiedad.get("overall_rating").asDouble());
    }
    return hotel;
  }

  private JsonNode obtenerPrecio(JsonNode tarifa) {
    if (tarifa.hasNonNull("extracted_lowest")) {
      return tarifa.get("extracted_lowest");
    }
    if (tarifa.hasNonNull("extracted_before_taxes_fees")) {
      return tarifa.get("extracted_before_taxes_fees");
    }
    return null;
  }

  private static final class ResultadoGuardado {

    private final List<Hotel> hoteles;
    private final Instant vencimiento;

    private ResultadoGuardado(List<Hotel> hoteles, Instant vencimiento) {
      this.hoteles = hoteles;
      this.vencimiento = vencimiento;
    }
  }
}
