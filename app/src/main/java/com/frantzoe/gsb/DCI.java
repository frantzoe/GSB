package com.frantzoe.gsb;

/**
 * Created by Lincoln on 4/6/2015.
 */

public class DCI {

    private String laDenominationCommuneInt;

    public DCI (String uneDCI)
    {
        this.laDenominationCommuneInt = uneDCI;
    }

    public String getDenomination()
    {
        return this.laDenominationCommuneInt;
    }

    public static boolean compare(DCI uneDCI, DCI uneAutreDCI)
    {
        return (uneDCI.laDenominationCommuneInt.equals(uneAutreDCI.laDenominationCommuneInt));
    }
}
