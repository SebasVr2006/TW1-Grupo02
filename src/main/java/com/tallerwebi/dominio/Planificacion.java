package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class Planificacion {

  private List<Destino> destinos;
  private int adultos;
  private int ninos;
  private String alojamiento;
  private List<String> intereses;

  public Planificacion() {
    this.destinos = new ArrayList<>();
    this.intereses = new ArrayList<>();
  }

  public void agregarDestino(Destino destino) {
    this.destinos.add(destino);
  }

  public List<Destino> getDestinos() {
    return destinos;
  }

  public void setDestinos(List<Destino> destinos) {
    this.destinos = destinos;
  }

  public int getAdultos() {
    return adultos;
  }

  public void setAdultos(int adultos) {
    this.adultos = adultos;
  }

  public int getNinos() {
    return ninos;
  }

  public void setNinos(int ninos) {
    this.ninos = ninos;
  }

  public String getAlojamiento() {
    return alojamiento;
  }

  public void setAlojamiento(String alojamiento) {
    this.alojamiento = alojamiento;
  }

  public List<String> getIntereses() {
    return intereses;
  }

  public void setIntereses(List<String> intereses) {
    this.intereses = intereses;
  }
}
