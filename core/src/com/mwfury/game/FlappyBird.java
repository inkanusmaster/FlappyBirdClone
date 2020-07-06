package com.mwfury.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch; //SpriteBatch to taki handler do zarządzania animacjami
    Texture background; //Texture to obrazy.
    Texture[] birds; //Tablica tekstur bird, bo on macha skrzydłami
    int flapState = 0; //Sprawdzanie stanu machania skrzydłami. To indeks tablicy naszych birds
    int pause = 0; //Pauza do spowolnienia machania
    float birdY = 0; //Pozycja ptaka. Jego Y będzie się zmieniał bo będzie latał góra/dół
    float velocity = 0; //Prędkość ptaka przy lataniu góra/dół
    int gameState = 0; //Stan gry. Na początku 0, żeby po uruchomieniu ptak był na środku i dopiero po tapnięciu zaczął się ruszać.
    float gravity = 2; //Dodatkowa zmienna zwiększająca szybkość grawitacji

    @Override
    public void create() { //taka metoda oncreate jakby
        batch = new SpriteBatch();
        background = new Texture("bg.png");

        birds = new Texture[2]; //tablica tekstur z ptakiem bo on macha skrzydłami (bird i bird2 więc tablica 2 elementów)
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        birdY = Gdx.graphics.getHeight() / 2; //inicjalizujemy pozycję birdY. Na początku taka, ale będzie się zmieniać góra/dół
    }

    @Override
    public void render() { //tutaj wkółko leci ta metoda i w tej metodzie gra się wykonuje


        //Sprawdzamy stan gry. Na początku jest 0, ptak się nie rusza. Dopiero po kliknięciu w ekran jest 1;
        if (gameState != 0) {
            //Interakcja, czyli jak dotkniemy ekran to ten dziad ptak leci do góry, a jak nie to spada
            if (Gdx.input.justTouched()) {
//            Gdx.app.log("TOUCHED!", "YES!!"); // tak się loguje w libgdx
                velocity = -30;
            }

            //Pierwszy warunek działa jeśli birdY >0, więc zatrzyma się na dole ekranu
            //Drugi warunek OR velocity <0 pozwala nam tapnąć ptaka do góry jak leży na ziemii bo po tapnięciu zmienia nam się velocity na -30. Jakby nie było tego wyrażenia, tylko sam birdY>0 to w ogóle by tapnięcia nie brał pod uwagę.
            if (birdY > 0 || velocity < 0) {
                //z każdą pętlą velocity się zwiększa. Pozycję Y zmniejszamy o to velocity więc ptak spada coraz szybciej.
//            velocity++;
//            birdY -= velocity;
                //a druga metoda to ze zmienną gravity:
                velocity = velocity + gravity;
                birdY -= velocity;
            }


        } else { //Jeśli stan gry jest 0 i jeśli klikniesz zmień stan na 1
            if (Gdx.input.justTouched()) {
//            Gdx.app.log("TOUCHED!", "YES!!"); // tak się loguje w libgdx
                gameState = 1;
            }
        }

        //machanie skrzydłami. Zmiana stanu to zmiana indeksu tablicy birds. Opóźnienie co 6 pętli żeby spowolnić machanie
        if (pause < 6) {
            pause++;
        } else {
            pause = 0;
            if (flapState == 0) {
                flapState = 1;
            } else {
                flapState = 0;
            }
        }

        //rozpoczynamy rysowanie batcha
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // tło
//        batch.draw(bird, Gdx.graphics.getWidth() / 2 - bird.getWidth() / 2, Gdx.graphics.getHeight() / 2 - bird.getWidth() / 2); // ptak będzie na środku ekranu. cofamy w lewo i w dół o połowę rozmiaru sprita
        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 4, birdY); // ale tak jest ładniej. birdY na początku jest screen/2 ale będzie się zmieniać
        batch.end();
    }


    @Override
    public void dispose() { // tu na końcu musimy chyba zwolnić wszystkie tekstury, batche itp.
        batch.dispose();
        background.dispose();
    }
}
