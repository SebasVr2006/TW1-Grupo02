package com.tallerwebi.dominio;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioResumenItinerario")
public class ServicioResumenItinerarioImpl implements ServicioResumenItinerario {

  private final ServicioItinerario servicioItinerario;

  @Autowired
  public ServicioResumenItinerarioImpl(ServicioItinerario servicioItinerario) {
    this.servicioItinerario = servicioItinerario;
  }

  @Override
  public ResumenItinerario generarResumen(
    SeleccionItinerario seleccionItinerario,
    Planificacion planificacion
  ) {
    validarDatos(seleccionItinerario, planificacion);

    ResumenItinerario resumen = new ResumenItinerario();
    List<DetalleDestinoItinerario> detalles = new ArrayList<>();
    double totalGeneral = 0.0;

    for (Destino destino : planificacion.getDestinos()) {
      if (destino == null) {
        continue;
      }

      DetalleDestinoItinerario detalle = generarDetalleDestino(destino, seleccionItinerario);

      detalles.add(detalle);
      totalGeneral += detalle.getSubtotal();
    }

    resumen.setDestinos(detalles);
    resumen.setTotal(totalGeneral);

    return resumen;
  }

  private void validarDatos(SeleccionItinerario seleccionItinerario, Planificacion planificacion) {
    if (
      seleccionItinerario == null ||
      seleccionItinerario.getDestinosSeleccionados() == null ||
      planificacion == null ||
      planificacion.getDestinos() == null
    ) {
      throw new IllegalArgumentException("La selección y la planificación son obligatorias.");
    }
  }

  private DetalleDestinoItinerario generarDetalleDestino(
    Destino destino,
    SeleccionItinerario seleccionItinerario
  ) {
    SeleccionDestino seleccion = buscarSeleccion(seleccionItinerario, destino.getNombre());

    if (seleccion == null) {
      throw new IllegalArgumentException(
        "Falta seleccionar las opciones de " + destino.getNombre()
      );
    }

    OpcionDestino opcion = this.servicioItinerario.generarItinerario(destino.getNombre());

    if (opcion == null) {
      throw new IllegalArgumentException("No hay opciones disponibles para " + destino.getNombre());
    }

    Hotel hotel = buscarHotel(opcion, seleccion.getHotelSeleccionado());

    if (hotel == null) {
      throw new IllegalArgumentException(
        "Debés elegir un hotel válido para " + destino.getNombre()
      );
    }

    List<Actividad> actividades = buscarActividades(
      opcion,
      seleccion.getActividadesSeleccionadas()
    );

    return crearDetalle(destino, opcion, hotel, actividades);
  }

  private DetalleDestinoItinerario crearDetalle(
    Destino destino,
    OpcionDestino opcion,
    Hotel hotel,
    List<Actividad> actividades
  ) {
    double costoHotel = hotel.getPrecioPorNoche() * destino.getDuracion();
    double costoActividades = calcularCostoActividades(actividades);

    DetalleDestinoItinerario detalle = new DetalleDestinoItinerario();

    detalle.setNombreDestino(opcion.getCiudad());
    detalle.setNombrePais(opcion.getPais());
    detalle.setHotelSeleccionado(hotel);
    detalle.setActividadesSeleccionadas(actividades);
    detalle.setCostoHotel(costoHotel);
    detalle.setCostoActividades(costoActividades);
    detalle.setSubtotal(costoHotel + costoActividades);

    return detalle;
  }

  private double calcularCostoActividades(List<Actividad> actividades) {
    double total = 0.0;

    for (Actividad actividad : actividades) {
      total += actividad.getPrecio();
    }

    return total;
  }

  private SeleccionDestino buscarSeleccion(
    SeleccionItinerario seleccionItinerario,
    String nombreDestino
  ) {
    for (SeleccionDestino seleccion : seleccionItinerario.getDestinosSeleccionados()) {
      if (coincideDestino(seleccion, nombreDestino)) {
        return seleccion;
      }
    }

    return null;
  }

  private boolean coincideDestino(SeleccionDestino seleccion, String nombreDestino) {
    return (
      seleccion != null &&
      nombreDestino != null &&
      seleccion.getNombreDestino() != null &&
      normalizarCiudad(nombreDestino).equals(normalizarCiudad(seleccion.getNombreDestino()))
    );
  }

  private String normalizarCiudad(String ciudad) {
    return Normalizer
      .normalize(ciudad.trim(), Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);
  }

  private Hotel buscarHotel(OpcionDestino opcion, String nombreHotel) {
    if (nombreHotel == null || nombreHotel.isBlank()) {
      return null;
    }

    for (Hotel hotel : opcion.getHoteles()) {
      if (hotel.getNombre().equals(nombreHotel)) {
        return hotel;
      }
    }

    return null;
  }

  private List<Actividad> buscarActividades(
    OpcionDestino opcion,
    List<String> nombresSeleccionados
  ) {
    List<Actividad> actividades = new ArrayList<>();

    if (nombresSeleccionados == null) {
      return actividades;
    }

    for (String nombreSeleccionado : nombresSeleccionados) {
      Actividad actividad = buscarActividad(opcion, nombreSeleccionado);

      if (actividad == null) {
        throw new IllegalArgumentException("Se seleccionó una actividad no válida.");
      }

      actividades.add(actividad);
    }

    return actividades;
  }

  private Actividad buscarActividad(OpcionDestino opcion, String nombreSeleccionado) {
    for (Actividad actividad : opcion.getActividades()) {
      if (actividad.getNombre().equals(nombreSeleccionado)) {
        return actividad;
      }
    }

    return null;
  }
}
