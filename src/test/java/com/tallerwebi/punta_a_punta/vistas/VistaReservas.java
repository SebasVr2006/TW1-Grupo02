package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaReservas extends VistaWeb {

  private static final String BASE = "localhost:8080/spring";

  public VistaReservas(Page page) {
    super(page);
  }

  public void irAPlanificacion() {
    page.navigate(BASE + "/planificacion");
  }

  public void completarPlanificacion(String destino, String fechaInicio, String duracion) {
    this.llenarElElemento("#destino-0", destino);
    this.llenarElElemento("#fechaInicio-0", fechaInicio);
    this.llenarElElemento("#duracion-0", duracion);
    page.selectOption("#alojamiento", "hotel");
  }

  public void darClickEnContinuarPlanificacion() {
    this.darClickEnElElemento("#continuar");
  }

  public void darClickEnVerItinerario() {
    page.locator("a[href*='/itinerario']").first().click();
  }

  public void elegirHotel(String nombreHotel) {
    this.darClickEnElElemento("input[type='radio'][value='" + nombreHotel + "']");
  }

  public void darClickEnVerResumenDelItinerario() {
    this.darClickEnElElemento("form button[type='submit']");
  }

  public void darClickEnContinuarConLaCompra() {
    this.darClickEnElElemento("a.boton-confirmar");
  }

  public void aceptarLosTerminos() {
    page.check("input[name='aceptaTerminos']");
  }

  public void darClickEnConfirmarYContinuarAlPago() {
    this.darClickEnElElemento("form.formulario-confirmacion button[type='submit']");
  }

  public String obtenerTituloDeLaPagina() {
    return this.obtenerTextoDelElemento("main h2");
  }

  public String obtenerAvisoPreviaAlPago() {
    return this.obtenerTextoDelElemento("p.aviso");
  }

  public String obtenerTotalAPagar() {
    return this.obtenerTextoDelElemento("tr.total-a-pagar td:last-child");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("p.mensaje-error");
  }

  public String obtenerEstadoDeLaPrimeraReserva() {
    return this.obtenerTextoDelElemento("article.reserva .estado");
  }

  public String obtenerTextoDeLaPrimeraReserva() {
    return this.obtenerTextoDelElemento("article.reserva");
  }

  private void llenarElElemento(String selectorCSS, String texto) {
    page.fill(selectorCSS, texto);
  }
}
