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
