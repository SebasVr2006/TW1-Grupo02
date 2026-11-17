package com.tallerwebi.dominio;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("servicioRecomendaciones")
@Transactional(readOnly = true)
public class ServicioRecomendacionesImpl implements ServicioRecomendaciones {

  private static final int MAX_RECOMENDACIONES = 3;
  private static final int PUNTOS_MISMO_PAIS = 3;
  private static final int PUNTOS_MISMA_REGION = 1;

  private static final String SUDAMERICA = "Sudamérica";
  private static final String NORTEAMERICA = "Norteamérica";
  private static final String EUROPA = "Europa";
  private static final String ASIA = "Asia";
  private static final String AFRICA = "África";
  private static final String OCEANIA = "Oceanía";

  private static final Map<String, String> REGION_POR_PAIS = Map.ofEntries(
    Map.entry("Argentina", SUDAMERICA),
    Map.entry("Brasil", SUDAMERICA),
    Map.entry("Chile", SUDAMERICA),
    Map.entry("Perú", SUDAMERICA),
    Map.entry("Colombia", SUDAMERICA),
    Map.entry("Uruguay", SUDAMERICA),
    Map.entry("Estados Unidos", NORTEAMERICA),
    Map.entry("Canadá", NORTEAMERICA),
    Map.entry("México", NORTEAMERICA),
    Map.entry("Francia", EUROPA),
    Map.entry("Italia", EUROPA),
    Map.entry("España", EUROPA),
    Map.entry("Reino Unido", EUROPA),
    Map.entry("Escocia", EUROPA),
    Map.entry("Alemania", EUROPA),
    Map.entry("Países Bajos", EUROPA),
    Map.entry("Portugal", EUROPA),
    Map.entry("República Checa", EUROPA),
    Map.entry("Austria", EUROPA),
    Map.entry("Grecia", EUROPA),
    Map.entry("Turquía", EUROPA),
    Map.entry("Japón", ASIA),
    Map.entry("Tailandia", ASIA),
    Map.entry("Emiratos Árabes Unidos", ASIA),
    Map.entry("Egipto", AFRICA),
    Map.entry("Australia", OCEANIA)
  );

  private final RepositorioItinerario repositorioItinerario;
  private final RepositorioReserva repositorioReserva;

  @Autowired
  public ServicioRecomendacionesImpl(
    RepositorioItinerario repositorioItinerario,
    RepositorioReserva repositorioReserva
  ) {
    this.repositorioItinerario = repositorioItinerario;
    this.repositorioReserva = repositorioReserva;
  }

  @Override
  public List<RecomendacionDestino> recomendarDestinos(
    Long idUsuario,
    Planificacion planificacionActual
  ) {
    List<DestinoDisponible> catalogo = repositorioItinerario.buscarTodosLosDestinos();
    List<Visita> visitas = obtenerVisitas(idUsuario, planificacionActual, catalogo);

    if (visitas.isEmpty()) {
      return List.of();
    }

    Set<String> yaElegidos = new HashSet<>();
    visitas.forEach(visita -> yaElegidos.add(normalizar(visita.destino().getCiudad())));

    return catalogo
      .stream()
      .filter(candidato -> !yaElegidos.contains(normalizar(candidato.getCiudad())))
      .map(candidato -> puntuar(candidato, visitas))
      .filter(candidatoPuntuado -> candidatoPuntuado.puntaje() > 0)
      .sorted(this::compararPuntuados)
      .limit(MAX_RECOMENDACIONES)
      .map(Puntuado::recomendacion)
      .toList();
  }

  private int compararPuntuados(Puntuado primero, Puntuado segundo) {
    int porPuntaje = Integer.compare(segundo.puntaje(), primero.puntaje());

    if (porPuntaje != 0) {
      return porPuntaje;
    }
    return primero.recomendacion().getCiudad().compareTo(segundo.recomendacion().getCiudad());
  }

  private List<Visita> obtenerVisitas(
    Long idUsuario,
    Planificacion planificacionActual,
    List<DestinoDisponible> catalogo
  ) {
    List<Visita> visitas = new ArrayList<>();

    if (idUsuario != null) {
      for (Reserva reserva : repositorioReserva.buscarPorUsuario(idUsuario)) {
        for (ReservaDestino destino : reserva.getDestinos()) {
          agregarVisita(visitas, buscarEnCatalogo(destino.getNombreDestino(), catalogo), false);
        }
      }
    }

    if (planificacionActual != null) {
      for (Destino destino : planificacionActual.getDestinos()) {
        agregarVisita(visitas, buscarEnCatalogo(destino.getNombre(), catalogo), true);
      }
    }

    return visitas;
  }

  private void agregarVisita(List<Visita> visitas, DestinoDisponible destino, boolean actual) {
    if (destino != null) {
      visitas.add(new Visita(destino, actual));
    }
  }

  private DestinoDisponible buscarEnCatalogo(String nombre, List<DestinoDisponible> catalogo) {
    if (nombre == null) {
      return null;
    }
    String ciudadBuscada = normalizar(nombre.split(",")[0]);

    return catalogo
      .stream()
      .filter(destino -> normalizar(destino.getCiudad()).equals(ciudadBuscada))
      .findFirst()
      .orElse(null);
  }

  private Puntuado puntuar(DestinoDisponible candidato, List<Visita> visitas) {
    int puntajeTotal = 0;
    int mejorPuntaje = 0;
    Visita mejorVisita = null;

    for (Visita visita : visitas) {
      int puntos = puntosEntre(visita.destino(), candidato);
      puntajeTotal += puntos;

      boolean desempateAFavorDeActual =
        puntos == mejorPuntaje && mejorVisita != null && visita.actual() && !mejorVisita.actual();

      if (puntos > mejorPuntaje || (puntos > 0 && desempateAFavorDeActual)) {
        mejorPuntaje = puntos;
        mejorVisita = visita;
      }
    }

    String motivo = mejorVisita == null ? "" : armarMotivo(mejorVisita, mejorPuntaje);
    return new Puntuado(
      puntajeTotal,
      new RecomendacionDestino(candidato.getCiudad(), candidato.getPais(), motivo)
    );
  }

  private int puntosEntre(DestinoDisponible visitado, DestinoDisponible candidato) {
    if (visitado.getPais().equals(candidato.getPais())) {
      return PUNTOS_MISMO_PAIS;
    }

    String regionVisitada = REGION_POR_PAIS.get(visitado.getPais());
    if (regionVisitada != null && regionVisitada.equals(REGION_POR_PAIS.get(candidato.getPais()))) {
      return PUNTOS_MISMA_REGION;
    }

    return 0;
  }

  private String armarMotivo(Visita visita, int puntos) {
    String inicio = visita.actual()
      ? "Porque estás planificando viajar a "
      : "Porque ya viajaste a ";
    DestinoDisponible destino = visita.destino();

    if (puntos == PUNTOS_MISMO_PAIS) {
      return inicio + destino.getCiudad() + ", también en " + destino.getPais();
    }
    return (
      inicio + destino.getCiudad() + ": otro destino de " + REGION_POR_PAIS.get(destino.getPais())
    );
  }

  private String normalizar(String texto) {
    return Normalizer
      .normalize(texto.trim(), Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(Locale.ROOT);
  }

  private record Visita(DestinoDisponible destino, boolean actual) {}

  private record Puntuado(int puntaje, RecomendacionDestino recomendacion) {}
}
