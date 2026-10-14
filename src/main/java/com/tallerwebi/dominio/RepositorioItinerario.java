package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioItinerario {
  DestinoDisponible buscarDestinoPorCiudad(String ciudad);

  List<Hotel> buscarHotelesPorDestino(Long destinoId);

  List<Actividad> buscarActividadesPorDestino(Long destinoId);
}
