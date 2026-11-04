package com.tallerwebi.dominio;

import java.util.Objects;

public class CiudadDto {

  private final String nombre;
  private final String pais;
  private final String provincia;

  public CiudadDto(String nombre, String pais, String provincia) {
    this.nombre = nombre;
    this.pais = pais;
    this.provincia = provincia;
  }

  public String getNombre() {
    return nombre;
  }

  public String getPais() {
    return pais;
  }

  public String getProvincia() {
    return provincia;
  }

  @Override
  public boolean equals(Object otro) {
    if (this == otro) {
      return true;
    }
    if (!(otro instanceof CiudadDto)) {
      return false;
    }
    CiudadDto that = (CiudadDto) otro;
    return (
      Objects.equals(nombre, that.nombre) &&
      Objects.equals(pais, that.pais) &&
      Objects.equals(provincia, that.provincia)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(nombre, pais, provincia);
  }
}
