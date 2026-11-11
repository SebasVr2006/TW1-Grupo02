package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "hotel")
public class Hotel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precioPorNoche;

  @ManyToOne
  @JoinColumn(name = "destino_id")
  private DestinoDisponible destino;

  // Datos que solo trae la API de hoteles: no se guardan en la base.
  @Transient
  private String imagenUrl;

  @Transient
  private Integer estrellas;

  @Transient
  private Double puntaje;

  public Hotel() {}

  public Hotel(String nombre, Double precioPorNoche) {
    this.nombre = nombre;
    this.precioPorNoche = precioPorNoche;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public DestinoDisponible getDestino() {
    return destino;
  }

  public void setDestino(DestinoDisponible destino) {
    this.destino = destino;
  }

  public String getImagenUrl() {
    return imagenUrl;
  }

  public void setImagenUrl(String imagenUrl) {
    this.imagenUrl = imagenUrl;
  }

  public Integer getEstrellas() {
    return estrellas;
  }

  public void setEstrellas(Integer estrellas) {
    this.estrellas = estrellas;
  }

  public Double getPuntaje() {
    return puntaje;
  }

  public void setPuntaje(Double puntaje) {
    this.puntaje = puntaje;
  }
}
