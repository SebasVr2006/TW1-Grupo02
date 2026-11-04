package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.excepcion.CiudadesNoDisponiblesException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioCiudadesTest {

  private ProveedorCiudades proveedorCiudades;
  private ServicioCiudades servicioCiudades;

  private List<CiudadDto> proveedorDevuelve;
  private List<CiudadDto> resultado;

  @BeforeEach
  public void inicializar() {
    this.proveedorCiudades = mock(ProveedorCiudades.class);
    this.servicioCiudades = new ServicioCiudadesImpl(this.proveedorCiudades);
  }

  @Test
  public void deberiaBuscarLasCiudadesQueCoincidenConElTexto() {
    givenElProveedorEncuentraBuenosAires();

    whenBuscoCiudades("Buen");

    thenObtengoLasCiudadesDelProveedor();
  }

  @Test
  public void deberiaPedirComoMaximoCincoSugerencias() {
    givenElProveedorEncuentraBuenosAires();

    whenBuscoCiudades("Buen");

    thenSePidenComoMaximoCincoCiudades();
  }

  @Test
  public void deberiaIgnorarLosEspaciosDelPrincipioYDelFinal() {
    givenElProveedorEncuentraBuenosAires();

    whenBuscoCiudades("  Buen  ");

    thenElProveedorRecibeElTexto("Buen");
  }

  @Test
  public void noDeberiaBuscarSiElTextoTieneMenosDeDosCaracteres() {
    whenBuscoCiudades("B");

    thenNoSeConsultaAlProveedor();
    thenNoHaySugerencias();
  }

  @Test
  public void noDeberiaBuscarSiElTextoEstaVacio() {
    whenBuscoCiudades("   ");

    thenNoSeConsultaAlProveedor();
    thenNoHaySugerencias();
  }

  @Test
  public void noDeberiaBuscarSiElTextoEsNulo() {
    whenBuscoCiudades(null);

    thenNoSeConsultaAlProveedor();
    thenNoHaySugerencias();
  }

  @Test
  public void deberiaEliminarLasCiudadesRepetidas() {
    givenElProveedorDevuelveLaMismaCiudadDosVeces();

    whenBuscoCiudades("Buen");

    thenLaCiudadApareceUnaSolaVez();
  }

  @Test
  public void noDeberiaFallarSiElServicioExternoNoEstaDisponible() {
    givenElServicioExternoNoEstaDisponible();

    whenBuscoCiudades("Buen");

    thenNoHaySugerencias();
  }

  private void givenElProveedorEncuentraBuenosAires() {
    this.proveedorDevuelve =
      List.of(new CiudadDto("Buenos Aires", "Argentina", "Buenos Aires F.D."));
    when(this.proveedorCiudades.buscar(anyString(), anyInt())).thenReturn(this.proveedorDevuelve);
  }

  private void givenElProveedorDevuelveLaMismaCiudadDosVeces() {
    this.proveedorDevuelve =
      List.of(
        new CiudadDto("Buenos Aires", "Argentina", "Buenos Aires F.D."),
        new CiudadDto("Buenos Aires", "Argentina", "Buenos Aires F.D.")
      );
    when(this.proveedorCiudades.buscar(anyString(), anyInt())).thenReturn(this.proveedorDevuelve);
  }

  private void givenElServicioExternoNoEstaDisponible() {
    when(this.proveedorCiudades.buscar(anyString(), anyInt()))
      .thenThrow(new CiudadesNoDisponiblesException("No responde", new RuntimeException()));
  }

  private void whenBuscoCiudades(String texto) {
    this.resultado = this.servicioCiudades.buscarCiudades(texto);
  }

  private void thenObtengoLasCiudadesDelProveedor() {
    assertThat(this.resultado, is(equalTo(this.proveedorDevuelve)));
  }

  private void thenSePidenComoMaximoCincoCiudades() {
    verify(this.proveedorCiudades).buscar("Buen", 5);
  }

  private void thenElProveedorRecibeElTexto(String textoEsperado) {
    verify(this.proveedorCiudades).buscar(textoEsperado, 5);
  }

  private void thenNoSeConsultaAlProveedor() {
    verify(this.proveedorCiudades, never()).buscar(anyString(), anyInt());
  }

  private void thenNoHaySugerencias() {
    assertThat(this.resultado, is(empty()));
  }

  private void thenLaCiudadApareceUnaSolaVez() {
    assertThat(this.resultado.size(), is(equalTo(1)));
    assertThat(this.resultado.get(0).getNombre(), is(equalTo("Buenos Aires")));
  }
}
