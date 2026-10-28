package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaLogin extends VistaWeb {

  public VistaLogin(Page page) {
    super(page);
    page.navigate("localhost:8080/spring/login");
  }

  public String obtenerTextoDelEncabezado() {
    return this.obtenerTextoDelElemento("nav.menu .lista_header");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("p.mensaje-error");
  }

  public void escribirEMAIL(String email) {
    this.escribirEnElElemento("#email", email);
  }

  public void escribirClave(String clave) {
    this.escribirEnElElemento("#contrasena", clave);
  }

  public void darClickEnIniciarSesion() {
    this.darClickEnElElemento("#login-form button[type='submit']");
  }

  public void darClickEnCerrarSesion() {
    this.darClickEnElElemento(".boton-cerrar-sesion");
    this.page.locator("nav.menu a[href*='/login']").waitFor();
  }

  public void esperarAlHome() {
    this.page.waitForURL("**/home**");
  }

  public void darClickEnRegistrarse() {
    this.darClickEnElElemento("a[href*='/registro']");
  }
}
