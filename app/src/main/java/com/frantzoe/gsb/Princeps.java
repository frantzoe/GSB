package com.frantzoe.gsb;

/**
 * Created by Lincoln on 4/6/2015.
 */
public class Princeps extends Medicament {

    private String leNomCommercial;

    public Princeps(DCI uneDCI, String unNomCommercial)
    {
        super(uneDCI);
        this.leNomCommercial = unNomCommercial;
    }

    public String getNomCommercial()
    {
        return this.leNomCommercial;
    }

    public String fiche()
    {
        String resultat;
        resultat = "\n" + this.leNomCommercial+ " est un princeps" + "\n\n";
        resultat += super.fiche();
        return resultat;
    }
}
