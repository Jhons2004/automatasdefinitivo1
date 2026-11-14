package com.automatas.vista;

import com.automatas.logica.ResultadoEvaluacion;
import com.automatas.logica.ValidadorAFD;
import com.automatas.logica.ValidadorGramatica;
import com.automatas.modelo.AFD;
import com.automatas.modelo.Gramatica;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Menú de Evaluar Cadenas.
 *
 * <p>Se solicita el nombre de la gramática o del AFD a usar y luego una
 * cadena. Con "Solo validar" se muestra "cadena válida" o "cadena
 * inválida", junto con la ruta (AFD) o la expansión (gramática) que lo
 * demuestra, con los formatos del enunciado. Cada evaluación queda
 * registrada en el historial del repositorio (para los reportes).</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelEvaluar extends JPanel implements Refrescable {

    private final VentanaPrincipal ventana;

    private final JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"AFD", "Gramática"});
    private final JComboBox<String> cmbNombre = new JComboBox<>();
    private final JTextField txtCadena = new JTextField(25);
    private final JTextArea txtResultado = new JTextArea(14, 60);

    public PanelEvaluar(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel norte = new JPanel();
        norte.setLayout(new BoxLayout(norte, BoxLayout.Y_AXIS));
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila1.add(new JLabel("Evaluar Cadenas"));
        fila1.add(new JLabel("Tipo:"));
        fila1.add(cmbTipo);
        fila1.add(new JLabel("Nombre:"));
        fila1.add(cmbNombre);
        norte.add(fila1);
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fila2.add(new JLabel("Cadena a evaluar:"));
        fila2.add(txtCadena);
        JButton btnValidar = new JButton("Solo validar");
        btnValidar.addActionListener(e -> validar());
        fila2.add(btnValidar);
        norte.add(fila2);
        add(norte, BorderLayout.NORTH);

        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        add(new JScrollPane(txtResultado), BorderLayout.CENTER);

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

    /** Valida la cadena y muestra la ruta o expansión con el resultado. */
    private void validar() {
        String nombre = (String) cmbNombre.getSelectedItem();
        if (nombre == null) {
            txtResultado.setText("No hay ningún "
                    + ("AFD".equals(cmbTipo.getSelectedItem()) ? "AFD" : "gramática")
                    + " creado todavía. Créelo primero en su menú o cárguelo desde un archivo.");
            return;
        }
        String cadena = txtCadena.getText().trim();
        StringBuilder sb = new StringBuilder();
        try {
            if ("AFD".equals(cmbTipo.getSelectedItem())) {
                AFD afd = ventana.getRepositorio().obtenerAfd(nombre);
                ResultadoEvaluacion r = ValidadorAFD.validar(afd, cadena);
                sb.append("Ruta en AFD: ").append(r.getRecorrido()).append(System.lineSeparator());
                sb.append("Resultado: ").append(r.esValida() ? "cadena válida" : "cadena inválida");
                if (!r.esValida()) {
                    sb.append(System.lineSeparator()).append("Motivo: ").append(r.getMensaje());
                }
                ventana.getRepositorio().registrarEvaluacion("AFD", nombre, cadena, r.esValida());
            } else {
                Gramatica g = ventana.getRepositorio().obtenerGramatica(nombre);
                ResultadoEvaluacion r = ValidadorGramatica.validar(g, cadena);
                sb.append("Expansión Gramática: ").append(r.getRecorrido()).append(System.lineSeparator());
                sb.append("Resultado: ").append(r.esValida() ? "cadena válida" : "cadena inválida");
                if (!r.esValida()) {
                    sb.append(System.lineSeparator()).append("Motivo: ").append(r.getMensaje());
                }
                ventana.getRepositorio().registrarEvaluacion("Gramática", nombre, cadena, r.esValida());
            }
        } catch (IllegalArgumentException e) {
            sb.append("Error: ").append(e.getMessage());
        }
        txtResultado.setText(sb.toString());
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.addActionListener(e -> accion.run());
        return b;
    }
}
