package com.frede.buscaminas;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;

public class BombButton extends AppCompatButton{
    private boolean flagged=false;//If a flag as been placed on this Button
    private boolean visited=false; //If the Button has been visited
    private int row; // The row index of the button in the grid
    private int column; // The column index of the button in the grid
    private int bombs; // The number of bombs around the button. -1 if the Button is a Bomb
    private int iconID;
    private Button btn; // The reference to the Button itself
    private ImageView iv;

    public BombButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        // Initialize variables if needed
        bombs = 0;
        btn = this; // Assign the Button instance to btn
        iv=new ImageView(getContext());
        iv.setVisibility(GONE);
    }

    // Getter and setter for bombs
    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }

    // Getter for btn
    public Button getBtn() {
        return btn;
    }

    public void setIconID(int iconID) {
        this.iconID=iconID;
    }

    public int getIconID(){
        return iconID;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged() {
        flagged = !flagged;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    public void setIv(ImageView iv) {
        this.iv = iv;
    }

    public ImageView getIv() {
        return iv;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setIcon(){
        iv.setImageDrawable(null);//Clear previous image
        if(flagged){
            iv.setImageResource(R.drawable.flag);
        }
        else {
            iv.setImageResource(iconID);
        }
    }

    public void hideButton(){
        this.setVisibility(GONE);
    }

    public void showButton(){
        this.setVisibility(VISIBLE);
    }

    public void hideIcon(){
        iv.setVisibility(GONE);
    }

    public void showIcon(){
        iv.setVisibility(VISIBLE);
    }

}