package superlab.persistencia;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FicheroAnomalias {
    private final Path ruta;
    private static final int TAMANO_REGISTRO = 20;

    public FicheroAnomalias(Path ruta)throws IOException {
        this.ruta = ruta;
        if (ruta.getParent()!=null){
            Files.createDirectories(ruta.getParent());
        }
    }

    public void guardar(List<FicheroBinario.Lectura> lecturas)throws IOException{

        try(DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(ruta)))) {
            for (FicheroBinario.Lectura l : lecturas) {
                out.writeInt(l.idSensor());
                out.writeLong(l.timestamp());
                out.writeDouble(l.valor());
            }

        }
    }

    public List<FicheroBinario.Lectura> leerTodas()throws IOException{
        if (!Files.exists(ruta)) {
            return List.of();
        }

        List<FicheroBinario.Lectura> lecturas = new ArrayList<>();

        long cantidad = Files.size(ruta) /TAMANO_REGISTRO;
        try(DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(ruta)))) {
            for (int i = 0; i < cantidad; i++) {
                int id = in.readInt();
                long ts =in.readLong();
                double valor = in.readDouble();
                lecturas.add(new FicheroBinario.Lectura(id,ts,valor));
            }

        }
        return lecturas;
    }
}
