package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Destino;
import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.SeleccionVuelos;
import com.tallerwebi.dominio.ServicioVuelos;
import com.tallerwebi.dominio.TramoVuelo;
import com.tallerwebi.dominio.Vuelo;
import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
import com.tallerwebi.dominio.excepcion.SeleccionVueloInvalidaException;
import com.tallerwebi.dominio.excepcion.VuelosNoDisponiblesException;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorVuelosTest {

  private ServicioVuelos servicioVuelos;
  private ControladorVuelos controladorVuelos;
  private HttpSession sesion;
  private Planificacion planificacion;
  private List<TramoVuelo> tramos;
  private List<Vuelo> vuelosElegidos;
  private SeleccionVuelos seleccionVuelos;
  private ModelAndView resultado;

  @BeforeEach
  public void inicializar() {
    this.servicioVuelos = mock(ServicioVuelos.class);
    this.controladorVuelos = new ControladorVuelos(this.servicioVuelos);
    this.sesion = mock(HttpSession.class);
  }

  @Test
  public void mostrarSeleccionVueloDeberiaMostrarLosTramosYGuardarlosEnSesion() {
    givenTengoUnaPlanificacionEnLaSesion();
    givenElServicioEncuentraVuelos();

    whenMuestroLaSeleccionDeVuelo();

    thenSeMuestraLaVistaSeleccionVuelo();
    thenElModeloTieneLosTramos();
    thenLosTramosSeGuardanEnLaSesion();
  }

  @Test
  public void mostrarSeleccionVueloSinPlanificacionDeberiaRedirigirALaPlanificacion() {
    givenNoHayPlanificacionEnLaSesion();

    whenMuestroLaSeleccionDeVuelo();

    thenSeRedirigeA("redirect:/planificacion");
    verifyNoInteractions(this.servicioVuelos);
  }

  @Test
  public void mostrarSeleccionVueloConPlanificacionInvalidaDeberiaRedirigirALaPlanificacion() {
    givenTengoUnaPlanificacionEnLaSesion();
    givenLaPlanificacionEsInvalida();

    whenMuestroLaSeleccionDeVuelo();

    thenSeRedirigeA("redirect:/planificacion");
  }

  @Test
  public void mostrarSeleccionVueloDeberiaMostrarUnErrorSiElServicioDeVuelosFalla() {
    givenTengoUnaPlanificacionEnLaSesion();
    givenElServicioDeVuelosNoEstaDisponible();

    whenMuestroLaSeleccionDeVuelo();

    thenSeMuestraLaVistaSeleccionVuelo();
    thenElModeloTieneElError("No se pudo consultar el servicio de vuelos");
    thenNoSeGuardanTramosEnLaSesion();
  }

  @Test
  public void procesarSeleccionDeberiaGuardarLosVuelosEnLaPlanificacionYRedirigirAlResumen() {
    givenTengoUnaPlanificacionEnLaSesion();
    givenHayTramosEnLaSesion();
    givenElUsuarioEligeLosVuelos();
    givenElServicioDevuelveLosVuelosElegidos();

    whenProcesoLaSeleccion();

    thenSeRedirigeA("redirect:/resumen-planificacion");
    thenLosVuelosElegidosSeGuardanEnLaPlanificacion();
  }

  @Test
  public void procesarSeleccionSinPlanificacionDeberiaRedirigirALaPlanificacion() {
    givenNoHayPlanificacionEnLaSesion();
    givenHayTramosEnLaSesion();
    givenElUsuarioEligeLosVuelos();

    whenProcesoLaSeleccion();

    thenSeRedirigeA("redirect:/planificacion");
    verifyNoInteractions(this.servicioVuelos);
  }

  @Test
  public void procesarSeleccionSinTramosEnLaSesionDeberiaVolverASeleccionDeVuelo() {
    givenTengoUnaPlanificacionEnLaSesion();
    givenNoHayTramosEnLaSesion();
    givenElUsuarioEligeLosVuelos();

    whenProcesoLaSeleccion();

    thenSeRedirigeA("redirect:/seleccion-vuelo");
    verifyNoInteractions(this.servicioVuelos);
  }

  @Test
  public void procesarSeleccionInvalidaDeberiaMostrarElErrorSinAvanzar() {
    givenTengoUnaPlanificacionEnLaSesion();
    givenHayTramosEnLaSesion();
    givenElUsuarioEligeLosVuelos();
    givenLaSeleccionEsInvalida();

    whenProcesoLaSeleccion();

    thenSeMuestraLaVistaSeleccionVuelo();
    thenElModeloTieneElError("Elegí un vuelo para el tramo Buenos Aires → Bariloche");
    thenLaPlanificacionNoTieneVuelosSeleccionados();
  }

  private void givenTengoUnaPlanificacionEnLaSesion() {
    this.planificacion = new Planificacion();
    this.planificacion.setOrigen("Buenos Aires");
    this.planificacion.agregarDestino(new Destino("Bariloche", "2026-12-10", 5));
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
  }

  private void givenNoHayPlanificacionEnLaSesion() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(null);
  }

  private void givenElServicioEncuentraVuelos() {
    this.tramos = Arrays.asList(new TramoVuelo("Buenos Aires", "Bariloche", "2026-12-10"));
    when(this.servicioVuelos.buscarVuelos(this.planificacion)).thenReturn(this.tramos);
  }

  private void givenLaPlanificacionEsInvalida() {
    when(this.servicioVuelos.buscarVuelos(this.planificacion))
      .thenThrow(new PlanificacionInvalidaException("Debe ingresar la ciudad de origen"));
  }

  private void givenElServicioDeVuelosNoEstaDisponible() {
    when(this.servicioVuelos.buscarVuelos(this.planificacion))
      .thenThrow(new VuelosNoDisponiblesException("No se pudo consultar el servicio de vuelos"));
  }

  private void givenHayTramosEnLaSesion() {
    this.tramos = Arrays.asList(new TramoVuelo("Buenos Aires", "Bariloche", "2026-12-10"));
    when(this.sesion.getAttribute("tramosVuelo")).thenReturn(this.tramos);
  }

  private void givenNoHayTramosEnLaSesion() {
    when(this.sesion.getAttribute("tramosVuelo")).thenReturn(null);
  }

  private void givenElUsuarioEligeLosVuelos() {
    this.seleccionVuelos = new SeleccionVuelos();
    this.seleccionVuelos.setIndicesVuelos(Arrays.asList(0));
  }

  private void givenElServicioDevuelveLosVuelosElegidos() {
    this.vuelosElegidos = Arrays.asList(new Vuelo());
    when(this.servicioVuelos.seleccionarVuelos(this.tramos, Arrays.asList(0)))
      .thenReturn(this.vuelosElegidos);
  }

  private void givenLaSeleccionEsInvalida() {
    when(this.servicioVuelos.seleccionarVuelos(this.tramos, Arrays.asList(0)))
      .thenThrow(
        new SeleccionVueloInvalidaException("Elegí un vuelo para el tramo Buenos Aires → Bariloche")
      );
  }

  private void whenMuestroLaSeleccionDeVuelo() {
    this.resultado = this.controladorVuelos.mostrarSeleccionVuelo(this.sesion);
  }

  private void whenProcesoLaSeleccion() {
    this.resultado =
      this.controladorVuelos.procesarSeleccionVuelo(this.seleccionVuelos, this.sesion);
  }

  private void thenSeMuestraLaVistaSeleccionVuelo() {
    assertThat(this.resultado.getViewName(), is(equalTo("seleccion-vuelo")));
  }

  private void thenSeRedirigeA(String destino) {
    assertThat(this.resultado.getViewName(), is(equalTo(destino)));
  }

  private void thenElModeloTieneLosTramos() {
    assertThat(this.resultado.getModel().get("tramos"), is(equalTo(this.tramos)));
  }

  private void thenLosTramosSeGuardanEnLaSesion() {
    verify(this.sesion).setAttribute("tramosVuelo", this.tramos);
  }

  private void thenNoSeGuardanTramosEnLaSesion() {
    verify(this.sesion, never()).setAttribute(eq("tramosVuelo"), any());
  }

  private void thenElModeloTieneElError(String mensaje) {
    assertThat(this.resultado.getModel().get("error"), is(equalTo(mensaje)));
  }

  private void thenLosVuelosElegidosSeGuardanEnLaPlanificacion() {
    assertThat(this.planificacion.getVuelosSeleccionados(), is(equalTo(this.vuelosElegidos)));
  }

  private void thenLaPlanificacionNoTieneVuelosSeleccionados() {
    assertThat(this.planificacion.getVuelosSeleccionados(), is(empty()));
  }
}
