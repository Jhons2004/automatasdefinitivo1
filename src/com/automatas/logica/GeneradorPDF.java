package com.automatas.logica;

import com.automatas.modelo.AFD;
import com.automatas.modelo.DatosDelCurso;
import com.automatas.modelo.Gramatica;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import javax.imageio.ImageIO;

/**
 * Generador de reportes en PDF (opción "Generar Reporte" del menú de Reportes).
 *
 * <p>Este generador <b>no usa ninguna librería externa</b>: escribe el archivo
 * PDF directamente, con la especificación del formato (objetos, tabla de
 * referencias cruzadas, fuentes estándar Type1 y compresión Flate). Así el
 * proyecto compila y funciona solo con el JDK, sin .jar adicionales. Cada
 * reporte contiene, como pide el enunciado:</p>
 * <ul>
 *   <li>toda la información de "Ver Detalle" (elementos formales del AFD o
 *       de la gramática),</li>
 *   <li>la representación gráfica del AFD (imagen del grafo generada con
 *       Graphviz; si Graphviz no está instalado, se incluye el código DOT y
 *       las instrucciones para instalarlo),</li>
 *   <li>al menos 3 cadenas válidas y 3 cadenas inválidas,</li>
 *   <li>el historial de cadenas evaluadas en la sesión (si lo hay).</li>
 * </ul>
 *
 * <p>Notas de implementación: las fuentes son las 14 fuentes estándar del PDF
 * (Helvetica, Helvetica-Bold y Courier, con codificación WinAnsi); por eso los
 * símbolos griegos Σ, δ y ε y la flecha → se sustituyen por su nombre en
 * ASCII antes de escribir (las vocales acentuadas y la ñ sí están en
 * WinAnsi). El ajuste de línea se hace por conteo de caracteres (las
 * fuentes son proporcionales, así que se usa un límite conservador). La
 * imagen del grafo se incrusta en JPEG (el PNG se convierte con ImageIO).</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class GeneradorPDF {

    /** Ancho útil de la página carta con márgenes de 50 puntos. */
    private static final float ANCHO_TEXTO = 612 - 2 * 50;

    private GeneradorPDF() {
        // Clase de utilería: no se instancia.
    }

    /**
     * Genera el reporte PDF de un AFD.
     *
     * @param afd AFD del reporte
     * @param destino archivo PDF de salida
     * @param validas cadenas válidas de ejemplo (al menos 3)
     * @param invalidas cadenas inválidas de ejemplo (al menos 3)
     * @param pngGrafo imagen PNG del grafo (puede ser {@code null})
     * @param dotGrafo código DOT del grafo (se incluye si no hay PNG)
     * @param historial evaluaciones realizadas en la sesión
     * @throws IOException si no se puede escribir el PDF
     */
    public static void generarReporteAfd(AFD afd, File destino,
            List<String> validas, List<String> invalidas,
            File pngGrafo, String dotGrafo,
            List<Repositorio.EvaluacionRegistrada> historial) throws IOException {
        Documento doc = new Documento();
        doc.titulo("Reporte de AFD - " + afd.getNombre());
        doc.encabezadoAutor();
        doc.subtitulo("1. Detalle del AFD");
        doc.textoPlano(afd.detalle());
        doc.subtitulo("2. Representación gráfica (Graphviz)");
        doc.grafo(pngGrafo, dotGrafo);
        doc.subtitulo("3. Cadenas de ejemplo");
        doc.cadenas(validas, invalidas);
        doc.subtitulo("4. Historial de evaluaciones en la sesión");
        doc.historial(historial);
        doc.guardar(destino);
    }

    /**
     * Genera el reporte PDF de una gramática.
     *
     * @param gramatica gramática del reporte
     * @param destino archivo PDF de salida
     * @param validas cadenas válidas de ejemplo (al menos 3)
     * @param invalidas cadenas inválidas de ejemplo (al menos 3)
     * @param pngGrafo imagen PNG del grafo del AFD equivalente (puede ser {@code null})
     * @param dotGrafo código DOT del grafo (se incluye si no hay PNG)
     * @param historial evaluaciones realizadas en la sesión
     * @throws IOException si no se puede escribir el PDF
     */
    public static void generarReporteGramatica(Gramatica gramatica, File destino,
            List<String> validas, List<String> invalidas,
            File pngGrafo, String dotGrafo,
            List<Repositorio.EvaluacionRegistrada> historial) throws IOException {
        Documento doc = new Documento();
        doc.titulo("Reporte de Gramática - " + gramatica.getNombre());
        doc.encabezadoAutor();
        doc.subtitulo("1. Detalle de la gramática");
        doc.textoPlano(gramatica.detalle());
        doc.subtitulo("2. Representación gráfica del AFD equivalente (Graphviz)");
        doc.parrafo("El grafo corresponde al AFD generado a partir de esta "
                + "gramática (conversión gramática a AFD).");
        doc.grafo(pngGrafo, dotGrafo);
        doc.subtitulo("3. Cadenas de ejemplo");
        doc.cadenas(validas, invalidas);
        doc.subtitulo("4. Historial de evaluaciones en la sesión");
        doc.historial(historial);
        doc.guardar(destino);
    }

    // =====================================================================
    // Escritor de PDF (sin librerías externas)
    // =====================================================================

    /** Fuentes estándar del PDF (nombres en el diccionario de recursos). */
    private static final String F_HELVETICA = "F1";
    private static final String F_HELVETICA_BOLD = "F2";
    private static final String F_COURIER = "F3";

    private static final float MARGEN = 50;
    private static final float ALTO_PAGINA = 792; // Carta: 612 x 792 puntos.

    /**
     * Documento en construcción: maneja las páginas, el cursor vertical y
     * los saltos de página. Cada página guarda su flujo de contenido (los
     * operadores PDF) y, como mucho, una imagen (el grafo).
     */
    private static final class Documento {
        private final List<Pagina> paginas = new ArrayList<>();
        private Pagina actual;
        private float y;

        Documento() {
            nuevaPagina();
        }

        private void nuevaPagina() {
            actual = new Pagina();
            paginas.add(actual);
            y = ALTO_PAGINA - MARGEN;
        }

        /** Escribe el PDF completo en el archivo destino. */
        void guardar(File destino) throws IOException {
            try (FileOutputStream out = new FileOutputStream(destino)) {
                out.write(construirPdf());
            }
        }

        /** Baja el cursor; cambia de página si no queda espacio. */
        private void asegurarEspacio(float altoNecesario) {
            if (y - altoNecesario < MARGEN) {
                nuevaPagina();
            }
        }

        void titulo(String texto) {
            texto = sanitizar(texto);
            asegurarEspacio(60);
            for (String linea : ajustar(texto, 48)) {
                escribir(linea, F_HELVETICA_BOLD, 18, 24);
            }
            y -= 8;
            linea();
        }

        void subtitulo(String texto) {
            texto = sanitizar(texto);
            asegurarEspacio(36);
            y -= 6;
            for (String linea : ajustar(texto, 68)) {
                escribir(linea, F_HELVETICA_BOLD, 13, 18);
            }
            y -= 2;
        }

        void parrafo(String texto) {
            texto = sanitizar(texto);
            for (String linea : ajustar(texto, 85)) {
                asegurarEspacio(16);
                escribir(linea, F_HELVETICA, 11, 14);
            }
            y -= 4;
        }

        /** Escribe texto plano (detalle) con fuente monoespaciada. */
        void textoPlano(String texto) {
            texto = sanitizar(texto);
            for (String linea : texto.split("\\R")) {
                asegurarEspacio(16);
                // Las líneas del detalle son cortas; si una excede el ancho,
                // se corta en trozos de 80 caracteres.
                String resto = linea.isEmpty() ? " " : linea;
                while (resto.length() > 80) {
                    escribir(resto.substring(0, 80), F_COURIER, 10, 13);
                    resto = resto.substring(80);
                }
                escribir(resto, F_COURIER, 10, 13);
            }
            y -= 4;
        }

        void encabezadoAutor() {
            parrafo(DatosDelCurso.UNIVERSIDAD + " - " + DatosDelCurso.FACULTAD);
            parrafo("Curso: " + DatosDelCurso.CURSO + " (" + DatosDelCurso.CICLO
                    + ") - Sección " + DatosDelCurso.SECCION);
            parrafo("Autor: " + DatosDelCurso.AUTOR + " - Carné: " + DatosDelCurso.CARNE);
            parrafo("Catedrático: " + DatosDelCurso.CATEDRATICO);
            parrafo("Fecha del reporte: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            y -= 4;
        }

        void cadenas(List<String> validas, List<String> invalidas) {
            parrafo("Cadenas válidas:");
            if (validas == null || validas.isEmpty()) {
                parrafo("  (no se encontraron cadenas válidas con la enumeración actual)");
            } else {
                for (String c : validas) {
                    parrafo("  - " + mostrarCadena(c));
                }
            }
            parrafo("Cadenas inválidas:");
            if (invalidas == null || invalidas.isEmpty()) {
                parrafo("  (no se encontraron cadenas inválidas con la enumeración actual)");
            } else {
                for (String c : invalidas) {
                    parrafo("  - " + mostrarCadena(c));
                }
            }
        }

        void historial(List<Repositorio.EvaluacionRegistrada> historial) {
            if (historial == null || historial.isEmpty()) {
                parrafo("(sin evaluaciones registradas en esta sesión)");
                return;
            }
            for (Repositorio.EvaluacionRegistrada e : historial) {
                parrafo("  - " + e.toString());
            }
        }

        void grafo(File pngGrafo, String dotGrafo) throws IOException {
            if (pngGrafo != null && pngGrafo.exists()) {
                Imagen imagen = Imagen.desdePng(pngGrafo);
                float escala = Math.min(1.0f, ANCHO_TEXTO / imagen.ancho);
                float ancho = imagen.ancho * escala;
                float alto = imagen.alto * escala;
                asegurarEspacio(alto + 10);
                actual.dibujarImagen(imagen, MARGEN, y - alto, ancho, alto);
                y -= alto + 8;
            } else {
                parrafo("No se pudo generar la imagen del grafo porque Graphviz "
                        + "no está instalado en este equipo. El código DOT del grafo es:");
                textoPlano(dotGrafo == null ? "(sin grafo)" : dotGrafo);
                parrafo("Para ver el grafo: instale Graphviz (graphviz.org, sección Download) "
                        + "y ejecute: dot -Tpng grafo.dot -o grafo.png");
            }
        }

        private String mostrarCadena(String cadena) {
            return "(vacía)".equals(cadena) ? "epsilon (cadena vacía)" : "'" + cadena + "'";
        }

        private void linea() {
            actual.operador("1 w " + fmt(MARGEN) + " " + fmt(y) + " m "
                    + fmt(MARGEN + ANCHO_TEXTO) + " " + fmt(y) + " l S");
            y -= 10;
        }

        private void escribir(String texto, String fuente, float tamano, float interlineado) {
            actual.operador("BT /" + fuente + " " + fmt(tamano) + " Tf "
                    + fmt(MARGEN) + " " + fmt(y) + " Td "
                    + cadenaPdf(texto) + " Tj ET");
            y -= interlineado;
        }

        /** Ajusta un párrafo al ancho útil, cortando por palabras. */
        private List<String> ajustar(String texto, int maxChars) {
            List<String> lineas = new ArrayList<>();
            StringBuilder actual = new StringBuilder();
            for (String palabra : texto.split("\\s+")) {
                if (palabra.isEmpty()) {
                    continue;
                }
                String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
                if (prueba.length() > maxChars && actual.length() > 0) {
                    lineas.add(actual.toString());
                    actual = new StringBuilder(palabra);
                } else {
                    actual = new StringBuilder(prueba);
                }
            }
            if (actual.length() > 0) {
                lineas.add(actual.toString());
            }
            return lineas;
        }

        /**
         * Sustituye los caracteres que las fuentes estándar del PDF
         * (WinAnsiEncoding) no pueden representar. Las vocales acentuadas y
         * la ñ sí están en WinAnsi y se conservan; los símbolos griegos y
         * las flechas se escriben con su nombre en ASCII.
         */
        private static String sanitizar(String texto) {
            if (texto == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder(texto.length());
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                switch (c) {
                    case '\u03A3': sb.append("Sigma"); break;   // Σ
                    case '\u03B4': sb.append("delta"); break;   // δ
                    case '\u03B5': sb.append("epsilon"); break; // ε
                    case '\u2192': sb.append("->"); break;      // →
                    case '\u2190': sb.append("<-"); break;      // ←
                    default:
                        sb.append(c);
                        break;
                }
            }
            return sb.toString();
        }

        /** Número con un decimal, como lo espera el PDF. */
        private static String fmt(float v) {
            return String.format(java.util.Locale.US, "%.1f", v);
        }

        /** Convierte texto a literal de cadena PDF "(...)" en WinAnsi. */
        private static String cadenaPdf(String texto) {
            byte[] bytes = aWinAnsi(texto);
            StringBuilder sb = new StringBuilder("(");
            for (byte b : bytes) {
                int v = b & 0xFF;
                if (v == '(' || v == ')' || v == '\\') {
                    sb.append('\\');
                }
                sb.append((char) v);
            }
            sb.append(')');
            return sb.toString();
        }

        /** Codifica texto a bytes WinAnsi (Latin-1 + guiones y comillas). */
        private static byte[] aWinAnsi(String texto) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                if (c < 128) {
                    out.write(c);
                } else if (c >= 0xA0 && c <= 0xFF) {
                    out.write(c); // En WinAnsi coincide con Latin-1.
                } else if (c == '\u2013') {
                    out.write(0x96); // guion corto
                } else if (c == '\u2014') {
                    out.write(0x97); // guion largo
                } else if (c == '\u201C') {
                    out.write(0x93);
                } else if (c == '\u201D') {
                    out.write(0x94);
                } else if (c == '\u2018') {
                    out.write(0x91);
                } else if (c == '\u2019') {
                    out.write(0x92);
                } else if (c == '\u2022') {
                    out.write(0x95);
                } else {
                    out.write('?');
                }
            }
            return out.toByteArray();
        }

        // ---------------- Ensamblado del PDF ----------------

        /** Construye los bytes completos del PDF (cabecera, objetos, xref). */
        private byte[] construirPdf() throws IOException {
            List<byte[]> objetos = new ArrayList<>();
            // 1: Catálogo, 2: Páginas, 3-5: fuentes.
            objetos.add(obj("<< /Type /Catalog /Pages 2 0 R >>"));
            StringBuilder kids = new StringBuilder("<< /Type /Pages /Kids [");
            int numPaginas = paginas.size();
            // Cada página usa 2 objetos (página + contenido) y 1 más si tiene imagen.
            int siguiente = 6;
            int[] numPagina = new int[numPaginas];
            int[] numContenido = new int[numPaginas];
            int[] numImagen = new int[numPaginas];
            for (int i = 0; i < numPaginas; i++) {
                numPagina[i] = siguiente++;
                numContenido[i] = siguiente++;
                numImagen[i] = paginas.get(i).imagen != null ? siguiente++ : -1;
                kids.append(numPagina[i]).append(" 0 R ");
            }
            kids.append("] /Count ").append(numPaginas).append(" >>");
            objetos.add(obj(kids.toString()));
            objetos.add(obj("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica "
                    + "/Encoding /WinAnsiEncoding >>"));
            objetos.add(obj("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold "
                    + "/Encoding /WinAnsiEncoding >>"));
            objetos.add(obj("<< /Type /Font /Subtype /Type1 /BaseFont /Courier "
                    + "/Encoding /WinAnsiEncoding >>"));
            for (int i = 0; i < numPaginas; i++) {
                Pagina p = paginas.get(i);
                String recursos = "<< /Font << /F1 3 0 R /F2 4 0 R /F3 5 0 R >>"
                        + (p.imagen != null ? " /XObject << /Im1 " + numImagen[i] + " 0 R >>" : "")
                        + " >>";
                objetos.add(obj("<< /Type /Page /Parent 2 0 R "
                        + "/MediaBox [0 0 612 792] /Resources " + recursos
                        + " /Contents " + numContenido[i] + " 0 R >>"));
                objetos.add(flujo(p.contenido.toByteArray()));
                if (p.imagen != null) {
                    objetos.add(imagenJpeg(p.imagen));
                }
            }

            ByteArrayOutputStream pdf = new ByteArrayOutputStream();
            pdf.write("%PDF-1.4\n".getBytes(StandardCharsets.US_ASCII));
            List<Integer> offsets = new ArrayList<>();
            for (int i = 0; i < objetos.size(); i++) {
                offsets.add(pdf.size());
                pdf.write(Integer.toString(i + 1).getBytes(StandardCharsets.US_ASCII));
                pdf.write(" 0 obj\n".getBytes(StandardCharsets.US_ASCII));
                pdf.write(objetos.get(i));
                pdf.write("\nendobj\n".getBytes(StandardCharsets.US_ASCII));
            }
            int inicioXref = pdf.size();
            pdf.write(("xref\n0 " + (objetos.size() + 1) + "\n").getBytes(StandardCharsets.US_ASCII));
            pdf.write("0000000000 65535 f \n".getBytes(StandardCharsets.US_ASCII));
            for (int off : offsets) {
                pdf.write(String.format("%010d 00000 n \n", off).getBytes(StandardCharsets.US_ASCII));
            }
            pdf.write(("trailer\n<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\n"
                    + "startxref\n" + inicioXref + "\n%%EOF\n").getBytes(StandardCharsets.US_ASCII));
            return pdf.toByteArray();
        }

        private static byte[] obj(String diccionario) {
            return diccionario.getBytes(StandardCharsets.US_ASCII);
        }

        /** Objeto de flujo con contenido comprimido (FlateDecode). */
        private static byte[] flujo(byte[] contenido) throws IOException {
            byte[] comprimido = comprimir(contenido);
            String cabecera = "<< /Length " + comprimido.length + " /Filter /FlateDecode >>\nstream\n";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(cabecera.getBytes(StandardCharsets.US_ASCII));
            out.write(comprimido);
            out.write("\nendstream".getBytes(StandardCharsets.US_ASCII));
            return out.toByteArray();
        }

        /** Objeto de imagen XObject con datos JPEG (DCTDecode). */
        private static byte[] imagenJpeg(Imagen imagen) throws IOException {
            String cabecera = "<< /Type /XObject /Subtype /Image /Width " + imagen.ancho
                    + " /Height " + imagen.alto + " /BitsPerComponent 8 /ColorSpace /DeviceRGB"
                    + " /Filter /DCTDecode /Length " + imagen.jpeg.length + " >>\nstream\n";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(cabecera.getBytes(StandardCharsets.US_ASCII));
            out.write(imagen.jpeg);
            out.write("\nendstream".getBytes(StandardCharsets.US_ASCII));
            return out.toByteArray();
        }

        private static byte[] comprimir(byte[] datos) throws IOException {
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
            deflater.setInput(datos);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (!deflater.finished()) {
                int n = deflater.deflate(buffer);
                out.write(buffer, 0, n);
            }
            deflater.end();
            return out.toByteArray();
        }
    }

    /** Una página: su flujo de contenido y, como mucho, una imagen. */
    private static final class Pagina {
        final ByteArrayOutputStream contenido = new ByteArrayOutputStream();
        Imagen imagen;

        void operador(String linea) {
            try {
                // ISO-8859-1 (Latin-1): cada carácter U+0000..U+00FF se escribe
                // como el byte de mismo valor, así se conservan los bytes
                // WinAnsi de las vocales acentuadas (US-ASCII los volvía '?').
                contenido.write((linea + "\n").getBytes(StandardCharsets.ISO_8859_1));
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo escribir el contenido del PDF.", e);
            }
        }

        void dibujarImagen(Imagen img, float x, float y, float ancho, float alto) {
            this.imagen = img;
            operador("q " + Documento.fmt(ancho) + " 0 0 " + Documento.fmt(alto)
                    + " " + Documento.fmt(x) + " " + Documento.fmt(y) + " cm /Im1 Do Q");
        }
    }

    /** Imagen del grafo: dimensiones y bytes JPEG. */
    private static final class Imagen {
        final int ancho;
        final int alto;
        final byte[] jpeg;

        Imagen(int ancho, int alto, byte[] jpeg) {
            this.ancho = ancho;
            this.alto = alto;
            this.jpeg = jpeg;
        }

        /** Lee un PNG y lo convierte a JPEG (sin canal alfa). */
        static Imagen desdePng(File png) throws IOException {
            BufferedImage original = ImageIO.read(png);
            if (original == null) {
                throw new IOException("No se pudo leer la imagen del grafo: " + png);
            }
            BufferedImage rgb = new BufferedImage(original.getWidth(), original.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            rgb.createGraphics().drawImage(original, 0, 0, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(rgb, "jpg", baos);
            return new Imagen(original.getWidth(), original.getHeight(), baos.toByteArray());
        }
    }
}
