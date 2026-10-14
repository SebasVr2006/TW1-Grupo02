package com.tallerwebi.dominio;

import java.util.List;
import org.springframework.stereotype.Service;

@Service("servicioItinerario")
public class ServicioItinerarioImpl implements ServicioItinerario {

    @Override
    public List<OpcionDestino> generarItinerario() {
        return List.of(crearBuenosAires(), creaDestinoParis(), crearSaoPaulo());
    }

    public OpcionDestino crearBuenosAires() {
        return new OpcionDestino(
                "Buenos Aires",
                "Argentina",
                List.of(new Hotel("Hotel Paulista Centro", 80.0), new Hotel("Hotel Jardins Boutique", 135.0)),
                List.of(
                        new Actividad("Visita al Mercado Municipal", 25.0),
                        new Actividad("Tour por Avenida Paulista y MASP", 40.0)
                )
        );
    }

    public OpcionDestino crearSaoPaulo() {
        return new OpcionDestino(
                "Sao Paulo",
                "Brasil",
                List.of(new Hotel("Hotel Palermo Soho", 90.0), new Hotel("Hotel Recoleta Plaza", 150.0)),
                List.of(
                        new Actividad("Show de tango en San Telmo", 70.0),
                        new Actividad("Tour por La Boca y Caminito", 35.0)
                )
        );
    }

    public OpcionDestino creaDestinoParis() {
        return new OpcionDestino(
                "Paris",
                "Francia",
                List.of(new Hotel("Hôtel Le Marais", 160.0), new Hotel("Hôtel Montmartre Vue", 210.0)),
                List.of(
                        new Actividad("Entrada a la Torre Eiffel", 35.0),
                        new Actividad("Crucero por el Sena", 25.0)
                )
        );
    }
}
