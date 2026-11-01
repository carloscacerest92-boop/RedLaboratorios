package superlab.analizador;

import superlab.modelo.Incidente;
import superlab.persistencia.FicheroBinario;
import superlab.persistencia.FicheroXml;

import java.io.IOException;
import java.nio.file.Path;

public class Analizador {
    private final FicheroBinario binario;
    private final FicheroXml xml;

    public Analizador(Path carpetaDatos)throws IOException {
        this.binario = new FicheroBinario(carpetaDatos.resolve("sensores.dat"));
        this.xml = new FicheroXml(carpetaDatos.resolve("estado.xml"));
    }

    public Incidente identificar()throws IOException{
        double minutosEstacionada = binario.leer(1).valor();
        double stockEpi = binario.leer(2).valor();
        double gas = binario.leer(3).valor();
        double rpm = binario.leer(4).valor();
        double olor = binario.leer(5).valor();
        double desviacion = binario.leer(6).valor();


        if (rpm<100 && gas>80&&xml.leerEstado("VEN01").equals("APAGADO")){
            return Incidente.FALTA_VENTILACION;
        }

        if (olor>70&&rpm>=100&&xml.leerEstado("OLO01").equals("ALERTA_VECINAL")){
            return Incidente.OLOR_VECINOS;
        }

        if (minutosEstacionada>180&&xml.leerEstado("VEH01").equals("VIGILADA")){
            return Incidente.VEHICULO_SOSPECHOSO;
        }

        if (stockEpi<1&&xml.leerEstado("EPI01").equals("SIN_PROTECCION")){
            return Incidente.FALTA_PROTECCION;
        }

        if (desviacion>5&&xml.leerEstado("EQU01").equals("DESCALIBRADO")){
            return Incidente.EQUIPO_DESCALIBRADO;
        }


        return Incidente.INDETERMINADO;
    }
}
