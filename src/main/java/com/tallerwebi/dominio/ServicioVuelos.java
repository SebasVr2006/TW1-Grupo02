package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioVuelos {
  List<TramoVuelo> buscarVuelos(Planificacion planificacion);

  List<Vuelo> seleccionarVuelos(List<TramoVuelo> tramos, List<Integer> indicesElegidos);
}
