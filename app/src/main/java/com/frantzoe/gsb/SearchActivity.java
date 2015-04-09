package com.frantzoe.gsb;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class SearchActivity extends ActionBarActivity implements SearchView.OnQueryTextListener {

    ListView listProducts;
    SimpleCursorAdapter adapter;
    MyDataBase myDB;
    SearchView searchView;
    Princeps princeps = null;
    Generique generique = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        myDB = new MyDataBase(this, null, null, 1);
        listProducts = (ListView) findViewById(R.id.listProducts);
        //Utilisation du SimpleCursorAdapter qui traite directement un cursor, comme ca on a pas a passer par un ArrayList pour remplir la listView
        //Le SimpleCursorAdapter est aussi indispensable quand il s'agit de filtrer les resultats d'un cursor
        adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, myDB.allMedicaments(),
                new String[]{MyDataBase.COL_PRINCEPS_NOM, MyDataBase.COL_PRINCEPS_ID},
                new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        //Filtrage du SimpleCursorAdapter
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                return myDB.getLesValeursFiltrees(constraint.toString());
            }
        });
        listProducts.setAdapter(adapter);
        listProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Recuperation de l'element selectionne dans la liste
                SQLiteCursor sqLiteCursor = (SQLiteCursor) parent.getItemAtPosition(position);
                //Recuperation du layout qu'on va affecter au dialog
                View layout = getLayoutInflater().inflate(R.layout.medetails, null);
                //Recuperation de la chaine correspondant a l'element selectionne dans la liste
                String titre = sqLiteCursor.getString(sqLiteCursor.getColumnIndex(MyDataBase.COL_PRINCEPS_NOM));
                //Si la valeur selectionnee n'est pas un princeps, alors c'est forcement un generique alors on fait appel au try catch
                try {
                    princeps = myDB.getPrinceps(titre);
                    String denomDCI = princeps.getDCI().getDenomination();
                    String classesysteme = myDB.getClasseDCI(denomDCI);
                    ((TextView) layout.findViewById(R.id.textDetails)).setText(princeps.fiche() + "\n"
                            + "Classe : " + classesysteme.split(",")[0] + "\n\n"
                            + "Systeme : " + classesysteme.split(",")[1] + "\n\n"
                            + "Generique : " + existeUnGenerique(denomDCI));
                } catch (Exception e) {
                    generique = myDB.getGenerique(titre);
                    String classesysteme = myDB.getClasseDCI(titre);
                    ((TextView) layout.findViewById(R.id.textDetails)).setText(generique.fiche() + "\n"
                            + "Classe : " + classesysteme.split(",")[0] + "\n\n"
                            + "Systeme : " + classesysteme.split(",")[1] + "\n\n"
                            + "Princeps : " + myDB.getPrincepsGenerique(titre));
                }
                //Affichage du dialog avec les informations sur le generique ou le princeps
                new AlertDialog.Builder(SearchActivity.this)
                        .setTitle(titre)
                        .setView(layout)
                        .setPositiveButton(getString(R.string.dialog_fermer_text), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                            }
                        })
                        .create().show();
            }
        });
        //Definition a afficher lorsque la liste est vide, c'est a dire quand la chaine saisie dans la barre de recherche ne correspond a aucun medicament
        listProducts.setEmptyView(findViewById(R.id.textViewEmpty));
        //Cacher la liste au lancement de l'activite de recherche
        listProducts.setVisibility(View.INVISIBLE);
    }

    public String existeUnGenerique(String denomination) {
        String result = "Non";
        if (myDB.getGenerique(denomination).getAnnee() != 0) {
            result = "Oui";
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    //Methode qui s'execute lors de la soumission d'une requete dans la barre de recherche
    @Override
    public boolean onQueryTextSubmit(String query) {
        //Inutile dans ce cas car le filtre s'execute au fur et a mesure qu'un caratere est saisi donc on retourne false
        return false;
    }

    //Methode qui s'active a chaque sasie d'un nouveau caractere dans la barre de recherche
    @Override
    public boolean onQueryTextChange(String newText) {
        if (searchView.getQuery().length() < 1) {
            listProducts.setVisibility(View.INVISIBLE);
        } else { listProducts.setVisibility(View.INVISIBLE); }
        //Appel de la methode de filtrage
        this.adapter.getFilter().filter(newText);
        this.adapter.notifyDataSetChanged();
        return true;
    }
}
