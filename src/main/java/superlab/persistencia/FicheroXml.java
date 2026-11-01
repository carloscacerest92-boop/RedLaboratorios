package superlab.persistencia;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FicheroXml {
    private final Path ruta;

    public FicheroXml(Path ruta) throws IOException {
        this.ruta = ruta;
        if (ruta.getParent() != null) {
            Files.createDirectories(ruta.getParent());
        }
    }

    private Document cargar() throws IOException {
        try {
            return DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(ruta.toFile());
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("XML inválido: " + ruta, e);
        }
    }

    private void guardar(Document doc) throws IOException {
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(ruta.toFile()));
        } catch (TransformerException e) {
            throw new IOException("No se pudo guardar " + ruta, e);
        }
    }

    public String leerEstado(String idElemento) throws IOException {
        NodeList elementos = cargar().getElementsByTagName("elemento");
        for (int i = 0; i < elementos.getLength(); i++) {
            Element e = (Element) elementos.item(i);
            if (e.getAttribute("id").equals(idElemento)) {
                return e.getAttribute("estado");
            }
        }
        throw new IllegalArgumentException("Elemento inexistente: " + idElemento);
    }

    public void cambiarEstado(String idElemento, String nuevoEstado) throws IOException {
        Document doc = cargar();
        NodeList elementos = doc.getElementsByTagName("elemento");
        for (int i = 0; i < elementos.getLength(); i++) {
            Element e = (Element) elementos.item(i);
            if (e.getAttribute("id").equals(idElemento)){
                e.setAttribute("estado",nuevoEstado);
                guardar(doc);
                return;
            }
            
        }
        throw new IllegalArgumentException("Elemento inexistente: "+idElemento);
    }
}