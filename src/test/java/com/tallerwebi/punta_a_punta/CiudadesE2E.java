package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaCiudades;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CiudadesE2E {

  private static final String RESPUESTA_CON_DOS_CIUDADES =
    "[{\"nombre\":\"Buenos Aires\",\"pais\":\"Argentina\",\"provincia\":\"Buenos Aires F.D.\"}," +
    "{\"nombre\":\"Buenos Aires\",\"pais\":\"Costa Rica\",\"provincia\":null}]";

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  Page page;
  VistaCiudades vistaCiudades;

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
    page = context.newPage();
    vistaCiudades = new VistaCiudades(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaSugerirCiudadesAlEscribirEnElDestino() {
    dadoQueElUsuarioInicioSesion();
    dadoQueLaApiDeCiudadesDevuelveDosCiudades();
    dadoQueElUsuarioEstaEnLaPlanificacion();

    cuandoEscribeEnElDestino("Buen");

    entoncesVeDosSugerenciasDeBuenosAires();
  }

  @Test
  void noDeberiaSugerirCiudadesConUnaSolaLetra() {
    dadoQueElUsuarioInicioSesion();
    dadoQueLaApiDeCiudadesDevuelveDosCiudades();
    dadoQueElUsuarioEstaEnLaPlanificacion();

    cuandoEscribeEnElDestino("B");

    entoncesNoVeSugerencias();
  }

  private void dadoQueElUsuarioInicioSesion() {
    VistaLogin vistaLogin = new VistaLogin(page);
    vistaLogin.escribirEMAIL("test@unlam.edu.ar");
    vistaLogin.escribirClave("test");
    vistaLogin.darClickEnIniciarSesion();
    page.waitForURL("**/home**");
  }

  private void dadoQueLaApiDeCiudadesDevuelveDosCiudades() {
    vistaCiudades.simularRespuestaDeLaApiDeCiudades(RESPUESTA_CON_DOS_CIUDADES);
  }

  private void dadoQueElUsuarioEstaEnLaPlanificacion() {
    vistaCiudades.irAPlanificacion();
  }

  private void cuandoEscribeEnElDestino(String texto) {
    vistaCiudades.escribirEnElDestino(texto);
  }

  private void entoncesVeDosSugerenciasDeBuenosAires() {
    vistaCiudades.esperarSugerencias(2);
    assertThat(vistaCiudades.contarSugerencias(), is(equalTo(2)));
    assertThat(vistaCiudades.contarSugerenciasConValor("Buenos Aires, Argentina"), is(equalTo(1)));
    assertThat(vistaCiudades.contarSugerenciasConValor("Buenos Aires, Costa Rica"), is(equalTo(1)));
  }

  private void entoncesNoVeSugerencias() {
    page.waitForTimeout(600);
    assertThat(vistaCiudades.contarSugerencias(), is(equalTo(0)));
  }
}
