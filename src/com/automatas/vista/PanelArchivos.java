package com.automatas.vista;

import com.automatas.logica.ManejadorArchivos;
import com.automatas.modelo.AFD;
import com.automatas.modelo.Gramatica;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
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
 * Menú de Cargar archivo de entrada y Menú Guardar.
 *
 * <p><b>Cargar:</b> con un File Chooser se selecciona un archivo {@code .afd}
 * (AFD) o {@code .gtk} (gramática). El nombre del archivo es el nombre del
 * AFD o de la gramática dentro de la aplicación. El estado inicial del AFD
 * es el estado que esté al inicio del archivo, y el no terminal inicial de
 * la gramática es el que viene al inicio.</p>
 *
 * <p><b>Guardar:</b> se elige el AFD o la gramática y el nombre con el que
 * se desea guardar; se escribe el archivo {@code .afd} o {@code .gtk} con
 * el formato del enunciado.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelArchivos extends JPanel implements Refrescable {

    private final VentanaPrincipal ventana;

    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"AFD", "Gramática"});
    private final JComboBox<String> cmbNombre = new JComboBox<>();
    private final JTextArea txtMensajes = new JTextArea(10, 60);

    public PanelArchivos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JPanel cargar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cargar.setBorder(BorderFactory.createTitledBorder("Cargar archivo de entrada"));
        cargar.add(boton("Cargar archivo .afd", this::cargarAfd));
        cargar.add(boton("Cargar archivo .gtk", this::cargarGtk));
        centro.add(cargar);

        JPanel guardar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        guardar.setBorder(BorderFactory.createTitledBorder("Guardar (.afd / .gtk)"));
        guardar.add(new JLabel("Tipo:"));
        guardar.add(cmbTipo);
        guardar.add(new JLabel("Nombre:"));
        guardar.add(cmbNombre);
        guardar.add(boton("Guardar como...", this::guardarComo));
        centro.add(guardar);

        txtMensajes.setEditable(false);
        JPanel mensajes = new JPanel(new BorderLayout());
        mensajes.setBorder(BorderFactory.createTitledBorder("Mensajes"));
        mensajes.add(new JScrollPane(txtMensajes), BorderLayout.CENTER);
        centro.add(mensajes);

        add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(boton("Ayuda", ventana::mostrarAyuda));
        sur.add(boton("Volver al menú", () -> ventana.mostrar(VentanaPrincipal.MENU)));
        add(sur, BorderLayout.SOUTH);

        cmbTipo.addActionListener(e -> cargarNombres());
    }

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

    private void cargarAfd() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar archivo AFD (.afd)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivo AFD (*.afd)", "afd"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        try {
            AFD afd = ManejadorArchivos.cargarAfd(archivo);
            ventana.getRepositorio().guardarAfd(afd);
            mensaje("AFD '" + afd.getNombre() + "' cargado desde " + archivo.getName() + ": "
                    + afd.getEstados().size() + " estados, "
                    + afd.getAlfabeto().size() + " símbolos, "
                    + afd.getTransiciones().size() + " transiciones. Estado inicial: "
                    + afd.getEstadoInicial() + ".");
            cargarNombres();
        } catch (Exception e) {
            mensaje("Error al cargar el archivo .afd: " + e.getMessage());
        }
    }

    private void cargarGtk() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar archivo de gramática (.gtk)");
        chooser.setFileFilter(new FileNameExtensionFilter("Archivo de gramática (*.gtk)", "gtk"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File archivo = chooser.getSelectedFile();
        try {
            Gramatica g = ManejadorArchivos.cargarGramatica(archivo);
            ventana.getRepositorio().guardarGramatica(g);
            mensaje("Gramática '" + g.getNombre() + "' cargada desde " + archivo.getName() + ": "
                    + g.getNoTerminales().size() + " no terminales, "
                    + g.getTerminales().size() + " terminales, "
                    + g.getProducciones().size() + " producciones. Inicial: "
                    + g.getInicial() + ".");
            cargarNombres();
        } catch (Exception e) {
            mensaje("Error al cargar el archivo .gtk: " + e.getMessage());
        }
    }

    private void guardarComo() {
        String nombre = (String) cmbNombre.getSelectedItem();
        if (nombre == null) {
            mensaje("No hay ningún " + ("AFD".equals(cmbTipo.getSelectedItem()) ? "AFD" : "gramática")
                    + " creado todavía.");
            return;
        }
        boolean esAfd = "AFD".equals(cmbTipo.getSelectedItem());
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar " + (esAfd ? "AFD (.afd)" : "gramática (.gtk)"));
        chooser.setFileFilter(new FileNameExtensionFilter(
                esAfd ? "Archivo AFD (*.afd)" : "Archivo de gramática (*.gtk)",
                esAfd ? "afd" : "gtk"));
        chooser.setSelectedFile(new File(nombre + (esAfd ? ".afd" : ".gtk")));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            if (esAfd) {
                ManejadorArchivos.guardarAfd(ventana.getRepositorio().obtenerAfd(nombre),
                        chooser.getSelectedFile());
            } else {
                ManejadorArchivos.guardarGramatica(ventana.getRepositorio().obtenerGramatica(nombre),
                        chooser.getSelectedFile());
            }
            mensaje((esAfd ? "AFD '" : "Gramática '") + nombre + "' guardado en: "
                    + chooser.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {
            mensaje("Error al guardar: " + e.getMessage());
        }
    }

    private void mensaje(String texto) {
        txtMensajes.append(texto + System.lineSeparator());
        txtMensajes.setCaretPosition(txtMensajes.getDocument().getLength());
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }
}
