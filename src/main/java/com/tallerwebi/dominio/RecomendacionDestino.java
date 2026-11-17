package com.tallerwebi.dominio;

public class RecomendacionDestino {

  private final String ciudad;
  private final String pais;
  private final String motivo;

  public RecomendacionDestino(String ciudad, String pais, String motivo) {
    this.ciudad = ciudad;
    this.pais = pais;
    this.motivo = motivo;
  }

  public String getCiudad() {
    return ciudad;
  }

  public String getPais() {
    return pais;
  }

  public String getMotivo() {
    return motivo;
  }
}
