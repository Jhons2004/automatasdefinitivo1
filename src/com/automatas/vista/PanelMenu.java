package com.automatas.vista;

import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Menú principal de la aplicación.
 *
 * <p>Muestra las opciones del enunciado: Crear AFD, Crear Gramática,
 * Evaluar Cadenas, Reportes, Cargar archivo de entrada, Guardar y
 * Salir / Cerrar programa. Cada opción limpia la pantalla y muestra las
 * opciones disponibles para esa tarea.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class PanelMenu extends JPanel {

    /** Crea el menú principal con las opciones de la aplicación. */
    public PanelMenu(VentanaPrincipal ventana) {
        setLayout(null);
        setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel titulo = new JLabel("Menú Principal", SwingConstants.CENTER);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titulo.setBounds(60, 25, 740, 40);
        add(titulo);

        JPanel botones = new JPanel(new GridLayout(4, 2, 15, 15));
        botones.setBounds(60, 90, 740, 380);
        botones.add(boton("1. Crear AFD", () -> ventana.mostrar(VentanaPrincipal.CREAR_AFD)));
        botones.add(boton("2. Crear Gramática", () -> ventana.mostrar(VentanaPrincipal.CREAR_GRAMATICA)));
        botones.add(boton("3. Evaluar Cadenas", () -> ventana.mostrar(VentanaPrincipal.EVALUAR)));
        botones.add(boton("4. Reportes", () -> ventana.mostrar(VentanaPrincipal.REPORTES)));
        botones.add(boton("5. Cargar archivo de entrada", () -> ventana.mostrar(VentanaPrincipal.ARCHIVOS)));
        botones.add(boton("6. Guardar (.afd / .gtk)", () -> ventana.mostrar(VentanaPrincipal.ARCHIVOS)));
        botones.add(boton("7. Ayuda", ventana::mostrarAyuda));
        botones.add(boton("8. Salir / Cerrar programa", () -> {
            int r = JOptionPane.showConfirmDialog(ventana,
                    "¿Desea cerrar el programa?", "Salir",
                    JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }));
        add(botones);

        JLabel pie = new JLabel("Seleccione una opción para continuar.", SwingConstants.CENTER);
        pie.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        pie.setBounds(60, 490, 740, 25);
        add(pie);
    }

    private JButton boton(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        b.addActionListener(e -> accion.run());
        return b;
    }
}
