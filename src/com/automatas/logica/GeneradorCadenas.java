package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Constantes;
import com.automatas.modelo.Gramatica;
import java.util.ArrayList;
import java.util.List;

/**
 * Generador de cadenas de ejemplo para los reportes.
 *
 * <p>El menú de Reportes debe incluir al menos 3 cadenas válidas y 3 cadenas
 * inválidas para la gramática o AFD. Este generador las obtiene por
 * <b>enumeración por longitud creciente</b> (BFS sobre Σ*): genera todas las
 * cadenas de longitud 0, 1, 2, ... sobre el alfabeto, las valida con el
 * validador correspondiente y se queda con las primeras N válidas y las
 * primeras N inválidas.</p>
 *
 * <p>La enumeración está acotada (longitud máxima y número máximo de
 * candidatos) para que no se dispare con alfabetos grandes.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class GeneradorCadenas {

    /** Máximo de cadenas candidatas que se prueban en total. */
    private static final int MAXIMO_CANDIDATOS = 5000;

    private GeneradorCadenas() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Genera hasta {@code cantidad} cadenas válidas para el AFD dado.
     *
     * @param afd AFD del que se generan ejemplos
     * @param cantidad cuántas cadenas válidas se buscan
     * @return lista de cadenas válidas (puede tener menos si no hay tantas)
     */
    public static List<String> validasParaAfd(AFD afd, int cantidad) {
        return generar(afd.getAlfabeto(), cantidad, true,
                cadena -> ValidadorAFD.validar(afd, cadena).esValida());
    }

    /**
     * Genera hasta {@code cantidad} cadenas inválidas para el AFD dado.
     */
    public static List<String> invalidasParaAfd(AFD afd, int cantidad) {
        return generar(afd.getAlfabeto(), cantidad, false,
                cadena -> ValidadorAFD.validar(afd, cadena).esValida());
    }

    /**
     * Genera hasta {@code cantidad} cadenas válidas para la gramática dada.
     */
    public static List<String> validasParaGramatica(Gramatica gramatica, int cantidad) {
        return generar(gramatica.getTerminales(), cantidad, true,
                cadena -> ValidadorGramatica.validar(gramatica, cadena).esValida());
    }

    /**
     * Genera hasta {@code cantidad} cadenas inválidas para la gramática dada.
     */
    public static List<String> invalidasParaGramatica(Gramatica gramatica, int cantidad) {
        return generar(gramatica.getTerminales(), cantidad, false,
                cadena -> ValidadorGramatica.validar(gramatica, cadena).esValida());
    }

    /**
     * Enumeración por longitud creciente. Genera las cadenas de longitud 0,
     * luego 1, luego 2, ... y se queda con las primeras {@code cantidad} cuya
     * validez coincida con {@code buscarValidas}.
     */
    private static List<String> generar(java.util.Set<String> alfabeto, int cantidad,
            boolean buscarValidas, java.util.function.Predicate<String> esValida) {
        List<String> resultado = new ArrayList<>();
        List<String> simbolos = new ArrayList<>(alfabeto);
        if (simbolos.isEmpty() || cantidad <= 0) {
            return resultado;
        }
        int probadas = 0;
        // Longitud 0: la cadena vacía.
        if (esValida.test("") == buscarValidas) {
            resultado.add("(vacía)");
            if (resultado.size() >= cantidad) {
                return resultado;
            }
        }
        probadas++;
        for (int longitud = 1; longitud <= Constantes.LONGITUD_MAXIMA_EJEMPLOS
                && resultado.size() < cantidad && probadas < MAXIMO_CANDIDATOS; longitud++) {
            for (String cadena : cadenasDeLongitud(simbolos, longitud)) {
                if (probadas >= MAXIMO_CANDIDATOS || resultado.size() >= cantidad) {
                    break;
                }
                probadas++;
                if (esValida.test(cadena) == buscarValidas) {
                    resultado.add(cadena);
                }
            }
        }
        return resultado;
    }

    /** Genera todas las cadenas de una longitud dada sobre el alfabeto. */
    private static List<String> cadenasDeLongitud(List<String> simbolos, int longitud) {
        List<String> resultado = new ArrayList<>();
        generarRecursivo(simbolos, longitud, "", resultado);
        return resultado;
    }

    private static void generarRecursivo(List<String> simbolos, int faltan,
            String prefijo, List<String> resultado) {
        if (faltan == 0) {
            resultado.add(prefijo);
            return;
        }
        for (String s : simbolos) {
            generarRecursivo(simbolos, faltan - 1, prefijo + s, resultado);
        }
    }
}
