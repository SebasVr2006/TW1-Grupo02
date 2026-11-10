package com.tallerwebi.dominio.excepcion;

public class SeleccionVueloInvalidaException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SeleccionVueloInvalidaException(String mensaje) {
    super(mensaje);
  }
}
