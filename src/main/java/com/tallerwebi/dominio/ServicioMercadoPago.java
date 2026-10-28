package com.tallerwebi.dominio;

public interface ServicioMercadoPago {
  String crearCheckout(Reserva reserva, Usuario usuario);

  EstadoReserva sincronizarPorReserva(Long idReserva, Usuario usuario);

  void sincronizarPorOrden(String orderId);
}
