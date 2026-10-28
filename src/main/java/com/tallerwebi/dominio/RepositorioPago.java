package com.tallerwebi.dominio;

public interface RepositorioPago {
  void guardar(Pago pago);

  Pago buscarPorReserva(Long idReserva);

  Pago buscarPorMercadoPagoOrderId(String mercadoPagoOrderId);
}
