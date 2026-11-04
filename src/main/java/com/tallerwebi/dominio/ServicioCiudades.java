package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ServicioCiudades {
  List<CiudadDto> buscarCiudades(String texto);
}
