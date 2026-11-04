package com.tallerwebi.infraestructura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tallerwebi.dominio.CiudadDto;
import com.tallerwebi.dominio.ProveedorCiudades;
import com.tallerwebi.dominio.excepcion.CiudadesNoDisponiblesException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProveedorCiudadesOpenMeteo implements ProveedorCiudades {

  private static final String URL_BUSQUEDA = "https://geocoding-api.open-meteo.com/v1/search";

  private final RestTemplate restTemplate;

  @Autowired
  public ProveedorCiudadesOpenMeteo(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<CiudadDto> buscar(String texto, int maximo) {
    URI uri = UriComponentsBuilder
      .fromUriString(URL_BUSQUEDA)
      .queryParam("name", texto)
      .queryParam("count", maximo)
      .queryParam("language", "es")
      .queryParam("format", "json")
      .build()
      .encode()
      .toUri();
    try {
      RespuestaOpenMeteo respuesta = restTemplate.getForObject(uri, RespuestaOpenMeteo.class);
      return convertir(respuesta);
    } catch (RestClientException e) {
      throw new CiudadesNoDisponiblesException("No se pudo consultar el servicio de ciudades", e);
    }
  }

  private List<CiudadDto> convertir(RespuestaOpenMeteo respuesta) {
    List<CiudadDto> ciudades = new ArrayList<>();
    if (respuesta == null || respuesta.getResults() == null) {
      return ciudades;
    }
    for (ResultadoOpenMeteo resultado : respuesta.getResults()) {
      ciudades.add(
        new CiudadDto(resultado.getName(), resultado.getCountry(), resultado.getAdmin1())
      );
    }
    return ciudades;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class RespuestaOpenMeteo {

    private List<ResultadoOpenMeteo> results;

    public List<ResultadoOpenMeteo> getResults() {
      return results;
    }

    public void setResults(List<ResultadoOpenMeteo> results) {
      this.results = results;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ResultadoOpenMeteo {

    private String name;
    private String country;
    private String admin1;

    @JsonProperty("name")
    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @JsonProperty("country")
    public String getCountry() {
      return country;
    }

    public void setCountry(String country) {
      this.country = country;
    }

    @JsonProperty("admin1")
    public String getAdmin1() {
      return admin1;
    }

    public void setAdmin1(String admin1) {
      this.admin1 = admin1;
    }
  }
}
