package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Actividad;
import com.tallerwebi.dominio.DestinoDisponible;
import com.tallerwebi.dominio.Hotel;
import com.tallerwebi.dominio.RepositorioItinerario;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RepositorioItinerarioImpl implements RepositorioItinerario {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioItinerarioImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public DestinoDisponible buscarDestinoPorCiudad(String ciudad) {
    List<DestinoDisponible> destinos = sessionFactory
      .getCurrentSession()
      .createQuery("from DestinoDisponible", DestinoDisponible.class)
      .getResultList();

    String ciudadBuscada = normalizarCiudad(ciudad);

    return destinos
      .stream()
      .filter(destino -> normalizarCiudad(destino.getCiudad()).equals(ciudadBuscada))
      .findFirst()
      .orElse(null);
  }

  private String normalizarCiudad(String ciudad) {
    return java.text.Normalizer
      .normalize(ciudad.trim(), java.text.Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase(java.util.Locale.ROOT);
  }

  @Override
  public List<Hotel> buscarHotelesPorDestino(Long destinoId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery("from Hotel h where h.destino.id = :destinoId order by h.nombre", Hotel.class)
      .setParameter("destinoId", destinoId)
      .getResultList();
  }

  @Override
  public List<Actividad> buscarActividadesPorDestino(Long destinoId) {
    return sessionFactory
      .getCurrentSession()
      .createQuery(
        "from Actividad a where a.destino.id = :destinoId order by a.nombre",
        Actividad.class
      )
      .setParameter("destinoId", destinoId)
      .getResultList();
  }
}
