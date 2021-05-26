package com.example.inciresolver.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.inciresolver.db.Ingredient;
//import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import com.example.inciresolver.R;

public class CustomAdapter implements ListAdapter {
    private static final int GRAY = R.color.grey_star;
    private static final int GREEN = R.color.green_star;
    private static final int RED = R.color.red_star;

    ArrayList<Ingredient> arrayList;
    Context context;

    //ColorFilter filter = context.getResources().getColor(R.color.grey_star);

    public CustomAdapter(Context context, ArrayList<Ingredient> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Ingredient ingredient = arrayList.get(position);
        if(convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_row, null);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                }
//            });
            TextView tittle = convertView.findViewById(R.id.title);
            tittle.setText(ingredient.getName());
            TextView description = convertView.findViewById(R.id.description);
            description.setText(ingredient.getDescription());
            ImageView image_star = convertView.findViewById(R.id.list_image);
            image_star.setColorFilter(context.getResources().getColor(GRAY));
            if(ingredient.getQuality().equals("1")) {
                image_star.setColorFilter(context.getResources().getColor(GREEN));
                description.setText(ingredient.getDescription() + "\n\nQuality: HIGH");
            } else if(!ingredient.getDescription().equals("NOT FOUND")){
                image_star.setColorFilter(context.getResources().getColor(RED));
                description.setText(ingredient.getDescription() + "\n\nQuality: POOR");
            }
            if(ingredient.getName().contains("EXTRACT") || ingredient.getName().contains("OIL")) {
                image_star.setColorFilter(context.getResources().getColor(GREEN));
                description.setText(ingredient.getDescription() + "\n\nQuality: HIGH");
            }
            if(ingredient.getName().contains("WATER") || ingredient.getName().contains("BUTTER")) {
                image_star.setColorFilter(context.getResources().getColor(GREEN));
                description.setText(ingredient.getDescription() + "\n\nQuality: HIGH");
            }
        }
        return convertView;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        return arrayList.size();
    }
    @Override
    public boolean isEmpty() {
        return false;
    }
}