export function parsearFecha(valor) {
  if (!valor) {
    return null;
  }
  const [anio, mes, dia] = valor.split("-").map(Number);
  return new Date(anio, mes - 1, dia);
}

export function formatearFecha(fecha) {
  const dia = String(fecha.getDate()).padStart(2, "0");
  const mes = String(fecha.getMonth() + 1).padStart(2, "0");
  return `${dia}/${mes}/${fecha.getFullYear()}`;
}

export function esFechaAnteriorAHoy(valor, hoy = new Date()) {
  const fecha = parsearFecha(valor);
  if (!fecha) {
    return false;
  }
  const inicioDeHoy = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());
  return fecha < inicioDeHoy;
}

export function calcularFechaFin(fechaInicio, duracion) {
  const inicio = parsearFecha(fechaInicio);
  const dias = Number(duracion);
  if (!inicio || !(dias > 0)) {
    return null;
  }
  return new Date(inicio.getFullYear(), inicio.getMonth(), inicio.getDate() + dias);
}

export function validarDestinos(destinos, hoy = new Date()) {
  return destinos.map(function (destino, indice) {
    if (esFechaAnteriorAHoy(destino.fechaInicio, hoy)) {
      return "La fecha de inicio no puede ser anterior a hoy.";
    }

    if (indice === 0) {
      return null;
    }

    const anterior = destinos[indice - 1];
    const finAnterior = calcularFechaFin(anterior.fechaInicio, anterior.duracion);
    const inicio = parsearFecha(destino.fechaInicio);

    if (finAnterior && inicio && inicio < finAnterior) {
      return `La fecha de inicio no puede ser anterior al fin del destino anterior (${formatearFecha(finAnterior)}).`;
    }

    return null;
  });
}
