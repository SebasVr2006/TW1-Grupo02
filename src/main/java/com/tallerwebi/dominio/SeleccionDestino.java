package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class SeleccionDestino {

  private String nombreDestino;
  private String hotelSeleccionado;
  private List<String> actividadesSeleccionadas = new ArrayList<>();

  public String getNombreDestino() {
    return nombreDestino;
  }

  public void setNombreDestino(String nombreDestino) {
    this.nombreDestino = nombreDestino;
  }

  public String getHotelSeleccionado() {
    return hotelSeleccionado;
  }

  public void setHotelSeleccionado(String hotelSeleccionado) {
    this.hotelSeleccionado = hotelSeleccionado;
  }

  public List<String> getActividadesSeleccionadas() {
    return actividadesSeleccionadas;
  }

  public void setActividadesSeleccionadas(List<String> actividadesSeleccionadas) {
    this.actividadesSeleccionadas = actividadesSeleccionadas;
  }
}
