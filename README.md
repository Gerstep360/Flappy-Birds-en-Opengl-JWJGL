# Flappy Birds OpenGL - Defensa Academica

Proyecto de Programacion Grafica desarrollado con Java, LWJGL y OpenGL 3.3 (Core Profile). La arquitectura del juego esta basada en un sistema jerarquico de nodos con propagacion automatica de eventos y ciclo de vida, inspirada en el motor de videojuegos Godot.

### Informacion del Estudiante
- Nombre: German Lino Rojas Cruz
- Registro: 221045740

---

# MODIFICACIONES EN VIVO DURANTE LA DEFENSA

EL PROYECTO CUMPLE CON TODOS LOS REQUERIMIENTOS EXIGIDOS. LAS SIGUIENTES MODIFICACIONES SE HAN REALIZADO EN VIVO PARA LA DEFENSA DE EXAMEN DEL PRIMER PARCIAL:

### 1. Tercer Jugador Simultaneo
Se agrego soporte completo para un tercer jugador de forma simultanea e independiente en la misma ventana de juego:
- Controles: El Jugador 3 se controla con la tecla I para saltar.
- Identificacion: Su personaje es de color rojo en modo geometrico y modo texturizado. Su puntaje individual y su estado de vida se controlan por separado.
- Integracion: Sigue las mismas reglas de supervivencia y fisicas individuales de los demas jugadores. La partida continua activa mientras al menos un jugador permanezca con vida; la pantalla de Game Over solo se despliega cuando todos los pajaros colisionan. La distribucion de los marcadores en pantalla (HUD) es la siguiente: el pajaro amarillo tiene su HUD a la izquierda, el pajaro azul tiene su HUD en el medio (debajo de la barra de nivel), y el pajaro rojo tiene su HUD a la derecha.

### 2. Puntaje Objetivo para Terminar el Juego (Limite de Puntaje)
Se incorporo una condicion de finalizacion de la partida por puntaje objetivo:
- Configuracion: En el archivo Config.java, el booleano TERMINAR_PUNTAJE activa o desactiva esta funcion, y la variable entera PUNTAJE_TERMINAR establece la meta (por defecto configurado en 1 punto para pruebas rapidas en vivo).
- Comportamiento: Si la opcion esta activada, la partida finaliza de inmediato (mostrando la pantalla de Game Over y deteniendo el juego con sonido) tan pronto como cualquiera de los jugadores alcance el puntaje objetivo establecido.

---

## Controles del Juego

| Jugador | Accion | Tecla |
| :--- | :--- | :--- |
| Global | Alternar Texturizado / Geometrico | T |
| Global | Activar/Desactivar Musica | M |
| Global | Activar/Desactivar Sonidos | S |
| Global | Reiniciar Partida | R |
| Global | Salir del Juego | ESC |
| Jugador 1 | Saltar / Iniciar Juego | ESPACIO o FLECHA ARRIBA |
| Jugador 2 | Saltar / Iniciar Juego | W |
| Jugador 3 | Saltar / Iniciar Juego | I |

---

## Instrucciones de Ejecucion

Para compilar y ejecutar el proyecto, ejecute los siguientes comandos en la terminal desde el directorio raiz:

```bash
mvn compile          # Compilar el codigo fuente
mvn exec:exec        # Ejecutar la aplicacion
```

---

## Arquitectura del Proyecto: Sistema de Nodos

La arquitectura del videojuego imita el patron de diseno de escena jerarquica del motor Godot. Todo el arbol de componentes propaga automaticamente eventos y renderizado en cascada.

### Estructura de Directorios
```
src/main/java/com/graphics/
  core/     <- Modulo del motor (no se altera para la logica del juego)
  game/     <- Modulo de la logica y componentes especificos del juego
```

### Clases del Core (Motor Grafico)
- Node.java: Clase base que provee funciones para agregar hijos (addChild), liberar memoria (queueFree) y propagar recursivamente las etapas del ciclo de vida.
- Renderer.java: Capa de abstraccion de OpenGL para dibujar cuadrilateros, circulos, triangulos y texturas de manera unificada.
- Shader.java: Carga, compila y gestiona el enlazado de programas de sombreado en la GPU.
- Texture.java: Permite la carga e integracion de imagenes PNG.
- Sound.java: Encargado de cargar y reproducir audio en formato WAV.
- Input.java: Administrador del estado del teclado con deteccion del primer pulso (flanco de subida) por cuadro.
- Animation.java: Administra secuencias de fotogramas para animaciones de sprites en base a tiempo.

