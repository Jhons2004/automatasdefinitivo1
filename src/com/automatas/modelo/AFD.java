package com.automatas.modelo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Autómata Finito Determinista (AFD).
 *
 * <p>Definición formal: un AFD es una quíntupla M = (Q, Σ, δ, q<sub>0</sub>, F)
 * donde:</p>
 * <ul>
 *   <li>Q es el conjunto finito de <b>estados</b> ({@link #estados}).</li>
 *   <li>Σ es el <b>alfabeto</b> del lenguaje ({@link #alfabeto}).</li>
 *   <li>δ: Q × Σ → Q es la <b>función de transición</b>
 *       ({@link #transiciones}); es determinista: para cada par
 *       (estado, símbolo) existe a lo sumo un destino.</li>
 *   <li>q<sub>0</sub> ∈ Q es el <b>estado inicial</b>
 *       ({@link #estadoInicial}).</li>
 *   <li>F ⊆ Q es el conjunto de <b>estados de aceptación</b>
 *       ({@link #estadosAceptacion}).</li>
 * </ul>
 *
 * <p>Esta clase es solo el <b>modelo</b>: guarda los datos y hace cumplir las
 * reglas del enunciado (sin estados repetidos, sin estados iguales a un
 * símbolo del alfabeto, estado inicial y de aceptación existentes, y
 * transiciones deterministas). La validación de cadenas vive en
 * {@code com.automatas.logica.ValidadorAFD} y la conversión a gramática en
 * {@code com.automatas.logica.Convertidor}.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class AFD {

    /** Nombre con el que se identifica el AFD dentro de la aplicación. */
    private final String nombre;

    /** Q: conjunto de estados, en el orden en que se agregaron. */
    private final Set<String> estados = new LinkedHashSet<>();

    /** Σ: alfabeto del lenguaje, en el orden en que se agregó. */
    private final Set<String> alfabeto = new LinkedHashSet<>();

    /** q0: estado inicial (debe pertenecer a Q). */
    private String estadoInicial;

    /** F: estados de aceptación (subconjunto de Q). */
    private final Set<String> estadosAceptacion = new LinkedHashSet<>();

    /** δ: lista de transiciones del autómata. */
    private final List<Transicion> transiciones = new ArrayList<>();

    /**
     * Crea un AFD vacío con el nombre dado.
     *
     * @param nombre nombre del AFD (no nulo ni vacío)
     */
    public AFD(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del AFD no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    /** Nombre con el que se identifica el AFD dentro de la aplicación. */
    public String getNombre() {
        return nombre;
    }

    /** Q: conjunto de estados, en el orden en que se agregaron. */
    public Set<String> getEstados() {
        return estados;
    }

    /** Σ: alfabeto del lenguaje, en el orden en que se agregó. */
    public Set<String> getAlfabeto() {
        return alfabeto;
    }

    /** Estado inicial (q0), o {@code null} si aún no se definió. */
    public String getEstadoInicial() {
        return estadoInicial;
    }

    /** F: conjunto de estados de aceptación. */
    public Set<String> getEstadosAceptacion() {
        return estadosAceptacion;
    }

    /** Transiciones (δ), en el orden en que se agregaron. */
    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    /**
     * Agrega un estado a Q.
     *
     * <p>Reglas del enunciado: no se repiten estados y un estado no puede ser
     * igual a un símbolo del alfabeto.</p>
     *
     * @param estado nombre del estado
     * @throws IllegalArgumentException si el estado ya existe o coincide con
     *         un símbolo del alfabeto
     */
    public void agregarEstado(String estado) {
        validarNombre(estado, "estado");
        if (estados.contains(estado)) {
            throw new IllegalArgumentException("El estado '" + estado + "' ya existe.");
        }
        if (alfabeto.contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado '" + estado + "' no puede ser igual a un símbolo del alfabeto.");
        }
        estados.add(estado);
    }

    /**
     * Agrega un símbolo a Σ.
     *
     * <p>Reglas del enunciado: no se repiten símbolos y un símbolo no puede
     * ser igual a un estado. Además "epsilon" no es un símbolo del alfabeto:
     * en un AFD no existen transiciones vacías.</p>
     *
     * @param simbolo símbolo del alfabeto
     * @throws IllegalArgumentException si el símbolo ya existe, coincide con
     *         un estado o es "epsilon"
     */
    public void agregarSimbolo(String simbolo) {
        validarNombre(simbolo, "símbolo");
        if (Constantes.EPSILON.equals(simbolo)) {
            throw new IllegalArgumentException(
                    "El símbolo 'epsilon' no pertenece al alfabeto de un AFD.");
        }
        if (alfabeto.contains(simbolo)) {
            throw new IllegalArgumentException("El símbolo '" + simbolo + "' ya existe en el alfabeto.");
        }
        if (estados.contains(simbolo)) {
            throw new IllegalArgumentException(
                    "El símbolo '" + simbolo + "' no puede ser igual a un estado del AFD.");
        }
        alfabeto.add(simbolo);
    }

    /**
     * Define el estado inicial q0.
     *
     * <p>Debe existir en Q. Si ya había un estado inicial y se escribe otro,
     * se borra el anterior y se toma como inicial el último escrito
     * (regla del enunciado).</p>
     *
     * @param estado estado inicial
     * @throws IllegalArgumentException si el estado no está declarado
     */
    public void setEstadoInicial(String estado) {
        validarNombre(estado, "estado inicial");
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado inicial '" + estado + "' no existe en los estados declarados.");
        }
        this.estadoInicial = estado;
    }

    /**
     * Agrega un estado de aceptación a F. Debe existir en Q.
     *
     * @param estado estado de aceptación
     * @throws IllegalArgumentException si el estado no está declarado
     */
    public void agregarEstadoAceptacion(String estado) {
        validarNombre(estado, "estado de aceptación");
        if (!estados.contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado de aceptación '" + estado + "' no existe en los estados declarados.");
        }
        estadosAceptacion.add(estado);
    }

    /** Quita un estado del conjunto de aceptación (sin error si no estaba). */
    public void quitarEstadoAceptacion(String estado) {
        estadosAceptacion.remove(estado);
    }

    /** Indica si un estado pertenece a F. */
    public boolean esEstadoAceptacion(String estado) {
        return estadosAceptacion.contains(estado);
    }

    /**
     * Agrega una transición δ(origen, símbolo) = destino.
     *
     * <p>Reglas del enunciado: origen y destino deben existir en Q, el
     * símbolo debe existir en Σ y no puede haber dos transiciones con el
     * mismo símbolo saliendo del mismo estado (eso solo es posible en un AFN).
     * Tampoco se aceptan transiciones con epsilon.</p>
     *
     * @param origen estado de partida
     * @param destino estado de llegada
     * @param simbolo símbolo del alfabeto
     * @throws IllegalArgumentException si se viola alguna regla
     */
    public void agregarTransicion(String origen, String destino, String simbolo) {
        validarNombre(origen, "estado de origen");
        validarNombre(destino, "estado de destino");
        validarNombre(simbolo, "símbolo");
        if (!estados.contains(origen)) {
            throw new IllegalArgumentException(
                    "El estado de origen '" + origen + "' no existe en los estados declarados.");
        }
        if (!estados.contains(destino)) {
            throw new IllegalArgumentException(
                    "El estado de destino '" + destino + "' no existe en los estados declarados.");
        }
        if (!alfabeto.contains(simbolo)) {
            throw new IllegalArgumentException(
                    "El símbolo '" + simbolo + "' no existe en el alfabeto declarado.");
        }
        Transicion nueva = new Transicion(origen, destino, simbolo);
        if (transiciones.contains(nueva)) {
            throw new IllegalArgumentException(
                    "Ya existe una transición desde '" + origen + "' con el símbolo '" + simbolo
                    + "'. Sacar dos símbolos iguales desde el mismo estado solo es posible "
                    + "con un AFN (no determinista).");
        }
        transiciones.add(nueva);
    }

    /**
     * Busca la transición δ(origen, símbolo).
     *
     * @param origen estado de partida
     * @param simbolo símbolo del alfabeto
     * @return la transición, o {@code null} si no existe (δ parcial: en ese
     *         caso una cadena que la necesite se considera inválida)
     */
    public Transicion buscarTransicion(String origen, String simbolo) {
        for (Transicion t : transiciones) {
            if (t.getOrigen().equals(origen) && t.getSimbolo().equals(simbolo)) {
                return t;
            }
        }
        return null;
    }

    /** Valida que un nombre no sea nulo ni vacío (después de quitar espacios). */
    private void validarNombre(String valor, String queEs) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + queEs + " no puede estar vacío.");
        }
    }

    /**
     * Serializa el AFD al formato de archivo .afd del enunciado.
     *
     * <p>Cada línea tiene la forma {@code A,B,0;false,true}: estado de partida,
     * estado de llegada, símbolo, y luego las banderas de aceptación del
     * estado de partida y del estado de llegada, en ese orden. Las líneas se
     * ordenan para que la primera empiece por el estado inicial (el archivo
     * toma como inicial el estado que esté al inicio).</p>
     *
     * @return contenido del archivo .afd
     */
    public String serializarAfd() {
        StringBuilder sb = new StringBuilder();
        List<Transicion> ordenadas = new ArrayList<>(transiciones);
        // La primera línea debe partir del estado inicial (regla del .afd).
        ordenadas.sort((a, b) -> {
            boolean aEsInicial = a.getOrigen().equals(estadoInicial);
            boolean bEsInicial = b.getOrigen().equals(estadoInicial);
            return Boolean.compare(bEsInicial, aEsInicial);
        });
        for (Transicion t : ordenadas) {
            sb.append(t.getOrigen()).append(",")
                .append(t.getDestino()).append(",")
                .append(t.getSimbolo()).append(";")
                .append(esEstadoAceptacion(t.getOrigen())).append(",")
                .append(esEstadoAceptacion(t.getDestino()))
                .append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Detalle completo del AFD (opción "Ver Detalle" del menú de Reportes):
     * alfabeto, estados, estado inicial, estados de aceptación y
     * transiciones con la notación del enunciado.
     *
     * @return detalle en texto plano
     */
    public String detalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("AFD: ").append(nombre).append(System.lineSeparator());
        sb.append("Alfabeto (Σ): ").append(unir(alfabeto)).append(System.lineSeparator());
        sb.append("Estados (Q): ").append(unir(estados)).append(System.lineSeparator());
        sb.append("Estado inicial (q0): ")
            .append(estadoInicial == null ? "(sin definir)" : estadoInicial)
            .append(System.lineSeparator());
        sb.append("Estados de aceptación (F): ")
            .append(estadosAceptacion.isEmpty() ? "(ninguno)" : unir(estadosAceptacion))
            .append(System.lineSeparator());
        sb.append("Transiciones (δ):").append(System.lineSeparator());
        if (transiciones.isEmpty()) {
            sb.append("  (sin transiciones)").append(System.lineSeparator());
        }
        for (Transicion t : transiciones) {
            sb.append("  ").append(t)
                .append(";").append(esEstadoAceptacion(t.getOrigen()))
                .append(",").append(esEstadoAceptacion(t.getDestino()))
                .append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** Une un conjunto con ", " para mostrarlo. */
    private String unir(Set<String> conjunto) {
        return String.join(", ", conjunto);
    }
}
