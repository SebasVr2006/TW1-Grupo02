package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class ResumenItinerario {

  private List<DetalleDestinoItinerario> destinos = new ArrayList<>();
  private List<Vuelo> vuelos = new ArrayList<>();
  private Double costoVuelos = 0.0;
  private Double total = 0.0;

  public List<DetalleDestinoItinerario> getDestinos() {
    return destinos;
  }

  public void setDestinos(List<DetalleDestinoItinerario> destinos) {
    this.destinos = destinos;
  }

  public List<Vuelo> getVuelos() {
    return vuelos;
  }

  public void setVuelos(List<Vuelo> vuelos) {
    this.vuelos = vuelos;
  }

  public Double getCostoVuelos() {
    return costoVuelos;
  }

  public void setCostoVuelos(Double costoVuelos) {
    this.costoVuelos = costoVuelos;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
}
