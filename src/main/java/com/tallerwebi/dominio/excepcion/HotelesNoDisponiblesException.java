package com.tallerwebi.dominio.excepcion;

public class HotelesNoDisponiblesException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public HotelesNoDisponiblesException(String mensaje) {
    super(mensaje);
  }

  public HotelesNoDisponiblesException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
