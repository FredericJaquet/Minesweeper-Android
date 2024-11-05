package com.frede.buscaminas;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity
    {
    private final int LEVEL_EASY = 8;
    private final int LEVEL_MEDIUM = 12;
    private final int LEVEL_HARD = 16;
    private final int BOMBS_EASY = 10;
    private final int BOMBS_MEDIUM = 30;
    private final int BOMBS_HARD = 60;

    private int level=LEVEL_EASY;
    private int bombs=BOMBS_EASY;
    private int bombCount=bombs;
    private int selectedCharacterID=R.drawable.bomb;
    private GridLayout gridLayout;
    private TextView bombCountTextView;
    private ImageView characterIconImageView;
    private Chronometer chronometer;
    private ArrayList<BombButton> bombButtons=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        bombCountTextView = findViewById(R.id.bombCountTextView);
        characterIconImageView = findViewById(R.id.characterIconImageView);
        bombCountTextView.setText("Bombs: " + bombCount);
        characterIconImageView.setImageResource(selectedCharacterID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_instructions) {
            showInstructions();
            return true;
        } else if (id == R.id.item_new_game) {
            startNewGame();
            return true;
        } else if (id == R.id.item_config) {
            showConfig();
            return true;
        } else if (id == R.id.item_character) {
            showCharacter();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showInstructions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.st_instructions))
                .setMessage(getString(R.string.st_instructions_text))
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss()); // Using lambda expression

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void startNewGame(){
        //1. Reset bombs count
        bombCount=bombs;
        bombCountTextView.setText("Bombs: " + bombCount);
        bombButtons.clear();

        // 2. Calculate available space
        Resources resources = getResources();
        int screenWidth = resources.getDisplayMetrics().widthPixels;
        int screenHeight = resources.getDisplayMetrics().heightPixels;
        int minSpace = Math.min(screenWidth, screenHeight);

        // 3. Calculate cell size
        int cellSize = minSpace / level;

        // 4. Get the GridLayout container
        this.gridLayout = findViewById(R.id.gridLayout);

        // 5. Clear any existing views in the GridLayout
        this.gridLayout.removeAllViews();

        // 6. Set the number of rows and columns based on 'level'
        this.gridLayout.setColumnCount(level);
        this.gridLayout.setRowCount(level);

        // 7. Create and add BombButtons to the GridLayout
        for (int i = 0; i < level; i++) {
            for (int j = 0; j < level; j++) {
                // Create a new BombButton
                BombButton bombButton = new BombButton(this);

                // Set layout parameters for the BombButton
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = android.widget.GridLayout.spec(i); // Distribute rows evenly
                params.columnSpec = android.widget.GridLayout.spec(j); // Distribute columns evenly
                params.setMargins(0, 0, 0, 0);
                bombButton.setLayoutParams(params);
                bombButton.setRow(i);
                bombButton.setColumn(j);

                // Set other properties of the BombButton (click listener)
                bombButton.setOnClickListener(v -> {
                    if(!bombButton.isFlagged()){
                        //1. switch to ImageView
                        switchToIV(bombButton);

                        //2.Search for void cells
                        searchVoidCells(bombButton.getRow(), bombButton.getColumn());

                        if(bombButton.getBombs()==-1){
                            gameOver();
                        }
                    }
                });

                // Set a long click listener for the BombButton
                bombButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        // 1. Get the count of bombs left
                        bombCount--;
                        bombCountTextView.setText("Bombs: " + bombCount);
                        bombButton.setFlagged();

                        switchToIV(bombButton);
                        bombButton.getIv().setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                bombCount++;
                                bombCountTextView.setText("Bombs: " + bombCount);
                                bombButton.hideIcon();
                                gridLayout.removeView(bombButton.getIv());
                                bombButton.showButton();
                                bombButton.setFlagged();
                                return true; // Indicate that the long click was handled
                            }
                        });

                    System.out.println(bombCount);
                    if(bombCount==0){
                        endGame();
                    }

                    return true; // Indicate that the long click was handled
                    }
                });

                // Add the BombButton to the GridLayout and to the List
                bombButtons.add(bombButton);
                this.gridLayout.addView(bombButton);
            }
        }

        //8. Set bombs positions
        setBombs();

        //9. Calculate surroundings bombs
        SetSurroundings();

        //10. Create and Start Chronometer
        createChronometer();
        chronometer.start();
    }

    private void setBombs(){
        int bombsSet=bombs;
        Random random = new Random();

        do{
            int position = random.nextInt(level * level);
            int row=position/level;
            int col=position%level;
            BombButton btn = (BombButton)gridLayout.getChildAt(position);

            if(btn.getBombs()!=-1){
                btn.setBombs(-1);
                bombsSet--;
                btn.setIconID(selectedCharacterID);
            }
        }while(bombsSet>0);
    }

    private void SetSurroundings(){
        for(int i=0;i<this.gridLayout.getRowCount();i++){
            for(int j=0;j<this.gridLayout.getColumnCount();j++){
                BombButton btn = (BombButton)gridLayout.getChildAt(i*this.gridLayout.getRowCount()+j);
                if(btn.getBombs()!=-1){
                    for(int k=i-1;k<=i+1;k++) {
                        for (int l = j - 1; l <= j + 1; l++) {
                            if (k >= 0 && k < this.gridLayout.getRowCount() && l >= 0 && l < this.gridLayout.getColumnCount()) {
                                BombButton btn2 = (BombButton) gridLayout.getChildAt(k * this.gridLayout.getRowCount() + l);
                                if (btn2.getBombs() == -1) {
                                    btn.setBombs(btn.getBombs() + 1);
                                }
                            }
                        }
                    }
                    switch (btn.getBombs()){
                        case 1:
                            btn.setIconID(R.drawable.n1);
                            break;
                        case 2:
                            btn.setIconID(R.drawable.n2);
                            break;
                        case 3:
                            btn.setIconID(R.drawable.n3);
                            break;
                        case 4:
                            btn.setIconID(R.drawable.n4);
                            break;
                        case 5:
                            btn.setIconID(R.drawable.n5);
                            break;
                        case 6:
                            btn.setIconID(R.drawable.n6);
                            break;
                        case 7:
                            btn.setIconID(R.drawable.n7);
                            break;
                        case 8:
                            btn.setIconID(R.drawable.n8);
                            break;
                    }
                }
                else{
                    btn.setText("B");
                }
            }
        }
    }

    private void createChronometer(){
        chronometer = findViewById(R.id.chronometer);

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                int minutes = (int) (elapsedMillis / 60000);
                int seconds = (int) (elapsedMillis % 60000 / 1000);
                String formattedTime = String.format("%02dmin:%02dsec", minutes, seconds);
                chronometer.setText(formattedTime);
            }
        });
    }

    private void switchToIV(BombButton bombButton){
        // 1. Get position of the clicked BombButton
        int row = bombButton.getRow();
        int column = bombButton.getColumn();
        int cellWidth = gridLayout.getWidth() / gridLayout.getColumnCount();
        int cellHeight = gridLayout.getHeight() / gridLayout.getRowCount();
        GridLayout.LayoutParams paramsIV = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column));

        // 2. Hide the BombButton and Show the Icon
        bombButton.hideButton();
        bombButton.setIcon();
        bombButton.showIcon();

        // 3. Create an ImageView and remove it if exist
        ImageView imageView = bombButton.getIv();
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setLayoutParams(paramsIV);
        gridLayout.removeView(imageView);


        //4. Modify width and height
        paramsIV.width = cellWidth;
        paramsIV.height = cellHeight;

        //5. Add the ImageView to the GridLayout
        gridLayout.addView(imageView);
    }

    private void searchVoidCells(int row, int col){
        BombButton bombButton=null;
        View child = gridLayout.getChildAt(row * gridLayout.getColumnCount() + col);
        if(child instanceof BombButton) {
            bombButton = (BombButton) gridLayout.getChildAt(row * this.gridLayout.getRowCount() + col);
        }else{
            return;
        }

        //if (row < 0 || row >= gridLayout.getRowCount() || col < 0 || col >= gridLayout.getColumnCount() || bombButton.isFlagged() || bombButton.getBombs() != 0 || bombButton.isVisited()) {
        if (row < 0 || row >= gridLayout.getRowCount() || col < 0 || col >= gridLayout.getColumnCount() || bombButton.isFlagged() || bombButton.isVisited()) {
            return;
        }

        bombButton.setVisited(true);
        switchToIV(bombButton);
        if(bombButton.getBombs()==0) {
            //bombButton.hideButton();
            searchVoidCells(row - 1, col);
            searchVoidCells(row + 1, col);
            searchVoidCells(row, col - 1);
            searchVoidCells(row, col + 1);
        }
    }

    private void endGame(){
        boolean victory=true;

        for(int i=0;i<bombButtons.size();i++){
            if(bombButtons.get(i).getBombs()!=-1 && bombButtons.get(i).isFlagged()){
                System.out.println(bombButtons.get(i).getBombs()+"Error");
                victory=false;
            }
        }
        if(victory) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.st_victory));
            builder.setMessage(getString(R.string.st_victory_text));
            builder.setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            // Create and show the dialog
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
            chronometer.stop();
        }else{
            gameOver();
        }
    }

    private void gameOver(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the dialog message
        builder.setTitle(getString(R.string.st_game_over))
                .setMessage(getString(R.string.st_game_over_text))
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        //Show all bombs
        for(int i=0;i<bombButtons.size();i++){
            if(bombButtons.get(i).isFlagged()){
                bombButtons.get(i).setFlagged();
            }
            switchToIV(bombButtons.get(i));
        }
        chronometer.stop();
    }

    private void showConfig(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_config, null); // Create dialog_level_selection.xml

