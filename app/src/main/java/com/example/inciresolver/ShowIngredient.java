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

        Ingredient ingredient = handleIntent(getIntent());

        if(ingredient.isEmpty()){
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
            ArrayList<Ingredient> arrayList = new ArrayList<>();
            arrayList.add(new Ingredient("GLYCERIN", "humektant", "BEST!"));
            arrayList.add(ingredient);
            CustomAdapter customAdapter = new CustomAdapter(this, arrayList);
            list.setAdapter(customAdapter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private Ingredient handleIntent(Intent intent) {
        Ingredient ingredient = new Ingredient();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            DBProvider mydb= new DBProvider(ShowIngredient.this);
            ingredient = mydb.getIngredientByName(query.toUpperCase());
            //use the query to search your data somehow
        }
        return ingredient;
    }
}