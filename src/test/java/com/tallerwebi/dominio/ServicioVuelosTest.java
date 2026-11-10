package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
import com.tallerwebi.dominio.excepcion.SeleccionVueloInvalidaException;
import com.tallerwebi.dominio.excepcion.VuelosNoDisponiblesException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioVuelosTest {

  private ProveedorVuelos proveedorVuelos;
  private ServicioVuelos servicioVuelos;
  private Planificacion planificacion;
  private Vuelo vueloBarato;
  private Vuelo vueloCaro;
  private List<TramoVuelo> tramos;
  private List<Vuelo> seleccionados;

  @BeforeEach
  public void inicializar() {
    this.proveedorVuelos = mock(ProveedorVuelos.class);
    this.servicioVuelos = new ServicioVuelosImpl(this.proveedorVuelos);
    this.vueloBarato = crearVuelo("Aerolineas Argentinas", 300);
    this.vueloCaro = crearVuelo("LATAM", 500);
  }

  @Test
  public void buscarVuelosDeberiaArmarUnTramoDesdeElOrigenHastaElDestino() {
    givenTengoUnaPlanificacionConOrigenYUnDestino();
    givenElProveedorTieneVuelosParaBuenosAiresBariloche();

    whenBuscoLosVuelos();

    thenHayUnTramoDeBuenosAiresABariloche();
    thenElTramoTieneLosVuelosDelProveedor();
  }

  @Test
  public void buscarVuelosConVariosDestinosDeberiaEncadenarLosTramos() {
    givenTengoUnaPlanificacionConOrigenYDosDestinos();

    whenBuscoLosVuelos();

    thenSeConsultaBuenosAiresABarilocheConSusPasajeros();
    thenSeConsultaBarilocheAMendozaConLaFechaDelSegundoDestino();
    thenHayDosTramosEncadenados();
  }

  @Test
  public void buscarVuelosSinOrigenDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConOrigenYUnDestino();
    givenLaPlanificacionNoTieneOrigen();

    whenIntentoBuscarVuelosConUnaPlanificacionInvalida();
  }

  @Test
  public void buscarVuelosSinDestinosDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConOrigenYUnDestino();
    givenLaPlanificacionNoTieneDestinos();

    whenIntentoBuscarVuelosConUnaPlanificacionInvalida();
  }

  @Test
  public void buscarVuelosDeberiaPropagarLaExcepcionSiElProveedorFalla() {
    givenTengoUnaPlanificacionConOrigenYUnDestino();
    givenElProveedorFalla();

    whenIntentoBuscarVuelosYElProveedorFalla();
  }

  @Test
  public void seleccionarVuelosDeberiaDevolverElVueloElegidoDeCadaTramo() {
    givenTengoDosTramosConVuelos();

    whenSeleccionoLosVuelos(Arrays.asList(1, 0));

    thenSeSeleccionanLosVuelos(this.vueloCaro, this.vueloBarato);
  }

  @Test
  public void seleccionarVuelosSinElegirUnVueloDeberiaLanzarExcepcion() {
    givenTengoDosTramosConVuelos();

    whenIntentoSeleccionarVuelos(Arrays.asList(0));
  }

  @Test
  public void seleccionarVuelosConIndiceInexistenteDeberiaLanzarExcepcion() {
    givenTengoDosTramosConVuelos();

    whenIntentoSeleccionarVuelos(Arrays.asList(0, 5));
  }

  @Test
  public void seleccionarVuelosSinNingunaEleccionDeberiaLanzarExcepcion() {
    givenTengoDosTramosConVuelos();

    whenIntentoSeleccionarVuelos(null);
  }

  @Test
  public void seleccionarVuelosDeberiaOmitirLosTramosSinVuelosDisponibles() {
    givenTengoUnTramoSinVuelosYOtroConVuelos();

    whenSeleccionoLosVuelos(Arrays.asList(null, 1));

    thenSeSeleccionanLosVuelos(this.vueloCaro);
  }

  private void givenTengoUnaPlanificacionConOrigenYUnDestino() {
    this.planificacion = new Planificacion();
    this.planificacion.setOrigen("Buenos Aires");
    this.planificacion.agregarDestino(new Destino("Bariloche", "2026-12-10", 5));
    this.planificacion.setAdultos(2);
    this.planificacion.setNinos(1);
  }

  private void givenTengoUnaPlanificacionConOrigenYDosDestinos() {
    givenTengoUnaPlanificacionConOrigenYUnDestino();
    this.planificacion.agregarDestino(new Destino("Mendoza", "2026-12-20", 3));
  }

  private void givenLaPlanificacionNoTieneOrigen() {
    this.planificacion.setOrigen("  ");
  }

  private void givenLaPlanificacionNoTieneDestinos() {
    this.planificacion.getDestinos().clear();
  }

  private void givenElProveedorTieneVuelosParaBuenosAiresBariloche() {
    when(this.proveedorVuelos.buscar("Buenos Aires", "Bariloche", "2026-12-10", 2, 1))
      .thenReturn(Arrays.asList(this.vueloBarato, this.vueloCaro));
  }

  private void givenElProveedorFalla() {
    when(this.proveedorVuelos.buscar("Buenos Aires", "Bariloche", "2026-12-10", 2, 1))
      .thenThrow(new VuelosNoDisponiblesException("No se pudo consultar el servicio de vuelos"));
  }

  private void givenTengoDosTramosConVuelos() {
    TramoVuelo primero = new TramoVuelo("Buenos Aires", "Bariloche", "2026-12-10");
    primero.setVuelos(Arrays.asList(this.vueloBarato, this.vueloCaro));
    TramoVuelo segundo = new TramoVuelo("Bariloche", "Mendoza", "2026-12-20");
    segundo.setVuelos(Arrays.asList(this.vueloBarato, this.vueloCaro));
    this.tramos = Arrays.asList(primero, segundo);
  }

  private void givenTengoUnTramoSinVuelosYOtroConVuelos() {
    TramoVuelo sinVuelos = new TramoVuelo("Buenos Aires", "Bariloche", "2026-12-10");
    TramoVuelo conVuelos = new TramoVuelo("Bariloche", "Mendoza", "2026-12-20");
    conVuelos.setVuelos(Arrays.asList(this.vueloBarato, this.vueloCaro));
    this.tramos = Arrays.asList(sinVuelos, conVuelos);
  }

  private void whenBuscoLosVuelos() {
    this.tramos = this.servicioVuelos.buscarVuelos(this.planificacion);
  }

  private void whenIntentoBuscarVuelosConUnaPlanificacionInvalida() {
    assertThrows(
      PlanificacionInvalidaException.class,
      () -> this.servicioVuelos.buscarVuelos(this.planificacion)
    );
  }

  private void whenIntentoBuscarVuelosYElProveedorFalla() {
    assertThrows(
      VuelosNoDisponiblesException.class,
      () -> this.servicioVuelos.buscarVuelos(this.planificacion)
    );
  }

  private void whenSeleccionoLosVuelos(List<Integer> indices) {
    this.seleccionados = this.servicioVuelos.seleccionarVuelos(this.tramos, indices);
  }

  private void whenIntentoSeleccionarVuelos(List<Integer> indices) {
    assertThrows(
      SeleccionVueloInvalidaException.class,
      () -> this.servicioVuelos.seleccionarVuelos(this.tramos, indices)
    );
  }

  private void thenHayUnTramoDeBuenosAiresABariloche() {
    assertThat(this.tramos, hasSize(1));
    assertThat(this.tramos.get(0).getOrigen(), is(equalTo("Buenos Aires")));
    assertThat(this.tramos.get(0).getDestino(), is(equalTo("Bariloche")));
    assertThat(this.tramos.get(0).getFecha(), is(equalTo("2026-12-10")));
  }

  private void thenElTramoTieneLosVuelosDelProveedor() {
    assertThat(this.tramos.get(0).getVuelos(), hasSize(2));
    assertThat(this.tramos.get(0).getVuelos().get(0), is(equalTo(this.vueloBarato)));
  }

  private void thenSeConsultaBuenosAiresABarilocheConSusPasajeros() {
    verify(this.proveedorVuelos).buscar("Buenos Aires", "Bariloche", "2026-12-10", 2, 1);
  }

  private void thenSeConsultaBarilocheAMendozaConLaFechaDelSegundoDestino() {
    verify(this.proveedorVuelos).buscar("Bariloche", "Mendoza", "2026-12-20", 2, 1);
  }

  private void thenHayDosTramosEncadenados() {
    assertThat(this.tramos, hasSize(2));
    assertThat(this.tramos.get(1).getOrigen(), is(equalTo("Bariloche")));
    assertThat(this.tramos.get(1).getDestino(), is(equalTo("Mendoza")));
  }

  private void thenSeSeleccionanLosVuelos(Vuelo... esperados) {
    assertThat(this.seleccionados, is(equalTo(new ArrayList<>(Arrays.asList(esperados)))));
    assertThat(this.seleccionados, is(not(empty())));
  }

  private Vuelo crearVuelo(String aerolinea, int precio) {
    Vuelo vuelo = new Vuelo();
    vuelo.setAerolinea(aerolinea);
    vuelo.setPrecio(precio);
    return vuelo;
  }
}
