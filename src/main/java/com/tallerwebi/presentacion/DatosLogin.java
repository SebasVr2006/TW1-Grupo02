package com.tallerwebi.presentacion;

public class DatosLogin {

  private String nombreUsuario;
  private String email;
  private String password;
  private String confirmarPassword;

  public DatosLogin() {}

  public DatosLogin(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getNombreUsuario() {
    return nombreUsuario;
  }

  public void setNombreUsuario(String nombreUsuario) {
    this.nombreUsuario = nombreUsuario;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getConfirmarPassword() {
    return confirmarPassword;
  }

  public void setConfirmarPassword(String confirmarPassword) {
    this.confirmarPassword = confirmarPassword;
  }
}
