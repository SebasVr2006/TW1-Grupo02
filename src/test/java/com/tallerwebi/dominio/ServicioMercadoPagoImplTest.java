package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioMercadoPagoImplTest {

  private RepositorioPago repositorioPago;
  private ServicioMercadoPagoImpl servicioMercadoPago;
  private Reserva reserva;
  private Usuario usuario;

  private String resultado;
  private RuntimeException excepcion;

  @BeforeEach
  public void setUp() {
    repositorioPago = mock(RepositorioPago.class);
    servicioMercadoPago = new ServicioMercadoPagoImpl(repositorioPago);
    reserva = mock(Reserva.class);
    usuario = mock(Usuario.class);
  }

  @Test
  public void dadoUnaReservaNulaCuandoCreoCheckoutEntoncesLanzaExcepcion() {
    givenUnaReservaNula();
    whenIntentoCrearCheckout();
    thenSeLanzaUnaExcepcion();
  }

  @Test
  public void dadoUnUsuarioNuloCuandoCreoCheckoutEntoncesLanzaExcepcion() {
    givenUnUsuarioNulo();
    whenIntentoCrearCheckout();
    thenSeLanzaUnaExcepcion();
  }

  @Test
  public void dadoUnCheckoutExistenteCuandoCreoCheckoutEntoncesDevuelveLaUrlGuardada() {
    givenUnaReservaConCheckoutExistente();
    whenCreoCheckout();
    thenDevuelveLaUrlGuardada();
  }

  @Test
  public void dadoUnCheckoutExistenteCuandoCreoCheckoutEntoncesNoGuardaOtroPago() {
    givenUnaReservaConCheckoutExistente();
    whenCreoCheckout();
    thenNoSeGuardaOtroPago();
  }

  private void givenUnaReservaNula() {
    reserva = null;
  }

  private void givenUnUsuarioNulo() {
    usuario = null;
  }

  private void givenUnaReservaConCheckoutExistente() {
    when(reserva.getId()).thenReturn(1L);
    when(reserva.getTotal()).thenReturn(100.0);
    when(reserva.getUsuario()).thenReturn(usuario);

    when(usuario.getId()).thenReturn(2L);
    when(usuario.getEmail()).thenReturn("usuario@ejemplo.com");

    Pago pagoExistente = new Pago();
    pagoExistente.setReserva(reserva);
    pagoExistente.setCheckoutUrl("https://www.mercadopago.com/checkout/ejemplo");

    when(repositorioPago.buscarPorReserva(1L)).thenReturn(pagoExistente);
  }

  private void whenIntentoCrearCheckout() {
    excepcion =
      assertThrows(
        RuntimeException.class,
        () -> servicioMercadoPago.crearCheckout(reserva, usuario)
      );
  }

  private void whenCreoCheckout() {
    resultado = servicioMercadoPago.crearCheckout(reserva, usuario);
  }

  private void thenSeLanzaUnaExcepcion() {
    assertThat(excepcion, notNullValue());
  }

  private void thenDevuelveLaUrlGuardada() {
    assertThat(resultado, equalTo("https://www.mercadopago.com/checkout/ejemplo"));
  }

  private void thenNoSeGuardaOtroPago() {
    verify(repositorioPago).buscarPorReserva(1L);
    verify(repositorioPago, org.mockito.Mockito.never())
      .guardar(org.mockito.ArgumentMatchers.any(Pago.class));
  }
}
