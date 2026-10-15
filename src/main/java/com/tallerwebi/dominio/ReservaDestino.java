package com.tallerwebi.dominio;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class ReservaDestino {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombreDestino;
  private String pais;
  private String fechaInicio;
  private int duracion;

  @Embedded
  private HotelReservado hotel;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
    name = "reserva_destino_actividad",
    joinColumns = @JoinColumn(name = "reserva_destino_id")
  )
  @OrderColumn(name = "orden")
  @Fetch(FetchMode.SUBSELECT)
  private List<ActividadReservada> actividades = new ArrayList<>();

  private Double costoHotel;
  private Double costoActividades;
  private Double subtotal;

  public Long getId() {
    return id;
  }

  public String getNombreDestino() {
    return nombreDestino;
  }

  public void setNombreDestino(String nombreDestino) {
    this.nombreDestino = nombreDestino;
  }

  public String getPais() {
    return pais;
  }

  public void setPais(String pais) {
    this.pais = pais;
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

  public HotelReservado getHotel() {
    return hotel;
  }

  public void setHotel(HotelReservado hotel) {
    this.hotel = hotel;
  }

  public List<ActividadReservada> getActividades() {
    return actividades;
  }

  public void setActividades(List<ActividadReservada> actividades) {
    this.actividades = new ArrayList<>(actividades);
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
