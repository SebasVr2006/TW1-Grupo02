package com.tallerwebi.dominio;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class Pago {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "reserva_id", nullable = false, unique = true)
  private Reserva reserva;

  private String mercadoPagoOrderId;
  private String checkoutUrl;

  public Long getId() {
    return id;
  }

  public Reserva getReserva() {
    return reserva;
  }

  public void setReserva(Reserva reserva) {
    this.reserva = reserva;
  }

  public String getMercadoPagoOrderId() {
    return mercadoPagoOrderId;
  }

  public void setMercadoPagoOrderId(String mercadoPagoOrderId) {
    this.mercadoPagoOrderId = mercadoPagoOrderId;
  }

  public String getCheckoutUrl() {
    return checkoutUrl;
  }

  public void setCheckoutUrl(String checkoutUrl) {
    this.checkoutUrl = checkoutUrl;
  }
}
