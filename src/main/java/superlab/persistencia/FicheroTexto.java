package superlab.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FicheroTexto {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Path ruta;

    public FicheroTexto(Path ruta) throws IOException {
        this.ruta = ruta;
        if (ruta.getParent()!=null){
            Files.createDirectories(ruta.getParent());
        }
    }

    public void registrar(String zona, int idSensor, String mensaje) throws IOException {
        String linea = LocalDateTime.now().format(FORMATO)
                + " | " + zona + " | S" + idSensor + " | " + mensaje
                + System.lineSeparator();
        Files.writeString(ruta, linea,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public List<String> leerTodas() throws IOException {
        if (!Files.exists(ruta)){
            return List.of();
        }
        return Files.readAllLines(ruta);
    }
}
