# -*- coding: utf-8 -*-
"""Genera los dos manuales del proyecto AutomatasAFD (Manual de Usuario y
Manual Técnico) en formato Word (.docx).

Autor: Jonatan Abdiel Alvararado Villavicencio — Carné: 0910-22-8487
Curso: Autómatas y Lenguajes Formales — VI Ciclo — Sección 3
Catedrático: Alan Gonzalo Ucelo Moran
"""
import os
from docx import Document
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH, WD_BREAK
from docx.oxml import OxmlElement
from docx.oxml.ns import qn

# Carpeta raíz del proyecto: la carpeta padre de docs/ (donde vive este script).
BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
IMG = os.path.join(BASE, "docs", "img")
AUTOR = "Jonatan Abdiel Alvararado Villavicencio"
CARNE = "0910-22-8487"
CURSO = "Autómatas y Lenguajes Formales — VI Ciclo — Sección 3"
CATEDRATICO = "Alan Gonzalo Ucelo Moran"
UNIVERSIDAD = "Universidad Mariano Gálvez de Guatemala"
FACULTAD = "Facultad de Ingeniería en Sistemas de Información y Ciencias de la Computación"

# Paleta sobria para el diseño de los manuales.
AZUL_TITULO = RGBColor(0x1F, 0x38, 0x64)   # azul marino (títulos y portada)
AZUL_SECCION = RGBColor(0x2F, 0x54, 0x96)   # azul medio (encabezados H2/H3)
GRIS_TEXTO = RGBColor(0x59, 0x59, 0x59)     # gris (pies, captions, encabezado)
GRIS_FONDO_CODIGO = "F2F2F2"               # fondo gris claro de los bloques de código
AZUL_FONDO_TABLA = "1F3864"                # fondo del encabezado de las tablas
GRIS_FONDO_FILA = "D9E2F3"                 # fondo de las filas impares de las tablas


def nuevo_documento(titulo_corto):
    doc = Document()
    for section in doc.sections:
        section.top_margin = Inches(1)
        section.bottom_margin = Inches(1)
        section.left_margin = Inches(1)
        section.right_margin = Inches(1)
        # La portada (primera página) no lleva encabezado ni pie.
        section.different_first_page_header_footer = True
    normal = doc.styles["Normal"]
    normal.font.name = "Calibri"
    normal.font.size = Pt(11)
    normal.paragraph_format.space_after = Pt(6)
    normal.paragraph_format.line_spacing = 1.15
    h1 = doc.styles["Heading 1"]
    h1.font.name = "Calibri Light"
    h1.font.size = Pt(16)
    h1.font.bold = True
    h1.font.color.rgb = AZUL_TITULO
    h1.paragraph_format.space_before = Pt(18)
    h1.paragraph_format.space_after = Pt(6)
    h1.paragraph_format.keep_with_next = True
    h2 = doc.styles["Heading 2"]
    h2.font.name = "Calibri Light"
    h2.font.size = Pt(13)
    h2.font.bold = True
    h2.font.color.rgb = AZUL_SECCION
    h2.paragraph_format.space_before = Pt(14)
    h2.paragraph_format.space_after = Pt(4)
    h2.paragraph_format.keep_with_next = True
    h3 = doc.styles["Heading 3"]
    h3.font.name = "Calibri"
    h3.font.size = Pt(12)
    h3.font.bold = True
    h3.font.color.rgb = AZUL_SECCION
    h3.paragraph_format.space_before = Pt(10)
    h3.paragraph_format.space_after = Pt(4)
    h3.paragraph_format.keep_with_next = True
    _encabezado_pie(doc, titulo_corto)
    return doc


def _sombrear_celda(celda, color_hex):
    """Fondo de color para una celda de tabla."""
    tcPr = celda._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), color_hex)
    tcPr.append(shd)


def _sombrear_parrafo(parrafo, color_hex):
    """Fondo de color para un párrafo completo (bloques de código)."""
    pPr = parrafo._p.get_or_add_pPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:val"), "clear")
    shd.set(qn("w:color"), "auto")
    shd.set(qn("w:fill"), color_hex)
    pPr.append(shd)


def _regla(parrafo, color_hex="1F3864", tamano=12):
    """Línea horizontal bajo el párrafo (borde inferior)."""
    pPr = parrafo._p.get_or_add_pPr()
    pBdr = OxmlElement("w:pBdr")
    abajo = OxmlElement("w:bottom")
    abajo.set(qn("w:val"), "single")
    abajo.set(qn("w:sz"), str(tamano))
    abajo.set(qn("w:space"), "4")
    abajo.set(qn("w:color"), color_hex)
    pBdr.append(abajo)
    pPr.append(pBdr)


def _campo_pagina(parrafo):
    """Inserta el campo PAGE (número de página) en el párrafo dado."""
    run = parrafo.add_run()
    run.font.name = "Calibri"
    run.font.size = Pt(9)
    run.font.color.rgb = GRIS_TEXTO
    inicio = OxmlElement("w:fldChar")
    inicio.set(qn("w:fldCharType"), "begin")
    instr = OxmlElement("w:instrText")
    instr.set(qn("xml:space"), "preserve")
    instr.text = "PAGE"
    fin = OxmlElement("w:fldChar")
    fin.set(qn("w:fldCharType"), "end")
    run._r.append(inicio)
    run._r.append(instr)
    run._r.append(fin)


def _encabezado_pie(doc, titulo_corto):
    """Encabezado con el título del manual y pie con el proyecto y la página."""
    for section in doc.sections:
        encabezado = section.header.paragraphs[0]
        encabezado.alignment = WD_ALIGN_PARAGRAPH.RIGHT
        run = encabezado.add_run("AutomatasAFD — " + titulo_corto)
        run.font.name = "Calibri"
        run.font.size = Pt(9)
        run.font.color.rgb = GRIS_TEXTO
        pie = section.footer.paragraphs[0]
        pie.alignment = WD_ALIGN_PARAGRAPH.CENTER
        run = pie.add_run("AutomatasAFD  ·  Página ")
        run.font.name = "Calibri"
        run.font.size = Pt(9)
        run.font.color.rgb = GRIS_TEXTO
        _campo_pagina(pie)


def h1(doc, texto):
    doc.add_heading(texto, level=1)


def h2(doc, texto):
    doc.add_heading(texto, level=2)


def h3(doc, texto):
    doc.add_heading(texto, level=3)


