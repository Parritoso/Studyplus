-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 08-06-2025 a las 22:09:05
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `estudyplus`
--
CREATE DATABASE IF NOT EXISTS `estudyplus` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `estudyplus`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `amistad`
--

CREATE TABLE `amistad` (
  `id` int(11) NOT NULL,
  `usuario1_id` int(11) NOT NULL,
  `usuario2_id` int(11) NOT NULL,
  `estado` varchar(50) NOT NULL,
  `fechaPeticion` datetime NOT NULL DEFAULT current_timestamp(),
  `fechaRespuesta` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `amistad`
--

INSERT INTO `amistad` (`id`, `usuario1_id`, `usuario2_id`, `estado`, `fechaPeticion`, `fechaRespuesta`) VALUES
(1, 3, 7, 'aceptado', '2025-06-08 07:43:17', '2025-06-08 08:01:45');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `conversacion`
--

CREATE TABLE `conversacion` (
  `id` int(11) NOT NULL,
  `fechaCreacion` datetime NOT NULL DEFAULT current_timestamp(),
  `ultimoMensajeFecha` datetime DEFAULT NULL,
  `grupo_estudio_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `conversacion`
--

INSERT INTO `conversacion` (`id`, `fechaCreacion`, `ultimoMensajeFecha`, `grupo_estudio_id`) VALUES
(1, '2025-06-08 08:55:43', NULL, NULL),
(27, '2025-06-08 17:02:50', NULL, 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `entrega`
--

CREATE TABLE `entrega` (
  `id` int(11) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_limite` datetime NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT current_timestamp(),
  `fecha_entrega_real` int(11) DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  `prioridad` varchar(50) NOT NULL,
  `asignatura` varchar(100) NOT NULL,
  `recordatorio_activo` tinyint(1) DEFAULT NULL,
  `fecha_recordatorio` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `usuario_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `entrega`
--

INSERT INTO `entrega` (`id`, `titulo`, `descripcion`, `fecha_limite`, `fecha_creacion`, `fecha_entrega_real`, `estado`, `prioridad`, `asignatura`, `recordatorio_activo`, `fecha_recordatorio`, `usuario_id`) VALUES
(2, 'test', 'test', '2025-06-08 00:00:00', NULL, NULL, 'PENDIENTE', 'Normal', 'test', 0, NULL, 3),
(3, 'feygwtkf', 'fgergregwz', '2025-06-09 00:00:00', NULL, NULL, 'PENDIENTE', 'Alta', 'test', 0, NULL, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `examen`
--

CREATE TABLE `examen` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(1000) NOT NULL,
  `fecha_examen` datetime NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `asignatura` varchar(255) DEFAULT NULL,
  `tipo_examen` varchar(50) NOT NULL,
  `prioridad` varchar(50) NOT NULL,
  `recordatorio_activo` tinyint(1) DEFAULT 1,
  `fecha_recordatorio` timestamp NULL DEFAULT current_timestamp(),
  `usuario_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `examen`
--

INSERT INTO `examen` (`id`, `nombre`, `descripcion`, `fecha_examen`, `fecha_creacion`, `asignatura`, `tipo_examen`, `prioridad`, `recordatorio_activo`, `fecha_recordatorio`, `usuario_id`) VALUES
(1, 'TEST', 'test', '2025-06-08 00:00:00', '2025-06-06 15:39:28', 'test', 'Final', 'Alta', 0, NULL, 3);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `grupoestudio`
--

CREATE TABLE `grupoestudio` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `creador_usuario_id` int(11) NOT NULL,
  `notas_rapidas` text DEFAULT '',
  `checklist` text DEFAULT '[]'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `grupoestudio`
--

INSERT INTO `grupoestudio` (`id`, `nombre`, `descripcion`, `fecha_creacion`, `creador_usuario_id`, `notas_rapidas`, `checklist`) VALUES
(2, 'test', 'test', '2025-06-08 14:40:17', 3, NULL, '[]');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `logproductividad`
--

CREATE TABLE `logproductividad` (
  `id` int(11) NOT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp(),
  `duracion_estudio_total_minutos` int(11) DEFAULT NULL,
  `duracion_descanso_total_minutos` int(11) DEFAULT NULL,
  `nivel_fatiga` varchar(50) DEFAULT NULL,
  `calificacion_enfoque` int(11) NOT NULL,
  `notas` text DEFAULT NULL,
  `tipo_evento` varchar(100) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `sesion_estudio_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `logproductividad`
--

INSERT INTO `logproductividad` (`id`, `fecha_registro`, `duracion_estudio_total_minutos`, `duracion_descanso_total_minutos`, `nivel_fatiga`, `calificacion_enfoque`, `notas`, `tipo_evento`, `usuario_id`, `sesion_estudio_id`) VALUES
(1, '2025-06-04 20:36:08', 0, 0, NULL, 0, '', 'SESION_FINALIZADA_ABORTED', 3, 0),
(2, '2025-06-05 17:48:07', 0, 0, NULL, 0, '', 'SESION_FINALIZADA_COMPLETED', 3, 0),
(3, '2025-06-05 17:48:48', 0, 0, NULL, 0, NULL, 'SESION_FINALIZADA_COMPLETED', 3, 0),
(4, '2025-06-06 13:19:16', 21, 15, NULL, 0, NULL, 'SESION_FINALIZADA_COMPLETED', 3, 0),
(5, '2025-06-06 19:03:11', 2, 0, NULL, 0, '', 'SESION_FINALIZADA_ABORTED', 3, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mensaje`
--

CREATE TABLE `mensaje` (
  `id` int(11) NOT NULL,
  `conversacion_id` int(11) NOT NULL,
  `emisor_id` int(11) NOT NULL,
  `contenido` text NOT NULL,
  `fechaEnvio` datetime NOT NULL DEFAULT current_timestamp(),
  `leido` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `mensaje`
--

INSERT INTO `mensaje` (`id`, `conversacion_id`, `emisor_id`, `contenido`, `fechaEnvio`, `leido`) VALUES
(1, 1, 7, 'Hola', '2025-06-08 09:27:20', 1),
(2, 1, 7, 'Hola', '2025-06-08 09:33:34', 1),
(3, 1, 3, 'Hola&#x21;', '2025-06-08 10:34:55', 0),
(4, 27, 3, 'Hola', '2025-06-08 17:32:42', 0),
(5, 27, 3, 'test', '2025-06-08 17:40:41', 0),
(6, 27, 3, 'test', '2025-06-08 17:41:46', 0),
(7, 27, 3, 'prueba', '2025-06-08 19:36:50', 0),
(8, 27, 3, 'rere', '2025-06-08 20:53:55', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `participanteconversacion`
--

CREATE TABLE `participanteconversacion` (
  `conversacion_id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `fechaUnion` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `participanteconversacion`
--

INSERT INTO `participanteconversacion` (`conversacion_id`, `usuario_id`, `fechaUnion`) VALUES
(1, 3, '2025-06-08 08:55:43'),
(1, 7, '2025-06-08 08:55:43'),
(27, 3, '2025-06-08 17:02:50');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `participantegrupo`
--

CREATE TABLE `participantegrupo` (
  `id` int(11) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `grupo_id` int(11) NOT NULL,
  `rol` varchar(50) DEFAULT NULL,
  `fecha_union` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `participantegrupo`
--

INSERT INTO `participantegrupo` (`id`, `usuario_id`, `grupo_id`, `rol`, `fecha_union`) VALUES
(1, 3, 2, 'admin', '2025-06-08 14:40:19'),
(2, 7, 2, 'participante', '2025-06-08 22:06:14'),
(4, 4, 2, 'participante', '2025-06-08 22:07:43');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sesionestudio`
--

CREATE TABLE `sesionestudio` (
  `id` int(11) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `duracion_planificada_minutos` int(11) NOT NULL,
  `fecha_sesion_planificada` datetime NOT NULL,
  `fecha_creacion` date NOT NULL DEFAULT current_timestamp(),
  `fecha_inicio_real` datetime DEFAULT current_timestamp(),
  `fecha_fin_real` datetime DEFAULT NULL,
  `duracion_real_minutos` int(11) DEFAULT NULL,
  `tiempo_estudio_efectivo_minutos` int(11) DEFAULT NULL,
  `tiempo_descanso_minutos` int(11) DEFAULT NULL,
  `estado` varchar(50) NOT NULL,
  `notas` varchar(1000) DEFAULT NULL,
  `tecnica_aplicada_id` int(11) DEFAULT NULL,
  `usuario_id` int(11) NOT NULL,
  `calificacion_enfoque` int(11) DEFAULT NULL,
  `detalles_interrupcion` text DEFAULT NULL,
  `notas_rapidas` text DEFAULT NULL,
  `checklist` text DEFAULT '[]',
  `entrega_asociada_id` int(11) DEFAULT NULL,
  `examen_asociado_id` int(11) DEFAULT NULL,
  `grupo_asociado_id` int(11) DEFAULT NULL,
  `fechaUltimaActualizacionEstado` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `sesionestudio`
--

INSERT INTO `sesionestudio` (`id`, `titulo`, `descripcion`, `duracion_planificada_minutos`, `fecha_sesion_planificada`, `fecha_creacion`, `fecha_inicio_real`, `fecha_fin_real`, `duracion_real_minutos`, `tiempo_estudio_efectivo_minutos`, `tiempo_descanso_minutos`, `estado`, `notas`, `tecnica_aplicada_id`, `usuario_id`, `calificacion_enfoque`, `detalles_interrupcion`, `notas_rapidas`, `checklist`, `entrega_asociada_id`, `examen_asociado_id`, `grupo_asociado_id`, `fechaUltimaActualizacionEstado`) VALUES
(32, 'Test', 'test-test', 90, '2025-06-06 18:54:30', '2025-06-06', '2025-06-06 18:54:30', NULL, 0, 0, 0, 'ACTIVE', NULL, 1, 3, 0, NULL, '', '&#x5b;&#x5d;', NULL, NULL, NULL, '2025-06-06 18:54:30'),
(33, 'Test', 'test-test', 90, '2025-06-06 18:55:56', '2025-06-06', '2025-06-06 18:55:56', NULL, 0, 0, 0, 'ACTIVE', NULL, 1, 3, 0, NULL, '', '&#x5b;&#x5d;', NULL, NULL, NULL, '2025-06-06 18:55:56'),
(34, 'Test', 'test-test', 90, '2025-06-06 19:00:12', '2025-06-06', '2025-06-06 19:00:12', '2025-06-06 19:03:10', 2, 2, 0, 'ABORTED', '', 1, 3, NULL, '', '', '&#x5b;&#x5d;', NULL, NULL, NULL, '2025-06-06 19:03:10'),
(35, 'Test', 'fkweba,gb', 90, '2025-06-06 20:19:12', '2025-06-06', '2025-06-06 20:19:12', NULL, 0, 0, 0, 'ACTIVE', NULL, 1, 3, 0, NULL, '', '&#x5b;&#x5d;', NULL, NULL, NULL, '2025-06-06 20:19:12'),
(36, 'ergrdg', 'rgfdbtrhrt', 70, '2025-06-09 22:56:00', '2025-06-08', NULL, NULL, 0, 0, 0, 'PLANNED', NULL, 5, 3, 0, NULL, '', '[]', NULL, NULL, 2, '2025-06-08 20:57:23');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tecnicaestudio`
--

CREATE TABLE `tecnicaestudio` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `duracion_foco_minutos` int(11) DEFAULT NULL,
  `duracion_descanso_minutos` int(11) DEFAULT NULL,
  `TIPO_TECNICA` varchar(50) NOT NULL DEFAULT 'INDIVIDUAL'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tecnicaestudio`
--

INSERT INTO `tecnicaestudio` (`id`, `nombre`, `descripcion`, `duracion_foco_minutos`, `duracion_descanso_minutos`, `TIPO_TECNICA`) VALUES
(1, 'Pomodoro', '25 minutos de concentración intensa seguidos de 5 minutos de descanso. Ideal para evitar la fatiga y mantener la productividad.', 1, 0, 'INDIVIDUAL'),
(2, '90/20', 'Períodos de estudio de 90 minutos de inmersión profunda, seguidos de 20 minutos de descanso prolongado. Útil para tareas complejas.', 90, 20, 'INDIVIDUAL'),
(3, 'Feynman', 'Aprende un concepto explicándolo en voz alta como si le enseñaras a alguien más. Esto revela brechas en tu conocimiento.', 45, 0, 'INDIVIDUAL'),
(4, 'Estudio Intercalado', 'Alterna entre diferentes temas o asignaturas durante una misma sesión de estudio para mejorar la retención y la comprensión general.', 30, 0, 'INDIVIDUAL'),
(5, 'Q&A Colaborativa', 'Los miembros del grupo se hacen preguntas unos a otros sobre el material para identificar y reforzar puntos débiles en el conocimiento colectivo.', 0, 0, 'GRUPO'),
(6, 'Explicación Cruzada', 'Cada persona en el grupo se encarga de explicar un segmento del material a los demás, rotando los roles de \"profesor\" y \"alumno\".', 0, 0, 'GRUPO'),
(7, 'Recuperación Activa', 'En lugar de releer el material, te autoevalúas activamente (haciéndote preguntas, haciendo resúmenes de memoria, usando flashcards) para recordar la información. Fortalece la memoria a largo plazo.', 20, 5, 'INDIVIDUAL'),
(8, 'Repetición Espaciada', 'Revisar el material a intervalos de tiempo crecientes. Empiezas revisando pronto, luego más tarde, luego mucho más tarde. Se usa a menudo con flashcards. Ayuda a consolidar el conocimiento a largo plazo', 15, 0, 'INDIVIDUAL'),
(9, 'Método Rompecabeza', 'Cada miembro del grupo se convierte en \"experto\" en una parte específica del material. Luego, cada experto enseña su parte al resto del grupo, uniendo todas las piezas como un rompecabezas.', 0, 0, 'GRUPO'),
(10, 'Enseñanza entre Pares', 'Un miembro del grupo asume el rol de \"profesor\" para explicar un tema a los demás. Los \"alumnos\" hacen preguntas y ofrecen perspectivas. Los roles rotan para que todos tengan la oportunidad de enseñar.', 0, 0, 'GRUPO'),
(11, 'Lluvia de Ideas', 'El grupo genera libremente ideas, soluciones o enfoques sobre un tema o problema específico. Se fomenta la cantidad de ideas sobre la calidad inicial, evaluando después.', 0, 0, 'GRUPO'),
(12, 'Resolución Colaborativa de Problemas', 'El grupo trabaja unido para resolver ejercicios, problemas o proyectos complejos. Se comparten conocimientos y se divide el trabajo para llegar a una solución conjunta.', 0, 0, 'GRUPO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `puntos` int(11) NOT NULL DEFAULT 0,
  `configuracion_perfil` text DEFAULT NULL,
  `fecha_registro` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `nombre`, `email`, `contrasena`, `puntos`, `configuracion_perfil`, `fecha_registro`) VALUES
(3, 'test1', 'test@test.com', '$2a$10$Svn55Ycsff8tp1ZIFWpQg.rZDGsKfXA0EhZrFW22vgPA4Jkf0CcqS', 20, NULL, '2025-06-04 15:19:36'),
(4, 'test2', 'test2@test2.com', 'fgvewkanm<zgow', 10, NULL, '2025-06-07 02:56:43'),
(5, 'test3', 'test3@test3.com', 'fregvhtrhzgerthtershtyumnkuyws', 5, NULL, '2025-06-07 02:56:43'),
(6, 'test4', 'test4@test4.com', 'pfmeflbwmpoewfvnoWQmh9emgfbregeñpsgkjh', 4, NULL, '2025-06-07 02:57:52'),
(7, 'test5', 'test5&#x40;test5.com', '$2a$10$X0m0hml1/dQPgheKsvWys.hnFKTL4sUJQjg9e3OCByuy0jaSd0Hfu', 0, NULL, '2025-06-07 21:42:51');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `amistad`
--
ALTER TABLE `amistad`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `usuario1_id` (`usuario1_id`,`usuario2_id`),
  ADD KEY `IDX_AMISTAD_USUARIO1` (`usuario1_id`),
  ADD KEY `IDX_AMISTAD_USUARIO2` (`usuario2_id`);

--
-- Indices de la tabla `conversacion`
--
ALTER TABLE `conversacion`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_conversacion_grupoestudio` (`grupo_estudio_id`);

--
-- Indices de la tabla `entrega`
--
ALTER TABLE `entrega`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `examen`
--
ALTER TABLE `examen`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `grupoestudio`
--
ALTER TABLE `grupoestudio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `creador_usuario_id` (`creador_usuario_id`);

--
-- Indices de la tabla `logproductividad`
--
ALTER TABLE `logproductividad`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `sesion_estudio_od` (`sesion_estudio_id`) USING BTREE;

--
-- Indices de la tabla `mensaje`
--
ALTER TABLE `mensaje`
  ADD PRIMARY KEY (`id`),
  ADD KEY `emisor_id` (`emisor_id`),
  ADD KEY `IDX_MENSAJE_CONVERSACION` (`conversacion_id`),
  ADD KEY `IDX_MENSAJE_FECHAENVIO` (`fechaEnvio`);

--
-- Indices de la tabla `participanteconversacion`
--
ALTER TABLE `participanteconversacion`
  ADD PRIMARY KEY (`conversacion_id`,`usuario_id`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Indices de la tabla `participantegrupo`
--
ALTER TABLE `participantegrupo`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `usuario_id` (`usuario_id`,`grupo_id`),
  ADD KEY `grupo_id` (`grupo_id`);

--
-- Indices de la tabla `sesionestudio`
--
ALTER TABLE `sesionestudio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `usuario_id` (`usuario_id`),
  ADD KEY `tecnica_aplicada_id` (`tecnica_aplicada_id`),
  ADD KEY `fk_sesionestudio_entrega` (`entrega_asociada_id`),
  ADD KEY `fk_sesionestudio_examen` (`examen_asociado_id`),
  ADD KEY `fk_sesionestudio_grupo` (`grupo_asociado_id`);

--
-- Indices de la tabla `tecnicaestudio`
--
ALTER TABLE `tecnicaestudio`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `amistad`
--
ALTER TABLE `amistad`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `conversacion`
--
ALTER TABLE `conversacion`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT de la tabla `entrega`
--
ALTER TABLE `entrega`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `examen`
--
ALTER TABLE `examen`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `grupoestudio`
--
ALTER TABLE `grupoestudio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `logproductividad`
--
ALTER TABLE `logproductividad`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `mensaje`
--
ALTER TABLE `mensaje`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `participantegrupo`
--
ALTER TABLE `participantegrupo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `sesionestudio`
--
ALTER TABLE `sesionestudio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT de la tabla `tecnicaestudio`
--
ALTER TABLE `tecnicaestudio`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `amistad`
--
ALTER TABLE `amistad`
  ADD CONSTRAINT `amistad_ibfk_1` FOREIGN KEY (`usuario1_id`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `amistad_ibfk_2` FOREIGN KEY (`usuario2_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `conversacion`
--
ALTER TABLE `conversacion`
  ADD CONSTRAINT `fk_conversacion_grupoestudio` FOREIGN KEY (`grupo_estudio_id`) REFERENCES `grupoestudio` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `entrega`
--
ALTER TABLE `entrega`
  ADD CONSTRAINT `entrega_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `examen`
--
ALTER TABLE `examen`
  ADD CONSTRAINT `examen_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `grupoestudio`
--
ALTER TABLE `grupoestudio`
  ADD CONSTRAINT `grupoestudio_ibfk_1` FOREIGN KEY (`creador_usuario_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `logproductividad`
--
ALTER TABLE `logproductividad`
  ADD CONSTRAINT `logproductividad_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `mensaje`
--
ALTER TABLE `mensaje`
  ADD CONSTRAINT `mensaje_ibfk_1` FOREIGN KEY (`conversacion_id`) REFERENCES `conversacion` (`id`),
  ADD CONSTRAINT `mensaje_ibfk_2` FOREIGN KEY (`emisor_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `participanteconversacion`
--
ALTER TABLE `participanteconversacion`
  ADD CONSTRAINT `participanteconversacion_ibfk_1` FOREIGN KEY (`conversacion_id`) REFERENCES `conversacion` (`id`),
  ADD CONSTRAINT `participanteconversacion_ibfk_2` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`);

--
-- Filtros para la tabla `participantegrupo`
--
ALTER TABLE `participantegrupo`
  ADD CONSTRAINT `participantegrupo_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `participantegrupo_ibfk_2` FOREIGN KEY (`grupo_id`) REFERENCES `grupoestudio` (`id`);

--
-- Filtros para la tabla `sesionestudio`
--
ALTER TABLE `sesionestudio`
  ADD CONSTRAINT `fk_sesionestudio_entrega` FOREIGN KEY (`entrega_asociada_id`) REFERENCES `entrega` (`id`),
  ADD CONSTRAINT `fk_sesionestudio_examen` FOREIGN KEY (`examen_asociado_id`) REFERENCES `examen` (`id`),
  ADD CONSTRAINT `fk_sesionestudio_grupo` FOREIGN KEY (`grupo_asociado_id`) REFERENCES `grupoestudio` (`id`),
  ADD CONSTRAINT `sesionestudio_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`),
  ADD CONSTRAINT `sesionestudio_ibfk_2` FOREIGN KEY (`tecnica_aplicada_id`) REFERENCES `tecnicaestudio` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
