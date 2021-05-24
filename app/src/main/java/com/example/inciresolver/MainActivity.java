package com.example.inciresolver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

import com.example.inciresolver.db.DBProvider;
import com.example.inciresolver.db.Ingredient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBProvider mydb = new DBProvider(this);
//        mydb.deleteContact(2);
//        mydb.deleteContact(1);
//        mydb.insertIngredient("AQUA", "HUMEKTANT, ROZPUSZCZLNIK", "1");
//        mydb.insertIngredient("GLYCERIN", "HUMEKTANT", "1");
//        mydb.insertIngredient("BUTYROSPERMUM PARKII BUTTER", "EMOLIENT", "1");
//        mydb.insertIngredient("CETYL PALMITATE", "EMOLIENT, NIE-EKO", "1");
//        mydb.insertIngredient("OLUS OIL", "EMOLIENT", "1");
//        mydb.insertIngredient("CETYL ALKOHOL", "EMOLIENT", "1");
//        mydb.insertIngredient("ISOPROPYL PALMITATE", "EMOLIENT", "1");
//        mydb.insertIngredient("ALOE BARBADENSIS LEAF JUICE POWDER", "KONSYSTENCJOTWORCZE", "1");
//        mydb.insertIngredient("DIMETHICONE", "EMOLIENT", "1");
//        mydb.insertIngredient("SODIUM POLYACRYLATE", "KONSYSTENCJOTWORCZE", "0");
//        mydb.insertIngredient("PHENOXYETHANOL", "KONSERWANT", "0");
//        mydb.insertIngredient("ETHYLHEXYLGLYCERIN", "KONSERWANT", "0");
//        mydb.insertIngredient("PARFUM", "ZAPACH", "0");
        ArrayList array_list = mydb.getAllCotacts();
        Ingredient res = mydb.getIngredientByName("AQUA");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setSearchableInfo(
        //        searchManager.getSearchableInfo(getComponentName()));


        //SearchView search = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search);
        // Associate searchable configuration with the SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, ShowIngredient.class)));
        searchView.setQueryHint("INCI name ex. AQUA");
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.search:
//                Intent intent = new Intent(getBaseContext(), ShowIngredient.class);
//                startActivity(intent);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void click(View view) {
        switch(view.getId()){
            case R.id.imageButtonGallery:
                break;//akcja
//            case R.id.imageButtonSearch:
//                Intent intentSearch = new Intent(MainActivity.this, ShowIngredient.class);
//                startActivity(intentSearch);
//                break;//akcja
        }


    }
}