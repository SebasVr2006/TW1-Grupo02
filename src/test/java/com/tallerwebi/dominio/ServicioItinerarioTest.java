package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioItinerarioTest {

  private RepositorioItinerario repositorioItinerario;
  private ServicioItinerarioImpl servicioItinerario;
  private DestinoDisponible buenosAires;

  @BeforeEach
  void setUp() {
    repositorioItinerario = mock(RepositorioItinerario.class);
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario);

    buenosAires = new DestinoDisponible("Buenos Aires", "Argentina");
    buenosAires.setId(1L);
  }

  @Test
  void deberiaGenerarItinerarioConDatosDelRepositorio() {
    Hotel hotel = new Hotel("Hotel Palermo Soho", 90.0);
    Actividad actividad = new Actividad("Show de tango en San Telmo", 70.0);

    when(repositorioItinerario.buscarDestinoPorCiudad("buenos aires")).thenReturn(buenosAires);
    when(repositorioItinerario.buscarHotelesPorDestino(1L)).thenReturn(List.of(hotel));
    when(repositorioItinerario.buscarActividadesPorDestino(1L)).thenReturn(List.of(actividad));

    OpcionDestino resultado = servicioItinerario.generarItinerario("Buenos Aires");

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
    when(repositorioItinerario.buscarDestinoPorCiudad("paris"))
      .thenReturn(new DestinoDisponible("Paris", "Francia"));
    when(repositorioItinerario.buscarHotelesPorDestino(0L)).thenReturn(List.of());
    when(repositorioItinerario.buscarActividadesPorDestino(0L)).thenReturn(List.of());

    DestinoDisponible destino = new DestinoDisponible("Paris", "Francia");
    destino.setId(0L);
    when(repositorioItinerario.buscarDestinoPorCiudad("paris")).thenReturn(destino);

    OpcionDestino resultado = servicioItinerario.generarItinerario("París");

    assertNotNull(resultado);
    assertEquals("Paris", resultado.getCiudad());
  }

  @Test
  void deberiaRetornarNullSiElNombreEsNulo() {
    assertNull(servicioItinerario.generarItinerario(null));
  }

  @Test
  void deberiaRetornarNullSiElNombreEstaVacio() {
    assertNull(servicioItinerario.generarItinerario("   "));
  }

  @Test
  void deberiaRetornarNullSiElDestinoNoExiste() {
    when(repositorioItinerario.buscarDestinoPorCiudad("cordoba")).thenReturn(null);

    assertNull(servicioItinerario.generarItinerario("Córdoba"));
  }
}