def p(doc, texto):
    doc.add_paragraph(texto)


def bullet(doc, texto):
    doc.add_paragraph(texto, style="List Bullet")


def codigo(doc, texto):
    par = doc.add_paragraph()
    par.paragraph_format.space_after = Pt(6)
    par.paragraph_format.left_indent = Inches(0.15)
    _sombrear_parrafo(par, GRIS_FONDO_CODIGO)
    lineas = texto.strip("\n").split("\n")
    for i, linea in enumerate(lineas):
        run = par.add_run(linea if linea else " ")
        run.font.name = "Courier New"
        run.font.size = Pt(9.5)
        if i < len(lineas) - 1:
            run.add_break()


def tabla(doc, encabezados, filas):
    t = doc.add_table(rows=1, cols=len(encabezados))
    t.style = "Table Grid"
    for i, h in enumerate(encabezados):
        celda = t.rows[0].cells[i]
        celda.text = h
        _sombrear_celda(celda, AZUL_FONDO_TABLA)
        for par in celda.paragraphs:
            for run in par.runs:
                run.bold = True
                run.font.color.rgb = RGBColor(0xFF, 0xFF, 0xFF)
    for r, fila in enumerate(filas):
        celdas = t.add_row().cells
        for i, valor in enumerate(fila):
            celdas[i].text = valor
            if r % 2 == 1:
                _sombrear_celda(celdas[i], GRIS_FONDO_FILA)
    doc.add_paragraph()
    return t


def imagen(doc, nombre, pie=None, ancho=6.0):
    ruta = os.path.join(IMG, nombre)
    if os.path.exists(ruta):
        par = doc.add_paragraph()
        par.alignment = WD_ALIGN_PARAGRAPH.CENTER
        par.paragraph_format.space_before = Pt(6)
        par.paragraph_format.space_after = Pt(2)
        par.add_run().add_picture(ruta, width=Inches(ancho))
        if pie:
            par_pie = doc.add_paragraph()
            par_pie.alignment = WD_ALIGN_PARAGRAPH.CENTER
            par_pie.paragraph_format.space_after = Pt(8)
            run = par_pie.add_run(pie)
            run.italic = True
            run.font.size = Pt(9.5)
            run.font.color.rgb = GRIS_TEXTO
    else:
        p(doc, "[Imagen no disponible: " + nombre + "]")


def portada(doc, titulo, subtitulo):
    par = doc.add_paragraph()
    par.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = par.add_run(UNIVERSIDAD)
    run.bold = True
    run.font.size = Pt(13)
    par = doc.add_paragraph()
    par.alignment = WD_ALIGN_PARAGRAPH.CENTER
    par.add_run(FACULTAD).font.size = Pt(11)
    doc.add_paragraph()
    par = doc.add_paragraph()
    par.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = par.add_run(titulo)
    run.bold = True
    run.font.size = Pt(22)
    run.font.color.rgb = AZUL_TITULO
    _regla(par)
    par = doc.add_paragraph()
    par.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = par.add_run(subtitulo)
    run.font.size = Pt(14)
    run.font.color.rgb = AZUL_SECCION
    doc.add_paragraph()
    ultimo = None
    for linea in [
        "Curso: " + CURSO,
        "Catedrático: " + CATEDRATICO,
        "Autor: " + AUTOR,
        "Carné: " + CARNE,
        "Fecha: noviembre de 2026",
    ]:
        par = doc.add_paragraph()
        par.alignment = WD_ALIGN_PARAGRAPH.CENTER
        par.add_run(linea).font.size = Pt(12)
        ultimo = par
    # Salto de página sin agregar otro párrafo.
    ultimo.add_run().add_break(WD_BREAK.PAGE)


