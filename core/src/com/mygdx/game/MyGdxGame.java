package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] coinMan;
    int coinManState = 0;
    int pauseIndex = 0;
    float gravity = 0.2f;
    float velocity = 0;
    int coinManY = 0;
    Rectangle coinManRectangle;

    Texture coin;
    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    int coinCount;

    Random random;

    Texture bomb;
    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    int bombCount;

    ArrayList<Rectangle> coinRectangles = new ArrayList<>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<>();

    int score = 0;
    BitmapFont scoreFont;

    int gameState = 0;

    Texture dizzy;


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");

        coinMan = new Texture[4];
        coinMan[0] = new Texture("frame-1.png");
        coinMan[1] = new Texture("frame-2.png");
        coinMan[2] = new Texture("frame-3.png");
        coinMan[3] = new Texture("frame-4.png");
        coinManY = Gdx.graphics.getHeight() / 2;


        random = new Random();

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");

        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(10);

        dizzy = new Texture("dizzy-1.png");

    }

    //////////////////TO MAKE COINS//////////////////////
    public void makeCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();

        coinYs.add((int) height);

        coinXs.add(Gdx.graphics.getWidth());
    }

    /////////////////TO MAKE BOMBS/////////////////
    public void makebomb() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();

        bombYs.add((int) height);

        bombXs.add(Gdx.graphics.getWidth());
    }

    //////////////////////////////////////////////////////////
    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            //Game is Live

            ////TO SHOW COINS////////////////////////////////////
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 6);

                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }

            //TO SHOW BOMBS/////////////////////////////////////
            if (bombCount < 200) {
                bombCount++;
            } else {
                bombCount = 0;
                makebomb();
            }

            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 8);

                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            if (Gdx.input.justTouched()) {
                velocity = -10;
            }

            if (pauseIndex < 8) {
                pauseIndex++;
            } else {
                pauseIndex = 0;
                if (coinManState < 3) {
                    coinManState++;
                } else {
                    coinManState = 0;
                }
            }

            velocity += gravity;
            coinManY = (int) (coinManY - velocity);

            if (coinManY <= 0) {
                coinManY = 0;
            }
            //////////////////////////////////////////////////////////////////////
        } else if (gameState == 0) {
            //Game is Paused, Waiting to Start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }

        } else if (gameState == 2) {
            //Game Ended

            if (Gdx.input.justTouched()) {
                gameState = 1;
                coinManY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                coinRectangles.clear();
                coinCount = 0;
                bombXs.clear();
                bombYs.clear();
                bombRectangles.clear();
                bombCount = 0;
            }
        }




        if (gameState == 2) {
            batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - coinMan[coinManState].getWidth(), coinManY);
        } else {
            batch.draw(coinMan[coinManState], Gdx.graphics.getWidth() / 2 - coinMan[coinManState].getWidth(), coinManY);
        }


        coinManRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - coinMan[coinManState].getWidth(),
                coinManY, coinMan[coinManState].getWidth(), coinMan[coinManState].getHeight());


        ////To check Collisions//////////////////////////////////////////
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(coinManRectangle, coinRectangles.get(i))) {
                Gdx.app.log("Coin", "Collision!!");

                score++;

                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(coinManRectangle, bombRectangles.get(i))) {
                Gdx.app.log("Bomb", "Collision!!");

                gameState = 2;
            }
        }


        //Show Score/////////////
        scoreFont.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
