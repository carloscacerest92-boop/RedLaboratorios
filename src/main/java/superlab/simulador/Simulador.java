package superlab.simulador;

import superlab.persistencia.FicheroBinario;
import superlab.persistencia.FicheroTexto;
import superlab.persistencia.FicheroXml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Simulador {
    private static final String XML_INICIAL = """
            <laboratorio>
              <zona id="AC" nombre="Autocaravana">
                <elemento id="VEH01" tipo="VEHICULO" sensor="1" estado="NORMAL"/>
                <elemento id="EPI01" tipo="PROTECCION" sensor="2" estado="CON_PROTECCION"/>
              </zona>
              <zona id="CC" nombre="Casa clandestina">
                <elemento id="GAS01" tipo="DETECTOR_GAS" sensor="3" estado="NORMAL"/>
                <elemento id="VEN01" tipo="VENTILADOR" sensor="4" estado="FUNCIONANDO"/>
                <elemento id="OLO01" tipo="OLOR_EXTERIOR" sensor="5" estado="NORMAL"/>
              </zona>
              <zona id="LP" nombre="Laboratorio profesional">
                <elemento id="EQU01" tipo="EQUIPO" sensor="6" estado="CALIBRADO"/>
              </zona>
            </laboratorio>
            """;

    private final Path rutaBinario;
    private final Path rutaXml;
    private final FicheroTexto texto;
    private final FicheroBinario binario;
    private final FicheroXml xml;

    public Simulador(Path carpetaDatos)throws IOException {
        this.rutaBinario = carpetaDatos.resolve("sensores.dat");
        this.rutaXml = carpetaDatos.resolve("estado.xml");
        this.texto = new FicheroTexto(carpetaDatos.resolve("historial.txt"));
        this.binario = new FicheroBinario(rutaBinario);
        this.xml = new FicheroXml(rutaXml);
    }

    public void iniciar()throws IOException{
        Files.deleteIfExists(rutaBinario);
        Files.writeString(rutaXml,XML_INICIAL);

        long ahora = System.currentTimeMillis();
        binario.anadir(new FicheroBinario.Lectura(1,ahora,20.0));
        binario.anadir(new FicheroBinario.Lectura(2,ahora,10.0));
        binario.anadir(new FicheroBinario.Lectura(3,ahora,5.0));
        binario.anadir(new FicheroBinario.Lectura(4,ahora,1200.0));
        binario.anadir(new FicheroBinario.Lectura(5,ahora,10.0));
        binario.anadir(new FicheroBinario.Lectura(6,ahora,0.5));

        texto.registrar("LAB",0,"Inicio de operacion");

    }
}
