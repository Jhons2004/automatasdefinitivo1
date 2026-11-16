package com.automatas.app;

import com.automatas.vista.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación.
 *
 * <p>Configura la apariencia (Nimbus, si está disponible) y abre la ventana
 * principal en la portada. Todo el trabajo se hace en el hilo de despacho
 * de eventos de Swing (EDT), como exige Swing.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public final class Main {

    private Main() {
        // Clase de entrada: no se instancia.
    }

    /** Punto de entrada: aplica Nimbus si está disponible y abre la ventana principal en el EDT. */
    public static void main(String[] args) {
        // Apariencia Nimbus (la más limpia que trae Java por defecto).
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, se usa la apariencia por defecto.
            System.err.println("No se pudo aplicar Nimbus: " + e.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
