package mainpkg;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        
        Scanner sc = new Scanner(System.in);
        
        String trimestre = sc.nextLine();
        
        
        System.out.println(trimestre);
        
        new Coordinator().init(trimestre);
    }
}