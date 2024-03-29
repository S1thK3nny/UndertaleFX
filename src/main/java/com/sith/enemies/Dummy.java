package com.sith.enemies;

import com.sith.Globals;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;

public class Dummy extends Enemy {

    boolean isSad = false;
    boolean isGlad = false;
    int gladCounter = 0;
    String mood = "";

    public Dummy(ArrayList<Enemy> enemies, HBox enemiesBox, String name, int atk, int def) {
        super(enemies, enemiesBox, Globals.dummySprites, name, atk, def);

        actOptionsDisplayName.add("Insult");
        actOptionsDisplayName.add("Surprise");
        actOptionsDisplayName.add("Compliment");
        checkDescription = "\n* A dummy of his own, different from the rest";

        hp = 15;
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
            return makeHimSadOrGlad("SAD ", false, 4, 5, "* He is, indeed, sad", "Apologize", "You tell " + name + " that he's just a puppet \n* You made him sad! ");

        }
        else {
            return "He doesn't want to hear more insults";
        }

    }

    @Override
    public String actOption3() {
        MediaPlayer mediaPlayer = new MediaPlayer(Globals.surpriseSound);
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
            if(gladCounter<3) {
                gladCounter++;
                return "He's too happy to hear you";
            }
            else {
                return "Okay, you can stop now\n* Really";
            }
        }
    }

    private String makeHimNeutral(int subStringInt, String returnMessage) {
        name = name.substring(subStringInt);

        actOptionsDisplayName.remove(actOptionsDisplayName.size()-1);
        actOptionsDisplayName.add("Compliment");

        isSad = false;
        isGlad = false;
        canBeSpared = false;

        mood = "";

        setCurrentSprite(sprites[0], sprites[1]);

        return returnMessage;
    }

    private String makeHimSadOrGlad(String gladOrSad, boolean thisMakesHimGlad, int spriteNormal, int spriteHurt, String mood, String nextActOption, String returnMessage) {
        name = gladOrSad + name;

        if(thisMakesHimGlad) {
            isGlad = true;
            canBeSpared = true;
            gladCounter++;
        }
        else {
            isSad = true;
            canBeSpared = false;
            if(gladCounter>0) gladCounter--;
        }

        setCurrentSprite(sprites[spriteNormal], sprites[spriteHurt]);

        this.mood = mood;


        actOptionsDisplayName.remove(actOptionsDisplayName.size()-1);
        actOptionsDisplayName.add(nextActOption);

        return returnMessage;
    }
}
