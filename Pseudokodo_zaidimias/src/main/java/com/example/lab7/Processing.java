package com.example.lab7;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;
import static processing.awt.ShimAWT.loadImage;


public class Processing extends PApplet implements Runnable{
    final static float MOVE_SPEED = 5;
    final static float SPRITE_SCALE = 50.0f/128;
    final static float SPRITE_SIZE = 50;
    final static float GRAVITY = 0.6f;
    final static float JUMP_SPEED = 14;
    final static float WIDTH = SPRITE_SIZE * 16;
    final static float HEIGHT = SPRITE_SIZE * 12;
    final static float GROUND_LEVEL = HEIGHT - SPRITE_SIZE;

    final static float RIGHT_MARGIN = 400;
    final static float LEFT_MARGIN = 60;
    final static float VERTICAL_MARGIN = 40;
    Player p;
    PImage snow, crate, red_brick, brown_brick, gold, spider, player;
    ArrayList<Sprite> platforms;
    ArrayList<Sprite> coins;
    int map = 0;
    ArrayList<String> maps;
    boolean isGameOver = false;
    int numCoins = 0;
    float view_x = 0;
    float view_y = 0;
    float command_speed = 5;
    public void settings(){
        size(800, 600);
    }
    public void setup(){
        maps = new ArrayList<>();
        maps.add("map1.csv");
        maps.add("map2.csv");
        maps.add("map3.csv");
        imageMode(CENTER);

        numCoins = 0;
        isGameOver = false;
        view_x = 0;
        view_y = 0;
        platforms = new ArrayList<>();
        coins = new ArrayList<>();
        player = loadImage("player.png");
        gold = loadImage("gold1.png");
        red_brick = loadImage("data/red_brick.png");
        brown_brick = loadImage("data/brown_brick.png");
        crate = loadImage("data/crate.png");
        snow = loadImage("data/snow.png");

        p = new Player(this, this, player, 0.8f);
        p.setBottom(GROUND_LEVEL);
        p.center_x = 100;
        p.change_x = 0;
        p.change_y = 0;

        createPlatforms(maps.get(map));
    }

    public void draw(){
        background(255);

        scroll();

        displayAll();

        if(!isGameOver){
            updateAll();
            collectCoins();
        }



    }
    // ################# FUNKCIJOS ###################
    void displayAll(){
        p.display();
        for(Sprite s: platforms){
            s.display();
        }

        for(Sprite s: coins){
            s.display();
        }

        fill(255,0,0);
        textSize(32);
        text("Coin:" + numCoins, view_x + 50, view_y + 50);
        text("Lives:" + p.lives, view_x + 50, view_y + 100);

        if(isGameOver){
            fill(0,0,255);
            if(p.lives <= 0) {
                text("GAME OVER", view_x + width/2f - 100, view_y + height/2f);
                text("You lose", view_x + width/2f - 100, view_y + height/2f + 50);
            }
            else{
                text("You win", view_x + width/2f - 100, view_y + height/2f + 50);
                text("Press SPACE to restart", view_x + width/2f - 100, view_y + height/2f + 100);

            }

        }
    }
    void updateAll(){

        p.updateAnimation();
        resolvePlatformCollisions(p, platforms);
        for(Sprite s: coins){
            ((AnimatedSprite)s).updateAnimation();
        }
        checkDeath();
    }
    void runCode(String[] lines) throws InterruptedException {

        for(String line : lines){
            String[] values = split(line, " ");
            handle_command(values);

            resolvePlatformCollisions(p, platforms);
            sleep(100);
            p.change_x = 0;
        }
    }
    void handle_command(String[] words) throws InterruptedException {
        switch (words[0]) {
            case "move" -> handle_movement(true, !words[1].equals("left"));
            case "jump" -> handle_movement(false, false);
            case "wait" -> sleep(Integer.parseInt(words[1]));
            case "speed" -> {
                float speed = Float.parseFloat(words[1]);
                if (speed > 10) {
                    speed = 10;
                } else if (speed < 1) {
                    speed = 1;
                }
                command_speed = speed;
            }
            case "while" -> handle_while(words);
            case "if" -> handle_if(words);
        }
        if(words[0].startsWith("for(")){
            handle_for(words);
        }
        if(words.length > 2){
            String[] send = null;
            if(words[0].equals("jump") && words[1].equals("&")){
                send = Arrays.copyOfRange(words, 2, words.length);
            } else if(words[2].equals("&")){
                send = Arrays.copyOfRange(words, 3, words.length);
            }
            if(send != null){
                handle_command(send);
            }
        }
    }

