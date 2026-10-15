package com.tallerwebi.dominio;

import jakarta.persistence.Embeddable;

@Embeddable
public class HotelReservado {

  private String nombre;
  private Double precioPorNoche;

  public HotelReservado() {}

  public HotelReservado(String nombre, Double precioPorNoche) {
    this.nombre = nombre;
    this.precioPorNoche = precioPorNoche;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public Double getPrecioPorNoche() {
    return precioPorNoche;
  }

  public void setPrecioPorNoche(Double precioPorNoche) {
    this.precioPorNoche = precioPorNoche;
  }
}
