const botonAgregar = document.getElementById("agregarDestino");
const seccionDestinos = document.querySelector(".destinos");

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
});
