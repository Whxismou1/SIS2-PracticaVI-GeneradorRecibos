package mainpkg;

import Controllers.CCCController;
import Controllers.ExcelManager;
import Controllers.GeneradorRecibosXML;
import Controllers.IBANController;
import Controllers.NIFController;
import Entities.Contribuyente;
import Entities.Recibo;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Coordinator {

    public void init(String userInput) {
        ExcelManager excMang = new ExcelManager();
        CCCController cccController = new CCCController();
        IBANController ibanCont = new IBANController();
        NIFController nifControler = new NIFController();
        List<Contribuyente> malNie = new ArrayList<>();
        List<Contribuyente> malCCC = new ArrayList<>();
        List<Contribuyente> nifNiesApariciones = new ArrayList<>();

        StringBuilder sb = new StringBuilder(userInput);

        int numTrimestre = Integer.parseInt(sb.substring(0, 1));;

        int año = Integer.parseInt(sb.substring(3, sb.length()));

        List<Contribuyente> listaContribuyente = excMang.readEcel();

        for (int i = 0; i < listaContribuyente.size(); i++) {
            Contribuyente contribuyenteActual = listaContribuyente.get(i);
            System.out.println(contribuyenteActual.toString());
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
        new GeneradorRecibosXML().generateRecibeXML(listaContribuyente);
//        new GeneradorRecibosXML().generateRecibeXML(listaRecibosUsuarios);
    }

    private boolean isEmptyContribuyente(Contribuyente actual) {

        if (actual.getNIFNIE() == null && actual.getCCC() == null && actual.getIBAN() == null) {
            return true;
        }

        return false;
    }
}
