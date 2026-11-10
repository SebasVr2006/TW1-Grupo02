package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class TramoVuelo {

  private final String origen;
  private final String destino;
  private final String fecha;
  private List<Vuelo> vuelos;

  public TramoVuelo(String origen, String destino, String fecha) {
    this.origen = origen;
    this.destino = destino;
    this.fecha = fecha;
    this.vuelos = new ArrayList<>();
  }

  public String getOrigen() {
    return origen;
  }

  public String getDestino() {
    return destino;
  }

  public String getFecha() {
    return fecha;
  }

  public List<Vuelo> getVuelos() {
    return vuelos;
  }

  public void setVuelos(List<Vuelo> vuelos) {
    this.vuelos = vuelos;
  }
}
