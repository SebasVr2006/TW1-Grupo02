package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.CiudadDto;
import com.tallerwebi.dominio.ServicioCiudades;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ControladorCiudadesTest {

  private ServicioCiudades servicioCiudades;
  private ControladorCiudades controladorCiudades;

  private List<CiudadDto> ciudades;
  private List<CiudadDto> resultado;

  @BeforeEach
  public void inicializar() {
    this.servicioCiudades = mock(ServicioCiudades.class);
    this.controladorCiudades = new ControladorCiudades(this.servicioCiudades);
  }

  @Test
  public void deberiaDevolverLasCiudadesQueEncuentraElServicio() {
    givenElServicioEncuentraBuenosAires();

    whenBuscoCiudades("Buen");

    thenObtengoLasCiudadesDelServicio();
  }

  @Test
  public void deberiaConsultarAlServicioConElTextoRecibido() {
    givenElServicioEncuentraBuenosAires();

    whenBuscoCiudades("Buen");

    thenSeConsultaAlServicioConElTexto("Buen");
  }

  private void givenElServicioEncuentraBuenosAires() {
    this.ciudades = List.of(new CiudadDto("Buenos Aires", "Argentina", "Buenos Aires F.D."));
    when(this.servicioCiudades.buscarCiudades("Buen")).thenReturn(this.ciudades);
  }

  private void whenBuscoCiudades(String texto) {
    this.resultado = this.controladorCiudades.buscarCiudades(texto);
  }

  private void thenObtengoLasCiudadesDelServicio() {
    assertThat(this.resultado, is(equalTo(this.ciudades)));
  }

  private void thenSeConsultaAlServicioConElTexto(String texto) {
    verify(this.servicioCiudades).buscarCiudades(texto);
  }
}
