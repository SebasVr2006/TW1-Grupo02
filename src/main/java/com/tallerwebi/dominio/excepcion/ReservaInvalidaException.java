package com.tallerwebi.dominio.excepcion;

public class ReservaInvalidaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ReservaInvalidaException(String mensaje) {
    super(mensaje);
  }

  public ReservaInvalidaException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
