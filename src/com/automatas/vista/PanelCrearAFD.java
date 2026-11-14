package com.automatas.vista;

import com.automatas.logica.Convertidor;
import com.automatas.logica.ParserTransiciones;
import com.automatas.modelo.AFD;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
 * Menú de Crear AFD.
 *
 * <p>Primero se solicita el nombre del AFD (es el que se usará dentro de la
 * aplicación) y luego se muestran las opciones: ingresar estados (uno por
 * uno), ingresar alfabeto (uno por uno), estado inicial, estados de
 * aceptación y transiciones en modo 1 o en modo 2. Todas las validaciones
 * del enunciado las hace el modelo ({@link AFD}) y los analizadores de
 * transiciones; este panel solo recoge los datos y muestra los mensajes.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelCrearAFD extends JPanel implements Refrescable {

    private final VentanaPrincipal ventana;
    private AFD actual;

    private final JTextField txtNombre = new JTextField(18);
    private final JLabel lblActual = new JLabel("Sin AFD creado todavía.");
    private final JTextField txtEstado = new JTextField(10);
    private final JTextField txtSimbolo = new JTextField(10);
    private final JTextField txtInicial = new JTextField(10);
    private final JTextField txtAceptacion = new JTextField(10);
    private final JTextField txtModo1 = new JTextField(30);
    private final JTextField txtModo2 = new JTextField(30);
    private final DefaultListModel<String> modeloEstados = new DefaultListModel<>();
    private final DefaultListModel<String> modeloAlfabeto = new DefaultListModel<>();
    private final DefaultListModel<String> modeloAceptacion = new DefaultListModel<>();
    private final JTextArea txtTransiciones = new JTextArea(6, 60);
    private final JTextArea txtMensajes = new JTextArea(5, 60);

    public PanelCrearAFD(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel norte = new JPanel(new FlowLayout(FlowLayout.LEFT));
        norte.add(new JLabel("Crear AFD"));
        norte.add(new JLabel("Nombre del AFD:"));
        norte.add(txtNombre);
        JButton btnNuevo = new JButton("Nuevo AFD");
        btnNuevo.addActionListener(e -> nuevoAfd());
        norte.add(btnNuevo);
        norte.add(lblActual);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(seccion("1. Ingresar Estados (uno por uno)",
                new JLabel("Estado:"), txtEstado, boton("Agregar estado", this::agregarEstado),
                new JScrollPane(new JList<>(modeloEstados))));
        centro.add(seccion("2. Ingresar Alfabeto (uno por uno)",
                new JLabel("Símbolo:"), txtSimbolo, boton("Agregar símbolo", this::agregarSimbolo),
                new JScrollPane(new JList<>(modeloAlfabeto))));
        centro.add(seccion("3. Estado Inicial (debe existir en los estados)",
                new JLabel("Estado inicial:"), txtInicial, boton("Definir inicial", this::definirInicial)));
        centro.add(seccion("4. Estado(s) de aceptación (deben existir en los estados)",
                new JLabel("Estado:"), txtAceptacion, boton("Agregar aceptación", this::agregarAceptacion),
                new JScrollPane(new JList<>(modeloAceptacion))));
        centro.add(seccion("5. Transiciones — Modo 1 (una a la vez: A,B,0;false,true)",
                new JLabel("Transición:"), txtModo1, boton("Agregar (Modo 1)", this::aplicarModo1)));
        centro.add(seccion("6. Transiciones — Modo 2 (tabla: [a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -])",
                new JLabel("Tabla:"), txtModo2, boton("Aplicar tabla (Modo 2)", this::aplicarModo2)));
        txtTransiciones.setEditable(false);
        centro.add(seccionConArea("Transiciones actuales", txtTransiciones));
        txtMensajes.setEditable(false);
        centro.add(seccionConArea("Mensajes", txtMensajes));

        add(new JScrollPane(centro), BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(boton("Generar gramática de este AFD", this::generarGramatica));
        sur.add(boton("Ver detalle", this::verDetalle));
        sur.add(boton("Ayuda", ventana::mostrarAyuda));
        sur.add(boton("Volver al menú", () -> ventana.mostrar(VentanaPrincipal.MENU)));
        add(sur, BorderLayout.SOUTH);

        txtModo1.setToolTipText("Ejemplo: A,B,0;false,true");
        txtModo2.setToolTipText("Ejemplo: [a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]");
    }

    @Override
    public void alMostrar() {
        refrescar();
    }

    /** Crea el AFD con el nombre escrito (o avisa si ya existe). */
    private void nuevoAfd() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mensaje("Error: escriba primero el nombre del AFD.");
            return;
        }
        actual = new AFD(nombre);
        boolean reemplazado = ventana.getRepositorio().guardarAfd(actual);
        lblActual.setText("AFD actual: " + nombre + (reemplazado ? " (reemplazó al anterior con ese nombre)" : ""));
        txtNombre.setText("");
        refrescar();
        mensaje("AFD '" + nombre + "' creado. Ahora agregue estados, alfabeto, estado inicial, "
                + "aceptación y transiciones.");
    }

    private void agregarEstado() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.agregarEstado(txtEstado.getText().trim());
            mensaje("Estado '" + txtEstado.getText().trim() + "' agregado.");
            txtEstado.setText("");
            refrescar();
        });
    }

    private void agregarSimbolo() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.agregarSimbolo(txtSimbolo.getText().trim());
            mensaje("Símbolo '" + txtSimbolo.getText().trim() + "' agregado al alfabeto.");
            txtSimbolo.setText("");
            refrescar();
        });
    }

    private void definirInicial() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.setEstadoInicial(txtInicial.getText().trim());
            mensaje("Estado inicial definido: " + txtInicial.getText().trim() + ".");
            txtInicial.setText("");
            refrescar();
        });
    }

    private void agregarAceptacion() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            actual.agregarEstadoAceptacion(txtAceptacion.getText().trim());
            mensaje("Estado '" + txtAceptacion.getText().trim() + "' agregado a los de aceptación.");
            txtAceptacion.setText("");
            refrescar();
        });
    }

    private void aplicarModo1() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            String resumen = ParserTransiciones.aplicarModo1(actual, txtModo1.getText());
            mensaje(resumen);
            txtModo1.setText("");
            refrescar();
        });
    }

    private void aplicarModo2() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            String resumen = ParserTransiciones.aplicarModo2(actual, txtModo2.getText());
            mensaje(resumen);
            txtModo2.setText("");
            refrescar();
        });
    }

    /** Genera la gramática equivalente de este AFD (conversión AFD → gramática). */
    private void generarGramatica() {
        if (!hayActual()) {
            return;
        }
        ejecutar(() -> {
            com.automatas.modelo.Gramatica g = Convertidor.afdAGramatica(actual);
            ventana.getRepositorio().guardarGramatica(g);
            mensaje("Gramática '" + g.getNombre() + "' generada a partir del AFD. "
                    + "Puede verla en Crear Gramática o en Reportes.");
        });
    }

    private void verDetalle() {
        if (!hayActual()) {
            return;
        }
        mostrarTextoEnDialogo("Detalle del AFD '" + actual.getNombre() + "'", actual.detalle());
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
            mensaje("Primero escriba el nombre del AFD y presione 'Nuevo AFD'.");
            return false;
        }
        return true;
    }

    private void refrescar() {
        modeloEstados.clear();
        modeloAlfabeto.clear();
        modeloAceptacion.clear();
        if (actual == null) {
            txtTransiciones.setText("(sin AFD)");
            return;
        }
        actual.getEstados().forEach(modeloEstados::addElement);
        actual.getAlfabeto().forEach(modeloAlfabeto::addElement);
        actual.getEstadosAceptacion().forEach(modeloAceptacion::addElement);
        StringBuilder sb = new StringBuilder();
        sb.append("Inicial: ").append(actual.getEstadoInicial() == null ? "(sin definir)" : actual.getEstadoInicial());
        sb.append(System.lineSeparator());
        for (com.automatas.modelo.Transicion t : actual.getTransiciones()) {
            sb.append(t).append(";").append(actual.esEstadoAceptacion(t.getOrigen()))
              .append(",").append(actual.esEstadoAceptacion(t.getDestino()))
              .append(System.lineSeparator());
        }
        txtTransiciones.setText(sb.toString());
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
