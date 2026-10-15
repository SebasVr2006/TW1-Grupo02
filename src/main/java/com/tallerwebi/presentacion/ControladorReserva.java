package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.SeleccionItinerario;
import com.tallerwebi.dominio.ServicioReserva;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ReservaInvalidaException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorReserva {

  private static final String CLAVE_USUARIO = "usuarioLogueado";
  private static final String CLAVE_PLANIFICACION = "planificacion";
  private static final String CLAVE_SELECCION_ITINERARIO = "seleccionItinerario";
  private static final String CLAVE_RESUMEN_ITINERARIO = "resumenItinerario";
  private static final String REDIRECT_LOGIN = "redirect:/login";

  private final ServicioReserva servicioReserva;

  @Autowired
  public ControladorReserva(ServicioReserva servicioReserva) {
    this.servicioReserva = servicioReserva;
  }

  @PostMapping("/reservas/confirmar")
  public ModelAndView confirmarReserva(HttpSession sesion, RedirectAttributes redirectAttributes) {
    Usuario usuario = (Usuario) sesion.getAttribute(CLAVE_USUARIO);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);
    SeleccionItinerario seleccion = (SeleccionItinerario) sesion.getAttribute(
      CLAVE_SELECCION_ITINERARIO
    );
    if (planificacion == null || seleccion == null) {
      return new ModelAndView("redirect:/planificacion");
    }

    try {
      this.servicioReserva.confirmarReserva(usuario, planificacion, seleccion);
    } catch (ReservaInvalidaException e) {
      redirectAttributes.addFlashAttribute("error", e.getMessage());
      return new ModelAndView("redirect:/resumen-itinerario");
    }

    sesion.removeAttribute(CLAVE_PLANIFICACION);
    sesion.removeAttribute(CLAVE_SELECCION_ITINERARIO);
    sesion.removeAttribute(CLAVE_RESUMEN_ITINERARIO);
    return new ModelAndView("redirect:/mis-reservas");
  }

  @GetMapping("/mis-reservas")
  public ModelAndView verMisReservas(HttpSession sesion) {
    Usuario usuario = (Usuario) sesion.getAttribute(CLAVE_USUARIO);
    if (usuario == null) {
      return new ModelAndView(REDIRECT_LOGIN);
    }

    ModelAndView modelAndView = new ModelAndView("mis-reservas");
    modelAndView.addObject("reservas", this.servicioReserva.obtenerReservasDelUsuario(usuario));
    return modelAndView;
  }
}
