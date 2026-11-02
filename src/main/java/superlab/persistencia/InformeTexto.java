package superlab.persistencia;

import superlab.modelo.Incidente;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class InformeTexto {
    private final Path ruta;

    public InformeTexto(Path ruta)throws IOException{
        this.ruta = ruta;
        if (ruta.getParent()!=null){
            Files.createDirectories(ruta.getParent());
        }
    }

    public void escribir(Incidente incidente, List<FicheroBinario.Lectura> anomalas)throws IOException{
        try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(ruta.toFile())))) {
            pw.println("INFORME FORENSE");
            pw.println("Incidente identificado: "+incidente);
            pw.println("Efectos detectados: "+incidente.getEfectos());
            pw.println("Lecturas anomalas utilizadas: "+anomalas.size());
            pw.println("Conclusion: "+incidente.getConclusion());

        }
    }
}
