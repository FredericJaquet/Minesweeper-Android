package com.frede.buscaminas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CharacterSpinnerAdapter extends ArrayAdapter<Character> {

    private Context context;
    private List<Character> characters;

    public CharacterSpinnerAdapter(Context context, List<Character> characters) {
        super(context, 0, characters);
        this.context = context;
        this.characters = characters;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item_character, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.imageViewCharacter);
        TextView textView = convertView.findViewById(R.id.textViewCharacterName);

        Character currentCharacter = characters.get(position);
        imageView.setImageResource(currentCharacter.getIconResource());
        textView.setText(currentCharacter.getName());

        return convertView;
    }
}