# =====================================================================
# MANUAL DE USUARIO
# =====================================================================
def manual_usuario():
    doc = nuevo_documento("Manual de Usuario")
    portada(doc, "Manual de Usuario",
            "Software de Autómatas Finitos Deterministas\ny Gramáticas Regulares (AutomatasAFD)")

    h1(doc, "1. Introducción")
    p(doc, "AutomatasAFD es una aplicación de escritorio en Java (desarrollada en "
           "NetBeans) para el curso de Autómatas y Lenguajes Formales. Con ella se "
           "pueden definir gramáticas regulares, generar Autómatas Finitos "
           "Deterministas (AFD), evaluar cadenas de entrada y generar reportes en "
           "PDF con el grafo del autómata.")
    p(doc, "El programa se puede usar empezando por cualquiera de los dos lados: "
           "crear una gramática y generar su AFD, o crear un AFD y generar su "
           "gramática correspondiente. Las gramáticas y los AFD también se pueden "
           "cargar desde archivos de texto (.gtk y .afd).")
    p(doc, "Este manual explica el funcionamiento de la aplicación a nivel de "
           "usuario: qué hace cada pantalla, qué datos pide y qué resultados "
           "muestra. La lógica interna y la teoría se explican en el Manual Técnico.")

    h1(doc, "2. Requisitos e instalación")
    h2(doc, "2.1 Requisitos")
    bullet(doc, "JDK 17 o superior instalado en el equipo.")
    bullet(doc, "NetBeans 17 o superior (el proyecto es un proyecto Ant estándar).")
    bullet(doc, "Graphviz (opcional): solo se necesita para renderizar el grafo a "
                "imagen PNG. Sin Graphviz el programa funciona igual y genera el "
                "archivo .dot del grafo.")
    h2(doc, "2.2 Abrir el proyecto en NetBeans")
    p(doc, "1. Abrir NetBeans.")
    p(doc, "2. File → Open Project (Abrir proyecto).")
    p(doc, "3. Seleccionar la carpeta del proyecto (AutomatasAFD) y presionar "
           "Open Project.")
    p(doc, "4. El proyecto aparece en la ventana Projects con sus paquetes "
           "com.automatas.app, com.automatas.modelo, com.automatas.logica y "
           "com.automatas.vista.")
    h2(doc, "2.3 Ejecutar el programa")
    p(doc, "Con el proyecto abierto, presionar F6 (Run Project) o usar el menú "
           "Run → Run Project. La clase principal es com.automatas.app.Main. "
           "También se puede ejecutar desde consola (ver el Manual Técnico).")
    p(doc, "Al ejecutar aparece primero la portada. Para entrar al menú "
           "principal se presiona la tecla ENTER o el botón “Continuar al menú”.")

    h1(doc, "3. Recorrido general")
    p(doc, "La aplicación tiene las siguientes pantallas, en el orden en que se usan:")
    tabla(doc, ["Pantalla", "Para qué sirve"], [
        ["Portada", "Datos del curso, la sección y el carné. ENTER lleva al menú."],
        ["Menú Principal", "Las 8 opciones: Crear AFD, Crear Gramática, Evaluar Cadenas, Reportes, Cargar archivo, Guardar, Ayuda y Salir."],
        ["Crear AFD", "Definir un AFD paso a paso (estados, alfabeto, inicial, aceptación, transiciones)."],
        ["Crear Gramática", "Definir una gramática regular (NT, terminales, inicial, producciones)."],
        ["Evaluar Cadenas", "Validar una cadena en un AFD o en una gramática y ver su ruta o expansión."],
        ["Reportes", "Ver el detalle y generar el reporte PDF y el grafo Graphviz."],
        ["Cargar / Guardar", "Cargar archivos .afd y .gtk, y guardar el AFD o la gramática."],
        ["Ayuda", "Datos del curso, nombre del catedrático y último dígito del carné."],
    ])
    h2(doc, "3.1 Portada")
    imagen(doc, "portada.png", "Figura 1. Portada de la aplicación con los datos del curso.")
    p(doc, "La portada muestra la universidad, la facultad, el curso, el ciclo y "
           "la sección, el nombre del proyecto, el autor, el carné y el "
           "catedrático. Al presionar ENTER (o el botón) se pasa al menú principal.")
    h2(doc, "3.2 Menú principal")
    imagen(doc, "menu.png", "Figura 2. Menú principal con las ocho opciones.")
    p(doc, "Cada botón limpia la pantalla y muestra las opciones de esa tarea. "
           "El botón “Salir / Cerrar programa” pide confirmación antes de cerrar.")

    h1(doc, "4. Crear AFD")
    imagen(doc, "crear_afd.png", "Figura 3. Panel Crear AFD con sus seis secciones.")
    p(doc, "Pasos para crear un AFD:")
    p(doc, "1. Escribir el nombre del AFD (es el nombre con el que se usará dentro "
           "de la aplicación) y presionar “Nuevo AFD”. Si ya existe un AFD con ese "
           "nombre, se reemplaza y se avisa.")
    p(doc, "2. Ingresar Estados: escribir un estado y presionar “Agregar estado”. "
           "Se ingresan de uno en uno. No se pueden repetir estados y un estado no "
           "puede ser igual a un símbolo del alfabeto.")
    p(doc, "3. Ingresar Alfabeto: escribir un símbolo y presionar “Agregar símbolo”. "
           "Tampoco se pueden repetir símbolos ni usar un símbolo igual a un estado.")
    p(doc, "4. Estado Inicial: escribir el estado inicial y presionar “Definir "
           "inicial”. Debe existir en los estados declarados; si no, se muestra el "
           "error. Si ya había un estado inicial y se escribe otro, se toma el "
           "último escrito.")
    p(doc, "5. Estado(s) de aceptación: escribir un estado y presionar “Agregar "
           "aceptación”. Debe existir en los estados declarados. Se pueden agregar "
           "varios.")
    p(doc, "6. Transiciones: elegir el modo 1 o el modo 2 (ver 4.1 y 4.2).")
    p(doc, "El botón “Generar gramática de este AFD” crea la gramática regular "
           "equivalente. El botón “Ver detalle” muestra todos los elementos "
           "formales del AFD.")
    h2(doc, "4.1 Transiciones — Modo 1")
    p(doc, "Se ingresa una transición a la vez con la notación:")
    codigo(doc, "A,B,0;false,true")
    p(doc, "Esto indica que se va del estado A al estado B con el símbolo 0. "
           "Después del punto y coma van dos banderas: si A es de aceptación "
           "(false) y si B es de aceptación (true), en ese orden. Si en una "
           "línea posterior cambia la bandera de un estado, se toma la última "
           "escrita.")
    p(doc, "El modo 1 rechaza con mensaje de error:")
    bullet(doc, "Sacar dos símbolos iguales desde el mismo estado (por ejemplo, "
                "ya existe A,?,0 y se intenta agregar A,?,0 de nuevo). Eso solo es "
                "posible con un AFN (no determinista).")
    bullet(doc, "Una transición con epsilon: un AFD no tiene transiciones vacías.")
    h2(doc, "4.2 Transiciones — Modo 2 (tabla)")
    p(doc, "Se ingresa la tabla de transiciones completa en una línea:")
    codigo(doc, "[a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]")
    p(doc, "El primer corchete trae los terminales (columnas: a, b). El segundo "
           "trae los estados (filas: A, B, C, D) y los estados de aceptación "
           "después del punto y coma (B, D). El tercero trae la matriz de "
           "destinos: las columnas se separan por comas y las filas por punto y "
           "coma. El símbolo “-” indica que el estado no tiene transición con "
           "ese símbolo (en el ejemplo, el estado D no tiene transiciones).")
    p(doc, "La tabla del ejemplo corresponde a:")
    tabla(doc, ["Estado", "a", "b"], [
        ["A", "A", "C"],
        ["B", "A", "C"],
        ["C", "B", "D"],
        ["D", "-", "-"],
    ])
    p(doc, "Al aplicar la tabla, los estados y símbolos que falten se agregan "
           "automáticamente. Si el AFD aún no tiene estado inicial, el mensaje "
           "recuerda definirlo.")

    h1(doc, "5. Crear Gramática")
    imagen(doc, "crear_gramatica.png", "Figura 4. Panel Crear Gramática.")
    p(doc, "Pasos para crear una gramática regular:")
    p(doc, "1. Escribir el nombre de la gramática y presionar “Nueva gramática”.")
    p(doc, "2. Ingresar NT: escribir un No Terminal y presionar “Agregar NT”. Se "
           "ingresan de uno en uno, sin repetir y sin usar un NT igual a un "
           "terminal.")
    p(doc, "3. Ingresar Terminales: escribir un terminal y presionar “Agregar "
           "terminal”. Sin repetir y sin usar un terminal igual a un NT. La "
           "palabra “epsilon” está reservada para la producción vacía.")
    p(doc, "4. NT Inicial: escribir el No Terminal inicial y presionar “Definir "
           "inicial”. Debe existir en los NT declarados; si ya había uno y se "
           "escribe otro, se toma el último.")
    p(doc, "5. Producciones: escribir una producción a la vez con la notación "
           "basada en BNF, pero con “>” en lugar de “::=”:")
    codigo(doc, "A > a B")
    p(doc, "El lado derecho lleva los símbolos separados por espacio. El vacío se "
           "escribe con la palabra “epsilon” (por ejemplo: A > epsilon). La "
           "disyunción se puede escribir de dos formas, que son equivalentes:")
    bullet(doc, "Con el símbolo “|”: A > a B | b (se crean dos producciones: "
                "A > a B y A > b).")
    bullet(doc, "Con dos producciones con el mismo No Terminal a la izquierda: "
                "A > a B y A > b.")
    p(doc, "No se aceptan producciones repetidas. El botón “Generar AFD de esta "
           "gramática” crea el AFD equivalente (solo si la gramática es regular "
           "y determinista; ver la sección 9).")

    h1(doc, "6. Evaluar Cadenas")
    imagen(doc, "evaluar.png", "Figura 5. Panel Evaluar Cadenas.")
    p(doc, "1. Elegir el tipo (AFD o Gramática) y el nombre de la gramática o AFD "
           "a usar (deben estar creados o cargados antes).")
    p(doc, "2. Escribir la cadena y presionar “Solo validar”.")
    p(doc, "3. El programa muestra el recorrido y el resultado: “cadena válida” "
           "o “cadena inválida” con el motivo.")
    h2(doc, "6.1 Evaluación en AFD: la ruta")
    p(doc, "Se muestra la ruta con el formato del enunciado: el estado del que "
           "se parte, el estado al que se llega y el símbolo, separados por coma; "
           "y un punto y coma entre cada movimiento. Ejemplo con el AFD de la "
           "sección 4.2, validando “aababb” (D es de aceptación):")
    codigo(doc, "Ruta en AFD: A, A, a; A, A, a; A, C, b; C, B, a; B, C, b; C, D, b\n"
           "Resultado: cadena válida")
    p(doc, "Si la cadena termina en un estado que no es de aceptación, o si falta "
           "una transición, o si un carácter no pertenece al alfabeto, la cadena "
           "es inválida y el mensaje dice el motivo.")
    h2(doc, "6.2 Evaluación en gramática: la expansión")
    p(doc, "Se muestra la expansión (derivación por la izquierda), con cada forma "
           "sentencial separada por “>”. Cuando se aplica una producción vacía, "
           "el paso se marca con (epsilon). Ejemplo de la gramática A>0B | 1A | "
           "epsilon, B>0B | 1A, validando “0011”:")
    codigo(doc, "Expansión Gramática: A>0B>00B>001A>0011A>0011(epsilon)>0011\n"
           "Resultado: cadena válida")
    p(doc, "Cada cadena evaluada queda registrada en el historial de la sesión y "
           "aparece en el reporte PDF.")

    h1(doc, "7. Cargar y guardar archivos")
    imagen(doc, "archivos.png", "Figura 6. Panel Cargar y guardar archivos.")
    h2(doc, "7.1 Cargar archivo de entrada")
    p(doc, "Con el File Chooser se selecciona el archivo. El nombre del archivo "
           "(sin la extensión) es el nombre del AFD o de la gramática dentro de "
           "la aplicación. Los archivos vienen sin errores y con el patrón "
           "definido, así que no hace falta corregirlos.")
    p(doc, "Archivo .afd (AFD). Cada línea tiene la forma:")
    codigo(doc, "A,B,0;false,true")
    p(doc, "Estado de partida, estado de llegada, símbolo, y las banderas de "
           "aceptación del estado de partida y del de llegada, en ese orden. "
           "Cada línea se separa con ENTER. El estado inicial es el estado que "
           "esté al inicio del archivo. Si la bandera de aceptación de un estado "
           "cambia en líneas posteriores, se toma la última escrita.")
    p(doc, "Archivo .gtk (gramática). Cada línea tiene la forma:")
    codigo(doc, "A>Ab\nA>bC\nB>c\nC>d")
    p(doc, "Con mayúsculas se indican los no terminales y con minúsculas (y "
           "dígitos) los símbolos del alfabeto. El símbolo “>” denota la "
           "derivación. El no terminal inicial es el que viene al inicio de la "
           "gramática. Para usar “|” se crea una producción con el mismo no "
           "terminal pero con transición distinta.")
    h2(doc, "7.2 Guardar (.afd / .gtk)")
    p(doc, "Se elige el tipo (AFD o Gramática) y el nombre, y luego “Guardar "
           "como...” para elegir la carpeta y el nombre del archivo. El programa "
           "escribe el archivo con el formato descrito arriba. Nota: en el "
           "archivo .afd, la primera línea parte del estado inicial, porque el "
           "archivo toma como inicial el estado que esté al inicio.")

    h1(doc, "8. Reportes")
    imagen(doc, "reportes.png", "Figura 7. Panel Reportes.")
    p(doc, "1. Elegir el tipo (AFD o Gramática) y el nombre.")
    p(doc, "2. “Ver Detalle” muestra en pantalla todos los elementos formales:")
    bullet(doc, "AFD: alfabeto, estados, estado inicial, estados de aceptación y "
                "transiciones con la notación del enunciado.")
    bullet(doc, "Gramática: No Terminales, terminales, símbolo inicial y "
                "producciones.")
    p(doc, "3. “Generar grafo Graphviz” guarda el archivo .dot del AFD y, si "
           "Graphviz está instalado, la imagen PNG del grafo.")
    p(doc, "4. “Generar Reporte PDF” crea el archivo PDF con:")
    bullet(doc, "Toda la información de Ver Detalle.")
    bullet(doc, "La representación gráfica del AFD (imagen PNG si Graphviz está "
                "instalado; si no, el código DOT y las instrucciones).")
    bullet(doc, "Al menos 3 cadenas válidas y 3 cadenas inválidas generadas por "
                "el programa.")
    bullet(doc, "El historial de cadenas evaluadas en la sesión.")
    imagen(doc, "reporte_pdf.png", "Figura 8. Ejemplo de reporte PDF generado.")
    p(doc, "El grafo usa doble círculo para los estados de aceptación y una "
           "flecha hacia el estado inicial:")
    imagen(doc, "grafo_ejemplo.png", "Figura 9. Grafo del AFD del enunciado: doble círculo en B y D.", ancho=4.5)

    h1(doc, "9. Conversión entre gramática y AFD")
    p(doc, "Desde “Crear Gramática”, el botón “Generar AFD de esta gramática” "
           "crea el AFD equivalente (se guarda con el nombre de la gramática más "
           "“_AFD”). Desde “Crear AFD”, el botón “Generar gramática de este AFD” "
           "crea la gramática equivalente (nombre del AFD más “_G”).")
    p(doc, "La conversión de gramática a AFD exige que la gramática sea regular "
           "(producciones A → aB, A → a o A → epsilon) y determinista (que no "
           "haya dos producciones desde el mismo no terminal con el mismo "
           "terminal hacia destinos distintos). Si no se cumple, el programa lo "
           "dice con un mensaje claro y no genera un AFD incorrecto.")

    h1(doc, "10. Ayuda")
    p(doc, "La ventana de Ayuda (disponible en el menú principal y en cada "
           "submenú) muestra los datos del curso, el nombre del catedrático y "
           "el último dígito del carné (7).")

    h1(doc, "11. Solución de problemas")
    tabla(doc, ["Mensaje o situación", "Qué significa y qué hacer"], [
        ["“El estado ya existe.”", "El estado ya está declarado. Use otro nombre."],
        ["“El símbolo ya existe en el alfabeto.”", "El símbolo ya está declarado. Use otro."],
        ["“El estado inicial no existe...”", "Primero agregue el estado en “Ingresar Estados”."],
        ["“...solo es posible con un AFN.”", "Ya existe una transición desde ese estado con ese símbolo. Un AFD no puede tener dos."],
        ["“Un AFD no puede tener transiciones con epsilon.”", "El símbolo epsilon no se usa en transiciones de AFD."],
        ["“La producción ya existe.”", "Esa producción ya fue agregada."],
        ["“El símbolo ... no está declarado.”", "Agregue el símbolo como terminal o No Terminal antes de usarlo en una producción."],
        ["“No hay ningún AFD creado todavía.”", "Cree un AFD (opción 1) o cargue un .afd (opción 5) antes de evaluar o reportar."],
        ["El grafo sale como texto en el PDF", "Graphviz no está instalado. Instálelo (sitio oficial de Graphviz) y vuelva a generar el reporte."],
        ["El PDF no incluye imagen del grafo (gramática)", "La gramática no se pudo convertir a AFD (no es regular o no es determinista). Revise las producciones."],
    ])

    h1(doc, "12. Ejemplos incluidos")
    tabla(doc, ["Archivo", "Contenido"], [
        ["ejemplos/ejemplo_afd.afd", "AFD del enunciado (A, B, C, D; aceptación B, D). “aababb” es válida."],
        ["ejemplos/ejemplo_gramatica.gtk", "Gramática del enunciado (A>0B | 1A | epsilon, B>0B | 1A). “0011” es válida."],
        ["ejemplos/ejemplo_simple.gtk", "Ejemplo de carga masiva del enunciado (A>Ab, A>bC, B>c, C>d)."],
    ])
    p(doc, "Estos archivos están en la carpeta “ejemplos” del proyecto y se pueden "
           "cargar tal cual desde la opción 5 del menú.")

    doc.save(os.path.join(BASE, "docs", "Manual_de_Usuario.docx"))
    print("Manual_de_Usuario.docx generado")


