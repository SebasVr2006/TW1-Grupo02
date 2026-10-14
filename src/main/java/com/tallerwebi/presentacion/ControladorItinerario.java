package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.OpcionDestino;
import com.tallerwebi.dominio.ServicioItinerario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ControladorItinerario {

    private final ServicioItinerario servicioItinerario;

    @Autowired
    public ControladorItinerario(ServicioItinerario servicioItinerario) {
        this.servicioItinerario = servicioItinerario;
    }

    @GetMapping("/itinerario")
    @ResponseBody
    public List<OpcionDestino> generarItinerario() {
        return this.servicioItinerario.generarItinerario();
    }
}
