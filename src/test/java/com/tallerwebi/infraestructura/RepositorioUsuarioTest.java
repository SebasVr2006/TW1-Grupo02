package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HibernateInfraestructuraTestConfig.class })
public class RepositorioUsuarioTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioUsuario repositorioUsuario;

  @BeforeEach
  public void init() {
    repositorioUsuario = new RepositorioUsuarioImpl(sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnNuevoUsuario() {
    String emailNuevoUsuario = "nuevo.usuario@test.com";

    // Preparación
    Usuario usuario = this.dadoQueTengoUnUsuario("usuarioPrueba", emailNuevoUsuario, "1234");

    // Ejecución
    this.cuandoGuardoUnUsuario(usuario);

    // Validación
    this.entoncesSeGuardoElUsuario(emailNuevoUsuario, usuario);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnUsuarioExistenteCuandoBuscoPorEmailYPassword() {
    String email = "test@test.com";
    String password = "123";

    Usuario usuario = this.dadoQueTengoUnUsuario("usuarioPrueba", email, password);
    this.dadoQueExisteElUsuario(usuario);

    Usuario obtenido = this.cuandoBuscoUnUsuario(email, password);

    this.entoncesElUsuarioObtenidoEsCorrecto(obtenido, usuario);
  }

  @Test
  @Transactional
  public void noDeberiaEncontrarUnUsuarioInexistenteCuandoBuscoPorEmailYPassword() {
    Usuario obtenido = this.cuandoBuscoUnUsuario("test@test.com", "123");

    this.entoncesElUsuarioObtenidoEsNull(obtenido);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnUsuarioExistenteCuandoBuscoPorEmail() {
    String email = "test@test.com";

    Usuario usuario = this.dadoQueTengoUnUsuario("usuarioPrueba", email, "123");
    this.dadoQueExisteElUsuario(usuario);

    Usuario obtenido = this.cuandoObtengoUnUsuarioPorEmail(email);

    this.entoncesElUsuarioObtenidoEsCorrecto(obtenido, usuario);
  }

  @Test
  @Transactional
  public void noDeberiaEncontrarUnUsuarioInexistenteCuandoBuscoPorEmail() {
    Usuario obtenido = this.cuandoObtengoUnUsuarioPorEmail("test@test.com");

    this.entoncesElUsuarioObtenidoEsNull(obtenido);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaModificarUnUsuarioExistente() {
    String email = "test@test.com";

    Usuario usuario = this.dadoQueTengoUnUsuario("usuarioPrueba", email, "123");
    this.dadoQueExisteElUsuario(usuario);

    usuario.setNombreUsuario("nombreActualizado");
    usuario.setPassword("4567");

    this.cuandoModificoUnUsuario(usuario);

    Usuario obtenido = this.cuandoObtengoUnUsuarioPorEmail(email);
    this.entoncesElUsuarioObtenidoEsCorrecto(obtenido, usuario);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaLanzarUnaExcepcionAlIntentarModificarUnUsuarioInexistente() {
    Usuario usuario = this.dadoQueTengoUnUsuario("usuarioInexistente", "noexiste@test.com", "123");

    // Al no estar persistido, el usuario no tiene ID.
    // Por eso, modificar debe lanzar UsuarioNoEncontrado.
    this.entoncesSeLanzaUnaUsuarioNoEncontrado(usuario);
  }

  private Usuario dadoQueTengoUnUsuario(String nombreUsuario, String email, String password) {
    Usuario usuario = new Usuario();
    usuario.setNombreUsuario(nombreUsuario);
    usuario.setEmail(email);
    usuario.setPassword(password);
    return usuario;
  }

  private void dadoQueExisteElUsuario(Usuario usuario) {
    this.sessionFactory.getCurrentSession().persist(usuario);
  }

  private void cuandoGuardoUnUsuario(Usuario usuario) {
    this.repositorioUsuario.guardar(usuario);
  }

  private Usuario cuandoBuscoUnUsuario(String email, String password) {
    return this.repositorioUsuario.buscarUsuario(email, password);
  }

  private Usuario cuandoObtengoUnUsuarioPorEmail(String email) {
    return this.repositorioUsuario.buscar(email);
  }

  private void cuandoModificoUnUsuario(Usuario usuario) {
    this.repositorioUsuario.modificar(usuario);
  }

  private void entoncesSeGuardoElUsuario(String email, Usuario usuarioEsperado) {
    String hql = "FROM Usuario WHERE email = :email";
    Query query = this.sessionFactory.getCurrentSession().createQuery(hql, Usuario.class);
    query.setParameter("email", email);

    Usuario usuarioObtenido = (Usuario) query.getSingleResult();

    this.entoncesElUsuarioObtenidoEsCorrecto(usuarioObtenido, usuarioEsperado);
  }

  private void entoncesElUsuarioObtenidoEsCorrecto(
    Usuario usuarioObtenido,
    Usuario usuarioEsperado
  ) {
    assertThat(usuarioObtenido.getNombreUsuario(), is(equalTo(usuarioEsperado.getNombreUsuario())));
    assertThat(usuarioObtenido.getEmail(), is(equalTo(usuarioEsperado.getEmail())));
    assertThat(usuarioObtenido.getPassword(), is(equalTo(usuarioEsperado.getPassword())));
  }

  private void entoncesElUsuarioObtenidoEsNull(Usuario obtenido) {
    assertThat(obtenido, is(nullValue()));
  }

  private void entoncesSeLanzaUnaUsuarioNoEncontrado(Usuario usuario) {
    assertThrows(UsuarioNoEncontrado.class, () -> this.cuandoModificoUnUsuario(usuario));
  }
}
