package com.sith;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.text.Font;

import java.io.File;

public class globals {
    //Splash screen
    public static final Image splashScreen = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/Splash.png");

    //Player icon hearts
    public static final Image redHeart = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/hearts/heart.png");
    public static final Image blueHeart = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/hearts/heart_blue.png");

    //Fonts
    public static final Font dtmSans = Font.loadFont("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/fonts/DTM-Sans.otf", 60);
    public static final Font dtmSansHP = Font.loadFont("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/fonts/DTM-Sans.otf", 40);

    //Buttons, not selected
    public static final Image attackButton = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_fightbt_0.png");
    public static final Image actButton = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_talkbt_0.png");
    public static final Image itemButton = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_itembt_0.png");
    public static final Image mercyButton = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_sparebt_0.png");

    //Buttons, selected
    public static final Image attackButtonSelected = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_fightbt_1.png");
    public static final Image actButtonSelected = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_talkbt_1.png");
    public static final Image itemButtonSelected = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_itembt_1.png");
    public static final Image mercyButtonSelected = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/buttons/spr_sparebt_1.png");

    /*Sounds
    NOTE: OGG FILES ARE NOT SUPPORTED!
    */
    public static final AudioClip hurtSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/sounds/snd_hurt1.wav").toURI().toString());
    public static final AudioClip buttonConfirmSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/sounds/snd_select.wav").toURI().toString());
    public static final AudioClip switchCurrentElementSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/sounds/snd_squeak.wav").toURI().toString()); //Ok look, I get it, switchCurrentElement is not a good name but squeak? Come on Toby, that took me at least a minute to find
    public static final AudioClip charAppearSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/sounds/snd_char.wav").toURI().toString());
    public static final AudioClip healSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/sounds/snd_heal_c.wav").toURI().toString());
    public static final AudioClip levelUpSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/sounds/snd_levelup.wav").toURI().toString());

    /*Music
    For music, we have to use Media so that other AudioClips will not interrupt / stop the music.
    splashScreenSound is an exception, due to it playing only at the very beginning.
     */
    public static final AudioClip splashScreenSound = new AudioClip(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/music/mus_intronoise.wav").toURI().toString()); //I have no idea how, but it took me 10 minutes to find this at least and only then also thought of the name "intro" due to a YT vid. This is what people mean when they say I am special.
    public static final Media battleMusic = new Media(new File("C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/music/mus_battle1.wav").toURI().toString());


    // Enemies //

    //Dummy
    private static final Image dummy = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/enemies/dummy/spr_dummybattle_0.png");
    private static final Image dummyHurt = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/enemies/dummy/spr_dummybattle_1.png");
    private static final Image dummyGlad = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/enemies/dummy/spr_dummybattle_glad_0.png");
    private static final Image dummyGladHurt = new Image("file:C:/Users/Kenny/Documents/GitHub/Undertale_Two/src/main/resources/images/enemies/dummy/spr_dummybattle_glad_1.png");

    public static final Image[] dummySprites = {dummy, dummyHurt, dummyGlad, dummyGladHurt};
}