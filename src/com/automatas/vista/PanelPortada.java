package com.automatas.vista;

import com.automatas.modelo.DatosDelCurso;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Pantalla principal (portada / carátula) de la aplicación.
 *
 * <p>Muestra el nombre del curso, la facultad, la sección y el carné del
 * autor, como pide el enunciado. Al presionar ENTER (o el botón
 * "Continuar") se dirige al menú principal.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelPortada extends JPanel {

    /** Crea la portada con los datos del curso y del autor. */
    public PanelPortada(VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(etiqueta(DatosDelCurso.UNIVERSIDAD, 16, true));
        centro.add(etiqueta(DatosDelCurso.FACULTAD, 14, false));
        centro.add(Box.createVerticalStrut(30));
        centro.add(etiqueta(DatosDelCurso.CURSO, 26, true));
        centro.add(etiqueta(DatosDelCurso.CICLO + " — Sección " + DatosDelCurso.SECCION, 16, false));
        centro.add(Box.createVerticalStrut(30));
        centro.add(etiqueta("Proyecto: Software de Autómatas Finitos", 18, true));
        centro.add(etiqueta("Deterministas y Gramáticas Regulares", 18, true));
        centro.add(Box.createVerticalStrut(30));
        centro.add(etiqueta("Autor: " + DatosDelCurso.AUTOR, 15, false));
        centro.add(etiqueta("Carné: " + DatosDelCurso.CARNE, 15, false));
        centro.add(etiqueta("Catedrático: " + DatosDelCurso.CATEDRATICO, 15, false));
        centro.add(Box.createVerticalStrut(40));

        JButton continuar = new JButton("Continuar al menú  (ENTER)");
        continuar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        continuar.addActionListener(e -> ventana.mostrar(VentanaPrincipal.MENU));
        JPanel panelBoton = new JPanel();
        panelBoton.add(continuar);
        centro.add(panelBoton);
        centro.add(Box.createVerticalStrut(10));
        centro.add(etiqueta("Presione ENTER para continuar", 12, false));

        add(centro, BorderLayout.CENTER);

        // Tecla ENTER: ir al menú principal.
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "irMenu");
        getActionMap().put("irMenu", new AbstractAction() {
            /** Tecla ENTER: pasa al menú principal. */
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.mostrar(VentanaPrincipal.MENU);
            }
        });
    }

    private JLabel etiqueta(String texto, int tamano, boolean negrita) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font(Font.SANS_SERIF, negrita ? Font.BOLD : Font.PLAIN, tamano));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }
}
