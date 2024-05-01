package mainpkg;

import Controllers.CCCController;
import Controllers.ExcelManager;
import Controllers.IBANController;
import Controllers.NIFController;
import Entities.Contribuyente;
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
            boolean isSpanish = nifControler.isSpanish(nifActual);
            System.out.println(nifActual);

            if (nifActual == null) {
                continue;
            } else {
                if (nifControler.isNifValid(nifActual, isSpanish, contribuyenteActual)) {
                    String actualCCC = contribuyenteActual.getCCC();
                    cccController.checkCCC(actualCCC, contribuyenteActual);

                    ibanCont.checkIban(contribuyenteActual);
                }
            }

        }

        for (int i = 0; i < listaContribuyente.size(); i++) {
            Contribuyente contribuyenteActual = listaContribuyente.get(i);
            System.out.println(contribuyenteActual.toString());
        }
    }

    private boolean isEmptyContribuyente(Contribuyente actual) {

        if (actual.getNIFNIE() == null && actual.getCCC() == null && actual.getIBAN() == null) {
            return true;
        }

        return false;
    }
}
