package com.automatas.vista;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Visor del grafo del AFD en pantalla.
 *
 * <p>Muestra el grafo renderizado por Graphviz como una imagen en una
 * ventana modal con scroll. Si Graphviz no está instalado y el PNG no se
 * pudo generar, muestra el texto DOT del grafo con las instrucciones para
 * renderizarlo a mano.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class VisorGrafo extends JDialog {

    /** Muestra el PNG del grafo en una ventana modal con scroll. */
    public VisorGrafo(VentanaPrincipal ventana, String nombre, File png) {
        super(ventana, "Grafo de " + nombre, true);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(ventana);
        BufferedImage imagen = null;
        try {
            imagen = ImageIO.read(png);
        } catch (IOException e) {
            // La imagen queda en null y se muestra el mensaje de error.
        }
        if (imagen != null) {
            add(new JScrollPane(new JLabel(new ImageIcon(imagen))), BorderLayout.CENTER);
        } else {
            add(new JScrollPane(areaTexto("No se pudo leer la imagen del grafo.")),
                    BorderLayout.CENTER);
        }
        add(panelCerrar(), BorderLayout.SOUTH);
    }

    /** Muestra el texto DOT del grafo cuando el PNG no se pudo generar. */
    public VisorGrafo(VentanaPrincipal ventana, String nombre, String texto) {
        super(ventana, "Grafo de " + nombre, true);
        setLayout(new BorderLayout(10, 10));
        setSize(800, 600);
        setLocationRelativeTo(ventana);
        add(new JScrollPane(areaTexto(texto)), BorderLayout.CENTER);
        add(panelCerrar(), BorderLayout.SOUTH);
    }

    private JTextArea areaTexto(String texto) {
        JTextArea area = new JTextArea(texto);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        return area;
    }

    private JPanel panelCerrar() {
        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel();
        sur.add(cerrar);
        return sur;
    }
}
