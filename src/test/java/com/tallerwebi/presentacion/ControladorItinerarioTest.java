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

  @BeforeEach
  public void inicializar() {
    this.servicioItinerario = mock(ServicioItinerario.class);
    this.controladorItinerario = new ControladorItinerario(this.servicioItinerario);
    this.sesion = mock(HttpSession.class);
  }

  @Test
  public void mostrarItinerarioConPlanificacionValida() {
    Planificacion planificacion = new Planificacion();
    planificacion.agregarDestino(new Destino("Buenos Aires", "2026-12-10", 5));

    OpcionDestino opcion = new OpcionDestino("Buenos Aires", "Argentina", List.of(), List.of());

    when(this.sesion.getAttribute("planificacion")).thenReturn(planificacion);
    when(this.servicioItinerario.generarItinerario("Buenos Aires")).thenReturn(opcion);

    ModelAndView resultado = this.controladorItinerario.mostrarItinerario(this.sesion);

    assertThat(resultado.getViewName(), equalTo("itinerario"));
    assertThat((List<?>) resultado.getModel().get("opcionesItinerario"), hasSize(1));
    assertThat(resultado.getModel().get("planificacion"), equalTo(planificacion));

    verify(this.servicioItinerario).generarItinerario("Buenos Aires");
  }

  @Test
  public void redirigirCuandoNoExistePlanificacionEnSesion() {
    when(this.sesion.getAttribute("planificacion")).thenReturn(null);

    ModelAndView resultado = this.controladorItinerario.mostrarItinerario(this.sesion);

    assertThat(resultado.getViewName(), equalTo("redirect:/planificacion"));

    verifyNoInteractions(this.servicioItinerario);
  }
}
