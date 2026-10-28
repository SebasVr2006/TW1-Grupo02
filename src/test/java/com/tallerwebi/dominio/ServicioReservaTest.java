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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ServicioReservaTest {

  private static final Long ID_USUARIO = 7L;

  private RepositorioReserva repositorioReserva;
  private RepositorioUsuario repositorioUsuario;
  private ServicioResumenItinerario servicioResumenItinerario;
  private ServicioReserva servicioReserva;

  private Usuario usuario;
  private Long idUsuarioAConfirmar = ID_USUARIO;
  private Planificacion planificacion;
  private SeleccionItinerario seleccion;
  private ResumenItinerario resumen;
  private Reserva reservaResultado;
  private ConfirmacionCompraDto confirmacion;
  private ReservaInvalidaException excepcion;

  @Test
  public void deberiaGuardarLaReservaConLosDatosDeLaPlanificacion() {
    givenTengoDatosValidosParaLaReserva();

    whenConfirmoLaReserva();

    thenLaReservaSeGuardoConLosDatosEsperados();
  }

  private void thenLaReservaSeGuardoConLosDatosEsperados() {
    Reserva guardada = entoncesSeGuardoUnaReserva();

    assertThat(guardada, is(sameInstance(reservaResultado)));
    assertThat(guardada.getUsuario(), is(sameInstance(usuario)));
    assertThat(guardada.getFechaCreacion(), is(notNullValue()));
    assertThat(guardada.getAdultos(), is(equalTo(2)));
    assertThat(guardada.getNinos(), is(equalTo(1)));
    assertThat(guardada.getAlojamiento(), is(equalTo("Hotel")));
    assertThat(guardada.getIntereses(), is(equalTo(List.of("Gastronomía", "Cultura"))));
  }

  @Test
  public void deberiaGuardarElTotalDelItinerario() {
    givenTengoDatosValidosParaLaReserva();

    whenConfirmoLaReserva();

    thenLaReservaTieneElTotalEsperado();
  }

  private void thenLaReservaTieneElTotalEsperado() {
    assertThat(entoncesSeGuardoUnaReserva().getTotal(), is(equalTo(1000.0)));
  }

  @Test
  public void deberiaGuardarCadaDestinoConSuHotelSusActividadesYSusCostos() {
    givenTengoDatosValidosParaLaReserva();

    whenConfirmoLaReserva();

    thenLaReservaContieneLosDestinosConSusHotelesActividadesYCostos();
  }

  private void thenLaReservaContieneLosDestinosConSusHotelesActividadesYCostos() {
    Reserva guardada = entoncesSeGuardoUnaReserva();

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
    givenTengoUnaPlanificacionSinIntereses();

    whenConfirmoLaReserva();

    thenLaReservaNoTieneIntereses();
  }

  private void givenTengoUnaPlanificacionSinIntereses() {
    givenTengoDatosValidosParaLaReserva();
    planificacion.setIntereses(null);
  }

  private void thenLaReservaNoTieneIntereses() {
    assertThat(entoncesSeGuardoUnaReserva().getIntereses(), is(empty()));
  }

  @Test
  public void deberiaIgnorarLosDestinosNulosDeLaPlanificacion() {
    givenTengoUnaPlanificacionConUnDestinoNulo();

    whenConfirmoLaReserva();

    thenLaReservaIgnoraElDestinoNulo();
  }

  private void givenTengoUnaPlanificacionConUnDestinoNulo() {
    givenTengoDatosValidosParaLaReserva();

    List<Destino> destinos = new ArrayList<>();
    destinos.add(planificacion.getDestinos().get(0));
    destinos.add(null);
    destinos.add(planificacion.getDestinos().get(1));
    planificacion.setDestinos(destinos);
  }

  private void thenLaReservaIgnoraElDestinoNulo() {
    Reserva guardada = entoncesSeGuardoUnaReserva();

    assertThat(guardada.getDestinos(), hasSize(2));
    assertThat(guardada.getDestinos().get(1).getNombreDestino(), is(equalTo("Paris")));
  }

  @Test
  public void noDeberiaGuardarLaReservaSiNoHayUsuario() {
    givenNoTengoUnUsuario();

    whenIntentoConfirmarLaReservaSinDatosValidos();

    thenLaConfirmacionFallaConElMensaje("Debés iniciar sesión para confirmar la reserva.");
  }

  private void givenNoTengoUnUsuario() {
    givenTengoDatosValidosParaLaReserva();
    idUsuarioAConfirmar = null;
  }

  private void givenElUsuarioYaNoExisteEnLaBaseDeDatos() {
    givenTengoDatosValidosParaLaReserva();
    when(repositorioUsuario.buscarPorId(ID_USUARIO)).thenReturn(null);
  }

  @Test
  public void noDeberiaGuardarLaReservaSiElUsuarioNoExisteEnLaBaseDeDatos() {
    givenElUsuarioYaNoExisteEnLaBaseDeDatos();

    whenIntentoConfirmarLaReservaSinDatosValidos();

    thenLaConfirmacionFallaConElMensaje("Debés iniciar sesión para confirmar la reserva.");
  }

  @Test
  public void deberiaAsociarLaReservaAlUsuarioBuscadoPorId() {
    givenTengoDatosValidosParaLaReserva();

    whenConfirmoLaReserva();

    thenSeBuscoElUsuarioPorId();
    thenLaReservaPerteneceAlUsuarioEncontrado();
  }

  private void thenSeBuscoElUsuarioPorId() {
    verify(repositorioUsuario).buscarPorId(ID_USUARIO);
  }

  private void thenLaReservaPerteneceAlUsuarioEncontrado() {
    assertThat(entoncesSeGuardoUnaReserva().getUsuario(), is(sameInstance(usuario)));
  }

  @Test
  public void noDeberiaGuardarLaReservaSiNoHayPlanificacion() {
    givenNoTengoUnaPlanificacion();

    whenIntentoConfirmarLaReservaSinDatosValidos();

    thenLaConfirmacionFallaConElMensaje("No hay un itinerario para confirmar.");
  }

  private void givenNoTengoUnaPlanificacion() {
    givenTengoDatosValidosParaLaReserva();
    planificacion = null;
  }

  @Test
  public void noDeberiaGuardarLaReservaSiNoHaySeleccion() {
    givenNoTengoUnaSeleccion();

    whenIntentoConfirmarLaReservaSinDatosValidos();

    thenLaConfirmacionFallaConElMensaje("No hay un itinerario para confirmar.");
  }

  private void givenNoTengoUnaSeleccion() {
    givenTengoDatosValidosParaLaReserva();
    seleccion = null;
  }

  @Test
  public void noDeberiaGuardarLaReservaSiLaSeleccionEsInvalida() {
    givenTengoUnaSeleccionInvalida();

    whenIntentoConfirmarLaReservaSinDatosValidos();

    thenLaConfirmacionFallaConElMensaje("Debés elegir un hotel válido para Paris");
  }

  private void givenTengoUnaSeleccionInvalida() {
    givenTengoDatosValidosParaLaReserva();

    when(servicioResumenItinerario.generarResumen(seleccion, planificacion))
      .thenThrow(new IllegalArgumentException("Debés elegir un hotel válido para Paris"));
  }

  @Test
  public void noDeberiaGuardarLaReservaSiElResumenNoCoincideConLaPlanificacion() {
    givenTengoUnResumenQueNoCoincideConLaPlanificacion();

    whenIntentoConfirmarLaReservaSinDatosValidos();

    thenLaConfirmacionFallaConElMensaje("El itinerario no coincide con la planificación.");
  }

  private void givenTengoUnResumenQueNoCoincideConLaPlanificacion() {
    givenTengoDatosValidosParaLaReserva();
    resumen.setDestinos(List.of(resumen.getDestinos().get(0)));
  }

  @Test
  public void deberiaGuardarLaReservaComoPendienteDePago() {
    givenTengoDatosValidosParaLaReserva();

    whenConfirmoLaReserva();

    thenLaReservaEstaPendienteDePago();
  }

  private void thenLaReservaEstaPendienteDePago() {
    assertThat(
      entoncesSeGuardoUnaReserva().getEstado(),
      is(equalTo(EstadoReserva.PENDIENTE_DE_PAGO))
    );
  }

  @Test
  public void deberiaPrepararLaConfirmacionConElDesgloseDeCostos() {
    givenTengoDatosValidosParaLaReserva();

    whenPreparoLaConfirmacion();

    thenLaConfirmacionContieneElDesgloseDeCostos();
  }

  private void thenLaConfirmacionContieneElDesgloseDeCostos() {
    assertThat(confirmacion.getTotalAlojamiento(), is(equalTo(930.0)));
    assertThat(confirmacion.getTotalActividades(), is(equalTo(70.0)));
    assertThat(confirmacion.getTotal(), is(equalTo(1000.0)));
  }

  @Test
  public void deberiaPrepararLaConfirmacionConLosDatosDelViaje() {
    givenTengoDatosValidosParaLaReserva();

    whenPreparoLaConfirmacion();

    thenLaConfirmacionContieneLosDatosDelViaje();
  }

  private void thenLaConfirmacionContieneLosDatosDelViaje() {
    assertThat(confirmacion.getAdultos(), is(equalTo(2)));
    assertThat(confirmacion.getNinos(), is(equalTo(1)));
    assertThat(confirmacion.getAlojamiento(), is(equalTo("Hotel")));
  }

  @Test
  public void deberiaPrepararLaConfirmacionConUnResumenCortoDeCadaDestino() {
    givenTengoDatosValidosParaLaReserva();

    whenPreparoLaConfirmacion();

    thenLaConfirmacionContieneUnResumenDeCadaDestino();
  }

  private void thenLaConfirmacionContieneUnResumenDeCadaDestino() {
    assertThat(confirmacion.getDestinos(), hasSize(2));

    DestinoConfirmacionDto buenosAires = confirmacion.getDestinos().get(0);
    assertThat(buenosAires.getNombre(), is(equalTo("Buenos Aires")));
    assertThat(buenosAires.getPais(), is(equalTo("Argentina")));
    assertThat(buenosAires.getFechaInicio(), is(equalTo("2026-12-10")));
    assertThat(buenosAires.getNoches(), is(equalTo(5)));
    assertThat(confirmacion.getDestinos().get(1).getNombre(), is(equalTo("Paris")));
    assertThat(confirmacion.getDestinos().get(1).getNoches(), is(equalTo(3)));
  }

  @Test
  public void noDeberiaGuardarNadaAlPrepararLaConfirmacion() {
    givenTengoDatosValidosParaLaReserva();

    whenPreparoLaConfirmacion();

    thenNoSeGuardaNingunaReserva();
  }

  private void thenNoSeGuardaNingunaReserva() {
    verifyNoInteractions(repositorioReserva);
  }

  @Test
  public void noDeberiaPrepararLaConfirmacionSiNoHayPlanificacion() {
    givenNoTengoUnaPlanificacion();

    whenIntentoPrepararLaConfirmacionSinDatosValidos();

    thenLaPreparacionFallaConElMensaje("No hay un itinerario para confirmar.");
  }

  @Test
  public void noDeberiaPrepararLaConfirmacionSiNoHaySeleccion() {
    givenNoTengoUnaSeleccion();

    whenIntentoPrepararLaConfirmacionSinDatosValidos();

    thenLaPreparacionFallaConElMensaje("No hay un itinerario para confirmar.");
  }

  @Test
  public void noDeberiaPrepararLaConfirmacionSiLaSeleccionEsInvalida() {
    givenTengoUnaSeleccionInvalida();

    whenIntentoPrepararLaConfirmacionSinDatosValidos();

    thenLaPreparacionFallaConElMensaje("Debés elegir un hotel válido para Paris");
  }

  @Test
  public void noDeberiaPrepararLaConfirmacionSiElResumenNoCoincideConLaPlanificacion() {
    givenTengoUnResumenQueNoCoincideConLaPlanificacion();

    whenIntentoPrepararLaConfirmacionSinDatosValidos();

    thenLaPreparacionFallaConElMensaje("El itinerario no coincide con la planificación.");
  }

  @Test
  public void deberiaObtenerLasReservasDelUsuario() {
    givenTengoUnUsuarioConReservas();

    whenConsultoLasReservasDelUsuario();

    thenObtengoLasReservasEsperadas();
  }

  private List<Reserva> reservasEsperadas;

  private List<Reserva> reservasObtenidas;

  private void givenTengoUnUsuarioConReservas() {
    givenTengoDatosValidosParaLaReserva();
    reservasEsperadas = List.of(new Reserva(), new Reserva());

    when(repositorioReserva.buscarPorUsuario(ID_USUARIO)).thenReturn(reservasEsperadas);
  }

  private void whenConsultoLasReservasDelUsuario() {
    reservasObtenidas = servicioReserva.obtenerReservasDelUsuario(ID_USUARIO);
  }

  private void thenObtengoLasReservasEsperadas() {
    assertThat(reservasObtenidas, is(equalTo(reservasEsperadas)));
  }

  @Test
  public void noDeberiaObtenerReservasSiNoHayUsuario() {
    givenTengoDatosValidosParaLaReserva();

    whenIntentoObtenerReservasSinUsuario();

    thenNoSeConsultaElRepositorioDeReservas();
  }

  private void whenIntentoObtenerReservasSinUsuario() {
    assertThrows(
      ReservaInvalidaException.class,
      () -> servicioReserva.obtenerReservasDelUsuario(null)
    );
  }

  @Test
  public void noDeberiaObtenerReservasSiElUsuarioNoExisteEnLaBaseDeDatos() {
    givenElUsuarioYaNoExisteEnLaBaseDeDatos();

    whenIntentoObtenerReservasDeUnUsuarioInexistente();

    thenNoSeConsultaElRepositorioDeReservas();
  }

  @Test
  public void cadaUsuarioDeberiaObtenerSoloSusReservas() {
    givenHayDosUsuariosConReservasDistintas();

    whenConsultoLasReservasDeCadaUsuario();

    thenCadaUsuarioObtieneSoloLasSuyas();
  }

  private static final Long ID_OTRO_USUARIO = 8L;
  private List<Reserva> reservasDelOtroUsuario;

  private void givenHayDosUsuariosConReservasDistintas() {
    givenTengoUnUsuarioConReservas();
    Usuario otro = new Usuario();
    otro.setId(ID_OTRO_USUARIO);
    reservasDelOtroUsuario = List.of(new Reserva());

    when(repositorioUsuario.buscarPorId(ID_OTRO_USUARIO)).thenReturn(otro);
    when(repositorioReserva.buscarPorUsuario(ID_OTRO_USUARIO)).thenReturn(reservasDelOtroUsuario);
  }

  private void whenConsultoLasReservasDeCadaUsuario() {
    reservasObtenidas = servicioReserva.obtenerReservasDelUsuario(ID_USUARIO);
    reservasObtenidasPorElOtro = servicioReserva.obtenerReservasDelUsuario(ID_OTRO_USUARIO);
  }

  private List<Reserva> reservasObtenidasPorElOtro;

  private void thenCadaUsuarioObtieneSoloLasSuyas() {
    assertThat(reservasObtenidas, is(equalTo(reservasEsperadas)));
    assertThat(reservasObtenidasPorElOtro, is(equalTo(reservasDelOtroUsuario)));
  }

  private void whenIntentoObtenerReservasDeUnUsuarioInexistente() {
    assertThrows(
      ReservaInvalidaException.class,
      () -> servicioReserva.obtenerReservasDelUsuario(ID_USUARIO)
    );
  }

  private void thenNoSeConsultaElRepositorioDeReservas() {
    verifyNoInteractions(repositorioReserva);
  }

  private void givenTengoDatosValidosParaLaReserva() {
    repositorioReserva = mock(RepositorioReserva.class);
    servicioResumenItinerario = mock(ServicioResumenItinerario.class);
    repositorioUsuario = mock(RepositorioUsuario.class);
    servicioReserva =
      new ServicioReservaImpl(repositorioReserva, repositorioUsuario, servicioResumenItinerario);

    usuario = new Usuario();
    usuario.setId(ID_USUARIO);
    when(repositorioUsuario.buscarPorId(ID_USUARIO)).thenReturn(usuario);
    planificacion = dadoQueTengoUnaPlanificacion();
    seleccion = new SeleccionItinerario();
    resumen = dadoQueTengoUnResumen();

    when(servicioResumenItinerario.generarResumen(seleccion, planificacion)).thenReturn(resumen);
  }

  private void whenConfirmoLaReserva() {
    reservaResultado =
      servicioReserva.confirmarReserva(idUsuarioAConfirmar, planificacion, seleccion);
  }

  private void whenPreparoLaConfirmacion() {
    confirmacion = servicioReserva.prepararConfirmacion(planificacion, seleccion);
  }

  private void whenIntentoConfirmarLaReservaSinDatosValidos() {
    excepcion =
      assertThrows(
        ReservaInvalidaException.class,
        () -> servicioReserva.confirmarReserva(idUsuarioAConfirmar, planificacion, seleccion)
      );
  }

  private void whenIntentoPrepararLaConfirmacionSinDatosValidos() {
    excepcion =
      assertThrows(
        ReservaInvalidaException.class,
        () -> servicioReserva.prepararConfirmacion(planificacion, seleccion)
      );
  }

  private void thenLaConfirmacionFallaConElMensaje(String mensajeEsperado) {
    assertThat(excepcion.getMessage(), is(equalTo(mensajeEsperado)));
    verifyNoInteractions(repositorioReserva);
  }

  private void thenLaPreparacionFallaConElMensaje(String mensajeEsperado) {
    assertThat(excepcion.getMessage(), is(equalTo(mensajeEsperado)));
  }

  private Reserva entoncesSeGuardoUnaReserva() {
    ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
    verify(repositorioReserva).guardar(captor.capture());
    return captor.getValue();
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
    DetalleDestinoItinerario buenosAires = dadoQueTengoUnDetalle(
      "Buenos Aires",
      "Argentina",
      new Hotel("Hotel Palermo Soho", 90.0),
      List.of(new Actividad("Show de tango", 70.0)),
      new Double[] { 450.0, 70.0, 520.0 }
    );

    DetalleDestinoItinerario paris = dadoQueTengoUnDetalle(
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
