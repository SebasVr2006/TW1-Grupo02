package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.OpcionDestino;
import com.tallerwebi.dominio.ServicioItinerario;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControladorItinerarioTest {

  private ServicioItinerario servicioItinerarioMock;
  private ControladorItinerario controladorItinerario;

  @BeforeEach
  public void init() {
    this.servicioItinerarioMock = mock(ServicioItinerario.class);
    this.controladorItinerario = new ControladorItinerario(this.servicioItinerarioMock);
  }

  @Test
  public void generarItinerarioDeberiaDevolverLasOpcionesDelServicio() {
    List<OpcionDestino> esperadas = List.of(
      new OpcionDestino("París", "Francia", List.of(), List.of())
    );
    when(this.servicioItinerarioMock.generarItinerario()).thenReturn(esperadas);

    List<OpcionDestino> obtenidas = this.controladorItinerario.generarItinerario();

    assertThat(obtenidas, equalTo(esperadas));
  }
}
