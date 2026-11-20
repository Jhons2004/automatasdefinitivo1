package com.automatas.vista;

import com.automatas.logica.Convertidor;
import com.automatas.logica.GeneradorCadenas;
import com.automatas.logica.GeneradorGrafos;
import com.automatas.logica.GeneradorPDF;
import com.automatas.modelo.AFD;
import com.automatas.modelo.Gramatica;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Menú de Reportes.
 *
 * <p>Se solicita el nombre de la gramática o del AFD a usar y luego se
 * puede: <b>Ver Detalle</b> (todos sus elementos formales) o <b>Generar
 * Reporte</b> (archivo PDF con el detalle completo, la representación
 * gráfica del AFD en Graphviz, al menos 3 cadenas válidas y 3 inválidas, y
 * el historial de evaluaciones de la sesión). También permite generar solo
 * el grafo Graphviz (.dot y PNG) o verlo en pantalla en una ventana.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelReportes extends JPanel implements Refrescable {

    private final VentanaPrincipal ventana;

    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"AFD", "Gramática"});
    private final JComboBox<String> cmbNombre = new JComboBox<>();
    private final JTextArea txtSalida = new JTextArea(16, 60);

    /** Crea el panel de reportes (detalle, grafo y PDF). */
    public PanelReportes(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Reportes"));
        fila1.add(new JLabel("Tipo:"));
        fila1.add(cmbTipo);
        fila1.add(new JLabel("Nombre:"));
        fila1.add(cmbNombre);
        norte.add(fila1);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(boton("Ver Detalle", this::verDetalle));
        fila2.add(boton("Generar grafo Graphviz", this::generarGrafo));
        fila2.add(boton("Ver grafo en pantalla", this::verGrafo));
        fila2.add(boton("Generar Reporte PDF", this::generarPdf));
        norte.add(fila2);
        add(norte, BorderLayout.NORTH);

        txtSalida.setEditable(false);
        add(new JScrollPane(txtSalida), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(boton("Ayuda", ventana::mostrarAyuda));
        sur.add(boton("Volver al menú", () -> ventana.mostrar(VentanaPrincipal.MENU)));
        add(sur, BorderLayout.SOUTH);

        cmbTipo.addActionListener(e -> cargarNombres());
    }

    /** Actualiza el contenido del panel cada vez que se muestra. */
    @Override
    public void alMostrar() {
        cargarNombres();
    }

    private void cargarNombres() {
        cmbNombre.removeAllItems();
        if ("AFD".equals(cmbTipo.getSelectedItem())) {
            ventana.getRepositorio().nombresAfd().forEach(cmbNombre::addItem);
        } else {
            ventana.getRepositorio().nombresGramatica().forEach(cmbNombre::addItem);
        }
    }

    private boolean esAfd() {
        return "AFD".equals(cmbTipo.getSelectedItem());
    }

    private String nombreSeleccionado() {
        return (String) cmbNombre.getSelectedItem();
    }

    /** Muestra el detalle completo (elementos formales) del AFD o gramática. */
    private void verDetalle() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            txtSalida.setText("No hay ningún " + (esAfd() ? "AFD" : "gramática")
                    + " creado todavía.");
            return;
        }
        if (esAfd()) {
            txtSalida.setText(ventana.getRepositorio().obtenerAfd(nombre).detalle());
        } else {
            txtSalida.setText(ventana.getRepositorio().obtenerGramatica(nombre).detalle());
        }
    }

    /** Genera el archivo .dot y, si Graphviz está instalado, el PNG del grafo. */
    private void generarGrafo() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            txtSalida.setText("No hay ningún " + (esAfd() ? "AFD" : "gramática")
                    + " creado todavía.");
            return;
        }
        AFD afd = afdParaGrafo(nombre);
        if (afd == null) {
            return; // El error ya quedó en txtSalida.
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar grafo Graphviz (.dot)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivo Graphviz DOT (*.dot)", "dot"));
        chooser.setSelectedFile(new File(nombre + ".dot"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File dot = chooser.getSelectedFile();
        if (!dot.getName().toLowerCase().endsWith(".dot")) {
            dot = new File(dot.getParentFile(), dot.getName() + ".dot");
        }
        try {
            GeneradorGrafos.escribirDot(afd, dot);
            StringBuilder sb = new StringBuilder();
            sb.append("Grafo guardado en: ").append(dot.getAbsolutePath()).append(System.lineSeparator());
            File png = new File(dot.getParentFile(),
                    dot.getName().replaceFirst("(?i)\\.dot$", "") + ".png");
            if (GeneradorGrafos.renderizarPng(dot, png)) {
                sb.append("Imagen PNG generada en: ").append(png.getAbsolutePath());
            } else {
                sb.append("No se pudo generar el PNG: Graphviz no está instalado. "
                        + "Puede renderizarlo con: dot -Tpng ").append(dot.getName())
                        .append(" -o ").append(png.getName());
            }
            txtSalida.setText(sb.toString());
        } catch (Exception e) {
            txtSalida.setText("Error al generar el grafo: " + e.getMessage());
        }
    }

    /** Muestra el grafo en una ventana dentro de la aplicación. */
    private void verGrafo() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            txtSalida.setText("No hay ningún " + (esAfd() ? "AFD" : "gramática")
                    + " creado todavía.");
            return;
        }
        AFD afd = afdParaGrafo(nombre);
        if (afd == null) {
            return; // El error ya quedó en txtSalida.
        }
        try {
            String seguro = nombre.replaceAll("[^A-Za-z0-9_.-]", "_");
            File dot = File.createTempFile("grafo_" + seguro + "_", ".dot");
            dot.deleteOnExit();
            GeneradorGrafos.escribirDot(afd, dot);
            File png = new File(dot.getParentFile(),
                    dot.getName().replaceFirst("(?i)\\.dot$", "") + ".png");
            png.deleteOnExit();
            if (GeneradorGrafos.renderizarPng(dot, png)) {
                new VisorGrafo(ventana, nombre, png).setVisible(true);
            } else {
                String texto = "No se pudo generar el PNG: Graphviz no está instalado. "
                        + "Puede renderizarlo con: dot -Tpng " + dot.getName()
                        + " -o " + png.getName() + System.lineSeparator()
                        + System.lineSeparator() + GeneradorGrafos.aDot(afd);
                new VisorGrafo(ventana, nombre, texto).setVisible(true);
            }
        } catch (Exception e) {
            txtSalida.setText("Error al mostrar el grafo: " + e.getMessage());
        }
    }

    /** Genera el reporte PDF completo. */
    private void generarPdf() {
        String nombre = nombreSeleccionado();
        if (nombre == null) {
            txtSalida.setText("No hay ningún " + (esAfd() ? "AFD" : "gramática")
                    + " creado todavía.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("Documento PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("Reporte_" + nombre + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File pdf = chooser.getSelectedFile();
        if (!pdf.getName().toLowerCase().endsWith(".pdf")) {
            pdf = new File(pdf.getParentFile(), pdf.getName() + ".pdf");
        }
        try {
            List<String> validas;
            List<String> invalidas;
            AFD afd = afdParaGrafo(nombre);
            // Grafo: se genera junto al PDF (mismo nombre base + _grafo).
            File pngGrafo = null;
            String dotGrafo = null;
            if (afd != null) {
                dotGrafo = GeneradorGrafos.aDot(afd);
                File dotFile = new File(pdf.getParentFile(), nombre + "_grafo.dot");
                GeneradorGrafos.escribirDot(afd, dotFile);
                File pngFile = new File(pdf.getParentFile(), nombre + "_grafo.png");
                if (GeneradorGrafos.renderizarPng(dotFile, pngFile)) {
                    pngGrafo = pngFile;
                }
            } else {
                dotGrafo = "(no se pudo generar el grafo: ver mensaje anterior)";
            }
            List<com.automatas.logica.Repositorio.EvaluacionRegistrada> historial =
                    ventana.getRepositorio().historialDe(esAfd() ? "AFD" : "Gramática", nombre);
            if (esAfd()) {
                AFD a = ventana.getRepositorio().obtenerAfd(nombre);
                validas = GeneradorCadenas.validasParaAfd(a, 3);
                invalidas = GeneradorCadenas.invalidasParaAfd(a, 3);
                GeneradorPDF.generarReporteAfd(a, pdf, validas, invalidas, pngGrafo, dotGrafo, historial);
            } else {
                Gramatica g = ventana.getRepositorio().obtenerGramatica(nombre);
                validas = GeneradorCadenas.validasParaGramatica(g, 3);
                invalidas = GeneradorCadenas.invalidasParaGramatica(g, 3);
                GeneradorPDF.generarReporteGramatica(g, pdf, validas, invalidas, pngGrafo, dotGrafo, historial);
            }
            txtSalida.setText("Reporte PDF generado en: " + pdf.getAbsolutePath()
                    + System.lineSeparator()
                    + "Cadenas válidas incluidas: " + validas
                    + System.lineSeparator()
                    + "Cadenas inválidas incluidas: " + invalidas
                    + System.lineSeparator()
                    + "Evaluaciones del historial incluidas: " + historial.size());
        } catch (Exception e) {
            txtSalida.setText("Error al generar el reporte PDF: " + e.getMessage());
        }
    }

    /**
     * Devuelve el AFD que se grafica: el AFD mismo, o el AFD equivalente de
     * la gramática. Si la conversión falla (gramática no regular o no
     * determinista), muestra el error y devuelve {@code null}.
     */
    private AFD afdParaGrafo(String nombre) {
        if (esAfd()) {
            return ventana.getRepositorio().obtenerAfd(nombre);
        }
        try {
            return Convertidor.gramaticaAAfd(ventana.getRepositorio().obtenerGramatica(nombre));
        } catch (IllegalArgumentException e) {
            txtSalida.setText("No se pudo generar el grafo del AFD equivalente: " + e.getMessage()
                    + System.lineSeparator()
                    + "El reporte PDF se generará sin imagen del grafo.");
            return null;
        }
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }
}
