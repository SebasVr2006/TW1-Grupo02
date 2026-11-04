package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.CiudadDto;
import com.tallerwebi.dominio.ServicioCiudades;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ControladorCiudades {

  private final ServicioCiudades servicioCiudades;

  @Autowired
  public ControladorCiudades(ServicioCiudades servicioCiudades) {
    this.servicioCiudades = servicioCiudades;
  }

  @GetMapping("/ciudades")
  @ResponseBody
  public List<CiudadDto> buscarCiudades(@RequestParam(name = "q", defaultValue = "") String texto) {
    return servicioCiudades.buscarCiudades(texto);
  }
}
