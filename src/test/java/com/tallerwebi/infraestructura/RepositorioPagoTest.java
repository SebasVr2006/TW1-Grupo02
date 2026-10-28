package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Pago;
import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RepositorioPagoTest {

  private SessionFactory sessionFactory;
  private Session session;
  private Query<Pago> consulta;
  private RepositorioPagoImpl repositorio;
  private Pago pago;
  private Pago resultado;

  @BeforeEach
  void setUp() {
    sessionFactory = mock(SessionFactory.class);
    session = mock(Session.class);
    consulta = mock(Query.class);
    repositorio = new RepositorioPagoImpl(sessionFactory);
    pago = new Pago();

    when(sessionFactory.getCurrentSession()).thenReturn(session);
  }

  @Test
  public void guardarPagoDeberiaPersistirLaEntidad() {
    whenGuardoElPago();

    thenSePersisteElPago();
  }

  private void whenGuardoElPago() {
    repositorio.guardar(pago);
  }

  private void thenSePersisteElPago() {
    verify(session).persist(pago);
  }

  @Test
  public void buscarPorReservaSinResultadosDeberiaDevolverNulo() {
    givenNoHayPagosParaLaReserva();

    whenBuscoElPagoPorReserva();

    thenElResultadoEsNulo();
  }

  private void givenNoHayPagosParaLaReserva() {
    when(session.createQuery("from Pago p where p.reserva.id = :idReserva", Pago.class))
      .thenReturn(consulta);

    when(consulta.setParameter("idReserva", 10L)).thenReturn(consulta);
    when(consulta.setMaxResults(1)).thenReturn(consulta);
    when(consulta.getResultList()).thenReturn(Collections.emptyList());
  }

  private void whenBuscoElPagoPorReserva() {
    resultado = repositorio.buscarPorReserva(10L);
  }

  private void thenElResultadoEsNulo() {
    assertThat(resultado, nullValue());
  }

  @Test
  public void buscarPorReservaConResultadoDeberiaDevolverElPago() {
    givenExisteUnPagoParaLaReserva();

    whenBuscoElPagoPorReserva();

    thenSeDevuelveElPagoEncontrado();
  }

  private void givenExisteUnPagoParaLaReserva() {
    when(session.createQuery("from Pago p where p.reserva.id = :idReserva", Pago.class))
      .thenReturn(consulta);

    when(consulta.setParameter("idReserva", 10L)).thenReturn(consulta);
    when(consulta.setMaxResults(1)).thenReturn(consulta);
    when(consulta.getResultList()).thenReturn(List.of(pago));
  }

  private void thenSeDevuelveElPagoEncontrado() {
    assertThat(resultado, equalTo(pago));
  }

  @Test
  public void buscarPorOrdenSinResultadosDeberiaDevolverNulo() {
    givenNoHayPagosParaLaOrden();

    whenBuscoElPagoPorOrden();

    thenElResultadoEsNulo();
  }

  private void givenNoHayPagosParaLaOrden() {
    when(session.createQuery("from Pago p where p.mercadoPagoOrderId = :orderId", Pago.class))
      .thenReturn(consulta);

    when(consulta.setParameter("orderId", "orden-123")).thenReturn(consulta);
    when(consulta.setMaxResults(1)).thenReturn(consulta);
    when(consulta.getResultList()).thenReturn(Collections.emptyList());
  }

  private void whenBuscoElPagoPorOrden() {
    resultado = repositorio.buscarPorMercadoPagoOrderId("orden-123");
  }

  @Test
  public void buscarPorOrdenConResultadoDeberiaDevolverElPago() {
    givenExisteUnPagoParaLaOrden();

    whenBuscoElPagoPorOrden();

    thenSeDevuelveElPagoEncontrado();
  }

  private void givenExisteUnPagoParaLaOrden() {
    when(session.createQuery("from Pago p where p.mercadoPagoOrderId = :orderId", Pago.class))
      .thenReturn(consulta);

    when(consulta.setParameter("orderId", "orden-123")).thenReturn(consulta);
    when(consulta.setMaxResults(1)).thenReturn(consulta);
    when(consulta.getResultList()).thenReturn(List.of(pago));
  }
}
