package net.heinousgames.game.takecare.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.takecare.helpers.Constants;
import net.heinousgames.game.takecare.Main;

public class StartScreen implements Screen {

    private OrthographicCamera camera;
    private Stage stage;

    public StartScreen(final Main main) {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();
        stage = new Stage(new StretchViewport(1280, 720, camera));

        Table table = new Table();
        table.setSize(1280, 720);

        Label label = new Label("But WHO\nDo I Care For?", main.lblStyleStartScreen);
        label.setAlignment(Align.center);

        Label lblLevel = new Label("Highest Level: " + main.preferences.getInteger(Constants.HIGHEST_LEVEL, 0), main.lblStyleGameScreen);
        lblLevel.setAlignment(Align.center);

        TextButton btnPlay = new TextButton("PLAY", main.btnStyleStartScreen);

        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    main.setScreen(new GameScreen(main, 0));
                    dispose();
                }
            }
        });

        Label step1 = new Label(   "Click or Drag to\nmove. " +
                                        "Charge zone\nrefills self care.", main.lblStyleGameScreen);
        step1.setAlignment(Align.center);

        Label step2 = new Label(   "Meet angry people to\nheal. " +
                                        "Angry people can\nupset happy people.", main.lblStyleGameScreen);
        step2.setAlignment(Align.center);

        Label step3 = new Label("Heal everybody\nbefore time\nruns out.", main.lblStyleGameScreen);
        step3.setAlignment(Align.center);

        table.add(label).padBottom(15).colspan(3).row();
        table.add(btnPlay).padTop(15).padBottom(15).colspan(3).row();
        table.add(lblLevel).padTop(15).padBottom(60).colspan(3).row();

        Table instructionsTable = new Table();
        instructionsTable.add(step1).padTop(60).padRight(60);
        instructionsTable.add(step2).padTop(60).padLeft(60).padRight(60);
        instructionsTable.add(step3).padTop(60).padLeft(60);

        table.add(instructionsTable).colspan(3);

        stage.addActor(table);

        if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
            main.assetManager.get("secret_robot_parade.ogg", Music.class).setVolume(1);
        } else {
            main.assetManager.get("secret_robot_parade.mp3", Music.class).setVolume(1);
        }

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1 ,1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
