package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Constantes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analizadores (parsers) de las dos notaciones para ingresar transiciones de
 * un AFD, según el enunciado.
 *
 * <h2>Modo 1 — notación lineal</h2>
 * <p>Una transición a la vez: {@code A,B,0;false,true}. Indica que se va del
 * estado A al estado B con el símbolo 0; las banderas indican si A y B son
 * de aceptación, en ese orden. Si ya existe una transición desde el mismo
 * estado con el mismo símbolo, se rechaza (eso solo es posible con un AFN),
 * y si el símbolo es epsilon también se rechaza.</p>
 *
 * <h2>Modo 2 — notación matricial</h2>
 * <p>La tabla de transiciones completa en una línea:
 * {@code [a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]}. El primer
 * corchete trae los terminales (columnas), el segundo los estados (filas) y
 * los estados de aceptación después del punto y coma, y el tercero la matriz
 * de destinos (columnas separadas por comas, filas separadas por punto y
 * coma). El símbolo {@code -} indica que el estado no tiene transición con
 * ese símbolo.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class ParserTransiciones {

    /** Patrón para extraer cada grupo entre corchetes del modo 2. */
    private static final Pattern GRUPO_CORCHETES = Pattern.compile("\\[(.*?)\\]");

    private ParserTransiciones() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Aplica una transición en modo 1.
     *
     * @param afd AFD al que se agrega la transición
     * @param texto notación {@code A,B,0;false,true}
     * @return resumen de lo aplicado, para mostrar al usuario
     * @throws IllegalArgumentException si el formato es inválido o se viola
     *         alguna regla del AFD (estados o símbolo no declarados,
     *         no determinismo, epsilon)
     */
    public static String aplicarModo1(AFD afd, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("Escriba la transición en modo 1. Ejemplo: A,B,0;false,true");
        }
        String[] partes = texto.trim().split(";", 2);
        if (partes.length != 2) {
            throw new IllegalArgumentException(
                    "Formato inválido. Use: ORIGEN,DESTINO,SIMBOLO;aceptacionOrigen,aceptacionDestino. "
                    + "Ejemplo: A,B,0;false,true");
        }
        String[] lados = partes[0].split(",", 3);
        if (lados.length != 3) {
            throw new IllegalArgumentException(
                    "Formato inválido. Antes del punto y coma van ORIGEN,DESTINO,SIMBOLO. "
                    + "Ejemplo: A,B,0;false,true");
        }
        String origen = lados[0].trim();
        String destino = lados[1].trim();
        String simbolo = lados[2].trim();
        String[] banderas = partes[1].split(",", 2);
        if (banderas.length != 2) {
            throw new IllegalArgumentException(
                    "Formato inválido. Después del punto y coma van dos banderas (false,true). "
                    + "Ejemplo: A,B,0;false,true");
        }
        boolean accOrigen = Boolean.parseBoolean(banderas[0].trim());
        boolean accDestino = Boolean.parseBoolean(banderas[1].trim());

        // Las validaciones (existencia, determinismo, epsilon) las hace el modelo.
        afd.agregarTransicion(origen, destino, simbolo);
        // La última definición de aceptación escrita gana (regla del enunciado).
        aplicarBandera(afd, origen, accOrigen);
        aplicarBandera(afd, destino, accDestino);

        return "Transición agregada: " + origen + " --" + simbolo + "--> " + destino
                + " (" + origen + (accOrigen ? " es" : " no es") + " de aceptación, "
                + destino + (accDestino ? " es" : " no es") + " de aceptación).";
    }

    /** Aplica una bandera de aceptación: true la agrega, false la quita. */
    private static void aplicarBandera(AFD afd, String estado, boolean aceptacion) {
        if (aceptacion) {
            afd.agregarEstadoAceptacion(estado);
        } else {
            afd.quitarEstadoAceptacion(estado);
        }
    }

    /**
     * Aplica una tabla de transiciones completa en modo 2.
     *
     * @param afd AFD al que se agregan las transiciones
     * @param texto notación matricial
     *        {@code [a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]}
     * @return resumen de lo aplicado, para mostrar al usuario
     * @throws IllegalArgumentException si el formato es inválido o se viola
     *         alguna regla del AFD
     */
    public static String aplicarModo2(AFD afd, String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Escriba la tabla en modo 2. Ejemplo: [a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]");
        }
        List<String> grupos = new ArrayList<>();
        Matcher m = GRUPO_CORCHETES.matcher(texto);
        while (m.find()) {
            grupos.add(m.group(1));
        }
        if (grupos.size() != 3) {
            throw new IllegalArgumentException(
                    "Formato inválido. El modo 2 lleva tres grupos entre corchetes: "
                    + "[terminales] [estados; aceptación] [matriz]. Ejemplo: "
                    + "[a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]");
        }
        List<String> terminales = dividir(grupos.get(0));
        String[] estadosYAceptacion = grupos.get(1).split(";", 2);
        List<String> estados = dividir(estadosYAceptacion[0]);
        List<String> aceptacion = estadosYAceptacion.length > 1
                ? dividir(estadosYAceptacion[1]) : new ArrayList<>();
        List<String> filas = dividirPorPuntoYComa(grupos.get(2));

        if (terminales.isEmpty() || estados.isEmpty()) {
            throw new IllegalArgumentException(
                    "El modo 2 necesita al menos un terminal y un estado.");
        }
        if (filas.size() != estados.size()) {
            throw new IllegalArgumentException(
                    "La matriz debe tener una fila por estado: se esperaban "
                    + estados.size() + " filas y vienen " + filas.size() + ".");
        }
        // Se declaran los símbolos y estados que falten (la matriz los define).
        for (String t : terminales) {
            if (!afd.getAlfabeto().contains(t)) {
                afd.agregarSimbolo(t);
            }
        }
        for (String e : estados) {
            if (!afd.getEstados().contains(e)) {
                afd.agregarEstado(e);
            }
        }
        for (String a : aceptacion) {
            afd.agregarEstadoAceptacion(a);
        }
        int agregadas = 0;
        for (int i = 0; i < estados.size(); i++) {
            List<String> celdas = dividir(filas.get(i));
            if (celdas.size() != terminales.size()) {
                throw new IllegalArgumentException(
                        "La fila " + (i + 1) + " debe tener " + terminales.size()
                        + " columnas (una por terminal) y trae " + celdas.size() + ".");
            }
            for (int j = 0; j < terminales.size(); j++) {
                String destino = celdas.get(j);
                if (Constantes.SIN_TRANSICION.equals(destino)) {
                    continue; // "-" = el estado no tiene transición con ese símbolo.
                }
                afd.agregarTransicion(estados.get(i), destino, terminales.get(j));
                agregadas++;
            }
        }
        StringBuilder resumen = new StringBuilder();
        resumen.append("Tabla aplicada: ").append(agregadas)
            .append(" transiciones agregadas. Estados: ")
            .append(String.join(", ", estados))
            .append(". Aceptación: ")
            .append(aceptacion.isEmpty() ? "(ninguno)" : String.join(", ", aceptacion))
            .append(".");
        if (afd.getEstadoInicial() == null) {
            resumen.append(" Recuerde definir el estado inicial.");
        }
        return resumen.toString();
    }

    /** Divide por comas, quitando espacios y descartando vacíos. */
    private static List<String> dividir(String texto) {
        List<String> resultado = new ArrayList<>();
        for (String parte : texto.split(",")) {
            String p = parte.trim();
            if (!p.isEmpty()) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    /** Divide por punto y coma, quitando espacios y descartando vacíos. */
    private static List<String> dividirPorPuntoYComa(String texto) {
        List<String> resultado = new ArrayList<>();
        for (String parte : texto.split(";")) {
            String p = parte.trim();
            if (!p.isEmpty()) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
