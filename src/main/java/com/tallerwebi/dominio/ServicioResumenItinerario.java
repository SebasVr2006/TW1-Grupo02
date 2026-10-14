package com.tallerwebi.dominio;

@FunctionalInterface
public interface ServicioResumenItinerario {
  ResumenItinerario generarResumen(
    SeleccionItinerario seleccionItinerario,
    Planificacion planificacion
  );
}
