package com.automatas.vista;

import com.automatas.modelo.DatosDelCurso;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Ventana de Ayuda.
 *
 * <p>Muestra los datos del curso, el nombre del catedrático y el último
 * dígito del carné, como pide el enunciado. Está disponible desde el menú
 * principal y desde cada submenú.</p>
 *
 * @author Jonatan Abdiel Alvararado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class DialogoAyuda extends JDialog {

    public DialogoAyuda(VentanaPrincipal ventana) {
        super(ventana, "Ayuda", true);
        setLayout(new BorderLayout(10, 10));
        setSize(520, 340);
        setLocationRelativeTo(ventana);

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setBorder(BorderFactory.createEmptyBorder(20, 25, 10, 25));
        centro.add(etiqueta("Ayuda", 20, true));
        centro.add(etiqueta(" ", 12, false));
        centro.add(etiqueta(DatosDelCurso.UNIVERSIDAD, 13, false));
        centro.add(etiqueta(DatosDelCurso.FACULTAD, 13, false));
        centro.add(etiqueta("Curso: " + DatosDelCurso.CURSO + " — " + DatosDelCurso.CICLO, 14, true));
        centro.add(etiqueta("Sección: " + DatosDelCurso.SECCION, 14, false));
        centro.add(etiqueta("Catedrático: " + DatosDelCurso.CATEDRATICO, 14, false));
        centro.add(etiqueta("Autor: " + DatosDelCurso.AUTOR, 14, false));
        centro.add(etiqueta("Último dígito del carné: " + DatosDelCurso.ULTIMO_DIGITO_CARNE, 14, true));
        add(centro, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar");
        cerrar.addActionListener(e -> dispose());
        JPanel sur = new JPanel();
        sur.add(cerrar);
        add(sur, BorderLayout.SOUTH);
    }

    private JLabel etiqueta(String texto, int tamano, boolean negrita) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font(Font.SANS_SERIF, negrita ? Font.BOLD : Font.PLAIN, tamano));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }
}
