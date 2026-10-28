package com.tallerwebi.presentacion;

import com.fasterxml.jackson.databind.JsonNode;
import com.tallerwebi.dominio.EstadoReserva;
import com.tallerwebi.dominio.ServicioMercadoPago;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ControladorPago {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControladorPago.class);
  private static final String TIPO_NOTIFICACION_ORDEN = "order";
  private final ServicioMercadoPago servicioMercadoPago;

  @Autowired
  public ControladorPago(ServicioMercadoPago servicioMercadoPago) {
    this.servicioMercadoPago = servicioMercadoPago;
  }

  @GetMapping("/pago/resultado")
  public String verResultado(
    @RequestParam("reserva") Long idReserva,
    HttpSession sesion,
    RedirectAttributes redirectAttributes
  ) {
    Usuario usuario = (Usuario) sesion.getAttribute("usuarioLogueado");
    if (usuario == null) {
      return "redirect:/login";
    }
    try {
      EstadoReserva estado = servicioMercadoPago.sincronizarPorReserva(idReserva, usuario);
      if (estado == EstadoReserva.PAGADA) {
        redirectAttributes.addFlashAttribute("exito", "¡Pago confirmado!");
      } else {
        redirectAttributes.addFlashAttribute(
          "error",
          "Tu pago todavía no figura acreditado. Si ya pagaste, se actualiza en unos minutos."
        );
      }
    } catch (RuntimeException e) {
      LOGGER.error("Error al verificar el pago", e);
      redirectAttributes.addFlashAttribute("error", "No pudimos verificar el pago.");
    }
    return "redirect:/mis-reservas";
  }

  @PostMapping("/pago/webhook")
  @ResponseBody
  public ResponseEntity<Void> recibirWebhook(@RequestBody JsonNode notificacion) {
    if (TIPO_NOTIFICACION_ORDEN.equals(notificacion.path("type").asText())) {
      servicioMercadoPago.sincronizarPorOrden(notificacion.path("data").path("id").asText());
    }
    return ResponseEntity.ok().build();
  }
}
