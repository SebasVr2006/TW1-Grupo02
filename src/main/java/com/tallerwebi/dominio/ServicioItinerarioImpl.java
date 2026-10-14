package com.tallerwebi.dominio;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioItinerario")
public class ServicioItinerarioImpl implements ServicioItinerario {

  private final RepositorioItinerario repositorioItinerario;

  @Autowired
  public ServicioItinerarioImpl(RepositorioItinerario repositorioItinerario) {
    this.repositorioItinerario = repositorioItinerario;
  }

  @Override
  @Transactional(readOnly = true)
  public OpcionDestino generarItinerario(String nombreDestino) {
    if (nombreDestino == null || nombreDestino.isBlank()) {
      return null;
    }

    String ciudad = Normalizer
      .normalize(nombreDestino.trim(), Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);

    DestinoDisponible destino = this.repositorioItinerario.buscarDestinoPorCiudad(ciudad);

    if (destino == null) {
      return null;
    }

    List<Hotel> hoteles = this.repositorioItinerario.buscarHotelesPorDestino(destino.getId());

    List<Actividad> actividades =
      this.repositorioItinerario.buscarActividadesPorDestino(destino.getId());

    return new OpcionDestino(destino.getCiudad(), destino.getPais(), hoteles, actividades);
  }
}
