INSERT INTO Usuario (id, nombreUsuario, email, password)
VALUES (NULL, 'usuarioPrueba', 'test@unlam.edu.ar', 'test');

INSERT INTO destino_disponible (ciudad, pais)
VALUES
    ('Buenos Aires', 'Argentina'),
    ('São Paulo', 'Brasil'),
    ('París', 'Francia'),
    ('Roma', 'Italia'),
    ('Madrid', 'España'),
    ('Barcelona', 'España'),
    ('Londres', 'Reino Unido'),
    ('Nueva York', 'Estados Unidos'),
    ('Tokio', 'Japón'),
    ('Berlín', 'Alemania'),
    ('Ámsterdam', 'Países Bajos'),
    ('Lisboa', 'Portugal'),
    ('Río de Janeiro', 'Brasil'),
    ('Santiago', 'Chile'),
    ('Lima', 'Perú'),
    ('Ciudad de México', 'México'),
    ('Bogotá', 'Colombia'),
    ('Montevideo', 'Uruguay'),
    ('Praga', 'República Checa'),
    ('Viena', 'Austria'),
    ('Atenas', 'Grecia'),
    ('Sidney', 'Australia'),
    ('Dubái', 'Emiratos Árabes Unidos'),
    ('Bangkok', 'Tailandia'),
    ('El Cairo', 'Egipto'),
    ('Estambul', 'Turquía'),
    ('Toronto', 'Canadá'),
    ('Miami', 'Estados Unidos'),
    ('Los Ángeles', 'Estados Unidos'),
    ('Edimburgo', 'Escocia');

