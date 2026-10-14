package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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

  @BeforeEach
  public void init() {
    servicioLoginMock = org.mockito.Mockito.mock(ServicioLogin.class);
    controladorLogin = new ControladorLogin(servicioLoginMock);
  }

  @Test
  public void irALoginDeberiaRetornarVistaLogin() {
    ModelAndView modelAndView = controladorLogin.irALogin();

    assertThat(modelAndView.getViewName(), is(equalTo("login")));
  }

  @Test
  public void irARegistroDeberiaRetornarVistaRegistro() {
    ModelAndView modelAndView = controladorLogin.irARegistro();

    assertThat(modelAndView.getViewName(), is(equalTo("registro")));
  }

  @Test
  public void irAHomeDeberiaRetornarVistaHome() {
    ModelAndView modelAndView = controladorLogin.irAHome();

    assertThat(modelAndView.getViewName(), is(equalTo("home")));
  }

  @Test
  public void registrarUsuarioConDatosValidosDeberiaRegistrarYRedirigirALogin()
    throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    assertThat(modelAndView.getViewName(), is(equalTo("redirect:/login")));
    verify(servicioLoginMock).registrar(any(Usuario.class));
  }

  @Test
  public void registrarUsuarioConNombreVacioDeberiaMostrarError() throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();
    datosLogin.setNombreUsuario("");

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    verificarErrorDeRegistro(modelAndView, "Completá todos los campos.");
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarUsuarioConEmailVacioDeberiaMostrarError() throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();
    datosLogin.setEmail("");

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    verificarErrorDeRegistro(modelAndView, "Completá todos los campos.");
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarUsuarioConPasswordVacioDeberiaMostrarError() throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();
    datosLogin.setPassword("");

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    verificarErrorDeRegistro(modelAndView, "Completá todos los campos.");
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarUsuarioConConfirmacionVaciaDeberiaMostrarError() throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();
    datosLogin.setConfirmarPassword("");

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    verificarErrorDeRegistro(modelAndView, "Completá todos los campos.");
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarUsuarioConPasswordsDiferentesDeberiaMostrarError() throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();
    datosLogin.setConfirmarPassword("otraClave");

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    verificarErrorDeRegistro(modelAndView, "Las contraseñas no coinciden.");
    verify(servicioLoginMock, never()).registrar(any(Usuario.class));
  }

  @Test
  public void registrarUsuarioConEmailExistenteDeberiaMostrarError() throws UsuarioExistente {
    DatosLogin datosLogin = crearDatosLoginValidos();

    doThrow(UsuarioExistente.class).when(servicioLoginMock).registrar(any(Usuario.class));

    ModelAndView modelAndView = controladorLogin.registrarUsuario(datosLogin);

    verificarErrorDeRegistro(modelAndView, "Ya existe una cuenta registrada con ese email.");
    verify(servicioLoginMock).registrar(any(Usuario.class));
  }

  private DatosLogin crearDatosLoginValidos() {
    DatosLogin datosLogin = new DatosLogin();
    datosLogin.setNombreUsuario("usuarioPrueba");
    datosLogin.setEmail("usuario@test.com");
    datosLogin.setPassword("123456");
    datosLogin.setConfirmarPassword("123456");
    return datosLogin;
  }

  private void verificarErrorDeRegistro(ModelAndView modelAndView, String mensajeEsperado) {
    assertThat(modelAndView.getViewName(), is(equalTo("registro")));
    assertThat(modelAndView.getModel().get("mensajeError"), is(equalTo(mensajeEsperado)));
    assertThat(modelAndView.getModel().containsKey("datosLogin"), is(true));
  }
}
