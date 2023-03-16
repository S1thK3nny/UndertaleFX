package com.sith;

import com.sith.buttons.*;
import com.sith.enemies.Projectile;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application {
    interactiveButton[] buttons;
    int currentSelectedButton = -1;
    int currentSelectedInteraction = 0;

    boolean atTopBorder = false;
    boolean atBottomBorder = false;
    boolean atLeftBorder = false;
    boolean atRightBorder = false;

    static Text curAndMaxHealth;
    public static FightingBox fb;

    Projectile test;
    Player player;

    boolean wTimer = false;
    boolean throwProjectiles = false;

    final HashSet<String> keysPressed = new HashSet<>();
    final List<Projectile> list= new ArrayList<>();
    final List<Projectile> projectiles = Collections.synchronizedList(list);

    static Rectangle healthBar;
    static Rectangle lostHealthBar;
    HBox underFB;
    HBox healthBars;
    HBox horizontalButtonAlignment;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 1920, 1080, Color.BLACK);
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());



        //fb start
        double fbWidth = scene.getWidth()/2 + scene.getWidth()/4;
        double fbHeight = scene.getHeight()/2 - scene.getWidth()/10;
        fb = new FightingBox(scene.getWidth()/2 - fbWidth/2, scene.getHeight()/2 - fbHeight/4, fbWidth, fbHeight);
        //fb end

        //player start
        player = new Player(fb.getX() + fb.getWidth()/2 - 22.5, fb.getY() + fb.getHeight()/2 - 22.5, 45, 45); //For the player position, we cannot just take the width and height into account but also the moved X and Y position of the fb
        //player end

        //Health start
        curAndMaxHealth = new Text(player.getCurHealth()+"/"+player.getMaxHealth());
        healthBar = new Rectangle(scene.getWidth()/2, curAndMaxHealth.getY(), player.getMaxHealth(), 15);
        lostHealthBar = new Rectangle(healthBar.getX(), healthBar.getY(), healthBar.getWidth(), healthBar.getHeight());
        underFB = new HBox(); //We need an HBox to keep all elements under the fb aligned (health, hp values, name, etc)
        configureUnderFBStuff();
        //Health end

        //Buttons start
        buttons = new interactiveButton[]{new attackButton(), new actButton(player.getWidth()), new itemButton(), new mercyButton(player.getWidth())}; //Need to declare them here so that the FB is ready
        addButtons();
        //Buttons end

        root.getChildren().addAll(underFB, horizontalButtonAlignment, player, player.collisionBox, fb, buttons[1].getOptions(), buttons[3].getOptions());

        //Window stuff start
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("UNDERTALE");
        primaryStage.getIcons().add(globals.redHeart);
        //Window stuff start

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(150), e -> test = new Projectile(projectiles, root, fb, fb.getY(), 35, 50))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);



        //OnKey start
        scene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode().toString());
            if(player.getState().equals("menu")) {
                if(keysPressed.contains("D")) {
                    if(!interactiveButton.wentIntoButton) {
                        selectButton(true);
                    }
                    else {
                        selectInteraction("right");
                    }
                }
                else if(keysPressed.contains("A")) {
                    if(!interactiveButton.wentIntoButton) {
                        selectButton(false);
                    }
                    else {
                        selectInteraction("left");
                    }
                }
                else if(keysPressed.contains("W")) {
                    if(interactiveButton.wentIntoButton) {
                        selectInteraction("up");
                    }
                }
                else if(keysPressed.contains("S")) {
                    if(interactiveButton.wentIntoButton) {
                        selectInteraction("down");
                    }
                }
            }

            if(keysPressed.contains("W") && player.getState().equals("gravity")) {
                if(!wTimer && !player.isJumping) {
                    wTimer = true;
                    Timeline wTimeline = new Timeline(
                            new KeyFrame(Duration.millis(600), e -> wTimer = false)
                    );
                    wTimeline.play();
                }
            }
            else if(keysPressed.contains("SPACE")) {
                if(player.state.equals("normal")) {
                    //player.setState("gravity");
                    player.setState("menu");
                    currentSelectedButton = 0;
                    movePlayerToButton(currentSelectedButton);
                    buttons[currentSelectedButton].select(buttons);

                }
                else {
                    player.setState("normal");
                    interactiveButton.wentIntoButton = false;
                    deselectButtons();
                    buttons[currentSelectedButton].hideOptions();
                    player.movePlayer(fb.getX() + fb.getWidth()/2 - 22.5, fb.getY() + fb.getHeight()/2 - 22.5);
                }
            }
            else if(keysPressed.contains("G")) {
                if(throwProjectiles) {
                    timeline.stop();

                }
                if(!throwProjectiles) {
                    timeline.play();
                }
                throwProjectiles = !throwProjectiles;
            }


            if (keysPressed.contains("Z")) {
                //player.drawCollision();
                if(!interactiveButton.wentIntoButton && currentSelectedButton>=0) {
                    buttons[currentSelectedButton].interact();
                    interactiveButton.wentIntoButton = true;
                    //Yes, this excludes the fight button.
                    if(currentSelectedButton>0) {
                        player.movePlayer(buttons[currentSelectedButton].getTextX(0), buttons[currentSelectedButton].getTextY(0));
                        currentSelectedInteraction = 0;
                    }
                }
            }
            else if(keysPressed.contains("X") && interactiveButton.wentIntoButton) {
                interactiveButton.wentIntoButton = false;
                buttons[currentSelectedButton].hideOptions();
                movePlayerToButton(currentSelectedButton);
            }
        });

        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode().toString()));
        //OnKey end



        // start animation timer to update everything necessary start
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (!player.getState().equals("menu")) {
                    atLeftBorder = player.getX() < fb.getX() + player.getWidth() / 6;
                    atRightBorder = player.getX() > (fb.getX() + fb.getWidth()) - (player.getWidth() + player.getWidth() / 6);
                    atTopBorder = player.getY() < fb.getY() + player.getHeight() / 6;
                    atBottomBorder = player.getY() > (fb.getY() + fb.getHeight()) - (player.getHeight() + player.getHeight() / 6);
                    //we need the /5 to get accurate sprite border stuff

                    Boolean[] borders = {atLeftBorder, atRightBorder, atTopBorder, atBottomBorder};
                    player.updatePosition(scene.getWidth(), scene.getHeight(), keysPressed, borders, wTimer); //using scene.get to correspond to window size update
                    fb.updatePosition(scene.getWidth(), scene.getHeight(), keysPressed, player.getHeight(), player.getWidth());
                    player.checkBounds(fb, borders);

                    synchronized (projectiles) {
                        Iterator<Projectile> iterator = projectiles.iterator();
                        while (iterator.hasNext()) {
                            Projectile projectile = iterator.next();
                            projectile.move();
                            if (projectile.getY() >= (fb.getY() + fb.getHeight()) - (projectile.getHeight() + fb.getStrokeWidth())) {
                                iterator.remove();
                                root.getChildren().remove(projectile);
                                projectile.removeFromList();
                            } else if (player.intersects(projectile.getBoundsInLocal()) && !player.isInvincible()) {
                                player.setCurHealth(1, true);

                                iterator.remove();
                                root.getChildren().remove(projectile);
                                projectile.removeFromList();
                            }
                        }
                    }
                }
            }
        }.start();
        // start animation timer to update everything necessary end
    }



    public void configureUnderFBStuff() {
        Text name = new Text(player.getName());
        configureText(name);

        Text LVL = new Text("LV " + player.getLV());
        configureText(LVL);

        Text HP = new Text("HP");
        configureText(HP);
        //We need to run configureText first before actually resizing as it needs another scale
        HP.setScaleX(2.35);
        HP.setScaleY(1.75);

        //HP Values
        configureText(curAndMaxHealth);

        healthBar.setFill(Color.YELLOW);
        healthBar.setScaleX(3);
        healthBar.setScaleY(3);

        lostHealthBar.setFill(Color.DARKRED);
        lostHealthBar.setScaleX(3);
        lostHealthBar.setScaleY(3);

        //HBox for Health bars
        healthBars = new HBox();
        healthBars.setSpacing(-player.getMaxHealth()); //Spacing must be minus the max health of the player so that it aligns over the yellow health bar
        healthBars.getChildren().addAll(lostHealthBar, healthBar);

        /*
        We need two more HBoxes to have a proper section for the "HP", health bar and the actual HP values. This is the best way to align them and for them to not be affected by the underFB scaling.
         */

        HBox hbAndHPValues = new HBox();
        hbAndHPValues.setSpacing(healthBar.getWidth() + curAndMaxHealth.getX() + curAndMaxHealth.getLayoutBounds().getWidth() + 10);
        hbAndHPValues.getChildren().addAll(healthBars, curAndMaxHealth);
        hbAndHPValues.setAlignment(Pos.BASELINE_CENTER);

        HBox completeHealthSection = new HBox();
        completeHealthSection.setSpacing(50);
        completeHealthSection.getChildren().addAll(HP, hbAndHPValues);



        underFB.getChildren().addAll(name, LVL, completeHealthSection);
        underFB.setLayoutX(fb.getX() + name.getBaselineOffset() + fb.getStrokeWidth()*2);
        underFB.setLayoutY(fb.getHeight() + fb.getY() + fb.getStrokeWidth() + 35);
        underFB.setSpacing(150);
    }



    public static void configureText(Text t) {
        t.setFill(Color.WHITE);
        t.setScaleX(2.75);
        t.setScaleY(2.75);
        t.setFont(globals.dtmSans);
    }



    public static void setHealthText(int curHealth, int maxHealth, int damageTaken) {
        curAndMaxHealth.setText(curHealth+"/"+maxHealth);

        //Decrease width, retain position
        healthBar.setWidth(healthBar.getWidth() - damageTaken);
        healthBar.setTranslateX(healthBar.getTranslateX() - damageTaken);

        //This lets the HP values stay in place. We do not want it to keep translating once the health bar is gone
        if(curHealth>=0) {
            curAndMaxHealth.setTranslateX(curAndMaxHealth.getTranslateX() + damageTaken);
        }
    }



    public void addButtons() {
        horizontalButtonAlignment = new HBox();
        horizontalButtonAlignment.getChildren().addAll(buttons[0], buttons[1], buttons[2], buttons[3]);
        horizontalButtonAlignment.setLayoutX(underFB.getLayoutX() + buttons[0].getWidth()/2 - fb.getStrokeWidth()/2 + 1.25);
        horizontalButtonAlignment.setLayoutY(underFB.getLayoutY() + buttons[0].getHeight()*2 + healthBar.getHeight());
        horizontalButtonAlignment.setSpacing(fb.getWidth()/9 + buttons[0].getWidth() + fb.getStrokeWidth()/4);
    }



    public void selectButton(boolean goRight) {
        if(goRight) {
            if(currentSelectedButton >= buttons.length-1) return;
            ++currentSelectedButton;
        }
        else {
            if(currentSelectedButton <= 0) return;
            --currentSelectedButton;
        }
        /*
        Ok...what does setX do here actually?
        It is simple.
        We get the X position of the horizontalButtonAlignment, subtract the width of the button, so we are at the start of it, and then we add 1/3 of it to be in the position before the text.
        However, since we have multiple buttons, we need to account for each position. Luckily for button 0, that would be 0 so everything fits perfectly.
         */
        movePlayerToButton(currentSelectedButton);
        buttons[currentSelectedButton].select(buttons);
    }



    public void deselectButtons() {
        for(interactiveButton b : buttons) {
            b.setSelected(false);
        }
    }



    public void movePlayerToButton(int buttonID) {
        //You want advice? Don't touch this ever again.
        player.movePlayer(horizontalButtonAlignment.getLayoutX() - horizontalButtonAlignment.getChildren().get(buttonID).getBoundsInLocal().getWidth() + horizontalButtonAlignment.getChildren().get(buttonID).getBoundsInLocal().getWidth()/3 + horizontalButtonAlignment.getChildren().get(buttonID).getLayoutX(), horizontalButtonAlignment.getLayoutY());

    }


    public void selectInteraction(String direction) {
        switch (direction) {
            case "up" -> {
                if(currentSelectedInteraction>=2 && buttons[currentSelectedButton].getText(currentSelectedInteraction-2).isVisible()) {
                    currentSelectedInteraction-=2;
                }
                else if(currentSelectedInteraction>=1  && buttons[currentSelectedButton].getText(currentSelectedInteraction-1).isVisible()) {
                    --currentSelectedInteraction;
                }
            }
            case "left" -> {
                if(currentSelectedInteraction>0  && buttons[currentSelectedButton].getText(currentSelectedInteraction-1).isVisible()) --currentSelectedInteraction;
            }
            case "down" -> {
                if(currentSelectedInteraction+2 < buttons[currentSelectedButton].getTexts().size()  && buttons[currentSelectedButton].getText(currentSelectedInteraction+2).isVisible()) {
                    currentSelectedInteraction+=2;
                }
                else if(currentSelectedInteraction+1 < buttons[currentSelectedButton].getTexts().size()  && buttons[currentSelectedButton].getText(currentSelectedInteraction+1).isVisible()) {
                    ++currentSelectedInteraction;
                }
            }
            case "right" -> {
                if(currentSelectedInteraction<buttons[currentSelectedButton].getTexts().size()-1  && buttons[currentSelectedButton].getText(currentSelectedInteraction+1).isVisible()) ++currentSelectedInteraction;
            }
        }
        player.movePlayer(buttons[currentSelectedButton].getTextX(currentSelectedInteraction), buttons[currentSelectedButton].getTextY(currentSelectedInteraction));
    }
}