### Clases del modulo Game (Juego)
- App.java: Inicializa la ventana GLFW, crea el contexto de OpenGL, maneja el teclado y ejecuta el ciclo de ejecucion principal.
- Game.java: Nodo raiz que controla el estado del juego, gestiona la generacion de obstaculos, evalua las colisiones y computa el escalado de la dificultad.
- Bird.java: Representa el nodo de cada pajaro, encapsulando su comportamiento fisico, animacion y renderizado geometrico personalizado.
- Pipe.java: Representa los obstaculos (tuberias superiores e inferiores).
- HUD.java: Dibuja los puntajes (con digitos en sprite o aproximados por display de 7 segmentos en modo geometrico) y la barra de nivel.
- Background.java: Controla el fondo del escenario y el movimiento parallax.
- ParticleSystem.java: Administra los efectos visuales de particulas.
- Config.java: Centraliza todas las variables y constantes del sistema para facilitar pruebas de modificacion en vivo.

---

## Explicacion del Shader y sus Variables

El motor utiliza un unico programa de sombreado (Shader Program) constituido por un Vertex Shader y un Fragment Shader escritos en GLSL (OpenGL Shading Language) version 330 core.

### Por que se utiliza de esta manera
En OpenGL 3.3 Core Profile no existe el pipeline de funcion fija (funciones obsoletas como glBegin, glEnd, glTranslatef, glRotatef o glPushMatrix). Todo objeto en la pantalla debe ser procesado mediante sombreadores programables.
Para optimizar el rendimiento y evitar transferencias repetitivas de geometria a la GPU, se define una geometria estandarizada en la CPU (por ejemplo, un cuadrado, triangulo o circulo de tamano 1 centrado en el origen). Luego, en cada frame, el Vertex Shader aplica las transformaciones geometricas de traslacion, rotacion, escala y profundidad mediante variables uniformes enviadas desde la CPU.
Asimismo, el Fragment Shader permite alternar dinamicamente entre el color plano de las figuras geometricas o el texturizado de los sprites usando un unico uniforme de control (uUseTexture), simplificando la logica del motor.

### Variables del Vertex Shader (Sombreador de Vertices)
- aPos (atributo de entrada, layout location = 0): Vector de 3 componentes (vec3) que define la posicion local del vertice de la primitiva geometrica.
- aTexCoord (atributo de entrada, layout location = 1): Vector de 2 componentes (vec2) que indica las coordenadas de textura UV del vertice para mapear las imagenes.
- uProjection (uniforme): Matriz de 4x4 (mat4) de proyeccion ortografica. Se encarga de transformar las coordenadas de la escena del juego al espacio normalizado de dispositivo de OpenGL (NDC) en el rango [-1, 1].
- uOffset (uniforme): Vector de 2 componentes (vec2) que traslada la posicion del objeto a sus coordenadas correspondientes en el espacio bidimensional del juego (X, Y).
- uScale (uniforme): Vector de 2 componentes (vec2) que define la escala (ancho y alto) del objeto para dimensionarlo.
- uRotation (uniforme): Valor flotante (float) que indica el angulo de rotacion en radianes. Se utiliza para generar la matriz de rotacion y rotar los vertices en la GPU alrededor del eje Z.
- uDepth (uniforme): Valor flotante (float) que representa la profundidad Z del vertice. Se utiliza para gestionar el orden de superposicion de los elementos (Z-Index) a traves del buffer de profundidad.
- vTexCoord (salida hacia el Fragment Shader): Vector de 2 componentes (vec2) que transfiere las coordenadas de textura interpoladas hacia el fragment shader.

### Variables del Fragment Shader (Sombreador de Fragmentos)
- vTexCoord (entrada desde el Vertex Shader): Vector de 2 componentes (vec2) con las coordenadas de textura interpoladas para el pixel actual.
- uColor (uniforme): Vector de 3 componentes (vec3) que define el color solido (RGB) a pintar cuando no se utiliza textura.
- uTexture (uniforme): Muestreador de texturas 2D (sampler2D) que hace referencia a la unidad de textura donde esta cargada la imagen PNG del sprite.
- uUseTexture (uniforme): Entero (int) que actua como bandera. Si su valor es 1, el pixel se colorea usando el sprite de la textura; si es 0, se colorea con el color plano definido en uColor.
- fragColor (salida): Vector de 4 componentes (vec4) que determina el color final en formato RGBA que se escribira en el frame buffer para ese pixel. Adicionalmente, si el canal Alpha de la textura muestreada es menor a 0.1, se descarta el fragmento (discard) para renderizar pixeles transparentes.

---

