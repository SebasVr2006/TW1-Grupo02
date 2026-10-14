package com.tallerwebi.dominio;

@FunctionalInterface
public interface ServicioItinerario {
  OpcionDestino generarItinerario(String nombreDestino);
}
