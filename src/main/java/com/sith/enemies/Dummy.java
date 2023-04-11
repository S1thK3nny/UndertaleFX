package com.sith.enemies;

import com.sith.globals;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;

public class Dummy extends Enemy {

    boolean isSad = false;
    boolean isGlad = false;
    String mood = "";

    public Dummy(Pane root, ArrayList<Enemy> enemies, HBox enemiesBox, String name, int atk, int def) {
        super(root, enemies, enemiesBox, globals.dummySprites, name, atk, def);

        actOptions.add("Insult");
        actOptions.add("Surprise");
        actOptions.add("Compliment");
        checkDescription = "\n* A dummy of his own, different from the rest";
    }

    @Override
    public String actOption1() {
        String description = super.actOption1();
        return description + "\n" + mood;
    }

    @Override
    public String actOption2() {
        if(isGlad) {
            return makeHimNeutral(5,  "You say that he smells of oldness \n* He is confused");
        }
        else if(!isSad) {
            return makeHimSadOrGlad("SAD ", false, 4, 5, "* He is, indeed, sad", "Apologize", "You tell " + name + "that he's just a puppet. \n* You made him sad! ");

        }
        else {
            return "He doesn't want to hear more insults";
        }

    }

    @Override
    public String actOption3() {
        MediaPlayer mediaPlayer = new MediaPlayer(globals.surpriseSound);
        mediaPlayer.play();
        return "Surprise!\n* ...?";
    }

    @Override
    public String actOption4() {
        if(isSad) {
            return makeHimNeutral(4,  "You apologize to him \n* It makes him feel a little bit better");
        }
        else if(!isGlad) {
            return makeHimSadOrGlad("GLAD ", true, 2, 3, "* He is, indeed, glad", "Compliment", "You tell him his hair looks great \n* No one has said that to him before!");
        }
        else {
            return "He's too happy to hear you";
        }
    }

    private String makeHimNeutral(int subStringInt, String returnMessage) {
        name = name.substring(subStringInt);

        actOptions.remove(actOptions.size()-1);
        actOptions.add("Compliment");

        isSad = false;
        isGlad = false;
        canBeSpared = false;

        mood = "";

        setFill(new ImagePattern(sprites[0]));
        hurtVersion = sprites[1];

        return returnMessage;
    }

    private String makeHimSadOrGlad(String gladOrSad, boolean thisMakesHimGlad, int spriteNormal, int spriteHurt, String mood, String nextActOption, String returnMessage) {
        name = gladOrSad + name;

        if(thisMakesHimGlad) {
            isGlad = true;
            canBeSpared = true;
        }
        else {
            isSad = true;
            canBeSpared = false;
        }

        setFill(new ImagePattern(sprites[spriteNormal]));
        hurtVersion = sprites[spriteHurt];

        this.mood = mood;


        actOptions.remove(actOptions.size()-1);
        actOptions.add(nextActOption);

        return returnMessage;
    }
}
