package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorLoginTest {

  private ControladorLogin controladorLogin;
  private ServicioLogin servicioLoginMock;
  private DatosLogin datosLogin;
  private ModelAndView resultado;

  @BeforeEach
  public void inicializar() {
    this.servicioLoginMock = mock(ServicioLogin.class);
    this.controladorLogin = new ControladorLogin(this.servicioLoginMock);
  }

  @Test
  public void irALoginDeberiaRetornarVistaLogin() {
    whenIngresoAlLogin();

    thenSeMuestraLaVistaLogin();
  }

  @Test
  public void irARegistroDeberiaRetornarVistaRegistro() {
    whenIngresoAlRegistro();

    thenSeMuestraLaVistaRegistro();
  }

  @Test
  public void irAHomeDeberiaRetornarVistaHome() {
    whenIngresoAlHome();

    thenSeMuestraLaVistaHome();
  }

  @Test
  public void registrarUsuarioConDatosValidosDeberiaRegistrarYRedirigirALogin()
    throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();

    whenRegistroElUsuario();

    thenSeRedirigeAlLogin();
    thenSeRegistraElUsuario();
  }

  @Test
  public void registrarUsuarioConNombreVacioDeberiaMostrarError() throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();
    givenElNombreEstaVacio();

    whenRegistroElUsuario();

    thenSeMuestraElErrorDeCamposObligatorios();
    thenNoSeRegistraElUsuario();
  }

  @Test
  public void registrarUsuarioConEmailVacioDeberiaMostrarError() throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();
    givenElEmailEstaVacio();

    whenRegistroElUsuario();

    thenSeMuestraElErrorDeCamposObligatorios();
    thenNoSeRegistraElUsuario();
  }

  @Test
  public void registrarUsuarioConPasswordVacioDeberiaMostrarError() throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();
    givenLaPasswordEstaVacia();

    whenRegistroElUsuario();

    thenSeMuestraElErrorDeCamposObligatorios();
    thenNoSeRegistraElUsuario();
  }

  @Test
  public void registrarUsuarioConConfirmacionVaciaDeberiaMostrarError() throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();
    givenLaConfirmacionEstaVacia();

    whenRegistroElUsuario();

    thenSeMuestraElErrorDeCamposObligatorios();
    thenNoSeRegistraElUsuario();
  }

  @Test
  public void registrarUsuarioConPasswordsDiferentesDeberiaMostrarError() throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();
    givenLasPasswordsSonDiferentes();

    whenRegistroElUsuario();

    thenSeMuestraElErrorDePasswordsDiferentes();
    thenNoSeRegistraElUsuario();
  }

  @Test
  public void registrarUsuarioConEmailExistenteDeberiaMostrarError() throws UsuarioExistente {
    givenTengoDatosDeRegistroValidos();
    givenElEmailYaEstaRegistrado();

    whenRegistroElUsuario();

    thenSeMuestraElErrorDeEmailExistente();
    thenSeIntentaRegistrarElUsuario();
  }

  private void givenTengoDatosDeRegistroValidos() {
    this.datosLogin = new DatosLogin();
    this.datosLogin.setNombreUsuario("usuarioPrueba");
    this.datosLogin.setEmail("usuario@test.com");
    this.datosLogin.setPassword("123456");
    this.datosLogin.setConfirmarPassword("123456");
  }

  private void givenElNombreEstaVacio() {
    this.datosLogin.setNombreUsuario("");
  }

  private void givenElEmailEstaVacio() {
    this.datosLogin.setEmail("");
  }

  private void givenLaPasswordEstaVacia() {
    this.datosLogin.setPassword("");
  }

  private void givenLaConfirmacionEstaVacia() {
    this.datosLogin.setConfirmarPassword("");
  }

  private void givenLasPasswordsSonDiferentes() {
    this.datosLogin.setConfirmarPassword("otraClave");
  }

  private void givenElEmailYaEstaRegistrado() throws UsuarioExistente {
    doThrow(UsuarioExistente.class).when(this.servicioLoginMock).registrar(any(Usuario.class));
  }

  private void whenIngresoAlLogin() {
    this.resultado = this.controladorLogin.irALogin();
  }

  private void whenIngresoAlRegistro() {
    this.resultado = this.controladorLogin.irARegistro();
  }

  private void whenIngresoAlHome() {
    this.resultado = this.controladorLogin.irAHome();
  }

  private void whenRegistroElUsuario() throws UsuarioExistente {
    this.resultado = this.controladorLogin.registrarUsuario(this.datosLogin);
  }

  private void thenSeMuestraLaVistaLogin() {
    assertThat(this.resultado.getViewName(), is(equalTo("login")));
  }

  private void thenSeMuestraLaVistaRegistro() {
    assertThat(this.resultado.getViewName(), is(equalTo("registro")));
  }

  private void thenSeMuestraLaVistaHome() {
    assertThat(this.resultado.getViewName(), is(equalTo("home")));
  }

  private void thenSeRedirigeAlLogin() {
    assertThat(this.resultado.getViewName(), is(equalTo("redirect:/login")));
  }

  private void thenSeRegistraElUsuario() throws UsuarioExistente {
    verify(this.servicioLoginMock).registrar(any(Usuario.class));
  }

  private void thenSeMuestraElErrorDeCamposObligatorios() {
    verificarErrorDeRegistro(this.resultado, "Completá todos los campos.");
  }

  private void thenSeMuestraElErrorDePasswordsDiferentes() {
    verificarErrorDeRegistro(this.resultado, "Las contraseñas no coinciden.");
  }

  private void thenSeMuestraElErrorDeEmailExistente() {
    verificarErrorDeRegistro(this.resultado, "Ya existe una cuenta registrada con ese email.");
  }

  private void thenNoSeRegistraElUsuario() throws UsuarioExistente {
    verify(this.servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  private void thenSeIntentaRegistrarElUsuario() throws UsuarioExistente {
    verify(this.servicioLoginMock).registrar(any(Usuario.class));
  }

  private void verificarErrorDeRegistro(ModelAndView modelAndView, String mensajeEsperado) {
    assertThat(modelAndView.getViewName(), is(equalTo("registro")));
    assertThat(modelAndView.getModel().get("mensajeError"), is(equalTo(mensajeEsperado)));
    assertThat(modelAndView.getModel().containsKey("datosLogin"), is(true));
  }
}