INSERT INTO hotel (nombre, precioPorNoche, destino_id)
VALUES
    -- Buenos Aires
    ('Hotel Palermo Soho', 90.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    ('Hotel Recoleta Plaza', 150.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    -- São Paulo
    ('Hotel Paulista Centro', 80.0, (SELECT id FROM destino_disponible WHERE ciudad = 'São Paulo')),
    ('Hotel Jardins Boutique', 135.0, (SELECT id FROM destino_disponible WHERE ciudad = 'São Paulo')),
    -- París
    ('Hôtel Le Marais', 225.0, (SELECT id FROM destino_disponible WHERE ciudad = 'París')),
    ('Hôtel Montmartre Vue', 210.0, (SELECT id FROM destino_disponible WHERE ciudad = 'París')),
    -- Roma
    ('Hotel Colosseo View', 160.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Roma')),
    ('Trastevere Boutique Hotel', 130.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Roma')),
    -- Madrid
    ('Hotel Gran Vía Central', 120.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Madrid')),
    ('Hotel Retiro Park', 140.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Madrid')),
    -- Barcelona
    ('Hotel Ramblas Luxe', 155.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Barcelona')),
    ('Gothic Quarter Suite', 135.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Barcelona')),
    -- Londres
    ('The Soho Central Hotel', 210.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Londres')),
    ('Kensington Palace Hotel', 240.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Londres')),
    -- Nueva York
    ('Times Square Manhattan Hotel', 280.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Nueva York')),
    ('Brooklyn Bridge Inn', 220.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Nueva York')),
    -- Tokio
    ('Shinjuku Tower Hotel', 175.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Tokio')),
    ('Shibuya Crossing Inn', 150.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Tokio')),
    -- Berlín
    ('Alexanderplatz Central Hotel', 110.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Berlín')),
    ('Kreuzberg Boutique Stay', 95.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Berlín')),
    -- Ámsterdam
    ('Canal View Suites', 190.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ámsterdam')),
    ('Jordaan District Hotel', 170.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ámsterdam')),
    -- Lisboa
    ('Alfama Heritage Hotel', 105.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lisboa')),
    ('Chiado Square Suites', 125.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lisboa')),
    -- Río de Janeiro
    ('Copacabana Oceanfront Hotel', 140.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Río de Janeiro')),
    ('Ipanema Beach Boutique', 160.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Río de Janeiro')),
    -- Santiago
    ('Hotel Providencia Park', 85.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Santiago')),
    ('Lastarria Suites', 105.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Santiago')),
    -- Lima
    ('Miraflores Bay Hotel', 95.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lima')),
    ('Barranco Art Hotel', 110.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lima')),
    -- Ciudad de México
    ('Condesa Boutique Hotel', 100.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ciudad de México')),
    ('Reforma Grand Hotel', 130.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ciudad de México')),
    -- Bogotá
    ('Hotel Zona Rosa Central', 75.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bogotá')),
    ('La Candelaria Colonial Inn', 65.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bogotá')),
    -- Montevideo
    ('Punta Carretas Hotel', 85.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Montevideo')),
    ('Rambla Ocean View', 100.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Montevideo')),
    -- Praga
    ('Old Town Square Hotel', 115.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Praga')),
    ('Charles Bridge Suites', 135.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Praga')),
    -- Viena
    ('Hotel Opera Vienna', 165.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Viena')),
    ('Belvedere Park Inn', 145.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Viena')),
    -- Atenas
    ('Plaka Acropolis Hotel', 110.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Atenas')),
    ('Monastiraki Suites', 95.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Atenas')),
    -- Sidney
    ('Darling Harbour Hotel', 215.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Sidney')),
    ('Opera View Suites', 250.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Sidney')),
    -- Dubái
    ('Marina Luxury Hotel', 290.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Dubái')),
    ('Downtown Resort & Spa', 340.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Dubái')),
    -- Bangkok
    ('Riverside Grand Hotel', 70.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bangkok')),
    ('Sukhumvit Central Hotel', 60.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bangkok')),
    -- El Cairo
    ('Pyramids View Resort', 85.0, (SELECT id FROM destino_disponible WHERE ciudad = 'El Cairo')),
    ('Nile River Palace', 110.0, (SELECT id FROM destino_disponible WHERE ciudad = 'El Cairo')),
    -- Estambul
    ('Sultanahmet Heritage Hotel', 90.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Estambul')),
    ('Bosphorus View Suites', 130.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Estambul')),
    -- Toronto
    ('CN Tower View Hotel', 180.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Toronto')),
    ('Downtown Toronto Inn', 150.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Toronto')),
    -- Miami
    ('South Beach Ocean Hotel', 230.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Miami')),
    ('Brickell Financial Suites', 195.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Miami')),
    -- Los Ángeles
    ('Hollywood Boulevard Hotel', 200.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Los Ángeles')),
    ('Santa Monica Beach Inn', 240.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Los Ángeles')),
    -- Edimburgo
    ('Royal Mile Historic Hotel', 140.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Edimburgo')),
    ('Edinburgh Castle View Inn', 160.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Edimburgo'));

INSERT INTO actividad (nombre, precio, destino_id)
VALUES
    -- Buenos Aires
    ('Show de tango en San Telmo', 70.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    ('Tour por La Boca y Caminito', 45.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Buenos Aires')),
    -- São Paulo
    ('Visita al Mercado Municipal', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'São Paulo')),
    ('Tour por Avenida Paulista y MASP', 40.0, (SELECT id FROM destino_disponible WHERE ciudad = 'São Paulo')),
    -- París
    ('Entrada a la Torre Eiffel', 40.0, (SELECT id FROM destino_disponible WHERE ciudad = 'París')),
    ('Crucero por el Sena', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'París')),
    -- Roma
    ('Tour guiado por el Coliseo y Foro Romano', 55.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Roma')),
    ('Visita a los Museos Vaticanos y Capilla Sixtina', 60.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Roma')),
    -- Madrid
    ('Entrada al Museo del Prado', 20.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Madrid')),
    ('Tour gastronómico de tapas por La Latina', 50.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Madrid')),
    -- Barcelona
    ('Entrada a la Basílica de la Sagrada Familia', 35.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Barcelona')),
    ('Paseo guiado por el Park Güell', 22.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Barcelona')),
    -- Londres
    ('Ticket para el London Eye', 38.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Londres')),
    ('Tour guiado por la Torre de Londres', 42.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Londres')),
    -- Nueva York
    ('Subida al mirador Empire State Building', 48.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Nueva York')),
    ('Paseo en ferry a la Estatua de la Libertad', 30.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Nueva York')),
    -- Tokio
    ('Tour guiado por el Templo Senso-ji en Asakusa', 35.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Tokio')),
    ('Entrada a la torre Tokyo Skytree', 28.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Tokio')),
    -- Berlín
    ('Tour histórico del Muro de Berlín y Checkpoint Charlie', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Berlín')),
    ('Visita guiada al Parlamento (Reichstag)', 18.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Berlín')),
    -- Ámsterdam
    ('Paseo en barco por los canales de Ámsterdam', 22.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ámsterdam')),
    ('Entrada al Museo Van Gogh', 24.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ámsterdam')),
    -- Lisboa
    ('Paseo en el tranvía 28 y tour por el barrio de Alfama', 20.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lisboa')),
    ('Visita a la Torre de Belém y Pastéis de Belém', 18.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lisboa')),
    -- Río de Janeiro
    ('Teleférico y entrada al Cristo Redentor', 40.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Río de Janeiro')),
    ('Paseo en teleférico por el Pan de Azúcar', 35.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Río de Janeiro')),
    -- Santiago
    ('Excursión y cata de vinos en Valle del Maipo', 65.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Santiago')),
    ('Teleférico y subida al Cerro San Cristóbal', 15.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Santiago')),
    -- Lima
    ('Tour gastronómico y cata de Pisco', 55.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lima')),
    ('Visita al Huaca Pucllana y Centro Histórico', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Lima')),
    -- Ciudad de México
    ('Tour a las Pirámides de Teotihuacán', 45.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ciudad de México')),
    ('Visita al Museo Frida Kahlo en Coyoacán', 20.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Ciudad de México')),
    -- Bogotá
    ('Subida en funicular al Cerro de Monserrate', 16.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bogotá')),
    ('Visita al Museo del Oro y La Candelaria', 22.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bogotá')),
    -- Montevideo
    ('Tour a pie por la Ciudad Vieja y Mercado del Puerto', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Montevideo')),
    ('Cata de vinos en bodega tradicional', 50.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Montevideo')),
    -- Praga
    ('Tour a pie por el Castillo de Praga', 30.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Praga')),
    ('Crucero nocturno por el río Moldava', 28.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Praga')),
    -- Viena
    ('Concierto de música clásica en la Orquesta de Viena', 65.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Viena')),
    ('Visita guiada al Palacio de Schönbrunn', 32.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Viena')),
    -- Atenas
    ('Entrada a la Acrópolis y Museo de la Acrópolis', 38.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Atenas')),
    ('Paseo gastronómico por Monastiraki y Plaka', 45.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Atenas')),
    -- Sidney
    ('Tour guiado por la Ópera de Sidney', 32.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Sidney')),
    ('Crucero por la Bahía de Sidney', 45.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Sidney')),
    -- Dubái
    ('Entrada al mirador del Burj Khalifa', 65.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Dubái')),
    ('Safari 4x4 por el desierto con cena show', 75.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Dubái')),
    --- Bangkok
    ('Visita al Gran Palacio Real y Templo del Buda de Esmeralda', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bangkok')),
    ('Paseo en barco por los mercados flotantes', 30.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Bangkok')),
    -- El Cairo
    ('Tour privado por las Pirámides de Giza y la Esfinge', 50.0, (SELECT id FROM destino_disponible WHERE ciudad = 'El Cairo')),
    ('Visita al Gran Museo Egipcio', 30.0, (SELECT id FROM destino_disponible WHERE ciudad = 'El Cairo')),
    -- Estambul
    ('Visita a Santa Sofía y la Mezquita Azul', 35.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Estambul')),
    ('Crucero por el Estrecho del Bósforo', 25.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Estambul')),
    -- Toronto
    ('Entrada a la torre CN Tower', 35.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Toronto')),
    ('Excursión de un día a las Cataratas del Niágara', 85.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Toronto')),
    -- Miami
    ('Paseo en lancha por las mansiones de Biscayne Bay', 40.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Miami')),
    ('Tour de Arte Urbano en Wynwood Walls', 28.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Miami')),
    -- Los Ángeles
    ('Tour por los estudios Universal Studios Hollywood', 110.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Los Ángeles')),
    ('Paseo por el Paseo de la Fama y Observatorio Griffith', 30.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Los Ángeles')),
    -- Edimburgo
    ('Visita guiada al Castillo de Edimburgo', 28.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Edimburgo')),
    ('Tour de mitos y fantasmas por el subterráneo de la ciudad', 22.0, (SELECT id FROM destino_disponible WHERE ciudad = 'Edimburgo'));