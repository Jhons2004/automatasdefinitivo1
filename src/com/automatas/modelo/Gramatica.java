package com.automatas.modelo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gramática regular (lineal por la derecha).
 *
 * <p>Definición formal: una gramática es una cuádrupla G = (N, Σ, P, S)
 * donde:</p>
 * <ul>
 *   <li>N es el conjunto de <b>no terminales</b> ({@link #noTerminales}).</li>
 *   <li>Σ es el conjunto de <b>terminales</b> ({@link #terminales}).</li>
 *   <li>P es el conjunto de <b>producciones</b> ({@link #producciones}),
 *       de la forma A → α con A ∈ N y α una secuencia de terminales y no
 *       terminales (en una gramática regular, α es de la forma
 *       {@code aB}, {@code a} o {@code epsilon}).</li>
 *   <li>S ∈ N es el <b>símbolo inicial</b> ({@link #inicial}).</li>
 * </ul>
 *
 * <p>Esta clase es solo el <b>modelo</b>: guarda los datos y hace cumplir las
 * reglas del enunciado (sin no terminales ni terminales repetidos, sin
 * símbolos que sean a la vez terminal y no terminal, símbolo inicial
 * existente y producciones no repetidas). La validación de cadenas vive en
 * {@code com.automatas.logica.ValidadorGramatica} y la conversión a AFD en
 * {@code com.automatas.logica.Convertidor}.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class Gramatica {

    /** Nombre con el que se identifica la gramática dentro de la aplicación. */
    private final String nombre;

    /** N: no terminales, en el orden en que se agregaron. */
    private final Set<String> noTerminales = new LinkedHashSet<>();

    /** Σ: terminales, en el orden en que se agregaron. */
    private final Set<String> terminales = new LinkedHashSet<>();

    /** S: no terminal inicial (debe pertenecer a N). */
    private String inicial;

    /** P: producciones de la gramática. */
    private final List<Produccion> producciones = new ArrayList<>();

    /**
     * Crea una gramática vacía con el nombre dado.
     *
     * @param nombre nombre de la gramática (no nulo ni vacío)
     */
    public Gramatica(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la gramática no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public Set<String> getNoTerminales() {
        return noTerminales;
    }

    public Set<String> getTerminales() {
        return terminales;
    }

    public String getInicial() {
        return inicial;
    }

    public List<Produccion> getProducciones() {
        return producciones;
    }

    /**
     * Agrega un no terminal a N.
     *
     * <p>Reglas del enunciado: no se repiten no terminales y un no terminal
     * no puede ser igual a un terminal del lenguaje.</p>
     */
    public void agregarNoTerminal(String nt) {
        validarNombre(nt, "no terminal");
        if (noTerminales.contains(nt)) {
            throw new IllegalArgumentException("El no terminal '" + nt + "' ya existe.");
        }
        if (terminales.contains(nt)) {
            throw new IllegalArgumentException(
                    "El no terminal '" + nt + "' no puede ser igual a un terminal del lenguaje.");
        }
        noTerminales.add(nt);
    }

    /**
     * Agrega un terminal a Σ.
     *
     * <p>Reglas del enunciado: no se repiten terminales y un terminal no
     * puede ser igual a un no terminal. La palabra "epsilon" está reservada
     * para la producción vacía y no puede ser un terminal.</p>
     */
    public void agregarTerminal(String terminal) {
        validarNombre(terminal, "terminal");
        if (Constantes.EPSILON.equals(terminal)) {
            throw new IllegalArgumentException(
                    "La palabra 'epsilon' está reservada para la producción vacía.");
        }
        if (terminales.contains(terminal)) {
            throw new IllegalArgumentException("El terminal '" + terminal + "' ya existe.");
        }
        if (noTerminales.contains(terminal)) {
            throw new IllegalArgumentException(
                    "El terminal '" + terminal + "' no puede ser igual a un no terminal del lenguaje.");
        }
        terminales.add(terminal);
    }

    /**
     * Define el no terminal inicial S.
     *
     * <p>Debe existir en N. Si ya había uno y se escribe otro, se borra el
     * anterior y se toma como inicial el último escrito (regla del
     * enunciado).</p>
     */
    public void setInicial(String nt) {
        validarNombre(nt, "no terminal inicial");
        if (!noTerminales.contains(nt)) {
            throw new IllegalArgumentException(
                    "El no terminal inicial '" + nt + "' no existe en los no terminales declarados.");
        }
        this.inicial = nt;
    }

    /**
     * Agrega una producción a P.
     *
     * <p>Reglas: el lado izquierdo debe ser un no terminal declarado, cada
     * símbolo del lado derecho debe ser un terminal declarado, un no terminal
     * declarado o "epsilon", y no se aceptan producciones repetidas.</p>
     *
     * @param produccion producción a agregar
     * @throws IllegalArgumentException si se viola alguna regla
     */
    public void agregarProduccion(Produccion produccion) {
        if (produccion == null) {
            throw new IllegalArgumentException("La producción no puede ser nula.");
        }
        if (!noTerminales.contains(produccion.getIzquierdo())) {
            throw new IllegalArgumentException(
                    "El no terminal '" + produccion.getIzquierdo() + "' no está declarado.");
        }
        for (String s : produccion.getDerecho()) {
            if (Constantes.EPSILON.equals(s)) {
                continue;
            }
            if (!terminales.contains(s) && !noTerminales.contains(s)) {
                throw new IllegalArgumentException(
                        "El símbolo '" + s + "' del lado derecho no está declarado "
                        + "(no es terminal ni no terminal).");
            }
        }
        if (producciones.contains(produccion)) {
            throw new IllegalArgumentException(
                    "La producción '" + produccion + "' ya existe (producción repetida).");
        }
        producciones.add(produccion);
    }

    /**
     * Agrega una producción escrita como texto, con la notación del
     * enunciado: {@code A > a B} o {@code A > a B | b}.
     *
     * <p>Si el lado derecho trae disyunción ({@code |}), se separa en una
     * producción por cada alternativa (las dos formas de disyunción del
     * enunciado son equivalentes).</p>
     *
     * @param textoProduccion texto completo, por ejemplo "A > a B | b"
     * @return lista de producciones agregadas (una por alternativa)
     * @throws IllegalArgumentException si el formato es inválido o se viola
     *         alguna regla de {@link #agregarProduccion(Produccion)}
     */
    public List<Produccion> agregarProduccionTexto(String textoProduccion) {
        if (textoProduccion == null || textoProduccion.trim().isEmpty()) {
            throw new IllegalArgumentException("La producción no puede estar vacía.");
        }
        String[] lados = textoProduccion.split(">", 2);
        if (lados.length != 2) {
            throw new IllegalArgumentException(
                    "Formato inválido. Use: <NT> > <símbolos separados por espacio>. "
                    + "Ejemplo: A > a B");
        }
        String izquierdo = lados[0].trim();
        String derecho = lados[1].trim();
        if (izquierdo.isEmpty() || derecho.isEmpty()) {
            throw new IllegalArgumentException(
                    "La producción debe tener lado izquierdo y lado derecho. Ejemplo: A > a B");
        }
        List<Produccion> agregadas = new ArrayList<>();
        // Disyunción: cada alternativa es una producción independiente.
        String[] alternativas = derecho.split("\\|");
        for (String alternativa : alternativas) {
            Produccion p = Produccion.desdeTexto(izquierdo, alternativa.trim());
            agregarProduccion(p);
            agregadas.add(p);
        }
        return agregadas;
    }

    /**
     * Devuelve las producciones cuyo lado izquierdo es el no terminal dado,
     * en el orden en que se agregaron.
     */
    public List<Produccion> produccionesDe(String nt) {
        List<Produccion> resultado = new ArrayList<>();
        for (Produccion p : producciones) {
            if (p.getIzquierdo().equals(nt)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /** Valida que un nombre no sea nulo ni vacío. */
    private void validarNombre(String valor, String queEs) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + queEs + " no puede estar vacío.");
        }
    }

    /**
     * Serializa la gramática al formato de archivo .gtk del enunciado.
     *
     * <p>Una producción por línea, en forma compacta: {@code A>Ab},
     * {@code A>bC}, {@code B>c}, {@code C>d}. La primera línea corresponde
     * al no terminal inicial (el archivo toma como inicial el no terminal
     * que viene al inicio).</p>
     *
     * @return contenido del archivo .gtk
     */
    public String serializarGtk() {
        StringBuilder sb = new StringBuilder();
        List<Produccion> ordenadas = new ArrayList<>(producciones);
        // La primera línea debe ser del no terminal inicial (regla del .gtk).
        ordenadas.sort((a, b) -> {
            boolean aEsInicial = a.getIzquierdo().equals(inicial);
            boolean bEsInicial = b.getIzquierdo().equals(inicial);
            return Boolean.compare(bEsInicial, aEsInicial);
        });
        for (Produccion p : ordenadas) {
            sb.append(p.aFormatoGtk()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     * Detalle completo de la gramática (opción "Ver Detalle" del menú de
     * Reportes): no terminales, terminales, símbolo inicial y producciones
     * con la notación del enunciado.
     *
     * @return detalle en texto plano
     */
    public String detalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("Gramática: ").append(nombre).append(System.lineSeparator());
        sb.append("No Terminales (N): ").append(unir(noTerminales)).append(System.lineSeparator());
        sb.append("Terminales (Σ): ").append(unir(terminales)).append(System.lineSeparator());
        sb.append("Símbolo inicial (S): ")
          .append(inicial == null ? "(sin definir)" : inicial)
          .append(System.lineSeparator());
        sb.append("Producciones (P):").append(System.lineSeparator());
        if (producciones.isEmpty()) {
            sb.append("  (sin producciones)").append(System.lineSeparator());
        }
        for (Produccion p : producciones) {
            sb.append("  ").append(p).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** Une un conjunto con ", " para mostrarlo. */
    private String unir(Set<String> conjunto) {
        return String.join(", ", conjunto);
    }
}