    private void handle_if(String[] words) throws InterruptedException {
        switch (words[1]) {
            case "inAir", "!onGround" -> {
                if (!isOnPlatforms(p, platforms)) {
                    handle_command(Arrays.copyOfRange(words, 2, words.length));
                }
            }
            case "onGround", "!inAir" -> {
                if (isOnPlatforms(p, platforms)) {
                    handle_command(Arrays.copyOfRange(words, 2, words.length));
                }
            }
            case "collision" -> {
                if (checkWallCollision(p, platforms)) {
                    System.out.println();
                    handle_command(Arrays.copyOfRange(words, 2, words.length));
                }
            }
            case "!collision" -> {
                if (!checkWallCollision(p, platforms)) {
                    handle_command(Arrays.copyOfRange(words, 2, words.length));
                }
            }
        }
    }

    void handle_while(String[] words) throws InterruptedException {
        switch (words[1]) {
            case "coin" -> {
                int currcoin = coins.size();
                while (currcoin == coins.size()) {
                    handle_command(Arrays.copyOfRange(words, 2, words.length));
                    sleep(10);
                }
            }
            case "collision" -> {
                while (!checkWallCollision(p, platforms)) {
                    handle_command(Arrays.copyOfRange(words, 2, words.length));
                    sleep(10);
                }
            }
        }
    }
    void handle_for(String[] words) throws InterruptedException {
        String[] word = String.join(" ", words).split("[)]",2);
        word[0] = word[0].replace("for(", "");
        String[] for_words = word[0].split(";");
        for(int i = 0; i < for_words.length; i++){
            if(for_words[i].charAt(0) == ' '){
                for_words[i] = for_words[i].substring(1);
            }
        }
        int start = Integer.parseInt(for_words[0].split(" ", 3)[2]);
        int end = Integer.parseInt(for_words[1].split(" ", 3)[2]);

        String handle = word[1];
        if(handle.charAt(0) == ' '){
            handle = handle.substring(1);
        }
        String[] send = handle.split(" ");
        System.out.println(start);
        System.out.println(end);
        if(for_words[1].contains("<")){
            System.out.println("hello");
            for(int i = start; i < end;){
                handle_command(send);
                sleep(40);
                if(for_words[2].contains("++")){
                    i++;
                    System.out.println("++");
                }else{
                    i--;
                    System.out.println("++");
                }
            }
        }
        else{
            for(int i = start; i > end;){
                handle_command(send);
                sleep(40);
                if(for_words[2].contains("++")){i++;}else{i--;}
            }
        }
    }
    public void handle_movement(boolean move, boolean side){ // if move 1 - move if move 0 - jump   if side 1 - go right if side 0 go left
        if(move){
            if(side){
                p.change_x = command_speed;
            }
            else{
                p.change_x = -command_speed;
            }
        }
        else{
            if(isOnPlatforms(p, platforms)){
                p.change_y = -JUMP_SPEED;
            }
        }
    }
    void checkDeath(){
        boolean fallOffCliff = p.getBottom() > GROUND_LEVEL;
        if(fallOffCliff){
            p.lives--;
            if(p.lives <= 0){
                isGameOver = true;
            }
            else{
                p.center_x = 100;
                p.setBottom(GROUND_LEVEL);
            }
        }

    }
    void collectCoins(){
        ArrayList<Sprite> coin_list = checkCollisionList(p, coins);
        if(!coin_list.isEmpty()){
            for(Sprite coin: coin_list){
                numCoins++;
                coins.remove(coin);
            }
        }
        if(coins.isEmpty()){
            if(map == 2){
                isGameOver = true;
            }
            else{
                map++;
                setup();
            }
        }
    }

    void scroll(){
        float right_boundary = view_x + width - RIGHT_MARGIN;
        if(p.getRight() > right_boundary){
            view_x += p.getRight() - right_boundary;
        }
        float left_boundary = view_x + LEFT_MARGIN;
        if(p.getLeft() < left_boundary){
            view_x -= left_boundary - p.getLeft();
        }
        float bottom_boundary = view_y + height - VERTICAL_MARGIN;
        if(p.getBottom() > bottom_boundary){
            view_y += p.getBottom() - bottom_boundary;
        }
        float top_boundary = view_y + VERTICAL_MARGIN;
        if(p.getTop() < top_boundary){
            view_y -= top_boundary - p.getTop();
        }
        translate(-view_x, -view_y);
    }

    public boolean isOnPlatforms(Sprite s, ArrayList<Sprite> walls){
        s.center_y += 5;
        ArrayList<Sprite> col_list = checkCollisionList(s, walls);
        s.center_y -= 5;
        return !col_list.isEmpty();
    }


