package com.tallerwebi.dominio.excepcion;

public class CiudadesNoDisponiblesException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public CiudadesNoDisponiblesException(String mensaje) {
    super(mensaje);
  }

  public CiudadesNoDisponiblesException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
