package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.ReservaInvalidaException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("servicioReserva")
@Transactional
public class ServicioReservaImpl implements ServicioReserva {

  private static final String MENSAJE_SIN_USUARIO =
    "Debés iniciar sesión para confirmar la reserva.";
  private static final String MENSAJE_SIN_ITINERARIO = "No hay un itinerario para confirmar.";
  private static final String MENSAJE_NO_COINCIDE =
    "El itinerario no coincide con la planificación.";

  private final RepositorioReserva repositorioReserva;
  private final ServicioResumenItinerario servicioResumenItinerario;

  @Autowired
  public ServicioReservaImpl(
    RepositorioReserva repositorioReserva,
    ServicioResumenItinerario servicioResumenItinerario
  ) {
    this.repositorioReserva = repositorioReserva;
    this.servicioResumenItinerario = servicioResumenItinerario;
  }

  @Override
  public Reserva confirmarReserva(
    Usuario usuario,
    Planificacion planificacion,
    SeleccionItinerario seleccion
  ) {
    validarUsuario(usuario);
    if (planificacion == null || seleccion == null) {
      throw new ReservaInvalidaException(MENSAJE_SIN_ITINERARIO);
    }

    ResumenItinerario resumen = generarResumen(seleccion, planificacion);
    List<Destino> destinos = destinosDe(planificacion);
    if (destinos.size() != resumen.getDestinos().size()) {
      throw new ReservaInvalidaException(MENSAJE_NO_COINCIDE);
    }

    Reserva reserva = crearReserva(usuario, planificacion, resumen, destinos);
    this.repositorioReserva.guardar(reserva);
    return reserva;
  }

  @Override
  public List<Reserva> obtenerReservasDelUsuario(Usuario usuario) {
    validarUsuario(usuario);
    return this.repositorioReserva.buscarPorUsuario(usuario.getId());
  }

  private void validarUsuario(Usuario usuario) {
    if (usuario == null) {
      throw new ReservaInvalidaException(MENSAJE_SIN_USUARIO);
    }
  }

  private ResumenItinerario generarResumen(
    SeleccionItinerario seleccion,
    Planificacion planificacion
  ) {
    try {
      return this.servicioResumenItinerario.generarResumen(seleccion, planificacion);
    } catch (IllegalArgumentException e) {
      throw new ReservaInvalidaException(e.getMessage(), e);
    }
  }

  private List<Destino> destinosDe(Planificacion planificacion) {
    List<Destino> destinos = new ArrayList<>();
    for (Destino destino : planificacion.getDestinos()) {
      if (destino != null) {
        destinos.add(destino);
      }
    }
    return destinos;
  }

  private Reserva crearReserva(
    Usuario usuario,
    Planificacion planificacion,
    ResumenItinerario resumen,
    List<Destino> destinos
  ) {
    Reserva reserva = new Reserva();
    reserva.setUsuario(usuario);
    reserva.setFechaCreacion(LocalDateTime.now());
    reserva.setAdultos(planificacion.getAdultos());
    reserva.setNinos(planificacion.getNinos());
    reserva.setAlojamiento(planificacion.getAlojamiento());
    reserva.setIntereses(interesesDe(planificacion));
    reserva.setTotal(resumen.getTotal());

    for (int i = 0; i < destinos.size(); i++) {
      reserva.agregarDestino(crearReservaDestino(resumen.getDestinos().get(i), destinos.get(i)));
    }
    return reserva;
  }

  private List<String> interesesDe(Planificacion planificacion) {
    if (planificacion.getIntereses() == null) {
      return new ArrayList<>();
    }
    return planificacion.getIntereses();
  }

  private ReservaDestino crearReservaDestino(DetalleDestinoItinerario detalle, Destino destino) {
    ReservaDestino reservaDestino = new ReservaDestino();
    reservaDestino.setNombreDestino(detalle.getNombreDestino());
    reservaDestino.setPais(detalle.getNombrePais());
    reservaDestino.setFechaInicio(destino.getFechaInicio());
    reservaDestino.setDuracion(destino.getDuracion());
    reservaDestino.setHotel(
      new HotelReservado(
        detalle.getHotelSeleccionado().getNombre(),
        detalle.getHotelSeleccionado().getPrecioPorNoche()
      )
    );
    reservaDestino.setActividades(actividadesDe(detalle));
    reservaDestino.setCostoHotel(detalle.getCostoHotel());
    reservaDestino.setCostoActividades(detalle.getCostoActividades());
    reservaDestino.setSubtotal(detalle.getSubtotal());
    return reservaDestino;
  }

  private List<ActividadReservada> actividadesDe(DetalleDestinoItinerario detalle) {
    List<ActividadReservada> actividades = new ArrayList<>();
    for (Actividad actividad : detalle.getActividadesSeleccionadas()) {
      actividades.add(new ActividadReservada(actividad.getNombre(), actividad.getPrecio()));
    }
    return actividades;
  }
}
