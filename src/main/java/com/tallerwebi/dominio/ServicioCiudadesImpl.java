package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CiudadesNoDisponiblesException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServicioCiudadesImpl implements ServicioCiudades {

  private static final int MINIMO_CARACTERES = 2;
  private static final int MAXIMO_SUGERENCIAS = 5;

  private final ProveedorCiudades proveedorCiudades;

  @Autowired
  public ServicioCiudadesImpl(ProveedorCiudades proveedorCiudades) {
    this.proveedorCiudades = proveedorCiudades;
  }

  @Override
  public List<CiudadDto> buscarCiudades(String texto) {
    String textoLimpio = texto == null ? "" : texto.trim();
    if (textoLimpio.length() < MINIMO_CARACTERES) {
      return new ArrayList<>();
    }
    try {
      return proveedorCiudades
        .buscar(textoLimpio, MAXIMO_SUGERENCIAS)
        .stream()
        .distinct()
        .collect(Collectors.toList());
    } catch (CiudadesNoDisponiblesException e) {
      return new ArrayList<>();
    }
  }
}
