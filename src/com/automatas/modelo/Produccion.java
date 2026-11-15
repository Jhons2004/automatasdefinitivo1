package com.automatas.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa una producción de una gramática regular.
 *
 * <p>Notación del enunciado (basada en BNF, pero con {@code >} en lugar de
 * {@code ::=}): {@code <NT> > <expresión con símbolos>}. Ejemplo:
 * {@code A > a B} significa que el no terminal A deriva en el terminal
 * {@code a} seguido del no terminal B. El lado derecho puede ser:</p>
 * <ul>
 *   <li>una secuencia de terminales y no terminales separados por espacio
 *       (por ejemplo {@code a B}, {@code 0 B}),</li>
 *   <li>un solo terminal (por ejemplo {@code b}),</li>
 *   <li>la palabra {@code epsilon}, que representa la producción vacía
 *       (A deriva en la cadena vacía).</li>
 * </ul>
 *
 * <p>La disyunción ({@code |}) no se guarda como parte de la producción: al
 * ingresar {@code A > a B | b} la aplicación la separa en dos producciones
 * ({@code A > a B} y {@code A > b}), que es la segunda forma de disyunción
 * que permite el enunciado.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class Produccion {

    /** No terminal del lado izquierdo de la producción. */
    private final String izquierdo;

    /** Símbolos del lado derecho, en orden (["epsilon"] si es vacía). */
    private final List<String> derecho;

    /**
     * Crea una producción {@code izquierdo > derecho}.
     *
     * @param izquierdo no terminal del lado izquierdo
     * @param derecho símbolos del lado derecho (lista no vacía; use
     *        {@link Constantes#EPSILON} para la producción vacía)
     */
    public Produccion(String izquierdo, List<String> derecho) {
        if (izquierdo == null || izquierdo.trim().isEmpty()) {
            throw new IllegalArgumentException("El no terminal izquierdo no puede estar vacío.");
        }
        if (derecho == null || derecho.isEmpty()) {
            throw new IllegalArgumentException("El lado derecho de la producción no puede estar vacío.");
        }
        this.izquierdo = izquierdo.trim();
        List<String> copia = new ArrayList<>();
        for (String s : derecho) {
            if (s == null || s.trim().isEmpty()) {
                throw new IllegalArgumentException("Un símbolo del lado derecho no puede estar vacío.");
            }
            copia.add(s.trim());
        }
        this.derecho = Collections.unmodifiableList(copia);
    }

    /**
     * Crea una producción a partir del lado derecho escrito como texto.
     *
     * <p>Los símbolos pueden venir de dos formas:</p>
     * <ul>
     *   <li>separados por espacio (notación del menú: {@code "0 B"} → los
     *       símbolos son {@code 0} y {@code B}; así se pueden usar símbolos
     *       de más de un carácter),</li>
     *   <li>sin espacios (notación compacta del archivo .gtk y del ejemplo
     *       del enunciado: {@code "0B"} → los símbolos son {@code 0} y
     *       {@code B}, un carácter cada uno).</li>
     * </ul>
     * <p>Si el texto es {@code "epsilon"} se crea la producción vacía. Si el
     * texto trae {@code "|"} se debe separar antes de llamar a este
     * constructor (ver {@code Gramatica.agregarProduccionTexto}).</p>
     *
     * @param izquierdo no terminal del lado izquierdo
     * @param derechoTexto lado derecho como texto
     * @return la producción creada
     */
    public static Produccion desdeTexto(String izquierdo, String derechoTexto) {
        if (derechoTexto == null || derechoTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("El lado derecho de la producción no puede estar vacío.");
        }
        String texto = derechoTexto.trim();
        if (Constantes.EPSILON.equals(texto)) {
            return new Produccion(izquierdo, Collections.singletonList(Constantes.EPSILON));
        }
        List<String> simbolos = new ArrayList<>();
        if (texto.contains(" ") || texto.contains("\t")) {
            // Notación con espacios: cada palabra es un símbolo.
            for (String p : texto.split("\\s+")) {
                simbolos.add(p);
            }
        } else {
            // Notación compacta (como en el .gtk): cada carácter es un símbolo.
            for (char c : texto.toCharArray()) {
                simbolos.add(String.valueOf(c));
            }
        }
        return new Produccion(izquierdo, simbolos);
    }

    /** No terminal del lado izquierdo de la producción. */
    public String getIzquierdo() {
        return izquierdo;
    }

    /** Símbolos del lado derecho, en orden. */
    public List<String> getDerecho() {
        return derecho;
    }

    /** Indica si es la producción vacía (A → epsilon). */
    public boolean esEpsilon() {
        return derecho.size() == 1 && Constantes.EPSILON.equals(derecho.get(0));
    }

    /**
     * Dos producciones son iguales si tienen el mismo lado izquierdo y el
     * mismo lado derecho. Sirve para rechazar producciones repetidas, como
     * pide el enunciado.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Produccion)) {
            return false;
        }
        Produccion otra = (Produccion) obj;
        return izquierdo.equals(otra.izquierdo) && derecho.equals(otra.derecho);
    }

    /** Código hash coherente con {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return Objects.hash(izquierdo, derecho);
    }

    /**
     * Notación del enunciado: {@code A > a B} o {@code A > epsilon}.
     * En el archivo .gtk se usa la forma compacta sin espacios
     * ({@code A>aB}); ver {@link #aFormatoGtk()}.
     */
    @Override
    public String toString() {
        return izquierdo + " > " + String.join(" ", derecho);
    }

    /**
     * Forma compacta para el archivo .gtk: {@code A>aB} (sin espacios, con
     * mayúsculas para los no terminales y minúsculas para los terminales).
     * Para epsilon se escribe {@code A>epsilon}.
     */
    public String aFormatoGtk() {
        if (esEpsilon()) {
            return izquierdo + ">" + Constantes.EPSILON;
        }
        StringBuilder sb = new StringBuilder(izquierdo).append(">");
        for (String s : derecho) {
            sb.append(s);
        }
        return sb.toString();
    }
}
