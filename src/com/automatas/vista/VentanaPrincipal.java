package com.automatas.vista;

import com.automatas.logica.Repositorio;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Ventana principal de la aplicación.
 *
 * <p>Usa un {@link CardLayout}: cada "pantalla" del enunciado (portada, menú
 * principal, crear AFD, crear gramática, evaluar cadenas, reportes y
 * archivos) es una tarjeta. Al cambiar de pantalla se "limpia" la anterior
 * y se muestra la nueva, como pide el enunciado. La ventana guarda el
 * {@link Repositorio} central, que comparten todas las pantallas.</p>
 *
 * @author Jonatan Abdiel Alvarado Villavicencio
 * @carné 0910-22-8487
 * @curso Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
 * @catedrático Alan Gonzalo Ucelo Moran
 */
public class VentanaPrincipal extends JFrame {

    public static final String PORTADA = "portada";
    public static final String MENU = "menu";
    public static final String CREAR_AFD = "crearAFD";
    public static final String CREAR_GRAMATICA = "crearGramatica";
    public static final String EVALUAR = "evaluar";
    public static final String REPORTES = "reportes";
    public static final String ARCHIVOS = "archivos";

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenedor = new JPanel(cartas);
    private final Repositorio repositorio = new Repositorio();

    private final PanelPortada panelPortada;
    private final PanelMenu panelMenu;
    private final PanelCrearAFD panelCrearAFD;
    private final PanelCrearGramatica panelCrearGramatica;
    private final PanelEvaluar panelEvaluar;
    private final PanelReportes panelReportes;
    private final PanelArchivos panelArchivos;

    /** Crea la ventana principal de la aplicación. */
    public VentanaPrincipal() {
        super("Autómatas y Lenguajes Formales — AFD y Gramáticas Regulares");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 700);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null);

        panelPortada = new PanelPortada(this);
        panelMenu = new PanelMenu(this);
        panelCrearAFD = new PanelCrearAFD(this);
        panelCrearGramatica = new PanelCrearGramatica(this);
        panelEvaluar = new PanelEvaluar(this);
        panelReportes = new PanelReportes(this);
        panelArchivos = new PanelArchivos(this);

        contenedor.add(panelPortada, PORTADA);
        contenedor.add(panelMenu, MENU);
        contenedor.add(panelCrearAFD, CREAR_AFD);
        contenedor.add(panelCrearGramatica, CREAR_GRAMATICA);
        contenedor.add(panelEvaluar, EVALUAR);
        contenedor.add(panelReportes, REPORTES);
        contenedor.add(panelArchivos, ARCHIVOS);

        add(contenedor);
        mostrar(PORTADA);
    }

    /** Repositorio central con los AFD, las gramáticas y el historial. */
    public Repositorio getRepositorio() {
        return repositorio;
    }

    /**
     * Muestra una pantalla y, si el panel lo necesita, actualiza su
     * contenido (listas de AFD y gramáticas, etc.).
     */
    public void mostrar(String nombre) {
        cartas.show(contenedor, nombre);
        JPanel panel = panelDe(nombre);
        if (panel instanceof Refrescable) {
            ((Refrescable) panel).alMostrar();
        }
    }

    private JPanel panelDe(String nombre) {
        switch (nombre) {
            case PORTADA: return panelPortada;
            case MENU: return panelMenu;
            case CREAR_AFD: return panelCrearAFD;
            case CREAR_GRAMATICA: return panelCrearGramatica;
            case EVALUAR: return panelEvaluar;
            case REPORTES: return panelReportes;
            case ARCHIVOS: return panelArchivos;
            default: return panelMenu;
        }
    }

    /** Muestra la ventana de Ayuda (datos del curso y del catedrático). */
    public void mostrarAyuda() {
        new DialogoAyuda(this).setVisible(true);
    }
}
