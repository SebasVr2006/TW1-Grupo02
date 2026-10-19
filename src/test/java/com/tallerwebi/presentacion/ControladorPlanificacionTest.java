package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

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

  @BeforeEach
  public void init() {
    this.servicioPlanificacionMock = mock(ServicioPlanificacion.class);
    this.controladorPlanificacion = new ControladorPlanificacion(this.servicioPlanificacionMock);
  }

  @Test
  public void mostrarPlanificacionDeberiaRetornarVistaPlanificacion() {
    // ejecucion
    ModelAndView modeloYVista = this.controladorPlanificacion.mostrarPlanificacion();

    // validacion
    assertThat(modeloYVista.getViewName(), equalTo("planificacion"));
  }

  @Test
  public void recibirPlanificacionDeberiaGuardarEnSesionYRedireccionarAlResumen() {
    // preparacion
    Planificacion planificacion = new Planificacion();
    planificacion.agregarDestino(new Destino("Bariloche", "2026-12-10", 5));
    planificacion.setAdultos(1);
    planificacion.setNinos(0);
    planificacion.setAlojamiento("hotel");

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession sesion = mock(HttpSession.class);

    when(request.getSession()).thenReturn(sesion);
    when(this.servicioPlanificacionMock.guardarPlanificacion(planificacion))
      .thenReturn(planificacion);

    // ejecucion
    ModelAndView modeloYVista =
      this.controladorPlanificacion.recibirPlanificacion(planificacion, request);

    // validacion
    assertThat(modeloYVista.getViewName(), equalTo("redirect:/resumen-planificacion"));

    verify(this.servicioPlanificacionMock, times(1)).guardarPlanificacion(planificacion);

    verify(sesion, times(1)).setAttribute("planificacion", planificacion);
  }

  @Test
  public void mostrarResumenDeberiaRecuperarPlanificacionDeSesion() {
    // preparacion
    Planificacion planificacion = new Planificacion();
    planificacion.agregarDestino(new Destino("Bariloche", "2026-12-10", 5));

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpSession sesion = mock(HttpSession.class);

    when(request.getSession()).thenReturn(sesion);
    when(sesion.getAttribute("planificacion")).thenReturn(planificacion);

    // ejecucion
    ModelAndView modeloYVista = this.controladorPlanificacion.mostrarResumen(request);

    // validacion
    assertThat(modeloYVista.getViewName(), equalTo("resumen-planificacion"));

    assertThat(modeloYVista.getModel().get("planificacion"), equalTo(planificacion));

    verify(sesion, times(1)).getAttribute("planificacion");
  }
}