## Implementacion de Requerimientos Obligatorios

### 2.1 Pajaro compuesto por figuras geometricas
El personaje principal, cuando se ejecuta en modo geometrico (desactivando las texturas mediante la tecla T), se renderiza como una composicion coherente de figuras basicas mediante llamadas en la clase Bird.java:
- Cuerpo principal: Se dibuja mediante un circulo aproximado por un abanico de triangulos (triangle fan), usando el color caracteristico de cada jugador.
- Pico: Se dibuja con un triangulo naranja rotado y desplazado hacia la parte frontal del cuerpo del pajaro.
- Ojo y Pupila: Compuesto por dos circulos concentricos, un circulo blanco para el globo ocular y un circulo negro mas pequeno en su interior que representa la pupila.
- Cola: Un triangulo posicionado y orientado en la parte trasera del pajaro.
- Ala Animada: Un triangulo superpuesto en el cuerpo. La animacion de aleteo se logra oscilando su angulo de rotacion de forma ciclica a traves de una funcion senoidal (Math.sin(flapTimer)) que depende del tiempo delta.
Toda la composicion se mantiene unida y coherente, rotando de forma automatica en funcion de la velocidad vertical del pajaro.

### 2.2 Modo de dos jugadores simultaneos
El juego permite la participacion simultanea de dos jugadores de forma local en la misma ventana:
- Instanciacion: Se crean dos objetos de la clase Bird con indices de jugador diferentes (0 y 1) y distintas coordenadas X iniciales.
- Controles: Las pulsaciones de teclado se discriminan en App.java: el Jugador 1 salta con la barra ESPACIO o la FLECHA ARRIBA, mientras que el Jugador 2 lo hace con la tecla W.
- Logica de Supervivencia: Las tuberias son comunes y compartidas. Cada pajaro cuenta con su propia velocidad fisica, estado de colision y puntaje acumulado. La partida continua activa mientras al menos un jugador permanezca con vida; la pantalla de Game Over solo se despliega cuando todos los pajaros colisionan.
- Identificacion Visual: Los jugadores se diferencian por el color de sus personajes (amarillo para Jugador 1 con HUD a la izquierda, azul para Jugador 2 con HUD en el medio) tanto en modo geometrico como en modo texturizado, asi como en los marcadores individuales reflejados en el HUD y el titulo de la ventana.

### 2.3 Incremento progresivo de la velocidad
La dificultad del juego escala dinamicamente segun el progreso de los jugadores:
- Calculo del Puntaje: Se toma como referencia el puntaje maximo alcanzado entre los jugadores activos.
- Ajuste Exponencial: A traves de formulas matematicas de decaimiento exponencial inverso en Game.java, se reduce la distancia del espacio entre las tuberias (gap) y se incrementa la velocidad de movimiento de las tuberias y del fondo parallax.
- Frecuencia de Spawn: El intervalo de aparicion de las tuberias disminuye gradualmente a medida que se acumulan puntos.
- Limite de Dificultad: Se aplica un tope de velocidad maxima configurable en Config.java para asegurar la jugabilidad.
- Feedback Visual: El nivel actual y la velocidad se muestran dinamicamente en el titulo de la ventana y en la barra de progreso del HUD.

### 2.4 Mejora de la interfaz del juego
Se integraron mejoras de presentacion visual y sonora para optimizar la experiencia de usuario:
- Escenario y Parallax: El fondo esta constituido por multiples capas (cielo, nubes, suelo) que se desplazan a diferentes velocidades para crear una ilusion de profundidad en 2D.
- Audio: Se implemento una arquitectura de reproduccion de sonidos basada en clips de audio WAV que maneja musica de fondo en bucle y efectos especificos. Ademas, para evitar interferencias en partidas multijugador, se separaron los sonidos individuales de aleteo, puntuacion y colision en canales independientes para cada jugador, permitiendo reproducciones simultaneas limpias.
- Pantallas de Estado: Se disenaron superposiciones de pantalla para las fases de MENU/READY y GAMEOVER. En el estado de Game Over, se introdujo un retraso minimo obligatorio de 1.5 segundos antes de permitir que cualquier jugador reinicie la partida presionando los botones de salto, previniendo reinicios accidentales.
- Efecto Dia/Noche: La iluminacion y los recursos cambian dinamicamente de tonos diurnos a nocturnos (variando el color del cielo y los sprites de las tuberias de verde a rojo) cada cierta cantidad de puntos acumulados.
- Particulas: Se integra un sistema de particulas que emite rafagas de colores cuando un pajaro salta o choca contra una tuberia.
