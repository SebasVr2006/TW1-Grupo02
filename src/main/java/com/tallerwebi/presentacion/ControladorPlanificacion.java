package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ServicioPlanificacion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPlanificacion {

  private static final String VISTA_PLANIFICACION = "planificacion";
  private static final String CLAVE_PLANIFICACION = "planificacion";

  private ServicioPlanificacion servicioPlanificacion;

  @Autowired
  public ControladorPlanificacion(ServicioPlanificacion servicioPlanificacion) {
    this.servicioPlanificacion = servicioPlanificacion;
  }

  @GetMapping("/planificacion")
  public ModelAndView mostrarPlanificacion() {
    return new ModelAndView(VISTA_PLANIFICACION);
  }

  @GetMapping("/resumen-planificacion")
  public ModelAndView mostrarResumen(HttpServletRequest request) {
    HttpSession sesion = request.getSession();

    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);

    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_PLANIFICACION, planificacion);

    return new ModelAndView("resumen-planificacion", modelo);
  }

  @PostMapping("/planificacion")
  public ModelAndView recibirPlanificacion(
    @ModelAttribute Planificacion planificacion,
    HttpServletRequest request
  ) {
    Planificacion planificacionValidada =
      this.servicioPlanificacion.guardarPlanificacion(planificacion);

    HttpSession sesion = request.getSession();
    sesion.setAttribute(CLAVE_PLANIFICACION, planificacionValidada);

    return new ModelAndView("redirect:/resumen-planificacion");
  }
}
