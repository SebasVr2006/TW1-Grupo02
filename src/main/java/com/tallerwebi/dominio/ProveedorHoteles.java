package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ProveedorHoteles {
  List<Hotel> buscar(String lugar, String fechaEntrada, String fechaSalida, int maximo);
}
