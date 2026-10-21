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
  private Usuario usuario;
  private Usuario obtenido;

  @BeforeEach
  public void inicializar() {
    this.repositorioUsuario = new RepositorioUsuarioImpl(this.sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarUnNuevoUsuario() {
    givenTengoUnUsuarioNuevo();

    whenGuardoElUsuario();

    thenElUsuarioSeGuardaCorrectamente();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnUsuarioExistenteCuandoBuscoPorEmailYPassword() {
    givenTengoUnUsuarioExistente();

    whenBuscoElUsuarioPorEmailYPassword();

    thenElUsuarioObtenidoEsCorrecto();
  }

  @Test
  @Transactional
  public void noDeberiaEncontrarUnUsuarioInexistenteCuandoBuscoPorEmailYPassword() {
    givenNoExisteUnUsuarioConEseEmailYPassword();

    whenBuscoElUsuarioPorEmailYPassword();

    thenElUsuarioObtenidoEsNulo();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaEncontrarUnUsuarioExistenteCuandoBuscoPorEmail() {
    givenTengoUnUsuarioExistente();

    whenBuscoElUsuarioPorEmail();

    thenElUsuarioObtenidoEsCorrecto();
  }

  @Test
  @Transactional
  public void noDeberiaEncontrarUnUsuarioInexistenteCuandoBuscoPorEmail() {
    givenNoExisteUnUsuarioConEseEmail();

    whenBuscoElUsuarioPorEmail();

    thenElUsuarioObtenidoEsNulo();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaModificarUnUsuarioExistente() {
    givenTengoUnUsuarioExistente();
    givenModificoLosDatosDelUsuario();

    whenModificoElUsuario();
    whenBuscoElUsuarioPorEmail();

    thenElUsuarioObtenidoEsCorrecto();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaLanzarUnaExcepcionAlIntentarModificarUnUsuarioInexistente() {
    givenTengoUnUsuarioInexistente();

    whenIntentoModificarElUsuarioYEsperoUnaExcepcion();
  }

  private void givenTengoUnUsuarioNuevo() {
    this.usuario = crearUsuario("usuarioPrueba", "nuevo.usuario@test.com", "1234");
  }

  private void givenTengoUnUsuarioExistente() {
    this.usuario = crearUsuario("usuarioPrueba", "test@test.com", "123");
    this.sessionFactory.getCurrentSession().persist(this.usuario);
  }

  private void givenNoExisteUnUsuarioConEseEmailYPassword() {
    this.usuario = crearUsuario("usuarioPrueba", "test@test.com", "123");
  }

  private void givenNoExisteUnUsuarioConEseEmail() {
    this.usuario = crearUsuario("usuarioPrueba", "test@test.com", "123");
  }

  private void givenModificoLosDatosDelUsuario() {
    this.usuario.setNombreUsuario("nombreActualizado");
    this.usuario.setPassword("4567");
  }

  private void givenTengoUnUsuarioInexistente() {
    this.usuario = crearUsuario("usuarioInexistente", "noexiste@test.com", "123");
  }

  private void whenGuardoElUsuario() {
    this.repositorioUsuario.guardar(this.usuario);
  }

  private void whenBuscoElUsuarioPorEmailYPassword() {
    this.obtenido =
      this.repositorioUsuario.buscarUsuario(this.usuario.getEmail(), this.usuario.getPassword());
  }

  private void whenBuscoElUsuarioPorEmail() {
    this.obtenido = this.repositorioUsuario.buscar(this.usuario.getEmail());
  }

  private void whenModificoElUsuario() {
    this.repositorioUsuario.modificar(this.usuario);
  }

  private void whenIntentoModificarElUsuarioYEsperoUnaExcepcion() {
    assertThrows(UsuarioNoEncontrado.class, () -> this.repositorioUsuario.modificar(this.usuario));
  }

  private void thenElUsuarioSeGuardaCorrectamente() {
    String hql = "FROM Usuario WHERE email = :email";
    Query query = this.sessionFactory.getCurrentSession().createQuery(hql, Usuario.class);
    query.setParameter("email", this.usuario.getEmail());

    Usuario usuarioGuardado = (Usuario) query.getSingleResult();

    thenElUsuarioObtenidoEsCorrecto(usuarioGuardado);
  }

  private void thenElUsuarioObtenidoEsCorrecto() {
    thenElUsuarioObtenidoEsCorrecto(this.obtenido);
  }

  private void thenElUsuarioObtenidoEsCorrecto(Usuario usuarioObtenido) {
    assertThat(usuarioObtenido.getNombreUsuario(), is(equalTo(this.usuario.getNombreUsuario())));
    assertThat(usuarioObtenido.getEmail(), is(equalTo(this.usuario.getEmail())));
    assertThat(usuarioObtenido.getPassword(), is(equalTo(this.usuario.getPassword())));
  }

  private void thenElUsuarioObtenidoEsNulo() {
    assertThat(this.obtenido, is(nullValue()));
  }

  private Usuario crearUsuario(String nombreUsuario, String email, String password) {
    Usuario usuario = new Usuario();
    usuario.setNombreUsuario(nombreUsuario);
    usuario.setEmail(email);
    usuario.setPassword(password);
    return usuario;
  }
}
