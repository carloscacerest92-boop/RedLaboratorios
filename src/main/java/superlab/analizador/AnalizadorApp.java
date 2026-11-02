package superlab.analizador;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import superlab.modelo.Incidente;

import java.io.IOException;
import java.nio.file.Path;

public class AnalizadorApp {
    private static final Logger LOG = LogManager.getLogger(AnalizadorApp.class);

    public static void main(String[] args) {
        Path datos = args.length > 0 ? Path.of(args[0]) : Path.of("datos");
        try {
            LOG.info("Analizador iniciado sobre {}", datos);
            Analizador analizador = new Analizador(datos);
            Incidente incidente = analizador.analizarYGenerarSalidas(Path.of("salida"));
            LOG.info("Analisis completado: {}", incidente);
        } catch (IOException e) {
            LOG.error("Error de entrada/salida en el analizador", e);
        }
    }
}
