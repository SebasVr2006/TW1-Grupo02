package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Planificacion;
import com.tallerwebi.dominio.ServicioPlanificacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ControladorPlanificacion {

  private ServicioPlanificacion servicioPlanificacion;

  @Autowired
  public ControladorPlanificacion(ServicioPlanificacion servicioPlanificacion) {
    this.servicioPlanificacion = servicioPlanificacion;
  }

  @GetMapping("/planificacion")
  public String mostrarPlanificacion() {
    return "planificacion";
  }

  @PostMapping("/planificacion")
  @ResponseBody
  public Planificacion recibirPlanificacion(@RequestBody Planificacion planificacion) {
    return this.servicioPlanificacion.guardarPlanificacion(planificacion);
  }
}
