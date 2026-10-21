package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Destino;
import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ServicioPlanificacion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorPlanificacionTest {

  private ControladorPlanificacion controladorPlanificacion;
  private ServicioPlanificacion servicioPlanificacionMock;
  private HttpServletRequest request;
  private HttpSession sesion;
  private Planificacion planificacion;
  private ModelAndView resultado;

  @BeforeEach
  public void inicializar() {
    this.servicioPlanificacionMock = mock(ServicioPlanificacion.class);
    this.controladorPlanificacion =
            new ControladorPlanificacion(this.servicioPlanificacionMock);
    this.request = mock(HttpServletRequest.class);
    this.sesion = mock(HttpSession.class);
  }

  @Test
  public void mostrarPlanificacionDeberiaRetornarVistaPlanificacion() {
    whenMuestroLaPlanificacion();

    thenSeMuestraLaVistaPlanificacion();
  }

  @Test
  public void recibirPlanificacionDeberiaGuardarEnSesionYRedireccionarAlResumen() {
    givenTengoUnaPlanificacionValida();
    givenLaSesionEstaDisponible();
    givenElServicioGuardaLaPlanificacion();

    whenReciboLaPlanificacion();

    thenSeRedirigeAlResumen();
    thenSeGuardaLaPlanificacionEnElServicio();
    thenSeGuardaLaPlanificacionEnLaSesion();
  }

  @Test
  public void mostrarResumenDeberiaRecuperarPlanificacionDeSesion() {
    givenTengoUnaPlanificacionValida();
    givenLaSesionEstaDisponible();
    givenLaPlanificacionEstaGuardadaEnLaSesion();

    whenMuestroElResumen();

    thenSeMuestraLaVistaResumen();
    thenSeIncluyeLaPlanificacionEnElModelo();
    thenSeRecuperaLaPlanificacionDeLaSesion();
  }

  private void givenTengoUnaPlanificacionValida() {
    this.planificacion = new Planificacion();
    this.planificacion.agregarDestino(
            new Destino("Bariloche", "2026-12-10", 5)
    );
    this.planificacion.setAdultos(1);
    this.planificacion.setNinos(0);
    this.planificacion.setAlojamiento("hotel");
  }

  private void givenLaSesionEstaDisponible() {
    when(this.request.getSession()).thenReturn(this.sesion);
  }

  private void givenElServicioGuardaLaPlanificacion() {
    when(this.servicioPlanificacionMock.guardarPlanificacion(this.planificacion))
            .thenReturn(this.planificacion);
  }

  private void givenLaPlanificacionEstaGuardadaEnLaSesion() {
    when(this.sesion.getAttribute("planificacion"))
            .thenReturn(this.planificacion);
  }

  private void whenMuestroLaPlanificacion() {
    this.resultado = this.controladorPlanificacion.mostrarPlanificacion();
  }

  private void whenReciboLaPlanificacion() {
    this.resultado = this.controladorPlanificacion.recibirPlanificacion(
            this.planificacion,
            this.request
    );
  }

  private void whenMuestroElResumen() {
    this.resultado = this.controladorPlanificacion.mostrarResumen(this.request);
  }

  private void thenSeMuestraLaVistaPlanificacion() {
    assertThat(this.resultado.getViewName(), equalTo("planificacion"));
  }

  private void thenSeRedirigeAlResumen() {
    assertThat(
            this.resultado.getViewName(),
            equalTo("redirect:/resumen-planificacion")
    );
  }

  private void thenSeGuardaLaPlanificacionEnElServicio() {
    verify(this.servicioPlanificacionMock, times(1))
            .guardarPlanificacion(this.planificacion);
  }

  private void thenSeGuardaLaPlanificacionEnLaSesion() {
    verify(this.sesion, times(1))
            .setAttribute("planificacion", this.planificacion);
  }

  private void thenSeMuestraLaVistaResumen() {
    assertThat(
            this.resultado.getViewName(),
            equalTo("resumen-planificacion")
    );
  }

  private void thenSeIncluyeLaPlanificacionEnElModelo() {
    assertThat(
            this.resultado.getModel().get("planificacion"),
            equalTo(this.planificacion)
    );
  }

  private void thenSeRecuperaLaPlanificacionDeLaSesion() {
    verify(this.sesion, times(1)).getAttribute("planificacion");
  }
}
