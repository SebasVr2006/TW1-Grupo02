package com.tallerwebi.dominio;

import java.util.List;

@FunctionalInterface
public interface ProveedorVuelos {
  List<Vuelo> buscar(String origen, String destino, String fecha, int adultos, int ninos);
}