// 3. Find the RadioButtons in the layout
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupLevels);
        RadioButton rbEasy = dialogView.findViewById(R.id.rbEasy);
        RadioButton rbMedium = dialogView.findViewById(R.id.rbMedium);
        RadioButton rbHard = dialogView.findViewById(R.id.rbHard);

// 4. Set the dialog view
        builder.setView(dialogView);

// 5. Set the title
        builder.setTitle(R.string.st_config);

// 6. Set the "Volver" button
        builder.setNegativeButton(R.string.st_back, (dialog, which) -> {
            // Do nothing, just dismiss the dialog
            dialog.dismiss();
        });

// 7. Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

// 8. Handle RadioButton selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId==R.id.rbEasy){
                level=LEVEL_EASY;
                bombs=BOMBS_EASY;
            }else if(checkedId==R.id.rbMedium){
                level=LEVEL_MEDIUM;
                bombs=BOMBS_MEDIUM;
            }else if(checkedId==R.id.rbHard){
                level=LEVEL_HARD;
                bombs=BOMBS_HARD;
            }
            bombCount=bombs;
            bombCountTextView.setText("Bombs: " + bombCount);
        });
    }

    private void showCharacter(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_characters_selection, null);

// 3. Find the Spinner in the layout
        Spinner spinnerCharacter = dialogView.findViewById(R.id.spinnerCharacters);

// 4. Create a list of weapons
        List<Character> characters = new ArrayList<>();
        characters.add(new Character("Bomba", R.drawable.bomb));
        characters.add(new Character("Dinamita", R.drawable.dynamite));
        characters.add(new Character("Granada", R.drawable.grenade));
        characters.add(new Character("Cocktail Molotov", R.drawable.molotov));
        characters.add(new Character("Volcán", R.drawable.volcano));
        characters.add(new Character("Mina submarina", R.drawable.submarine));

// 5. Create and set the custom adapter
        CharacterSpinnerAdapter adapter = new CharacterSpinnerAdapter(this, characters);
        spinnerCharacter.setAdapter(adapter);

// 6. Set the dialog view
        builder.setView(dialogView);

// 7. Set the title
        builder.setTitle("Selecciona un arma");

// 8. Set the positive button (OK)
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Get the selected weapon
            Character selectedCharacter = (Character) spinnerCharacter.getSelectedItem();
            selectedCharacterID = selectedCharacter.getIconResource();
            characterIconImageView.setImageResource(selectedCharacterID);
            dialog.dismiss();
        });

// 9. Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}