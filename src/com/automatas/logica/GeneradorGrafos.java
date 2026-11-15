package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Transicion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Generador de grafos del AFD en formato Graphviz (DOT).
 *
 * <p>El enunciado pide que el programa sea capaz de realizar grafos (en
 * Graphviz) para un mejor entendimiento de lo realizado. Esta clase genera
 * el archivo {@code .dot} del AFD y, si el programa {@code dot} de Graphviz
 * está instalado en el equipo, lo renderiza a PNG para incluirlo en el
 * reporte PDF. Si Graphviz no está instalado, el archivo .dot queda
 * guardado y el reporte lo incluye como texto, con las instrucciones para
 * instalar Graphviz.</p>
 *
 * <p>Convenciones del grafo: los estados de aceptación se dibujan con doble
 * círculo, el estado inicial recibe una flecha desde un nodo invisible, y
 * cada transición es una flecha etiquetada con su símbolo.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class GeneradorGrafos {

    private GeneradorGrafos() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Genera el código DOT del AFD.
     *
     * @param afd AFD a graficar
     * @return texto en formato DOT (Graphviz)
     */
    public static String aDot(AFD afd) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph \"").append(afd.getNombre()).append("\" {")
            .append(System.lineSeparator());
        sb.append("  rankdir=LR;").append(System.lineSeparator());
        sb.append("  label=\"AFD: ").append(afd.getNombre()).append("\";")
            .append(System.lineSeparator());
        sb.append("  node [shape=circle];").append(System.lineSeparator());
        // Flecha al estado inicial.
        sb.append("  inicio [shape=none, label=\"\"];").append(System.lineSeparator());
        if (afd.getEstadoInicial() != null) {
            sb.append("  inicio -> \"").append(afd.getEstadoInicial()).append("\";")
                .append(System.lineSeparator());
        }
        // Estados: doble círculo para los de aceptación.
        for (String estado : afd.getEstados()) {
            if (afd.esEstadoAceptacion(estado)) {
                sb.append("  \"").append(estado).append("\" [shape=doublecircle];")
                    .append(System.lineSeparator());
            }
        }
        // Transiciones: flechas etiquetadas con el símbolo.
        for (Transicion t : afd.getTransiciones()) {
            sb.append("  \"").append(t.getOrigen()).append("\" -> \"")
                .append(t.getDestino()).append("\" [label=\"")
                .append(t.getSimbolo()).append("\"];")
                .append(System.lineSeparator());
        }
        sb.append("}").append(System.lineSeparator());
        return sb.toString();
    }

    /**
     * Escribe el archivo .dot del AFD.
     *
     * @param afd AFD a graficar
     * @param archivoDot destino del archivo .dot
     * @throws IOException si no se puede escribir el archivo
     */
    public static void escribirDot(AFD afd, File archivoDot) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoDot))) {
            pw.print(aDot(afd));
        }
    }

    /**
     * Renderiza un archivo .dot a PNG usando el programa {@code dot} de
     * Graphviz (si está instalado).
     *
     * @param archivoDot archivo .dot de entrada
     * @param archivoPng destino del PNG
     * @return {@code true} si el PNG se generó; {@code false} si Graphviz no
     *         está instalado o falló el renderizado
     */
    public static boolean renderizarPng(File archivoDot, File archivoPng) {
        try {
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng",
                    archivoDot.getAbsolutePath(), "-o", archivoPng.getAbsolutePath());
            pb.redirectErrorStream(true);
            Process proceso = pb.start();
            int codigo = proceso.waitFor();
            return codigo == 0 && archivoPng.exists() && archivoPng.length() > 0;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Indica si el programa {@code dot} de Graphviz está disponible.
     *
     * @return {@code true} si {@code dot -V} responde sin error
     */
    public static boolean graphvizDisponible() {
        try {
            Process proceso = new ProcessBuilder("dot", "-V")
                    .redirectErrorStream(true).start();
            return proceso.waitFor() == 0;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
