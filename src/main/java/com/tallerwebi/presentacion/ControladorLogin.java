package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLogin {

  @GetMapping("/login")
  public ModelAndView irALogin() {
    return new ModelAndView("login");
  }

  @GetMapping("/registro")
  public ModelAndView irARegistro() {
    return new ModelAndView("registro");
  }

  @GetMapping("/home")
  public ModelAndView irAHome() {
    return new ModelAndView("home");
  }
}
