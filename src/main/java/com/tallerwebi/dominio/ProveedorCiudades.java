package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ProveedorCiudades {
  List<CiudadDto> buscar(String texto, int maximo);
}
