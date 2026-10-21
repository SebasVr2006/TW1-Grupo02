package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.ActividadReservada;
import com.tallerwebi.dominio.EstadoReserva;
import com.tallerwebi.dominio.HotelReservado;
import com.tallerwebi.dominio.RepositorioReserva;
import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.ReservaDestino;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.config.HibernateInfraestructuraTestConfig;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.Session;
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
public class RepositorioReservaTest {

  @Autowired
  private SessionFactory sessionFactory;

  private RepositorioReserva repositorioReserva;
  private Usuario usuario;
  private Reserva reserva;
  private Reserva guardada;
  private List<Reserva> reservas;

  @BeforeEach
  public void inicializar() {
    this.repositorioReserva = new RepositorioReservaImpl(this.sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarLaReservaConTodosSusDatos() {
    givenExisteUnUsuario("ana@test.com");
    givenTengoUnaReservaConTotal(1500.0);
    givenLaReservaEstaPagada();

    whenGuardoLaReserva();
    whenSincronizoYConsultoLaReservaGuardada();

    thenLaReservaSeGuardaConTodosSusDatos();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarSoloLasReservasDelUsuario() {
    givenExistenDosUsuariosConReservas();
    givenHayDosReservasDeAnaYUnaDeBeto();

    whenBuscoLasReservasDeAna();

    thenEncuentroSoloLasReservasDeAna();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverLasReservasMasRecientesPrimero() {
    givenExisteUnUsuario("ana@test.com");
    givenTengoUnaReservaAntiguaYUnaNueva();

    whenBuscoLasReservasDeAna();

    thenLaReservaMasRecienteAparecePrimero();
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverListaVaciaSiElUsuarioNoTieneReservas() {
    givenExisteUnUsuario("ana@test.com");

    whenBuscoLasReservasDeAna();

    thenLaListaDeReservasEstaVacia();
  }

  private void givenExisteUnUsuario(String email) {
    this.usuario = new Usuario();
    this.usuario.setNombreUsuario("usuario");
    this.usuario.setEmail(email);
    this.usuario.setPassword("1234");
    this.sessionFactory.getCurrentSession().persist(this.usuario);
  }

  private void givenTengoUnaReservaConTotal(double total) {
    this.reserva = crearReserva(this.usuario, total);
  }

  private void givenLaReservaEstaPagada() {
    this.reserva.setEstado(EstadoReserva.PAGADA);
  }

  private void givenExistenDosUsuariosConReservas() {
    Usuario ana = crearUsuario("ana@test.com");
    Usuario beto = crearUsuario("beto@test.com");

    this.usuario = ana;
    this.sessionFactory.getCurrentSession().persist(ana);
    this.sessionFactory.getCurrentSession().persist(beto);

    this.reserva = crearReserva(ana, 100.0);
    this.repositorioReserva.guardar(this.reserva);
    this.repositorioReserva.guardar(crearReserva(ana, 200.0));
    this.repositorioReserva.guardar(crearReserva(beto, 300.0));
  }

  private void givenHayDosReservasDeAnaYUnaDeBeto() {
    this.sincronizarYLimpiarLaSesion();
  }

  private void givenTengoUnaReservaAntiguaYUnaNueva() {
    Reserva vieja = crearReserva(this.usuario, 100.0);
    vieja.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 10, 0));

    Reserva nueva = crearReserva(this.usuario, 200.0);
    nueva.setFechaCreacion(LocalDateTime.of(2026, 6, 1, 10, 0));

    this.repositorioReserva.guardar(vieja);
    this.repositorioReserva.guardar(nueva);
    this.sincronizarYLimpiarLaSesion();
  }

  private void whenGuardoLaReserva() {
    this.repositorioReserva.guardar(this.reserva);
  }

  private void whenSincronizoYConsultoLaReservaGuardada() {
    this.sincronizarYLimpiarLaSesion();
    this.guardada =
      this.sessionFactory.getCurrentSession().get(Reserva.class, this.reserva.getId());
  }

  private void whenBuscoLasReservasDeAna() {
    this.reservas = this.repositorioReserva.buscarPorUsuario(this.usuario.getId());
  }

  private void thenLaReservaSeGuardaConTodosSusDatos() {
    assertThat(this.guardada, is(notNullValue()));
    assertThat(this.guardada.getUsuario().getId(), is(equalTo(this.usuario.getId())));
    assertThat(this.guardada.getAdultos(), is(equalTo(2)));
    assertThat(this.guardada.getNinos(), is(equalTo(1)));
    assertThat(this.guardada.getAlojamiento(), is(equalTo("Hotel")));
    assertThat(this.guardada.getIntereses(), is(equalTo(List.of("Cultura", "Gastronomía"))));
    assertThat(this.guardada.getTotal(), is(equalTo(1500.0)));
    assertThat(this.guardada.getEstado(), is(equalTo(EstadoReserva.PAGADA)));
    assertThat(this.guardada.getDestinos(), hasSize(2));

    ReservaDestino primero = this.guardada.getDestinos().get(0);

    assertThat(primero.getNombreDestino(), is(equalTo("Buenos Aires")));
    assertThat(primero.getHotel().getNombre(), is(equalTo("Hotel Palermo")));
    assertThat(primero.getActividades(), hasSize(1));
    assertThat(primero.getActividades().get(0).getNombre(), is(equalTo("Show de tango")));
    assertThat(this.guardada.getDestinos().get(1).getNombreDestino(), is(equalTo("Paris")));
    assertThat(this.guardada.getDestinos().get(1).getActividades(), is(empty()));
  }

  private void thenEncuentroSoloLasReservasDeAna() {
    assertThat(this.reservas, hasSize(2));
    assertThat(this.reservas.get(0).getDestinos(), hasSize(2));
  }

  private void thenLaReservaMasRecienteAparecePrimero() {
    assertThat(this.reservas.get(0).getTotal(), is(equalTo(200.0)));
    assertThat(this.reservas.get(1).getTotal(), is(equalTo(100.0)));
  }

  private void thenLaListaDeReservasEstaVacia() {
    assertThat(this.reservas, is(empty()));
  }

  private Usuario crearUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setNombreUsuario("usuario");
    usuario.setEmail(email);
    usuario.setPassword("1234");
    return usuario;
  }

  private Reserva crearReserva(Usuario usuario, double total) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setFechaCreacion(LocalDateTime.now());
    reserva.setAdultos(2);
    reserva.setNinos(1);
    reserva.setAlojamiento("Hotel");
    reserva.setIntereses(List.of("Cultura", "Gastronomía"));
    reserva.setTotal(total);

    reserva.agregarDestino(
      crearDestino(
        "Buenos Aires",
        new HotelReservado("Hotel Palermo", 90.0),
        List.of(new ActividadReservada("Show de tango", 70.0))
      )
    );

    reserva.agregarDestino(
      crearDestino("Paris", new HotelReservado("Hotel Marais", 160.0), List.of())
    );

    return reserva;
  }

  private ReservaDestino crearDestino(
    String nombre,
    HotelReservado hotel,
    List<ActividadReservada> actividades
  ) {
    ReservaDestino destino = new ReservaDestino();
    destino.setNombreDestino(nombre);
    destino.setPais("Pais");
    destino.setFechaInicio("2026-12-10");
    destino.setDuracion(5);
    destino.setHotel(hotel);
    destino.setActividades(actividades);
    destino.setCostoHotel(450.0);
    destino.setCostoActividades(70.0);
    destino.setSubtotal(520.0);
    return destino;
  }

  private void sincronizarYLimpiarLaSesion() {
    Session sesion = this.sessionFactory.getCurrentSession();
    sesion.flush();
    sesion.clear();
  }
}
