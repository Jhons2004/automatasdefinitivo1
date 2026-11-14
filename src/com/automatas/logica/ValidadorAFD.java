package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Transicion;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador de cadenas para un Autómata Finito Determinista (AFD).
 *
 * <p>Algoritmo: simulación directa de δ. Se parte del estado inicial q0 y se
 * consume la cadena símbolo a símbolo; en cada paso se sigue la única
 * transición δ(estadoActual, símbolo). Al terminar, la cadena es válida si
 * y solo si el estado final pertenece a F. Complejidad O(n) con n = número
 * de símbolos de la cadena.</p>
 *
 * <p>La cadena se divide en símbolos del alfabeto buscando, en cada posición,
 * el símbolo más largo que sea prefijo del resto de la cadena. Con alfabetos
 * de un solo carácter (0, 1, a, b, ...) esto equivale a leer carácter por
 * carácter. Si un trozo no coincide con ningún símbolo del alfabeto, la
 * cadena es inválida.</p>
 *
 * <p>La ruta se muestra con el formato del enunciado: el estado del que se
 * parte, el estado al que se llega y el símbolo, separados por coma; y un
 * punto y coma entre cada movimiento. Ejemplo:
 * {@code Ruta en AFD: A, A, a; A, A, a; A, C, b; C, B, a; B, C, b; C, D, b}
 * </p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class ValidadorAFD {

    private ValidadorAFD() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Valida una cadena en el AFD dado.
     *
     * @param afd AFD contra el que se valida (debe tener estado inicial)
     * @param cadena cadena a validar (puede ser vacía)
     * @return resultado con la ruta y el veredicto
     */
    public static ResultadoEvaluacion validar(AFD afd, String cadena) {
        if (afd == null) {
            throw new IllegalArgumentException("El AFD no puede ser nulo.");
        }
        if (cadena == null) {
            cadena = "";
        }
        List<String> pasos = new ArrayList<>();
        if (afd.getEstadoInicial() == null) {
            return ResultadoEvaluacion.invalida("",
                    "el AFD no tiene estado inicial definido.", pasos);
        }

        String actual = afd.getEstadoInicial();
        int posicion = 0;

        // Cadena vacía: válida solo si el estado inicial es de aceptación.
        if (cadena.isEmpty()) {
            String ruta = "(cadena vacía: se permanece en el estado inicial " + actual + ")";
            if (afd.esEstadoAceptacion(actual)) {
                return ResultadoEvaluacion.valida(ruta, pasos);
            }
            return ResultadoEvaluacion.invalida(ruta,
                    "la cadena vacía solo es válida si el estado inicial es de aceptación.",
                    pasos);
        }

        while (posicion < cadena.length()) {
            // Se busca el símbolo del alfabeto que coincida en esta posición
            // (el más largo primero, para alfabetos con símbolos de varios caracteres).
            String simbolo = buscarSimbolo(afd, cadena, posicion);
            if (simbolo == null) {
                String ruta = String.join("; ", pasos);
                return ResultadoEvaluacion.invalida(ruta,
                        "el carácter '" + cadena.charAt(posicion) + "' (posición " + posicion
                        + ") no pertenece al alfabeto del AFD.", pasos);
            }
            Transicion t = afd.buscarTransicion(actual, simbolo);
            if (t == null) {
                String ruta = String.join("; ", pasos);
                return ResultadoEvaluacion.invalida(ruta,
                        "no existe transición desde el estado '" + actual
                        + "' con el símbolo '" + simbolo + "'.", pasos);
            }
            // Paso de la ruta: "origen, destino, símbolo".
            pasos.add(t.getOrigen() + ", " + t.getDestino() + ", " + t.getSimbolo());
            actual = t.getDestino();
            posicion += simbolo.length();
        }

        String ruta = String.join("; ", pasos);
        if (afd.esEstadoAceptacion(actual)) {
            return ResultadoEvaluacion.valida(ruta, pasos);
        }
        return ResultadoEvaluacion.invalida(ruta,
                "la cadena termina en el estado '" + actual
                + "', que no es de aceptación.", pasos);
    }

    /**
     * Busca, en la posición dada de la cadena, el símbolo del alfabeto que
     * coincida. Se prueba primero el símbolo más largo para que un alfabeto
     * con símbolos de varios caracteres no se parta por error.
     *
     * @return el símbolo encontrado, o {@code null} si ninguno coincide
     */
    private static String buscarSimbolo(AFD afd, String cadena, int posicion) {
        String mejor = null;
        for (String simbolo : afd.getAlfabeto()) {
            if (cadena.startsWith(simbolo, posicion)) {
                if (mejor == null || simbolo.length() > mejor.length()) {
                    mejor = simbolo;
                }
            }
        }
        return mejor;
    }
}
