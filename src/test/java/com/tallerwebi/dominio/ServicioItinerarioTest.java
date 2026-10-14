package com.tallerwebi.dominio;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServicioItinerarioTest {

    private ServicioItinerario servicioItinerario;

    @BeforeEach
    public void init() {
        this.servicioItinerario = new ServicioItinerarioImpl();
    }
    
    @Test
    void generarItinerarioDeberiaDevolverTresOpcionesDeDestino() {
        // Ejecucion --> when
        List<OpcionDestino> destinos = this.servicioItinerario.generarItinerario();

        // Validacion --> then
        assertThat(destinos, hasSize(3));
    }
}
