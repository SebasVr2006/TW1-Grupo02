package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.tallerwebi.dominio.CiudadDto;
import com.tallerwebi.dominio.excepcion.CiudadesNoDisponiblesException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class ProveedorCiudadesOpenMeteoTest {

  private static final String RESPUESTA_CON_CIUDADES =
    "{\"results\":[" +
    "{\"id\":3435910,\"name\":\"Buenos Aires\",\"latitude\":-34.61,\"longitude\":-58.37," +
    "\"country_code\":\"AR\",\"admin1\":\"Buenos Aires F.D.\",\"country\":\"Argentina\"}," +
    "{\"id\":3435907,\"name\":\"Buenos Aires\",\"latitude\":9.17,\"longitude\":-83.33," +
    "\"country_code\":\"CR\",\"country\":\"Costa Rica\"}" +
    "],\"generationtime_ms\":0.5}";

  private MockRestServiceServer servidorExterno;
  private ProveedorCiudadesOpenMeteo proveedor;

  private List<CiudadDto> resultado;

  @BeforeEach
  public void inicializar() {
    RestTemplate restTemplate = new RestTemplate();
    this.servidorExterno = MockRestServiceServer.bindTo(restTemplate).build();
    this.proveedor = new ProveedorCiudadesOpenMeteo(restTemplate);
  }

  @Test
  public void deberiaConvertirLaRespuestaEnCiudades() {
    givenLaApiResponde(RESPUESTA_CON_CIUDADES);

    whenBuscoCiudades("Buenos", 5);

    thenObtengoLasDosCiudadesDeLaRespuesta();
  }

  @Test
  public void deberiaDejarLaProvinciaVaciaSiLaApiNoLaInforma() {
    givenLaApiResponde(RESPUESTA_CON_CIUDADES);

    whenBuscoCiudades("Buenos", 5);

    thenLaSegundaCiudadNoTieneProvincia();
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiLaApiNoEncuentraCiudades() {
    givenLaApiResponde("{\"generationtime_ms\":0.3}");

    whenBuscoCiudades("xyzxyz", 5);

    thenNoObtengoCiudades();
  }

  @Test
  public void deberiaPedirLaBusquedaEnEspanolConLaCantidadIndicada() {
    givenLaApiEsperaUnaPeticionConLosParametrosEstandar();

    whenBuscoCiudades("Buenos", 5);

    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void deberiaCodificarLosEspaciosYLasLetrasEspecialesDelTexto() {
    givenLaApiEsperaElTextoCodificado();

    whenBuscoCiudades("São Paulo", 5);

    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiLaApiFalla() {
    givenLaApiFalla();

    whenBuscoCiudadesYEsperoUnaExcepcion();
  }

  private void givenLaApiResponde(String json) {
    this.servidorExterno.expect(requestTo(containsString("geocoding-api.open-meteo.com/v1/search")))
      .andExpect(method(HttpMethod.GET))
      .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
  }

  private void givenLaApiEsperaUnaPeticionConLosParametrosEstandar() {
    this.servidorExterno.expect(requestTo(containsString("name=Buenos")))
      .andExpect(requestTo(containsString("count=5")))
      .andExpect(requestTo(containsString("language=es")))
      .andRespond(withSuccess(RESPUESTA_CON_CIUDADES, MediaType.APPLICATION_JSON));
  }

  private void givenLaApiEsperaElTextoCodificado() {
    this.servidorExterno.expect(requestTo(containsString("name=S%C3%A3o%20Paulo")))
      .andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
  }

  private void givenLaApiFalla() {
    this.servidorExterno.expect(requestTo(containsString("geocoding-api.open-meteo.com")))
      .andRespond(withServerError());
  }

  private void whenBuscoCiudades(String texto, int maximo) {
    this.resultado = this.proveedor.buscar(texto, maximo);
  }

  private void whenBuscoCiudadesYEsperoUnaExcepcion() {
    assertThrows(CiudadesNoDisponiblesException.class, () -> this.proveedor.buscar("Buenos", 5));
  }

  private void thenObtengoLasDosCiudadesDeLaRespuesta() {
    assertThat(this.resultado.size(), is(equalTo(2)));
    assertThat(this.resultado.get(0).getNombre(), is(equalTo("Buenos Aires")));
    assertThat(this.resultado.get(0).getPais(), is(equalTo("Argentina")));
    assertThat(this.resultado.get(0).getProvincia(), is(equalTo("Buenos Aires F.D.")));
    assertThat(this.resultado.get(1).getPais(), is(equalTo("Costa Rica")));
  }

  private void thenLaSegundaCiudadNoTieneProvincia() {
    assertThat(this.resultado.get(1).getProvincia(), is(nullValue()));
  }

  private void thenNoObtengoCiudades() {
    assertThat(this.resultado, is(empty()));
  }

  private void thenSeHizoLaPeticionEsperada() {
    this.servidorExterno.verify();
  }
}
