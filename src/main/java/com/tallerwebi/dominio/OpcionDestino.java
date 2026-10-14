package com.tallerwebi.dominio;

import java.util.List;

public class OpcionDestino {

  private final String ciudad;
  private final String pais;
  private final List<Hotel> hoteles;
  private final List<Actividad> actividades;

  public OpcionDestino(
      String ciudad,
      String pais,
      List<Hotel> hoteles,
      List<Actividad> actividades) {
    this.ciudad = ciudad;
    this.pais = pais;
    this.hoteles = hoteles;
    this.actividades = actividades;
  }

  public String getCiudad() {
    return ciudad;
  }

  public String getPais() {
    return pais;
  }

  public List<Hotel> getHoteles() {
    return hoteles;
  }

  public List<Actividad> getActividades() {
    return actividades;
  }
}
