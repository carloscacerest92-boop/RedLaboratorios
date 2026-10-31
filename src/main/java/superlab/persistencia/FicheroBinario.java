package superlab.persistencia;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

public class FicheroBinario {
    private static final int TAMANO_REGISTRO = 20;
    private final Path ruta;

    public record Lectura(int idSensor, long timestamp, double valor){    }

    public FicheroBinario(Path ruta) throws IOException {
        this.ruta = ruta;
        if (ruta.getParent()!=null){
            Files.createDirectories(ruta.getParent());
        }

    }

    public void anadir(Lectura l) throws IOException {
        long siguiente = contarRegistros()+1;
        if (l.idSensor()!=siguiente){
            throw new IllegalArgumentException("Se esperaba el sensor "+siguiente+" pero llego el  "+l.idSensor());
        }
        try (RandomAccessFile escritura = new RandomAccessFile(ruta.toFile(), "rw")) {
            escritura.seek(escritura.length());
            escritura.writeInt(l.idSensor());
            escritura.writeLong(l.timestamp());
            escritura.writeDouble(l.valor());
        }
    }

    public Lectura leer(int idSensor) throws IOException {
        comprobarSensor(idSensor);

        try (RandomAccessFile lectura = new RandomAccessFile(ruta.toFile(),"r")){
            lectura.seek(posicion(idSensor));
            int id = lectura.readInt();
            long ts = lectura.readLong();
            double valor = lectura.readDouble();

            if(id!=idSensor){
                throw new IOException("Registro corrupto: se pedia el sensor: "+idSensor+" y habia el "+id);
            }
            return new Lectura(id, ts, valor);
        }
    }

    public void actualizar(int idSensor, long timestamp, double valor) throws IOException {
        comprobarSensor(idSensor);
        try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(),"rw")){
            raf.seek(posicion(idSensor)+4);
            raf.writeLong(timestamp);
            raf.writeDouble(valor);
        }
    }

    public long contarRegistros() throws IOException {
        if (!Files.exists(ruta)){
            return 0;
        }
        try (RandomAccessFile raf = new RandomAccessFile(ruta.toFile(),"r")){
            return (raf.length())/TAMANO_REGISTRO;

        }
    }

    private long posicion(int idSensor){
        return (long)(idSensor-1)*TAMANO_REGISTRO;
    }

    public void comprobarSensor(int idSensor) throws IOException {
        if (idSensor<1||idSensor>contarRegistros()){
            throw new IllegalArgumentException("Sensor inexistente: "+idSensor);
        }
    }
}
