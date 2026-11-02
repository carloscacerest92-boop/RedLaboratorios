package superlab.analizador;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import superlab.modelo.Incidente;
import superlab.persistencia.FicheroAnomalias;
import superlab.persistencia.FicheroBinario.Lectura;
import superlab.persistencia.InformeTexto;
import superlab.persistencia.FicheroBinario;
import superlab.persistencia.FicheroXml;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Analizador {
    private static final Logger LOG = LogManager.getLogger(Analizador.class);
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

    public Incidente analizarYGenerarSalidas(Path carpetaSalida) throws IOException {
        LOG.info("{} registros binarios procesados", binario.contarRegistros());
        Incidente incidente = identificar();
        LOG.info("Incidente identificado: {}", incidente);

        List<Lectura> anomalas = new ArrayList<>();
        for (int id : sensoresRelevantes(incidente)) {
            anomalas.add(binario.leer(id));
        }

        new InformeTexto(carpetaSalida.resolve("informe_forense.txt")).escribir(incidente, anomalas);
        new FicheroAnomalias(carpetaSalida.resolve("telemetria_anomala.dat")).guardar(anomalas);
        xml.exportarAfectados(carpetaSalida.resolve("estado_afectado.xml"), elementosRelevantes(incidente));

        LOG.info("Salidas generadas en {}", carpetaSalida);
        return incidente;
    }

    private List<Integer> sensoresRelevantes(Incidente incidente) {
        return switch (incidente) {
            case FALTA_VENTILACION -> List.of(3, 4);
            case OLOR_VECINOS -> List.of(5);
            case VEHICULO_SOSPECHOSO -> List.of(1);
            case FALTA_PROTECCION -> List.of(2);
            case EQUIPO_DESCALIBRADO -> List.of(6);
            case INDETERMINADO -> List.of();
        };
    }

    private List<String> elementosRelevantes(Incidente incidente) {
        return switch (incidente) {
            case FALTA_VENTILACION -> List.of("GAS01", "VEN01");
            case OLOR_VECINOS -> List.of("OLO01", "VEN01");
            case VEHICULO_SOSPECHOSO -> List.of("VEH01");
            case FALTA_PROTECCION -> List.of("EPI01");
            case EQUIPO_DESCALIBRADO -> List.of("EQU01");
            case INDETERMINADO -> List.of();
        };
    }
}
