package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class ResumenItinerario {

  private List<DetalleDestinoItinerario> destinos = new ArrayList<>();
  private Double total = 0.0;

  public List<DetalleDestinoItinerario> getDestinos() {
    return destinos;
  }

  public void setDestinos(List<DetalleDestinoItinerario> destinos) {
    this.destinos = destinos;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
}
