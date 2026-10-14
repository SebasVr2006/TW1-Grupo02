package com.tallerwebi.dominio.excepcion;

public class PlanificacionInvalidaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PlanificacionInvalidaException(String mensaje) {
    super(mensaje);
  }
}
