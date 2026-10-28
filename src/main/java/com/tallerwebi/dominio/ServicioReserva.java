package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioReserva {
  Reserva confirmarReserva(
    Long idUsuario,
    Planificacion planificacion,
    SeleccionItinerario seleccion
  );
  ConfirmacionCompraDto prepararConfirmacion(
    Planificacion planificacion,
    SeleccionItinerario seleccion
  );
  List<Reserva> obtenerReservasDelUsuario(Long idUsuario);
}
