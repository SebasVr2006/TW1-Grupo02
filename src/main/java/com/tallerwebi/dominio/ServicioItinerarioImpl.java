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

    String[] partes = nombreDestino.split(",", 2);
    String ciudad = normalizar(partes[0]);
    String pais = partes.length > 1 ? normalizar(partes[1]) : "";

    DestinoDisponible destino = this.repositorioItinerario.buscarDestinoPorCiudad(ciudad);

    if (destino == null || !coincidePais(destino, pais)) {
      return null;
    }

    List<Hotel> hoteles = this.repositorioItinerario.buscarHotelesPorDestino(destino.getId());

    List<Actividad> actividades =
      this.repositorioItinerario.buscarActividadesPorDestino(destino.getId());

    return new OpcionDestino(destino.getCiudad(), destino.getPais(), hoteles, actividades);
  }

  private boolean coincidePais(DestinoDisponible destino, String paisIndicado) {
    return paisIndicado.isEmpty() || normalizar(destino.getPais()).equals(paisIndicado);
  }

  private String normalizar(String texto) {
    return Normalizer
      .normalize(texto.trim(), Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);
  }
}
