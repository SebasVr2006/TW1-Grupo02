package com.tallerwebi.dominio;

public enum EstadoReserva {
  PENDIENTE_DE_PAGO("Pendiente de pago"),
  PAGADA("Pagada");

  private final String descripcion;

  EstadoReserva(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }
}
