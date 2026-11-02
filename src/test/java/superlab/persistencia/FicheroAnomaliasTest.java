package superlab.persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FicheroAnomaliasTest {

    @TempDir
    Path carpeta;

    @Test
    void guardarYLeerAnomalias()throws Exception{
        FicheroAnomalias fa = new FicheroAnomalias(carpeta.resolve("anomalias.dat"));
        fa.guardar(List.of(
                new FicheroBinario.Lectura(3,1000L,120.0),
                new FicheroBinario.Lectura(4,1000L,0.0)));

        List<FicheroBinario.Lectura> lecturas = fa.leerTodas();

        assertEquals(2, lecturas.size());
        assertEquals(0.0, lecturas.get(1).valor());
    }

    @Test
    void leerTodasDevuelveListaVaciaSiNoExisteElFichero() throws Exception {
        FicheroAnomalias fa = new FicheroAnomalias(carpeta.resolve("anomalias.dat"));

        assertTrue(fa.leerTodas().isEmpty());
    }
}
