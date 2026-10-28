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

  private static final Long ID_USUARIO = 5L;
  private static final Long ID_OTRO_USUARIO = 6L;

  private ServicioReserva servicioReserva;
  private ControladorReserva controladorReserva;
  private HttpSession sesion;
  private RedirectAttributes redirectAttributes;
  private Model modelo;

  private Usuario usuario;
  private Planificacion planificacion;
  private SeleccionItinerario seleccion;
  private ConfirmacionCompraDto confirmacion;
  private List<Reserva> reservas;
  private String resultado;

  @BeforeEach
  public void inicializar() {
    this.servicioReserva = mock(ServicioReserva.class);
    this.controladorReserva = new ControladorReserva(this.servicioReserva);
    this.sesion = mock(HttpSession.class);
    this.redirectAttributes = mock(RedirectAttributes.class);
    this.modelo = new ExtendedModelMap();

    this.usuario = new Usuario();
    this.usuario.setId(ID_USUARIO);
    this.planificacion = new Planificacion();
    this.seleccion = new SeleccionItinerario();
  }

  @Test
  public void deberiaMostrarLaConfirmacionDeCompraConSusDatosEnElModelo() {
    givenLaSesionTieneTodo();
    givenElServicioPreparaLaConfirmacion();

    whenConsultoLaConfirmacionDeCompra();

    thenSeMuestraLaConfirmacionDeCompra();
  }

  @Test
  public void deberiaRedirigirALoginAlVerLaConfirmacionSinUsuarioLogueado() {
    givenLaSesionTienePlanificacionYSeleccion();

    whenConsultoLaConfirmacionDeCompra();

    thenSeRedirigeAlLogin();
    thenNoSeConsultaElServicio();
  }

  @Test
  public void deberiaRedirigirAPlanificacionAlVerLaConfirmacionSinItinerario() {
    givenHayUnUsuarioLogueado();

    whenConsultoLaConfirmacionDeCompra();

    thenSeRedirigeAPlanificacion();
    thenNoSeConsultaElServicio();
  }

  @Test
  public void deberiaVolverAlResumenSiNoSePuedePrepararLaConfirmacion() {
    givenLaSesionTieneTodo();
    givenLaConfirmacionEsInvalida();

    whenConsultoLaConfirmacionDeCompra();

    thenSeRedirigeAlResumenDelItinerario();
    thenSeMuestraElErrorDeReservaInvalida();
    thenElModeloPermaneceVacio();
  }

  @Test
  public void deberiaConfirmarLaReservaYRedirigirAMisReservas() {
    givenLaSesionTieneTodo();

    whenConfirmoLaReservaAceptandoLosTerminos();

    thenSeRedirigeAMisReservas();
    thenSeConfirmaLaReserva();
  }

  @Test
  public void deberiaLimpiarElItinerarioDeLaSesionLuegoDeConfirmar() {
    givenLaSesionTieneTodo();

    whenConfirmoLaReservaAceptandoLosTerminos();

    thenSeLimpiaElItinerarioDeLaSesion();
  }

  @Test
  public void noDeberiaConfirmarSiNoSeAceptanLosTerminos() {
    givenLaSesionTieneTodo();

    whenConfirmoLaReservaSinAceptarLosTerminos();

    thenSeRedirigeALaConfirmacionDeCompra();
    thenSeMuestraElErrorDeTerminos();
    thenNoSeConsultaElServicio();
    thenNoSeEliminaLaPlanificacionDeLaSesion();
  }

  @Test
  public void deberiaRedirigirALoginSiNoHayUsuarioLogueadoAlConfirmar() {
    givenLaSesionTienePlanificacionYSeleccion();

    whenConfirmoLaReservaAceptandoLosTerminos();

    thenSeRedirigeAlLogin();
    thenNoSeConsultaElServicio();
  }

  @Test
  public void deberiaRedirigirAPlanificacionSiNoHayItinerarioEnSesion() {
    givenHayUnUsuarioLogueado();

    whenConfirmoLaReservaAceptandoLosTerminos();

    thenSeRedirigeAPlanificacion();
    thenNoSeConsultaElServicio();
  }

  @Test
  public void deberiaVolverAlResumenConElErrorSiLaReservaEsInvalida() {
    givenLaSesionTieneTodo();
    givenLaReservaEsInvalida();

    whenConfirmoLaReservaAceptandoLosTerminos();

    thenSeRedirigeAlResumenDelItinerario();
    thenSeMuestraElErrorDeReservaInvalida();
    thenNoSeEliminaLaPlanificacionDeLaSesion();
  }

  @Test
  public void deberiaMostrarLasReservasDelUsuarioEnElModelo() {
    givenHayUnUsuarioLogueado();
    givenElUsuarioTieneReservas();

    whenConsultoMisReservas();

    thenSeMuestraLaVistaMisReservas();
    thenSeIncluyenLasReservasEnElModelo();
  }

  @Test
  public void deberiaConsultarLasReservasConElIdDelUsuarioDeLaSesionActual() {
    givenHayUnUsuarioLogueado();
    givenElUsuarioTieneReservas();

    whenConsultoMisReservas();

    thenSeConsultanLasReservasPorElIdDelUsuario();
  }

  @Test
  public void noDeberiaMostrarReservasAjenasCuandoCambiaElUsuarioDeLaSesion() {
    givenHayUnUsuarioLogueado();
    givenElUsuarioTieneReservas();
    givenInicioSesionOtroUsuarioSinReservas();

    whenConsultoMisReservas();

    thenSeMuestraLaVistaMisReservas();
    thenNoSeMuestranLasReservasDelUsuarioAnterior();
  }

  @Test
  public void deberiaRedirigirALoginAlVerReservasSinUsuarioLogueado() {
    whenConsultoMisReservas();

    thenSeRedirigeAlLogin();
    thenElModeloPermaneceVacio();
    thenNoSeConsultaElServicio();
  }

  private void givenLaSesionTieneTodo() {
    givenHayUnUsuarioLogueado();
    givenLaSesionTienePlanificacionYSeleccion();
  }

  private void givenHayUnUsuarioLogueado() {
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(this.usuario);
  }

  private void givenInicioSesionOtroUsuarioSinReservas() {
    Usuario otro = new Usuario();
    otro.setId(ID_OTRO_USUARIO);
    when(this.sesion.getAttribute("usuarioLogueado")).thenReturn(otro);
    when(this.servicioReserva.obtenerReservasDelUsuario(ID_OTRO_USUARIO)).thenReturn(List.of());
  }

  private void givenLaSesionTienePlanificacionYSeleccion() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(this.planificacion);
    when(this.sesion.getAttribute("seleccionItinerario")).thenReturn(this.seleccion);
  }

  private void givenElServicioPreparaLaConfirmacion() {
    this.confirmacion = mock(ConfirmacionCompraDto.class);

    when(this.servicioReserva.prepararConfirmacion(this.planificacion, this.seleccion))
      .thenReturn(this.confirmacion);
  }

  private void givenLaConfirmacionEsInvalida() {
    doThrow(new ReservaInvalidaException("Selección inválida"))
      .when(this.servicioReserva)
      .prepararConfirmacion(this.planificacion, this.seleccion);
  }

  private void givenLaReservaEsInvalida() {
    doThrow(new ReservaInvalidaException("Selección inválida"))
      .when(this.servicioReserva)
      .confirmarReserva(ID_USUARIO, this.planificacion, this.seleccion);
  }

  private void givenElUsuarioTieneReservas() {
    this.reservas = List.of(new Reserva());

    when(this.servicioReserva.obtenerReservasDelUsuario(ID_USUARIO)).thenReturn(this.reservas);
  }

  private void whenConsultoLaConfirmacionDeCompra() {
    this.resultado =
      this.controladorReserva.verConfirmacionCompra(
          this.sesion,
          this.modelo,
          this.redirectAttributes
        );
  }

  private void whenConfirmoLaReservaAceptandoLosTerminos() {
    this.resultado =
      this.controladorReserva.confirmarReserva(this.sesion, true, this.redirectAttributes);
  }

  private void whenConfirmoLaReservaSinAceptarLosTerminos() {
    this.resultado =
      this.controladorReserva.confirmarReserva(this.sesion, false, this.redirectAttributes);
  }

  private void whenConsultoMisReservas() {
    this.resultado = this.controladorReserva.verMisReservas(this.sesion, this.modelo);
  }

  private void thenSeMuestraLaConfirmacionDeCompra() {
    assertThat(this.resultado, is(equalTo("confirmacion-compra")));
    assertThat(this.modelo.getAttribute("confirmacion"), is(equalTo(this.confirmacion)));
  }

  private void thenSeRedirigeAlLogin() {
    assertThat(this.resultado, is(equalTo("redirect:/login")));
  }

  private void thenSeRedirigeAPlanificacion() {
    assertThat(this.resultado, is(equalTo("redirect:/planificacion")));
  }

  private void thenSeRedirigeAlResumenDelItinerario() {
    assertThat(this.resultado, is(equalTo("redirect:/resumen-itinerario")));
  }

  private void thenSeRedirigeAMisReservas() {
    assertThat(this.resultado, is(equalTo("redirect:/mis-reservas")));
  }

  private void thenSeRedirigeALaConfirmacionDeCompra() {
    assertThat(this.resultado, is(equalTo("redirect:/confirmacion-compra")));
  }

  private void thenSeConfirmaLaReserva() {
    verify(this.servicioReserva).confirmarReserva(ID_USUARIO, this.planificacion, this.seleccion);
  }

  private void thenSeLimpiaElItinerarioDeLaSesion() {
    verify(this.sesion).removeAttribute("planificacion");
    verify(this.sesion).removeAttribute("seleccionItinerario");
    verify(this.sesion).removeAttribute("resumenItinerario");
  }

  private void thenSeMuestraElErrorDeTerminos() {
    verify(this.redirectAttributes)
      .addFlashAttribute("error", "Tenés que aceptar los términos y condiciones para continuar.");
  }

  private void thenSeMuestraElErrorDeReservaInvalida() {
    verify(this.redirectAttributes).addFlashAttribute("error", "Selección inválida");
  }

  private void thenNoSeConsultaElServicio() {
    verifyNoInteractions(this.servicioReserva);
  }

  private void thenNoSeEliminaLaPlanificacionDeLaSesion() {
    verify(this.sesion, never()).removeAttribute("planificacion");
  }

  private void thenElModeloPermaneceVacio() {
    assertThat(this.modelo.asMap().isEmpty(), is(true));
  }

  private void thenSeMuestraLaVistaMisReservas() {
    assertThat(this.resultado, is(equalTo("mis-reservas")));
  }

  private void thenSeConsultanLasReservasPorElIdDelUsuario() {
    verify(this.servicioReserva).obtenerReservasDelUsuario(ID_USUARIO);
  }

  private void thenNoSeMuestranLasReservasDelUsuarioAnterior() {
    assertThat(this.modelo.getAttribute("reservas"), is(equalTo(List.of())));
  }

  private void thenSeIncluyenLasReservasEnElModelo() {
    assertThat(this.modelo.getAttribute("reservas"), is(equalTo(this.reservas)));
  }
}
