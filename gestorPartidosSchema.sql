CREATE TABLE equipo(nombre VARCHAR(50) PRIMARY KEY);
CREATE TABLE jugador(dni integer PRIMARY KEY, nombre VARCHAR(50), nombre_equipo VARCHAR(50) REFERENCES equipo);
CREATE TABLE partido(id_partido SERIAL PRIMARY KEY, equipo1 VARCHAR(50) REFERENCES equipo, equipo2 VARCHAR(50) REFERENCES equipo, equipo_ganador VARCHAR(50) REFERENCES equipo, fecha_hora TIMESTAMP, equipo1_goles integer, equipo2_goles integer);
CREATE TABLE jugador_partido(dni_jugador integer REFERENCES jugador, id_partido integer REFERENCES partido);
ALTER TABLE jugador_partido ADD PRIMARY KEY (dni_jugador, id_partido);
