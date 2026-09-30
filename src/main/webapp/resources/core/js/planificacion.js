const botonAgregar = document.getElementById("agregarDestino");
const destinos = document.querySelector(".destinos");

let numeroDestino = 1;

botonAgregar.addEventListener("click", function () {

    numeroDestino++;

    const nuevoDestino = document.createElement("div");

    nuevoDestino.classList.add("destino");

    nuevoDestino.innerHTML = `
        <h4>Destino ${numeroDestino}</h4>

        <label>Destino:</label>
        <input type="text" name="destino">

        <label>Fecha de inicio:</label>
        <input type="date" name="fechaInicio">

        <label>Duración:</label>
        <input type="number" name="duracion" min="1">

        <span>días</span>
    `;

    destinos.insertBefore(nuevoDestino, botonAgregar);
});

const botonContinuar = document.getElementById("continuar");

botonContinuar.addEventListener("click", function () {

    const adultos = document.getElementById("adultos").value;
    const ninos = document.getElementById("ninos").value;
    const alojamiento = document.getElementById("alojamiento").value;
    const destinosIngresados = document.querySelectorAll(".destino");

    const destinos = [];

    destinosIngresados.forEach(function (destino) {

        const nombre = destino.querySelector('input[name="destino"]').value;
        const fecha = destino.querySelector('input[name="fechaInicio"]').value;
        const duracion = destino.querySelector('input[name="duracion"]').value;

        destinos.push({
            destino: nombre,
            fechaInicio: fecha,
            duracion: duracion
        });
    });

    const interesesSeleccionados = document.querySelectorAll(
        'input[name="intereses"]:checked'
    );

    const intereses = [];

    interesesSeleccionados.forEach(function (interes) {
        intereses.push(interes.value);
    });

    console.log("Adultos:", adultos);
    console.log("Niños:", ninos);
    console.log("Alojamiento:", alojamiento);
    console.log("Intereses:", intereses);
    console.log("Destinos:", destinos);

    const resumen = document.getElementById("resumenPlanificacion");

    resumen.innerHTML = `
        <h3>Resumen de tu viaje</h3>

            <h4>Destinos</h4>
            <ul>
                ${destinos.map(function (destino) {
                    return `
                        <li>
                            ${destino.destino} -
                            ${destino.fechaInicio} -
                            ${destino.duracion} días
                        </li>
                    `;
                }).join("")}
            </ul>

            <h4>Viajeros</h4>
            <p>Adultos: ${adultos}</p>
            <p>Niños: ${ninos}</p>

            <h4>Preferencias</h4>
            <p>Alojamiento: ${alojamiento || "No seleccionado"}</p>
            <p>Intereses: ${intereses.length > 0 ? intereses.join(", ") : "Ninguno"}</p>
    `;

     fetch("/spring/planificacion", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            destinos: destinos,
            adultos: adultos,
            ninos: ninos,
            alojamiento: alojamiento,
            intereses: intereses
        })
    });
});