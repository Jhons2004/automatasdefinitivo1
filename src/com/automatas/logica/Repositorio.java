package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Gramatica;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Repositorio en memoria de la aplicación.
 *
 * <p>Guarda todos los AFD y gramáticas creados o cargados durante la sesión
 * (identificados por su nombre) y el <b>historial de cadenas evaluadas</b>.
 * El historial es lo que permite que el reporte PDF incluya las cadenas que
 * ya se validaron en el menú de Evaluar Cadenas, como pide el enunciado.</p>
 *
 * <p>Esta clase es el único lugar donde vive el estado global de la sesión:
 * las pantallas (vista) no se pasan datos entre sí, todas leen y escriben
 * aquí. Es el patrón de diseño más simple para una aplicación de escritorio
 * de este tamaño (un "almacén" central).</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class Repositorio {

    /** AFD creados o cargados en la sesión, por nombre. */
    private final Map<String, AFD> afds = new LinkedHashMap<>();

    /** Gramáticas creadas o cargadas en la sesión, por nombre. */
    private final Map<String, Gramatica> gramaticas = new LinkedHashMap<>();

    /** Historial de evaluaciones realizadas en la sesión. */
    private final List<EvaluacionRegistrada> historial = new ArrayList<>();

    /**
     * Guarda un AFD. Si ya existía uno con el mismo nombre, lo reemplaza.
     *
     * @param afd AFD a guardar
     * @return {@code true} si ya existía un AFD con ese nombre (reemplazo)
     */
    public boolean guardarAfd(AFD afd) {
        boolean reemplazado = afds.containsKey(afd.getNombre());
        afds.put(afd.getNombre(), afd);
        return reemplazado;
    }

    /**
     * Guarda una gramática. Si ya existía una con el mismo nombre, la reemplaza.
     *
     * @param gramatica gramática a guardar
     * @return {@code true} si ya existía una gramática con ese nombre (reemplazo)
     */
    public boolean guardarGramatica(Gramatica gramatica) {
        boolean reemplazado = gramaticas.containsKey(gramatica.getNombre());
        gramaticas.put(gramatica.getNombre(), gramatica);
        return reemplazado;
    }

    /** Devuelve el AFD guardado con ese nombre, o {@code null} si no existe. */
    public AFD obtenerAfd(String nombre) {
        return afds.get(nombre);
    }

    /** Devuelve la gramática guardada con ese nombre, o {@code null} si no existe. */
    public Gramatica obtenerGramatica(String nombre) {
        return gramaticas.get(nombre);
    }

    /** Nombres de los AFD guardados, en el orden en que se crearon. */
    public List<String> nombresAfd() {
        return new ArrayList<>(afds.keySet());
    }

    /** Nombres de las gramáticas guardadas, en el orden en que se crearon. */
    public List<String> nombresGramatica() {
        return new ArrayList<>(gramaticas.keySet());
    }

    /** Indica si ya hay un AFD guardado con ese nombre. */
    public boolean existeAfd(String nombre) {
        return afds.containsKey(nombre);
    }

    /** Indica si ya hay una gramática guardada con ese nombre. */
    public boolean existeGramatica(String nombre) {
        return gramaticas.containsKey(nombre);
    }

    /**
     * Registra una evaluación en el historial.
     *
     * @param tipo "AFD" o "Gramática"
     * @param nombre nombre del AFD o gramática evaluada
     * @param cadena cadena evaluada
     * @param valida resultado de la evaluación
     */
    public void registrarEvaluacion(String tipo, String nombre, String cadena, boolean valida) {
        historial.add(new EvaluacionRegistrada(tipo, nombre, cadena, valida));
    }

    /** Historial completo de evaluaciones de la sesión. */
    public List<EvaluacionRegistrada> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    /** Historial filtrado por tipo y nombre. */
    public List<EvaluacionRegistrada> historialDe(String tipo, String nombre) {
        List<EvaluacionRegistrada> resultado = new ArrayList<>();
        for (EvaluacionRegistrada e : historial) {
            if (e.getTipo().equals(tipo) && e.getNombre().equals(nombre)) {
                resultado.add(e);
            }
        }
        return resultado;
    }

    /**
     * Una evaluación registrada en el historial.
     */
    public static final class EvaluacionRegistrada {
        private final String tipo;
        private final String nombre;
        private final String cadena;
        private final boolean valida;
        private final LocalDateTime fechaHora;

        EvaluacionRegistrada(String tipo, String nombre, String cadena, boolean valida) {
            this.tipo = tipo;
            this.nombre = nombre;
            this.cadena = cadena;
            this.valida = valida;
            this.fechaHora = LocalDateTime.now();
        }

        /** Tipo de la evaluación: "AFD" o "Gramática". */
        public String getTipo() {
            return tipo;
        }

        /** Nombre del AFD o de la gramática evaluada. */
        public String getNombre() {
            return nombre;
        }

        /** Cadena que se evaluó. */
        public String getCadena() {
            return cadena;
        }

        /** Indica si la cadena resultó válida. */
        public boolean esValida() {
            return valida;
        }

        /** Fecha y hora en que se hizo la evaluación. */
        public LocalDateTime getFechaHora() {
            return fechaHora;
        }

        /** Una línea del historial: fecha, tipo, nombre, cadena y resultado. */
        @Override
        public String toString() {
            return fechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    + " | " + tipo + " '" + nombre + "' | cadena '" + cadena + "' | "
                    + (valida ? "válida" : "inválida");
        }
    }
}
