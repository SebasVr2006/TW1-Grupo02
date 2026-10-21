package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.ConfirmacionCompraDto;
import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.Reserva;
import com.tallerwebi.dominio.SeleccionItinerario;
import com.tallerwebi.dominio.ServicioReserva;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.ReservaInvalidaException;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorReservaTest {

  private ServicioReserva servicioReserva;
  private ControladorReserva controladorReserva;
  private HttpSession sesion;
  private RedirectAttributes redirectAttributes;
  private Model modelo;

  private Usuario usuario;
  private Planificacion planificacion;
  private SeleccionItinerario seleccion;

  @BeforeEach
  public void init() {
    this.servicioReserva = mock(ServicioReserva.class);
    this.controladorReserva = new ControladorReserva(this.servicioReserva);
    this.sesion = mock(HttpSession.class);
    this.redirectAttributes = mock(RedirectAttributes.class);
    this.modelo = new ExtendedModelMap();

    this.usuario = new Usuario();
    this.planificacion = new Planificacion();
    this.seleccion = new SeleccionItinerario();
  }

  @Test
  public void deberiaMostrarLaConfirmacionDeCompraConSusDatosEnElModelo() {
    this.dadoQueLaSesionTieneTodo();
    ConfirmacionCompraDto confirmacion = mock(ConfirmacionCompraDto.class);
    when(this.servicioReserva.prepararConfirmacion(this.planificacion, this.seleccion))
      .thenReturn(confirmacion);

    String vista =
      this.controladorReserva.verConfirmacionCompra(
          this.sesion,
          this.modelo,
          this.redirectAttributes
        );

    assertThat(vista, is(equalTo("confirmacion-compra")));
    assertThat(this.modelo.getAttribute("confirmacion"), is(equalTo(confirmacion)));
  }

  @Test
  public void deberiaRedirigirALoginAlVerLaConfirmacionSinUsuarioLogueado() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
    when(this.sesion.getAttribute("seleccionItinerario")).thenReturn(this.seleccion);

    String vista =
      this.controladorReserva.verConfirmacionCompra(
          this.sesion,
          this.modelo,
          this.redirectAttributes
        );

    assertThat(vista, is(equalTo("redirect:/login")));
    verifyNoInteractions(this.servicioReserva);
  }

  @Test
  public void deberiaRedirigirAPlanificacionAlVerLaConfirmacionSinItinerario() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);

    String vista =
      this.controladorReserva.verConfirmacionCompra(
          this.sesion,
          this.modelo,
          this.redirectAttributes
        );

    assertThat(vista, is(equalTo("redirect:/planificacion")));
    verifyNoInteractions(this.servicioReserva);
  }

  @Test
  public void deberiaVolverAlResumenSiNoSePuedePrepararLaConfirmacion() {
    this.dadoQueLaSesionTieneTodo();
    doThrow(new ReservaInvalidaException("Selección inválida"))
      .when(this.servicioReserva)
      .prepararConfirmacion(this.planificacion, this.seleccion);

    String vista =
      this.controladorReserva.verConfirmacionCompra(
          this.sesion,
          this.modelo,
          this.redirectAttributes
        );

    assertThat(vista, is(equalTo("redirect:/resumen-itinerario")));
    verify(this.redirectAttributes).addFlashAttribute("error", "Selección inválida");
    assertThat(this.modelo.asMap().isEmpty(), is(true));
  }

  @Test
  public void deberiaConfirmarLaReservaYRedirigirAMisReservas() {
    this.dadoQueLaSesionTieneTodo();

    String vista =
      this.controladorReserva.confirmarReserva(this.sesion, true, this.redirectAttributes);

    assertThat(vista, is(equalTo("redirect:/mis-reservas")));
    verify(this.servicioReserva).confirmarReserva(this.usuario, this.planificacion, this.seleccion);
  }

  @Test
  public void deberiaLimpiarElItinerarioDeLaSesionLuegoDeConfirmar() {
    this.dadoQueLaSesionTieneTodo();

    this.controladorReserva.confirmarReserva(this.sesion, true, this.redirectAttributes);

    verify(this.sesion).removeAttribute("planificacion");
    verify(this.sesion).removeAttribute("seleccionItinerario");
    verify(this.sesion).removeAttribute("resumenItinerario");
  }

  @Test
  public void noDeberiaConfirmarSiNoSeAceptanLosTerminos() {
    this.dadoQueLaSesionTieneTodo();

    String vista =
      this.controladorReserva.confirmarReserva(this.sesion, false, this.redirectAttributes);

    assertThat(vista, is(equalTo("redirect:/confirmacion-compra")));
    verify(this.redirectAttributes)
      .addFlashAttribute("error", "Tenés que aceptar los términos y condiciones para continuar.");
    verifyNoInteractions(this.servicioReserva);
    verify(this.sesion, never()).removeAttribute("planificacion");
  }

  @Test
  public void deberiaRedirigirALoginSiNoHayUsuarioLogueadoAlConfirmar() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
    when(this.sesion.getAttribute("seleccionItinerario")).thenReturn(this.seleccion);

    String vista =
      this.controladorReserva.confirmarReserva(this.sesion, true, this.redirectAttributes);

    assertThat(vista, is(equalTo("redirect:/login")));
    verifyNoInteractions(this.servicioReserva);
  }

  @Test
  public void deberiaRedirigirAPlanificacionSiNoHayItinerarioEnSesion() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);

    String vista =
      this.controladorReserva.confirmarReserva(this.sesion, true, this.redirectAttributes);

    assertThat(vista, is(equalTo("redirect:/planificacion")));
    verifyNoInteractions(this.servicioReserva);
  }

  @Test
  public void deberiaVolverAlResumenConElErrorSiLaReservaEsInvalida() {
    this.dadoQueLaSesionTieneTodo();
    doThrow(new ReservaInvalidaException("Selección inválida"))
      .when(this.servicioReserva)
      .confirmarReserva(this.usuario, this.planificacion, this.seleccion);

    String vista =
      this.controladorReserva.confirmarReserva(this.sesion, true, this.redirectAttributes);

    assertThat(vista, is(equalTo("redirect:/resumen-itinerario")));
    verify(this.redirectAttributes).addFlashAttribute("error", "Selección inválida");
    verify(this.sesion, never()).removeAttribute("planificacion");
  }

  @Test
  public void deberiaMostrarLasReservasDelUsuarioEnElModelo() {
    List<Reserva> reservas = List.of(new Reserva());
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);
    when(this.servicioReserva.obtenerReservasDelUsuario(this.usuario)).thenReturn(reservas);

    String vista = this.controladorReserva.verMisReservas(this.sesion, this.modelo);

    assertThat(vista, is(equalTo("mis-reservas")));
    assertThat(this.modelo.getAttribute("reservas"), is(equalTo(reservas)));
  }

  @Test
  public void deberiaRedirigirALoginAlVerReservasSinUsuarioLogueado() {
    String vista = this.controladorReserva.verMisReservas(this.sesion, this.modelo);

    assertThat(vista, is(equalTo("redirect:/login")));
    assertThat(this.modelo.asMap().isEmpty(), is(true));
    verifyNoInteractions(this.servicioReserva);
  }

  private void dadoQueLaSesionTieneTodo() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
    when(this.sesion.getAttribute("seleccionItinerario")).thenReturn(this.seleccion);
  }
}
