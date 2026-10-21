package com.tallerwebi.presentacion;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Destino;
import com.tallerwebi.dominio.OpcionDestino;
import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ServicioItinerario;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

public class ControladorItinerarioTest {

  private ServicioItinerario servicioItinerario;
  private ControladorItinerario controladorItinerario;
  private HttpSession sesion;
  private Planificacion planificacion;
  private ModelAndView resultado;

  @BeforeEach
  public void inicializar() {
    this.servicioItinerario = mock(ServicioItinerario.class);
    this.controladorItinerario = new ControladorItinerario(this.servicioItinerario);
    this.sesion = mock(HttpSession.class);
  }

  @Test
  public void mostrarItinerarioConPlanificacionValida() {
    givenTengoUnaPlanificacionValida();
    givenElServicioGeneraUnaOpcionParaBuenosAires();

    whenMuestroElItinerario();

    thenSeMuestraElItinerarioConLaPlanificacion();
    thenSeSolicitaElItinerarioAlServicio();
  }

  @Test
  public void redirigirCuandoNoExistePlanificacionEnSesion() {
    givenNoExisteUnaPlanificacionEnLaSesion();

    whenMuestroElItinerario();

    thenSeRedirigeALaPlanificacion();
    thenNoSeConsultaElServicioDeItinerario();
  }

  private void givenTengoUnaPlanificacionValida() {
    this.planificacion = new Planificacion();
    this.planificacion.agregarDestino(
            new Destino("Buenos Aires", "2026-12-10", 5)
    );

    when(this.sesion.getAttribute("planificacion"))
            .thenReturn(this.planificacion);
  }

  private void givenElServicioGeneraUnaOpcionParaBuenosAires() {
    OpcionDestino opcion = new OpcionDestino(
            "Buenos Aires",
            "Argentina",
            List.of(),
            List.of()
    );

    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
            .thenReturn(opcion);
  }

  private void givenNoExisteUnaPlanificacionEnLaSesion() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(null);
  }

  private void whenMuestroElItinerario() {
    this.resultado = this.controladorItinerario.mostrarItinerario(this.sesion);
  }

  private void thenSeMuestraElItinerarioConLaPlanificacion() {
    assertThat(this.resultado.getViewName(), equalTo("itinerario"));
    assertThat(
            (List<?>) this.resultado.getModel().get("opcionesItinerario"),
            hasSize(1)
    );
    assertThat(
            this.resultado.getModel().get("planificacion"),
            equalTo(this.planificacion)
    );
  }

  private void thenSeSolicitaElItinerarioAlServicio() {
    verify(this.servicioItinerario).generarItinerario("Buenos Aires");
  }

  private void thenSeRedirigeALaPlanificacion() {
    assertThat(
            this.resultado.getViewName(),
            equalTo("redirect:/planificacion")
    );
  }

  private void thenNoSeConsultaElServicioDeItinerario() {
    verifyNoInteractions(this.servicioItinerario);
  }
}
