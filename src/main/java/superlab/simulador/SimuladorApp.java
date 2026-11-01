package superlab.simulador;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import superlab.excepciones.EventoCriticoException;
import superlab.modelo.Incidente;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

public class SimuladorApp {
    private static final Logger LOG =LogManager.getLogger(SimuladorApp.class);

    public static void main(String[] args) {
        try {
            Simulador sim = new Simulador(Path.of("datos"));
            sim.iniciar();
            Incidente incidente = elegirIncidente(args);
            LOG.info("Simulacion iniciada");
            sim.provocar(incidente);

        }catch (EventoCriticoException e){
            LOG.error("Evento critico capturado: {}",e.getMessage());

        }catch (IOException e){
            LOG.error("Error de entrada/salida en el simulador",e);

        }catch (IllegalArgumentException e){
            LOG.error("Incidente no valido: {}",e.getMessage());

        }
    }

    private static Incidente elegirIncidente(String[] args){
        for (String arg : args) {
            if (arg.startsWith("--incidente")){
                return Incidente.valueOf(arg.substring("--incidente".length()));
            }
            
        }
        Incidente[] posibles = Incidente.values();
        return posibles[new Random().nextInt(posibles.length -1)];

    }

}
