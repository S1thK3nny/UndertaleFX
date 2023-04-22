package com.sith;

import com.sith.buttons.*;
import com.sith.enemies.Dummy;
import com.sith.enemies.Enemy;
import com.sith.enemies.Projectile;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

import static com.sith.Globals.DEVELOPER_MODE;

public class Main extends Application {
    interactiveButton[] buttons;
    itemButton itemButton;
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
    public static final ArrayList<Enemy> enemies = new ArrayList<>();

    static Rectangle healthBar;
    static Rectangle lostHealthBar;

    HBox underFB;
    HBox healthBars;
    HBox horizontalButtonAlignment;
    HBox enemiesBox;

    Text LVL;

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
        primaryStage.setTitle("UndertaleFX");
        primaryStage.getIcons().add(Globals.redHeart);
        primaryStage.show();

        root.setBackground(new Background(
                new BackgroundImage(
                        Globals.splashScreen,
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

        Globals.splashScreenSound.play();

        //Yes, we could use a mediaplayer, but how about we don't
        splashTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
            if (!Globals.splashScreenSound.isPlaying()) {
                splashTimeline.stop();
                runGame(root, scene);
            }
        }));
        splashTimeline.setCycleCount(Timeline.INDEFINITE);
        splashTimeline.play();
    }



    public void runGame(Pane root, Scene scene) {
        root.setStyle("-fx-background-color: black;");
        MediaPlayer mediaPlayer = new MediaPlayer(Globals.preBattleMusic);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();

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
        itemButton = new itemButton(player);
        buttons = new interactiveButton[]{new attackButton(), new actButton(player.getWidth()), itemButton, new mercyButton(player.getWidth())}; //Need to declare them here so that the FB is ready
        addButtons();
        //Buttons end



        projectileTimeline = new Timeline(
                new KeyFrame(Duration.millis(150), e -> test = new Projectile(projectiles, root, fb, fb.getY(), 35, 50))
        );
        projectileTimeline.setCycleCount(Timeline.INDEFINITE);



        /*Enemies start
        DO NOT SPAWN ENEMIES BEFORE ANYTHING OF THIS HAPPENED
         */
        setupEnemyBox();
        if (DEVELOPER_MODE) enemiesBox.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, null, new BorderWidths(1))));
        for (Enemy enemy : enemies) {
            enemiesBox.getChildren().add(enemy);
        }

        new Enemy(enemies, enemiesBox, Globals.dummySprites, "DUMMY", 0, 0);
        //Enemies end



        root.getChildren().addAll(underFB, horizontalButtonAlignment, player, fb, fb.getCurrentText(), buttons[0].getOptions(), buttons[1].getOptions(), buttons[2].getOptions(), buttons[3].getOptions(), enemiesBox);



        //OnKey start
        scene.setOnKeyPressed(event -> {
            keysPressed.add(event.getCode().toString());

            handleDeveloperModeKeys();
            //The order of these two is important. 23014: Fixed the text skip after confirming final option
            handleGoneState();
            handleMenuState();

            handleGravityState();
        });

        scene.setOnKeyReleased(event -> keysPressed.remove(event.getCode().toString()));
        //OnKey end



        // start animation timer to update everything necessary start
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (!(player.getState() == Player.State.MENU)) {
                    atLeftBorder = player.getX() < fb.getX() + player.getWidth() / 6;
                    atRightBorder = player.getX() > (fb.getX() + fb.getWidth()) - (player.getWidth() + player.getWidth() / 6);
                    atTopBorder = player.getY() < fb.getY() + player.getHeight() / 6;
                    atBottomBorder = player.getY() > (fb.getY() + fb.getHeight()) - (player.getHeight() + player.getHeight() / 6);
                    //we need the /6 to get accurate sprite border stuff

                    Boolean[] borders = {atLeftBorder, atRightBorder, atTopBorder, atBottomBorder};
                    player.updatePosition(scene.getWidth(), scene.getHeight(), borders, wTimer); //using scene.get to correspond to window size update
                    fb.updatePosition(scene.getWidth(), scene.getHeight(), player.getHeight(), player.getWidth());
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

            resize.getKeyFrames().addAll(
                    new KeyFrame(Duration.millis(0),
                            new KeyValue(fb.xProperty(), fb.getX()),
                            new KeyValue(fb.yProperty(), fb.getY()),
                            new KeyValue(fb.widthProperty(), fb.getWidth()),
                            new KeyValue(fb.heightProperty(), fb.getHeight())),
                    new KeyFrame(Duration.millis(275),
                            new KeyValue(fb.xProperty(), fbX),
                            new KeyValue(fb.yProperty(), fbY),
                            new KeyValue(fb.widthProperty(), fbWidth),
                            new KeyValue(fb.heightProperty(), fbHeight)),
                    new KeyFrame(Duration.millis(275), e -> {
                        fb.setIsResizing(false);
                        fb.setCurrentTextVisible(true);
                    })

            );
            resize.play();
        }
        else {
            fb.setCurrentTextVisible(true);
        }
    }



    public void configureUnderFBStuff() {
        Text name = new Text(player.getName());
        configureText(name);

        LVL = new Text("LV " + player.getLV());
        configureText(LVL);

        Text HP = new Text("HP");
        configureText(HP);
        HP.setFont(Globals.dtmSansHP);
        HP.setTranslateY(HP.getTranslateY()+ (Globals.dtmSans.getSize()- Globals.dtmSansHP.getSize())/2 );

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
        t.setFont(Globals.dtmSans);
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



    private void movePlayerToButton(int buttonID) {
        //You want advice? Don't touch this ever again.
        Platform.runLater(() -> player.movePlayer(buttons[buttonID].getLayoutX() + buttons[buttonID].getWidth()/6.5 - player.getWidth()/2 + horizontalButtonAlignment.getLayoutX(), horizontalButtonAlignment.getLayoutY() + buttons[0].getHeight()/2 - player.getHeight()/2));

    }



    private void movePlayerToTextOption(int buttonID) {
        Platform.runLater(() -> player.movePlayer(buttons[currentSelectedButton].getTextX(buttonID, player.getWidth()), buttons[currentSelectedButton].getTextY(buttonID, player.getHeight())));

    }



    private void movePlayerToCenter() {
        Platform.runLater(() -> player.movePlayer(fb.getX() + fb.getWidth()/2 - player.getWidth()/2, fb.getY() + fb.getHeight()/2 - player.getHeight()/2));
    }



    public void finishPlayerMove() {
        //The Platform.runLater() is ABSOLUTELY NECESSARY. DO NOT REMOVE UNDER ANY CIRCUMSTANCES
        Platform.runLater(() -> {
            fb.setCurrentTextVisible(false);
            player.setState(Player.State.NORMAL);
            player.setWentIntoButton(false);
            interactiveButton.hasSelectedEnemy = false;
            buttons[currentSelectedButton].getResetSelectedEnemy();
            deselectButtons();
            buttons[currentSelectedButton].hideOptions();
            movePlayerToCenter();
        });
    }



    public void enterMenu() {
        player.setState(Player.State.MENU);
        currentSelectedButton = 0;
        movePlayerToButton(currentSelectedButton);
        buttons[currentSelectedButton].select(buttons);
        projectileTimeline.stop();
        resetFB();
    }



    public void changePlayerLV(boolean increaseLV) {
        //For hells' sake, no, do NOT mess with this again. This is the way.
        if(increaseLV) {
            player.increaseLV();
        }
        else {
            player.decreaseLV();
        }

        player.restoreHealth(player.getMaxHealth());
        LVL.setText("LV " + player.getLV());
        curAndMaxHealth.setText(player.getCurHealth()+"/"+player.getMaxHealth());

        lostHealthBar.setWidth(player.getMaxHealth()*3);
        healthBar.setWidth(lostHealthBar.getWidth());
        curAndMaxHealth.setTranslateX(0);
        healthBars.setSpacing(-healthBar.getWidth());
    }

    public void setupEnemyBox() {
        enemiesBox = new HBox();
        enemiesBox.setSpacing(fb.getWidth()/4 - fb.getX()/2);

        enemiesBox.setMaxWidth(fb.getWidth() + fb.getStrokeWidth());
        enemiesBox.setMinWidth(fb.getWidth() + fb.getStrokeWidth());

        enemiesBox.setMinHeight(fb.getHeight());

        enemiesBox.setTranslateY((fb.getY() - fb.getHeight()) - player.getHeight()/2);
        enemiesBox.setTranslateX(fb.getX() - fb.getStrokeWidth()/2);
        enemiesBox.setAlignment(Pos.BOTTOM_CENTER);
    }



    //      --  Developer Mode Keys start    --  //

    private void handleDeveloperModeKeys() {
        if (!DEVELOPER_MODE) return;

        if (keysPressed.contains("E") || keysPressed.contains("Q")) {
            handleDEVPlayerLVChange();
        } else if (keysPressed.contains("G")) {
            handleDEVProjectileToggle();
        } else if (keysPressed.contains("SPACE")) {
            handleDEVEnterMenu();
        } else if (keysPressed.contains("TAB")) {
            handleDEVEnemySpawn();
        }
    }

        private void handleDEVPlayerLVChange() {
            if (keysPressed.contains("E")) {
                changePlayerLV(true);
            } else if (keysPressed.contains("Q")) {
                changePlayerLV(false);
            }
        }

        private void handleDEVProjectileToggle() {
            if (throwProjectiles) {
                projectileTimeline.stop();
            } else {
                projectileTimeline.play();
            }
            throwProjectiles = !throwProjectiles;
        }

        private void handleDEVEnterMenu() {
            if (player.getState() == Player.State.NORMAL) {
                enterMenu();
            } else {
                finishPlayerMove();
            }
        }

        private void handleDEVEnemySpawn() {
            Dummy spawnedEnemy = new Dummy(enemies, enemiesBox, "DUMMY "  + enemies.size(), 0, 0);

            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.25), spawnedEnemy);
            spawnedEnemy.setTranslateX(-spawnedEnemy.getWidth());
            transition.setToX(0);
            transition.play();

            for(int i=0; i<enemies.size()-1; i++) {
                TranslateTransition transition2 = new TranslateTransition(Duration.seconds(0.25), enemies.get(i));
                enemies.get(i).setTranslateX(enemies.get(i).getWidth());
                transition2.setToX(0);
                transition2.play();
            }

            if (enemiesBox.getChildren().size() > 4) {
                enemies.clear();
                enemiesBox.getChildren().clear();
                new Enemy(enemies, enemiesBox, Globals.dummySprites, "DUMMY", 0, 0);
            }
        }

    //      --  Developer Mode Keys end    --  //



    //      --  Menu State start    --  //

    private void handleMenuState() {
        if(!(player.getState() == Player.State.MENU)) return;

        /*This HAS to be in a reverse order of how it should look in game.
        If not, when choosing Enemy or Option the ButtonSelect() will also work and thus mess up which option you choose (e.g. go two to the right when you press right once).
         */

        handleMENUOptionSelect();
        handleMENUEnemySelect();
        handleMENUButtonSelect();

        handleMENUUserInputSelect();
        handleMENUUserInputBack();
    }

        private void handleMENUOptionSelect() {
            if((!interactiveButton.hasSelectedEnemy && buttons[currentSelectedButton].needsSelectedEnemy) || !player.getWentIntoButton() || currentSelectedButton==0) return;

            if(userInputTop()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.UP, player, buttons[currentSelectedButton].getOption());
            }
            else if(userInputLeft()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.LEFT, player, buttons[currentSelectedButton].getOption());
            }
            else if(userInputDown()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.DOWN, player, buttons[currentSelectedButton].getOption());
            }
            else if(userInputRight()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.RIGHT, player, buttons[currentSelectedButton].getOption());
            }
        }

        private void handleMENUEnemySelect() {
            if(!player.getWentIntoButton() || !buttons[currentSelectedButton].needsSelectedEnemy || interactiveButton.hasSelectedEnemy) return;

            if(userInputTop()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.UP, player, buttons[currentSelectedButton].getSelectedEnemy());
            }
            else if(userInputLeft()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.LEFT, player, buttons[currentSelectedButton].getSelectedEnemy());
            }
            else if(userInputDown()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.DOWN, player, buttons[currentSelectedButton].getSelectedEnemy());
            }
            else if(userInputRight()) {
                buttons[currentSelectedButton].selectInteraction(interactiveButton.Directions.RIGHT, player, buttons[currentSelectedButton].getSelectedEnemy());
            }
        }

        private void handleMENUButtonSelect() {
            if(player.getWentIntoButton()) return;

            if(userInputRight()) {
                selectButton(true);
            }
            else if(userInputLeft()) {
                selectButton(false);
            }
        }



        private void handleMENUUserInputSelect() {
            if (!userInputSelect() || fb.getIsResizing()) return;

            /*This HAS to be in a reverse order of how it should look in game. (e.g. select one of the four buttons, then enemy, then an act)
            Otherwise, it will just skip through it.
             */
            handleMENUUIConfirmFinalOption();
            handleMENUUIEnemySelect();
            handleMENUUIButtonSelect();
        }

            private void handleMENUUIConfirmFinalOption() {
                if(!player.getWentIntoButton() || (buttons[currentSelectedButton].needsSelectedEnemy && !interactiveButton.hasSelectedEnemy)) return;

                deselectButtons();
                buttons[currentSelectedButton].hideOptions();

                //Act and Item button
                if(buttons[currentSelectedButton].getWantsToReturnText()) {
                    fb.setCurrentTextVisible(true, buttons[currentSelectedButton].interact());
                    player.setState(Player.State.GONE);
                }
                //Fight and Mercy button
                else {
                    buttons[currentSelectedButton].interact();
                    fb.setCurrentTextVisible(false);
                    finishPlayerMove();
                }
            }

            private void handleMENUUIEnemySelect() {
                if(!player.getWentIntoButton() || !buttons[currentSelectedButton].needsSelectedEnemy || interactiveButton.hasSelectedEnemy) return;

                interactiveButton.hasSelectedEnemy = true;
                buttons[currentSelectedButton].actionAfterEnemySelected();
                if(currentSelectedButton!=0) movePlayerToTextOption(0);
            }

            private void handleMENUUIButtonSelect() {
                if(player.getWentIntoButton() || !(currentSelectedButton>=0)) return;

                if(currentSelectedButton==2 && itemButton.noItemsLeft()) {
                    Globals.buttonConfirmSound.play();
                    return;
                }

                buttons[currentSelectedButton].openButton();
                player.setWentIntoButton(true);
                interactiveButton.hasSelectedEnemy = false;
                fb.setCurrentTextVisible(false);
                movePlayerToTextOption(0);
            }



        private void handleMENUUserInputBack() {
            if(!userInputBack()) return;

            handleMENUUIBackReselectButton();
            handleMENUUIBackReselectEnemy();
        }

            private void handleMENUUIBackReselectEnemy() {
                if((!interactiveButton.hasSelectedEnemy && buttons[currentSelectedButton].needsSelectedEnemy) || currentSelectedButton<=0 || !buttons[currentSelectedButton].needsSelectedEnemy) return;

                interactiveButton.hasSelectedEnemy = false;
                buttons[currentSelectedButton].openButton();
                player.setWentIntoButton(true);
                fb.setCurrentTextVisible(false);
                movePlayerToTextOption(buttons[currentSelectedButton].getSelectedEnemy());
            }

            private void handleMENUUIBackReselectButton() {
                if(!player.getWentIntoButton() || (interactiveButton.hasSelectedEnemy && buttons[currentSelectedButton].needsSelectedEnemy)) return;

                player.setWentIntoButton(false);
                buttons[currentSelectedButton].getResetSelectedEnemy();
                buttons[currentSelectedButton].hideOptions();
                movePlayerToButton(currentSelectedButton);
                fb.setCurrentTextVisible(true);
            }

    //      --  Menu State end    --  //



    //      --  Gone State start    --  //

    private void handleGoneState() {
        if(!(player.getState() == Player.State.GONE)) return;

        if(userInputSelect()) {
            if(fb.hasFinishedDialog()) finishPlayerMove();
        }
        else if(userInputBack()) {
            if(!fb.hasFinishedDialog()) fb.skipDialog();
        }
    }

    //      --  Gone State end    --  //



    //      --  Gravity State start    --  //

    private void handleGravityState() {
        if(!(player.getState() == Player.State.GRAVITY)) return;

        if(userInputTop()) {
            if(!wTimer && !player.isJumping) {
                wTimer = true;
                Timeline wTimeline = new Timeline(
                        new KeyFrame(Duration.millis(600), e -> wTimer = false)
                );
                wTimeline.play();
            }
        }
    }

    //      --  Gravity State end    --  //



    //      --  User input start    --  //

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

    //      --  User input end    --  //
}