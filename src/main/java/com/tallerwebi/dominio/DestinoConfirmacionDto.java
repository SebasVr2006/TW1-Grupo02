package com.tallerwebi.dominio;

public class DestinoConfirmacionDto {

  private final String nombre;
  private final String pais;
  private final String fechaInicio;
  private final int noches;

  public DestinoConfirmacionDto(String nombre, String pais, String fechaInicio, int noches) {
    this.nombre = nombre;
    this.pais = pais;
    this.fechaInicio = fechaInicio;
    this.noches = noches;
  }

  public String getNombre() {
    return nombre;
  }

  public String getPais() {
    return pais;
  }

  public String getFechaInicio() {
    return fechaInicio;
  }

  public int getNoches() {
    return noches;
  }
}
