package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ServicioPlanificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    String vista = this.controladorPlanificacion.mostrarPlanificacion();

    // validacion
    assertThat(vista, equalTo("planificacion"));
  }

  @Test
  public void recibirPlanificacionDeberiaDelegarEnElServicio() {
    // preparacion
    Planificacion planificacion = new Planificacion();

    when(this.servicioPlanificacionMock.guardarPlanificacion(planificacion))
      .thenReturn(planificacion);

    // ejecucion
    Planificacion resultado = this.controladorPlanificacion.recibirPlanificacion(planificacion);

    // validacion
    assertThat(resultado, equalTo(planificacion));

    verify(this.servicioPlanificacionMock, times(1)).guardarPlanificacion(planificacion);
  }
}
