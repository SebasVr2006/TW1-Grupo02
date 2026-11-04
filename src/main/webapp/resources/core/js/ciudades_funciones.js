const MINIMO_CARACTERES = 2;

export function debeBuscarCiudades(texto) {
  return typeof texto === "string" && texto.trim().length >= MINIMO_CARACTERES;
}

export function armarOpciones(ciudades) {
  return ciudades.map(function (ciudad) {
    const valor = [ciudad.nombre, ciudad.pais].filter(Boolean).join(", ");
    return { value: valor, label: ciudad.provincia || "" };
  });
}

export async function pedirCiudades(urlBase, texto) {
  const respuesta = await fetch(`${urlBase}?q=${encodeURIComponent(texto.trim())}`);
  if (!respuesta.ok) {
    return [];
  }
  return respuesta.json();
}

export function mostrarOpciones(datalist, opciones) {
  datalist.replaceChildren(
    ...opciones.map(function (opcion) {
      const elemento = document.createElement("option");
      elemento.value = opcion.value;
      elemento.label = opcion.label;
      return elemento;
    }),
  );
}
