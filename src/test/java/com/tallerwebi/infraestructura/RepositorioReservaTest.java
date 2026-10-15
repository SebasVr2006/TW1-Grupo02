package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.tallerwebi.dominio.ActividadReservada;
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

  @BeforeEach
  public void init() {
    this.repositorioReserva = new RepositorioReservaImpl(this.sessionFactory);
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaGuardarLaReservaConTodosSusDatos() {
    Usuario usuario = this.dadoQueExisteUnUsuario("ana@test.com");
    Reserva reserva = this.dadoQueTengoUnaReserva(usuario, 1500.0);

    this.repositorioReserva.guardar(reserva);
    this.sincronizarYLimpiarLaSesion();

    Reserva guardada = this.sessionFactory.getCurrentSession().get(Reserva.class, reserva.getId());
    assertThat(guardada, is(notNullValue()));
    assertThat(guardada.getUsuario().getId(), is(equalTo(usuario.getId())));
    assertThat(guardada.getAdultos(), is(equalTo(2)));
    assertThat(guardada.getNinos(), is(equalTo(1)));
    assertThat(guardada.getAlojamiento(), is(equalTo("Hotel")));
    assertThat(guardada.getIntereses(), is(equalTo(List.of("Cultura", "Gastronomía"))));
    assertThat(guardada.getTotal(), is(equalTo(1500.0)));
    assertThat(guardada.getDestinos(), hasSize(2));

    ReservaDestino primero = guardada.getDestinos().get(0);
    assertThat(primero.getNombreDestino(), is(equalTo("Buenos Aires")));
    assertThat(primero.getHotel().getNombre(), is(equalTo("Hotel Palermo")));
    assertThat(primero.getActividades(), hasSize(1));
    assertThat(primero.getActividades().get(0).getNombre(), is(equalTo("Show de tango")));
    assertThat(guardada.getDestinos().get(1).getNombreDestino(), is(equalTo("Paris")));
    assertThat(guardada.getDestinos().get(1).getActividades(), is(empty()));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaBuscarSoloLasReservasDelUsuario() {
    Usuario ana = this.dadoQueExisteUnUsuario("ana@test.com");
    Usuario beto = this.dadoQueExisteUnUsuario("beto@test.com");
    this.repositorioReserva.guardar(this.dadoQueTengoUnaReserva(ana, 100.0));
    this.repositorioReserva.guardar(this.dadoQueTengoUnaReserva(ana, 200.0));
    this.repositorioReserva.guardar(this.dadoQueTengoUnaReserva(beto, 300.0));
    this.sincronizarYLimpiarLaSesion();

    List<Reserva> deAna = this.repositorioReserva.buscarPorUsuario(ana.getId());

    assertThat(deAna, hasSize(2));
    assertThat(deAna.get(0).getDestinos(), hasSize(2));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverLasReservasMasRecientesPrimero() {
    Usuario ana = this.dadoQueExisteUnUsuario("ana@test.com");
    Reserva vieja = this.dadoQueTengoUnaReserva(ana, 100.0);
    vieja.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 10, 0));
    Reserva nueva = this.dadoQueTengoUnaReserva(ana, 200.0);
    nueva.setFechaCreacion(LocalDateTime.of(2026, 6, 1, 10, 0));
    this.repositorioReserva.guardar(vieja);
    this.repositorioReserva.guardar(nueva);
    this.sincronizarYLimpiarLaSesion();

    List<Reserva> reservas = this.repositorioReserva.buscarPorUsuario(ana.getId());

    assertThat(reservas.get(0).getTotal(), is(equalTo(200.0)));
    assertThat(reservas.get(1).getTotal(), is(equalTo(100.0)));
  }

  @Test
  @Transactional
  @Rollback
  public void deberiaDevolverListaVaciaSiElUsuarioNoTieneReservas() {
    Usuario ana = this.dadoQueExisteUnUsuario("ana@test.com");

    assertThat(this.repositorioReserva.buscarPorUsuario(ana.getId()), is(empty()));
  }

  private Usuario dadoQueExisteUnUsuario(String email) {
    Usuario usuario = new Usuario();
    usuario.setNombreUsuario("usuario");
    usuario.setEmail(email);
    usuario.setPassword("1234");
    this.sessionFactory.getCurrentSession().persist(usuario);
    return usuario;
  }

  private Reserva dadoQueTengoUnaReserva(Usuario usuario, double total) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setFechaCreacion(LocalDateTime.now());
    reserva.setAdultos(2);
    reserva.setNinos(1);
    reserva.setAlojamiento("Hotel");
    reserva.setIntereses(List.of("Cultura", "Gastronomía"));
    reserva.setTotal(total);
    reserva.agregarDestino(
      this.dadoQueTengoUnDestino(
          "Buenos Aires",
          new HotelReservado("Hotel Palermo", 90.0),
          List.of(new ActividadReservada("Show de tango", 70.0))
        )
    );
    reserva.agregarDestino(
      this.dadoQueTengoUnDestino("Paris", new HotelReservado("Hotel Marais", 160.0), List.of())
    );
    return reserva;
  }

  private ReservaDestino dadoQueTengoUnDestino(
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
