package com.example.inciresolver;

import androidx.appcompat.app.AppCompatActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import com.example.inciresolver.adapters.CustomAdapter;
import com.example.inciresolver.db.DBProvider;
import com.example.inciresolver.db.Ingredient;

import java.util.ArrayList;

public class ShowIngredient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ingredient);

        ArrayList<Ingredient> ingredientsList = handleIntent(getIntent());

        if(ingredientsList.isEmpty()){
//            PopUpClass popUpClass = new PopUpClass();
//            popUpClass.showPopupWindow(findViewById(R.id.main_page));
            Intent intentMain = new Intent(ShowIngredient.this, MainActivity.class);
            intentMain.setAction(Intent.ACTION_SEND);
            intentMain.putExtra(Intent.EXTRA_TEXT, "textMessage");
            intentMain.setType("text/plain");
                startActivity(intentMain);
            finish();
            //NavUtils.navigateUpFromSameTask(this);
        } else{
            final ListView list = findViewById(R.id.list);
            CustomAdapter customAdapter = new CustomAdapter(this, ingredientsList);
            list.setAdapter(customAdapter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private ArrayList<Ingredient> handleIntent(Intent intent) {
        ArrayList<Ingredient> ingredientsList = new ArrayList<>();
        DBProvider mydb= new DBProvider(ShowIngredient.this);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            ingredientsList.add(mydb.getIngredientByName(query));
        } else if(Intent.ACTION_VIEW.equals(intent.getAction())){
            String[] ingredients = intent.getStringArrayExtra("strings");
            for(String name: ingredients){
                Ingredient ingredient = mydb.getIngredientByName(name);
                if(ingredient.isEmpty()) {
                    ingredient.setName(name.toUpperCase());
                    ingredient.setDescription("NOT FOUND");
                }
                ingredientsList.add(ingredient);
            }
        }
        return ingredientsList;
    }
}