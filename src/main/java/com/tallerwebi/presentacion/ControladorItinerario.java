package com.tallerwebi.presentacion;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tallerwebi.dominio.OpcionDestino;
import com.tallerwebi.dominio.ServicioItinerario;

@Controller 
public class ControladorItinerario {
    
    private final ServicioItinerario servicioItinerario;

    public ControladorItinerario(ServicioItinerario servicioItinerario) {
        this.servicioItinerario = servicioItinerario;
    }

    @GetMapping("/itinerario")
    @ResponseBody
    public List<OpcionDestino> generarItinerario() {
        return this.servicioItinerario.generarItinerario();
    } 
}
