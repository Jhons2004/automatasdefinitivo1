# AutomatasAFD — Software de Autómatas Finitos y Gramáticas Regulares

Proyecto del curso **Autómatas y Lenguajes Formales (VI Ciclo, Sección 3)**,
Universidad Mariano Gálvez de Guatemala.

**Autor:** Jonatan Abdiel Alvarado Villavicencio — **Carné:** 0910-22-8487
**Catedrático:** Alan Gonzalo Ucelo Moran

Aplicación de escritorio en **Java (NetBeans)** que permite definir gramáticas
regulares, generar Autómatas Finitos Deterministas (AFD), evaluar cadenas de
entrada y generar reportes en PDF con el grafo del autómata (Graphviz).

## Requisitos

- **JDK 17** o superior.
- **NetBeans 17+** (proyecto Ant estándar, se abre con *File → Open Project*).
- **Graphviz** (opcional, solo para renderizar el grafo a PNG; sin él, el
  programa igual genera el archivo `.dot` y el reporte lo incluye como texto).

## Cómo ejecutar

1. Abrir NetBeans → *File → Open Project* → seleccionar esta carpeta.
2. Presionar **F6** (Run) o *Run → Run Project*.
3. La portada aparece primero; presionar **ENTER** para ir al menú principal.

Desde consola (estando en esta carpeta):

```bash
javac -encoding UTF-8 -d build/classes $(find src -name "*.java")
java -cp build/classes com.automatas.app.Main
```

## Qué hace el programa

| Menú | Descripción |
|------|-------------|
| 1. Crear AFD | Estados, alfabeto, estado inicial, aceptación y transiciones en modo 1 (`A,B,0;false,true`) o modo 2 (tabla matricial). |
| 2. Crear Gramática | No terminales, terminales, NT inicial y producciones (`A > a B`, con `\|` o `epsilon`). |
| 3. Evaluar Cadenas | Valida una cadena y muestra la **ruta en AFD** o la **expansión de la gramática** con el resultado. |
| 4. Reportes | Ver Detalle y Generar Reporte PDF (detalle + grafo Graphviz + 3 cadenas válidas y 3 inválidas + historial). |
| 5. Cargar archivo | Archivos `.afd` y `.gtk` con File Chooser. |
| 6. Guardar | Guarda el AFD o la gramática en `.afd` / `.gtk`. |
| 7. Ayuda | Datos del curso, catedrático y último dígito del carné. |
| 8. Salir | Cierra el programa. |

Además: conversión en ambos sentidos (**gramática → AFD** y **AFD → gramática**),
generación de grafos Graphviz (`.dot` y PNG) y registro del historial de
evaluaciones de la sesión.

## Ejemplos incluidos (`ejemplos/`)

- `ejemplo_afd.afd` — El AFD del enunciado (estados A, B, C, D; aceptación B, D).
  La cadena `aababb` es **válida** con ruta `A, A, a; A, A, a; A, C, b; C, B, a; B, C, b; C, D, b`.
- `ejemplo_gramatica.gtk` — La gramática del enunciado (`A>0B | 1A | epsilon`, `B>0B | 1A`).
  La cadena `0011` es **válida** con expansión `A>0B>00B>001A>0011A>0011(epsilon)>0011`.
- `ejemplo_simple.gtk` — El ejemplo de carga masiva del enunciado (`A>Ab`, `A>bC`, `B>c`, `C>d`).

## Estructura del proyecto

```text
AutomatasAFD/
├── build.xml                 Proyecto NetBeans (Ant)
├── manifest.mf
├── nbproject/                Configuración de NetBeans
├── src/com/automatas/
│   ├── app/                  Main (punto de entrada)
│   ├── modelo/               AFD, Gramatica, Transicion, Produccion, Constantes, DatosDelCurso
│   ├── logica/               Validadores, Convertidor, GeneradorCadenas, GeneradorGrafos,
│   │                         GeneradorPDF, ManejadorArchivos, ParserTransiciones, Repositorio
│   └── vista/                VentanaPrincipal y paneles Swing (portada, menú, crear, evaluar, reportes, archivos)
├── test/com/automatas/pruebas/   PruebaLogica (45 comprobaciones de la lógica)
├── ejemplos/                 Archivos .afd y .gtk de ejemplo
└── docs/
    └── generar_manuales.py   Script que genera los manuales (.docx) con python-docx
```

## Verificación

La lógica se verificó con 45 comprobaciones automatizadas
(`test/com/automatas/pruebas/PruebaLogica.java`): los ejemplos del enunciado
(ruta de `aababb`, expansión de `0011`), conversiones en ambos sentidos,
carga/guardado de archivos, modos 1 y 2, errores de determinismo y epsilon,
generación de cadenas, grafo Graphviz y reportes PDF. Ver el Manual Técnico,
sección de pruebas.

## Documentación

- `docs/Manual_de_Usuario.docx` — Funcionamiento de la aplicación a nivel de usuario.
- `docs/Manual_Tecnico.docx` — Lógica de programación, teoría de autómatas,
  gramáticas y lenguajes formales, arquitectura y pruebas.
- `GUION_VIDEO.md` — Guion paso a paso para presentar el proyecto en video.
