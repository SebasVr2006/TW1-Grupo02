package com.tallerwebi.punta_a_punta;

import java.io.IOException;

public class ReiniciarDB {

  public static void limpiarBaseDeDatos() {
    try {
      String dbHost = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
      String dbPort = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "3306";
      String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "tallerwebi";
      String dbUser = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "user";
      String dbPassword = System.getenv("DB_PASSWORD") != null
        ? System.getenv("DB_PASSWORD")
        : "user";

      String sqlCommands =
        "SET FOREIGN_KEY_CHECKS=0;\n" +
        "DELETE FROM reserva_destino_actividad;\n" +
        "DELETE FROM reserva_interes;\n" +
        "DELETE FROM ReservaDestino;\n" +
        "DELETE FROM Reserva;\n" +
        "DELETE FROM Usuario;\n" +
        "SET FOREIGN_KEY_CHECKS=1;\n" +
        "ALTER TABLE Usuario AUTO_INCREMENT = 1;\n" +
        "INSERT INTO Usuario(id, nombreUsuario, email, password) VALUES(null, 'usuarioPrueba', 'test@unlam.edu.ar', 'test'), " +
        "(null, 'otroUsuario', 'otro@unlam.edu.ar', 'otro');";

      String comando = String.format(
        "docker exec tallerwebi-mysql mysql -h %s -P %s -u %s -p%s %s -e \"%s\"",
        dbHost,
        dbPort,
        dbUser,
        dbPassword,
        dbName,
        sqlCommands
      );

      Process process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", comando });
      int exitCode = process.waitFor();

      if (exitCode == 0) {
        System.out.println("Base de datos limpiada exitosamente");
      } else {
        System.err.println("Error al limpiar la base de datos. Exit code: " + exitCode);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Error ejecutando script de limpieza: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
