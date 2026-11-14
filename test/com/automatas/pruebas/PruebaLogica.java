package com.automatas.pruebas;

import com.automatas.logica.Convertidor;
import com.automatas.logica.GeneradorCadenas;
import com.automatas.logica.GeneradorGrafos;
import com.automatas.logica.GeneradorPDF;
import com.automatas.logica.ManejadorArchivos;
import com.automatas.logica.ParserTransiciones;
import com.automatas.logica.ResultadoEvaluacion;
import com.automatas.logica.ValidadorAFD;
import com.automatas.logica.ValidadorGramatica;
import com.automatas.modelo.AFD;
import com.automatas.modelo.Gramatica;
import java.io.File;
import java.util.List;

/**
 * Harness de verificación de la lógica (no se distribuye en el repo).
 * Ejecuta los ejemplos del enunciado y comprueba los resultados esperados.
 */
public class PruebaLogica {
    private static int ok = 0;
    private static int fallo = 0;

    public static void main(String[] args) throws Exception {
        // ---------- 1. AFD del ejemplo del enunciado (modo 2) ----------
        AFD afd = new AFD("EjemploAFD");
        String resumen = ParserTransiciones.aplicarModo2(afd,
                "[a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]");
        afd.setEstadoInicial("A");
        check("modo2: 6 transiciones", afd.getTransiciones().size() == 6);
        check("modo2: aceptacion B,D", afd.esEstadoAceptacion("B") && afd.esEstadoAceptacion("D"));
        check("modo2: D sin transiciones", afd.buscarTransicion("D", "a") == null);

        ResultadoEvaluacion r = ValidadorAFD.validar(afd, "aababb");
        check("AFD aababb valida", r.esValida());
        check("AFD aababb ruta exacta",
                "A, A, a; A, A, a; A, C, b; C, B, a; B, C, b; C, D, b".equals(r.getRecorrido()));
        check("AFD aabab invalida (termina en C)",
                !ValidadorAFD.validar(afd, "aabab").esValida());
        check("AFD aababbx invalida (x no es del alfabeto)",
                !ValidadorAFD.validar(afd, "aababbx").esValida());
        check("AFD vacia invalida (A no es aceptacion)",
                !ValidadorAFD.validar(afd, "").esValida());

        // ---------- 2. Modo 1: errores que pide el enunciado ----------
        AFD afd2 = new AFD("Modo1");
        afd2.agregarEstado("A"); afd2.agregarEstado("B");
        afd2.agregarSimbolo("0"); afd2.agregarSimbolo("1");
        afd2.setEstadoInicial("A");
        String m1 = ParserTransiciones.aplicarModo1(afd2, "A,B,0;false,true");
        check("modo1: transicion agregada", afd2.getTransiciones().size() == 1);
        check("modo1: B aceptacion por bandera", afd2.esEstadoAceptacion("B"));
        boolean dup = false;
        try { ParserTransiciones.aplicarModo1(afd2, "A,A,0;false,false"); }
        catch (IllegalArgumentException e) { dup = e.getMessage().contains("AFN"); }
        check("modo1: duplicado rechazado con mensaje AFN", dup);
        boolean eps = false;
        try { afd2.agregarTransicion("A", "B", "epsilon"); }
        catch (IllegalArgumentException e) { eps = e.getMessage().contains("epsilon"); }
        check("modo1: epsilon rechazado", eps);

        // ---------- 3. Gramática del ejemplo del enunciado ----------
        Gramatica g = new Gramatica("EjemploG");
        g.agregarNoTerminal("A"); g.agregarNoTerminal("B");
        g.agregarTerminal("0"); g.agregarTerminal("1");
        g.setInicial("A");
        g.agregarProduccionTexto("A > 0B | 1A | epsilon");
        g.agregarProduccionTexto("B > 0B | 1A");
        check("gramatica: 5 producciones", g.getProducciones().size() == 5);
        ResultadoEvaluacion rg = ValidadorGramatica.validar(g, "0011");
        check("gramatica 0011 valida", rg.esValida());
        check("gramatica 0011 expansion exacta",
                "A>0B>00B>001A>0011A>0011(epsilon)>0011".equals(rg.getRecorrido()));
        check("gramatica 0010 invalida", !ValidadorGramatica.validar(g, "0010").esValida());
        check("gramatica 0101 valida", ValidadorGramatica.validar(g, "0101").esValida());
        check("gramatica vacia valida (A>epsilon)", ValidadorGramatica.validar(g, "").esValida());
        check("gramatica 2 invalida (2 no es terminal)",
                !ValidadorGramatica.validar(g, "2").esValida());

        // ---------- 4. Conversiones ----------
        AFD afdDeG = Convertidor.gramaticaAAfd(g);
        check("conversion G->AFD: 0011 valida en AFD",
                ValidadorAFD.validar(afdDeG, "0011").esValida());
        check("conversion G->AFD: 0010 invalida en AFD",
                !ValidadorAFD.validar(afdDeG, "0010").esValida());
        Gramatica gDeAfd = Convertidor.afdAGramatica(afd);
        check("conversion AFD->G: aababb valida en G",
                ValidadorGramatica.validar(gDeAfd, "aababb").esValida());
        check("conversion AFD->G: aabab invalida en G",
                !ValidadorGramatica.validar(gDeAfd, "aabab").esValida());

        // ---------- 5. Carga de archivos de ejemplo ----------
        AFD cargadoAfd = ManejadorArchivos.cargarAfd(new File("ejemplos/ejemplo_afd.afd"));
        check("carga .afd: nombre del archivo", "ejemplo_afd".equals(cargadoAfd.getNombre()));
        check("carga .afd: inicial A", "A".equals(cargadoAfd.getEstadoInicial()));
        check("carga .afd: aababb valida",
                ValidadorAFD.validar(cargadoAfd, "aababb").esValida());
        Gramatica cargadaG = ManejadorArchivos.cargarGramatica(new File("ejemplos/ejemplo_gramatica.gtk"));
        check("carga .gtk: nombre del archivo", "ejemplo_gramatica".equals(cargadaG.getNombre()));
        check("carga .gtk: inicial A", "A".equals(cargadaG.getInicial()));
        check("carga .gtk: 0011 valida",
                ValidadorGramatica.validar(cargadaG, "0011").esValida());
        Gramatica simple = ManejadorArchivos.cargarGramatica(new File("ejemplos/ejemplo_simple.gtk"));
        // A>Ab, A>bC, B>c, C>d — con la regla del PDF (mayúscula = NT,
        // minúscula = terminal), "A>Ab" es A -> A b (recursiva por la izquierda)
        // y el lenguaje es {bd, bdb, bdbb, ...}. No hay ninguna 'a' terminal.
        check("carga .gtk simple: bd valida",
                ValidadorGramatica.validar(simple, "bd").esValida());
        check("carga .gtk simple: bdb valida",
                ValidadorGramatica.validar(simple, "bdb").esValida());
        check("carga .gtk simple: ac invalida (no hay terminal 'a')",
                !ValidadorGramatica.validar(simple, "ac").esValida());
        check("carga .gtk simple: abcd invalida",
                !ValidadorGramatica.validar(simple, "abcd").esValida());
        check("carga .gtk simple: abc invalida",
                !ValidadorGramatica.validar(simple, "abc").esValida());

        // ---------- 6. Guardar y volver a cargar (round-trip) ----------
        File tmpAfd = new File("/tmp/prueba_guardado.afd");
        ManejadorArchivos.guardarAfd(afd, tmpAfd);
        AFD releido = ManejadorArchivos.cargarAfd(tmpAfd);
        check("round-trip .afd: aababb valida",
                ValidadorAFD.validar(releido, "aababb").esValida());
        File tmpGtk = new File("/tmp/prueba_guardado.gtk");
        ManejadorArchivos.guardarGramatica(g, tmpGtk);
        Gramatica releidaG = ManejadorArchivos.cargarGramatica(tmpGtk);
        check("round-trip .gtk: 0011 valida",
                ValidadorGramatica.validar(releidaG, "0011").esValida());

        // ---------- 7. Generador de cadenas ----------
        List<String> validas = GeneradorCadenas.validasParaAfd(afd, 3);
        List<String> invalidas = GeneradorCadenas.invalidasParaAfd(afd, 3);
        check("cadenas AFD: 3 validas", validas.size() == 3);
        check("cadenas AFD: 3 invalidas", invalidas.size() == 3);
        List<String> validasG = GeneradorCadenas.validasParaGramatica(g, 3);
        List<String> invalidasG = GeneradorCadenas.invalidasParaGramatica(g, 3);
        check("cadenas G: 3 validas", validasG.size() == 3);
        check("cadenas G: 3 invalidas", invalidasG.size() == 3);

        // ---------- 8. Graphviz ----------
        String dot = GeneradorGrafos.aDot(afd);
        check("DOT contiene doublecircle", dot.contains("doublecircle"));
        check("DOT contiene flecha inicial", dot.contains("inicio ->"));
        File dotFile = new File("/tmp/prueba_grafo.dot");
        GeneradorGrafos.escribirDot(afd, dotFile);
        File pngFile = new File("/tmp/prueba_grafo.png");
        boolean png = GeneradorGrafos.renderizarPng(dotFile, pngFile);
        check("PNG generado con dot", png && pngFile.length() > 0);

        // ---------- 9. PDF ----------
        File pdfAfd = new File("/tmp/prueba_reporte_afd.pdf");
        GeneradorPDF.generarReporteAfd(afd, pdfAfd, validas, invalidas,
                png ? pngFile : null, dot, new java.util.ArrayList<>());
        check("PDF AFD generado", pdfAfd.exists() && pdfAfd.length() > 1000);
        File pdfG = new File("/tmp/prueba_reporte_g.pdf");
        GeneradorPDF.generarReporteGramatica(g, pdfG, validasG, invalidasG,
                png ? pngFile : null, dot, new java.util.ArrayList<>());
        check("PDF gramatica generado", pdfG.exists() && pdfG.length() > 1000);

        System.out.println();
        System.out.println("RESULTADO: " + ok + " OK, " + fallo + " FALLOS");
        if (fallo > 0) System.exit(1);
    }

    private static void check(String nombre, boolean condicion) {
        if (condicion) { ok++; System.out.println("  OK   " + nombre); }
        else { fallo++; System.out.println("  FALLO " + nombre); }
    }
}
