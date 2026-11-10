package com.tallerwebi.dominio.excepcion;

public class VuelosNoDisponiblesException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public VuelosNoDisponiblesException(String mensaje) {
    super(mensaje);
  }

  public VuelosNoDisponiblesException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
