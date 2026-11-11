package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.tallerwebi.dominio.Vuelo;
import com.tallerwebi.dominio.excepcion.VuelosNoDisponiblesException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class ProveedorVuelosSerpApiTest {

  private static final String CLAVE_API = "clave-de-prueba";

  private static final String CIUDAD_ORIGEN =
    "{\"suggestions\":[" +
    "{\"position\":1,\"name\":\"Argentina\",\"type\":\"region\",\"id\":\"/m/region-ar\"}," +
    "{\"position\":2,\"name\":\"Buenos Aires, Argentina\",\"type\":\"city\"," +
    "\"id\":\"/m/ciudad-origen\",\"airports\":[" +
    "{\"name\":\"Aeroparque Jorge Newbery\",\"id\":\"AEP\"}," +
    "{\"name\":\"Ministro Pistarini\",\"id\":\"EZE\"}]}]}";

  private static final String CIUDAD_DESTINO =
    "{\"suggestions\":[" +
    "{\"position\":1,\"name\":\"Bariloche, Argentina\",\"type\":\"city\"," +
    "\"id\":\"/m/ciudad-destino\",\"airports\":[" +
    "{\"name\":\"Aeropuerto de Bariloche\",\"id\":\"BRC\"}]}]}";

  private static final String SOLO_REGIONES =
    "{\"suggestions\":[" +
    "{\"position\":1,\"name\":\"Patagonia\",\"type\":\"region\",\"id\":\"/m/region-patagonia\"}]}";

  private static final String RESPUESTA_CON_VUELOS =
    "{\"best_flights\":[{\"flights\":[" +
    "{\"departure_airport\":{\"name\":\"Aeroparque Jorge Newbery\",\"id\":\"AEP\"," +
    "\"time\":\"2026-12-10 08:00\"}," +
    "\"arrival_airport\":{\"name\":\"Aeropuerto de Bariloche\",\"id\":\"BRC\"," +
    "\"time\":\"2026-12-10 10:15\"}," +
    "\"airline\":\"Aerolineas Argentinas\"," +
    "\"airline_logo\":\"https://logos.test/AR.png\"}]," +
    "\"total_duration\":135,\"price\":320}]," +
    "\"other_flights\":[{\"flights\":[" +
    "{\"departure_airport\":{\"name\":\"Ministro Pistarini\",\"id\":\"EZE\"," +
    "\"time\":\"2026-12-10 06:00\"}," +
    "\"arrival_airport\":{\"name\":\"Aeropuerto de Cordoba\",\"id\":\"COR\"," +
    "\"time\":\"2026-12-10 07:30\"}," +
    "\"airline\":\"LATAM\",\"airline_logo\":\"https://logos.test/LA.png\"}," +
    "{\"departure_airport\":{\"name\":\"Aeropuerto de Cordoba\",\"id\":\"COR\"," +
    "\"time\":\"2026-12-10 09:00\"}," +
    "\"arrival_airport\":{\"name\":\"Aeropuerto de Bariloche\",\"id\":\"BRC\"," +
    "\"time\":\"2026-12-10 11:30\"}," +
    "\"airline\":\"Avianca\"}]," +
    "\"total_duration\":330}]}";

  private MockRestServiceServer servidorExterno;
  private RestTemplate restTemplate;
  private ProveedorVuelosSerpApi proveedor;

  private List<Vuelo> resultado;

  @BeforeEach
  public void inicializar() {
    this.restTemplate = new RestTemplate();
    this.servidorExterno = MockRestServiceServer.bindTo(this.restTemplate).build();
    this.proveedor = new ProveedorVuelosSerpApi(this.restTemplate, CLAVE_API);
  }

  @Test
  public void deberiaConvertirLasOpcionesDeVueloDeLaRespuesta() {
    givenLaApiResuelveOrigenYDestino();
    givenLaApiDevuelveVuelos(RESPUESTA_CON_VUELOS);

    whenBuscoVuelos();

    thenObtengoElVueloDirectoDeLaOpcionPrincipal();
    thenObtengoElVueloConEscalaYSinPrecio();
  }

  @Test
  public void deberiaPedirLosVuelosDeSoloIdaConLosParametrosEsperados() {
    givenLaApiResuelveOrigenYDestino();
    givenLaApiEsperaLaBusquedaConLosParametrosEstandar();

    whenBuscoVuelos();

    thenSeHicieronLasPeticionesEsperadas();
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiGoogleNoTieneResultados() {
    givenLaApiResuelveOrigenYDestino();
    givenLaApiDevuelveVuelos(
      "{\"error\":\"Google Flights hasn't returned any results for this query.\"}"
    );

    whenBuscoVuelos();

    thenNoObtengoVuelos();
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiElOrigenNoTieneAeropuertos() {
    givenLaApiResponde("engine=google_flights_autocomplete", SOLO_REGIONES);

    whenBuscoVuelos();

    thenNoObtengoVuelos();
    thenSeHicieronLasPeticionesEsperadas();
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiNoHayClaveDeApi() {
    this.proveedor = new ProveedorVuelosSerpApi(this.restTemplate, " ");

    assertThrows(VuelosNoDisponiblesException.class, this::whenBuscoVuelos);
    thenSeHicieronLasPeticionesEsperadas();
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiLaApiFalla() {
    givenLaApiFalla();

    assertThrows(VuelosNoDisponiblesException.class, this::whenBuscoVuelos);
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiLaApiDevuelveUnError() {
    givenLaApiResuelveOrigenYDestino();
    givenLaApiDevuelveVuelos("{\"error\":\"Invalid API key.\"}");

    assertThrows(VuelosNoDisponiblesException.class, this::whenBuscoVuelos);
  }

  @Test
  public void deberiaReutilizarLasUbicacionesYaResueltas() {
    givenLaApiResuelveOrigenYDestino();
    givenLaApiDevuelveVuelos("{\"best_flights\":[]}");
    givenLaApiDevuelveVuelos("{\"best_flights\":[]}");

    whenBuscoVuelos();
    whenBuscoVuelos();

    thenSeHicieronLasPeticionesEsperadas();
  }

  private void givenLaApiResuelveOrigenYDestino() {
    givenLaApiResponde("q=Buenos%20Aires", CIUDAD_ORIGEN);
    givenLaApiResponde("q=Bariloche", CIUDAD_DESTINO);
  }

  private void givenLaApiResponde(String fragmentoDeLaUrl, String json) {
    this.servidorExterno.expect(requestTo(containsString(fragmentoDeLaUrl)))
      .andExpect(method(HttpMethod.GET))
      .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
  }

  private void givenLaApiDevuelveVuelos(String json) {
    givenLaApiResponde("engine=google_flights&", json);
  }

  private void givenLaApiEsperaLaBusquedaConLosParametrosEstandar() {
    this.servidorExterno.expect(requestTo(containsString("engine=google_flights&")))
      .andExpect(requestTo(containsString("departure_id=/m/ciudad-origen")))
      .andExpect(requestTo(containsString("arrival_id=/m/ciudad-destino")))
      .andExpect(requestTo(containsString("outbound_date=2026-12-10")))
      .andExpect(requestTo(containsString("type=2")))
      .andExpect(requestTo(containsString("adults=2")))
      .andExpect(requestTo(containsString("children=1")))
      .andExpect(requestTo(containsString("api_key=" + CLAVE_API)))
      .andRespond(withSuccess("{\"best_flights\":[]}", MediaType.APPLICATION_JSON));
  }

  private void givenLaApiFalla() {
    this.servidorExterno.expect(requestTo(containsString("serpapi.com")))
      .andRespond(withServerError());
  }

  private void whenBuscoVuelos() {
    this.resultado = this.proveedor.buscar("Buenos Aires", "Bariloche", "2026-12-10", 2, 1);
  }

  private void thenObtengoElVueloDirectoDeLaOpcionPrincipal() {
    assertThat(this.resultado, hasSize(2));

    Vuelo directo = this.resultado.get(0);
    assertThat(directo.getAerolinea(), is(equalTo("Aerolineas Argentinas")));
    assertThat(directo.getLogoAerolinea(), is(equalTo("https://logos.test/AR.png")));
    assertThat(directo.getAeropuertoOrigen(), is(equalTo("Aeroparque Jorge Newbery (AEP)")));
    assertThat(directo.getAeropuertoDestino(), is(equalTo("Aeropuerto de Bariloche (BRC)")));
    assertThat(directo.getSalida(), is(equalTo("2026-12-10 08:00")));
    assertThat(directo.getLlegada(), is(equalTo("2026-12-10 10:15")));
    assertThat(directo.getDuracionMinutos(), is(equalTo(135)));
    assertThat(directo.getEscalas(), is(equalTo(0)));
    assertThat(directo.getPrecio(), is(equalTo(320)));
  }

  private void thenObtengoElVueloConEscalaYSinPrecio() {
    Vuelo conEscala = this.resultado.get(1);
    assertThat(conEscala.getAerolinea(), is(equalTo("LATAM, Avianca")));
    assertThat(conEscala.getAeropuertoOrigen(), is(equalTo("Ministro Pistarini (EZE)")));
    assertThat(conEscala.getAeropuertoDestino(), is(equalTo("Aeropuerto de Bariloche (BRC)")));
    assertThat(conEscala.getSalida(), is(equalTo("2026-12-10 06:00")));
    assertThat(conEscala.getLlegada(), is(equalTo("2026-12-10 11:30")));
    assertThat(conEscala.getEscalas(), is(equalTo(1)));
    assertThat(conEscala.getPrecio(), is(nullValue()));
  }

  private void thenNoObtengoVuelos() {
    assertThat(this.resultado, is(empty()));
  }

  private void thenSeHicieronLasPeticionesEsperadas() {
    this.servidorExterno.verify();
  }
}
