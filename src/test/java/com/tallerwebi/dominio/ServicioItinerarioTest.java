package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.HotelesNoDisponiblesException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicioItinerarioTest {

  private final ProveedorHoteles proveedorHoteles = mock(ProveedorHoteles.class);
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
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario, proveedorHoteles);

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
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario, proveedorHoteles);

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
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario, proveedorHoteles);
  }

  private void whenGeneroUnItinerarioConNombreNulo() {
    resultado = servicioItinerario.generarItinerario((String) null);
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
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario, proveedorHoteles);

    when(repositorioItinerario.buscarDestinoPorCiudad("cordoba")).thenReturn(null);
  }

  private void whenGeneroElItinerarioDeCordoba() {
    resultado = servicioItinerario.generarItinerario("Córdoba");
  }

  @Test
  void deberiaAceptarDestinoConElPaisIndicado() {
    givenTengoBuenosAiresDeArgentinaEnElRepositorio();

    whenGeneroElItinerarioDe("Buenos Aires, Argentina");

    thenElItinerarioEsDeBuenosAiresArgentina();
  }

  @Test
  void deberiaRetornarNullSiElPaisIndicadoNoCoincide() {
    givenTengoBuenosAiresDeArgentinaEnElRepositorio();

    whenGeneroElItinerarioDe("Buenos Aires, Nicaragua");

    thenElResultadoEsNulo();
  }

  @Test
  void deberiaIgnorarAcentosYMayusculasEnElPaisIndicado() {
    givenTengoUnDestinoParisSinHotelesNiActividades();

    whenGeneroElItinerarioDe("PARÍS, francia");

    thenElItinerarioContieneLaCiudadParis();
  }

  private void givenTengoBuenosAiresDeArgentinaEnElRepositorio() {
    repositorioItinerario = mock(RepositorioItinerario.class);
    servicioItinerario = new ServicioItinerarioImpl(repositorioItinerario, proveedorHoteles);

    DestinoDisponible buenosAires = new DestinoDisponible("Buenos Aires", "Argentina");
    buenosAires.setId(1L);

    when(repositorioItinerario.buscarDestinoPorCiudad("buenos aires")).thenReturn(buenosAires);
    when(repositorioItinerario.buscarHotelesPorDestino(1L)).thenReturn(List.of());
    when(repositorioItinerario.buscarActividadesPorDestino(1L)).thenReturn(List.of());
  }

  private void whenGeneroElItinerarioDe(String destino) {
    resultado = servicioItinerario.generarItinerario(destino);
  }

  private void thenElItinerarioEsDeBuenosAiresArgentina() {
    assertNotNull(resultado);
    assertEquals("Buenos Aires", resultado.getCiudad());
    assertEquals("Argentina", resultado.getPais());
  }

  @Test
  void deberiaUsarLosHotelesDeLaApiConLasActividadesDeLaBase() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();
    givenLaApiDevuelveElHotel("Hotel de la API", 150.0);

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires", "2026-12-10", 5));

    thenLosHotelesSon("Hotel de la API");
    thenLasActividadesSon("Show de tango en San Telmo");
    thenSeConsultoLaApiDeBuenosAiresDelDiezAlQuinceDeDiciembre();
  }

  @Test
  void deberiaConsultarLaApiConElPaisDelDestinoDeLaBase() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();
    givenLaApiDevuelveElHotel("Hotel de la API", 150.0);

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires, Argentina", "2026-12-10", 5));

    thenSeConsultoLaApiDeBuenosAiresDelDiezAlQuinceDeDiciembre();
  }

  @Test
  void deberiaUsarLosHotelesDeLaBaseSiLaApiNoEstaDisponible() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();
    givenLaApiFalla();

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires", "2026-12-10", 5));

    thenLosHotelesSon("Hotel Palermo Soho");
  }

  @Test
  void deberiaUsarLosHotelesDeLaBaseSiLaApiNoTieneResultados() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();
    when(proveedorHoteles.buscar(anyString(), anyString(), anyString(), anyInt()))
      .thenReturn(List.of());

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires", "2026-12-10", 5));

    thenLosHotelesSon("Hotel Palermo Soho");
  }

  @Test
  void noDeberiaConsultarLaApiSiLaFechaDeInicioNoEsValida() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires", "10/12/2026", 5));

    thenLosHotelesSon("Hotel Palermo Soho");
    verifyNoInteractions(proveedorHoteles);
  }

  @Test
  void noDeberiaConsultarLaApiSiNoHayFechaDeInicio() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires", null, 5));

    thenLosHotelesSon("Hotel Palermo Soho");
    verifyNoInteractions(proveedorHoteles);
  }

  @Test
  void noDeberiaConsultarLaApiSiLaDuracionNoEsValida() {
    givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase();

    whenGeneroElItinerarioParaElDestino(new Destino("Buenos Aires", "2026-12-10", 0));

    thenLosHotelesSon("Hotel Palermo Soho");
    verifyNoInteractions(proveedorHoteles);
  }

  @Test
  void deberiaMostrarSoloLosHotelesDeLaApiSiElDestinoNoEstaEnLaBase() {
    givenElRepositorioNoEncuentraCordoba();
    givenLaApiDevuelveElHotel("Hotel de la API", 150.0);

    whenGeneroElItinerarioParaElDestino(new Destino("Cancún, México", "2026-12-10", 5));

    assertNotNull(resultado);
    assertEquals("Cancún", resultado.getCiudad());
    assertEquals("México", resultado.getPais());
    assertEquals(1, resultado.getHoteles().size());
    assertEquals("Hotel de la API", resultado.getHoteles().get(0).getNombre());
    assertTrue(resultado.getActividades().isEmpty());
    verify(proveedorHoteles).buscar("Cancún, México", "2026-12-10", "2026-12-15", 6);
  }

  @Test
  void deberiaConsultarLaApiSoloConLaCiudadSiNoSeIndicaElPais() {
    givenElRepositorioNoEncuentraCordoba();
    givenLaApiDevuelveElHotel("Hotel de la API", 150.0);

    whenGeneroElItinerarioParaElDestino(new Destino("Cancún", "2026-12-10", 5));

    assertNotNull(resultado);
    assertEquals("", resultado.getPais());
    verify(proveedorHoteles).buscar("Cancún", "2026-12-10", "2026-12-15", 6);
  }

  @Test
  void deberiaRetornarNullSiElDestinoNoEstaEnLaBaseYLaApiNoTieneHoteles() {
    givenElRepositorioNoEncuentraCordoba();
    when(proveedorHoteles.buscar(anyString(), anyString(), anyString(), anyInt()))
      .thenReturn(List.of());

    whenGeneroElItinerarioParaElDestino(new Destino("Córdoba", "2026-12-10", 5));

    thenElResultadoEsNulo();
  }

  @Test
  void deberiaRetornarNullSiElDestinoNoEstaEnLaBaseYLaApiFalla() {
    givenElRepositorioNoEncuentraCordoba();
    givenLaApiFalla();

    whenGeneroElItinerarioParaElDestino(new Destino("Córdoba", "2026-12-10", 5));

    thenElResultadoEsNulo();
  }

  @Test
  void deberiaRetornarNullSiElDestinoNoEstaEnLaBaseYLaFechaNoEsValida() {
    givenElRepositorioNoEncuentraCordoba();

    whenGeneroElItinerarioParaElDestino(new Destino("Córdoba", "10/12/2026", 5));

    thenElResultadoEsNulo();
    verifyNoInteractions(proveedorHoteles);
  }

  @Test
  void deberiaRetornarNullSiElDestinoEsNulo() {
    givenTengoElServicioDeItinerarios();

    whenGeneroElItinerarioParaElDestino(null);

    thenElResultadoEsNulo();
  }

  private void givenTengoBuenosAiresConUnHotelYUnaActividadEnLaBase() {
    givenTengoUnDestinoConHotelYActividad();
  }

  private void givenLaApiDevuelveElHotel(String nombre, double precio) {
    when(proveedorHoteles.buscar(anyString(), anyString(), anyString(), anyInt()))
      .thenReturn(List.of(new Hotel(nombre, precio)));
  }

  private void givenLaApiFalla() {
    when(proveedorHoteles.buscar(anyString(), anyString(), anyString(), anyInt()))
      .thenThrow(new HotelesNoDisponiblesException("sin servicio"));
  }

  private void whenGeneroElItinerarioParaElDestino(Destino destino) {
    resultado = servicioItinerario.generarItinerario(destino);
  }

  private void thenLosHotelesSon(String nombre) {
    assertNotNull(resultado);
    assertEquals(1, resultado.getHoteles().size());
    assertEquals(nombre, resultado.getHoteles().get(0).getNombre());
  }

  private void thenLasActividadesSon(String nombre) {
    assertEquals(1, resultado.getActividades().size());
    assertEquals(nombre, resultado.getActividades().get(0).getNombre());
  }

  private void thenSeConsultoLaApiDeBuenosAiresDelDiezAlQuinceDeDiciembre() {
    verify(proveedorHoteles).buscar("Buenos Aires, Argentina", "2026-12-10", "2026-12-15", 6);
  }
}
