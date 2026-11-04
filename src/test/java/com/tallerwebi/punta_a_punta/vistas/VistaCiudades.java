package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;

public class VistaCiudades extends VistaWeb {

  private static final String BASE = "localhost:8080/spring";
  private static final String OPCIONES = "#ciudades-sugeridas option";

  public VistaCiudades(Page page) {
    super(page);
  }

  public void simularRespuestaDeLaApiDeCiudades(String json) {
    page.route(
      "**/ciudades?q=*",
      ruta ->
        ruta.fulfill(
          new Route.FulfillOptions().setStatus(200).setContentType("application/json").setBody(json)
        )
    );
  }

  public void irAPlanificacion() {
    page.navigate(BASE + "/planificacion");
  }

  public void escribirEnElDestino(String texto) {
    page.locator("#destino-0").pressSequentially(texto);
  }

  public void esperarSugerencias(int cantidad) {
    page.waitForFunction(
      "cantidad => document.querySelectorAll('" + OPCIONES + "').length === cantidad",
      cantidad
    );
  }

  public int contarSugerenciasConValor(String valor) {
    return page.locator(OPCIONES + "[value='" + valor + "']").count();
  }

  public int contarSugerencias() {
    return page.locator(OPCIONES).count();
  }
}
