# Superlaboratorio Forense · Breaking Bad

URL de GitHub: https://github.com/carloscacerest92-boop/RedLaboratorios

Aplicación Java con dos programas independientes: un **simulador** que genera y actualiza ficheros de datos mientras opera un sistema y puede provocar un incidente, y un **analizador forense** que lee esos ficheros después, identifica el incidente y genera sus propias salidas.

## Temática

Una red de laboratorios clandestinos con tres zonas:

| Zona | Código | Elementos |
|---|---|---|
| Autocaravana | `AC` | vehículo (`VEH01`), protección del operario (`EPI01`) |
| Casa clandestina | `CC` | detector de gas (`GAS01`), ventilador (`VEN01`), olor exterior (`OLO01`) |
| Laboratorio profesional | `LP` | equipo de síntesis (`EQU01`) |

## Incidentes posibles

| Incidente | Zona | Síntoma en el BIN (`sensores.dat`) | Estado en el XML |
|---|---|---|---|
| `VEHICULO_SOSPECHOSO` | AC | sensor 1 (minutos estacionada) > 180 | `VEH01` = `VIGILADA` |
| `FALTA_PROTECCION` | AC | sensor 2 (stock de EPI) < 1 | `EPI01` = `SIN_PROTECCION` |
| `FALTA_VENTILACION` | CC | sensor 4 (rpm) < 100 y sensor 3 (gas) > 80 | `VEN01` = `APAGADO` |
| `OLOR_VECINOS` | CC | sensor 5 (olor) > 70 con sensor 4 (rpm) >= 100 | `OLO01` = `ALERTA_VECINAL` |
| `EQUIPO_DESCALIBRADO` | LP | sensor 6 (desviación %) > 5 | `EQU01` = `DESCALIBRADO` |

Si los datos no encajan con ninguno, el analizador devuelve `INDETERMINADO`. La diferencia entre `FALTA_VENTILACION` y `OLOR_VECINOS` es el ventilador: parado en uno, funcionando en el otro.

## Ficheros y cómo se relacionan

**Los genera el simulador (carpeta `datos/`):**

| Fichero | Contenido | API |
|---|---|---|
| `historial.txt` | Histórico secuencial legible: `fecha hora \| zona \| S<sensor> \| mensaje`. Se acumula entre ejecuciones. | NIO.2 (`Files`, `Path`) |
| `sensores.dat` | Estado actual de los 6 sensores. Registros fijos de 20 bytes: `int idSensor` (4) + `long timestamp` (8) + `double valor` (8). Posición = `(idSensor - 1) * 20`. | `RandomAccessFile` |
| `estado.xml` | Estructura del sistema: zonas, elementos y su estado. | DOM (`org.w3c.dom`) |

**Los genera el analizador (carpeta `salida/`):**

| Fichero | Contenido | API |
|---|---|---|
| `informe_forense.txt` | Incidente identificado, efectos y conclusión. | Java I/O clásico (`FileWriter`, `PrintWriter`) |
| `telemetria_anomala.dat` | Solo las lecturas de sensores que intervinieron en el análisis. | `DataOutputStream` |
| `estado_afectado.xml` | Solo los elementos afectados, agrupados en su zona. | DOM |

**Cómo se relacionan:** el **ID de sensor** une el registro binario (`idSensor`), el atributo `sensor` de cada elemento del XML y la columna `S<n>` del historial de texto. La zona (`AC`, `CC`, `LP`) aparece en el TXT y en el XML, y el `timestamp` del binario sirve para ordenar las lecturas.

**Cómo decide el analizador:** abre `sensores.dat`, `estado.xml` y `historial.txt` (solo recibe la carpeta con los ficheros, sin conocer al simulador) y aplica condiciones conocidas que exigen las **tres fuentes a la vez**: un valor anómalo en el binario, el estado del elemento en el XML y el síntoma descrito en el historial (solo se leen las líneas posteriores al último `Inicio de operacion`, porque el historial acumula ejecuciones). Los textos de efectos y conclusión de cada incidente viven en el `enum Incidente`.

