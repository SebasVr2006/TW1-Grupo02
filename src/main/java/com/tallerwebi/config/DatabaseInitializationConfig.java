package com.tallerwebi.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DatabaseInitializationConfig {

  @Bean
  @DependsOn("sessionFactory")
  public InitializingBean cargaInicialDeDatos(DataSource dataSource) {
    return () -> {
      Integer cantidadDeDestinos = new JdbcTemplate(dataSource)
        .queryForObject("SELECT COUNT(*) FROM destino_disponible", Integer.class);

      if (cantidadDeDestinos != null && cantidadDeDestinos == 0) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
          new ClassPathResource("data.sql")
        );
        populator.setSqlScriptEncoding("UTF-8");
        populator.execute(dataSource);
      }
    };
  }
}
