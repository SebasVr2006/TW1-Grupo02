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

  @BeforeEach
  public void inicializar() {
    this.servicioItinerario = mock(ServicioItinerario.class);
    this.servicioResumen = new ServicioResumenItinerarioImpl(this.servicioItinerario);
  }

  @Test
  public void generarResumenConHotelYActividadesSeleccionadas() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 5));

    OpcionDestino opcion = crearOpcionBuenosAires();

    when(this.servicioItinerario.generarItinerario("Buenos Aires")).thenReturn(opcion);

    SeleccionItinerario seleccion = crearSeleccion(
      "Buenos Aires",
      "Hotel Palermo Soho",
      List.of("Show de tango en San Telmo", "Tour por La Boca y Caminito")
    );

    ResumenItinerario resumen = this.servicioResumen.generarResumen(seleccion, planificacion);

    assertEquals(1, resumen.getDestinos().size());
    assertEquals(555.0, resumen.getTotal());

    DetalleDestinoItinerario detalle = resumen.getDestinos().get(0);

    assertEquals("Buenos Aires", detalle.getNombreDestino());
    assertEquals("Argentina", detalle.getNombrePais());
    assertEquals("Hotel Palermo Soho", detalle.getHotelSeleccionado().getNombre());
    assertEquals(450.0, detalle.getCostoHotel());
    assertEquals(105.0, detalle.getCostoActividades());
    assertEquals(555.0, detalle.getSubtotal());
    assertEquals(2, detalle.getActividadesSeleccionadas().size());
  }

  @Test
  public void generarResumenSinActividadesSeleccionadas() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 3));

    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
      .thenReturn(crearOpcionBuenosAires());

    SeleccionItinerario seleccion = crearSeleccion(
      "Buenos Aires",
      "Hotel Recoleta Plaza",
      List.of()
    );

    ResumenItinerario resumen = this.servicioResumen.generarResumen(seleccion, planificacion);

    DetalleDestinoItinerario detalle = resumen.getDestinos().get(0);

    assertEquals(450.0, detalle.getCostoHotel());
    assertEquals(0.0, detalle.getCostoActividades());
    assertEquals(450.0, resumen.getTotal());
  }

  @Test
  public void generarResumenConVariosDestinos() {
    Planificacion planificacion = crearPlanificacion(
      new Destino("Buenos Aires", "2026-12-10", 2),
      new Destino("Paris", "2026-12-12", 3)
    );

    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
      .thenReturn(crearOpcionBuenosAires());

    when(this.servicioItinerario.generarItinerario("Paris"))
      .thenReturn(
        new OpcionDestino(
          "Paris",
          "Francia",
          List.of(new Hotel("Hôtel Le Marais", 160.0)),
          List.of(new Actividad("Crucero por el Sena", 25.0))
        )
      );

    SeleccionItinerario seleccion = new SeleccionItinerario();
    seleccion.setDestinosSeleccionados(
      List.of(
        crearSeleccionDestino(
          "Buenos Aires",
          "Hotel Palermo Soho",
          List.of("Show de tango en San Telmo")
        ),
        crearSeleccionDestino("Paris", "Hôtel Le Marais", List.of("Crucero por el Sena"))
      )
    );

    ResumenItinerario resumen = this.servicioResumen.generarResumen(seleccion, planificacion);

    assertEquals(2, resumen.getDestinos().size());
    assertEquals(250.0, resumen.getDestinos().get(0).getSubtotal());
    assertEquals(505.0, resumen.getDestinos().get(1).getSubtotal());
    assertEquals(755.0, resumen.getTotal());
  }

  @Test
  public void rechazarSeleccionNula() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 2));

    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioResumen.generarResumen(null, planificacion)
    );
  }

  @Test
  public void rechazarPlanificacionNula() {
    SeleccionItinerario seleccion = new SeleccionItinerario();

    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioResumen.generarResumen(seleccion, null)
    );
  }

  @Test
  public void rechazarDestinoSinSeleccion() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 2));

    SeleccionItinerario seleccion = new SeleccionItinerario();

    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioResumen.generarResumen(seleccion, planificacion)
    );
  }

  @Test
  public void rechazarDestinoSinOpcionesDisponibles() {
    Planificacion planificacion = crearPlanificacion(new Destino("Londres", "2026-12-10", 2));

    SeleccionItinerario seleccion = crearSeleccion("Londres", "Hotel inexistente", List.of());

    when(this.servicioItinerario.generarItinerario("Londres")).thenReturn(null);

    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioResumen.generarResumen(seleccion, planificacion)
    );
  }

  @Test
  public void rechazarHotelNoValido() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 2));

    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
      .thenReturn(crearOpcionBuenosAires());

    SeleccionItinerario seleccion = crearSeleccion("Buenos Aires", "Hotel inventado", List.of());

    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioResumen.generarResumen(seleccion, planificacion)
    );
  }

  @Test
  public void rechazarActividadNoValida() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 2));

    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
      .thenReturn(crearOpcionBuenosAires());

    SeleccionItinerario seleccion = crearSeleccion(
      "Buenos Aires",
      "Hotel Palermo Soho",
      List.of("Actividad inventada")
    );

    assertThrows(
      IllegalArgumentException.class,
      () -> this.servicioResumen.generarResumen(seleccion, planificacion)
    );
  }

  @Test
  public void aceptarListaDeActividadesNula() {
    Planificacion planificacion = crearPlanificacion(new Destino("Buenos Aires", "2026-12-10", 2));

    when(this.servicioItinerario.generarItinerario("Buenos Aires"))
      .thenReturn(crearOpcionBuenosAires());

    SeleccionDestino destino = crearSeleccionDestino("Buenos Aires", "Hotel Palermo Soho", null);

    SeleccionItinerario seleccion = new SeleccionItinerario();
    seleccion.setDestinosSeleccionados(List.of(destino));

    ResumenItinerario resumen = this.servicioResumen.generarResumen(seleccion, planificacion);

    assertEquals(1, resumen.getDestinos().size());
    assertEquals(180.0, resumen.getTotal());
    assertTrue(resumen.getDestinos().get(0).getActividadesSeleccionadas().isEmpty());
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
      List.of(new Hotel("Hotel Palermo Soho", 90.0), new Hotel("Hotel Recoleta Plaza", 150.0)),
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
    seleccion.setDestinosSeleccionados(List.of(crearSeleccionDestino(destino, hotel, actividades)));
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
