package superlab.persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FicheroXmlTest {

    @TempDir
    Path carpeta;

    private static final String XML= """
            <laboratorio>
              <zona id="CC" nombre="Casa clandestina">
                <elemento id="VEN01" tipo="VENTILADOR" sensor="4" estado="FUNCIONANDO"/>
                <elemento id="OLO01" tipo="OLOR_EXTERIOR" sensor="5" estado="NORMAL"/>
              </zona>
            </laboratorio>
            """;

    @Test
    void leerYCambiarEstado()throws Exception{
        Path ruta = carpeta.resolve("estadoXml");
        Files.writeString(ruta,XML);
        FicheroXml fx = new FicheroXml(ruta);

        assertEquals("FUNCIONANDO", fx.leerEstado("VEN01"));

        fx.cambiarEstado("VEN01","APAGADO");

        assertEquals("APAGADO",fx.leerEstado("VEN01"));
        assertEquals("NORMAL",fx.leerEstado("OLO01"));
    }

    @Test
    void leerElementoInexistenteLanzaExcepcion()throws Exception{
        Path ruta = carpeta.resolve("estado.xml");
        Files.writeString(ruta,XML);
        FicheroXml fx = new FicheroXml(ruta);

        assertThrows(IllegalArgumentException.class,
                ()-> fx.leerEstado("VEN02"));
    }
}
