package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;

class ServicioItinerarioTest {

  private RepositorioItinerario repositorioItinerario;
  private ServicioItinerarioImpl servicioItinerario;
  private OpcionDestino resultado;

  @Test
  void deberiaGenerarItinerarioConDatosDelRepositorio() {
    givenTengoUnDestinoConHotelYActividad();

    whenGeneroElItinerarioDeBuenosAires();

    thenElItinerarioContieneLosDatosDelRepositorio();
  }

  private void givenTengoUnDestinoConHotelYActividad() {
    repositorioItinerario = mock(RepositorioItinerario.class);
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario);

    DestinoDisponible buenosAires = new DestinoDisponible("Buenos Aires", "Argentina");
    buenosAires.setId(1L);

    Hotel hotel = new Hotel("Hotel Palermo Soho", 90.0);
    Actividad actividad = new Actividad("Show de tango en San Telmo", 70.0);

    when(repositorioItinerario.buscarDestinoPorCiudad("buenos aires")).thenReturn(buenosAires);
    when(repositorioItinerario.buscarHotelesPorDestino(1L)).thenReturn(List.of(hotel));
    when(repositorioItinerario.buscarActividadesPorDestino(1L)).thenReturn(List.of(actividad));
  }

  private void whenGeneroElItinerarioDeBuenosAires() {
    resultado = servicioItinerario.generarItinerario("Buenos Aires");
  }

  private void thenElItinerarioContieneLosDatosDelRepositorio() {
    assertNotNull(resultado);
    assertEquals("Buenos Aires", resultado.getCiudad());
    assertEquals("Argentina", resultado.getPais());
    assertEquals(1, resultado.getHoteles().size());
    assertEquals("Hotel Palermo Soho", resultado.getHoteles().get(0).getNombre());
    assertEquals(1, resultado.getActividades().size());
    assertEquals("Show de tango en San Telmo", resultado.getActividades().get(0).getNombre());
  }

  @Test
  void deberiaAceptarDestinoConAcentos() {
    givenTengoUnDestinoParisSinHotelesNiActividades();

    whenGeneroElItinerarioDeParisConAcento();

    thenElItinerarioContieneLaCiudadParis();
  }

  private void givenTengoUnDestinoParisSinHotelesNiActividades() {
    repositorioItinerario = mock(RepositorioItinerario.class);
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario);

    DestinoDisponible paris = new DestinoDisponible("Paris", "Francia");
    paris.setId(0L);

    when(repositorioItinerario.buscarDestinoPorCiudad("paris")).thenReturn(paris);
    when(repositorioItinerario.buscarHotelesPorDestino(0L)).thenReturn(List.of());
    when(repositorioItinerario.buscarActividadesPorDestino(0L)).thenReturn(List.of());
  }

  private void whenGeneroElItinerarioDeParisConAcento() {
    resultado = servicioItinerario.generarItinerario("París");
  }

  private void thenElItinerarioContieneLaCiudadParis() {
    assertNotNull(resultado);
    assertEquals("Paris", resultado.getCiudad());
  }

  @Test
  void deberiaRetornarNullSiElNombreEsNulo() {
    givenTengoElServicioDeItinerarios();

    whenGeneroUnItinerarioConNombreNulo();

    thenElResultadoEsNulo();
  }

  private void givenTengoElServicioDeItinerarios() {
    repositorioItinerario = mock(RepositorioItinerario.class);
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario);
  }

  private void whenGeneroUnItinerarioConNombreNulo() {
    resultado = servicioItinerario.generarItinerario(null);
  }

  private void thenElResultadoEsNulo() {
    assertNull(resultado);
  }

  @Test
  void deberiaRetornarNullSiElNombreEstaVacio() {
    givenTengoElServicioDeItinerarios();

    whenGeneroUnItinerarioConNombreVacio();

    thenElResultadoEsNulo();
  }

  private void whenGeneroUnItinerarioConNombreVacio() {
    resultado = servicioItinerario.generarItinerario("   ");
  }

  @Test
  void deberiaRetornarNullSiElDestinoNoExiste() {
    givenElRepositorioNoEncuentraCordoba();

    whenGeneroElItinerarioDeCordoba();

    thenElResultadoEsNulo();
  }

  private void givenElRepositorioNoEncuentraCordoba() {
    repositorioItinerario = mock(RepositorioItinerario.class);
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario);

    when(repositorioItinerario.buscarDestinoPorCiudad("cordoba")).thenReturn(null);
  }

  private void whenGeneroElItinerarioDeCordoba() {
    resultado = servicioItinerario.generarItinerario("Córdoba");
  }
}
