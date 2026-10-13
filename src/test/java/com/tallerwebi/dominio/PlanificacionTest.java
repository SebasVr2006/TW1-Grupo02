package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PlanificacionTest {

  @Test
  public void crearPlanificacionDeberiaTenerListaDeDestinosVacia() {
    // preparacion
    Planificacion planificacion = new Planificacion();

    // ejecucion
    int cantidadDestinos = planificacion.getDestinos().size();

    // validacion
    assertThat(cantidadDestinos, equalTo(0));
  }

  @Test
  public void agregarDosDestinosDeberiaConservarSusDatos() {
    // preparacion
    Destino destino1 = new Destino("Bariloche", "2026-12-10", 5);
    Destino destino2 = new Destino("Mendoza", "2026-12-15", 3);

    Planificacion planificacion = new Planificacion();

    // ejecucion
    planificacion.agregarDestino(destino1);
    planificacion.agregarDestino(destino2);

    // validacion
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
    // preparacion
    Planificacion planificacion = new Planificacion();

    // ejecucion
    planificacion.setAdultos(2);
    planificacion.setNinos(1);

    // validacion
    assertThat(planificacion.getAdultos(), equalTo(2));
    assertThat(planificacion.getNinos(), equalTo(1));
  }

  @Test
  public void guardarPreferenciasDeberiaConservarLosDatos() {
    // preparacion
    Planificacion planificacion = new Planificacion();
    List<String> intereses = new ArrayList<>();
    intereses.add("playa");
    intereses.add("cultura");

    // ejecucion
    planificacion.setAlojamiento("hotel");
    planificacion.setIntereses(intereses);

    // validacion
    assertThat(planificacion.getAlojamiento(), equalTo("hotel"));
    assertThat(planificacion.getIntereses(), equalTo(intereses));
  }
}
