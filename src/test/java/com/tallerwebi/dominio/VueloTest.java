package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VueloTest {

  private Vuelo vuelo;

  @BeforeEach
  public void inicializar() {
    this.vuelo = new Vuelo();
  }

  @Test
  public void laDuracionDeberiaMostrarHorasYMinutos() {
    this.vuelo.setDuracionMinutos(125);

    assertThat(this.vuelo.getDuracionFormateada(), is(equalTo("2 h 5 min")));
  }

  @Test
  public void laDuracionDeberiaMostrarSoloMinutosSiEsMenorAUnaHora() {
    this.vuelo.setDuracionMinutos(45);

    assertThat(this.vuelo.getDuracionFormateada(), is(equalTo("45 min")));
  }

  @Test
  public void laDuracionDeberiaMostrarSoloHorasSiNoHayMinutosSueltos() {
    this.vuelo.setDuracionMinutos(180);

    assertThat(this.vuelo.getDuracionFormateada(), is(equalTo("3 h")));
  }

  @Test
  public void unVueloSinEscalasDeberiaSerDirecto() {
    this.vuelo.setEscalas(0);

    assertThat(this.vuelo.getEscalasTexto(), is(equalTo("Directo")));
  }

  @Test
  public void unVueloConUnaEscalaDeberiaIndicarloEnSingular() {
    this.vuelo.setEscalas(1);

    assertThat(this.vuelo.getEscalasTexto(), is(equalTo("1 escala")));
  }

  @Test
  public void unVueloConVariasEscalasDeberiaIndicarloEnPlural() {
    this.vuelo.setEscalas(2);

    assertThat(this.vuelo.getEscalasTexto(), is(equalTo("2 escalas")));
  }
}
