package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.HotelesNoDisponiblesException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioItinerario")
public class ServicioItinerarioImpl implements ServicioItinerario {

  private static final int MAXIMO_HOTELES = 6;

  private final RepositorioItinerario repositorioItinerario;
  private final ProveedorHoteles proveedorHoteles;

  @Autowired
  public ServicioItinerarioImpl(
    RepositorioItinerario repositorioItinerario,
    ProveedorHoteles proveedorHoteles
  ) {
    this.repositorioItinerario = repositorioItinerario;
    this.proveedorHoteles = proveedorHoteles;
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

  @Override
  @Transactional(readOnly = true)
  public OpcionDestino generarItinerario(Destino destino) {
    if (destino == null) {
      return null;
    }

    OpcionDestino opcionDeLaBase = generarItinerario(destino.getNombre());

    if (opcionDeLaBase == null) {
      return generarSoloConHotelesEnLinea(destino);
    }

    List<Hotel> hotelesEnLinea = buscarHotelesEnLinea(armarLugar(opcionDeLaBase), destino);

    if (hotelesEnLinea.isEmpty()) {
      return opcionDeLaBase;
    }

    return new OpcionDestino(
      opcionDeLaBase.getCiudad(),
      opcionDeLaBase.getPais(),
      hotelesEnLinea,
      opcionDeLaBase.getActividades()
    );
  }

  // Un destino que no está en la base igual se muestra si la API trae hoteles (sin actividades).
  private OpcionDestino generarSoloConHotelesEnLinea(Destino destino) {
    if (destino.getNombre() == null || destino.getNombre().isBlank()) {
      return null;
    }

    String[] partes = destino.getNombre().split(",", 2);
    String ciudad = partes[0].trim();
    String pais = partes.length > 1 ? partes[1].trim() : "";

    List<Hotel> hoteles = buscarHotelesEnLinea(armarLugar(ciudad, pais), destino);

    if (hoteles.isEmpty()) {
      return null;
    }

    return new OpcionDestino(ciudad, pais, hoteles, List.of());
  }

  private String armarLugar(OpcionDestino opcion) {
    return armarLugar(opcion.getCiudad(), opcion.getPais());
  }

  private String armarLugar(String ciudad, String pais) {
    return pais.isEmpty() ? ciudad : ciudad + ", " + pais;
  }

  // Si la API no responde o las fechas no sirven, se usan los hoteles de la base de datos.
  private List<Hotel> buscarHotelesEnLinea(String lugar, Destino destino) {
    if (destino.getFechaInicio() == null || destino.getDuracion() < 1) {
      return List.of();
    }

    try {
      LocalDate entrada = LocalDate.parse(destino.getFechaInicio());
      LocalDate salida = entrada.plusDays(destino.getDuracion());

      return this.proveedorHoteles.buscar(
          lugar,
          entrada.toString(),
          salida.toString(),
          MAXIMO_HOTELES
        );
    } catch (DateTimeParseException | HotelesNoDisponiblesException e) {
      return List.of();
    }
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
