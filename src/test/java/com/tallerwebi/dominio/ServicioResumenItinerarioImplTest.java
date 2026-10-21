package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioResumenItinerarioImplTest {

  private ServicioItinerario servicioItinerario;
  private ServicioResumenItinerarioImpl servicioResumen;
  private Planificacion planificacion;
  private SeleccionItinerario seleccion;
  private ResumenItinerario resumen;

  @BeforeEach
  public void inicializar() {
    this.servicioItinerario = mock(ServicioItinerario.class);
    this.servicioResumen = new ServicioResumenItinerarioImpl(this.servicioItinerario);
  }

  @Test
  public void generarResumenConHotelYActividadesSeleccionadas() {
    givenTengoUnaPlanificacionDeBuenosAiresConCincoDias();
    givenHayOpcionesDeHotelYActividadesParaBuenosAires();
    givenSeleccionoHotelYDosActividades();

    whenGeneroElResumen();

    thenElResumenIncluyeLosCostosDelHotelYLasActividades();
  }

  @Test
  public void generarResumenSinActividadesSeleccionadas() {
    givenTengoUnaPlanificacionDeBuenosAiresConTresDias();
    givenHayOpcionesDeHotelYActividadesParaBuenosAires();
    givenSeleccionoUnHotelSinActividades();

    whenGeneroElResumen();

    thenElResumenIncluyeSoloElCostoDelHotel();
  }

  @Test
  public void generarResumenConVariosDestinos() {
    givenTengoUnaPlanificacionConBuenosAiresYParis();
    givenHayOpcionesParaBuenosAiresYParis();
    givenSeleccionoHotelYActividadesParaAmbosDestinos();

    whenGeneroElResumen();

    thenElResumenIncluyeLosSubtotalesDeAmbosDestinos();
  }

  @Test
  public void rechazarSeleccionNula() {
    givenTengoUnaPlanificacionDeBuenosAiresConDosDias();
    givenLaSeleccionEsNula();

    whenGeneroElResumenYEsperoUnaExcepcion();

  }

  @Test
  public void rechazarPlanificacionNula() {
    givenTengoUnaSeleccionVacia();
    givenLaPlanificacionEsNula();

    whenGeneroElResumenYEsperoUnaExcepcion();

  }

  @Test
  public void rechazarDestinoSinSeleccion() {
    givenTengoUnaPlanificacionDeBuenosAiresConDosDias();
    givenTengoUnaSeleccionVacia();

    whenGeneroElResumenYEsperoUnaExcepcion();

  }

  @Test
  public void rechazarDestinoSinOpcionesDisponibles() {
    givenTengoUnaPlanificacionDeLondresConDosDias();
    givenSeleccionoUnHotelInexistenteEnLondres();
    givenLondresNoTieneOpcionesDisponibles();

    whenGeneroElResumenYEsperoUnaExcepcion();

  }

  @Test
  public void rechazarHotelNoValido() {
    givenTengoUnaPlanificacionDeBuenosAiresConDosDias();
    givenHayOpcionesDeHotelYActividadesParaBuenosAires();
    givenSeleccionoUnHotelInexistente();

    whenGeneroElResumenYEsperoUnaExcepcion();

  }

  @Test
  public void rechazarActividadNoValida() {
    givenTengoUnaPlanificacionDeBuenosAiresConDosDias();
    givenHayOpcionesDeHotelYActividadesParaBuenosAires();
    givenSeleccionoUnaActividadInexistente();

    whenGeneroElResumenYEsperoUnaExcepcion();

  }

  @Test
  public void aceptarListaDeActividadesNula() {
    givenTengoUnaPlanificacionDeBuenosAiresConDosDias();
    givenHayOpcionesDeHotelYActividadesParaBuenosAires();
    givenSeleccionoUnHotelConListaDeActividadesNula();

    whenGeneroElResumen();

    thenElResumenIncluyeElHotelSinActividadesSeleccionadas();
  }

  private void givenTengoUnaPlanificacionDeBuenosAiresConCincoDias() {
    this.planificacion = crearPlanificacion(
            new Destino("Buenos Aires", "2026-12-10", 5)
    );
  }

  private void givenTengoUnaPlanificacionDeBuenosAiresConTresDias() {
    this.planificacion = crearPlanificacion(
            new Destino("Buenos Aires", "2026-12-10", 3)
    );
  }

  private void givenTengoUnaPlanificacionDeBuenosAiresConDosDias() {
    this.planificacion = crearPlanificacion(
            new Destino("Buenos Aires", "2026-12-10", 2)
    );
  }

  private void givenTengoUnaPlanificacionDeLondresConDosDias() {
    this.planificacion = crearPlanificacion(
            new Destino("Londres", "2026-12-10", 2)
    );
  }

  private void givenTengoUnaPlanificacionConBuenosAiresYParis() {
    this.planificacion = crearPlanificacion(
            new Destino("Buenos Aires", "2026-12-10", 2),
            new Destino("Paris", "2026-12-12", 3)
    );
  }

  private void givenHayOpcionesDeHotelYActividadesParaBuenosAires() {
    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
            .thenReturn(crearOpcionBuenosAires());
  }

  private void givenHayOpcionesParaBuenosAiresYParis() {
    givenHayOpcionesDeHotelYActividadesParaBuenosAires();

    when(this.servicioItinerario.generarItinerario("Paris"))
            .thenReturn(
                    new OpcionDestino(
                            "Paris",
                            "Francia",
                            List.of(new Hotel("Hôtel Le Marais", 160.0)),
                            List.of(new Actividad("Crucero por el Sena", 25.0))
                    )
            );
  }

  private void givenSeleccionoHotelYDosActividades() {
    this.seleccion = crearSeleccion(
            "Buenos Aires",
            "Hotel Palermo Soho",
            List.of("Show de tango en San Telmo", "Tour por La Boca y Caminito")
    );
  }

  private void givenSeleccionoUnHotelSinActividades() {
    this.seleccion = crearSeleccion(
            "Buenos Aires",
            "Hotel Recoleta Plaza",
            List.of()
    );
  }

  private void givenSeleccionoHotelYActividadesParaAmbosDestinos() {
    this.seleccion = new SeleccionItinerario();
    this.seleccion.setDestinosSeleccionados(
            List.of(
                    crearSeleccionDestino(
                            "Buenos Aires",
                            "Hotel Palermo Soho",
                            List.of("Show de tango en San Telmo")
                    ),
                    crearSeleccionDestino(
                            "Paris",
                            "Hôtel Le Marais",
                            List.of("Crucero por el Sena")
                    )
            )
    );
  }

  private void givenLaSeleccionEsNula() {
    this.seleccion = null;
  }

  private void givenTengoUnaSeleccionVacia() {
    this.seleccion = new SeleccionItinerario();
  }

  private void givenLaPlanificacionEsNula() {
    this.planificacion = null;
  }

  private void givenSeleccionoUnHotelInexistenteEnLondres() {
    this.seleccion = crearSeleccion(
            "Londres",
            "Hotel inexistente",
            List.of()
    );
  }

  private void givenLondresNoTieneOpcionesDisponibles() {
    when(this.servicioItinerario.generarItinerario("Londres"))
            .thenReturn(null);
  }

  private void givenSeleccionoUnHotelInexistente() {
    this.seleccion = crearSeleccion(
            "Buenos Aires",
            "Hotel inventado",
            List.of()
    );
  }

  private void givenSeleccionoUnaActividadInexistente() {
    this.seleccion = crearSeleccion(
            "Buenos Aires",
            "Hotel Palermo Soho",
            List.of("Actividad inventada")
    );
  }

  private void givenSeleccionoUnHotelConListaDeActividadesNula() {
    SeleccionDestino destino = crearSeleccionDestino(
            "Buenos Aires",
            "Hotel Palermo Soho",
            null
    );

    this.seleccion = new SeleccionItinerario();
    this.seleccion.setDestinosSeleccionados(List.of(destino));
  }

  private void whenGeneroElResumen() {
    this.resumen = this.servicioResumen.generarResumen(
            this.seleccion,
            this.planificacion
    );
  }

  private void whenGeneroElResumenYEsperoUnaExcepcion() {
    assertThrows(
            IllegalArgumentException.class,
            () -> this.servicioResumen.generarResumen(
                    this.seleccion,
                    this.planificacion
            )
    );
  }

  private void thenElResumenIncluyeLosCostosDelHotelYLasActividades() {
    assertEquals(1, this.resumen.getDestinos().size());
    assertEquals(555.0, this.resumen.getTotal());

    DetalleDestinoItinerario detalle = this.resumen.getDestinos().get(0);

    assertEquals("Buenos Aires", detalle.getNombreDestino());
    assertEquals("Argentina", detalle.getNombrePais());
    assertEquals("Hotel Palermo Soho", detalle.getHotelSeleccionado().getNombre());
    assertEquals(450.0, detalle.getCostoHotel());
    assertEquals(105.0, detalle.getCostoActividades());
    assertEquals(555.0, detalle.getSubtotal());
    assertEquals(2, detalle.getActividadesSeleccionadas().size());
  }

  private void thenElResumenIncluyeSoloElCostoDelHotel() {
    DetalleDestinoItinerario detalle = this.resumen.getDestinos().get(0);

    assertEquals(450.0, detalle.getCostoHotel());
    assertEquals(0.0, detalle.getCostoActividades());
    assertEquals(450.0, this.resumen.getTotal());
  }

  private void thenElResumenIncluyeLosSubtotalesDeAmbosDestinos() {
    assertEquals(2, this.resumen.getDestinos().size());
    assertEquals(250.0, this.resumen.getDestinos().get(0).getSubtotal());
    assertEquals(505.0, this.resumen.getDestinos().get(1).getSubtotal());
    assertEquals(755.0, this.resumen.getTotal());
  }

  private void thenElResumenIncluyeElHotelSinActividadesSeleccionadas() {
    assertEquals(1, this.resumen.getDestinos().size());
    assertEquals(180.0, this.resumen.getTotal());
    assertTrue(
            this.resumen.getDestinos().get(0).getActividadesSeleccionadas().isEmpty()
    );
  }

  private Planificacion crearPlanificacion(Destino... destinos) {
    Planificacion planificacion = new Planificacion();

    for (Destino destino : destinos) {
      planificacion.agregarDestino(destino);
    }

    return planificacion;
  }

  private OpcionDestino crearOpcionBuenosAires() {
    return new OpcionDestino(
            "Buenos Aires",
            "Argentina",
            List.of(
                    new Hotel("Hotel Palermo Soho", 90.0),
                    new Hotel("Hotel Recoleta Plaza", 150.0)
            ),
            List.of(
                    new Actividad("Show de tango en San Telmo", 70.0),
                    new Actividad("Tour por La Boca y Caminito", 35.0)
            )
    );
  }

  private SeleccionItinerario crearSeleccion(
          String destino,
          String hotel,
          List<String> actividades
  ) {
    SeleccionItinerario seleccion = new SeleccionItinerario();
    seleccion.setDestinosSeleccionados(
            List.of(crearSeleccionDestino(destino, hotel, actividades))
    );
    return seleccion;
  }

  private SeleccionDestino crearSeleccionDestino(
          String destino,
          String hotel,
          List<String> actividades
  ) {
    SeleccionDestino seleccion = new SeleccionDestino();
    seleccion.setNombreDestino(destino);
    seleccion.setHotelSeleccionado(hotel);
    seleccion.setActividadesSeleccionadas(actividades);
    return seleccion;
  }
}
