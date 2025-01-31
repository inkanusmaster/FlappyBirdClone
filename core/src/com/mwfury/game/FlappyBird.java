package com.mwfury.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch; //SpriteBatch to taki handler do zarządzania animacjami
    Texture background; //Texture to obrazy.
    Texture[] birds; //Tablica tekstur bird, bo on macha skrzydłami
    Texture topTube; //Rura górna
    Texture bottomTube; //Rura dolna
    Texture gameOver; //GAME OVER MAN
    int score = 0; //Punktacja
    int scoringTube = 0; //Która rura daje punkt... Wstępnie 0
    int flapState = 0; //Sprawdzanie stanu machania skrzydłami. To indeks tablicy naszych birds
    int pause = 0; //Pauza do spowolnienia machania
    float birdY = 0; //Pozycja ptaka. Jego Y będzie się zmieniał bo będzie latał góra/dół
    float velocity = 0; //Prędkość ptaka przy lataniu góra/dół
    int gameState = 0; //Stan gry. Na początku 0, żeby po uruchomieniu ptak był na środku i dopiero po tapnięciu zaczął się ruszać.
    float gravity = 2; //Dodatkowa zmienna zwiększająca szybkość grawitacji
    float gap = 450; //Odległość między rurami
    float maxTubeOffset; //Maksykalne przesunięcie rury. Będziemy je rysować w losowych miejscach
    Random randomGenerator; //Będziemy losować gap
    float tubeVelocity = 4; //Prędkość rury. Będzie się przesuwać
    int numberOfTubes = 4; //ilość rur generowanych na ekran
    float[] tubeX = new float[numberOfTubes]; //Współrzędna X rur będzie się zmieniać jak rura będzie się poruszać. Bedziemy mieli 4 rury na ekranie więc tablica 4 współrzędnych X
    float[] tubeOffset = new float[numberOfTubes]; //Przesunięcie rury. Tablica 4 offsetów bo mamy 4 rury na ekranie.
    float distanceBetweenTubes; //Odległość pomiędzy rurami
    Circle birdCircle; //Do kolizji nakładamy na ptaka okrąg (lepszy niż kwadrat).
    ShapeRenderer shapeRenderer; //Shaperenderer... będzie nam renderował kształty na rurach i ptaku tak do sprawdzenia.
    Rectangle[] topTubeRectangles; //Do kolizji nakładamy na górne rury prostokąty.
    Rectangle[] bottomTubeRectangles; //Do kolizji nakładamy na dolne rury prostokąty.
    BitmapFont font; //Do wyświetlania punktacji na ekranie

    public void startGame() { //INICJALIZUJEMY POCZATKOWE POLOZENIA PTAKA I RUR. W METODZIE ZEBY NIE POWIELAC KODU POTEM
        birdY = Gdx.graphics.getHeight() / 2; //inicjalizujemy pozycję birdY. Na początku taka, ale będzie się zmieniać góra/dół
        //Tutaj też jest początkowe położenie rur
        for (int i = 0; i < numberOfTubes; i++) { // dla każdej pary rur generujemy X i offset
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200); //Ciężko to ogarnąć... Za każdym razem gdy tapniemy, tubeoffset przyjmie losową wartość pomiędzy 0 i 1. Hadrkorowe losowanie
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes; //współrzędne X rur dajemy na start na środek. Rysujemy kolejne rury o przesunięcie X distanceBetweenTubes.
            topTubeRectangles[i] = new Rectangle(); //Tworzymy rectangle, ale jeszcze nie przypisane do kształtu rur.
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void create() { //taka metoda oncreate jakby
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        gameOver = new Texture("gameover.png");

        birds = new Texture[2]; //tablica tekstur z ptakiem bo on macha skrzydłami (bird i bird2 więc tablica 2 elementów)
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");


        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100; //maksymalne wychylenie rury
        randomGenerator = new Random();

        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4; //szerokość ekranu /2 to odległość pomiędzy kolejnymi rurami

        shapeRenderer = new ShapeRenderer(); //inicjalizujemy shaperenderer
        birdCircle = new Circle(); //inicjalizujemy circle

        topTubeRectangles = new Rectangle[numberOfTubes]; //inicjalizujemy prostokąty dla górnych rur.
        bottomTubeRectangles = new Rectangle[numberOfTubes]; //jw. dla dolnych.

        font = new BitmapFont(); //inicjalizujemy font, który posłuży do wyświetlania punktacji
        font.setColor(Color.WHITE); //kolorek i rozmiar
        font.getData().setScale(10);
        startGame();
    }

    @Override
    public void render() { //tutaj wkółko leci ta metoda i w tej metodzie gra się wykonuje

        //rozpoczynamy rysowanie batcha
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // tło. Musi być tutaj bo potem są rury rysowane

        if (gameState == 1) { //Sprawdzamy stan gry. Na początku jest 0, ptak się nie rusza. Dopiero po kliknięciu w ekran jest 1;
            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 5) {
                score++;
                Gdx.app.log("PUNKTY!", "WARTOSC:  " + score);
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }
            if (Gdx.input.justTouched()) {  //Interakcja, czyli jak dotkniemy ekran to ten dziad ptak leci do góry, a jak nie to spada
                if (birdY < Gdx.graphics.getHeight()) { //Ażeby jak ptak wyleci do góry poza ekran żeby nie dało się klikać
                    velocity = -30;
                }
            }

            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) { //Jeśli rura górna (lub dolna) wyjdzie za już ekran...
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;  //Dodajemy do niej 4 połowy szerokości ekranu, bo tyle rur mamy
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200); //generujemy nowe rury
                } else { //Jeśli nie, to przesuwamy
                    tubeX[i] -= tubeVelocity; // przesuwanie rur o velocity
                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);//Rysujemy rury. Na parametrze Y będziemy dawali przerwę gap i losowy offset. Współrzędna X się rusza w lewo
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);//Ogarnięte. Nie trudne. Dolna rura.
                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());//Tu tworzymy rectangle na rury do kolizji. Parametr x,y jako początkowe i potem x,y końcowe czyli W i H rury
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight()); //tak samo dolna
            }

            //warunek działa jeśli birdY >0, więc zatrzyma się na dole ekranu
            if (birdY > 0) {
                //z każdą pętlą velocity się zwiększa. Pozycję Y zmniejszamy o to velocity więc ptak spada coraz szybciej.
//            velocity++;
//            birdY -= velocity;
                //a druga metoda to ze zmienną gravity:
                velocity = velocity + gravity;
                birdY -= velocity;
            } else {
                gameState = 2; //ptak spadł, koniec gry
            }
        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) { //Jeśli stan gry jest 0 i jeśli klikniesz zmień stan na 1
//            Gdx.app.log("TOUCHED!", "YES!!"); // tak się loguje w libgdx
                gameState = 1;
            }
        } else if (gameState == 2) { //jak koniec gry to rysujemy gameover.png
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
            if (Gdx.input.justTouched()) { //Jeśli stan gry jest 0 i jeśli klikniesz zmień stan na 1
                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;
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

        font.draw(batch, String.valueOf(score), 100, 200);//rysujemy na dole w lewym rogu punktację

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 5, birdY); // ale tak jest ładniej. birdY na początku jest screen/2 ale będzie się zmieniać
        batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 5 + birds[flapState].getWidth() / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2.5f); //ustawiamy kółko na pozycji ptaka
//      SHAPERENDERER SLUZY TYLKO ZEBY NARYSOWAC KSZTALTY I WYPELNIC JE DLA SPRAWDZENIA. POTEM MOZNA TO WYWALIC
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // coś jak batch. Wybieramy typ kształtu
//        shapeRenderer.setColor(Color.RED); //Czerwonym wypełniamy kółko żeby widzieć
//        shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius); //renderujemy shape kółka

        for (int i = 0; i < numberOfTubes; i++) { //pętla, w której będziemy renderować rury
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//            shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
            if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) { //sprawdzamy kolizję
//                Gdx.app.log("COLLISION!", "YES!!!"); //logowanie kolizji
                gameState = 2; //wprowadzamy nowy gamestate
            }
        }
//        shapeRenderer.end(); //kończymy shaperenderer
    }

    @Override
    public void dispose() { // tu na końcu musimy chyba zwolnić wszystkie tekstury, batche itp.
        batch.dispose();
        background.dispose();
        topTube.dispose();
        bottomTube.dispose();
        for (Texture bird : birds) { // bardziej elegancka pętla for
            bird.dispose();
        }
    }
}
