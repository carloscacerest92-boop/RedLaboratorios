package superlab.persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FicheroBinarioTest {

    @TempDir
    Path carpeta;

    @Test
    void anadirLeerYActualizarUnSensor() throws Exception {
        FicheroBinario fb = new FicheroBinario(carpeta.resolve("sensores.dat"));
        fb.anadir(new FicheroBinario.Lectura(1, 1000L, 10.0));
        fb.anadir(new FicheroBinario.Lectura(2, 2000L, 20.0));
        fb.anadir(new FicheroBinario.Lectura(3, 3000L, 30.0));

        assertEquals(3, fb.contarRegistros());
        assertEquals(20.0, fb.leer(2).valor());

        fb.actualizar(2, 9999L, 99.5);

        assertEquals(99.5, fb.leer(2).valor());
        assertEquals(10.0, fb.leer(1).valor());
        assertEquals(30.0, fb.leer(3).valor());
    }

    @Test
    void anadirFueraDeOrdenLanzaExcepcion() throws Exception{
        FicheroBinario fb = new FicheroBinario(carpeta.resolve("sensores.dat"));

        assertThrows(IllegalArgumentException.class,()->fb.anadir(new FicheroBinario.Lectura(2,1000L,10.0)));
    }
}