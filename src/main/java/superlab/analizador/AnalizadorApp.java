package superlab.analizador;

import java.nio.file.Path;

public class AnalizadorApp {
    public static void main(String[] args) throws Exception{
        Analizador a = new Analizador(Path.of("datos"));
        System.out.println(a.identificar());
    }
}
