/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.helsinki.cs.nero.data;

/**
 *
 * @author ssinisal
 */
public class Kannykka {
    
    private String htunnus;
    private String phonenumber;
    private String omistaja;
    private String puh_id;

    public Kannykka(String htunnus, String phonenumber, String omistaja, String puh_id) {
        this.htunnus = htunnus;
        this.phonenumber = phonenumber;
        this.omistaja = omistaja;
        this.puh_id = puh_id;
    }

    public String getHtunnus() {
        return htunnus;
    }

    public void setHtunnus(String htunnus) {
        this.htunnus = htunnus;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getOmistaja() {
        return omistaja;
    }

    public void setOmistaja(String omistaja) {
        this.omistaja = omistaja;
    }

    public String getPuh_id() {
        return puh_id;
    }

    public void setPuh_id(String puh_id) {
        this.puh_id = puh_id;
    }
    
    
    
}
