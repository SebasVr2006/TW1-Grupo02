package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

import org.junit.jupiter.api.Test;

public class PagoTest {

  private Pago pago;
  private Reserva reserva;

  @Test
  public void crearPagoDeberiaTenerIdNulo() {
    givenTengoUnPagoNuevo();

    whenConsultoElId();

    thenElIdEsNulo();
  }

  private void givenTengoUnPagoNuevo() {
    pago = new Pago();
  }

  private void whenConsultoElId() {
    // El ID se genera al persistir la entidad.
  }

  private void thenElIdEsNulo() {
    assertThat(pago.getId(), nullValue());
  }

  @Test
  public void asignarReservaDeberiaConservarLaReserva() {
    givenTengoUnPagoYUnaReserva();

    whenAsignoLaReservaAlPago();

    thenElPagoConservaLaReserva();
  }

  private void givenTengoUnPagoYUnaReserva() {
    pago = new Pago();
    reserva = new Reserva();
  }

  private void whenAsignoLaReservaAlPago() {
    pago.setReserva(reserva);
  }

  private void thenElPagoConservaLaReserva() {
    assertThat(pago.getReserva(), equalTo(reserva));
  }

  @Test
  public void asignarOrdenMercadoPagoDeberiaConservarElIdentificador() {
    givenTengoUnPagoNuevo();

    whenAsignoElIdentificadorDeLaOrden();

    thenElPagoConservaElIdentificadorDeLaOrden();
  }

  private void whenAsignoElIdentificadorDeLaOrden() {
    pago.setMercadoPagoOrderId("orden-123");
  }

  private void thenElPagoConservaElIdentificadorDeLaOrden() {
    assertThat(pago.getMercadoPagoOrderId(), equalTo("orden-123"));
  }

  @Test
  public void asignarCheckoutUrlDeberiaConservarLaUrl() {
    givenTengoUnPagoNuevo();

    whenAsignoLaUrlDelCheckout();

    thenElPagoConservaLaUrlDelCheckout();
  }

  private void whenAsignoLaUrlDelCheckout() {
    pago.setCheckoutUrl("https://ejemplo.com/pagar");
  }

  private void thenElPagoConservaLaUrlDelCheckout() {
    assertThat(pago.getCheckoutUrl(), equalTo("https://ejemplo.com/pagar"));
  }

  @Test
  public void crearPagoSinAsignarCheckoutDeberiaTenerUrlNula() {
    givenTengoUnPagoNuevo();

    whenConsultoLaUrlDelCheckout();

    thenLaUrlDelCheckoutEsNula();
  }

  private void whenConsultoLaUrlDelCheckout() {
    // Se consulta el estado inicial del atributo.
  }

  private void thenLaUrlDelCheckoutEsNula() {
    assertThat(pago.getCheckoutUrl(), nullValue());
  }
}
