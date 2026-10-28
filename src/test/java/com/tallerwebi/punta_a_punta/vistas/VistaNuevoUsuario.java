package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaNuevoUsuario extends VistaWeb {

  public VistaNuevoUsuario(Page page) {
    super(page);
  }

  public void escribirNombreUsuario(String nombreUsuario) {
    this.escribirEnElElemento("#usuario", nombreUsuario);
  }

  public void escribirEMAIL(String email) {
    this.escribirEnElElemento("#email", email);
  }

  public void escribirClave(String clave) {
    this.escribirEnElElemento("#contrasena", clave);
  }

  public void escribirConfirmacionDeClave(String clave) {
    this.escribirEnElElemento("#confirmar-contrasena", clave);
  }

  public void darClickEnRegistrarme() {
    this.darClickEnElElemento("#register-form button[type='submit']");
  }

  public String obtenerMensajeDeError() {
    return this.obtenerTextoDelElemento("p.mensaje-error");
  }
}
