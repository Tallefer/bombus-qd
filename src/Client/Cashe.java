/*
 * Cashe.java
 *
 * Created on 25 ���� 2009 �., 18:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Client;

/**
 *
 * @author aqent
 */
public class Cashe {
    
    private Cashe() { }
    public static Cashe get(){
        if (cashe==null) 
            cashe=new Cashe();
        return cashe;
    }    
    private static Cashe cashe;
}
