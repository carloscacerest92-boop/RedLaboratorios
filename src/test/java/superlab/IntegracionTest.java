package superlab;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import superlab.analizador.Analizador;
import superlab.excepciones.EventoCriticoException;
import superlab.modelo.Incidente;
import superlab.persistencia.FicheroAnomalias;
import superlab.persistencia.FicheroBinario.Lectura;
import superlab.persistencia.FicheroXml;
import superlab.simulador.Simulador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IntegracionTest {

    @TempDir
    Path carpeta;

    @Test
    void flujoCompletoDelSimuladorAlAnalizador() throws Exception {
        Path datos = carpeta.resolve("datos");
        Path salida = carpeta.resolve("salida");

        // 1. El simulador opera y provoca un incidente: persiste los datos y lanza la excepcion
        Simulador sim = new Simulador(datos);
        sim.iniciar();
        assertThrows(EventoCriticoException.class,
                () -> sim.provocar(Incidente.FALTA_VENTILACION));

        // 2. El analizador solo conoce la carpeta con los ficheros
        Analizador analizador = new Analizador(datos);
        Incidente incidente = analizador.analizarYGenerarSalidas(salida);

        assertEquals(Incidente.FALTA_VENTILACION, incidente);

        // 3. Salida TXT: informe legible
        String informe = Files.readString(salida.resolve("informe_forense.txt"));
        assertTrue(informe.contains("FALTA_VENTILACION"));

        // 4. Salida BIN: solo las lecturas anomalas (gas y rpm)
        List<Lectura> anomalas =
                new FicheroAnomalias(salida.resolve("telemetria_anomala.dat")).leerTodas();
        assertEquals(2, anomalas.size());
        assertEquals(120.0, anomalas.get(0).valor());
        assertEquals(0.0, anomalas.get(1).valor());

        // 5. Salida XML: solo los elementos afectados
        FicheroXml afectados = new FicheroXml(salida.resolve("estado_afectado.xml"));
        assertEquals("APAGADO", afectados.leerEstado("VEN01"));
        assertThrows(IllegalArgumentException.class, () -> afectados.leerEstado("OLO01"));
    }
}
