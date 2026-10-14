package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class SeleccionItinerario {

  private List<SeleccionDestino> destinosSeleccionados = new ArrayList<>();

  public List<SeleccionDestino> getDestinosSeleccionados() {
    return destinosSeleccionados;
  }

  public void setDestinosSeleccionados(List<SeleccionDestino> destinosSeleccionados) {
    this.destinosSeleccionados = destinosSeleccionados;
  }
}
