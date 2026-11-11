package com.tallerwebi.dominio;

public interface ServicioItinerario {
  OpcionDestino generarItinerario(String nombreDestino);

  OpcionDestino generarItinerario(Destino destino);
}
