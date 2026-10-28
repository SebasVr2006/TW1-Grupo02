package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

public class ServicioMercadoPagoSincronizacionTest {

  private static final String ORDER_ID = "ORD123";
  private static final String URL_CHECKOUT = "https://www.mercadopago.com.ar/checkout/ejemplo";

  private RepositorioPago repositorioPago;
  private HttpClient httpClient;
  private HttpResponse<String> respuestaHttp;
  private ServicioMercadoPagoImpl servicioMercadoPago;
  private Usuario usuario;
  private Reserva reserva;
  private Pago pago;

  private EstadoReserva estado;
  private String checkoutUrl;
  private RuntimeException excepcion;

  @BeforeEach
  @SuppressWarnings("unchecked")
  public void setUp() throws Exception {
    repositorioPago = mock(RepositorioPago.class);
    httpClient = mock(HttpClient.class);
    respuestaHttp = mock(HttpResponse.class);
    servicioMercadoPago = new ServicioMercadoPagoImpl(repositorioPago, httpClient, "TEST-token");

    usuario = mock(Usuario.class);
    when(usuario.getId()).thenReturn(2L);
    when(usuario.getEmail()).thenReturn("usuario@ejemplo.com");

    reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setTotal(100.0);

    pago = new Pago();
    pago.setReserva(reserva);
    pago.setMercadoPagoOrderId(ORDER_ID);

    when(
      httpClient.send(
        any(HttpRequest.class),
        ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()
      )
    )
      .thenReturn(respuestaHttp);
  }

  @Test
  public void dadaUnaOrdenProcesadaCuandoSincronizoPorReservaEntoncesLaReservaQuedaPagada() {
    givenUnPagoGuardadoParaLaReserva();
    givenMercadoPagoResponde(200, "{\"status\":\"processed\"}");

    whenSincronizoPorReserva();

    thenElEstadoEs(EstadoReserva.PAGADA);
  }

  @Test
  public void dadaUnaOrdenEnProcesoCuandoSincronizoPorReservaEntoncesSigueasPendiente() {
    givenUnPagoGuardadoParaLaReserva();
    givenMercadoPagoResponde(200, "{\"status\":\"processing\"}");

    whenSincronizoPorReserva();

    thenElEstadoEs(EstadoReserva.PENDIENTE_DE_PAGO);
  }

  @Test
  public void dadaUnaReservaSinPagoCuandoSincronizoEntoncesLanzaExcepcion() {
    whenIntentoSincronizarPorReserva();

    thenSeLanzaUnaExcepcion(IllegalArgumentException.class);
  }

  @Test
  public void dadaUnaReservaDeOtroUsuarioCuandoSincronizoEntoncesLanzaExcepcion() {
    givenUnPagoGuardadoParaLaReserva();
    Usuario otroUsuario = mock(Usuario.class);
    when(otroUsuario.getId()).thenReturn(3L);

    excepcion =
      assertThrows(
        IllegalArgumentException.class,
        () -> servicioMercadoPago.sincronizarPorReserva(1L, otroUsuario)
      );

    thenSeLanzaUnaExcepcion(IllegalArgumentException.class);
  }

  @Test
  public void dadaUnaOrdenProcesadaCuandoSincronizoPorOrdenEntoncesLaReservaQuedaPagada() {
    when(repositorioPago.buscarPorMercadoPagoOrderId(ORDER_ID)).thenReturn(pago);
    givenMercadoPagoResponde(200, "{\"status\":\"processed\"}");

    servicioMercadoPago.sincronizarPorOrden(ORDER_ID);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.PAGADA)));
  }

  @Test
  public void dadaUnaOrdenDesconocidaCuandoSincronizoPorOrdenEntoncesNoConsultaMercadoPago() {
    servicioMercadoPago.sincronizarPorOrden("ORD-DESCONOCIDA");

    verifyNoInteractions(httpClient);
  }

  @Test
  public void dadaUnaRespuestaConErrorCuandoSincronizoEntoncesLanzaExcepcion() {
    givenUnPagoGuardadoParaLaReserva();
    givenMercadoPagoResponde(500, "error");

    whenIntentoSincronizarPorReserva();

    thenSeLanzaUnaExcepcion(IllegalStateException.class);
  }

  @Test
  public void dadoQueFaltaElTokenCuandoSincronizoEntoncesLanzaExcepcion() {
    servicioMercadoPago = new ServicioMercadoPagoImpl(repositorioPago, httpClient, null);
    givenUnPagoGuardadoParaLaReserva();

    whenIntentoSincronizarPorReserva();

    thenSeLanzaUnaExcepcion(IllegalStateException.class);
  }

  @Test
  public void dadaUnaReservaSinPagoCuandoCreoCheckoutEntoncesDevuelveLaUrlYGuardaElPago() {
    Reserva reservaGuardada = givenUnaReservaGuardada();
    givenMercadoPagoResponde(
      200,
      "{\"id\":\"" + ORDER_ID + "\",\"checkout_url\":\"" + URL_CHECKOUT + "\"}"
    );

    checkoutUrl = servicioMercadoPago.crearCheckout(reservaGuardada, usuario);

    assertThat(checkoutUrl, is(equalTo(URL_CHECKOUT)));
    verify(repositorioPago).guardar(any(Pago.class));
  }

  @Test
  public void dadaUnaRespuestaSinCheckoutUrlCuandoCreoCheckoutEntoncesLanzaExcepcion() {
    Reserva reservaGuardada = givenUnaReservaGuardada();
    givenMercadoPagoResponde(200, "{\"id\":\"" + ORDER_ID + "\"}");

    excepcion =
      assertThrows(
        IllegalStateException.class,
        () -> servicioMercadoPago.crearCheckout(reservaGuardada, usuario)
      );

    thenSeLanzaUnaExcepcion(IllegalStateException.class);
  }

  private void givenUnPagoGuardadoParaLaReserva() {
    when(repositorioPago.buscarPorReserva(1L)).thenReturn(pago);
  }

  private Reserva givenUnaReservaGuardada() {
    Reserva reservaGuardada = mock(Reserva.class);
    when(reservaGuardada.getId()).thenReturn(1L);
    when(reservaGuardada.getTotal()).thenReturn(100.0);
    when(reservaGuardada.getUsuario()).thenReturn(usuario);
    return reservaGuardada;
  }

  private void givenMercadoPagoResponde(int codigo, String cuerpo) {
    when(respuestaHttp.statusCode()).thenReturn(codigo);
    when(respuestaHttp.body()).thenReturn(cuerpo);
  }

  private void whenSincronizoPorReserva() {
    estado = servicioMercadoPago.sincronizarPorReserva(1L, usuario);
  }

  private void whenIntentoSincronizarPorReserva() {
    excepcion =
      assertThrows(
        RuntimeException.class,
        () -> servicioMercadoPago.sincronizarPorReserva(1L, usuario)
      );
  }

  private void thenElEstadoEs(EstadoReserva esperado) {
    assertThat(estado, is(equalTo(esperado)));
  }

  private void thenSeLanzaUnaExcepcion(Class<? extends RuntimeException> tipo) {
    assertThat(tipo.isInstance(excepcion), is(true));
  }
}
