package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.SeleccionVuelos;
import com.tallerwebi.dominio.ServicioVuelos;
import com.tallerwebi.dominio.TramoVuelo;
import com.tallerwebi.dominio.Vuelo;
import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
import com.tallerwebi.dominio.excepcion.SeleccionVueloInvalidaException;
import com.tallerwebi.dominio.excepcion.VuelosNoDisponiblesException;
import jakarta.servlet.http.HttpSession;
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
public class ControladorVuelos {

  private static final String VISTA_SELECCION_VUELO = "seleccion-vuelo";
  private static final String CLAVE_PLANIFICACION = "planificacion";
  private static final String CLAVE_TRAMOS = "tramosVuelo";
  private static final String CLAVE_SELECCION_VUELOS = "seleccionVuelos";
  private static final String CLAVE_ERROR = "error";

  private final ServicioVuelos servicioVuelos;

  @Autowired
  public ControladorVuelos(ServicioVuelos servicioVuelos) {
    this.servicioVuelos = servicioVuelos;
  }

  @GetMapping("/seleccion-vuelo")
  public ModelAndView mostrarSeleccionVuelo(HttpSession sesion) {
    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);

    if (planificacion == null) {
      return new ModelAndView("redirect:/planificacion");
    }

    List<TramoVuelo> tramos;
    try {
      tramos = this.servicioVuelos.buscarVuelos(planificacion);
    } catch (PlanificacionInvalidaException e) {
      return new ModelAndView("redirect:/planificacion");
    } catch (VuelosNoDisponiblesException e) {
      return new ModelAndView(VISTA_SELECCION_VUELO, CLAVE_ERROR, e.getMessage());
    }

    sesion.setAttribute(CLAVE_TRAMOS, tramos);

    return new ModelAndView(VISTA_SELECCION_VUELO, armarModelo(tramos));
  }

  @PostMapping("/seleccion-vuelo")
  public ModelAndView procesarSeleccionVuelo(
    @ModelAttribute SeleccionVuelos seleccionVuelos,
    HttpSession sesion
  ) {
    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);
    List<TramoVuelo> tramos = obtenerTramos(sesion);

    if (planificacion == null) {
      return new ModelAndView("redirect:/planificacion");
    }
    if (tramos == null) {
      return new ModelAndView("redirect:/seleccion-vuelo");
    }

    List<Vuelo> vuelosSeleccionados;
    try {
      vuelosSeleccionados =
        this.servicioVuelos.seleccionarVuelos(tramos, seleccionVuelos.getIndicesVuelos());
    } catch (SeleccionVueloInvalidaException e) {
      Map<String, Object> modelo = armarModelo(tramos);
      modelo.put(CLAVE_ERROR, e.getMessage());
      return new ModelAndView(VISTA_SELECCION_VUELO, modelo);
    }

    planificacion.setVuelosSeleccionados(vuelosSeleccionados);

    return new ModelAndView("redirect:/resumen-planificacion");
  }

  private Map<String, Object> armarModelo(List<TramoVuelo> tramos) {
    Map<String, Object> modelo = new HashMap<>();
    modelo.put("tramos", tramos);
    modelo.put(CLAVE_SELECCION_VUELOS, new SeleccionVuelos());
    return modelo;
  }

  @SuppressWarnings("unchecked")
  private List<TramoVuelo> obtenerTramos(HttpSession sesion) {
    return (List<TramoVuelo>) sesion.getAttribute(CLAVE_TRAMOS);
  }
}
