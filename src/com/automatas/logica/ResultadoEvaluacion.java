package com.automatas.logica;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de validar una cadena en un AFD o en una gramática.
 *
 * <p>Guarda si la cadena es válida, la ruta (AFD) o expansión (gramática)
 * que lo demuestra, y un mensaje explicativo para el usuario. La ruta y la
 * expansión usan exactamente los formatos del enunciado:</p>
 * <ul>
 *   <li>AFD: {@code Ruta en AFD: A, A, a; A, A, a; A, C, b; ...}</li>
 *   <li>Gramática: {@code Expansión Gramática: A>0B>00B>001A>0011A>0011(epsilon)>0011}</li>
 * </ul>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class ResultadoEvaluacion {

    /** true si la cadena pertenece al lenguaje; false en caso contrario. */
    private final boolean valida;

    /** Ruta (AFD) o expansión (gramática) que demuestra el resultado. */
    private final String recorrido;

    /** Mensaje para el usuario ("cadena válida" / "cadena inválida" + motivo). */
    private final String mensaje;

    /** Pasos individuales del recorrido (uno por símbolo consumido). */
    private final List<String> pasos;

    public ResultadoEvaluacion(boolean valida, String recorrido, String mensaje, List<String> pasos) {
        this.valida = valida;
        this.recorrido = recorrido == null ? "" : recorrido;
        this.mensaje = mensaje == null ? "" : mensaje;
        this.pasos = pasos == null ? new ArrayList<>() : new ArrayList<>(pasos);
    }

    /** Resultado de cadena válida con su recorrido. */
    public static ResultadoEvaluacion valida(String recorrido, List<String> pasos) {
        return new ResultadoEvaluacion(true, recorrido, "cadena válida", pasos);
    }

    /** Resultado de cadena inválida con el motivo. */
    public static ResultadoEvaluacion invalida(String recorrido, String motivo, List<String> pasos) {
        return new ResultadoEvaluacion(false, recorrido, "cadena inválida: " + motivo, pasos);
    }

    public boolean esValida() {
        return valida;
    }

    public String getRecorrido() {
        return recorrido;
    }

    public String getMensaje() {
        return mensaje;
    }

    public List<String> getPasos() {
        return pasos;
    }

    @Override
    public String toString() {
        return mensaje + (recorrido.isEmpty() ? "" : " | " + recorrido);
    }
}
