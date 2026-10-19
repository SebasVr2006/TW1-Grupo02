package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioLogin;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLogin {

  private static final String VISTA_LOGIN = "login";
  private static final String VISTA_REGISTRO = "registro";
  private static final String VISTA_HOME = "home";
  private static final String CLAVE_MENSAJE_ERROR = "mensajeError";
  private static final String CLAVE_DATOS_LOGIN = "datosLogin";

  private ServicioLogin servicioLogin;

  @Autowired
  public ControladorLogin(ServicioLogin servicioLogin) {
    this.servicioLogin = servicioLogin;
  }

  @GetMapping("/login")
  public ModelAndView irALogin() {
    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_DATOS_LOGIN, new DatosLogin());
    return new ModelAndView(VISTA_LOGIN, modelo);
  }

  @PostMapping("/login")
  public ModelAndView validarLogin(
    @ModelAttribute DatosLogin datosLogin,
    HttpServletRequest request
  ) {
    Usuario usuario = servicioLogin.consultarUsuario(
      datosLogin.getEmail(),
      datosLogin.getPassword()
    );

    if (usuario == null) {
      return mostrarLoginConError(datosLogin, "El email o la contraseña son incorrectos.");
    }

    HttpSession sesion = request.getSession();
    sesion.setAttribute("usuarioLogueado", usuario);

    return new ModelAndView("redirect:/home");
  }

  @GetMapping("/registro")
  public ModelAndView irARegistro() {
    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_DATOS_LOGIN, new DatosLogin());
    return new ModelAndView(VISTA_REGISTRO, modelo);
  }

  @PostMapping("/registro")
  public ModelAndView registrarUsuario(@ModelAttribute DatosLogin datosLogin) {
    String mensajeError = validarDatosRegistro(datosLogin);

    if (mensajeError != null) {
      return mostrarRegistroConError(datosLogin, mensajeError);
    }

    Usuario usuario = crearUsuario(datosLogin);

    try {
      servicioLogin.registrar(usuario);
    } catch (UsuarioExistente e) {
      return mostrarRegistroConError(datosLogin, "Ya existe una cuenta registrada con ese email.");
    }

    return new ModelAndView("redirect:/login");
  }

  private String validarDatosRegistro(DatosLogin datosLogin) {
    if (hayCamposObligatoriosVacios(datosLogin)) {
      return "Completá todos los campos.";
    }

    if (!coincidenLasContrasenas(datosLogin)) {
      return "Las contraseñas no coinciden.";
    }

    return null;
  }

  private boolean hayCamposObligatoriosVacios(DatosLogin datosLogin) {
    return (
      estaVacio(datosLogin.getNombreUsuario()) ||
      estaVacio(datosLogin.getEmail()) ||
      estaVacio(datosLogin.getPassword()) ||
      estaVacio(datosLogin.getConfirmarPassword())
    );
  }

  private boolean estaVacio(String valor) {
    return valor == null || valor.isBlank();
  }

  private boolean coincidenLasContrasenas(DatosLogin datosLogin) {
    return datosLogin.getPassword().equals(datosLogin.getConfirmarPassword());
  }

  private Usuario crearUsuario(DatosLogin datosLogin) {
    Usuario usuario = new Usuario();
    usuario.setNombreUsuario(datosLogin.getNombreUsuario());
    usuario.setEmail(datosLogin.getEmail());
    usuario.setPassword(datosLogin.getPassword());
    return usuario;
  }

  private ModelAndView mostrarRegistroConError(DatosLogin datosLogin, String mensajeError) {
    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_MENSAJE_ERROR, mensajeError);
    modelo.put(CLAVE_DATOS_LOGIN, datosLogin);
    return new ModelAndView(VISTA_REGISTRO, modelo);
  }

  private ModelAndView mostrarLoginConError(DatosLogin datosLogin, String mensajeError) {
    Map<String, Object> modelo = new HashMap<>();
    modelo.put(CLAVE_MENSAJE_ERROR, mensajeError);
    modelo.put(CLAVE_DATOS_LOGIN, datosLogin);
    return new ModelAndView(VISTA_LOGIN, modelo);
  }

  @GetMapping("/home")
  public ModelAndView irAHome() {
    return new ModelAndView(VISTA_HOME);
  }
}
