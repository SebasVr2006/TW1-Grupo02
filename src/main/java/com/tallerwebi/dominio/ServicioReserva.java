package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioReserva {
  Reserva confirmarReserva(
    Usuario usuario,
    Planificacion planificacion,
    SeleccionItinerario seleccion
  );
  List<Reserva> obtenerReservasDelUsuario(Usuario usuario);
}
