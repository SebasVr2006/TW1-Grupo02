package com.tallerwebi.dominio;

public class Vuelo {

  private static final int MINUTOS_POR_HORA = 60;

  private String aerolinea;
  private String logoAerolinea;
  private String aeropuertoOrigen;
  private String aeropuertoDestino;
  private String salida;
  private String llegada;
  private int duracionMinutos;
  private int escalas;
  private Integer precio;

  public Vuelo() {}

  public String getAerolinea() {
    return aerolinea;
  }

  public void setAerolinea(String aerolinea) {
    this.aerolinea = aerolinea;
  }

  public String getLogoAerolinea() {
    return logoAerolinea;
  }

  public void setLogoAerolinea(String logoAerolinea) {
    this.logoAerolinea = logoAerolinea;
  }

  public String getAeropuertoOrigen() {
    return aeropuertoOrigen;
  }

  public void setAeropuertoOrigen(String aeropuertoOrigen) {
    this.aeropuertoOrigen = aeropuertoOrigen;
  }

  public String getAeropuertoDestino() {
    return aeropuertoDestino;
  }

  public void setAeropuertoDestino(String aeropuertoDestino) {
    this.aeropuertoDestino = aeropuertoDestino;
  }

  public String getSalida() {
    return salida;
  }

  public void setSalida(String salida) {
    this.salida = salida;
  }

  public String getLlegada() {
    return llegada;
  }

  public void setLlegada(String llegada) {
    this.llegada = llegada;
  }

  public int getDuracionMinutos() {
    return duracionMinutos;
  }

  public void setDuracionMinutos(int duracionMinutos) {
    this.duracionMinutos = duracionMinutos;
  }

  public int getEscalas() {
    return escalas;
  }

  public void setEscalas(int escalas) {
    this.escalas = escalas;
  }

  public Integer getPrecio() {
    return precio;
  }

  public void setPrecio(Integer precio) {
    this.precio = precio;
  }

  public String getDuracionFormateada() {
    int horas = duracionMinutos / MINUTOS_POR_HORA;
    int minutos = duracionMinutos % MINUTOS_POR_HORA;

    if (horas == 0) {
      return minutos + " min";
    }
    if (minutos == 0) {
      return horas + " h";
    }
    return horas + " h " + minutos + " min";
  }

  public String getEscalasTexto() {
    if (escalas == 0) {
      return "Directo";
    }
    return escalas == 1 ? "1 escala" : escalas + " escalas";
  }
}
