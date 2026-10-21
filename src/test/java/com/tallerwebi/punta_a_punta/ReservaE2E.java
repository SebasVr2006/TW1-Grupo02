package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaReservas;
import java.net.MalformedURLException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservaE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaReservas vistaReservas;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
  }

  @AfterAll
  static void cerrarNavegador() {
    playwright.close();
  }

  @BeforeEach
  void crearContextoYPagina() {
    ReiniciarDB.limpiarBaseDeDatos();

    context = browser.newContext();
    Page page = context.newPage();
    dadoQueElUsuarioInicioSesion(page);
    vistaReservas = new VistaReservas(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaMostrarLaPaginaPreviaAlPagoAntesDeConfirmar() {
    dadoQueElUsuarioArmoSuItinerarioEnBuenosAires();
    cuandoElUsuarioContinuaConLaCompra();
    entoncesDeberiaVerLaPaginaPreviaAlPagoConElTotal();
  }

  @Test
  void noDeberiaConfirmarSiNoSeAceptanLosTerminos() throws MalformedURLException {
    dadoQueElUsuarioArmoSuItinerarioEnBuenosAires();
    cuandoElUsuarioContinuaConLaCompra();
    cuandoElUsuarioConfirmaSinAceptarLosTerminos();
    entoncesDeberiaVerUnErrorDeTerminos();
  }

  @Test
  void deberiaGuardarLaReservaYMostrarlaEnMisReservasComoPendienteDePago() {
    dadoQueElUsuarioArmoSuItinerarioEnBuenosAires();
    cuandoElUsuarioContinuaConLaCompra();
    cuandoElUsuarioAceptaLosTerminosYConfirma();
    entoncesDeberiaVerLaReservaPendienteDePagoEnMisReservas();
  }

  private void dadoQueElUsuarioInicioSesion(Page page) {
    VistaLogin vistaLogin = new VistaLogin(page);
    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();
    page.waitForURL("**/home**");
  }

  private void dadoQueElUsuarioArmoSuItinerarioEnBuenosAires() {
    vistaReservas.irAPlanificacion();
    vistaReservas.completarPlanificacion("Buenos Aires", "2026-12-01", "3");
    vistaReservas.darClickEnContinuarPlanificacion();
    vistaReservas.darClickEnVerItinerario();
    vistaReservas.elegirHotel("Hotel Palermo Soho");
    vistaReservas.darClickEnVerResumenDelItinerario();
  }

  private void cuandoElUsuarioContinuaConLaCompra() {
    vistaReservas.darClickEnContinuarConLaCompra();
  }

  private void cuandoElUsuarioConfirmaSinAceptarLosTerminos() {
    vistaReservas.darClickEnConfirmarYContinuarAlPago();
  }

  private void cuandoElUsuarioAceptaLosTerminosYConfirma() {
    vistaReservas.aceptarLosTerminos();
    vistaReservas.darClickEnConfirmarYContinuarAlPago();
  }

  private void entoncesDeberiaVerLaPaginaPreviaAlPagoConElTotal() {
    assertThat(vistaReservas.obtenerTituloDeLaPagina(), containsString("Confirmá tu compra"));
    assertThat(vistaReservas.obtenerAvisoPreviaAlPago(), containsString("pendiente de pago"));
    assertThat(vistaReservas.obtenerTotalAPagar(), matchesPattern("(?s).*\\$\\s*[0-9.,]+.*"));
  }

  private void entoncesDeberiaVerUnErrorDeTerminos() throws MalformedURLException {
    assertThat(vistaReservas.obtenerMensajeDeError(), containsString("términos y condiciones"));
    assertThat(
      vistaReservas.obtenerURLActual().getPath(),
      matchesPattern("^/spring/confirmacion-compra(?:;jsessionid=[^/\\s]+)?$")
    );
  }

  private void entoncesDeberiaVerLaReservaPendienteDePagoEnMisReservas() {
    assertThat(
      vistaReservas.obtenerEstadoDeLaPrimeraReserva(),
      containsString("Pendiente de pago")
    );
    assertThat(
      vistaReservas.obtenerTextoDeLaPrimeraReserva(),
      containsString("Hotel Palermo Soho")
    );
  }
}
