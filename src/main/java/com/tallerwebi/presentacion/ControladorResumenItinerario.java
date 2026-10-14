package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ResumenItinerario;
import com.tallerwebi.dominio.SeleccionItinerario;
import com.tallerwebi.dominio.ServicioResumenItinerario;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorResumenItinerario {

  private static final String CLAVE_PLANIFICACION = "planificacion";
  private static final String CLAVE_SELECCION_ITINERARIO = "seleccionItinerario";
  private static final String CLAVE_RESUMEN_ITINERARIO = "resumenItinerario";
  private static final String VISTA_RESUMEN_ITINERARIO = "resumen-itinerario";

  private final ServicioResumenItinerario servicioResumenItinerario;

  @Autowired
  public ControladorResumenItinerario(ServicioResumenItinerario servicioResumenItinerario) {
    this.servicioResumenItinerario = servicioResumenItinerario;
  }

  @GetMapping("/resumen-itinerario")
  public ModelAndView mostrarResumenItinerario(HttpSession sesion) {
    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);

    SeleccionItinerario seleccionItinerario = (SeleccionItinerario) sesion.getAttribute(
      CLAVE_SELECCION_ITINERARIO
    );

    if (planificacion == null || seleccionItinerario == null) {
      return new ModelAndView("redirect:/planificacion");
    }

    ResumenItinerario resumen =
      this.servicioResumenItinerario.generarResumen(seleccionItinerario, planificacion);

    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_RESUMEN_ITINERARIO, resumen);

    return new ModelAndView(VISTA_RESUMEN_ITINERARIO, modelo);
  }
}
