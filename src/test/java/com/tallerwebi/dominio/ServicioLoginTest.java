package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioLoginTest {

  private ServicioLogin servicioLogin;
  private RepositorioUsuario repositorioUsuarioMock;

  @BeforeEach
  public void init() {
    this.repositorioUsuarioMock = mock(RepositorioUsuario.class);
    this.servicioLogin = new ServicioLoginImpl(this.repositorioUsuarioMock);
  }

  @Test
  public void consultarUsuarioDeberiaLlamarAlRepositorio() {
    // Preparación
    String email = "test@test.com";
    String password = "password";
    Usuario usuarioEsperado = new Usuario();

    when(this.repositorioUsuarioMock.buscarUsuario(email, password)).thenReturn(usuarioEsperado);

    // Ejecución
    Usuario usuarioObtenido = this.servicioLogin.consultarUsuario(email, password);

    // Validación
    assertThat(usuarioObtenido, equalTo(usuarioEsperado));
    verify(this.repositorioUsuarioMock, times(1)).buscarUsuario(email, password);
  }

  @Test
  public void registrarUsuarioSiNoExisteDeberiaGuardarlo() throws UsuarioExistente {
    // Preparación
    Usuario usuario = new Usuario();
    usuario.setEmail("nuevo@test.com");
    usuario.setPassword("123");

    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(null);

    // Ejecución
    this.servicioLogin.registrar(usuario);

    // Validación
    verify(this.repositorioUsuarioMock, times(1)).buscar(usuario.getEmail());
    verify(this.repositorioUsuarioMock, times(1)).guardar(usuario);
  }

  @Test
  public void registrarUsuarioSiExisteDeberiaLanzarExcepcion() {
    // Preparación
    Usuario usuario = new Usuario();
    usuario.setEmail("existe@test.com");
    usuario.setPassword("otra-password");

    Usuario usuarioExistente = new Usuario();
    usuarioExistente.setEmail("existe@test.com");
    usuarioExistente.setPassword("password-anterior");

    when(this.repositorioUsuarioMock.buscar(usuario.getEmail())).thenReturn(usuarioExistente);

    // Ejecución y validación
    assertThrows(UsuarioExistente.class, () -> this.servicioLogin.registrar(usuario));

    verify(this.repositorioUsuarioMock, times(1)).buscar(usuario.getEmail());
    verify(this.repositorioUsuarioMock, never()).guardar(usuario);
  }
}
