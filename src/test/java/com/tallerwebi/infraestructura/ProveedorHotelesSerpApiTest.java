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

import com.tallerwebi.dominio.Hotel;
import com.tallerwebi.dominio.excepcion.HotelesNoDisponiblesException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class ProveedorHotelesSerpApiTest {

  private static final String CLAVE_API = "clave-de-prueba";

  private static final String RESPUESTA_CON_HOTELES =
    "{\"properties\":[" +
    "{\"name\":\"Hotel Cerro Catedral\",\"overall_rating\":4.6,\"extracted_hotel_class\":4," +
    "\"rate_per_night\":{\"lowest\":\"$120\",\"extracted_lowest\":120}," +
    "\"images\":[{\"thumbnail\":\"https://fotos.test/catedral.jpg\"," +
    "\"original_image\":\"https://fotos.test/catedral-grande.jpg\"}]}," +
    "{\"name\":\"Hostel del Lago\"," +
    "\"rate_per_night\":{\"extracted_before_taxes_fees\":45}}," +
    "{\"name\":\"Hotel sin precio\",\"overall_rating\":4.0}," +
    "{\"name\":\"hotel cerro catedral\",\"rate_per_night\":{\"extracted_lowest\":99}}," +
    "{\"rate_per_night\":{\"extracted_lowest\":70}}," +
    "{\"name\":\"Cabañas del Bosque\",\"rate_per_night\":{\"extracted_lowest\":80}}]}";

  private MockRestServiceServer servidorExterno;
  private RestTemplate restTemplate;
  private RelojManual reloj;
  private ProveedorHotelesSerpApi proveedor;

  private List<Hotel> resultado;

  @BeforeEach
  public void inicializar() {
    this.restTemplate = new RestTemplate();
    this.servidorExterno = MockRestServiceServer.bindTo(this.restTemplate).build();
    this.reloj = new RelojManual();
    this.proveedor = new ProveedorHotelesSerpApi(this.restTemplate, CLAVE_API, this.reloj);
  }

  @Test
  public void deberiaConvertirLosHotelesDeLaRespuesta() {
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    whenBuscoHoteles(10);

    thenObtengoLosHotelesConPrecio();
  }

  @Test
  public void deberiaConvertirLosDatosDeAyudaDelHotel() {
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    whenBuscoHoteles(10);

    thenElPrimerHotelTieneFotoEstrellasYPuntaje();
    thenElSegundoHotelNoTieneDatosDeAyuda();
  }

  @Test
  public void deberiaPedirLosHotelesConLosParametrosEsperados() {
    givenLaApiEsperaLaBusquedaConLosParametrosEstandar();

    whenBuscoHoteles(10);

    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void deberiaRespetarElMaximoDeHoteles() {
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    whenBuscoHoteles(2);

    assertThat(this.resultado, hasSize(2));
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiGoogleNoTieneResultados() {
    givenLaApiDevuelveHoteles(
      "{\"error\":\"Google Hotels hasn't returned any results for this query.\"}"
    );

    whenBuscoHoteles(10);

    assertThat(this.resultado, is(empty()));
  }

  @Test
  public void deberiaDevolverUnaListaVaciaSiLaRespuestaNoTienePropiedades() {
    givenLaApiDevuelveHoteles("{}");

    whenBuscoHoteles(10);

    assertThat(this.resultado, is(empty()));
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiNoHayClaveDeApi() {
    this.proveedor = new ProveedorHotelesSerpApi(this.restTemplate, " ", this.reloj);

    assertThrows(HotelesNoDisponiblesException.class, () -> whenBuscoHoteles(10));
    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiLaApiFalla() {
    givenLaApiFalla();

    assertThrows(HotelesNoDisponiblesException.class, () -> whenBuscoHoteles(10));
  }

  @Test
  public void deberiaLanzarUnaExcepcionSiLaApiDevuelveUnError() {
    givenLaApiDevuelveHoteles("{\"error\":\"Invalid API key.\"}");

    assertThrows(HotelesNoDisponiblesException.class, () -> whenBuscoHoteles(10));
  }

  @Test
  public void deberiaReutilizarLaRespuestaDeLaMismaBusqueda() {
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    whenBuscoHoteles(10);
    List<Hotel> primeraBusqueda = this.resultado;
    whenBuscoHoteles(10);

    assertThat(this.resultado, equalTo(primeraBusqueda));
    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void deberiaConsultarDeNuevoCuandoElResultadoGuardadoExpira() {
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    whenBuscoHoteles(10);
    this.reloj.avanzar(Duration.ofMinutes(61));
    whenBuscoHoteles(10);

    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void deberiaConsultarDeNuevoSiCambianLasFechas() {
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    this.resultado = this.proveedor.buscar("Bariloche", "2026-12-10", "2026-12-15", 10);
    this.resultado = this.proveedor.buscar("Bariloche", "2026-12-11", "2026-12-15", 10);

    thenSeHizoLaPeticionEsperada();
  }

  @Test
  public void noDeberiaGuardarLasRespuestasConError() {
    givenLaApiFalla();
    givenLaApiDevuelveHoteles(RESPUESTA_CON_HOTELES);

    assertThrows(HotelesNoDisponiblesException.class, () -> whenBuscoHoteles(10));
    whenBuscoHoteles(10);

    assertThat(this.resultado, hasSize(3));
    thenSeHizoLaPeticionEsperada();
  }

  private void givenLaApiDevuelveHoteles(String json) {
    this.servidorExterno.expect(requestTo(containsString("engine=google_hotels&")))
      .andExpect(method(HttpMethod.GET))
      .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
  }

  private void givenLaApiEsperaLaBusquedaConLosParametrosEstandar() {
    this.servidorExterno.expect(requestTo(containsString("engine=google_hotels&")))
      .andExpect(requestTo(containsString("q=Hoteles%20en%20Bariloche")))
      .andExpect(requestTo(containsString("check_in_date=2026-12-10")))
      .andExpect(requestTo(containsString("check_out_date=2026-12-15")))
      .andExpect(requestTo(containsString("currency=USD")))
      .andExpect(requestTo(containsString("hl=es")))
      .andExpect(requestTo(containsString("api_key=" + CLAVE_API)))
      .andRespond(withSuccess("{\"properties\":[]}", MediaType.APPLICATION_JSON));
  }

  private void givenLaApiFalla() {
    this.servidorExterno.expect(requestTo(containsString("serpapi.com")))
      .andRespond(withServerError());
  }

  private void whenBuscoHoteles(int maximo) {
    this.resultado = this.proveedor.buscar("Bariloche", "2026-12-10", "2026-12-15", maximo);
  }

  private void thenObtengoLosHotelesConPrecio() {
    assertThat(this.resultado, hasSize(3));
    assertThat(this.resultado.get(0).getNombre(), equalTo("Hotel Cerro Catedral"));
    assertThat(this.resultado.get(0).getPrecioPorNoche(), equalTo(120.0));
    assertThat(this.resultado.get(1).getNombre(), equalTo("Hostel del Lago"));
    assertThat(this.resultado.get(1).getPrecioPorNoche(), equalTo(45.0));
    assertThat(this.resultado.get(2).getNombre(), equalTo("Cabañas del Bosque"));
    assertThat(this.resultado.get(2).getPrecioPorNoche(), equalTo(80.0));
  }

  private void thenElPrimerHotelTieneFotoEstrellasYPuntaje() {
    Hotel hotel = this.resultado.get(0);

    assertThat(hotel.getImagenUrl(), equalTo("https://fotos.test/catedral.jpg"));
    assertThat(hotel.getEstrellas(), equalTo(4));
    assertThat(hotel.getPuntaje(), equalTo(4.6));
  }

  private void thenElSegundoHotelNoTieneDatosDeAyuda() {
    Hotel hotel = this.resultado.get(1);

    assertThat(hotel.getImagenUrl(), is(nullValue()));
    assertThat(hotel.getEstrellas(), is(nullValue()));
    assertThat(hotel.getPuntaje(), is(nullValue()));
  }

  private void thenSeHizoLaPeticionEsperada() {
    this.servidorExterno.verify();
  }

  private static class RelojManual extends Clock {

    private Instant ahora = Instant.parse("2026-11-11T12:00:00Z");

    void avanzar(Duration duracion) {
      this.ahora = this.ahora.plus(duracion);
    }

    @Override
    public ZoneId getZone() {
      return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zona) {
      return this;
    }

    @Override
    public Instant instant() {
      return this.ahora;
    }
  }
}
