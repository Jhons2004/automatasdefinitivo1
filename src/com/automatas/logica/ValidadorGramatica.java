package com.automatas.logica;

import com.automatas.modelo.Constantes;
import com.automatas.modelo.Gramatica;
import com.automatas.modelo.Produccion;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Validador de cadenas para una gramática regular.
 *
 * <p>Algoritmo: <b>búsqueda en anchura (BFS) sobre derivaciones por la
 * izquierda</b>. Se parte de la forma sentencial inicial [S] y en cada paso
 * se sustituye el no terminal más a la izquierda por el lado derecho de una
 * de sus producciones. La búsqueda termina cuando se alcanza una forma
 * sentencial compuesta solo por terminales e igual a la cadena buscada.</p>
 *
 * <p>Podas (para que la búsqueda siempre termine):</p>
 * <ul>
 *   <li>el prefijo terminal de la forma sentencial debe coincidir con el
 *       prefijo de la cadena buscada;</li>
 *   <li>la forma sentencial no puede tener más terminales que la cadena;</li>
 *   <li>no se vuelve a expandir una forma sentencial ya visitada (BFS
 *       garantiza que la primera vez que se llega a una forma es por el
 *       camino más corto);</li>
 *   <li>límite de pasos de derivación ({@link Constantes#MAXIMO_PASOS_DERIVACION}).</li>
 * </ul>
 *
 * <p>La expansión se muestra con el formato del enunciado: cada forma
 * sentencial separada por {@code >}. Cuando se aplica una producción vacía,
 * el paso se muestra con el marcador {@code (epsilon)} y luego la forma sin
 * el no terminal. Ejemplo del enunciado para validar {@code 0011}:
 * {@code Expansión Gramática: A>0B>00B>001A>0011A>0011(epsilon)>0011}</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class ValidadorGramatica {

    private ValidadorGramatica() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Valida una cadena en la gramática dada.
     *
     * @param gramatica gramática contra la que se valida (debe tener símbolo inicial)
     * @param cadena cadena a validar (puede ser vacía)
     * @return resultado con la expansión y el veredicto
     */
    public static ResultadoEvaluacion validar(Gramatica gramatica, String cadena) {
        if (gramatica == null) {
            throw new IllegalArgumentException("La gramática no puede ser nula.");
        }
        if (cadena == null) {
            cadena = "";
        }
        List<String> pasos = new ArrayList<>();
        if (gramatica.getInicial() == null) {
            return ResultadoEvaluacion.invalida("",
                    "la gramática no tiene no terminal inicial definido.", pasos);
        }
        // La cadena solo puede tener símbolos del alfabeto (terminales).
        for (int i = 0; i < cadena.length(); i++) {
            String c = String.valueOf(cadena.charAt(i));
            if (!gramatica.getTerminales().contains(c)) {
                return ResultadoEvaluacion.invalida("",
                        "el carácter '" + c + "' (posición " + i
                        + ") no pertenece a los terminales de la gramática.", pasos);
            }
        }

        // Nodo de la búsqueda: forma sentencial + cadena de expansión.
        Queue<Nodo> cola = new ArrayDeque<>();
        Set<String> visitados = new HashSet<>();
        List<String> formaInicial = new ArrayList<>();
        formaInicial.add(gramatica.getInicial());
        cola.add(new Nodo(formaInicial, gramatica.getInicial()));
        visitados.add(clave(formaInicial));

        int pasosRealizados = 0;
        while (!cola.isEmpty() && pasosRealizados < Constantes.MAXIMO_PASOS_DERIVACION) {
            Nodo actual = cola.poll();
            pasosRealizados++;

            // ¿La forma actual es solo terminales e igual a la cadena?
            if (esSoloTerminales(gramatica, actual.forma)) {
                String generada = String.join("", actual.forma);
                if (generada.equals(cadena)) {
                    return ResultadoEvaluacion.valida(actual.expansion, pasos);
                }
                continue;
            }
            // Poda: el prefijo terminal debe coincidir con la cadena.
            if (!prefijoCoincide(gramatica, actual.forma, cadena)) {
                continue;
            }
            // Poda: no puede haber más terminales que en la cadena buscada.
            if (contarTerminales(gramatica, actual.forma) > cadena.length()) {
                continue;
            }

            // Derivación por la izquierda: expandir el primer no terminal.
            int indiceNt = indicePrimerNoTerminal(gramatica, actual.forma);
            if (indiceNt < 0) {
                continue;
            }
            String nt = actual.forma.get(indiceNt);
            for (Produccion p : gramatica.produccionesDe(nt)) {
                List<String> nuevaForma = new ArrayList<>(actual.forma);
                String expansion;
                if (p.esEpsilon()) {
                    // Paso con epsilon: se muestra el marcador (epsilon) y
                    // luego la forma sin el no terminal (como en el ejemplo
                    // del enunciado: ...>0011A>0011(epsilon)>0011).
                    List<String> conMarcador = new ArrayList<>(actual.forma);
                    conMarcador.set(indiceNt, "(epsilon)");
                    nuevaForma.remove(indiceNt);
                    if (nuevaForma.isEmpty()) {
                        expansion = actual.expansion + ">" + mostrar(conMarcador);
                    } else {
                        expansion = actual.expansion + ">" + mostrar(conMarcador)
                                + ">" + mostrar(nuevaForma);
                    }
                } else {
                    nuevaForma.remove(indiceNt);
                    nuevaForma.addAll(indiceNt, p.getDerecho());
                    expansion = actual.expansion + ">" + mostrar(nuevaForma);
                }
                // Poda por tamaño: la forma no puede crecer sin límite.
                if (nuevaForma.size() > cadena.length() + 5) {
                    continue;
                }
                String claveNueva = clave(nuevaForma);
                if (visitados.add(claveNueva)) {
                    cola.add(new Nodo(nuevaForma, expansion));
                }
            }
        }

        return ResultadoEvaluacion.invalida("",
                "no existe derivación del símbolo inicial que genere la cadena.",
                pasos);
    }

    /** Indica si todos los símbolos de la forma son terminales (o no hay ninguno). */
    private static boolean esSoloTerminales(Gramatica g, List<String> forma) {
        for (String s : forma) {
            if (g.getNoTerminales().contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Indica si el prefijo terminal de la forma (los terminales que van antes
     * del primer no terminal) coincide con el inicio de la cadena buscada.
     */
    private static boolean prefijoCoincide(Gramatica g, List<String> forma, String cadena) {
        StringBuilder prefijo = new StringBuilder();
        for (String s : forma) {
            if (g.getNoTerminales().contains(s)) {
                break;
            }
            prefijo.append(s);
        }
        return cadena.startsWith(prefijo.toString());
    }

    /** Cuenta cuántos símbolos terminales hay en la forma sentencial. */
    private static int contarTerminales(Gramatica g, List<String> forma) {
        int n = 0;
        for (String s : forma) {
            if (g.getTerminales().contains(s)) {
                n++;
            }
        }
        return n;
    }

    /** Índice del primer no terminal de la forma, o -1 si no hay ninguno. */
    private static int indicePrimerNoTerminal(Gramatica g, List<String> forma) {
        for (int i = 0; i < forma.size(); i++) {
            if (g.getNoTerminales().contains(forma.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /** Muestra una forma sentencial como en el enunciado (sin espacios). */
    private static String mostrar(List<String> forma) {
        if (forma.isEmpty()) {
            return "(epsilon)";
        }
        return String.join("", forma);
    }

    /** Clave única de una forma sentencial para el conjunto de visitados. */
    private static String clave(List<String> forma) {
        return String.join(" ", forma);
    }

    /** Nodo de la búsqueda en anchura. */
    private static final class Nodo {
        final List<String> forma;
        final String expansion;

        Nodo(List<String> forma, String expansion) {
            this.forma = forma;
            this.expansion = expansion;
        }
    }
}
