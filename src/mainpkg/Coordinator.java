package mainpkg;

import Controllers.CCCController;
import Controllers.ExcelManager;
import Controllers.GeneradorRecibosXML;
import Controllers.IBANController;
import Controllers.NIFController;
import Entities.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Coordinator {

    public void init() {
        ExcelManager excMang = new ExcelManager();
        CCCController cccController = new CCCController();
        IBANController ibanCont = new IBANController();
        NIFController nifControler = new NIFController();
        List<Contribuyente> malNie = new ArrayList<>();
        List<Contribuyente> malCCC = new ArrayList<>();
        List<Contribuyente> nifNiesApariciones = new ArrayList<>();

        

        List<Contribuyente> listaContribuyente = excMang.readExcelContribuyente();
        List<Contribuyente> listaContribuyenteFiltrado = new ArrayList<>();
        List<Ordenanza> listaOrdenanza = excMang.readExcelOrdenanza();

        for (int i = 0; i < listaContribuyente.size(); i++) {
            Contribuyente contribuyenteActual = listaContribuyente.get(i);
            //System.out.println(contribuyenteActual.toString());
            if (isEmptyContribuyente(contribuyenteActual)) {
                continue;
            }

            String nifActual = contribuyenteActual.getNIFNIE();
            if (nifActual == null) {
                continue;
            } else {
                boolean isSpanish = nifControler.isSpanish(nifActual);
                if (nifControler.isNifValid(nifActual, isSpanish, contribuyenteActual)) {
                    String actualCCC = contribuyenteActual.getCCC();
                    cccController.checkCCC(actualCCC, contribuyenteActual);

                    ibanCont.checkIban(contribuyenteActual);
                    if (!nifControler.getIsSaneado()) {
                        listaContribuyenteFiltrado.add(contribuyenteActual);
                    }
                }
            }
        }
        
        List<Recibo> listaRecibosUsuarios = new ArrayList<>();
        for (int i = 0; i < listaContribuyente.size(); i++) {
            Contribuyente actualContri = listaContribuyente.get(i);
            actualContri.getExencion();
            actualContri.getId();
            actualContri.getNombre();
            actualContri.getApellido1();
            actualContri.getApellido2();
            actualContri.getNIFNIE();
            actualContri.getIBAN();
            actualContri.getLecturaActual();
            actualContri.getLecturaAnterior();
            actualContri.getFechaAlta();
            
            actualContri.getFechaBaja();
            
            
            Recibo reciboActual = new Recibo();
            listaRecibosUsuarios.add(reciboActual);
        }
//        new GeneradorRecibosXML().generateRecibeXML(listaRecibosUsuarios);
        for (int i = 0; i < listaOrdenanza.size(); i++) {

            Ordenanza actualOrdenanza = listaOrdenanza.get(i);
            actualOrdenanza.getPueblo();
            actualOrdenanza.getTipoCalculo();
            actualOrdenanza.getId();
            actualOrdenanza.getConcepto();
            actualOrdenanza.getSubconcepto();
            actualOrdenanza.getDescripcion();
            actualOrdenanza.getAcumulable();
            actualOrdenanza.getPrecioFijo();
            actualOrdenanza.getM3incluidos();
            actualOrdenanza.getPreciom3();
            actualOrdenanza.getPorcentajeSobreOtroConcepto();
            actualOrdenanza.getSobreQueConcepto();
            actualOrdenanza.getIVA();
        }
        Scanner sc = new Scanner(System.in);
        
        String trimestre = sc.nextLine();
        new GeneradorRecibosXML().generateRecibeXML(listaContribuyenteFiltrado, listaOrdenanza, trimestre);
    }

    private boolean isEmptyContribuyente(Contribuyente actual) {

        if (actual.getNIFNIE() == null && actual.getCCC() == null && actual.getIBAN() == null) {
            return true;
        }

        return false;
    }
}
