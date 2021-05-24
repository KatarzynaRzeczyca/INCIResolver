package com.example.inciresolver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.example.inciresolver.adapters.CustomAdapter;
import com.example.inciresolver.db.Ingredient;

import java.util.ArrayList;

public class ShowIngredient extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ingredient);

        handleIntent(getIntent());

        final ListView list = findViewById(R.id.list);
        ArrayList<Ingredient> arrayList = new ArrayList<Ingredient>();
        arrayList.add(new Ingredient("GLYCERIN", "humektant", "BEST!"));
        arrayList.add(new Ingredient("AQUA", "rozpuszczalnik", "BEST!"));
        CustomAdapter customAdapter = new CustomAdapter(this, arrayList);
        list.setAdapter(customAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
//            Intent intentSearch = new Intent(ShowIngredient.this, ShowIngredient.class);
//            startActivity(intentSearch);
            //use the query to search your data somehow
        }
    }
}