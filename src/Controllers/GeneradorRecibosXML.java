/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Entities.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
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
    public void generateRecibeXML(List<Contribuyente> listaContribuyentes, List<Ordenanza> listaOrdenanza, String userInput){
         try {
            ExcelManager excMang = new ExcelManager();
            Element contribuyentes = new Element("Recibos");
            Document doc = new Document(contribuyentes);
            
            int numTrimestre = Integer.parseInt(userInput.substring(0, 1));
            int año = Integer.parseInt(userInput.substring(2).trim());

            // Calcular la fecha de inicio y fin del trimestre
            LocalDate fechaInicioTrimestre = calcularInicioTrimestre(numTrimestre, año);
            LocalDate fechaFinTrimestre = calcularFinTrimestre(numTrimestre, año);

            // Formateador para la fecha de alta del contribuyente
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            // Filtrar los contribuyentes por la fecha de alta
            List<Contribuyente> contribuyentesFiltrados = filtrarContribuyentesPorFecha(listaContribuyentes, fechaFinTrimestre, formatter);
            
 
            float baseImponible = 0;
            float iva = 0;
            float recibos = 0;


            for (Contribuyente contr : contribuyentesFiltrados) {
                LocalDate fechaAlta = LocalDate.parse(contr.getFechaAlta(), formatter);
                    if (!excMang.isEmptyContribuyente(contr)) {
                        Element contribuyente = new Element("Recibo");
                        Attribute attr = new Attribute("id", contr.getId().toString());
                        contribuyente.setAttribute(attr);
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

                        float cons = 0;
                        if (contr.getLecturaActual() != null && contr.getLecturaAnterior() != null) {
                            cons = Float.parseFloat(contr.getLecturaActual()) - Float.parseFloat(contr.getLecturaAnterior());
                            consumo.setText(String.valueOf(cons));
                        }
                        float base = 0;
                        float IVA = 0;
                        float total = 0;
                        if (contr.getConceptosACobrar() != null) {
                            String[] conceptos = contr.getConceptosACobrar().split(" ");
                            Arrays.sort(conceptos);
                            for (int j = 0; j < conceptos.length; j++) {
                                float[] resultado = conceptos(conceptos[j], cons, base, IVA, listaOrdenanza);
                                base = resultado[0];
                                //System.out.println("Concepto actual numero: " + conceptos[j]);
                                //System.out.println("Base del " + contr.getId() + "es: " + base);
                                IVA = resultado[1];
                                //System.out.println("IVA del " + contr.getId() + "es: " + IVA);
                                total = base + IVA;
                                //System.out.println("Total del " + contr.getId() + "es: " + total);
                            }
                        } 
                        baseImponible += base;
                        iva += IVA;
                        recibos += total;

                        baseImponibleRecibo.setText(String.valueOf(base));
                        ivaRecibo.setText(String.valueOf(IVA));
                        totalRecibo.setText(String.valueOf(total));


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
                
            }
            
            Attribute fechaPadron = new Attribute("fechaPadron", userInput);
            Attribute totalBaseImponible = new Attribute("totalBaseImponible", String.valueOf(baseImponible));
            Attribute totalIVA = new Attribute("totalIVA", String.valueOf(iva));
            Attribute totalRecibos = new Attribute("totalRecibos", String.valueOf(recibos));

            contribuyentes.setAttribute(fechaPadron);
            contribuyentes.setAttribute(totalBaseImponible);
            contribuyentes.setAttribute(totalIVA);
            contribuyentes.setAttribute(totalRecibos);

            XMLOutputter xml = new XMLOutputter();
            xml.setFormat(Format.getPrettyFormat());
            xml.output(doc, new FileWriter(path));
        } catch (IOException ex) {
            Logger.getLogger(GeneradorRecibosXML.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private LocalDate calcularInicioTrimestre(int numTrimestre, int año) {
        switch (numTrimestre) {
            case 1:
                return LocalDate.of(año, 1, 1);
            case 2:
                return LocalDate.of(año, 4, 1);
            case 3:
                return LocalDate.of(año, 7, 1);
            case 4:
                return LocalDate.of(año, 10, 1);
            default:
                throw new IllegalArgumentException("Número de trimestre inválido: " + numTrimestre);
        }
    }

    private LocalDate calcularFinTrimestre(int numTrimestre, int año) {
        switch (numTrimestre) {
            case 1:
                return LocalDate.of(año, 3, 31);
            case 2:
                return LocalDate.of(año, 6, 30);
            case 3:
                return LocalDate.of(año, 9, 30);
            case 4:
                return LocalDate.of(año, 12, 31);
            default:
                throw new IllegalArgumentException("Número de trimestre inválido: " + numTrimestre);
        }
    }

    private List<Contribuyente> filtrarContribuyentesPorFecha(List<Contribuyente> contribuyentes, LocalDate fechaFinTrimestre, DateTimeFormatter formatter) {
        List<Contribuyente> contribuyentesFiltrados = new ArrayList<>();
        ExcelManager excMang = new ExcelManager();
        for (Contribuyente contribuyente : contribuyentes) {
            if(!excMang.isEmptyContribuyente(contribuyente)) {
                String fechaAltaString = contribuyente.getFechaAlta();
                try {
                    LocalDate fechaAlta = LocalDate.parse(fechaAltaString, formatter);
                    if (!fechaAlta.isAfter(fechaFinTrimestre)) {
                        contribuyentesFiltrados.add(contribuyente);
                    }
                } catch (DateTimeParseException e) {
                    System.err.println("Fecha de alta inválida para el contribuyente " + contribuyente.getId() + ": " + fechaAltaString);
                }
            }
        }
        return contribuyentesFiltrados;
    }
    
    public float[] conceptos(String concepto, float cons, float base, float IVA, List<Ordenanza> listaOrdenanza) {
        float[] resultado = new float[2];
        /*
        for (int i = 0; i < listaOrdenanza.size(); i++) {
            System.out.println(listaOrdenanza.get(i).toString());
        }
        */
        switch (concepto) {
            case "1":
                float consTemp1 = cons;
                base += Float.parseFloat(listaOrdenanza.get(0).getPrecioFijo());
                consTemp1 -= Float.parseFloat(listaOrdenanza.get(0).getM3incluidos());
                if (consTemp1 > 0) {
                    if (consTemp1 <= Float.parseFloat(listaOrdenanza.get(1).getM3incluidos())) {
                        base += Float.parseFloat(listaOrdenanza.get(1).getPreciom3()) * consTemp1;
                        consTemp1 = 0;
                    } else {
                        base += Float.parseFloat(listaOrdenanza.get(1).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(1).getM3incluidos());
                        consTemp1 -= Float.parseFloat(listaOrdenanza.get(1).getM3incluidos());
                    }
                    if (consTemp1 <= Float.parseFloat(listaOrdenanza.get(2).getM3incluidos())) {
                        base += Float.parseFloat(listaOrdenanza.get(2).getPreciom3()) * consTemp1;
                        consTemp1 = 0;
                    } else {
                        base += Float.parseFloat(listaOrdenanza.get(2).getPreciom3()) * Float.parseFloat(listaOrdenanza.get(2).getM3incluidos());
                        consTemp1 -= Float.parseFloat(listaOrdenanza.get(2).getM3incluidos());
                    }
                    if(consTemp1 > 0) {
                        base += Float.parseFloat(listaOrdenanza.get(3).getPreciom3()) * consTemp1;
                    }
                }
                IVA += Float.parseFloat(listaOrdenanza.get(0).getIVA())/100 * base;
                resultado[0] = base;
                resultado[1] = IVA;
                return resultado;
            case "2":
                resultado[0] = base + Float.parseFloat(listaOrdenanza.get(4).getPrecioFijo());
                resultado[1] = IVA;
                return resultado;
            case "3":
                int conceptoTemp = (int) Float.parseFloat(listaOrdenanza.get(5).getSobreQueConcepto()); 
                float[] resultConcepto = conceptos(String.valueOf(conceptoTemp), cons, 0, 0, listaOrdenanza);
                //System.out.println(Float.parseFloat(listaOrdenanza.get(5).getPorcentajeSobreOtroConcepto()) / 100);
                resultado[0] = (float) (base + (resultConcepto[0] * Float.parseFloat(listaOrdenanza.get(5).getPorcentajeSobreOtroConcepto()) / 100));
                resultado[1] = IVA;
                return resultado;
            case "4":
                float consTemp4 = cons;
                base += Float.parseFloat(listaOrdenanza.get(6).getPrecioFijo());
                consTemp4 -= Float.parseFloat(listaOrdenanza.get(6).getM3incluidos());
                if (consTemp4 > 0) {
                    base += Float.parseFloat(listaOrdenanza.get(7).getPreciom3()) * consTemp4;
                }
                IVA += Float.parseFloat(listaOrdenanza.get(6).getIVA())/100 * base;
                resultado[0] = base;
                resultado[1] = IVA;
                return resultado;
            case "5":
                resultado[0] = base + Float.parseFloat(listaOrdenanza.get(8).getPrecioFijo());
                resultado[1] = IVA;
                return resultado;
            case "6":
                float consTemp6 = cons;
                base += Float.parseFloat(listaOrdenanza.get(9).getPrecioFijo());
                if (consTemp6 <= Float.parseFloat(listaOrdenanza.get(9).getM3incluidos())) {
                    IVA += Float.parseFloat(listaOrdenanza.get(9).getIVA())/100 * base;
                } else {
                    base += Float.parseFloat(listaOrdenanza.get(9).getPreciom3()) * consTemp6;
                    IVA += Float.parseFloat(listaOrdenanza.get(9).getIVA())/100 * base;
                }
                resultado[0] = base;
                resultado[1] = IVA;
                return resultado;
            default:
                resultado[0] = base;
                resultado[1] = IVA;
                return resultado;
        }
    }

 
}