## Requisitos y dependencias

- JDK 21 o superior (el proyecto compila con `--release 21`; probado con JDK 25).
- Maven (o IntelliJ IDEA, que lo incluye).
- Log4j 2.26.1, JUnit 5.10.2 (las descarga Maven).

## Cómo ejecutar

Todos los comandos se lanzan desde la **raíz del proyecto**, porque `datos/`, `salida/` y `logs/` son rutas relativas.

### Simulador

Modo normal (incidente aleatorio):

```
mvn compile exec:java -Dexec.mainClass=superlab.simulador.SimuladorApp
```

Provocar un incidente concreto (modo de prueba):

```
mvn compile exec:java -Dexec.mainClass=superlab.simulador.SimuladorApp -Dexec.args="--incidente=OLOR_VECINOS"
```

Valores válidos: `VEHICULO_SOSPECHOSO`, `FALTA_PROTECCION`, `FALTA_VENTILACION`, `OLOR_VECINOS`, `EQUIPO_DESCALIBRADO`.

El simulador reinicia el binario y el XML en cada arranque, escribe los síntomas en los tres ficheros, y solo después lanza `EventoCriticoException`. El `catch` únicamente registra con Log4j.

### Analizador forense

```
mvn compile exec:java -Dexec.mainClass=superlab.analizador.AnalizadorApp
```

Por defecto lee `datos/`; se puede indicar otra carpeta con `-Dexec.args="ruta/de/datos"`. Escribe los tres ficheros en `salida/`.

### Pruebas automatizadas

```
mvn test
```

Hay pruebas JUnit 5 de: texto, binario (`RandomAccessFile`), XML, anomalías, simulador (excepción esperada), analizador (parametrizada con un caso por incidente, más `INDETERMINADO`) y una prueba de integración del flujo completo. Usan `@TempDir`, así que no tocan los ficheros reales.

### Desde IntelliJ IDEA

Ejecutar el `main` de `SimuladorApp` o `AnalizadorApp`. Para elegir incidente: Run > Edit Configurations > *Program arguments* > `--incidente=FALTA_VENTILACION`. Las pruebas: clic derecho en `src/test/java` > Run 'All Tests'.

## Logs (Log4j 2)

Configuración externa en `src/main/resources/log4j2.xml`. Cada línea lleva fecha y hora, nivel, clase y mensaje.

- **`logs/evidencias/ejecucion_dd-MM-yyyy_HH-mm-ss.log`:** un fichero por ejecución, con fecha y hora en el nombre (sin el carácter `:`).
- **`logs/runtime/aplicacion.log`:** log técnico con rotación automática.
  - **Condición de rollover:** al alcanzar **1 KB** (`SizeBasedTriggeringPolicy`), un límite pequeño para poder demostrarlo.
  - **Nombre de los históricos:** `aplicacion-yyyy-MM-dd-N.log.gz`, con fecha e índice.
  - **Retención:** `max=5`; al generar el sexto, se elimina el más antiguo.
  - **Compresión `.gz`:** los históricos ocupan mucho menos y se pueden conservar más tiempo.
  - **Activo frente a rotado:** el activo recibe los mensajes nuevos; el rotado es una copia cerrada, comprimida y ya no se modifica.

Estos logs son solo trazabilidad técnica: el analizador no los lee.

## Estructura

```
proyecto/
├── src/main/java/superlab/{simulador,analizador,persistencia,modelo,excepciones}
├── src/main/resources/log4j2.xml
├── src/test/java/superlab/...
├── datos/       ficheros generados por el simulador (ejemplos)
├── salida/      ficheros generados por el analizador (ejemplos)
├── logs/
│   ├── runtime/
│   └── evidencias/
├── IA.md
├── README.md
└── pom.xml
```
