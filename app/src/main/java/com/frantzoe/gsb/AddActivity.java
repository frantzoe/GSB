package com.frantzoe.gsb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;


public class AddActivity extends ActionBarActivity {

    Spinner spinner;
    ArrayAdapter adapter;
    MyDataBase myDB;
    Button buttonClasse;
    Button buttonDCI;
    EditText editTextName;
    EditText editTextAnnee;
    String nomClasse = null;
    String nomDCI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        myDB = new MyDataBase(this);
        buttonClasse = (Button) findViewById(R.id.buttonClasse);
        buttonDCI = (Button) findViewById(R.id.buttonDCI);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAnnee = (EditText) findViewById(R.id.editTextAnnee);
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Generique", "Princeps"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        buttonDCI.setVisibility(View.GONE);
                        editTextAnnee.setVisibility(View.VISIBLE);
                        buttonClasse.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        editTextAnnee.setVisibility(View.GONE);
                        buttonClasse.setVisibility(View.GONE);
                        buttonDCI.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //
            }
        });
    }

    //Cette methode est directement affectee au bouton ajouter qui n'est pas declare dans cette activite car
    //on ne s'en servira qu'une seule fois. L'affectation s'est faite dans le layout activity_add
    public void addMedicament(View view) {

        switch (spinner.getSelectedItemPosition()) {
            case 0:
                if (nomClasse != null) {
                    int annee = 0;
                    if (editTextAnnee.getText().length() != 0){
                        annee = Integer.parseInt(editTextAnnee.getText().toString());
                    }
                    myDB.addDCI(myDB.getWritableDatabase(), editTextName.getText().toString(), myDB.getIdClasse(nomClasse), annee);
                    Toast.makeText(getBaseContext(), "Generique ajouté!", Toast.LENGTH_SHORT).show();
                    editTextName.setText("");
                    editTextAnnee.setText("");
                    editTextName.requestFocus();
                    buttonClasse.setText(getString(R.string.selection_classe));
                } else {Toast.makeText(getBaseContext(), "Veuillez selectionner une Classe", Toast.LENGTH_SHORT).show();}
                break;
            case 1:
                if (nomDCI != null) {
                    myDB.addPrinceps(myDB.getWritableDatabase(), editTextName.getText().toString(), myDB.getIdDCI(nomDCI));
                    Toast.makeText(getBaseContext(), "Princeps ajouté!", Toast.LENGTH_SHORT).show();
                    editTextName.setText("");
                    editTextName.requestFocus();
                    buttonDCI.setText(R.string.selection_dci);
                } else {Toast.makeText(getBaseContext(), "Veuillez selectionner une DCI", Toast.LENGTH_SHORT).show();}
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    public void selectClasse(View view) {
        view = getLayoutInflater().inflate(R.layout.classesradio, null);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rgClasses);
        //Creation du dialog avec les radios
        new AlertDialog.Builder(AddActivity.this)
                .setTitle("Les Classes")
                .setView(radioGroup)
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .create().show();
        //Observeur de clicks sur un bouton radio
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioId = radioGroup.indexOfChild(radioGroup.findViewById(checkedId));
                nomClasse = (String) ((RadioButton) radioGroup.getChildAt(radioId)).getText();
                buttonClasse.setText(String.valueOf(nomClasse));
            }
        });
    }

    public void selectDCI(View view) {
        view = getLayoutInflater().inflate(R.layout.dciradio, null);
        //Instantiation d'un groupe radio
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rgDCI);
        //Definition des parametres des boutons radios
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT);
        //Creation du dialog avec des boutons radios crees de facon dynamique
        new AlertDialog.Builder(AddActivity.this)
                .setTitle("Les DCI")
                .setView(view)
                .setPositiveButton("Fermer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .create().show();
        //Ici on donne des valeurs au boutons radio a partir d'une ArrayList
        ArrayList<DCI> dcis = myDB.getListDCI();
        for (int i = 0; i < dcis.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(dcis.get(i).getDenomination());
            layoutParams.setMargins(6, 6, 6 ,6);
            radioGroup.addView(radioButton, layoutParams);
        }
        //Observeur de clicks sur un bouton radio
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioId = radioGroup.indexOfChild(radioGroup.findViewById(checkedId));
                nomDCI = (String) ((RadioButton) radioGroup.getChildAt(radioId)).getText();
                buttonDCI.setText(String.valueOf(nomDCI));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }
}
