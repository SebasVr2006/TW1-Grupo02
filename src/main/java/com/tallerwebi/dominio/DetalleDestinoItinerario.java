package com.tallerwebi.dominio;

import java.util.ArrayList;
import java.util.List;

public class DetalleDestinoItinerario {

  private String nombreDestino;
  private String nombrePais;
  private Hotel hotelSeleccionado;
  private List<Actividad> actividadesSeleccionadas = new ArrayList<>();
  private Double costoHotel;
  private Double costoActividades;
  private Double subtotal;

  public String getNombreDestino() {
    return nombreDestino;
  }

  public void setNombreDestino(String nombreDestino) {
    this.nombreDestino = nombreDestino;
  }

  public String getNombrePais() {
    return nombrePais;
  }

  public void setNombrePais(String nombrePais) {
    this.nombrePais = nombrePais;
  }

  public Hotel getHotelSeleccionado() {
    return hotelSeleccionado;
  }

  public void setHotelSeleccionado(Hotel hotelSeleccionado) {
    this.hotelSeleccionado = hotelSeleccionado;
  }

  public List<Actividad> getActividadesSeleccionadas() {
    return actividadesSeleccionadas;
  }

  public void setActividadesSeleccionadas(List<Actividad> actividadesSeleccionadas) {
    this.actividadesSeleccionadas = actividadesSeleccionadas;
  }

  public Double getCostoHotel() {
    return costoHotel;
  }

  public void setCostoHotel(Double costoHotel) {
    this.costoHotel = costoHotel;
  }

  public Double getCostoActividades() {
    return costoActividades;
  }

  public void setCostoActividades(Double costoActividades) {
    this.costoActividades = costoActividades;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }
}
