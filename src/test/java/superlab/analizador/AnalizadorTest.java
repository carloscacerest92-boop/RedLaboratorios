package superlab.analizador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import superlab.excepciones.EventoCriticoException;
import superlab.modelo.Incidente;
import superlab.simulador.Simulador;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnalizadorTest {

    @TempDir
    Path carpeta;

    @ParameterizedTest
    @EnumSource(value = Incidente.class, names = "INDETERMINADO", mode = EnumSource.Mode.EXCLUDE)
    void identificaCadaIncidente(Incidente incidente) throws Exception {
        Simulador sim = new Simulador(carpeta);
        sim.iniciar();
        assertThrows(EventoCriticoException.class, () -> sim.provocar(incidente));

        Analizador analizador = new Analizador(carpeta);

        assertEquals(incidente, analizador.identificar());
    }

    @Test
    void sinIncidenteDevuelveIndeterminado()throws Exception{
        Simulador sim = new Simulador(carpeta);
        sim.iniciar();
        Analizador analizador = new Analizador(carpeta);

        assertEquals(Incidente.INDETERMINADO,analizador.identificar());
    }

    @Test
    void sinSintomaEnElHistorialNoSeDeterminaElIncidente() throws Exception {
        Simulador sim = new Simulador(carpeta);
        sim.iniciar();
        assertThrows(EventoCriticoException.class,
                () -> sim.provocar(Incidente.FALTA_VENTILACION));
        // el binario y el XML tienen el incidente, pero el historial ya no recoge el sintoma
        Files.writeString(carpeta.resolve("historial.txt"),
                "2026-11-02 10:00:00 | LAB | S0 | Inicio de operacion" + System.lineSeparator());

        assertEquals(Incidente.INDETERMINADO, new Analizador(carpeta).identificar());
    }
}
