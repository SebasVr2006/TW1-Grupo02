package com.tallerwebi.dominio;

import java.util.List;

public class ConfirmacionCompraDto {

  private final int adultos;
  private final int ninos;
  private final String alojamiento;
  private final List<DestinoConfirmacionDto> destinos;
  private final double totalAlojamiento;
  private final double totalActividades;
  private final double total;

  public ConfirmacionCompraDto(
    int adultos,
    int ninos,
    String alojamiento,
    List<DestinoConfirmacionDto> destinos,
    double totalAlojamiento,
    double totalActividades,
    double total
  ) {
    this.adultos = adultos;
    this.ninos = ninos;
    this.alojamiento = alojamiento;
    this.destinos = destinos;
    this.totalAlojamiento = totalAlojamiento;
    this.totalActividades = totalActividades;
    this.total = total;
  }

  public int getAdultos() {
    return adultos;
  }

  public int getNinos() {
    return ninos;
  }

  public String getAlojamiento() {
    return alojamiento;
  }

  public List<DestinoConfirmacionDto> getDestinos() {
    return destinos;
  }

  public double getTotalAlojamiento() {
    return totalAlojamiento;
  }

  public double getTotalActividades() {
    return totalActividades;
  }

  public double getTotal() {
    return total;
  }
}
