package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
import com.tallerwebi.dominio.excepcion.SeleccionVueloInvalidaException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioVuelosImpl implements ServicioVuelos {

  private final ProveedorVuelos proveedorVuelos;

  @Autowired
  public ServicioVuelosImpl(ProveedorVuelos proveedorVuelos) {
    this.proveedorVuelos = proveedorVuelos;
  }

  @Override
  public List<TramoVuelo> buscarVuelos(Planificacion planificacion) {
    validarPlanificacion(planificacion);

    List<TramoVuelo> tramos = new ArrayList<>();
    String origen = planificacion.getOrigen().trim();

    for (Destino destino : planificacion.getDestinos()) {
      String nombreDestino = destino.getNombre().trim();
      TramoVuelo tramo = new TramoVuelo(origen, nombreDestino, destino.getFechaInicio());
      tramo.setVuelos(
        this.proveedorVuelos.buscar(
            origen,
            nombreDestino,
            destino.getFechaInicio(),
            planificacion.getAdultos(),
            planificacion.getNinos()
          )
      );
      tramos.add(tramo);
      origen = nombreDestino;
    }

    return tramos;
  }

  @Override
  public List<Vuelo> seleccionarVuelos(List<TramoVuelo> tramos, List<Integer> indicesElegidos) {
    List<Vuelo> seleccionados = new ArrayList<>();

    for (int posicion = 0; posicion < tramos.size(); posicion++) {
      TramoVuelo tramo = tramos.get(posicion);
      if (!tramo.getVuelos().isEmpty()) {
        seleccionados.add(elegirVuelo(tramo, indicesElegidos, posicion));
      }
    }

    return seleccionados;
  }

  private Vuelo elegirVuelo(TramoVuelo tramo, List<Integer> indicesElegidos, int posicion) {
    Integer indice = null;
    if (indicesElegidos != null && posicion < indicesElegidos.size()) {
      indice = indicesElegidos.get(posicion);
    }

    if (indice == null || indice < 0 || indice >= tramo.getVuelos().size()) {
      throw new SeleccionVueloInvalidaException(
        "Elegí un vuelo para el tramo " + tramo.getOrigen() + " → " + tramo.getDestino()
      );
    }

    return tramo.getVuelos().get(indice);
  }

  private void validarPlanificacion(Planificacion planificacion) {
    if (planificacion == null) {
      throw new PlanificacionInvalidaException("La planificación es obligatoria");
    }
    if (planificacion.getOrigen() == null || planificacion.getOrigen().isBlank()) {
      throw new PlanificacionInvalidaException("Debe ingresar la ciudad de origen");
    }
    if (planificacion.getDestinos() == null || planificacion.getDestinos().isEmpty()) {
      throw new PlanificacionInvalidaException("Debe ingresar al menos un destino");
    }
  }
}
