package com.tallerwebi.dominio;

public class Actividad {

    private final String nombre;
    private final Double precio;

    public Actividad(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecio() {
        return precio;
    }
}
