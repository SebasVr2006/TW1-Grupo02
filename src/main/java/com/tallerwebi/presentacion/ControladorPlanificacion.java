package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ServicioPlanificacion;
import com.tallerwebi.dominio.ServicioRecomendaciones;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.DestinoInvalidoException;
import com.tallerwebi.dominio.excepcion.PlanificacionInvalidaException;
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
  private static final String CLAVE_TRAMOS_VUELO = "tramosVuelo";
  private static final String CLAVE_USUARIO_LOGUEADO = "usuarioLogueado";

  private ServicioPlanificacion servicioPlanificacion;
  private ServicioRecomendaciones servicioRecomendaciones;

  @Autowired
  public ControladorPlanificacion(
    ServicioPlanificacion servicioPlanificacion,
    ServicioRecomendaciones servicioRecomendaciones
  ) {
    this.servicioPlanificacion = servicioPlanificacion;
    this.servicioRecomendaciones = servicioRecomendaciones;
  }

  @GetMapping("/planificacion")
  public ModelAndView mostrarPlanificacion(HttpSession session) {
    Usuario usuario = (Usuario) session.getAttribute(CLAVE_USUARIO_LOGUEADO);
    Planificacion planificacionActual = (Planificacion) session.getAttribute(CLAVE_PLANIFICACION);
    Long idUsuario = usuario == null ? null : usuario.getId();

    return new ModelAndView(
      VISTA_PLANIFICACION,
      "recomendaciones",
      this.servicioRecomendaciones.recomendarDestinos(idUsuario, planificacionActual)
    );
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
    Planificacion planificacionValidada;
    try {
      planificacionValidada = this.servicioPlanificacion.guardarPlanificacion(planificacion);
    } catch (PlanificacionInvalidaException | DestinoInvalidoException e) {
      return new ModelAndView(VISTA_PLANIFICACION, "error", e.getMessage());
    }

    HttpSession sesion = request.getSession();
    sesion.setAttribute(CLAVE_PLANIFICACION, planificacionValidada);
    sesion.removeAttribute(CLAVE_TRAMOS_VUELO);

    return new ModelAndView("redirect:/seleccion-vuelo");
  }
}
