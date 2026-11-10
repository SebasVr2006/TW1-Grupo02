package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class Planificacion {

  private String origen;
  private List<Destino> destinos;
  private int adultos;
  private int ninos;
  private String alojamiento;
  private List<String> intereses;
  private List<Vuelo> vuelosSeleccionados;

  public Planificacion() {
    this.destinos = new ArrayList<>();
    this.intereses = new ArrayList<>();
    this.vuelosSeleccionados = new ArrayList<>();
  }

  public String getOrigen() {
    return origen;
  }

  public void setOrigen(String origen) {
    this.origen = origen;
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

  public List<Vuelo> getVuelosSeleccionados() {
    return vuelosSeleccionados;
  }

  public void setVuelosSeleccionados(List<Vuelo> vuelosSeleccionados) {
    this.vuelosSeleccionados = vuelosSeleccionados;
  }
}
