package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Constantes;
import com.automatas.modelo.Gramatica;
import com.automatas.modelo.Produccion;
import com.automatas.modelo.Transicion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Conversión entre gramáticas regulares y AFD.
 *
 * <p>El enunciado pide que el programa pueda empezar por cualquiera de los
 * dos lados: crear una gramática y generar su AFD, o crear un AFD y generar
 * su gramática. Ambas conversiones preservan el lenguaje generado /
 * aceptado.</p>
 *
 * <h2>Gramática regular → AFD</h2>
 * <p>Para una gramática regular (producciones de la forma A → aB, A → a o
 * A → ε): los estados del AFD son los no terminales más un estado final
 * auxiliar QF; el alfabeto es el conjunto de terminales; el estado inicial
 * es el símbolo inicial de la gramática; y por cada producción:</p>
 * <ul>
 *   <li>A → aB genera la transición δ(A, a) = B;</li>
 *   <li>A → a genera la transición δ(A, a) = QF;</li>
 *   <li>A → ε hace que A sea estado de aceptación (QF siempre lo es).</li>
 * </ul>
 * <p>La conversión exige que la gramática sea <b>determinista</b>: no puede
 * haber dos producciones A → aB y A → aC con B ≠ C, porque eso produciría
 * dos transiciones con el mismo símbolo desde el mismo estado (un AFN). En
 * ese caso se lanza una excepción explicando el motivo.</p>
 *
 * <h2>AFD → Gramática regular</h2>
 * <p>Los no terminales son los estados del AFD, los terminales son el
 * alfabeto, el símbolo inicial es el estado inicial, y por cada transición
 * δ(A, a) = B se crea la producción A → aB; además, si B es de aceptación
 * se agrega A → a; y si el estado inicial es de aceptación, S → ε. Esta es
 * la construcción clásica y genera exactamente L(AFD).</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class Convertidor {

    private Convertidor() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Convierte una gramática regular en su AFD equivalente.
     *
     * @param gramatica gramática regular (determinista) a convertir
     * @return el AFD equivalente, llamado {@code <nombre>_AFD}
     * @throws IllegalArgumentException si la gramática no es regular
     *         (producciones con forma distinta de A → aB | a | ε) o no es
     *         determinista
     */
    public static AFD gramaticaAAfd(Gramatica gramatica) {
        if (gramatica == null) {
            throw new IllegalArgumentException("La gramática no puede ser nula.");
        }
        if (gramatica.getInicial() == null) {
            throw new IllegalArgumentException(
                    "La gramática no tiene no terminal inicial definido.");
        }
        AFD afd = new AFD(gramatica.getNombre() + "_AFD");
        for (String nt : gramatica.getNoTerminales()) {
            afd.agregarEstado(nt);
        }
        // Estado final auxiliar QF (con nombre libre si QF ya existe).
        String qf = Constantes.ESTADO_FINAL_AUXILIAR;
        int sufijo = 1;
        while (gramatica.getNoTerminales().contains(qf)) {
            qf = Constantes.ESTADO_FINAL_AUXILIAR + sufijo++;
        }
        afd.agregarEstado(qf);
        for (String t : gramatica.getTerminales()) {
            afd.agregarSimbolo(t);
        }
        afd.setEstadoInicial(gramatica.getInicial());
        afd.agregarEstadoAceptacion(qf);

        for (Produccion p : gramatica.getProducciones()) {
            String a = p.getIzquierdo();
            if (p.esEpsilon()) {
                // A → ε: A es estado de aceptación.
                afd.agregarEstadoAceptacion(a);
                continue;
            }
            List<String> der = p.getDerecho();
            if (der.size() == 1 && gramatica.getTerminales().contains(der.get(0))) {
                // A → a: δ(A, a) = QF.
                agregarTransicionSegura(afd, a, qf, der.get(0), gramatica);
            } else if (der.size() == 2
                    && gramatica.getTerminales().contains(der.get(0))
                    && gramatica.getNoTerminales().contains(der.get(1))) {
                // A → aB: δ(A, a) = B.
                agregarTransicionSegura(afd, a, der.get(1), der.get(0), gramatica);
            } else {
                throw new IllegalArgumentException(
                        "La producción '" + p + "' no es regular. Para convertir a AFD "
                        + "las producciones deben tener la forma A → aB, A → a o A → epsilon.");
            }
        }
        return afd;
    }

    /**
     * Agrega una transición controlando el determinismo: si ya existe una
     * transición desde el mismo estado con el mismo símbolo hacia otro
     * destino, la gramática no es determinista y no se puede convertir
     * directamente en un AFD.
     */
    private static void agregarTransicionSegura(AFD afd, String origen, String destino,
            String simbolo, Gramatica gramatica) {
        Transicion existente = afd.buscarTransicion(origen, simbolo);
        if (existente != null && !existente.getDestino().equals(destino)) {
            throw new IllegalArgumentException(
                    "La gramática no es determinista: desde '" + origen
                    + "' salen dos producciones con el terminal '" + simbolo
                    + "' hacia destinos distintos. Eso generaría un AFN, "
                    + "no un AFD.");
        }
        if (existente == null) {
            afd.agregarTransicion(origen, destino, simbolo);
        }
    }

    /**
     * Convierte un AFD en su gramática regular equivalente.
     *
     * @param afd AFD a convertir
     * @return la gramática equivalente, llamada {@code <nombre>_G}
     */
    public static Gramatica afdAGramatica(AFD afd) {
        if (afd == null) {
            throw new IllegalArgumentException("El AFD no puede ser nulo.");
        }
        if (afd.getEstadoInicial() == null) {
            throw new IllegalArgumentException("El AFD no tiene estado inicial definido.");
        }
        Gramatica gramatica = new Gramatica(afd.getNombre() + "_G");
        for (String estado : afd.getEstados()) {
            gramatica.agregarNoTerminal(estado);
        }
        for (String simbolo : afd.getAlfabeto()) {
            gramatica.agregarTerminal(simbolo);
        }
        gramatica.setInicial(afd.getEstadoInicial());

        for (Transicion t : afd.getTransiciones()) {
            // δ(A, a) = B  →  A → aB
            gramatica.agregarProduccion(new Produccion(t.getOrigen(),
                    lista(t.getSimbolo(), t.getDestino())));
            // Si B es de aceptación, también A → a.
            if (afd.esEstadoAceptacion(t.getDestino())) {
                Produccion corta = new Produccion(t.getOrigen(),
                        Collections.singletonList(t.getSimbolo()));
                if (!gramatica.getProducciones().contains(corta)) {
                    gramatica.agregarProduccion(corta);
                }
            }
        }
        // Si el estado inicial es de aceptación, S → ε (cadena vacía válida).
        if (afd.esEstadoAceptacion(afd.getEstadoInicial())) {
            gramatica.agregarProduccion(new Produccion(afd.getEstadoInicial(),
                    Collections.singletonList(Constantes.EPSILON)));
        }
        return gramatica;
    }

    /** Lista auxiliar de dos elementos. */
    private static List<String> lista(String a, String b) {
        List<String> l = new ArrayList<>();
        l.add(a);
        l.add(b);
        return l;
    }
}
