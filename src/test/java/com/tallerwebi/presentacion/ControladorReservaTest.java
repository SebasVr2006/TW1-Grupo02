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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControladorReservaTest {

  private ServicioReserva servicioReserva;
  private ControladorReserva controladorReserva;
  private HttpSession sesion;
  private RedirectAttributes redirectAttributes;

  private Usuario usuario;
  private Planificacion planificacion;
  private SeleccionItinerario seleccion;

  @BeforeEach
  public void init() {
    this.servicioReserva = mock(ServicioReserva.class);
    this.controladorReserva = new ControladorReserva(this.servicioReserva);
    this.sesion = mock(HttpSession.class);
    this.redirectAttributes = mock(RedirectAttributes.class);

    this.usuario = new Usuario();
    this.planificacion = new Planificacion();
    this.seleccion = new SeleccionItinerario();
  }

  @Test
  public void deberiaConfirmarLaReservaYRedirigirAMisReservas() {
    this.dadoQueLaSesionTieneTodo();

    ModelAndView resultado =
      this.controladorReserva.confirmarReserva(this.sesion, this.redirectAttributes);

    assertThat(resultado.getViewName(), is(equalTo("redirect:/mis-reservas")));
    verify(this.servicioReserva).confirmarReserva(this.usuario, this.planificacion, this.seleccion);
  }

  @Test
  public void deberiaLimpiarElItinerarioDeLaSesionLuegoDeConfirmar() {
    this.dadoQueLaSesionTieneTodo();

    this.controladorReserva.confirmarReserva(this.sesion, this.redirectAttributes);

    verify(this.sesion).removeAttribute("planificacion");
    verify(this.sesion).removeAttribute("seleccionItinerario");
    verify(this.sesion).removeAttribute("resumenItinerario");
  }

  @Test
  public void deberiaRedirigirALoginSiNoHayUsuarioLogueado() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
    when(this.sesion.getAttribute("seleccionItinerario")).thenReturn(this.seleccion);

    ModelAndView resultado =
      this.controladorReserva.confirmarReserva(this.sesion, this.redirectAttributes);

    assertThat(resultado.getViewName(), is(equalTo("redirect:/login")));
    verifyNoInteractions(this.servicioReserva);
  }

  @Test
  public void deberiaRedirigirAPlanificacionSiNoHayItinerarioEnSesion() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);

    ModelAndView resultado =
      this.controladorReserva.confirmarReserva(this.sesion, this.redirectAttributes);

    assertThat(resultado.getViewName(), is(equalTo("redirect:/planificacion")));
    verifyNoInteractions(this.servicioReserva);
  }

  @Test
  public void deberiaVolverAlResumenConElErrorSiLaReservaEsInvalida() {
    this.dadoQueLaSesionTieneTodo();
    doThrow(new ReservaInvalidaException("Selección inválida"))
      .when(this.servicioReserva)
      .confirmarReserva(this.usuario, this.planificacion, this.seleccion);

    ModelAndView resultado =
      this.controladorReserva.confirmarReserva(this.sesion, this.redirectAttributes);

    assertThat(resultado.getViewName(), is(equalTo("redirect:/resumen-itinerario")));
    verify(this.redirectAttributes).addFlashAttribute("error", "Selección inválida");
    verify(this.sesion, never()).removeAttribute("planificacion");
  }

  @Test
  public void deberiaMostrarLasReservasDelUsuario() {
    List<Reserva> reservas = List.of(new Reserva());
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);
    when(this.servicioReserva.obtenerReservasDelUsuario(this.usuario)).thenReturn(reservas);

    ModelAndView resultado = this.controladorReserva.verMisReservas(this.sesion);

    assertThat(resultado.getViewName(), is(equalTo("mis-reservas")));
    assertThat(resultado.getModel().get("reservas"), is(equalTo(reservas)));
  }

  @Test
  public void deberiaRedirigirALoginAlVerReservasSinUsuarioLogueado() {
    ModelAndView resultado = this.controladorReserva.verMisReservas(this.sesion);

    assertThat(resultado.getViewName(), is(equalTo("redirect:/login")));
    verifyNoInteractions(this.servicioReserva);
  }

  private void dadoQueLaSesionTieneTodo() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
    when(this.sesion.getAttribute("seleccionItinerario")).thenReturn(this.seleccion);
  }
}
