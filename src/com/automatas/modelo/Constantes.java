package com.automatas.modelo;

/**
 * Constantes compartidas por todo el proyecto.
 *
 * <p>Centraliza los símbolos reservados del enunciado para que ningún módulo
 * use literales sueltos y sea fácil cambiarlos en un solo lugar.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class Constantes {

    /** Palabra que representa la cadena vacía (ε) en las producciones. */
    public static final String EPSILON = "epsilon";

    /** Símbolo que indica "sin transición" en el modo 2 (matriz) del AFD. */
    public static final String SIN_TRANSICION = "-";

    /** Nombre del estado final auxiliar que se crea al convertir gramática → AFD. */
    public static final String ESTADO_FINAL_AUXILIAR = "QF";

    /** Longitud máxima de cadena que se explora al generar ejemplos. */
    public static final int LONGITUD_MAXIMA_EJEMPLOS = 6;

    /** Máximo de pasos de derivación al validar una cadena en una gramática. */
    public static final int MAXIMO_PASOS_DERIVACION = 2000;

    private Constantes() {
        // Clase de constantes: no se instancia.
    }
}
