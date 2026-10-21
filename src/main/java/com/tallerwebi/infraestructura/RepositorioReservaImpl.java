package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioReserva;
import com.tallerwebi.dominio.Reserva;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioReserva")
public class RepositorioReservaImpl implements RepositorioReserva {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioReservaImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Reserva reserva) {
    this.sessionFactory.getCurrentSession().persist(reserva);
  }

  @Override
  public List<Reserva> buscarPorUsuario(Long idUsuario) {
    return this.sessionFactory.getCurrentSession()
      .createQuery(
        "from Reserva where usuario.id = :idUsuario order by fechaCreacion desc, id desc",
        Reserva.class
      )
      .setParameter("idUsuario", idUsuario)
      .getResultList();
  }
}
