package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Pago;
import com.tallerwebi.dominio.RepositorioPago;
import java.util.List;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioPago")
public class RepositorioPagoImpl implements RepositorioPago {

  private final SessionFactory sessionFactory;

  @Autowired
  public RepositorioPagoImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void guardar(Pago pago) {
    this.sessionFactory.getCurrentSession().persist(pago);
  }

  @Override
  public Pago buscarPorReserva(Long idReserva) {
    List<Pago> pagos =
      this.sessionFactory.getCurrentSession()
        .createQuery("from Pago p where p.reserva.id = :idReserva", Pago.class)
        .setParameter("idReserva", idReserva)
        .setMaxResults(1)
        .getResultList();

    return pagos.isEmpty() ? null : pagos.get(0);
  }

  @Override
  public Pago buscarPorMercadoPagoOrderId(String mercadoPagoOrderId) {
    List<Pago> pagos =
      this.sessionFactory.getCurrentSession()
        .createQuery("from Pago p where p.mercadoPagoOrderId = :orderId", Pago.class)
        .setParameter("orderId", mercadoPagoOrderId)
        .setMaxResults(1)
        .getResultList();

    return pagos.isEmpty() ? null : pagos.get(0);
  }
}
