package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tallerwebi.dominio.excepcion.DestinoInvalidoException;
import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioPlanificacionTest {

  private ServicioPlanificacion servicioPlanificacion;

  @BeforeEach
  public void init() {
    this.servicioPlanificacion = new ServicioPlanificacionImpl();
  }

  @Test
  public void guardarPlanificacionDeberiaRetornarLaMismaPlanificacion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();

    // ejecucion
    Planificacion resultado = this.servicioPlanificacion.guardarPlanificacion(planificacion);

    // validacion
    assertThat(resultado, equalTo(planificacion));
  }

  @Test
  public void guardarPlanificacionConNombreDestinoVacioDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.getDestinos().get(0).setNombre("");

    // ejecucion y validacion
    assertThrows(
      DestinoInvalidoException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionConFechaVaciaDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.getDestinos().get(0).setFechaInicio("");

    // ejecucion y validacion
    assertThrows(
      DestinoInvalidoException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionConDuracionCeroDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.getDestinos().get(0).setDuracion(0);

    // ejecucion y validacion
    assertThrows(
      DestinoInvalidoException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionSinAdultosDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.setAdultos(0);

    // ejecucion y validacion
    assertThrows(
      PlanificacionInvalidaException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionSinDestinosDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.getDestinos().clear();

    // ejecucion y validacion
    assertThrows(
      PlanificacionInvalidaException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionConNombreDestinoEnBlancoDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.getDestinos().get(0).setNombre("   ");

    // ejecucion y validacion
    assertThrows(
      DestinoInvalidoException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionConNinosNegativosDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.setNinos(-1);

    // ejecucion y validacion
    assertThrows(
      PlanificacionInvalidaException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionSinAlojamientoDeberiaLanzarExcepcion() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();
    planificacion.setAlojamiento("");

    // ejecucion y validacion
    assertThrows(
      PlanificacionInvalidaException.class,
      () -> this.servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  @Test
  public void guardarPlanificacionSinInteresesDeberiaSerValida() {
    // preparacion
    Planificacion planificacion = crearPlanificacionValida();

    // ejecucion
    Planificacion resultado = this.servicioPlanificacion.guardarPlanificacion(planificacion);

    // validacion
    assertThat(resultado, equalTo(planificacion));
  }

  private Planificacion crearPlanificacionValida() {
    Planificacion planificacion = new Planificacion();

    Destino destino = new Destino("Bariloche", "2026-12-10", 5);
    planificacion.agregarDestino(destino);

    planificacion.setAdultos(1);
    planificacion.setNinos(0);
    planificacion.setAlojamiento("hotel");

    return planificacion;
  }
}
