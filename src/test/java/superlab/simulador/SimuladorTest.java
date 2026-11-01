package superlab.simulador;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import superlab.persistencia.FicheroBinario;
import superlab.persistencia.FicheroXml;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimuladorTest {

    @TempDir
    Path carpeta;

    @Test
    void iniciarCrearLosTresFicherosEnEstadoNormal()throws Exception{
        Simulador sim = new Simulador(carpeta);
        sim.iniciar();

        FicheroBinario fb = new FicheroBinario(carpeta.resolve("sensores.dat"));
        FicheroXml fx = new FicheroXml(carpeta.resolve("estado.xml"));

        assertEquals(6,fb.contarRegistros());
        assertEquals(1200.0,fb.leer(4).valor());
        assertEquals("FUNCIONANDO",fx.leerEstado("VEN01"));
    }
}
