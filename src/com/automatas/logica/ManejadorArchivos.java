package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.Constantes;
import com.automatas.modelo.Gramatica;
import com.automatas.modelo.Produccion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga y guardado de archivos de entrada (.afd y .gtk).
 *
 * <p>El enunciado indica que los archivos vendrán <b>sin errores</b> y con un
 * patrón definido, por lo que no es necesario un analizador completo; aun
 * así, esta clase valida lo mínimo (formato de línea) y reporta errores
 * claros si un archivo no cumple el patrón.</p>
 *
 * <h2>Archivo .afd (AFD)</h2>
 * <p>Cada línea tiene la forma {@code A,B,0;false,true}: estado de partida,
 * estado de llegada, símbolo, y luego las banderas de aceptación del estado
 * de partida y del de llegada, en ese orden. Las líneas se separan con ENTER.
 * El estado inicial es el estado que esté al inicio del archivo. El nombre
 * del archivo es el nombre del AFD. Si en una línea viene un estado y su
 * definición de aceptación es distinta a la anterior, se toma la última
 * escrita.</p>
 *
 * <h2>Archivo .gtk (gramática)</h2>
 * <p>Cada línea tiene la forma {@code A>Ab}: con mayúsculas se indican los no
 * terminales y con minúsculas (y dígitos) los símbolos del alfabeto; el
 * símbolo {@code >} denota la derivación. El no terminal inicial es el que
 * viene al inicio de la gramática. El nombre del archivo es el nombre de la
 * gramática. Para usar {@code |} se crea una producción con el mismo no
 * terminal pero con transición distinta.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class ManejadorArchivos {

    private ManejadorArchivos() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Carga un AFD desde un archivo .afd.
     *
     * @param archivo archivo .afd
     * @return el AFD cargado (nombre = nombre del archivo sin extensión)
     * @throws IOException si no se puede leer el archivo
     * @throws IllegalArgumentException si una línea no cumple el formato
     */
    public static AFD cargarAfd(File archivo) throws IOException {
        String nombre = nombreSinExtension(archivo);
        AFD afd = new AFD(nombre);
        // Última definición de aceptación escrita gana (regla del enunciado).
        Map<String, Boolean> aceptacion = new LinkedHashMap<>();
        String estadoInicial = null;
        List<String> lineas = leerLineas(archivo);
        for (String linea : lineas) {
            String[] partes = linea.split(";", 2);
            if (partes.length != 2) {
                throw new IllegalArgumentException(
                        "Línea inválida en el archivo .afd: '" + linea
                        + "'. Formato esperado: ORIGEN,DESTINO,SIMBOLO;false,true");
            }
            String[] estados = partes[0].split(",", 3);
            if (estados.length != 3) {
                throw new IllegalArgumentException(
                        "Línea inválida en el archivo .afd: '" + linea
                        + "'. Formato esperado: ORIGEN,DESTINO,SIMBOLO;false,true");
            }
            String origen = estados[0].trim();
            String destino = estados[1].trim();
            String simbolo = estados[2].trim();
            String[] banderas = partes[1].split(",", 2);
            if (banderas.length != 2) {
                throw new IllegalArgumentException(
                        "Línea inválida en el archivo .afd: '" + linea
                        + "'. Después del punto y coma van dos banderas: false,true");
            }
            boolean accOrigen = Boolean.parseBoolean(banderas[0].trim());
            boolean accDestino = Boolean.parseBoolean(banderas[1].trim());

            if (estadoInicial == null) {
                estadoInicial = origen;
            }
            agregarSiFalta(afd, origen, simbolo);
            agregarSiFalta(afd, destino, simbolo);
            aceptacion.put(origen, accOrigen);
            aceptacion.put(destino, accDestino);
            afd.agregarTransicion(origen, destino, simbolo);
        }
        if (estadoInicial == null) {
            throw new IllegalArgumentException("El archivo .afd está vacío.");
        }
        afd.setEstadoInicial(estadoInicial);
        for (Map.Entry<String, Boolean> e : aceptacion.entrySet()) {
            if (e.getValue()) {
                afd.agregarEstadoAceptacion(e.getKey());
            }
        }
        return afd;
    }

    /** Agrega el estado y el símbolo si aún no existen en el AFD. */
    private static void agregarSiFalta(AFD afd, String estado, String simbolo) {
        if (!afd.getEstados().contains(estado)) {
            afd.agregarEstado(estado);
        }
        if (!afd.getAlfabeto().contains(simbolo)) {
            afd.agregarSimbolo(simbolo);
        }
    }

    /**
     * Carga una gramática desde un archivo .gtk.
     *
     * @param archivo archivo .gtk
     * @return la gramática cargada (nombre = nombre del archivo sin extensión)
     * @throws IOException si no se puede leer el archivo
     * @throws IllegalArgumentException si una línea no cumple el formato
     */
    public static Gramatica cargarGramatica(File archivo) throws IOException {
        String nombre = nombreSinExtension(archivo);
        Gramatica gramatica = new Gramatica(nombre);
        String inicial = null;
        List<String[]> producciones = new ArrayList<>();
        for (String linea : leerLineas(archivo)) {
            String[] partes = linea.split(">", 2);
            if (partes.length != 2) {
                throw new IllegalArgumentException(
                        "Línea inválida en el archivo .gtk: '" + linea
                        + "'. Formato esperado: A>Ab");
            }
            String izquierdo = partes[0].trim();
            String derecho = partes[1].trim();
            if (izquierdo.isEmpty() || derecho.isEmpty()) {
                throw new IllegalArgumentException(
                        "Línea inválida en el archivo .gtk: '" + linea + "'.");
            }
            if (inicial == null) {
                inicial = izquierdo;
            }
            producciones.add(new String[]{izquierdo, derecho});
        }
        if (inicial == null) {
            throw new IllegalArgumentException("El archivo .gtk está vacío.");
        }
        // Primero se declaran todos los símbolos (dos pasadas: declarar y
        // luego agregar producciones, porque una producción puede usar un no
        // terminal declarado en una línea posterior).
        for (String[] p : producciones) {
            String izquierdo = p[0];
            if (!gramatica.getNoTerminales().contains(izquierdo)) {
                gramatica.agregarNoTerminal(izquierdo);
            }
            if (!Constantes.EPSILON.equals(p[1])) {
                for (char c : p[1].toCharArray()) {
                    String s = String.valueOf(c);
                    if (Character.isUpperCase(c)) {
                        if (!gramatica.getNoTerminales().contains(s)) {
                            gramatica.agregarNoTerminal(s);
                        }
                    } else {
                        if (!gramatica.getTerminales().contains(s)) {
                            gramatica.agregarTerminal(s);
                        }
                    }
                }
            }
        }
        gramatica.setInicial(inicial);
        for (String[] p : producciones) {
            if (Constantes.EPSILON.equals(p[1])) {
                gramatica.agregarProduccion(new Produccion(p[0],
                        Collections.singletonList(Constantes.EPSILON)));
            } else {
                List<String> ladoDerecho = new ArrayList<>();
                for (char c : p[1].toCharArray()) {
                    ladoDerecho.add(String.valueOf(c));
                }
                gramatica.agregarProduccion(new Produccion(p[0], ladoDerecho));
            }
        }
        return gramatica;
    }

    /**
     * Guarda un AFD en un archivo .afd (formato del enunciado).
     *
     * @param afd AFD a guardar
     * @param archivo destino (se agrega la extensión .afd si falta)
     * @throws IOException si no se puede escribir el archivo
     */
    public static void guardarAfd(AFD afd, File archivo) throws IOException {
        File destino = conExtension(archivo, ".afd");
        try (PrintWriter pw = new PrintWriter(new FileWriter(destino))) {
            pw.print(afd.serializarAfd());
        }
    }

    /**
     * Guarda una gramática en un archivo .gtk (formato del enunciado).
     *
     * @param gramatica gramática a guardar
     * @param archivo destino (se agrega la extensión .gtk si falta)
     * @throws IOException si no se puede escribir el archivo
     */
    public static void guardarGramatica(Gramatica gramatica, File archivo) throws IOException {
        File destino = conExtension(archivo, ".gtk");
        try (PrintWriter pw = new PrintWriter(new FileWriter(destino))) {
            pw.print(gramatica.serializarGtk());
        }
    }

    /** Lee las líneas no vacías de un archivo, quitando espacios sobrantes. */
    private static List<String> leerLineas(File archivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    lineas.add(linea);
                }
            }
        }
        return lineas;
    }

    /** Nombre del archivo sin la extensión. */
    public static String nombreSinExtension(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        return punto > 0 ? nombre.substring(0, punto) : nombre;
    }

    /** Devuelve el archivo con la extensión indicada (si no la tiene ya). */
    private static File conExtension(File archivo, String extension) {
        if (archivo.getName().toLowerCase().endsWith(extension)) {
            return archivo;
        }
        return new File(archivo.getParentFile(), archivo.getName() + extension);
    }
}
