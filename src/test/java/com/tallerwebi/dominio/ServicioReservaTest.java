package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.ReservaInvalidaException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioReservaTest {

  private static final Long ID_USUARIO = 7L;

  private RepositorioReserva repositorioReserva;
  private ServicioResumenItinerario servicioResumenItinerario;
  private ServicioReserva servicioReserva;

  private Usuario usuario;
  private Planificacion planificacion;
  private SeleccionItinerario seleccion;
  private ResumenItinerario resumen;

  @BeforeEach
  public void init() {
    this.repositorioReserva = mock(RepositorioReserva.class);
    this.servicioResumenItinerario = mock(ServicioResumenItinerario.class);
    this.servicioReserva =
      new ServicioReservaImpl(this.repositorioReserva, this.servicioResumenItinerario);

    this.usuario = new Usuario();
    this.usuario.setId(ID_USUARIO);
    this.planificacion = this.dadoQueTengoUnaPlanificacion();
    this.seleccion = new SeleccionItinerario();
    this.resumen = this.dadoQueTengoUnResumen();

    when(this.servicioResumenItinerario.generarResumen(this.seleccion, this.planificacion))
      .thenReturn(this.resumen);
  }

  @Test
  public void deberiaGuardarLaReservaConLosDatosDeLaPlanificacion() {
    Reserva reserva = this.cuandoConfirmoLaReserva();

    Reserva guardada = this.entoncesSeGuardoUnaReserva();
    assertThat(guardada, is(sameInstance(reserva)));
    assertThat(guardada.getUsuario(), is(sameInstance(this.usuario)));
    assertThat(guardada.getFechaCreacion(), is(notNullValue()));
    assertThat(guardada.getAdultos(), is(equalTo(2)));
    assertThat(guardada.getNinos(), is(equalTo(1)));
    assertThat(guardada.getAlojamiento(), is(equalTo("Hotel")));
    assertThat(guardada.getIntereses(), is(equalTo(List.of("Gastronomía", "Cultura"))));
  }

  @Test
  public void deberiaGuardarElTotalDelItinerario() {
    this.cuandoConfirmoLaReserva();

    Reserva guardada = this.entoncesSeGuardoUnaReserva();
    assertThat(guardada.getTotal(), is(equalTo(1000.0)));
  }

  @Test
  public void deberiaGuardarCadaDestinoConSuHotelSusActividadesYSusCostos() {
    this.cuandoConfirmoLaReserva();

    Reserva guardada = this.entoncesSeGuardoUnaReserva();
    assertThat(guardada.getDestinos(), hasSize(2));

    ReservaDestino buenosAires = guardada.getDestinos().get(0);
    assertThat(buenosAires.getNombreDestino(), is(equalTo("Buenos Aires")));
    assertThat(buenosAires.getPais(), is(equalTo("Argentina")));
    assertThat(buenosAires.getFechaInicio(), is(equalTo("2026-12-10")));
    assertThat(buenosAires.getDuracion(), is(equalTo(5)));
    assertThat(buenosAires.getHotel().getNombre(), is(equalTo("Hotel Palermo Soho")));
    assertThat(buenosAires.getHotel().getPrecioPorNoche(), is(equalTo(90.0)));
    assertThat(buenosAires.getActividades(), hasSize(1));
    assertThat(buenosAires.getActividades().get(0).getNombre(), is(equalTo("Show de tango")));
    assertThat(buenosAires.getActividades().get(0).getPrecio(), is(equalTo(70.0)));
    assertThat(buenosAires.getCostoHotel(), is(equalTo(450.0)));
    assertThat(buenosAires.getCostoActividades(), is(equalTo(70.0)));
    assertThat(buenosAires.getSubtotal(), is(equalTo(520.0)));

    ReservaDestino paris = guardada.getDestinos().get(1);
    assertThat(paris.getNombreDestino(), is(equalTo("Paris")));
    assertThat(paris.getFechaInicio(), is(equalTo("2026-12-20")));
    assertThat(paris.getDuracion(), is(equalTo(3)));
    assertThat(paris.getHotel().getNombre(), is(equalTo("Hotel Le Marais")));
    assertThat(paris.getActividades(), is(empty()));
    assertThat(paris.getSubtotal(), is(equalTo(480.0)));
  }

  @Test
  public void deberiaGuardarLaReservaSinInteresesCuandoLaPlanificacionNoLosTiene() {
    this.planificacion.setIntereses(null);

    this.cuandoConfirmoLaReserva();

    assertThat(this.entoncesSeGuardoUnaReserva().getIntereses(), is(empty()));
  }

  @Test
  public void deberiaIgnorarLosDestinosNulosDeLaPlanificacion() {
    List<Destino> destinos = new ArrayList<>();
    destinos.add(this.planificacion.getDestinos().get(0));
    destinos.add(null);
    destinos.add(this.planificacion.getDestinos().get(1));
    this.planificacion.setDestinos(destinos);

    this.cuandoConfirmoLaReserva();

    Reserva guardada = this.entoncesSeGuardoUnaReserva();
    assertThat(guardada.getDestinos(), hasSize(2));
    assertThat(guardada.getDestinos().get(1).getNombreDestino(), is(equalTo("Paris")));
  }

  @Test
  public void noDeberiaGuardarLaReservaSiNoHayUsuario() {
    this.usuario = null;

    this.entoncesLaConfirmacionFallaConElMensaje("Debés iniciar sesión para confirmar la reserva.");
  }

  @Test
  public void noDeberiaGuardarLaReservaSiNoHayPlanificacion() {
    this.planificacion = null;

    this.entoncesLaConfirmacionFallaConElMensaje("No hay un itinerario para confirmar.");
  }

  @Test
  public void noDeberiaGuardarLaReservaSiNoHaySeleccion() {
    this.seleccion = null;

    this.entoncesLaConfirmacionFallaConElMensaje("No hay un itinerario para confirmar.");
  }

  @Test
  public void noDeberiaGuardarLaReservaSiLaSeleccionEsInvalida() {
    when(this.servicioResumenItinerario.generarResumen(this.seleccion, this.planificacion))
      .thenThrow(new IllegalArgumentException("Debés elegir un hotel válido para Paris"));

    this.entoncesLaConfirmacionFallaConElMensaje("Debés elegir un hotel válido para Paris");
  }

  @Test
  public void noDeberiaGuardarLaReservaSiElResumenNoCoincideConLaPlanificacion() {
    this.resumen.setDestinos(List.of(this.resumen.getDestinos().get(0)));

    this.entoncesLaConfirmacionFallaConElMensaje("El itinerario no coincide con la planificación.");
  }

  @Test
  public void deberiaObtenerLasReservasDelUsuario() {
    List<Reserva> reservas = List.of(new Reserva(), new Reserva());
    when(this.repositorioReserva.buscarPorUsuario(ID_USUARIO)).thenReturn(reservas);

    List<Reserva> obtenidas = this.servicioReserva.obtenerReservasDelUsuario(this.usuario);

    assertThat(obtenidas, is(equalTo(reservas)));
  }

  @Test
  public void noDeberiaObtenerReservasSiNoHayUsuario() {
    assertThrows(
      ReservaInvalidaException.class,
      () -> this.servicioReserva.obtenerReservasDelUsuario(null)
    );

    verifyNoInteractions(this.repositorioReserva);
  }

  private Reserva cuandoConfirmoLaReserva() {
    return this.servicioReserva.confirmarReserva(this.usuario, this.planificacion, this.seleccion);
  }

  private Reserva entoncesSeGuardoUnaReserva() {
    ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
    verify(this.repositorioReserva).guardar(captor.capture());
    return captor.getValue();
  }

  private void entoncesLaConfirmacionFallaConElMensaje(String mensajeEsperado) {
    ReservaInvalidaException excepcion = assertThrows(
      ReservaInvalidaException.class,
      this::cuandoConfirmoLaReserva
    );

    assertThat(excepcion.getMessage(), is(equalTo(mensajeEsperado)));
    verifyNoInteractions(this.repositorioReserva);
  }

  private Planificacion dadoQueTengoUnaPlanificacion() {
    Planificacion nueva = new Planificacion();
    nueva.agregarDestino(new Destino("Buenos Aires", "2026-12-10", 5));
    nueva.agregarDestino(new Destino("Paris", "2026-12-20", 3));
    nueva.setAdultos(2);
    nueva.setNinos(1);
    nueva.setAlojamiento("Hotel");
    nueva.setIntereses(List.of("Gastronomía", "Cultura"));
    return nueva;
  }

  private ResumenItinerario dadoQueTengoUnResumen() {
    DetalleDestinoItinerario buenosAires =
      this.dadoQueTengoUnDetalle(
          "Buenos Aires",
          "Argentina",
          new Hotel("Hotel Palermo Soho", 90.0),
          List.of(new Actividad("Show de tango", 70.0)),
          new Double[] { 450.0, 70.0, 520.0 }
        );
    DetalleDestinoItinerario paris =
      this.dadoQueTengoUnDetalle(
          "Paris",
          "Francia",
          new Hotel("Hotel Le Marais", 160.0),
          List.of(),
          new Double[] { 480.0, 0.0, 480.0 }
        );

    ResumenItinerario nuevo = new ResumenItinerario();
    nuevo.setDestinos(List.of(buenosAires, paris));
    nuevo.setTotal(1000.0);
    return nuevo;
  }

  private DetalleDestinoItinerario dadoQueTengoUnDetalle(
    String ciudad,
    String pais,
    Hotel hotel,
    List<Actividad> actividades,
    Double[] costos
  ) {
    DetalleDestinoItinerario detalle = new DetalleDestinoItinerario();
    detalle.setNombreDestino(ciudad);
    detalle.setNombrePais(pais);
    detalle.setHotelSeleccionado(hotel);
    detalle.setActividadesSeleccionadas(actividades);
    detalle.setCostoHotel(costos[0]);
    detalle.setCostoActividades(costos[1]);
    detalle.setSubtotal(costos[2]);
    return detalle;
  }
}
