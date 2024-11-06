--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- PostgreSQL database dump complete
--

--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0 (Debian 17.0-1.pgdg120+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: arbitro; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.arbitro (
    dni integer NOT NULL,
    nombre character varying(50)
);


ALTER TABLE public.arbitro OWNER TO postgres;

--
-- Name: equipo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.equipo (
    nombre character varying(50) NOT NULL
);


ALTER TABLE public.equipo OWNER TO postgres;

--
-- Name: info_partido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.info_partido (
    id_partido integer NOT NULL,
    fecha_hora timestamp without time zone,
    dni_arbitro integer,
    equipo1_goles integer,
    equipo2_goles integer
);


ALTER TABLE public.info_partido OWNER TO postgres;

--
-- Name: jugador; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jugador (
    dni integer NOT NULL,
    nombre character varying(50) NOT NULL,
    nombre_equipo character varying(50)
);


ALTER TABLE public.jugador OWNER TO postgres;

--
-- Name: jugador_partido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.jugador_partido (
    dni_jugador integer NOT NULL,
    id_partido integer NOT NULL
);


ALTER TABLE public.jugador_partido OWNER TO postgres;

--
-- Name: partido; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.partido (
    id_partido integer NOT NULL,
    equipo1 character varying(50),
    equipo2 character varying(50),
    id_torneo integer,
    id_partido_torneo integer,
    equipo_ganador character varying(50)
);


ALTER TABLE public.partido OWNER TO postgres;

--
-- Name: partido_id_partido_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.partido_id_partido_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.partido_id_partido_seq OWNER TO postgres;

--
-- Name: partido_id_partido_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.partido_id_partido_seq OWNED BY public.partido.id_partido;


--
-- Name: torneo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.torneo (
    id_torneo integer NOT NULL,
    nombre character varying(50) NOT NULL,
    equipo_ganador character varying(50)
);


ALTER TABLE public.torneo OWNER TO postgres;

--
-- Name: torneo_id_torneo_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.torneo_id_torneo_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.torneo_id_torneo_seq OWNER TO postgres;

--
-- Name: torneo_id_torneo_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.torneo_id_torneo_seq OWNED BY public.torneo.id_torneo;


--
-- Name: partido id_partido; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partido ALTER COLUMN id_partido SET DEFAULT nextval('public.partido_id_partido_seq'::regclass);


--
-- Name: torneo id_torneo; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.torneo ALTER COLUMN id_torneo SET DEFAULT nextval('public.torneo_id_torneo_seq'::regclass);


--
-- Data for Name: arbitro; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.arbitro (dni, nombre) FROM stdin;
\.


--
-- Data for Name: equipo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.equipo (nombre) FROM stdin;
\.


--
-- Data for Name: info_partido; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.info_partido (id_partido, fecha_hora, dni_arbitro, equipo1_goles, equipo2_goles) FROM stdin;
\.


--
-- Data for Name: jugador; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.jugador (dni, nombre, nombre_equipo) FROM stdin;
\.


--
-- Data for Name: jugador_partido; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.jugador_partido (dni_jugador, id_partido) FROM stdin;
\.


--
-- Data for Name: partido; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.partido (id_partido, equipo1, equipo2, id_torneo, id_partido_torneo, equipo_ganador) FROM stdin;
\.


--
-- Data for Name: torneo; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.torneo (id_torneo, nombre, equipo_ganador) FROM stdin;
\.


--
-- Name: partido_id_partido_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.partido_id_partido_seq', 1, false);


--
-- Name: torneo_id_torneo_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.torneo_id_torneo_seq', 1, false);


--
-- Name: arbitro arbitro_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.arbitro
    ADD CONSTRAINT arbitro_pkey PRIMARY KEY (dni);


--
-- Name: equipo equipo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.equipo
    ADD CONSTRAINT equipo_pkey PRIMARY KEY (nombre);


--
-- Name: info_partido info_partido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.info_partido
    ADD CONSTRAINT info_partido_pkey PRIMARY KEY (id_partido);


--
-- Name: jugador_partido jugador_partido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jugador_partido
    ADD CONSTRAINT jugador_partido_pkey PRIMARY KEY (dni_jugador, id_partido);


--
-- Name: jugador jugador_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jugador
    ADD CONSTRAINT jugador_pkey PRIMARY KEY (dni);


--
-- Name: partido partido_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partido
    ADD CONSTRAINT partido_pkey PRIMARY KEY (id_partido);


--
-- Name: torneo torneo_nombre_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.torneo
    ADD CONSTRAINT torneo_nombre_key UNIQUE (nombre);


--
-- Name: torneo torneo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.torneo
    ADD CONSTRAINT torneo_pkey PRIMARY KEY (id_torneo);


--
-- Name: info_partido info_partido_dni_arbitro_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.info_partido
    ADD CONSTRAINT info_partido_dni_arbitro_fkey FOREIGN KEY (dni_arbitro) REFERENCES public.arbitro(dni);


--
-- Name: info_partido info_partido_id_partido_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.info_partido
    ADD CONSTRAINT info_partido_id_partido_fkey FOREIGN KEY (id_partido) REFERENCES public.partido(id_partido);


--
-- Name: jugador jugador_nombre_equipo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jugador
    ADD CONSTRAINT jugador_nombre_equipo_fkey FOREIGN KEY (nombre_equipo) REFERENCES public.equipo(nombre);


--
-- Name: jugador_partido jugador_partido_dni_jugador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jugador_partido
    ADD CONSTRAINT jugador_partido_dni_jugador_fkey FOREIGN KEY (dni_jugador) REFERENCES public.jugador(dni);


--
-- Name: jugador_partido jugador_partido_id_partido_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.jugador_partido
    ADD CONSTRAINT jugador_partido_id_partido_fkey FOREIGN KEY (id_partido) REFERENCES public.partido(id_partido);


--
-- Name: partido partido_equipo1_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partido
    ADD CONSTRAINT partido_equipo1_fkey FOREIGN KEY (equipo1) REFERENCES public.equipo(nombre);


--
-- Name: partido partido_equipo2_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partido
    ADD CONSTRAINT partido_equipo2_fkey FOREIGN KEY (equipo2) REFERENCES public.equipo(nombre);


--
-- Name: partido partido_equipo_ganador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partido
    ADD CONSTRAINT partido_equipo_ganador_fkey FOREIGN KEY (equipo_ganador) REFERENCES public.equipo(nombre);


--
-- Name: partido partido_id_torneo_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.partido
    ADD CONSTRAINT partido_id_torneo_fkey FOREIGN KEY (id_torneo) REFERENCES public.torneo(id_torneo);


--
-- Name: torneo torneo_equipo_ganador_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.torneo
    ADD CONSTRAINT torneo_equipo_ganador_fkey FOREIGN KEY (equipo_ganador) REFERENCES public.equipo(nombre);


--
-- PostgreSQL database dump complete
--

