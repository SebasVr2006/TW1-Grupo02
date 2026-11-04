import { armarOpciones, debeBuscarCiudades } from "../ciudades_funciones.js";

describe("Ciudades Funciones", function () {
  describe("debeBuscarCiudades", function () {
    it("debe devolver true cuando el texto tiene dos o más letras", function () {
      expect(debeBuscarCiudades("Bu")).toBe(true);
    });

    it("debe devolver false cuando el texto tiene una sola letra", function () {
      expect(debeBuscarCiudades("B")).toBe(false);
    });

    it("debe ignorar los espacios del principio y del final", function () {
      expect(debeBuscarCiudades("  B  ")).toBe(false);
    });

    it("debe devolver false cuando el texto está vacío, es null o no es un texto", function () {
      expect(debeBuscarCiudades("")).toBe(false);
      expect(debeBuscarCiudades(null)).toBe(false);
      expect(debeBuscarCiudades(undefined)).toBe(false);
    });
  });

  describe("armarOpciones", function () {
    it("debe usar la ciudad y el país como valor y la provincia como etiqueta", function () {
      const ciudades = [
        { nombre: "Buenos Aires", pais: "Argentina", provincia: "Ciudad Autónoma de Buenos Aires" },
      ];

      expect(armarOpciones(ciudades)).toEqual([
        { value: "Buenos Aires, Argentina", label: "Ciudad Autónoma de Buenos Aires" },
      ]);
    });

    it("debe dejar la etiqueta vacía cuando no hay provincia", function () {
      const ciudades = [{ nombre: "Buenos Aires", pais: "Costa Rica", provincia: null }];

      expect(armarOpciones(ciudades)).toEqual([{ value: "Buenos Aires, Costa Rica", label: "" }]);
    });

    it("debe usar solo la ciudad cuando no hay país", function () {
      const ciudades = [{ nombre: "Buenos Aires", pais: null, provincia: null }];

      expect(armarOpciones(ciudades)).toEqual([{ value: "Buenos Aires", label: "" }]);
    });

    it("debe devolver una lista vacía cuando no hay ciudades", function () {
      expect(armarOpciones([])).toEqual([]);
    });
  });
});
