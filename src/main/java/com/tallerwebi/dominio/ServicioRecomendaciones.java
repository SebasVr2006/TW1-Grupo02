package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Planificacion;
import java.util.List;

@FunctionalInterface
public interface ServicioRecomendaciones {
  List<RecomendacionDestino> recomendarDestinos(Long idUsuario, Planificacion planificacionActual);
}
