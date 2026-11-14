package com.automatas.modelo;

/**
 * Datos del curso y del autor, usados en un solo lugar para que la portada,
 * la ayuda, los reportes PDF y los encabezados de los archivos muestren
 * siempre los mismos datos.
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class DatosDelCurso {

    /** Nombre completo del autor del proyecto. */
    public static final String AUTOR = "Jonatan Abdiel Alvararado Villavicencio";

    /** Número de carné del autor. */
    public static final String CARNE = "0910-22-8487";

    /** Último dígito del carné (lo que muestra la Ayuda). */
    public static final String ULTIMO_DIGITO_CARNE = "7";

    /** Sección del curso. */
    public static final String SECCION = "3";

    /** Nombre del catedrático del curso. */
    public static final String CATEDRATICO = "Alan Gonzalo Ucelo Moran";

    /** Nombre del curso. */
    public static final String CURSO = "Autómatas y Lenguajes Formales";

    /** Ciclo del curso. */
    public static final String CICLO = "VI Ciclo";

    /** Facultad. */
    public static final String FACULTAD =
            "Facultad de Ingeniería en Sistemas de Información y Ciencias de la Computación";

    /** Universidad. */
    public static final String UNIVERSIDAD = "Universidad Mariano Gálvez de Guatemala";

    private DatosDelCurso() {
        // Clase de constantes: no se instancia.
    }
}