# =====================================================================
# MANUAL TÉCNICO
# =====================================================================
def manual_tecnico():
    doc = nuevo_documento("Manual Técnico")
    portada(doc, "Manual Técnico",
            "Software de Autómatas Finitos Deterministas\ny Gramáticas Regulares (AutomatasAFD)")

    h1(doc, "1. Introducción")
    p(doc, "Este manual explica la lógica de programación de AutomatasAFD, "
           "aplicando los temas del curso: Autómatas Finitos, Lenguajes "
           "Formales y Gramáticas. Está pensado para la revisión de código: "
           "cada decisión de diseño se explica con su fundamento teórico, y "
           "cada clase y algoritmo se describe con su responsabilidad y su "
           "complejidad.")
    p(doc, "El programa está escrito en Java (JDK 17) como proyecto Ant de "
           "NetBeans, con interfaz gráfica Swing. No usa frameworks: solo la "
           "biblioteca estándar de Java, sin dependencias externas: los reportes "
           "PDF se generan con un escritor propio incluido en el código. "
           "Graphviz se usa como programa "
           "externo (proceso dot) para renderizar los grafos.")

    h1(doc, "2. Fundamentos teóricos")
    h2(doc, "2.1 Autómata Finito Determinista (AFD)")
    p(doc, "Un AFD es una quíntupla M = (Q, Σ, δ, q0, F), donde Q es el "
           "conjunto finito de estados, Σ es el alfabeto, δ: Q × Σ → Q es la "
           "función de transición, q0 ∈ Q es el estado inicial y F ⊆ Q es el "
           "conjunto de estados de aceptación.")
    p(doc, "Una cadena w pertenece al lenguaje L(M) si, al simular δ desde q0 "
           "símbolo a símbolo, el estado final pertenece a F. La función δ es "
           "total en la definición matemática; en la práctica de este proyecto "
           "es parcial: el símbolo “-” del modo 2 indica que no hay transición, "
           "y una cadena que necesite una transición inexistente se considera "
           "inválida (se informa en qué estado y con qué símbolo se detuvo).")
    p(doc, "Determinismo: para cada par (estado, símbolo) existe a lo sumo un "
           "destino. Si se intentan dos transiciones con el mismo símbolo "
           "desde el mismo estado, el resultado sería un AFN (Autómata Finito "
           "No determinista), que este proyecto rechaza con mensaje de error, "
           "como pide el enunciado.")
    h2(doc, "2.2 Gramáticas regulares y lenguajes formales")
    p(doc, "Una gramática es una cuádrupla G = (N, Σ, P, S), donde N son los "
           "no terminales, Σ los terminales, P las producciones y S ∈ N el "
           "símbolo inicial. Una gramática es regular (lineal por la derecha) "
           "si todas sus producciones tienen la forma A → aB, A → a o "
           "A → ε, con A, B ∈ N y a ∈ Σ.")
    p(doc, "Una derivación es la aplicación sucesiva de producciones al símbolo "
           "inicial hasta obtener una cadena de solo terminales. La cadena "
           "pertenece al lenguaje L(G) si existe una derivación que la genera. "
           "La derivación por la izquierda (siempre se expande el no terminal "
           "más a la izquierda) es la que usa el validador de este proyecto, "
           "porque con ella cada forma sentencial tiene un prefijo terminal "
           "que permite podar la búsqueda.")
    p(doc, "El símbolo ε (aquí escrito “epsilon”) representa la cadena vacía: "
           "una producción A → ε permite que A desaparezca de la forma "
           "sentencial.")
    h2(doc, "2.3 Equivalencia entre AFD y gramáticas regulares")
    p(doc, "Todo lenguaje regular puede describirse tanto con un AFD como con "
           "una gramática regular, y ambas descripciones son convertibles entre "
           "sí (teorema de Kleene, en su forma para gramáticas regulares). "
           "Este proyecto implementa las dos construcciones clásicas:")
    bullet(doc, "Gramática → AFD: los estados son los no terminales más un estado "
                "final auxiliar QF; A → aB genera δ(A, a) = B; A → a genera "
                "δ(A, a) = QF; A → ε hace a A estado de aceptación (QF siempre "
                "lo es).")
    bullet(doc, "AFD → Gramática: los no terminales son los estados; por cada "
                "transición δ(A, a) = B se crea A → aB; si B es de aceptación "
                "también A → a; y si q0 es de aceptación, S → ε.")
    p(doc, "La conversión gramática → AFD exige que la gramática sea "
           "determinista (sin dos producciones A → aB y A → aC con B ≠ C). "
           "Una gramática regular no determinista generaría un AFN, y su "
           "determinización (construcción de subconjuntos) queda fuera del "
           "alcance de este proyecto; en ese caso el programa lo informa con "
           "un mensaje claro en lugar de generar un AFD incorrecto.")

    h1(doc, "3. Arquitectura")
    p(doc, "El proyecto sigue una separación en tres capas, sin frameworks, "
           "para que cada clase tenga una sola responsabilidad y sea fácil de "
           "explicar en la revisión de código:")
    tabla(doc, ["Capa (paquete)", "Responsabilidad", "Clases"], [
        ["modelo (com.automatas.modelo)", "Datos y reglas del dominio. Guarda los elementos formales y hace cumplir las validaciones del enunciado.", "AFD, Gramatica, Transicion, Produccion, Constantes, DatosDelCurso"],
        ["lógica (com.automatas.logica)", "Algoritmos: validación, conversión, archivos, grafos, PDF, generación de cadenas y repositorio central.", "ValidadorAFD, ValidadorGramatica, Convertidor, GeneradorCadenas, GeneradorGrafos, GeneradorPDF, ManejadorArchivos, ParserTransiciones, Repositorio, ResultadoEvaluacion"],
        ["vista (com.automatas.vista)", "Interfaz gráfica Swing: portada, menú y paneles. Recoge datos, llama a la lógica y muestra mensajes.", "VentanaPrincipal, PanelPortada, PanelMenu, PanelCrearAFD, PanelCrearGramatica, PanelEvaluar, PanelReportes, PanelArchivos, DialogoAyuda, Refrescable"],
        ["app (com.automatas.app)", "Punto de entrada.", "Main"],
    ])
    p(doc, "Flujo de datos: la vista nunca guarda estado propio entre pantallas; "
           "todas leen y escriben en el Repositorio central (un almacén en "
           "memoria con los AFD, las gramáticas y el historial de evaluaciones). "
           "El modelo no conoce a la vista ni a la lógica de archivos: las "
           "validaciones de dominio viven en el modelo y los algoritmos en la "
           "capa lógica, que solo depende del modelo.")
    h2(doc, "3.1 Diagrama de clases (resumen)")
    tabla(doc, ["Clase", "Atributos principales", "Métodos clave"], [
        ["AFD", "nombre, estados (Q), alfabeto (Σ), estadoInicial (q0), estadosAceptacion (F), transiciones (δ)", "agregarEstado, agregarSimbolo, setEstadoInicial, agregarEstadoAceptacion, agregarTransicion, buscarTransicion, serializarAfd, detalle"],
        ["Gramatica", "nombre, noTerminales (N), terminales (Σ), inicial (S), producciones (P)", "agregarNoTerminal, agregarTerminal, setInicial, agregarProduccion, agregarProduccionTexto, produccionesDe, serializarGtk, detalle"],
        ["Transicion", "origen, destino, simbolo", "equals/hashCode por (origen, símbolo): define la unicidad de δ"],
        ["Produccion", "izquierdo, derecho (lista de símbolos)", "esEpsilon, aFormatoGtk, desdeTexto"],
        ["ValidadorAFD", "— (utilería estática)", "validar(AFD, cadena): simulación de δ en O(n)"],
        ["ValidadorGramatica", "— (utilería estática)", "validar(Gramatica, cadena): BFS de derivación por la izquierda"],
        ["Convertidor", "— (utilería estática)", "gramaticaAAfd, afdAGramatica"],
        ["GeneradorPDF", "— (utilería estática)", "generarReporteAfd, generarReporteGramatica"],
        ["Repositorio", "afds, gramaticas, historial", "guardarAfd, guardarGramatica, registrarEvaluacion, historialDe"],
    ])

    h1(doc, "4. Paradigmas de programación orientada a objetos")
    p(doc, "El enunciado exige aplicar los paradigmas de la programación "
           "orientada a objetos y poder explicarlos en la revisión de código. "
           "Esta es la correspondencia concreta en el proyecto:")
    tabla(doc, ["Paradigma", "Dónde se aplica", "Ejemplo concreto"], [
        ["Encapsulamiento", "Modelo (AFD, Gramatica)", "Los conjuntos Q, Σ, F son privados; solo se modifican con métodos que validan (agregarEstado rechaza duplicados y estados iguales a símbolos)."],
        ["Abstracción", "Interfaces y clases de utilería", "Refrescable define alMostrar() para todos los paneles; las clases de lógica (ValidadorAFD, Convertidor) exponen solo métodos estáticos con una responsabilidad."],
        ["Herencia", "Vista Swing", "Todos los paneles heredan de JPanel y la ventana de JFrame; DialogoAyuda hereda de JDialog. Se reutiliza todo el comportamiento de Swing."],
        ["Polimorfismo", "Vista y lógica", "VentanaPrincipal trata a todos los paneles como JPanel y, si implementan Refrescable, llama a alMostrar() sin saber de qué panel se trata. Los validadores devuelven el mismo tipo ResultadoEvaluacion para AFD y gramática."],
        ["Composición", "Modelo", "Un AFD está compuesto por Transiciones; una Gramática por Producciones; una Produccion por su lista de símbolos. Ninguna clase hereda datos que no le corresponden."],
    ])
    p(doc, "Además se usan: colecciones con orden de inserción (LinkedHashSet y "
           "LinkedHashMap) para que los reportes muestren los elementos en el "
           "orden en que el usuario los creó; excepciones IllegalArgumentException "
           "con mensajes claros como mecanismo de validación; y clases de "
           "utilería con constructor privado (ValidadorAFD, Convertidor, "
           "GeneradorGrafos) para agrupar funciones sin estado.")

    h1(doc, "5. Algoritmos")
    h2(doc, "5.1 Validación en AFD (ValidadorAFD.validar)")
    p(doc, "Simulación directa de δ: se parte de q0 y se consume la cadena "
           "símbolo a símbolo, siguiendo la única transición posible en cada "
           "paso. Complejidad O(n), con n = número de símbolos. La cadena se "
           "divide en símbolos buscando en cada posición el símbolo más largo "
           "del alfabeto que sea prefijo del resto (con alfabetos de un solo "
           "carácter equivale a leer carácter por carácter). Si falta una "
           "transición o un carácter no pertenece al alfabeto, la cadena es "
           "inválida y se informa el motivo. La ruta se construye como "
           "“origen, destino, símbolo” por paso, unidos con “; ”.")
    h2(doc, "5.2 Validación en gramática (ValidadorGramatica.validar)")
    p(doc, "Búsqueda en anchura (BFS) sobre derivaciones por la izquierda. Cada "
           "nodo guarda la forma sentencial y su cadena de expansión. Se parte "
           "de [S] y se expande el primer no terminal con cada una de sus "
           "producciones. La búsqueda termina cuando se alcanza una forma de "
           "solo terminales igual a la cadena. Como BFS explora por niveles, la "
           "primera derivación encontrada es de las más cortas.")
    p(doc, "Podas que garantizan terminación: (1) el prefijo terminal de la "
           "forma debe coincidir con el prefijo de la cadena; (2) la forma no "
           "puede tener más terminales que la cadena; (3) no se reexpande una "
           "forma ya visitada; (4) límite de 2000 pasos y de tamaño de forma. "
           "El paso con epsilon se representa con el marcador (epsilon): por "
           "ejemplo, de “0011A” se pasa a “0011(epsilon)” y luego a “0011”, "
           "reproduciendo el formato del enunciado.")
    h2(doc, "5.3 Conversiones (Convertidor)")
    p(doc, "Gramática → AFD: construcción directa descrita en 2.3, con control "
           "de determinismo (si dos producciones desde el mismo no terminal con "
           "el mismo terminal llevan a destinos distintos, se lanza "
           "IllegalArgumentException explicando que se generaría un AFN). El "
           "estado final auxiliar se llama QF (o QF1, QF2... si QF ya existe "
           "como no terminal). AFD → Gramática: construcción clásica con "
           "producciones A → aB, A → a (si B es de aceptación) y S → ε (si q0 "
           "es de aceptación). Ambas preservan el lenguaje.")
    h2(doc, "5.4 Generación de cadenas de ejemplo (GeneradorCadenas)")
    p(doc, "Enumeración por longitud creciente (BFS sobre Σ*): se generan las "
           "cadenas de longitud 0, 1, 2, ... hasta longitud 6, se validan con el "
           "validador correspondiente y se toman las primeras 3 válidas y las "
           "primeras 3 inválidas. La enumeración está acotada a 5000 candidatos "
           "para no dispararse con alfabetos grandes.")
    h2(doc, "5.5 Grafos Graphviz (GeneradorGrafos)")
    p(doc, "Se genera el código DOT del AFD (rankdir=LR, doble círculo para "
           "aceptación, flecha al estado inicial, aristas etiquetadas) y se "
           "escribe el archivo .dot. Si el programa “dot” está instalado, se "
           "ejecuta “dot -Tpng” con ProcessBuilder y se obtiene el PNG. Si no "
           "está instalado, el método devuelve false y el reporte incluye el "
           "código DOT como texto con las instrucciones de instalación.")
    h2(doc, "5.6 Reporte PDF (GeneradorPDF, escritor propio sin librerías)")
    p(doc, "El PDF se escribe directamente, sin librerías externas: objetos, "
           "tabla de referencias cruzadas, fuentes estándar Type1, compresión "
           "Flate y la imagen del grafo incrustada en JPEG. Escritor "
           "incremental con cursor vertical y salto de página "
           "automático: título, datos del autor, detalle en fuente "
           "monoespaciada, grafo (PNG escalado al ancho útil o código DOT), "
           "cadenas válidas e inválidas e historial. Las fuentes estándar del "
           "PDF (WinAnsi) no tienen los símbolos griegos Σ, δ ni ε ni la flecha "
           "→, por lo que se sanitizan a “Sigma”, “delta”, “epsilon” y “->” "
           "antes de escribir (las vocales acentuadas y la ñ sí se conservan).")

    h1(doc, "6. Formatos de archivo (.afd y .gtk)")
    p(doc, "La carga y el guardado viven en ManejadorArchivos. Los archivos "
           "vienen sin errores según el enunciado, así que el cargador valida "
           "solo el formato de línea y reporta errores claros si no se cumple.")
    h2(doc, "6.1 Archivo .afd")
    codigo(doc, "A,B,0;false,true\n"
           "A,C,b;false,false\n"
           "B,A,a;true,false")
    p(doc, "Cada línea: ORIGEN,DESTINO,SIMBOLO;aceptacionOrigen,aceptacionDestino. "
           "El estado inicial es el de la primera línea; por eso al guardar se "
           "ordenan las líneas para que la primera parta del estado inicial. La "
           "última bandera de aceptación escrita para un estado es la que vale.")
    h2(doc, "6.2 Archivo .gtk")
    codigo(doc, "A>0B\nA>1A\nA>epsilon\nB>0B\nB>1A")
    p(doc, "Cada línea: NT>lado derecho compacto (sin espacios: cada carácter "
           "es un símbolo; mayúscula = no terminal, minúscula o dígito = "
           "terminal). El no terminal inicial es el de la primera línea; por "
           "eso al guardar se ordenan las producciones para que la primera sea "
           "del inicial. En el menú, en cambio, el lado derecho se escribe con "
           "espacios (“A > 0 B”), lo que permite símbolos de más de un carácter; "
           "si se escribe sin espacios (“A>0B”), cada carácter es un símbolo.")

    h1(doc, "7. Decisiones de diseño y limitaciones (honestas)")
    bullet(doc, "δ parcial: el enunciado usa “-” para estados sin transición. Una "
                "cadena que necesite una transición inexistente es inválida y se "
                "informa el punto exacto donde se detuvo.")
    bullet(doc, "Conversión gramática → AFD solo para gramáticas deterministas. "
                "Una gramática regular no determinista se rechaza con mensaje "
                "claro (haría falta la construcción de subconjuntos).")
    bullet(doc, "El ejemplo .gtk del enunciado (A>Ab, A>bC, B>c, C>d) genera el "
                "lenguaje {bd, bdb, bdbb, ...}: con la regla mayúscula = NT, "
                "“A>Ab” es A → A b (recursiva por la izquierda) y no hay ningún "
                "terminal “a”. El cargador sigue la regla del enunciado al pie "
                "de la letra; la cadena “ac” no pertenece a ese lenguaje.")
    bullet(doc, "El estado inicial de un .afd es el primero del archivo: al "
                "guardar, si el estado inicial no tiene transiciones de salida, "
                "no hay línea que empiece por él y al recargar se tomaría otro "
                "estado como inicial. En la práctica los AFD del curso tienen "
                "transiciones desde el inicial; el Manual de Usuario lo advierte.")
    bullet(doc, "El historial de evaluaciones vive solo en la sesión (memoria): "
                "al cerrar el programa se pierde. El reporte PDF es la forma de "
                "conservarlo.")
    bullet(doc, "Las fuentes del PDF no representan Σ, δ, ε ni →: se sustituyen "
                "por su nombre en ASCII dentro del PDF (el detalle en pantalla "
                "sí los muestra).")

    h1(doc, "8. Pruebas realizadas")
    p(doc, "La lógica se verificó con 45 comprobaciones automatizadas en "
           "test/com/automatas/pruebas/PruebaLogica.java (harness de consola, "
           "no incluido en la interfaz gráfica). Resultado: 45 OK, 0 fallos. "
           "La interfaz se verificó ejecutándola bajo Xvfb y capturando cada "
           "pantalla (las capturas se incluyen en el Manual de Usuario).")
    tabla(doc, ["Grupo", "Comprobaciones", "Resultado"], [
        ["Modo 2 (tabla del enunciado)", "6 transiciones; aceptación B y D; D sin transiciones", "OK"],
        ["AFD: ruta de “aababb”", "Válida; ruta exacta del enunciado", "OK"],
        ["AFD: inválidas", "“aabab” (termina en C), “aababbx” (x no es del alfabeto), vacía", "OK"],
        ["Modo 1", "Transición y banderas; duplicado rechazado con mensaje AFN; epsilon rechazado", "OK"],
        ["Gramática: expansión de “0011”", "Válida; expansión exacta del enunciado", "OK"],
        ["Gramática: inválidas", "“0010”, “2” (no es terminal); “0101” válida; vacía válida (A>epsilon)", "OK"],
        ["Conversión G→AFD y AFD→G", "“0011” y “aababb” válidas en ambos sentidos; “0010” y “aabab” inválidas", "OK"],
        ["Carga .afd / .gtk", "Nombres, inicial y validaciones de los 3 archivos de ejemplo", "OK"],
        ["Guardar y recargar", "Round-trip .afd y .gtk: las cadenas siguen validando igual", "OK"],
        ["Cadenas de ejemplo", "3 válidas y 3 inválidas para el AFD y para la gramática", "OK"],
        ["Graphviz", "DOT con doble círculo y flecha inicial; PNG generado con dot", "OK"],
        ["Reporte PDF", "PDF de AFD y de gramática generados (2 páginas, con grafo)", "OK"],
    ])
    h2(doc, "8.1 Cómo repetir las pruebas")
    codigo(doc, "javac -encoding UTF-8 -d build/classes $(find src -name \"*.java\")\n"
           "javac -encoding UTF-8 -cp build/classes -d /tmp/pruebas test/com/automatas/pruebas/PruebaLogica.java\n"
           "java -cp \"/tmp/pruebas:build/classes\" com.automatas.pruebas.PruebaLogica")
    p(doc, "El harness imprime una línea OK/FALLO por comprobación y el resumen "
           "final. Sale con código 1 si hay algún fallo.")

    h1(doc, "9. Compilación y ejecución")
    h2(doc, "9.1 Desde NetBeans (recomendado)")
    p(doc, "File → Open Project, seleccionar la carpeta, y F6 (Run Project). "
           "NetBeans compila con Ant (build.xml / nbproject/build-impl.xml) y "
           "ejecuta com.automatas.app.Main. El proyecto no necesita librerías "
           "externas: compila solo con el JDK 17.")
    h2(doc, "9.2 Desde consola")
    codigo(doc, "javac -encoding UTF-8 -d build/classes $(find src -name \"*.java\")\n"
           "java -cp build/classes com.automatas.app.Main")
    p(doc, "Se necesita JDK 17 o superior (javac.source y javac.target = 17).")
    h2(doc, "9.3 Estructura del repositorio")
    codigo(doc, "AutomatasAFD/\n"
           "├── build.xml / manifest.mf / nbproject/   Proyecto NetBeans (Ant)\n"
           "├── src/com/automatas/\n"
           "│   ├── app/Main.java\n"
           "│   ├── modelo/      AFD, Gramatica, Transicion, Produccion, ...\n"
           "│   ├── logica/      Validadores, Convertidor, Generadores, ...\n"
           "│   └── vista/       VentanaPrincipal y paneles Swing\n"
           "├── test/com/automatas/pruebas/PruebaLogica.java\n"
           "├── ejemplos/        ejemplo_afd.afd, ejemplo_gramatica.gtk, ejemplo_simple.gtk\n"
           "├── docs/            generar_manuales.py (genera los manuales .docx)\n"
           "├── README.md\n"
           "└── GUION_VIDEO.md")

    doc.save(os.path.join(BASE, "docs", "Manual_Tecnico.docx"))
    print("Manual_Tecnico.docx generado")


if __name__ == "__main__":
    manual_usuario()
    manual_tecnico()

