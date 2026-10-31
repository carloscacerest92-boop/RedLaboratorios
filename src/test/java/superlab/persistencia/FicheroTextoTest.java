package superlab.persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FicheroTextoTest {

    @TempDir
    Path carpeta;

    @Test
    void registrarYLeerLineas()throws Exception{
        FicheroTexto ft = new FicheroTexto(carpeta.resolve("historial.txt"));
        ft.registrar("CC",4,"Ventilador detenido");
        ft.registrar("CC",5,"Vecino denuncia olor");

        List<String> lineas = ft.leerTodas();

        assertEquals(2,lineas.size());
        assertTrue(lineas.get(0).contains("Ventilador detenido"));
    }

    @Test
    void leerTodasDevuelveListaVaciaSiNoExisteElFichero()throws Exception{
        FicheroTexto ft = new FicheroTexto(carpeta.resolve("historial.txt"));

        assertTrue(ft.leerTodas().isEmpty());

    }
}
