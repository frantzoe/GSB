package com.frantzoe.gsb;

/**
 * Created by Lincoln on 4/6/2015.
 */

public class Generique extends Medicament {

    private int annee;

    public Generique(DCI uneDCI, int uneAnnee)
    {
        super(uneDCI);
        this.annee = uneAnnee;
    }

    public int getAnnee()
    {
        return this.annee;
    }

    public String fiche()
    {
        String resultat = "";
        if (annee != 0) {
            resultat += "\n" + super.getDCI().getDenomination() + " est un generique" + "\n\n" + "Année : " + annee + "\n";
        } else {
            resultat += "\n" + super.getDCI().getDenomination() + " est une DCI" + "\n\n" + "Année : - - - -" + "\n";
        }
        return resultat;
    }
}
