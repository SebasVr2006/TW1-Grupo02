package com.tallerwebi.dominio.excepcion;

public class DestinoInvalidoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DestinoInvalidoException(String mensaje) {
    super(mensaje);
  }
}
