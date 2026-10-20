INSERT INTO Usuario (id, nombreUsuario, email, password)
VALUES (NULL, 'usuarioPrueba', 'test@unlam.edu.ar', 'test');

INSERT INTO destino_disponible (ciudad, pais)
VALUES
    ('Buenos Aires', 'Argentina'),
    ('Sao Paulo', 'Brasil'),
    ('Paris', 'Francia');

INSERT INTO hotel (nombre, precioPorNoche, destino_id)
VALUES
    ('Hotel Palermo Soho', 90.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    ('Hotel Recoleta Plaza', 150.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    ('Hotel Paulista Centro', 80.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Sao Paulo')),
    ('Hotel Jardins Boutique', 135.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Sao Paulo')),
    ('Hôtel Le Marais', 225.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Paris')),
    ('Hôtel Montmartre Vue', 210.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Paris'));

INSERT INTO actividad (nombre, precio, destino_id)
VALUES
    ('Show de tango en San Telmo', 70.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    ('Tour por La Boca y Caminito', 45.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    ('Visita al Mercado Municipal', 25.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Sao Paulo')),
    ('Tour por Avenida Paulista y MASP', 40.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Sao Paulo')),
    ('Entrada a la Torre Eiffel', 40.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Paris')),
    ('Crucero por el Sena', 25.0,
     (SELECT id FROM destino_disponible WHERE ciudad = 'Paris'));