    public void resolvePlatformCollisions(Sprite s, ArrayList<Sprite> walls){
        s.change_y += GRAVITY;

        s.center_y += s.change_y;
        ArrayList<Sprite> col_list = checkCollisionList(s, walls);
        if(!col_list.isEmpty()){
            Sprite collided = col_list.get(0);
            if(s.change_y > 0){
                s.setBottom(collided.getTop());
            }
            else if(s.change_y < 0){
                s.setTop(collided.getBottom());
            }
            s.change_y = 0;
        }

        s.center_x += s.change_x;
        col_list = checkCollisionList(s, walls);
        if(!col_list.isEmpty()){
            Sprite collided = col_list.get(0);
            if(s.change_x > 0){
                s.setRight(collided.getLeft());
            }
            else if(s.change_x < 0){
                s.setLeft(collided.getRight());
            }
        }

    }
    public boolean checkWallCollision(Sprite s, ArrayList<Sprite> walls){

        for(Sprite p: walls){
            boolean noXOverlap = s.getRight() < p.getLeft() || s.getLeft() > p.getRight();
            if(!noXOverlap && s.getBottom() == p.getBottom()){
                return true;
            }
        }
        return false;
    }
    boolean checkCollision(Sprite s1, Sprite s2){
        boolean noXOverlap = s1.getRight() <= s2.getLeft() || s1.getLeft() >= s2.getRight();
        boolean noYOverlap = s1.getBottom() <= s2.getTop() || s1.getTop() >= s2.getBottom();
        if(noXOverlap || noYOverlap){
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Sprite> checkCollisionList(Sprite s, ArrayList<Sprite> list){
        ArrayList<Sprite> collision_list = new ArrayList<>();
        for(Sprite p: list){
            if(checkCollision(s, p))
                collision_list.add(p);
        }
        return collision_list;
    }

    void createPlatforms(String filename){
        String[] lines = loadStrings(filename);
        for(int row = 0; row <lines.length; row++){
            String[] values = split(lines[row], ";");
            for(int col = 0; col <values.length; col++){
                switch (values[col]) {
                    case "1" -> {
                        Sprite s = new Sprite(this, red_brick, SPRITE_SCALE);
                        s.center_x = SPRITE_SIZE / 2 + col * SPRITE_SIZE;
                        s.center_y = SPRITE_SIZE / 2 + row * SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "2" -> {
                        Sprite s = new Sprite(this, snow, SPRITE_SCALE);
                        s.center_x = SPRITE_SIZE / 2 + col * SPRITE_SIZE;
                        s.center_y = SPRITE_SIZE / 2 + row * SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "3" -> {
                        Sprite s = new Sprite(this, brown_brick, SPRITE_SCALE);
                        s.center_x = SPRITE_SIZE / 2 + col * SPRITE_SIZE;
                        s.center_y = SPRITE_SIZE / 2 + row * SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "4" -> {
                        Sprite s = new Sprite(this, crate, SPRITE_SCALE);
                        s.center_x = SPRITE_SIZE / 2 + col * SPRITE_SIZE;
                        s.center_y = SPRITE_SIZE / 2 + row * SPRITE_SIZE;
                        platforms.add(s);
                    }
                    case "5" -> {
                        Coin s = new Coin(this, gold, SPRITE_SCALE);
                        s.center_x = SPRITE_SIZE / 2 + col * SPRITE_SIZE;
                        s.center_y = SPRITE_SIZE / 2 + row * SPRITE_SIZE;
                        coins.add(s);
                    }
                }
            }
        }
    }

//    public void keyPressed(){
//        if(keyCode == RIGHT){
//            p.change_x = MOVE_SPEED;
//        }
//        else if(keyCode == LEFT){
//            p.change_x = -MOVE_SPEED;
//        }
//        else if(keyCode == UP && isOnPlatforms(p, platforms)){
//            p.change_y = -JUMP_SPEED;
//        }
//        else if(isGameOver && key == ' '){
//            map = 0;
//            setup();
//        }
//    }
//    public void keyReleased(){
//        if(keyCode == RIGHT){
//            p.change_x = 0;
//        } else if(keyCode == LEFT){
//            p.change_x = 0;
//        }
//    }
    @Override
    public void run() {
        String[] processingArgs = {"Processing"};
        PApplet.runSketch(processingArgs, this);
    }
}
class Sprite{
    PApplet sketch;
    PImage img;
    float center_x, center_y;
    float change_x, change_y;
    float w, h;

    public Sprite(PApplet sketch, String filename, float scale, float x, float y){
        this.sketch = sketch;
        img = loadImage(sketch, filename);
        w = img.width * scale;
        h = img.height * scale;
        center_x = x;
        center_y = y;
        change_x = 0;
        change_y = 0;
    }
    public Sprite(PApplet sketch, PImage image, float scale, float x, float y){
        this.sketch = sketch;
        img = image;
        w = img.width * scale;
        h = img.height * scale;
        center_x = x;
        center_y = y;
        change_x = 0;
        change_y = 0;
    }
    public Sprite(PApplet sketch, String filename, float scale){
        this(sketch, filename, scale, 0, 0);
    }
    public Sprite(PApplet sketch, PImage image, float scale){
        this(sketch, image, scale, 0, 0);
    }
    public void display(){
        sketch.image(img, center_x, center_y, w, h);
    }
    public void update(){
        center_x += change_x;
        center_y += change_y;
    }
    void setLeft(float left){
        center_x = left + w/2;
    }
    float getLeft(){
        return center_x - w/2;
    }
    void setRight(float right){
        center_x = right - w/2;
    }
    float getRight(){
        return center_x + w/2;
    }
    void setTop(float top){
        center_y = top + h/2;
    }
    float getTop(){
        return center_y - h/2;
    }
    void setBottom(float bottom){
        center_y = bottom - h/2;
    }
    float getBottom(){
        return center_y + h/2;
    }
}
class AnimatedSprite extends Sprite{
    final static int NEUTRAL_FACING = 0;
    final static int RIGHT_FACING = 1;
    final static int LEFT_FACING = 2;
    PImage[] currentImages;
    PImage[] standNeutral;
    PImage[] moveLeft;
    PImage[] moveRight;
    int direction;
    int index;
    int frame;
    public AnimatedSprite(PApplet sketch, PImage img, float scale){
        super(sketch, img, scale);
        direction = NEUTRAL_FACING;
        index = 0;
        frame = 0;
    }
    public void updateAnimation(){
        frame++;
        if(frame % 5 == 0){
            selectDirection();
            selectCurrentImages();
            advanceToNextImage();
        }
    }
    public void selectDirection(){
        if(change_x > 0){
            direction = RIGHT_FACING;
        }
        else if(change_x < 0){
            direction = LEFT_FACING;
        }
        else{
            direction = NEUTRAL_FACING;
        }
    }
    public void selectCurrentImages(){
        if(direction == RIGHT_FACING){
            currentImages = moveRight;
        }
        else if(direction == LEFT_FACING){
            currentImages = moveLeft;
        } else {
            currentImages = standNeutral;
        }
    }
    public void advanceToNextImage(){
        index++;
        if(index >= currentImages.length){
            index = 0;
        }
        img = currentImages[index];
    }
}
class Coin extends AnimatedSprite{
    public Coin(PApplet sketch, PImage img, float scale){
        super(sketch, img, scale);
        standNeutral = new PImage[4];
        standNeutral[0] = loadImage(super.sketch,"gold1.png");
        standNeutral[1] = loadImage(super.sketch,"gold2.png");
        standNeutral[2] = loadImage(super.sketch,"gold3.png");
        standNeutral[3] = loadImage(super.sketch,"gold4.png");
        currentImages = standNeutral;
    }
}
class Player extends AnimatedSprite{
    Processing p;
    int lives;
    boolean onPlatform, inPlace;
    PImage[] standLeft;
    PImage[] standRight;
    PImage[] jumpLeft;
    PImage[] jumpRight;
    public Player(Processing p, PApplet sketch, PImage img, float scale){
        super(sketch, img, scale);
        this.p = p;
        lives = 3;
        direction = RIGHT_FACING;
        onPlatform = false;
        inPlace = true;
        standLeft = new PImage[1];
        standLeft[0] = loadImage(super.sketch, "player_stand_left.png");
        standRight = new PImage[1];
        standRight[0] = loadImage(super.sketch, "player_stand_right.png");
        jumpLeft = new PImage[1];
        jumpLeft[0] = loadImage(super.sketch, "player_walk_left1.png");
        jumpRight = new PImage[1];
        jumpRight[0] = loadImage(super.sketch, "player_walk_right1.png");

        moveLeft = new PImage[2];
        moveLeft[0] = loadImage(super.sketch, "player_walk_left1.png");
        moveLeft[1] = loadImage(super.sketch, "player_walk_left2.png");
        moveRight = new PImage[2];
        moveRight[0] = loadImage(super.sketch, "player_walk_right1.png");
        moveRight[1] = loadImage(super.sketch, "player_walk_right2.png");
        currentImages = standRight;
    }
    @Override
    public void updateAnimation(){
        onPlatform = p.isOnPlatforms(this, p.platforms);
        inPlace = change_x == 0 && change_y == 0;
        super.updateAnimation();
    }
    @Override
    public void selectDirection(){
        if(change_x > 0){
            direction = RIGHT_FACING;
        }
        else if(change_x < 0){
            direction = LEFT_FACING;
        }
    }
    @Override
    public void selectCurrentImages(){
        if(direction == RIGHT_FACING){
            if(inPlace){
                currentImages = standRight;
            }
            else if(!onPlatform) {
                currentImages = jumpRight;
            } else {
                currentImages = moveRight;
            }
        } else if(direction == LEFT_FACING){
            if(inPlace){
                currentImages = standLeft;
            }
            else if(!onPlatform) {
                currentImages = jumpLeft;
            } else {
                currentImages = moveLeft;
            }
        }
    }
}