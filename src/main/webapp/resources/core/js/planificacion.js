import { validarDestinos } from "./planificacion_funciones.js";

const botonAgregar = document.getElementById("agregarDestino");
const seccionDestinos = document.querySelector(".destinos");
const formulario = document.getElementById("formPlanificacion");

let numeroDestino = 1;

botonAgregar.addEventListener("click", function () {
  const indiceDestino = numeroDestino;
  numeroDestino++;

  const nuevoDestino = document.createElement("div");
  nuevoDestino.classList.add("destino");

  nuevoDestino.innerHTML = `
        <h4>Destino ${numeroDestino}</h4>

        <label for="destino-${indiceDestino}">Destino:</label>
        <input
            type="text"
            id="destino-${indiceDestino}"
            name="destinos[${indiceDestino}].nombre"
            required
        >

        <label for="fechaInicio-${indiceDestino}">Fecha de inicio:</label>
        <input
            type="date"
            id="fechaInicio-${indiceDestino}"
            name="destinos[${indiceDestino}].fechaInicio"
            required
        >

        <label for="duracion-${indiceDestino}">Duración:</label>
        <input
            type="number"
            id="duracion-${indiceDestino}"
            name="destinos[${indiceDestino}].duracion"
            min="1"
            required
        >

        <span>días</span>
    `;

  seccionDestinos.insertBefore(nuevoDestino, botonAgregar);
  mostrarErrores();
});

function leerDestinos() {
  return Array.from(seccionDestinos.querySelectorAll(".destino")).map(function (destino) {
    return {
      fechaInicio: destino.querySelector("input[type=date]").value,
      duracion: destino.querySelector("input[type=number]").value
    };
  });
}

function mostrarError(destino, texto) {
  let mensaje = destino.querySelector(".error-destino");

  if (!texto) {
    if (mensaje) {
      mensaje.remove();
    }
    destino.classList.remove("destino-invalido");
    return;
  }

  if (!mensaje) {
    mensaje = document.createElement("p");
    mensaje.classList.add("error-destino");
    mensaje.setAttribute("role", "alert");
    destino.appendChild(mensaje);
  }

  mensaje.textContent = texto;
  destino.classList.add("destino-invalido");
}

function mostrarErrores() {
  const errores = validarDestinos(leerDestinos());

  seccionDestinos.querySelectorAll(".destino").forEach(function (destino, indice) {
    mostrarError(destino, errores[indice]);
  });

  return errores.every(function (error) {
    return error === null;
  });
}

seccionDestinos.addEventListener("input", mostrarErrores);

formulario.addEventListener("submit", function (evento) {
  if (!mostrarErrores()) {
    evento.preventDefault();
    seccionDestinos.querySelector(".error-destino").scrollIntoView({ behavior: "smooth", block: "center" });
  }
});
