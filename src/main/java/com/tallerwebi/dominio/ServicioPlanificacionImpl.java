package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DestinoInvalidoException;
import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
import org.springframework.stereotype.Service;

@Service("servicioPlanificacion")
public class ServicioPlanificacionImpl implements ServicioPlanificacion {

  private static final int MIN_ADULTOS = 1;

  @Override
  public Planificacion guardarPlanificacion(Planificacion planificacion) {
    validarPlanificacion(planificacion);
    return planificacion;
  }

  private void validarPlanificacion(Planificacion planificacion) {
    if (planificacion == null) {
      throw new PlanificacionInvalidaException("La planificación es obligatoria");
    }

    validarDestinos(planificacion);
    validarAdultos(planificacion);
    validarNinos(planificacion);
    validarAlojamiento(planificacion);
  }

  private void validarDestinos(Planificacion planificacion) {
    if (planificacion.getDestinos() == null || planificacion.getDestinos().isEmpty()) {
      throw new PlanificacionInvalidaException("Debe ingresar al menos un destino");
    }

    for (Destino destino : planificacion.getDestinos()) {
      validarDestino(destino);
    }
  }

  private void validarAdultos(Planificacion planificacion) {
    if (planificacion.getAdultos() < MIN_ADULTOS) {
      throw new PlanificacionInvalidaException("Debe haber al menos un adulto");
    }
  }

  private void validarNinos(Planificacion planificacion) {
    if (planificacion.getNinos() < 0) {
      throw new PlanificacionInvalidaException("La cantidad de niños no puede ser negativa");
    }
  }

  private void validarAlojamiento(Planificacion planificacion) {
    if (planificacion.getAlojamiento() == null || planificacion.getAlojamiento().isBlank()) {
      throw new PlanificacionInvalidaException("Debe seleccionar un tipo de alojamiento");
    }
  }

  private void validarDestino(Destino destino) {
    if (destino == null) {
      throw new DestinoInvalidoException("El destino no puede estar vacío");
    }

    validarNombreDestino(destino);
    validarFechaInicio(destino);
    validarDuracion(destino);
  }

  private void validarNombreDestino(Destino destino) {
    if (destino.getNombre() == null || destino.getNombre().isBlank()) {
      throw new DestinoInvalidoException("El nombre del destino es obligatorio");
    }
  }

  private void validarFechaInicio(Destino destino) {
    if (destino.getFechaInicio() == null || destino.getFechaInicio().isBlank()) {
      throw new DestinoInvalidoException("La fecha de inicio es obligatoria");
    }
  }

  private void validarDuracion(Destino destino) {
    if (destino.getDuracion() <= 0) {
      throw new DestinoInvalidoException("La duración debe ser mayor que cero");
    }
  }
}
