package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioLoginTest {

  private ServicioLogin servicioLogin;
  private RepositorioUsuario repositorioUsuarioMock;
  private Usuario usuario;
  private Usuario usuarioObtenido;

  @BeforeEach
  public void init() {
    repositorioUsuarioMock = mock(RepositorioUsuario.class);
    servicioLogin = new ServicioLoginImpl(repositorioUsuarioMock);
  }

  @Test
  public void consultarUsuarioDeberiaLlamarAlRepositorio() {
    givenTengoUnUsuarioRegistradoEnElRepositorio();

    whenConsultoElUsuarioPorEmailYPassword();

    thenObtengoElUsuarioEsperadoYSeConsultaElRepositorio();
  }

  private void givenTengoUnUsuarioRegistradoEnElRepositorio() {
    String email = "test@test.com";
    String password = "password";
    usuario = new Usuario();

    when(repositorioUsuarioMock.buscarUsuario(email, password))
            .thenReturn(usuario);
  }

  private void whenConsultoElUsuarioPorEmailYPassword() {
    usuarioObtenido =
            servicioLogin.consultarUsuario("test@test.com", "password");
  }

  private void thenObtengoElUsuarioEsperadoYSeConsultaElRepositorio() {
    assertThat(usuarioObtenido, equalTo(usuario));
    verify(repositorioUsuarioMock, times(1))
            .buscarUsuario("test@test.com", "password");
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaGuardarlo()
          throws UsuarioExistente {
    givenTengoUnUsuarioNuevoQueNoExiste();

    whenRegistroElUsuario();

    thenSeBuscaYSeGuardaElUsuario();
  }

  private void givenTengoUnUsuarioNuevoQueNoExiste() {
    usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("123");

    when(repositorioUsuarioMock.buscar(usuario.getEmail()))
            .thenReturn(null);
  }

  private void whenRegistroElUsuario() throws UsuarioExistente {
    servicioLogin.registrar(usuario);
  }

  private void thenSeBuscaYSeGuardaElUsuario() {
    verify(repositorioUsuarioMock, times(1)).buscar(usuario.getEmail());
    verify(repositorioUsuarioMock, times(1)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioSiExisteDeberiaLanzarExcepcion() {
    givenTengoUnUsuarioConEmailExistente();

    whenIntentoRegistrarElUsuarioExistente();

    thenSeLanzaLaExcepcionYNoSeGuardaElUsuario();
  }

  private void givenTengoUnUsuarioConEmailExistente() {
    usuario = new Usuario();
    usuario.setEmail("existe@test.com");
    usuario.setPassword("otra-password");

    Usuario usuarioExistente = new Usuario();
    usuarioExistente.setEmail("existe@test.com");
    usuarioExistente.setPassword("password-anterior");

    when(repositorioUsuarioMock.buscar(usuario.getEmail()))
            .thenReturn(usuarioExistente);
  }

  private void whenIntentoRegistrarElUsuarioExistente() {
    assertThrows(
            UsuarioExistente.class,
            () -> servicioLogin.registrar(usuario)
    );
  }

  private void thenSeLanzaLaExcepcionYNoSeGuardaElUsuario() {
    verify(repositorioUsuarioMock, times(1)).buscar(usuario.getEmail());
    verify(repositorioUsuarioMock, never()).guardar(usuario);
  }
}
