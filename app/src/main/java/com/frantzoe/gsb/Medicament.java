package com.frantzoe.gsb;

/**
 * Created by Lincoln on 4/6/2015.
 */

public abstract class Medicament {

    private DCI laDCI;

    public Medicament(DCI uneDCI)
    {
        this.laDCI = uneDCI;
    }

    public DCI getDCI()
    {
        return this.laDCI;
    }

    public String fiche()
    {
        String resultat;
        resultat = "DCI : "+ this.getDCI().getDenomination()+ "\n";
        return resultat;
    }
}
