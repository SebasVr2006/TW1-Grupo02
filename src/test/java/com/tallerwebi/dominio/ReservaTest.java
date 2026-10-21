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

  @Test
  public void deberiaNacerSinIdNiDestinos() {
    Reserva reserva = new Reserva();

    assertThat(reserva.getId(), is(nullValue()));
    assertThat(reserva.getDestinos(), is(notNullValue()));
    assertThat(reserva.getDestinos(), hasSize(0));
    assertThat(reserva.getIntereses(), hasSize(0));
  }

  @Test
  public void deberiaNacerPendienteDePago() {
    assertThat(new Reserva().getEstado(), is(equalTo(EstadoReserva.PENDIENTE_DE_PAGO)));
  }

  @Test
  public void deberiaPoderMarcarseComoPagada() {
    Reserva reserva = new Reserva();

    reserva.setEstado(EstadoReserva.PAGADA);

    assertThat(reserva.getEstado(), is(equalTo(EstadoReserva.PAGADA)));
  }

  @Test
  public void deberiaDescribirCadaEstadoParaElUsuario() {
    assertThat(EstadoReserva.PENDIENTE_DE_PAGO.getDescripcion(), is(equalTo("Pendiente de pago")));
    assertThat(EstadoReserva.PAGADA.getDescripcion(), is(equalTo("Pagada")));
  }

  @Test
  public void deberiaAgregarDestinosEnOrden() {
    Reserva reserva = new Reserva();
    ReservaDestino primero = new ReservaDestino();
    primero.setNombreDestino("Buenos Aires");
    ReservaDestino segundo = new ReservaDestino();
    segundo.setNombreDestino("Paris");

    reserva.agregarDestino(primero);
    reserva.agregarDestino(segundo);

    assertThat(reserva.getDestinos(), hasSize(2));
    assertThat(reserva.getDestinos().get(1).getNombreDestino(), is(equalTo("Paris")));
  }

  @Test
  public void deberiaGuardarLosDatosGenerales() {
    Reserva reserva = new Reserva();
    LocalDateTime ahora = LocalDateTime.of(2026, 10, 14, 10, 30);
    Usuario usuario = new Usuario();

    reserva.setUsuario(usuario);
    reserva.setFechaCreacion(ahora);
    reserva.setAdultos(2);
    reserva.setNinos(1);
    reserva.setAlojamiento("Hotel");
    reserva.setIntereses(List.of("Cultura"));
    reserva.setTotal(1500.0);

    assertThat(reserva.getUsuario(), is(equalTo(usuario)));
    assertThat(reserva.getFechaCreacion(), is(equalTo(ahora)));
    assertThat(reserva.getAdultos(), is(equalTo(2)));
    assertThat(reserva.getNinos(), is(equalTo(1)));
    assertThat(reserva.getAlojamiento(), is(equalTo("Hotel")));
    assertThat(reserva.getIntereses(), is(equalTo(List.of("Cultura"))));
    assertThat(reserva.getTotal(), is(equalTo(1500.0)));
  }

  @Test
  public void deberiaFormatearLaFechaDeCreacion() {
    Reserva reserva = new Reserva();
    reserva.setFechaCreacion(LocalDateTime.of(2026, 10, 14, 9, 5));

    assertThat(reserva.getFechaCreacionFormateada(), is(equalTo("14/10/2026 09:05")));
  }

  @Test
  public void deberiaFormatearVacioSiNoHayFecha() {
    assertThat(new Reserva().getFechaCreacionFormateada(), is(equalTo("")));
  }

  @Test
  public void deberiaGuardarLosDatosDeUnDestinoReservado() {
    ReservaDestino destino = new ReservaDestino();
    HotelReservado hotel = new HotelReservado("Hotel Centro", 100.0);
    ActividadReservada actividad = new ActividadReservada("Museo", 20.0);

    destino.setNombreDestino("Roma");
    destino.setPais("Italia");
    destino.setFechaInicio("2026-11-01");
    destino.setDuracion(4);
    destino.setHotel(hotel);
    destino.setActividades(List.of(actividad));
    destino.setCostoHotel(400.0);
    destino.setCostoActividades(20.0);
    destino.setSubtotal(420.0);

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
