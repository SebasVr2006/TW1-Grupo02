package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class SeleccionVuelos {

  private List<Integer> indicesVuelos;

  public SeleccionVuelos() {
    this.indicesVuelos = new ArrayList<>();
  }

  public List<Integer> getIndicesVuelos() {
    return indicesVuelos;
  }

  public void setIndicesVuelos(List<Integer> indicesVuelos) {
    this.indicesVuelos = indicesVuelos;
  }
}
