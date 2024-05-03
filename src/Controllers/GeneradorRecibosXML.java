/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.Contribuyente;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


/**
 *
 * @author moasin
 */
public class GeneradorRecibosXML {
    private static String path = "src/resources/recibos.xml";
    public void generateRecibeXML(List<Contribuyente> listaContribuyentes){
         try {
            Element contribuyentes = new Element("Recibos");
            Document doc = new Document(contribuyentes);

            for (int i = 0; i < listaContribuyentes.size(); i++) {
                Contribuyente contr = listaContribuyentes.get(i);
                Element contribuyente = new Element("Recibo");
                Attribute attr = new Attribute("id", contr.getId().toString());
                contribuyente.setAttribute(attr);
//                <Exencion>N</Exencion>
                Element exencion = new Element("Exencion");
            //    <idFilaExcel>2</idFilaExcel>
                Element idFilaExcel = new Element("idFilaExcel");
            //    <nombre>Juan</nombre>
                Element nombre = new Element("nombre");
            //    <primerApellido>Martinez</primerApellido>
                Element apellido1 = new Element("primerApellido");
            //    <segundoApellido>Dominguez</segundoApellido>
                Element apellido2 = new Element("segundoApellido");
            //    <NIF>09632539R</NIF>
                Element nif = new Element("NIF");
            //    <IBAN>DK7331645124473461205164</IBAN>
                Element iban = new Element("IBAN");
            //    <lecturaActual>106</lecturaActual>
                Element lecturaActual = new Element("lecturaActual");
            //    <lecturaAnterior>21</lecturaAnterior>
                Element lecturaAnterior = new Element("lecturaAnterior");
            //    <consumo>85</consumo>
                Element consumo = new Element("consumo");
            
            //    <baseImponibleRecibo>34.5</baseImponibleRecibo>
                Element baseImponibleRecibo = new Element("baseImponibleRecibo");
            //    <ivaRecibo>7.245</ivaRecibo>
                Element ivaRecibo = new Element("ivaRecibo");
            //    <totalRecibo>41.745</totalRecibo>
                Element totalRecibo = new Element("totalRecibo");

                
                exencion.setText(contr.getExencion());
                idFilaExcel.setText(String.valueOf(contr.getId()));
                nombre.setText(contr.getNombre());
                apellido1.setText(contr.getApellido1());
                apellido2.setText(contr.getApellido2());
                nif.setText(contr.getNIFNIE());
                iban.setText(contr.getIBAN());
                lecturaActual.setText(contr.getLecturaActual());
                lecturaAnterior.setText(contr.getLecturaAnterior());

                contribuyente.addContent(exencion);
                contribuyente.addContent(idFilaExcel);
                contribuyente.addContent(nombre);
                contribuyente.addContent(apellido1);
                contribuyente.addContent(apellido2);
                contribuyente.addContent(iban);
                contribuyente.addContent(lecturaActual);
                contribuyente.addContent(lecturaAnterior);
                contribuyente.addContent(consumo);
                contribuyente.addContent(baseImponibleRecibo);
                contribuyente.addContent(ivaRecibo);
                contribuyente.addContent(totalRecibo);                
                
                contribuyentes.addContent(contribuyente);
            }

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            xml.output(doc, new FileWriter(path));
        } catch (IOException ex) {
            Logger.getLogger(GeneradorRecibosXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
