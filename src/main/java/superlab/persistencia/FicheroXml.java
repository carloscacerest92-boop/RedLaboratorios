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
import java.util.List;

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
        guardarEn(doc, ruta);
    }

    private void guardarEn(Document doc, Path destino) throws IOException {
        try {
            if (destino.getParent() != null) {
                Files.createDirectories(destino.getParent());
            }
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(doc), new StreamResult(destino.toFile()));
        } catch (TransformerException e) {
            throw new IOException("No se pudo guardar " + destino, e);
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

    /**
     * Crea un XML nuevo que contiene solo los elementos indicados (con su zona),
     * no una copia del fichero completo.
     */
    public void exportarAfectados(Path destino, List<String> ids) throws IOException {
        Document origen = cargar();
        try {
            Document nuevo = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element raiz = nuevo.createElement("afectados");
            nuevo.appendChild(raiz);

            NodeList zonas = origen.getElementsByTagName("zona");
            for (int i = 0; i < zonas.getLength(); i++) {
                Element zona = (Element) zonas.item(i);
                Element copiaZona = null;
                NodeList elementos = zona.getElementsByTagName("elemento");
                for (int j = 0; j < elementos.getLength(); j++) {
                    Element e = (Element) elementos.item(j);
                    if (ids.contains(e.getAttribute("id"))) {
                        if (copiaZona == null) {
                            copiaZona = nuevo.createElement("zona");
                            copiaZona.setAttribute("id", zona.getAttribute("id"));
                            copiaZona.setAttribute("nombre", zona.getAttribute("nombre"));
                            raiz.appendChild(copiaZona);
                        }
                        copiaZona.appendChild(nuevo.importNode(e, true));
                    }
                }
            }
            guardarEn(nuevo, destino);
        } catch (ParserConfigurationException e) {
            throw new IOException("No se pudo crear el XML de salida", e);
        }
    }
}
