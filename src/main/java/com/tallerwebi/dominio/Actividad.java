package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "actividad")
public class Actividad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nombre;
  private Double precio;

  @ManyToOne
  @JoinColumn(name = "destino_id")
  private DestinoDisponible destino;

  public Actividad() {}

  public Actividad(String nombre, Double precio) {
    this.nombre = nombre;
    this.precio = precio;
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

  public Double getPrecio() {
    return precio;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public DestinoDisponible getDestino() {
    return destino;
  }

  public void setDestino(DestinoDisponible destino) {
    this.destino = destino;
  }
}
