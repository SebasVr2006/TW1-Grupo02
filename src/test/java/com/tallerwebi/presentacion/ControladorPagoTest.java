package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tallerwebi.dominio.EstadoReserva;
import com.tallerwebi.dominio.ServicioMercadoPago;
import com.tallerwebi.dominio.Usuario;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorPagoTest {

  private ServicioMercadoPago servicioMercadoPago;
  private ControladorPago controladorPago;
  private HttpSession sesion;
  private RedirectAttributes redirectAttributes;
  private Usuario usuario;

  private String resultado;
  private ResponseEntity<Void> respuesta;

  @BeforeEach
  public void inicializar() {
    this.servicioMercadoPago = mock(ServicioMercadoPago.class);
    this.controladorPago = new ControladorPago(this.servicioMercadoPago);
    this.sesion = mock(HttpSession.class);
    this.redirectAttributes = mock(RedirectAttributes.class);
    this.usuario = new Usuario();
  }

  @Test
  public void deberiaRedirigirALoginSiNoHayUsuarioLogueado() {
    whenVuelvoDelPago();

    assertThat(this.resultado, is(equalTo("redirect:/login")));
    verifyNoInteractions(this.servicioMercadoPago);
  }

  @Test
  public void deberiaMostrarExitoSiLaReservaQuedoPagada() {
    givenHayUnUsuarioLogueado();
    when(this.servicioMercadoPago.sincronizarPorReserva(1L, this.usuario))
      .thenReturn(EstadoReserva.PAGADA);

    whenVuelvoDelPago();

    assertThat(this.resultado, is(equalTo("redirect:/mis-reservas")));
    verify(this.redirectAttributes).addFlashAttribute("exito", "¡Pago confirmado!");
  }

  @Test
  public void deberiaMostrarErrorSiElPagoTodaviaNoEstaAcreditado() {
    givenHayUnUsuarioLogueado();
    when(this.servicioMercadoPago.sincronizarPorReserva(1L, this.usuario))
      .thenReturn(EstadoReserva.PENDIENTE_DE_PAGO);

    whenVuelvoDelPago();

    assertThat(this.resultado, is(equalTo("redirect:/mis-reservas")));
    verify(this.redirectAttributes)
      .addFlashAttribute(
        "error",
        "Tu pago todavía no figura acreditado. Si ya pagaste, se actualiza en unos minutos."
      );
  }

  @Test
  public void deberiaMostrarErrorSiFallaLaVerificacionDelPago() {
    givenHayUnUsuarioLogueado();
    when(this.servicioMercadoPago.sincronizarPorReserva(1L, this.usuario))
      .thenThrow(new IllegalStateException("falló"));

    whenVuelvoDelPago();

    assertThat(this.resultado, is(equalTo("redirect:/mis-reservas")));
    verify(this.redirectAttributes).addFlashAttribute("error", "No pudimos verificar el pago.");
  }

  @Test
  public void deberiaSincronizarLaOrdenAlRecibirUnWebhookDeOrden() {
    whenRecibeUnWebhook("order");

    assertThat(this.respuesta.getStatusCode().value(), is(equalTo(200)));
    verify(this.servicioMercadoPago).sincronizarPorOrden("ORD123");
  }

  @Test
  public void noDeberiaSincronizarAlRecibirUnWebhookDeOtroTipo() {
    whenRecibeUnWebhook("payment");

    assertThat(this.respuesta.getStatusCode().value(), is(equalTo(200)));
    verify(this.servicioMercadoPago, never()).sincronizarPorOrden("ORD123");
  }

  private void givenHayUnUsuarioLogueado() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);
  }

  private void whenVuelvoDelPago() {
    this.resultado = this.controladorPago.verResultado(1L, this.sesion, this.redirectAttributes);
  }

  private void whenRecibeUnWebhook(String tipo) {
    ObjectNode notificacion = new ObjectMapper().createObjectNode();
    notificacion.put("type", tipo);
    notificacion.putObject("data").put("id", "ORD123");

    this.respuesta = this.controladorPago.recibirWebhook(notificacion);
  }
}
