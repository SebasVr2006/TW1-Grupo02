package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ServicioItinerario {
  List<OpcionDestino> generarItinerario();
}
