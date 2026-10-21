package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlanificacionTest {

  private Planificacion planificacion;
  private Destino destino1;
  private Destino destino2;
  private List<String> intereses;
  private int cantidadDestinos;

  @Test
  public void crearPlanificacionDeberiaTenerListaDeDestinosVacia() {
    givenTengoUnaPlanificacionNueva();

    whenConsultoLaCantidadDeDestinos();

    thenLaCantidadDeDestinosEsCero();
  }

  private void givenTengoUnaPlanificacionNueva() {
    planificacion = new Planificacion();
  }

  private void whenConsultoLaCantidadDeDestinos() {
    cantidadDestinos = planificacion.getDestinos().size();
  }

  private void thenLaCantidadDeDestinosEsCero() {
    assertThat(cantidadDestinos, equalTo(0));
  }

  @Test
  public void agregarDosDestinosDeberiaConservarSusDatos() {
    givenTengoUnaPlanificacionYDosDestinos();

    whenAgregoLosDosDestinos();

    thenLaPlanificacionConservaLosDatosDeAmbosDestinos();
  }

  private void givenTengoUnaPlanificacionYDosDestinos() {
    planificacion = new Planificacion();
    destino1 = new Destino("Bariloche", "2026-12-10", 5);
    destino2 = new Destino("Mendoza", "2026-12-15", 3);
  }

  private void whenAgregoLosDosDestinos() {
    planificacion.agregarDestino(destino1);
    planificacion.agregarDestino(destino2);
  }

  private void thenLaPlanificacionConservaLosDatosDeAmbosDestinos() {
    assertThat(planificacion.getDestinos().size(), equalTo(2));

    assertThat(planificacion.getDestinos().get(0).getNombre(), equalTo("Bariloche"));
    assertThat(planificacion.getDestinos().get(0).getFechaInicio(), equalTo("2026-12-10"));
    assertThat(planificacion.getDestinos().get(0).getDuracion(), equalTo(5));

    assertThat(planificacion.getDestinos().get(1).getNombre(), equalTo("Mendoza"));
    assertThat(planificacion.getDestinos().get(1).getFechaInicio(), equalTo("2026-12-15"));
    assertThat(planificacion.getDestinos().get(1).getDuracion(), equalTo(3));
  }

  @Test
  public void guardarCantidadDeViajerosDeberiaConservarLosDatos() {
    givenTengoUnaPlanificacionNuevaParaGuardarLosViajeros();

    whenGuardoLaCantidadDeAdultosYNinos();

    thenLaPlanificacionConservaLaCantidadDeViajeros();
  }

  private void givenTengoUnaPlanificacionNuevaParaGuardarLosViajeros() {
    planificacion = new Planificacion();
  }

  private void whenGuardoLaCantidadDeAdultosYNinos() {
    planificacion.setAdultos(2);
    planificacion.setNinos(1);
  }

  private void thenLaPlanificacionConservaLaCantidadDeViajeros() {
    assertThat(planificacion.getAdultos(), equalTo(2));
    assertThat(planificacion.getNinos(), equalTo(1));
  }

  @Test
  public void guardarPreferenciasDeberiaConservarLosDatos() {
    givenTengoUnaPlanificacionYMisIntereses();

    whenGuardoElAlojamientoYLosIntereses();

    thenLaPlanificacionConservaElAlojamientoYLosIntereses();
  }

  private void givenTengoUnaPlanificacionYMisIntereses() {
    planificacion = new Planificacion();
    intereses = new ArrayList<>();
    intereses.add("playa");
    intereses.add("cultura");
  }

  private void whenGuardoElAlojamientoYLosIntereses() {
    planificacion.setAlojamiento("hotel");
    planificacion.setIntereses(intereses);
  }

  private void thenLaPlanificacionConservaElAlojamientoYLosIntereses() {
    assertThat(planificacion.getAlojamiento(), equalTo("hotel"));
    assertThat(planificacion.getIntereses(), equalTo(intereses));
  }
}
