package com.sith;

import com.sith.buttons.*;
import com.sith.enemies.Projectile;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application {
    interactiveButton[] buttons;
    int currentSelectedButton = -1;

    boolean atTopBorder = false;
    boolean atBottomBorder = false;
    boolean atLeftBorder = false;
    boolean atRightBorder = false;

    static Text curAndMaxHealth;
    public static FightingBox fb;

    double fbWidth;
    double fbHeight;
    double fbX;
    double fbY;

    Projectile test;
    Player player;

    boolean wTimer = false;
    boolean throwProjectiles = false;

    static final HashSet<String> keysPressed = new HashSet<>();
    final List<Projectile> list = new ArrayList<>();
    final List<Projectile> projectiles = Collections.synchronizedList(list);

    static Rectangle healthBar;
    static Rectangle lostHealthBar;

    HBox underFB;
    HBox healthBars;
    HBox horizontalButtonAlignment;

    Timeline projectileTimeline;
    Timeline splashTimeline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        Scene scene = new Scene(root, 1920, 1080, Color.BLACK);
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("undertaleFX");
        primaryStage.getIcons().add(globals.redHeart);
        primaryStage.show();

        root.setBackground(new Background(
                new BackgroundImage(
                        globals.splashScreen,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(
                                root.getWidth(),
                                root.getHeight(),
                                false,
                                false,
                                false,
                                false
                        )
                )
        ));

        globals.splashScreenSound.play();

        //Yes, we could use a mediaplayer, but how about we don't
        splashTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            if (!globals.splashScreenSound.isPlaying()) {
                splashTimeline.stop();
                runGame(root, scene);
            }
        }));
        splashTimeline.setCycleCount(Timeline.INDEFINITE);
        splashTimeline.play();
    }

    public void runGame(Pane root, Scene scene) {
        root.setStyle("-fx-background-color: black;");

        //fb start
        fbWidth = scene.getWidth()/2 + scene.getWidth()/4;
        fbHeight = scene.getHeight()/2 - scene.getWidth()/10;
        fbX = scene.getWidth()/2 - fbWidth/2;
        fbY = scene.getHeight()/2 - fbHeight/4;
        fb = new FightingBox(fbX, fbY, fbWidth, fbHeight);
        //fb end

        //player start
        player = new Player(fb.getX() + fb.getWidth()/2 - 22.5, fb.getY() + fb.getHeight()/2 - 22.5, 45, 45); //For the player position, we cannot just take the width and height into account but also the moved X and Y position of the fb
        //player end

        //Health start
        curAndMaxHealth = new Text(player.getCurHealth()+"/"+player.getMaxHealth());
        healthBar = new Rectangle(scene.getWidth()/2, curAndMaxHealth.getY(), player.getMaxHealth()*3, 15*3);
        lostHealthBar = new Rectangle(healthBar.getX(), healthBar.getY(), healthBar.getWidth(), healthBar.getHeight());
        underFB = new HBox(); //We need an HBox to keep all elements under the fb aligned (health, hp values, name, etc)
        configureUnderFBStuff();
        //Health end

        //Buttons start
        //This is ugly, I know. However, we have to do this so that we can call itemButton.noItemsLeft() later
        itemButton itemButton = new itemButton(player);
        buttons = new interactiveButton[]{new attackButton(), new actButton(player.getWidth()), itemButton, new mercyButton(player.getWidth())}; //Need to declare them here so that the FB is ready
        addButtons();
        //Buttons end

        root.getChildren().addAll(underFB, horizontalButtonAlignment, player, player.collisionBox, fb, fb.getCurrentText(), buttons[0].getOptions(), buttons[1].getOptions(), buttons[2].getOptions(), buttons[3].getOptions());

        projectileTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), e -> test = new Projectile(projectiles, root, fb, fb.getY(), 35, 50))
        );
        projectileTimeline.setCycleCount(Timeline.INDEFINITE);



        //OnKey start
        scene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode().toString());
            if(player.getState().equals("menu")) {
                if(interactiveButton.wentIntoButton) {
                    if(userInputRight()) {
                        buttons[currentSelectedButton].selectInteraction("right", player);
                    }
                    else if(userInputLeft()) {
                        buttons[currentSelectedButton].selectInteraction("left", player);
                    }
                    else if(userInputTop()) {
                        buttons[currentSelectedButton].selectInteraction("up", player);
                    }
                    else if(userInputDown()) {
                        buttons[currentSelectedButton].selectInteraction("down", player);
                    }
                }
                else {
                    if(userInputRight()) {
                        selectButton(true);
                    }
                    else if(userInputLeft()) {
                        selectButton(false);
                    }
                }


                if (userInputSelect() && !fb.getIsResizing()) {
                    //player.drawCollision();

                    //If you have no items left and try to use the item button
                    if(!interactiveButton.wentIntoButton && currentSelectedButton==2 && itemButton.noItemsLeft()) {
                        globals.buttonConfirmSound.play();
                    }
                    else if(!interactiveButton.wentIntoButton && currentSelectedButton>=0) {
                        buttons[currentSelectedButton].openButton();
                        interactiveButton.wentIntoButton = true;
                        fb.setCurrentTextVisible(false);
                        //Yes, this excludes the fight button.
                        if(currentSelectedButton>0) {
                            //Moves player to the corresponding text
                            Platform.runLater(() -> player.movePlayer(buttons[currentSelectedButton].getTextX(0, player.getWidth()), buttons[currentSelectedButton].getTextY(0, player.getHeight())));
                        }
                        else {
                            //Temp option if the player pressed fight
                            finishPlayerMove();
                        }
                    }
                    else if(interactiveButton.wentIntoButton && currentSelectedButton>0) {
                        deselectButtons();
                        buttons[currentSelectedButton].hideOptions();
                        fb.setCurrentTextVisible(true, buttons[currentSelectedButton].interact(), player);
                    }
                }
                else if(keysPressed.contains("X") && interactiveButton.wentIntoButton && currentSelectedButton>0) {
                    interactiveButton.wentIntoButton = false;
                    buttons[currentSelectedButton].hideOptions();
                    movePlayerToButton(currentSelectedButton);
                    fb.setCurrentTextVisible(true);
                }
            }

            else if(player.getState().equals("gone")) {
                if(userInputSelect()) {
                    if(fb.hasFinishedDialog()) finishPlayerMove();
                }
                else if(keysPressed.contains("X")) {
                    if(!fb.hasFinishedDialog()) fb.skipDialog();
                }
            }

            if(userInputTop() && player.getState().equals("gravity")) {
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
                    enterMenu();
                }
                else {
                    finishPlayerMove();
                }
            }
            else if(keysPressed.contains("G")) {
                if(throwProjectiles) {
                    projectileTimeline.stop();

                }
                if(!throwProjectiles) {
                    projectileTimeline.play();
                }
                throwProjectiles = !throwProjectiles;
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
                    player.updatePosition(scene.getWidth(), scene.getHeight(), borders, wTimer); //using scene.get to correspond to window size update
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



    //We're using a timeline to make it look like an animation
    public void resetFB() {
        if(fb.getWidth() != fbWidth || fb.getHeight() != fbHeight || fb.getX() != fbX || fb.getY() != fbY) {
            fb.setIsResizing(true);
            Timeline resize = new Timeline();
            Timeline move = new Timeline();

            resize.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(0),
                            new KeyValue(fb.widthProperty(), fb.getWidth()),
                            new KeyValue(fb.heightProperty(), fb.getHeight())),
                    new KeyFrame(Duration.millis(275),
                            new KeyValue(fb.widthProperty(), fbWidth),
                            new KeyValue(fb.heightProperty(), fbHeight))

            );

            move.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(0),
                            new KeyValue(fb.xProperty(), fb.getX()),
                            new KeyValue(fb.yProperty(), fb.getY())),
                    new KeyFrame(Duration.millis(200),
                            new KeyValue(fb.xProperty(), fbX),
                            new KeyValue(fb.yProperty(), fbY)),
                    new KeyFrame(Duration.millis(200), e -> {
                        fb.setIsResizing(false);
                        fb.setCurrentTextVisible(true);
                    })
            );

            move.setDelay(Duration.millis(150));
            resize.play();
            move.play();
        }
        else {
            fb.setCurrentTextVisible(true);
        }
    }



    public void configureUnderFBStuff() {
        Text name = new Text(player.getName());
        configureText(name);

        Text LVL = new Text("LV " + player.getLV());
        configureText(LVL);

        Text HP = new Text("HP");
        configureText(HP);
        HP.setFont(globals.dtmSansHP);
        HP.setTranslateY(HP.getTranslateY()+ (globals.dtmSans.getSize()-globals.dtmSansHP.getSize())/2 );

        //HP Values
        configureText(curAndMaxHealth);

        healthBar.setFill(Color.YELLOW);

        lostHealthBar.setFill(Color.RED);

        //HBox for Health bars
        healthBars = new HBox();
        healthBars.setSpacing(-healthBar.getWidth()); //Spacing must be minus the max health of the player so that it aligns over the yellow health bar
        healthBars.getChildren().addAll(lostHealthBar, healthBar);
        healthBars.setTranslateY(name.getY());

        /*
        We need two more HBoxes to have a proper section for the "HP", health bar and the actual HP values. This is the best way to align them and for them to not be affected by the underFB scaling.
         */

        HBox hbAndHPValues = new HBox();
        hbAndHPValues.setSpacing(curAndMaxHealth.getBoundsInLocal().getWidth()/4);
        hbAndHPValues.getChildren().addAll(healthBars, curAndMaxHealth);
        hbAndHPValues.setAlignment(Pos.BASELINE_CENTER);

        HBox completeHealthSection = new HBox();
        completeHealthSection.setSpacing(HP.getBoundsInLocal().getWidth()/5);
        completeHealthSection.getChildren().addAll(HP, hbAndHPValues);



        underFB.getChildren().addAll(name, LVL, completeHealthSection);
        underFB.setLayoutX(fb.getX() - fb.getStrokeWidth()/2);
        underFB.setLayoutY(fb.getHeight() + fb.getY() + fb.getStrokeWidth());
        underFB.setSpacing(100);
    }



    public static void configureText(Text t) {
        t.setFill(Color.WHITE);
        t.setFont(globals.dtmSans);
        t.setTextAlignment(TextAlignment.LEFT);
    }



    public static void setHealthText(int curHealth, int maxHealth, int damageOrHealthTaken, boolean isDamage) {
        curAndMaxHealth.setText(curHealth+"/"+maxHealth);

        if(isDamage) {
            //Decrease width, retain position. Need to accommodate for the width and height that we multiplied by 3. I still hate scaling...
            healthBar.setWidth(healthBar.getWidth() - damageOrHealthTaken*3);

            //This lets the HP values stay in place. We do not want it to keep translating once the health bar is gone
            if(curHealth>=0) {
                curAndMaxHealth.setTranslateX(curAndMaxHealth.getTranslateX() + damageOrHealthTaken*3);
            }
        }
        else {
            if(!(healthBar.getWidth()>=maxHealth*3)) {
                if(!(healthBar.getWidth()+damageOrHealthTaken*3>=maxHealth*3)) {
                    healthBar.setWidth(healthBar.getWidth() + damageOrHealthTaken*3);
                }
                else {
                    healthBar.setWidth(maxHealth*3);
                    curAndMaxHealth.setTranslateX(0);
                }
            }

            if(curHealth<maxHealth) {
                curAndMaxHealth.setTranslateX(curAndMaxHealth.getTranslateX() - damageOrHealthTaken*3);
            }
        }
    }



    public void addButtons() {
        horizontalButtonAlignment = new HBox();
        horizontalButtonAlignment.setLayoutX(fb.getX() - fb.getStrokeWidth()/2);
        horizontalButtonAlignment.setLayoutY(underFB.getLayoutY() + buttons[0].getHeight()/2 + healthBar.getHeight()/2);
        horizontalButtonAlignment.setPrefWidth(fb.getWidth());
        double spacing = (fb.getWidth() - (buttons[0].getWidth()*4)) / 3 + fb.getStrokeWidth()/4;
        horizontalButtonAlignment.setSpacing(spacing);
        horizontalButtonAlignment.getChildren().addAll(buttons[0], buttons[1], buttons[2], buttons[3]);


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
        player.movePlayer(buttons[buttonID].getLayoutX() + buttons[buttonID].getWidth()/6.5 - player.getWidth()/2 + horizontalButtonAlignment.getLayoutX(), horizontalButtonAlignment.getLayoutY() + buttons[0].getHeight()/2 - player.getHeight()/2);

    }



    public void finishPlayerMove() {
        player.setState("normal");
        interactiveButton.wentIntoButton = false;
        deselectButtons();
        buttons[currentSelectedButton].hideOptions();
        player.movePlayer(fb.getX() + fb.getWidth()/2 - 22.5, fb.getY() + fb.getHeight()/2 - 22.5);
        fb.setCurrentTextVisible(false);
    }

    public void enterMenu() {
        player.setState("menu");
        currentSelectedButton = 0;
        movePlayerToButton(currentSelectedButton);
        buttons[currentSelectedButton].select(buttons);
        projectileTimeline.stop();
        resetFB();
    }

    public static boolean userInputTop() {
        return keysPressed.contains("W") || keysPressed.contains("UP");
    }

    public static boolean userInputLeft() {
        return keysPressed.contains("A") || keysPressed.contains("LEFT");
    }

    public static boolean userInputDown() {
        return keysPressed.contains("S") || keysPressed.contains("DOWN");
    }

    public static boolean userInputRight() {
        return keysPressed.contains("D") || keysPressed.contains("RIGHT");
    }

    public static boolean userInputBack() {
        return keysPressed.contains("SHIFT") || keysPressed.contains("X");
    }

    public static boolean userInputSelect() {return keysPressed.contains("Z") || keysPressed.contains("ENTER");}
}