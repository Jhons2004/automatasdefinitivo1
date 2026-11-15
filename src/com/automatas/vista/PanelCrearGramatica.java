package com.automatas.vista;

import com.automatas.logica.Convertidor;
import com.automatas.modelo.Gramatica;
import com.automatas.modelo.Produccion;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Menú de Crear Gramática.
 *
 * <p>Primero se solicita el nombre de la gramática (es el que se usará
 * dentro de la aplicación) y luego se muestran las opciones: ingresar no
 * terminales (uno por uno), ingresar terminales (uno por uno), no terminal
 * inicial y producciones (una a la vez, con la notación {@code A > a B}).
 * La disyunción se puede escribir con {@code |} o con dos producciones con
 * el mismo no terminal a la izquierda. Todas las validaciones del enunciado
 * las hace el modelo ({@link Gramatica}); este panel solo recoge los datos
 * y muestra los mensajes.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelCrearGramatica extends JPanel implements Refrescable {

    private final VentanaPrincipal ventana;
    private Gramatica actual;

    private final JTextField txtNombre = new JTextField(18);
    private final JLabel lblActual = new JLabel("Sin gramática creada todavía.");
    private final JTextField txtNt = new JTextField(10);
    private final JTextField txtTerminal = new JTextField(10);
    private final JTextField txtInicial = new JTextField(10);
    private final JTextField txtProduccion = new JTextField(30);
    private final DefaultListModel<String> modeloNt = new DefaultListModel<>();
    private final DefaultListModel<String> modeloTerminales = new DefaultListModel<>();
    private final DefaultListModel<String> modeloProducciones = new DefaultListModel<>();
    private final JTextArea txtMensajes = new JTextArea(5, 60);

    /** Crea el panel para definir una gramática regular. */
    public PanelCrearGramatica(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Crear Gramática"));
        norte.add(new JLabel("Nombre de la gramática:"));
        norte.add(txtNombre);
        JButton btnNueva = new JButton("Nueva gramática");
        btnNueva.addActionListener(e -> nuevaGramatica());
        norte.add(btnNueva);
        norte.add(lblActual);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(seccion("1. Ingresar NT — No Terminales (uno por uno)",
                new JLabel("No Terminal:"), txtNt, boton("Agregar NT", this::agregarNt),
                new JScrollPane(new JList<>(modeloNt))));
        centro.add(seccion("2. Ingresar Terminales (uno por uno)",
                new JLabel("Terminal:"), txtTerminal, boton("Agregar terminal", this::agregarTerminal),
                new JScrollPane(new JList<>(modeloTerminales))));
        centro.add(seccion("3. NT Inicial (debe existir en los No Terminales)",
                new JLabel("NT inicial:"), txtInicial, boton("Definir inicial", this::definirInicial)));
        centro.add(seccion("4. Producciones (una a la vez: A > a B  |  A > a B | b  |  A > epsilon)",
                new JLabel("Producción:"), txtProduccion, boton("Agregar producción", this::agregarProduccion),
                new JScrollPane(new JList<>(modeloProducciones))));
        txtMensajes.setEditable(false);
        centro.add(seccionConArea("Mensajes", txtMensajes));

        add(new JScrollPane(centro), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(boton("Generar AFD de esta gramática", this::generarAfd));
        sur.add(boton("Ver detalle", this::verDetalle));
        sur.add(boton("Ayuda", ventana::mostrarAyuda));
        sur.add(boton("Volver al menú", () -> ventana.mostrar(VentanaPrincipal.MENU)));
        add(sur, BorderLayout.SOUTH);

        txtProduccion.setToolTipText("Ejemplos: A > a B   |   A > 0B | 1A | epsilon");
    }

    /** Actualiza el contenido del panel cada vez que se muestra. */
    @Override
    public void alMostrar() {
        refrescar();
    }

    /** Crea la gramática con el nombre escrito (o avisa si ya existe). */
    private void nuevaGramatica() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mensaje("Error: escriba primero el nombre de la gramática.");
            return;
        }
        actual = new Gramatica(nombre);
        boolean reemplazado = ventana.getRepositorio().guardarGramatica(actual);
        lblActual.setText("Gramática actual: " + nombre
                + (reemplazado ? " (reemplazó a la anterior con ese nombre)" : ""));
        txtNombre.setText("");
        refrescar();
        mensaje("Gramática '" + nombre + "' creada. Ahora agregue No Terminales, Terminales, "
                + "NT inicial y producciones.");
    }

    private void agregarNt() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.agregarNoTerminal(txtNt.getText().trim());
            mensaje("No Terminal '" + txtNt.getText().trim() + "' agregado.");
            txtNt.setText("");
            refrescar();
        });
    }

    private void agregarTerminal() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.agregarTerminal(txtTerminal.getText().trim());
            mensaje("Terminal '" + txtTerminal.getText().trim() + "' agregado.");
            txtTerminal.setText("");
            refrescar();
        });
    }

    private void definirInicial() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.setInicial(txtInicial.getText().trim());
            mensaje("NT inicial definido: " + txtInicial.getText().trim() + ".");
            txtInicial.setText("");
            refrescar();
        });
    }

    private void agregarProduccion() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            List<Produccion> agregadas = actual.agregarProduccionTexto(txtProduccion.getText());
            for (Produccion p : agregadas) {
                mensaje("Producción agregada: " + p + ".");
            }
            txtProduccion.setText("");
            refrescar();
        });
    }

    /** Genera el AFD equivalente de esta gramática (conversión gramática → AFD). */
    private void generarAfd() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            com.automatas.modelo.AFD afd = Convertidor.gramaticaAAfd(actual);
            ventana.getRepositorio().guardarAfd(afd);
            mensaje("AFD '" + afd.getNombre() + "' generado a partir de la gramática. "
                    + "Puede verlo en Crear AFD o en Reportes.");
        });
    }

    private void verDetalle() {
        if (!hayActual()) {
            return;
        }
        mostrarTextoEnDialogo("Detalle de la gramática '" + actual.getNombre() + "'", actual.detalle());
    }

    /** Ejecuta una acción mostrando el error en el área de mensajes si falla. */
    private void ejecutar(Accion accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException e) {
            mensaje("Error: " + e.getMessage());
        }
    }

    private boolean hayActual() {
        if (actual == null) {
            mensaje("Primero escriba el nombre de la gramática y presione 'Nueva gramática'.");
            return false;
        }
        return true;
    }

    private void refrescar() {
        modeloNt.clear();
        modeloTerminales.clear();
        modeloProducciones.clear();
        if (actual == null) {
            return;
        }
        actual.getNoTerminales().forEach(modeloNt::addElement);
        actual.getTerminales().forEach(modeloTerminales::addElement);
        actual.getProducciones().forEach(p -> modeloProducciones.addElement(p.toString()));
    }

    private void mensaje(String texto) {
        txtMensajes.append(texto + System.lineSeparator());
        txtMensajes.setCaretPosition(txtMensajes.getDocument().getLength());
    }

    private JPanel seccion(String titulo, java.awt.Component... componentes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder(titulo));
        for (java.awt.Component c : componentes) {
            p.add(c);
        }
        return p;
    }

    private JPanel seccionConArea(String titulo, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(new TitledBorder(titulo));
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }

    private void mostrarTextoEnDialogo(String titulo, String texto) {
        JTextArea area = new JTextArea(texto, 20, 60);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Acción que puede lanzar IllegalArgumentException (validaciones). */
    private interface Accion {
        void run();
    }
}
