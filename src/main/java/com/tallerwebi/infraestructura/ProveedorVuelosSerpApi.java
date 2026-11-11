package com.tallerwebi.infraestructura;

import com.fasterxml.jackson.databind.JsonNode;
import com.tallerwebi.dominio.ProveedorVuelos;
import com.tallerwebi.dominio.Vuelo;
import com.tallerwebi.dominio.excepcion.VuelosNoDisponiblesException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProveedorVuelosSerpApi implements ProveedorVuelos {

  private static final String URL_BUSQUEDA = "https://serpapi.com/search.json";
  private static final String MOTOR_VUELOS = "google_flights";
  private static final String MOTOR_AUTOCOMPLETADO = "google_flights_autocomplete";
  private static final String VUELO_SOLO_IDA = "2";
  private static final String MONEDA = "USD";
  private static final String IDIOMA = "es";
  private static final String MENSAJE_SIN_RESULTADOS = "hasn't returned any results";
  private static final String TIPO_CIUDAD = "city";
  private static final int TIMEOUT_CONEXION_MS = 5000;
  private static final int TIMEOUT_LECTURA_MS = 20000;

  private final RestTemplate restTemplate;
  private final String claveApi;
  private final Map<String, String> ubicaciones = new ConcurrentHashMap<>();

  @Autowired
  public ProveedorVuelosSerpApi() {
    this(crearRestTemplate(), System.getenv("SERPAPI_API_KEY"));
  }

  public ProveedorVuelosSerpApi(RestTemplate restTemplate, String claveApi) {
    this.restTemplate = restTemplate;
    this.claveApi = claveApi;
  }

  @Override
  public List<Vuelo> buscar(String origen, String destino, String fecha, int adultos, int ninos) {
    validarClaveApi();

    String idOrigen = resolverUbicacion(origen);
    if (idOrigen == null) {
      return new ArrayList<>();
    }
    String idDestino = resolverUbicacion(destino);
    if (idDestino == null) {
      return new ArrayList<>();
    }

    JsonNode respuesta = consultar(armarUriVuelos(idOrigen, idDestino, fecha, adultos, ninos));
    if (esRespuestaSinResultados(respuesta)) {
      return new ArrayList<>();
    }
    validarRespuesta(respuesta);

    return convertir(respuesta);
  }

  private static RestTemplate crearRestTemplate() {
    SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
    fabrica.setConnectTimeout(TIMEOUT_CONEXION_MS);
    fabrica.setReadTimeout(TIMEOUT_LECTURA_MS);
    return new RestTemplate(fabrica);
  }

  private void validarClaveApi() {
    if (claveApi == null || claveApi.isBlank()) {
      throw new VuelosNoDisponiblesException("El servicio de vuelos no está configurado");
    }
  }

  private String resolverUbicacion(String nombre) {
    String clave = nombre.trim().toLowerCase(Locale.ROOT);
    String idGuardado = ubicaciones.get(clave);
    if (idGuardado != null) {
      return idGuardado;
    }

    JsonNode respuesta = consultar(armarUriAutocompletado(nombre.trim()));
    String id = buscarCiudadConAeropuerto(respuesta);
    if (id != null) {
      ubicaciones.put(clave, id);
    }
    return id;
  }

  private String buscarCiudadConAeropuerto(JsonNode respuesta) {
    if (respuesta == null) {
      return null;
    }
    for (JsonNode sugerencia : respuesta.path("suggestions")) {
      boolean esCiudad = TIPO_CIUDAD.equals(sugerencia.path("type").asText());
      boolean tieneAeropuertos = sugerencia.path("airports").size() > 0;
      if (esCiudad && tieneAeropuertos && sugerencia.hasNonNull("id")) {
        return sugerencia.get("id").asText();
      }
    }
    return null;
  }

  private URI armarUriAutocompletado(String nombre) {
    return UriComponentsBuilder
      .fromUriString(URL_BUSQUEDA)
      .queryParam("engine", MOTOR_AUTOCOMPLETADO)
      .queryParam("q", nombre)
      .queryParam("exclude_regions", "true")
      .queryParam("hl", IDIOMA)
      .queryParam("api_key", claveApi)
      .build()
      .encode()
      .toUri();
  }

  private URI armarUriVuelos(
    String idOrigen,
    String idDestino,
    String fecha,
    int adultos,
    int ninos
  ) {
    return UriComponentsBuilder
      .fromUriString(URL_BUSQUEDA)
      .queryParam("engine", MOTOR_VUELOS)
      .queryParam("departure_id", idOrigen)
      .queryParam("arrival_id", idDestino)
      .queryParam("outbound_date", fecha)
      .queryParam("type", VUELO_SOLO_IDA)
      .queryParam("adults", adultos)
      .queryParam("children", ninos)
      .queryParam("currency", MONEDA)
      .queryParam("hl", IDIOMA)
      .queryParam("api_key", claveApi)
      .build()
      .encode()
      .toUri();
  }

  private JsonNode consultar(URI uri) {
    try {
      return restTemplate.getForObject(uri, JsonNode.class);
    } catch (RestClientException e) {
      throw new VuelosNoDisponiblesException("No se pudo consultar el servicio de vuelos", e);
    }
  }

  private boolean esRespuestaSinResultados(JsonNode respuesta) {
    return (
      respuesta != null && respuesta.path("error").asText("").contains(MENSAJE_SIN_RESULTADOS)
    );
  }

  private void validarRespuesta(JsonNode respuesta) {
    if (respuesta != null && respuesta.hasNonNull("error")) {
      throw new VuelosNoDisponiblesException("El servicio de vuelos devolvió un error");
    }
  }

  private List<Vuelo> convertir(JsonNode respuesta) {
    List<Vuelo> vuelos = new ArrayList<>();
    if (respuesta == null) {
      return vuelos;
    }
    agregarVuelos(vuelos, respuesta.path("best_flights"));
    agregarVuelos(vuelos, respuesta.path("other_flights"));
    return vuelos;
  }

  private void agregarVuelos(List<Vuelo> vuelos, JsonNode opciones) {
    for (JsonNode opcion : opciones) {
      if (opcion.path("flights").size() > 0) {
        vuelos.add(convertirOpcion(opcion));
      }
    }
  }

  private Vuelo convertirOpcion(JsonNode opcion) {
    JsonNode tramos = opcion.path("flights");
    JsonNode primero = tramos.get(0);
    JsonNode ultimo = tramos.get(tramos.size() - 1);

    Vuelo vuelo = new Vuelo();
    vuelo.setAerolinea(nombrarAerolineas(tramos));
    vuelo.setLogoAerolinea(primero.path("airline_logo").asText(""));
    vuelo.setAeropuertoOrigen(describirAeropuerto(primero.path("departure_airport")));
    vuelo.setAeropuertoDestino(describirAeropuerto(ultimo.path("arrival_airport")));
    vuelo.setSalida(primero.path("departure_airport").path("time").asText(""));
    vuelo.setLlegada(ultimo.path("arrival_airport").path("time").asText(""));
    vuelo.setDuracionMinutos(opcion.path("total_duration").asInt(0));
    vuelo.setEscalas(tramos.size() - 1);
    if (opcion.hasNonNull("price")) {
      vuelo.setPrecio(opcion.get("price").asInt());
    }
    return vuelo;
  }

  private String nombrarAerolineas(JsonNode tramos) {
    Set<String> aerolineas = new LinkedHashSet<>();
    for (JsonNode tramo : tramos) {
      String nombre = tramo.path("airline").asText("");
      if (!nombre.isBlank()) {
        aerolineas.add(nombre);
      }
    }
    return String.join(", ", aerolineas);
  }

  private String describirAeropuerto(JsonNode aeropuerto) {
    String nombre = aeropuerto.path("name").asText("");
    String codigo = aeropuerto.path("id").asText("");
    return nombre.isBlank() ? codigo : nombre + " (" + codigo + ")";
  }
}
