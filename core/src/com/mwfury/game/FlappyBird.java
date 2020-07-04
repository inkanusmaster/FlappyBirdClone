package com.mwfury.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch; //SpriteBatch to taki handler do zarządzania animacjami
    Texture background; //Texture to obrazy.
    Texture bird;
    Texture bird2;
    Texture bottomTube;
    Texture topTube;

    @Override
    public void create() { //taka metoda oncreate jakby
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        bird = new Texture("bird.png");
        bird2 = new Texture("bird2.png");
        bottomTube = new Texture("bottomTube.png");
        topTube = new Texture("topTube.png");
    }

    @Override
    public void render() { //tutaj wkółko leci ta metoda i w tej metodzie gra się wykonuje
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void dispose() { // tu na końcu musimy chyba zwolnić wszystkie tekstury, batche itp.
        batch.dispose();
        background.dispose();
    }
}
