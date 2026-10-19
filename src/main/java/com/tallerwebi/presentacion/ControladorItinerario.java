package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Destino;
import com.tallerwebi.dominio.OpcionDestino;
import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.SeleccionDestino;
import com.tallerwebi.dominio.SeleccionItinerario;
import com.tallerwebi.dominio.ServicioItinerario;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorItinerario {

  private static final String CLAVE_PLANIFICACION = "planificacion";
  private static final String VISTA_ITINERARIO = "itinerario";
  private static final String CLAVE_OPCIONES_ITINERARIO = "opcionesItinerario";
  private static final String CLAVE_SELECCION_ITINERARIO = "seleccionItinerario";

  private final ServicioItinerario servicioItinerario;

  @Autowired
  public ControladorItinerario(ServicioItinerario servicioItinerario) {
    this.servicioItinerario = servicioItinerario;
  }

  @GetMapping("/itinerario")
  public ModelAndView mostrarItinerario(HttpSession sesion) {
    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);

    if (
      planificacion == null ||
      planificacion.getDestinos() == null ||
      planificacion.getDestinos().isEmpty()
    ) {
      return new ModelAndView("redirect:/planificacion");
    }

    List<OpcionDestino> opcionesItinerario = new ArrayList<>();
    SeleccionItinerario seleccionItinerario = new SeleccionItinerario();

    for (Destino destino : planificacion.getDestinos()) {
      if (destino == null) {
        continue;
      }

      OpcionDestino opcion = this.servicioItinerario.generarItinerario(destino.getNombre());

      if (opcion != null) {
        opcionesItinerario.add(opcion);

        SeleccionDestino seleccion = new SeleccionDestino();
        seleccion.setNombreDestino(opcion.getCiudad());
        seleccionItinerario.getDestinosSeleccionados().add(seleccion);
      }
    }

    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_PLANIFICACION, planificacion);
    modelo.put(CLAVE_OPCIONES_ITINERARIO, opcionesItinerario);
    modelo.put(CLAVE_SELECCION_ITINERARIO, seleccionItinerario);

    return new ModelAndView(VISTA_ITINERARIO, modelo);
  }

  @PostMapping("/itinerario")
  public ModelAndView procesarSeleccionItinerario(
    @ModelAttribute SeleccionItinerario seleccionItinerario,
    HttpSession sesion
  ) {
    if (
      seleccionItinerario.getDestinosSeleccionados() == null ||
      seleccionItinerario.getDestinosSeleccionados().isEmpty()
    ) {
      return new ModelAndView("redirect:/itinerario");
    }

    sesion.setAttribute(CLAVE_SELECCION_ITINERARIO, seleccionItinerario);

    return new ModelAndView("redirect:/resumen-itinerario");
  }
}
