package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioRecomendacionesTest {

  private static final Long ID_USUARIO = 1L;

  private RepositorioItinerario repositorioItinerarioMock;
  private RepositorioReserva repositorioReservaMock;
  private ServicioRecomendaciones servicioRecomendaciones;
  private Planificacion planificacionActual;
  private List<RecomendacionDestino> recomendaciones;

  @BeforeEach
  public void init() {
    this.repositorioItinerarioMock = mock(RepositorioItinerario.class);
    this.repositorioReservaMock = mock(RepositorioReserva.class);
    this.servicioRecomendaciones =
      new ServicioRecomendacionesImpl(this.repositorioItinerarioMock, this.repositorioReservaMock);
    this.planificacionActual = null;

    when(this.repositorioItinerarioMock.buscarTodosLosDestinos())
      .thenReturn(
        List.of(
          new DestinoDisponible("Madrid", "España"),
          new DestinoDisponible("Barcelona", "España"),
          new DestinoDisponible("Roma", "Italia"),
          new DestinoDisponible("Lisboa", "Portugal"),
          new DestinoDisponible("París", "Francia"),
          new DestinoDisponible("Buenos Aires", "Argentina"),
          new DestinoDisponible("Tokio", "Japón")
        )
      );
    when(this.repositorioReservaMock.buscarPorUsuario(ID_USUARIO)).thenReturn(List.of());
  }

  @Test
  public void sinViajesNiPlanificacionNoDeberiaRecomendarNada() {
    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(this.recomendaciones, empty());
  }

  @Test
  public void conUnViajePrevioDeberiaRecomendarPrimeroOtroDestinoDelMismoPais() {
    givenElUsuarioViajoA("Madrid");

    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(this.recomendaciones.get(0).getCiudad(), equalTo("Barcelona"));
    assertThat(this.recomendaciones.get(0).getMotivo(), containsString("ya viajaste a Madrid"));
  }

  @Test
  public void noDeberiaRecomendarDestinosYaVisitados() {
    givenElUsuarioViajoA("Madrid");

    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(ciudadesRecomendadas(), not(hasItem("Madrid")));
  }

  @Test
  public void conUnaPlanificacionActualDeberiaRecomendarDestinosDeLaMismaRegion() {
    givenLaPlanificacionActualIncluye("París");

    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(ciudadesRecomendadas(), contains("Barcelona", "Lisboa", "Madrid"));
    assertThat(
      this.recomendaciones.get(0).getMotivo(),
      containsString("estás planificando viajar a París")
    );
  }

  @Test
  public void noDeberiaRecomendarMasDeTresDestinos() {
    givenLaPlanificacionActualIncluye("París");

    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(this.recomendaciones, hasSize(3));
  }

  @Test
  public void deberiaReconocerDestinosConFormatoCiudadYPais() {
    givenLaPlanificacionActualIncluye("Madrid, España");

    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(this.recomendaciones.get(0).getCiudad(), equalTo("Barcelona"));
  }

  @Test
  public void deberiaIgnorarDestinosQueNoEstanEnElCatalogo() {
    givenLaPlanificacionActualIncluye("Mar del Plata, Argentina");

    whenPidoRecomendaciones(ID_USUARIO);

    assertThat(this.recomendaciones, empty());
  }

  @Test
  public void sinUsuarioDeberiaRecomendarSoloSegunLaPlanificacionActual() {
    givenLaPlanificacionActualIncluye("Tokio");
    givenElUsuarioViajoA("Madrid");

    whenPidoRecomendaciones(null);

    verify(this.repositorioReservaMock, never()).buscarPorUsuario(any());
    assertThat(this.recomendaciones, empty());
  }

  private void givenElUsuarioViajoA(String ciudad) {
    ReservaDestino destino = new ReservaDestino();
    destino.setNombreDestino(ciudad);
    Reserva reserva = new Reserva();
    reserva.agregarDestino(destino);
    when(this.repositorioReservaMock.buscarPorUsuario(ID_USUARIO)).thenReturn(List.of(reserva));
  }

  private void givenLaPlanificacionActualIncluye(String nombreDestino) {
    this.planificacionActual = new Planificacion();
    this.planificacionActual.agregarDestino(new Destino(nombreDestino, "2026-12-10", 5));
  }

  private void whenPidoRecomendaciones(Long idUsuario) {
    this.recomendaciones =
      this.servicioRecomendaciones.recomendarDestinos(idUsuario, this.planificacionActual);
  }

  private List<String> ciudadesRecomendadas() {
    return this.recomendaciones.stream().map(RecomendacionDestino::getCiudad).toList();
  }
}
