package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReservaTest {

  private Reserva reserva;
  private ReservaDestino destino;
  private ReservaDestino primero;
  private ReservaDestino segundo;
  private HotelReservado hotel;
  private ActividadReservada actividad;
  private Usuario usuario;
  private LocalDateTime fecha;
  private EstadoReserva estado;
  private String descripcionPendiente;
  private String descripcionPagada;
  private String fechaFormateada;
  private Long id;
  private List<ReservaDestino> destinos;
  private List<String> intereses;

  @Test
  public void deberiaNacerSinIdNiDestinos() {
    givenTengoUnaReservaNueva();

    whenConsultoElIdYLasColeccionesDeLaReserva();

    thenLaReservaNoTieneIdNiDestinosNiIntereses();
  }

  private void givenTengoUnaReservaNueva() {
    reserva = new Reserva();
  }

  private void whenConsultoElIdYLasColeccionesDeLaReserva() {
    id = reserva.getId();
    destinos = reserva.getDestinos();
    intereses = reserva.getIntereses();
  }

  private void thenLaReservaNoTieneIdNiDestinosNiIntereses() {
    assertThat(id, is(nullValue()));
    assertThat(destinos, is(notNullValue()));
    assertThat(destinos, hasSize(0));
    assertThat(intereses, hasSize(0));
  }

  @Test
  public void deberiaNacerPendienteDePago() {
    givenTengoUnaReservaNueva();

    whenConsultoElEstadoDeLaReserva();

    thenElEstadoEsPendienteDePago();
  }

  private void whenConsultoElEstadoDeLaReserva() {
    estado = reserva.getEstado();
  }

  private void thenElEstadoEsPendienteDePago() {
    assertThat(estado, is(equalTo(EstadoReserva.PENDIENTE_DE_PAGO)));
  }

  @Test
  public void deberiaPoderMarcarseComoPagada() {
    givenTengoUnaReservaNueva();

    whenMarcoLaReservaComoPagada();

    thenElEstadoDeLaReservaEsPagada();
  }

  private void whenMarcoLaReservaComoPagada() {
    reserva.setEstado(EstadoReserva.PAGADA);
    estado = reserva.getEstado();
  }

  private void thenElEstadoDeLaReservaEsPagada() {
    assertThat(estado, is(equalTo(EstadoReserva.PAGADA)));
  }

  @Test
  public void deberiaDescribirCadaEstadoParaElUsuario() {
    givenTengoLosEstadosDeUnaReserva();

    whenConsultoLasDescripcionesDeLosEstados();

    thenCadaEstadoTieneSuDescripcionCorrespondiente();
  }

  private void givenTengoLosEstadosDeUnaReserva() {
    // Los estados se encuentran definidos en el enum EstadoReserva.
  }

  private void whenConsultoLasDescripcionesDeLosEstados() {
    descripcionPendiente = EstadoReserva.PENDIENTE_DE_PAGO.getDescripcion();
    descripcionPagada = EstadoReserva.PAGADA.getDescripcion();
  }

  private void thenCadaEstadoTieneSuDescripcionCorrespondiente() {
    assertThat(descripcionPendiente, is(equalTo("Pendiente de pago")));
    assertThat(descripcionPagada, is(equalTo("Pagada")));
  }

  @Test
  public void deberiaAgregarDestinosEnOrden() {
    givenTengoUnaReservaYDosDestinos();

    whenAgregoLosDestinosEnOrden();

    thenLosDestinosSeConservanEnElOrdenAgregado();
  }

  private void givenTengoUnaReservaYDosDestinos() {
    reserva = new Reserva();

    primero = new ReservaDestino();
    primero.setNombreDestino("Buenos Aires");

    segundo = new ReservaDestino();
    segundo.setNombreDestino("Paris");
  }

  private void whenAgregoLosDestinosEnOrden() {
    reserva.agregarDestino(primero);
    reserva.agregarDestino(segundo);
    destinos = reserva.getDestinos();
  }

  private void thenLosDestinosSeConservanEnElOrdenAgregado() {
    assertThat(destinos, hasSize(2));
    assertThat(destinos.get(1).getNombreDestino(), is(equalTo("Paris")));
  }

  @Test
  public void deberiaGuardarLosDatosGenerales() {
    givenTengoUnaReservaYLosDatosGenerales();

    whenGuardoLosDatosGeneralesDeLaReserva();

    thenLaReservaConservaLosDatosGenerales();
  }

  private void givenTengoUnaReservaYLosDatosGenerales() {
    reserva = new Reserva();
    fecha = LocalDateTime.of(2026, 10, 14, 10, 30);
    usuario = new Usuario();
  }

  private void whenGuardoLosDatosGeneralesDeLaReserva() {
    reserva.setUsuario(usuario);
    reserva.setFechaCreacion(fecha);
    reserva.setAdultos(2);
    reserva.setNinos(1);
    reserva.setAlojamiento("Hotel");
    reserva.setIntereses(List.of("Cultura"));
    reserva.setTotal(1500.0);
  }

  private void thenLaReservaConservaLosDatosGenerales() {
    assertThat(reserva.getUsuario(), is(equalTo(usuario)));
    assertThat(reserva.getFechaCreacion(), is(equalTo(fecha)));
    assertThat(reserva.getAdultos(), is(equalTo(2)));
    assertThat(reserva.getNinos(), is(equalTo(1)));
    assertThat(reserva.getAlojamiento(), is(equalTo("Hotel")));
    assertThat(reserva.getIntereses(), is(equalTo(List.of("Cultura"))));
    assertThat(reserva.getTotal(), is(equalTo(1500.0)));
  }

  @Test
  public void deberiaFormatearLaFechaDeCreacion() {
    givenTengoUnaReservaConFechaDeCreacion();

    whenConsultoLaFechaDeCreacionFormateada();

    thenLaFechaSeMuestraEnElFormatoEsperado();
  }

  private void givenTengoUnaReservaConFechaDeCreacion() {
    reserva = new Reserva();
    reserva.setFechaCreacion(LocalDateTime.of(2026, 10, 14, 9, 5));
  }

  private void whenConsultoLaFechaDeCreacionFormateada() {
    fechaFormateada = reserva.getFechaCreacionFormateada();
  }

  private void thenLaFechaSeMuestraEnElFormatoEsperado() {
    assertThat(fechaFormateada, is(equalTo("14/10/2026 09:05")));
  }

  @Test
  public void deberiaFormatearVacioSiNoHayFecha() {
    givenTengoUnaReservaSinFechaDeCreacion();

    whenConsultoLaFechaDeCreacionFormateada();

    thenLaFechaFormateadaEsVacia();
  }

  private void givenTengoUnaReservaSinFechaDeCreacion() {
    reserva = new Reserva();
  }

  private void thenLaFechaFormateadaEsVacia() {
    assertThat(fechaFormateada, is(equalTo("")));
  }

  @Test
  public void deberiaGuardarLosDatosDeUnDestinoReservado() {
    givenTengoUnDestinoReservadoConSusDatos();

    whenGuardoLosDatosDelDestinoReservado();

    thenElDestinoReservadoConservaSusDatos();
  }

  private void givenTengoUnDestinoReservadoConSusDatos() {
    destino = new ReservaDestino();
    hotel = new HotelReservado("Hotel Centro", 100.0);
    actividad = new ActividadReservada("Museo", 20.0);
  }

  private void whenGuardoLosDatosDelDestinoReservado() {
    destino.setNombreDestino("Roma");
    destino.setPais("Italia");
    destino.setFechaInicio("2026-11-01");
    destino.setDuracion(4);
    destino.setHotel(hotel);
    destino.setActividades(List.of(actividad));
    destino.setCostoHotel(400.0);
    destino.setCostoActividades(20.0);
    destino.setSubtotal(420.0);
  }

  private void thenElDestinoReservadoConservaSusDatos() {
    assertThat(destino.getNombreDestino(), is(equalTo("Roma")));
    assertThat(destino.getPais(), is(equalTo("Italia")));
    assertThat(destino.getFechaInicio(), is(equalTo("2026-11-01")));
    assertThat(destino.getDuracion(), is(equalTo(4)));
    assertThat(destino.getHotel().getNombre(), is(equalTo("Hotel Centro")));
    assertThat(destino.getHotel().getPrecioPorNoche(), is(equalTo(100.0)));
    assertThat(destino.getActividades().get(0).getNombre(), is(equalTo("Museo")));
    assertThat(destino.getActividades().get(0).getPrecio(), is(equalTo(20.0)));
    assertThat(destino.getCostoHotel(), is(equalTo(400.0)));
    assertThat(destino.getCostoActividades(), is(equalTo(20.0)));
    assertThat(destino.getSubtotal(), is(equalTo(420.0)));
  }
}
