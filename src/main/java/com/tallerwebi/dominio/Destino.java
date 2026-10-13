package com.tallerwebi.dominio;

public class Destino {

  private String nombre;
  private String fechaInicio;
  private int duracion;

  public Destino() {}

  public Destino(String nombre, String fechaInicio, int duracion) {
    this.nombre = nombre;
    this.fechaInicio = fechaInicio;
    this.duracion = duracion;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(String fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public int getDuracion() {
    return duracion;
  }

  public void setDuracion(int duracion) {
    this.duracion = duracion;
  }
}
