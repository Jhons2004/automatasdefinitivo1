package com.automatas.modelo;

import java.util.Objects;

/**
 * Representa una transición de un Autómata Finito Determinista (AFD).
 *
 * <p>Una transición es una tripla (origen, símbolo, destino) que corresponde
 * a la función de transición δ del AFD: δ(origen, símbolo) = destino.</p>
 *
 * <p>En un AFD la función δ es <b>determinista</b>: desde un mismo estado no
 * pueden salir dos transiciones con el mismo símbolo. Por eso dos
 * transiciones se consideran iguales si comparten el mismo estado de origen
 * y el mismo símbolo, sin importar el destino (ver {@link #equals(Object)}).
 * Eso permite detectar el intento de crear un AFN y rechazarlo con el mensaje
 * que pide el enunciado.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class Transicion {

    /** Estado del que parte la transición. */
    private final String origen;

    /** Estado al que llega la transición. */
    private final String destino;

    /** Símbolo del alfabeto con el que se realiza la transición. */
    private final String simbolo;

    /**
     * Crea una transición δ(origen, símbolo) = destino.
     *
     * @param origen estado de partida (no nulo ni vacío)
     * @param destino estado de llegada (no nulo ni vacío)
     * @param simbolo símbolo del alfabeto (no nulo ni vacío, distinto de "epsilon")
     */
    public Transicion(String origen, String destino, String simbolo) {
        if (origen == null || origen.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de origen no puede estar vacío.");
        }
        if (destino == null || destino.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado de destino no puede estar vacío.");
        }
        if (simbolo == null || simbolo.trim().isEmpty()) {
            throw new IllegalArgumentException("El símbolo de la transición no puede estar vacío.");
        }
        if (Constantes.EPSILON.equals(simbolo.trim())) {
            throw new IllegalArgumentException(
                    "Un AFD no puede tener transiciones con epsilon. Eso solo es posible en un AFN.");
        }
        this.origen = origen.trim();
        this.destino = destino.trim();
        this.simbolo = simbolo.trim();
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public String getSimbolo() {
        return simbolo;
    }

    /**
     * Dos transiciones son iguales si parten del mismo estado con el mismo
     * símbolo. En un AFD eso es lo que define la unicidad de δ: si ya existe
     * una transición (A, 0) no se puede agregar otra (A, 0), aunque el
     * destino sea distinto, porque eso sería no determinismo (AFN).
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Transicion)) {
            return false;
        }
        Transicion otra = (Transicion) obj;
        return origen.equals(otra.origen) && simbolo.equals(otra.simbolo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origen, simbolo);
    }

    /**
     * Notación del enunciado para una transición: {@code A,B,0}.
     * (Las banderas de aceptación se agregan al serializar el AFD completo,
     * en {@link AFD#serializarAfd()}, no en la transición aislada.)
     */
    @Override
    public String toString() {
        return origen + "," + destino + "," + simbolo;
    }
}
