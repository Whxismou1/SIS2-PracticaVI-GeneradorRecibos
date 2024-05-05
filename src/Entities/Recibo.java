/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

/**
 *
 * @author moasin
 */
public class Recibo {

    private double consumo;
    private double baseImponible;
    private double iva;
    private double totalRecibo;
    
    public Recibo generarRecibo(Contribuyente actualContri){
        this.consumo = Double.parseDouble(actualContri.getLecturaActual()) - Double.parseDouble(actualContri.getLecturaAnterior());
        return null;
    }
    
    
    
}
