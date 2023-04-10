package com.sith.enemies;

import com.sith.globals;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;

public class Dummy extends Enemy {

    boolean isSad = false;

    public Dummy(Pane root, ArrayList<Enemy> enemies, HBox enemiesBox, String name, int atk, int def) {
        super(root, enemies, enemiesBox, globals.dummySprites, name, atk, def);

        actOptions.add("Insult");
        actOptions.add("Surprise");
        checkDescription = "\n* A dummy of his own, different from the rest";
    }

    @Override
    public String actOption2() {
        if(!isSad) {
            name = "Sad " + name;
            isSad = true; // :(
            setFill(new ImagePattern(sprites[4]));
            hurtVersion = sprites[5];
            return "You tell the dummy that he's just a puppet. \n* You made him sad! ";
        }
        else {
            return "He's not listening to you anymore.";
        }

    }

    @Override
    public String actOption3() {
        MediaPlayer mediaPlayer = new MediaPlayer(globals.surpriseSound);
        mediaPlayer.play();
        return "Surprise!\n* ...?";
    }
}
