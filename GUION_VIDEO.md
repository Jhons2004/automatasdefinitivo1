# Guion para presentar el proyecto en video

Duración estimada: **10 a 12 minutos**. Grabar la pantalla con el proyecto
abierto en NetBeans. No hace falta memorizar nada: cada paso dice exactamente
qué escribir y qué se debe ver.

## Antes de grabar

1. Tener NetBeans abierto con el proyecto `AutomatasAFD` (esta carpeta).
2. Tener instalado Graphviz (para que el grafo salga como imagen en el PDF).
   Si no está instalado, el programa igual funciona; el reporte muestra el
   código DOT en texto.
3. Cerrar otras ventanas para que solo se vea NetBeans.

## 0:00 – 0:40 | Presentación

- "Buen día, soy Jonatan Abdiel Alvararado Villavicencio, carné 0910-22-8487,
  de la Sección 3 del curso Autómatas y Lenguajes Formales, con el catedrático
  Alan Gonzalo Ucelo Moran."
- "Este es mi proyecto: un software en Java, hecho en NetBeans, para definir
  gramáticas regulares, generar AFD y evaluar cadenas."

## 0:40 – 1:30 | Ejecutar el programa

1. En NetBeans, presionar **F6** (Run Project).
2. Aparece la **portada** con los datos del curso y del autor.
3. Presionar **ENTER** → aparece el **menú principal** con las 8 opciones.
4. Decir: "El programa empieza por la portada y con ENTER se entra al menú.
   Desde aquí se puede crear un AFD, crear una gramática, evaluar cadenas,
   ver reportes, cargar y guardar archivos."

## 1:30 – 3:30 | Cargar el AFD del enunciado

1. Opción **5. Cargar archivo de entrada** → **Cargar archivo .afd**.
2. Elegir `ejemplos/ejemplo_afd.afd`.
3. Leer el mensaje: 4 estados, 2 símbolos, 6 transiciones, inicial A.
4. Volver al menú → **4. Reportes** → tipo **AFD**, nombre **ejemplo_afd** →
   **Ver Detalle**.
5. Mostrar el detalle: alfabeto, estados, inicial, aceptación y transiciones
   con la notación `A,B,0;false,true`.
6. Decir: "Este es el AFD del ejemplo del enunciado: estados A, B, C, D,
   aceptación en B y D, y el estado D no tiene transiciones."

## 3:30 – 5:30 | Evaluar cadenas en el AFD

1. Menú → **3. Evaluar Cadenas** → tipo **AFD**, nombre **ejemplo_afd**.
2. Escribir `aababb` → **Solo validar**.
3. Mostrar y leer en voz alta:
   `Ruta en AFD: A, A, a; A, A, a; A, C, b; C, B, a; B, C, b; C, D, b`
   `Resultado: cadena válida`
4. Escribir `aabab` → **Solo validar** → `Resultado: cadena inválida`
   (termina en C, que no es de aceptación).
5. Decir: "La ruta muestra cada movimiento: estado de partida, estado de
   llegada y símbolo, separados por punto y coma, como pide el enunciado."

## 5:30 – 7:30 | Cargar la gramática y evaluar 0011

1. Menú → **5. Cargar archivo** → **Cargar archivo .gtk** →
   `ejemplos/ejemplo_gramatica.gtk`.
2. Menú → **3. Evaluar Cadenas** → tipo **Gramática**, nombre
   **ejemplo_gramatica**.
3. Escribir `0011` → **Solo validar**.
4. Mostrar y leer:
   `Expansión Gramática: A>0B>00B>001A>0011A>0011(epsilon)>0011`
   `Resultado: cadena válida`
5. Escribir `0010` → **Solo validar** → `Resultado: cadena inválida`.
6. Decir: "La expansión muestra cada forma sentencial de la derivación por
   la izquierda, y el paso con epsilon queda marcado como 0011(epsilon)."

## 7:30 – 8:30 | Conversión gramática → AFD

1. Menú → **2. Crear Gramática** → escribir el nombre `ejemplo_gramatica`
   y presionar **Nueva gramática** (si ya existe, la reemplaza con los mismos
   datos cargados).
   *Atajo recomendado para el video: en su lugar, desde **Crear Gramática**
   presionar **Generar AFD de esta gramática** solo si la gramática se creó
   a mano. Si se cargó desde archivo, la conversión se demuestra en
   Reportes: el grafo del reporte de la gramática es el AFD equivalente.*
2. Mostrar el mensaje con el nombre del AFD generado (`ejemplo_gramatica_AFD`).

## 8:30 – 10:30 | Reporte PDF con grafo

1. Menú → **4. Reportes** → tipo **AFD**, nombre **ejemplo_afd**.
2. **Generar grafo Graphviz** → guardar como `ejemplo_afd.dot`.
   Mostrar el mensaje con la ruta del PNG generado.
3. **Generar Reporte PDF** → guardar como `Reporte_ejemplo_afd.pdf`.
4. Abrir el PDF y mostrar: el detalle, el grafo (A con su ciclo, C, B y D
   con doble círculo), las 3 cadenas válidas, las 3 inválidas y el historial
   con las evaluaciones que se acaban de hacer (`aababb`, `aabab`, `0011`, `0010`).
5. Decir: "El reporte trae el detalle completo, el grafo en Graphviz, tres
   cadenas válidas y tres inválidas, y el historial de lo evaluado."

## 10:30 – 11:30 | Crear un AFD con el modo 2 (opcional, si da tiempo)

1. Menú → **1. Crear AFD** → nombre `AFDPrueba` → **Nuevo AFD**.
2. En la sección 6 (Modo 2) pegar:
   `[a, b] [A, B, C, D; B, D] [A, C; A, C; B, D; -, -]`
   y presionar **Aplicar tabla (Modo 2)**.
3. Mostrar el mensaje: 6 transiciones agregadas, aceptación B y D.
4. Definir el estado inicial: escribir `A` → **Definir inicial**.
5. Decir: "El modo 2 carga la tabla de transiciones completa de una sola
   vez; el guion '-' significa que ese estado no tiene transición con ese
   símbolo."

## 11:30 – 12:00 | Cierre

- "El proyecto también valida las reglas del curso: no deja repetir estados
  ni símbolos, no deja usar un estado como símbolo del alfabeto, rechaza las
  transiciones con epsilon y las que harían el autómata no determinista, con
  su mensaje de error."
- "El código está organizado en modelo, lógica y vista, con comentarios en
  cada clase y método, y los manuales de usuario y técnico están en la
  carpeta docs. Gracias."

## Si algo sale mal en vivo

- **No aparece el menú:** presionar ENTER en la portada o el botón
  "Continuar al menú".
- **"No hay ningún AFD creado":** primero cargar `ejemplo_afd.afd` (opción 5)
  o crear uno (opción 1).
- **El grafo sale como texto en el PDF:** Graphviz no está instalado en ese
  equipo; el reporte igual se genera con el código DOT. Instalar Graphviz y
  volver a generar el reporte.
- **Error "AFN" al agregar una transición:** es el comportamiento correcto:
  ya existe una transición desde ese estado con ese símbolo.
