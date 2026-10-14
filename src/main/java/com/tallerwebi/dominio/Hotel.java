package com.tallerwebi.dominio;

public class Hotel {

    private final String nombre;
    private final Double precioPorNoche;

    public Hotel(String nombre, Double precio) {
        this.nombre = nombre;
        this.precioPorNoche = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecioPorNoche() {
        return precioPorNoche;
    }
}
