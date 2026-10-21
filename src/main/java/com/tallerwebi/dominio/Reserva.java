package com.tallerwebi.dominio;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Reserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  private LocalDateTime fechaCreacion;
  private int adultos;
  private int ninos;
  private String alojamiento;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "reserva_interes", joinColumns = @JoinColumn(name = "reserva_id"))
  @Column(name = "interes")
  @OrderColumn(name = "orden")
  @Fetch(FetchMode.SUBSELECT)
  private List<String> intereses = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  @JoinColumn(name = "reserva_id")
  @OrderBy("id")
  @Fetch(FetchMode.SUBSELECT)
  private List<ReservaDestino> destinos = new ArrayList<>();

  private Double total;

  @Enumerated(EnumType.STRING)
  private EstadoReserva estado = EstadoReserva.PENDIENTE_DE_PAGO;

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public LocalDateTime getFechaCreacion() {
    return fechaCreacion;
  }

  public void setFechaCreacion(LocalDateTime fechaCreacion) {
    this.fechaCreacion = fechaCreacion;
  }

  public String getFechaCreacionFormateada() {
    if (fechaCreacion == null) {
      return "";
    }
    return fechaCreacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
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
    this.intereses = new ArrayList<>(intereses);
  }

  public List<ReservaDestino> getDestinos() {
    return destinos;
  }

  public void agregarDestino(ReservaDestino destino) {
    this.destinos.add(destino);
  }

  public EstadoReserva getEstado() {
    return estado;
  }

  public void setEstado(EstadoReserva estado) {
    this.estado = estado;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
}
