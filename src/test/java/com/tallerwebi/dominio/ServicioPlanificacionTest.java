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
  private Planificacion planificacion;
  private Planificacion resultado;

  @BeforeEach
  public void init() {
    servicioPlanificacion = new ServicioPlanificacionImpl();
  }

  @Test
  public void guardarPlanificacionDeberiaRetornarLaMismaPlanificacion() {
    givenTengoUnaPlanificacionValida();

    whenGuardoLaPlanificacion();

    thenLaPlanificacionGuardadaEsLaMisma();
  }

  @Test
  public void guardarPlanificacionConNombreDestinoVacioDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConNombreDestinoVacio();

    whenIntentoGuardarLaPlanificacionConDestinoInvalido();
  }

  @Test
  public void guardarPlanificacionConFechaVaciaDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConFechaVacia();

    whenIntentoGuardarLaPlanificacionConDestinoInvalido();
  }

  @Test
  public void guardarPlanificacionConDuracionCeroDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConDuracionCero();

    whenIntentoGuardarLaPlanificacionConDestinoInvalido();
  }

  @Test
  public void guardarPlanificacionSinAdultosDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionSinAdultos();

    whenIntentoGuardarLaPlanificacionInvalida();
  }

  @Test
  public void guardarPlanificacionSinDestinosDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionSinDestinos();

    whenIntentoGuardarLaPlanificacionInvalida();
  }

  @Test
  public void guardarPlanificacionConNombreDestinoEnBlancoDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConNombreDestinoEnBlanco();

    whenIntentoGuardarLaPlanificacionConDestinoInvalido();
  }

  @Test
  public void guardarPlanificacionConNinosNegativosDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionConNinosNegativos();

    whenIntentoGuardarLaPlanificacionInvalida();
  }

  @Test
  public void guardarPlanificacionSinAlojamientoDeberiaLanzarExcepcion() {
    givenTengoUnaPlanificacionSinAlojamiento();

    whenIntentoGuardarLaPlanificacionInvalida();
  }

  @Test
  public void guardarPlanificacionSinInteresesDeberiaSerValida() {
    givenTengoUnaPlanificacionValida();

    whenGuardoLaPlanificacion();

    thenLaPlanificacionGuardadaEsLaMisma();
  }

  private void givenTengoUnaPlanificacionValida() {
    planificacion = crearPlanificacionValida();
  }

  private void givenTengoUnaPlanificacionConNombreDestinoVacio() {
    givenTengoUnaPlanificacionValida();
    planificacion.getDestinos().get(0).setNombre("");
  }

  private void givenTengoUnaPlanificacionConFechaVacia() {
    givenTengoUnaPlanificacionValida();
    planificacion.getDestinos().get(0).setFechaInicio("");
  }

  private void givenTengoUnaPlanificacionConDuracionCero() {
    givenTengoUnaPlanificacionValida();
    planificacion.getDestinos().get(0).setDuracion(0);
  }

  private void givenTengoUnaPlanificacionSinAdultos() {
    givenTengoUnaPlanificacionValida();
    planificacion.setAdultos(0);
  }

  private void givenTengoUnaPlanificacionSinDestinos() {
    givenTengoUnaPlanificacionValida();
    planificacion.getDestinos().clear();
  }

  private void givenTengoUnaPlanificacionConNombreDestinoEnBlanco() {
    givenTengoUnaPlanificacionValida();
    planificacion.getDestinos().get(0).setNombre("   ");
  }

  private void givenTengoUnaPlanificacionConNinosNegativos() {
    givenTengoUnaPlanificacionValida();
    planificacion.setNinos(-1);
  }

  private void givenTengoUnaPlanificacionSinAlojamiento() {
    givenTengoUnaPlanificacionValida();
    planificacion.setAlojamiento("");
  }

  private void whenGuardoLaPlanificacion() {
    resultado = servicioPlanificacion.guardarPlanificacion(planificacion);
  }

  private void whenIntentoGuardarLaPlanificacionConDestinoInvalido() {
    assertThrows(
      DestinoInvalidoException.class,
      () -> servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  private void whenIntentoGuardarLaPlanificacionInvalida() {
    assertThrows(
      PlanificacionInvalidaException.class,
      () -> servicioPlanificacion.guardarPlanificacion(planificacion)
    );
  }

  private void thenLaPlanificacionGuardadaEsLaMisma() {
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
