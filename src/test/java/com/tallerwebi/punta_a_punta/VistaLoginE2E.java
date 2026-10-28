package com.tallerwebi.punta_a_punta;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaNuevoUsuario;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VistaLoginE2E {

  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  VistaLogin vistaLogin;

  @BeforeAll
  static void abrirNavegador() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch();
    //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500));
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
    vistaLogin = new VistaLogin(page);
  }

  @AfterEach
  void cerrarContexto() {
    context.close();
  }

  @Test
  void deberiaMostrarIniciarSesionEnElEncabezadoSiNoHayUsuarioLogueado() {
    entoncesElEncabezadoDeberiaMostrarIniciarSesion();
  }

  @Test
  void deberiaDarUnErrorAlIntentarIniciarSesionConUnUsuarioQueNoExiste() {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("damian@unlam.edu.ar", "unlam");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaVerUnMensajeDeError();
  }

  @Test
  void deberiaNavegarAlHomeSiElUsuarioExiste() throws MalformedURLException {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("test@unlam.edu.ar", "test");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVistaDeHome();
  }

  @Test
  void deberiaMostrarElNombreDeUsuarioEnElEncabezadoLuegoDeIniciarSesion() {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("test@unlam.edu.ar", "test");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesElEncabezadoDeberiaMostrarElNombre("usuarioPrueba");
  }

  @Test
  void deberiaVolverAMostrarIniciarSesionLuegoDeCerrarSesion() {
    dadoQueElUsuarioCargaSusDatosDeLoginCon("test@unlam.edu.ar", "test");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesElEncabezadoDeberiaMostrarElNombre("usuarioPrueba");
    cuandoElUsuarioCierraSesion();
    entoncesElEncabezadoDeberiaMostrarIniciarSesion();
  }

  @Test
  void deberiaRegistrarUnUsuarioEIniciarSesionExistosamente() throws MalformedURLException {
    dadoQueElUsuarioNavegaALaVistaDeRegistro();
    dadoQueElUsuarioSeRegistraCon("juan", "juan@unlam.edu.ar", "123456");
    dadoQueElUsuarioEstaEnLaVistaDeLogin();
    dadoQueElUsuarioCargaSusDatosDeLoginCon("juan@unlam.edu.ar", "123456");
    cuandoElUsuarioTocaElBotonDeLogin();
    entoncesDeberiaSerRedirigidoALaVistaDeHome();
    entoncesElEncabezadoDeberiaMostrarElNombre("juan");
  }

  private void cuandoElUsuarioCierraSesion() {
    vistaLogin.darClickEnCerrarSesion();
  }

  private void entoncesElEncabezadoDeberiaMostrarIniciarSesion() {
    String encabezado = vistaLogin.obtenerTextoDelEncabezado();
    assertThat(encabezado, containsString("Iniciar Sesión"));
  }

  private void entoncesElEncabezadoDeberiaMostrarElNombre(String nombre) {
    vistaLogin.esperarAlHome();
    String encabezado = vistaLogin.obtenerTextoDelEncabezado();
    assertThat(encabezado, containsString("Home"));
    assertThat(encabezado, containsString(nombre));
    assertThat(encabezado, not(containsString("Iniciar Sesión")));
  }

  private void dadoQueElUsuarioEstaEnLaVistaDeLogin() throws MalformedURLException {
    URL urlLogin = vistaLogin.obtenerURLActual();
    assertThat(urlLogin.getPath(), matchesPattern("^/spring/login(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void cuandoElUsuarioTocaElBotonDeLogin() {
    vistaLogin.darClickEnIniciarSesion();
  }

  private void entoncesDeberiaSerRedirigidoALaVistaDeHome() throws MalformedURLException {
    URL url = vistaLogin.obtenerURLActual();
    assertThat(url.getPath(), matchesPattern("^/spring/home(?:;jsessionid=[^/\\s]+)?$"));
  }

  private void entoncesDeberiaVerUnMensajeDeError() {
    String texto = vistaLogin.obtenerMensajeDeError();
    assertThat(texto, containsString("El email o la contraseña son incorrectos."));
  }

  private void dadoQueElUsuarioCargaSusDatosDeLoginCon(String email, String clave) {
    vistaLogin.escribirEMAIL(email);
    vistaLogin.escribirClave(clave);
  }

  private void dadoQueElUsuarioNavegaALaVistaDeRegistro() {
    vistaLogin.darClickEnRegistrarse();
  }

  private void dadoQueElUsuarioSeRegistraCon(String nombre, String email, String clave) {
    VistaNuevoUsuario vistaNuevoUsuario = new VistaNuevoUsuario(context.pages().get(0));
    vistaNuevoUsuario.escribirNombreUsuario(nombre);
    vistaNuevoUsuario.escribirEMAIL(email);
    vistaNuevoUsuario.escribirClave(clave);
    vistaNuevoUsuario.escribirConfirmacionDeClave(clave);
    vistaNuevoUsuario.darClickEnRegistrarme();
  }
}
