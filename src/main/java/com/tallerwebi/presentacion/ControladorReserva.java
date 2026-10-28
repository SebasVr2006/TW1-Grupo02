package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ConfirmacionCompraDto;
import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.SeleccionItinerario;
import com.tallerwebi.dominio.ServicioMercadoPago;
import com.tallerwebi.dominio.ServicioReserva;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ReservaInvalidaException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorReserva {

  private static final String CLAVE_USUARIO = "usuarioLogueado";
  private static final String CLAVE_PLANIFICACION = "planificacion";
  private static final String CLAVE_SELECCION_ITINERARIO = "seleccionItinerario";
  private static final String CLAVE_RESUMEN_ITINERARIO = "resumenItinerario";
  private static final String CLAVE_RESERVAS = "reservas";
  private static final String CLAVE_ERROR = "error";
  private static final String CLAVE_CONFIRMACION = "confirmacion";
  private static final String MENSAJE_TERMINOS =
    "Tenés que aceptar los términos y condiciones para continuar.";
  private static final String VISTA_CONFIRMACION_COMPRA = "confirmacion-compra";
  private static final String VISTA_MIS_RESERVAS = "mis-reservas";
  private static final String REDIRECT_LOGIN = "redirect:/login";
  private static final String REDIRECT_PLANIFICACION = "redirect:/planificacion";
  private static final String REDIRECT_RESUMEN_ITINERARIO = "redirect:/resumen-itinerario";
  private static final String REDIRECT_MIS_RESERVAS = "redirect:/mis-reservas";
  private static final String REDIRECT_CONFIRMACION_COMPRA = "redirect:/confirmacion-compra";
  private static final Logger LOGGER = LoggerFactory.getLogger(ControladorReserva.class);

  private final ServicioReserva servicioReserva;
  private final ServicioMercadoPago servicioMercadoPago;

  @Autowired
  public ControladorReserva(
    ServicioReserva servicioReserva,
    ServicioMercadoPago servicioMercadoPago
  ) {
    this.servicioReserva = servicioReserva;
    this.servicioMercadoPago = servicioMercadoPago;
  }

  @GetMapping("/confirmacion-compra")
  public String verConfirmacionCompra(
    HttpSession sesion,
    Model modelo,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) sesion.getAttribute(CLAVE_USUARIO);
    if (usuario == null) {
      return REDIRECT_LOGIN;
    }

    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);
    SeleccionItinerario seleccion = (SeleccionItinerario) sesion.getAttribute(
      CLAVE_SELECCION_ITINERARIO
    );
    if (planificacion == null || seleccion == null) {
      return REDIRECT_PLANIFICACION;
    }

    try {
      ConfirmacionCompraDto confirmacion =
        this.servicioReserva.prepararConfirmacion(planificacion, seleccion);
      modelo.addAttribute(CLAVE_CONFIRMACION, confirmacion);
    } catch (ReservaInvalidaException e) {
      redirectAttributes.addFlashAttribute(CLAVE_ERROR, e.getMessage());
      return REDIRECT_RESUMEN_ITINERARIO;
    }
    return VISTA_CONFIRMACION_COMPRA;
  }

  @PostMapping("/reservas/confirmar")
  public String confirmarReserva(
    HttpSession sesion,
    @RequestParam(name = "aceptaTerminos", defaultValue = "false") boolean aceptaTerminos,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) sesion.getAttribute(CLAVE_USUARIO);
    if (usuario == null) {
      return REDIRECT_LOGIN;
    }

    Planificacion planificacion = (Planificacion) sesion.getAttribute(CLAVE_PLANIFICACION);
    SeleccionItinerario seleccion = (SeleccionItinerario) sesion.getAttribute(
      CLAVE_SELECCION_ITINERARIO
    );

    if (planificacion == null || seleccion == null) {
      return REDIRECT_PLANIFICACION;
    }

    if (!aceptaTerminos) {
      redirectAttributes.addFlashAttribute(CLAVE_ERROR, MENSAJE_TERMINOS);
      return REDIRECT_CONFIRMACION_COMPRA;
    }

    Reserva reserva;
    try {
      reserva = this.servicioReserva.confirmarReserva(usuario.getId(), planificacion, seleccion);
    } catch (ReservaInvalidaException e) {
      redirectAttributes.addFlashAttribute(CLAVE_ERROR, e.getMessage());
      return REDIRECT_RESUMEN_ITINERARIO;
    }

    sesion.removeAttribute(CLAVE_PLANIFICACION);
    sesion.removeAttribute(CLAVE_SELECCION_ITINERARIO);
    sesion.removeAttribute(CLAVE_RESUMEN_ITINERARIO);

    try {
      String checkoutUrl = this.servicioMercadoPago.crearCheckout(reserva, usuario);
      return "redirect:" + checkoutUrl;
    } catch (RuntimeException e) {
      LOGGER.error("Error al iniciar el checkout de Mercado Pago", e);
      redirectAttributes.addFlashAttribute(
        CLAVE_ERROR,
        "La reserva se creó, pero falló el inicio del pago: " + e.getMessage()
      );
      return REDIRECT_MIS_RESERVAS;
    }
  }

  @GetMapping("/mis-reservas")
  public String verMisReservas(HttpSession sesion, Model modelo) {
    Usuario usuario = (Usuario) sesion.getAttribute(CLAVE_USUARIO);
    if (usuario == null) {
      return REDIRECT_LOGIN;
    }

    modelo.addAttribute(
      CLAVE_RESERVAS,
      this.servicioReserva.obtenerReservasDelUsuario(usuario.getId())
    );
    return VISTA_MIS_RESERVAS;
  }
}
