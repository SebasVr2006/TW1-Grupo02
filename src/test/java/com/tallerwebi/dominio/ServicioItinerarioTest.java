package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioItinerarioTest {

  private ServicioItinerario servicioItinerario;

  @BeforeEach
  public void init() {
    this.servicioItinerario = new ServicioItinerarioImpl();
  }

  @Test
  public void generarItinerarioDeberiaDevolverTresOpcionesDeDestino() {
    List<OpcionDestino> destinos = this.servicioItinerario.generarItinerario();

    assertThat(destinos, hasSize(3));
  }

  @Test
  public void cadaOpcionDebeTenerDosHotelesYDosActividades() {
    List<OpcionDestino> destinos = this.servicioItinerario.generarItinerario();

    for (OpcionDestino destino : destinos) {
      assertThat(destino.getHoteles(), hasSize(2));
      assertThat(destino.getActividades(), hasSize(2));
    }
  }

  @Test
  public void cadaHotelYActividadDebeTenerPrecio() {
    List<OpcionDestino> destinos = this.servicioItinerario.generarItinerario();

    for (OpcionDestino destino : destinos) {
      for (Hotel hotel : destino.getHoteles()) {
        assertThat(hotel.getPrecioPorNoche(), greaterThan(0.0));
      }
      for (Actividad actividad : destino.getActividades()) {
        assertThat(actividad.getPrecio(), greaterThan(0.0));
      }
    }
  }
}
