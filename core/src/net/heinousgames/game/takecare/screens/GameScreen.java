package net.heinousgames.game.takecare.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import net.heinousgames.game.takecare.helpers.BackgroundColor;
import net.heinousgames.game.takecare.helpers.Constants;
import net.heinousgames.game.takecare.Main;
import net.heinousgames.game.takecare.actors.Person;
import net.heinousgames.game.takecare.actors.Player;

public class GameScreen implements Screen {

    private Array<Person> persons;
    private boolean levelBeatenFlag;
    private float time;
    private GlyphLayout glyphLayout;
    private int level;
    private Main main;
    private OrthographicCamera camera;
    private Player player;
    private Rectangle chargeArea;
    private Stage stage;
    private Table table;

    GameScreen(final Main main, float bonusTime) {
        this.main = main;

        chargeArea = new Rectangle(480, 270, 320, 180);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.position.set(1280/2f, 720/2f, 0);
        camera.update();
        main.batch.setProjectionMatrix(camera.combined);
        main.shapeRenderer.setProjectionMatrix(camera.combined);
        stage = new Stage(new StretchViewport(1280, 720, camera));

        player = new Player(main, 1280/2f - 32, 720/2f - 64, main.me, main.dead, camera);

        stage.addActor(player);

        level = main.preferences.getInteger(Constants.LEVEL, 1);

        time = level + 9 + bonusTime;

        glyphLayout = new GlyphLayout();

        persons = new Array<>(level);

        for (int i = 0; i < level; i++) {
            Vector2 pos = newPersonPosition();
            persons.add(new Person(main, pos.x, pos.y, false, main.angry));
        }

        for (Person person : persons) {
            stage.addActor(person);
        }

        table = new Table();
        table.setSize(321, 181);
        table.setPosition(479, 270);
        BackgroundColor backgroundColor = new BackgroundColor("white_color_texture.png");
        backgroundColor.setColor(255, 255, 255, 208);
        table.setBackground(backgroundColor);

        if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
            main.assetManager.get("secret_robot_parade.ogg", Music.class).setVolume(1);
        } else {
            main.assetManager.get("secret_robot_parade.mp3", Music.class).setVolume(1);
        }
    }

    private Vector2 newPersonPosition() {
        Circle circle = new Circle(getRandomX(), getRandomY(), 32);
        if (persons != null && !persons.isEmpty()) {
            for (Person person : persons) {
                if (person.circle.overlaps(circle)) {
                    return newPersonPosition();
                }
            }
        }

        return new Vector2(circle.x, circle.y);
    }

    private float getRandomX() {
        return main.random.nextFloat() * (1280 - 64);
    }

    private float getRandomY() {
        return main.random.nextFloat() * (270 - 64);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(player);
        // google chrome and firefox don't automatically play audio without user interaction
        if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
            if (!main.assetManager.get("secret_robot_parade.ogg", Music.class).isPlaying()) {
                main.assetManager.get("secret_robot_parade.ogg", Music.class).play();
            }
        } else {
            if (!main.assetManager.get("secret_robot_parade.mp3", Music.class).isPlaying()) {
                main.assetManager.get("secret_robot_parade.mp3", Music.class).play();
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1 ,1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        glyphLayout.setText(main.fntStartScreen, String.valueOf((int)Math.ceil(time)));
        main.batch.begin();
        main.fntStartScreen.draw(main.batch, String.valueOf((int)Math.ceil(time)),
                1280/2f - glyphLayout.width/2, 720/2f + 64);
        main.batch.end();

        main.shapeRenderer.setColor(0, 0, 0, 1);
        main.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        main.shapeRenderer.rect(chargeArea.x, chargeArea.y, chargeArea.width, chargeArea.height);
        main.shapeRenderer.end();

        stage.act();
        stage.draw();

        boolean everyoneHealed = true;
        for (final Person person : persons) {

            if (!person.isHappy) {
                everyoneHealed = false;
            }

            if (!player.isDead) {
                if (person.circle.overlaps(player.circle)) {
                    if (!person.isHappy && !person.isHealing) {
                        person.isHealing = true;
                        person.addAction(Actions.sequence(Actions.delay(3), new Action() {
                            @Override
                            public boolean act(float delta) {
                                person.isHealing = false;
                                person.isHappy = true;
                                person.setDrawable(new TextureRegionDrawable(main.smile));
                                return true;
                            }
                        }));
                    }
                }
            }

            if (Intersector.overlaps(person.circle, chargeArea)) {
                if (person.getX() < chargeArea.x || person.getX() > chargeArea.x + chargeArea.width) {
                    person.velocityX *= -1;
                    if (person.getX() < chargeArea.x) {
                        person.setPosition(person.getX() - 2, person.getY());
                    } else if (person.getX() > chargeArea.x + chargeArea.width) {
                        person.setPosition(person.getX() + 2, person.getY());
                    }
                } else if (person.getY() < chargeArea.y || person.getY() < chargeArea.y + chargeArea.height) {
                    person.velocityY *= -1;
                    if (person.getY() < chargeArea.y) {
                        person.setPosition(person.getX(), person.getY() - 2);
                    } else if (person.getY() < chargeArea.y + chargeArea.height) {
                        person.setPosition(person.getX(), person.getY() + 2);
                    }
                }
            }

            for (int i = 0; i < persons.size; i++) {
                if (!person.equals(persons.get(i))) {
                    if (person.circle.overlaps(persons.get(i).circle)) {
                        person.velocityX *= -1;
                        person.velocityY *= -1;
                        person.setPosition(person.getX()-2, person.getY()-2);
                        if (!person.isHealing && !persons.get(i).isHealing
                                && (!person.isHappy || !persons.get(i).isHappy)) {
                            person.isHappy = false;
                            person.hp = 0;
                            person.setDrawable(new TextureRegionDrawable(main.angry));
                        }
                    }
                }
            }
        }

        if (!player.isDead) {
            if (everyoneHealed) {
                if (!levelBeatenFlag) {
                    levelBeatenFlag = true;
                    level++;
                    if (level > main.preferences.getInteger(Constants.HIGHEST_LEVEL, 0)) {
                        main.preferences.putInteger(Constants.HIGHEST_LEVEL, level);
                    }
                    main.preferences.putInteger(Constants.LEVEL, level).flush();
                    buildTable(true, "You Won!");
                }
            } else {
                time -= delta;
                if (time < 0) {
                    player.isDead = true;
                    player.hp = -1;
                    main.preferences.putInteger(Constants.LEVEL, 1).flush();
                    buildTable(false, "You ran out of time");
                } else {
                    if (Intersector.overlaps(player.circle, chargeArea)) {
                        player.hp += 50 * delta;
                    } else {
                        player.hp -= 25 * delta;
                        if (player.hp < 0) {
                            player.isDead = true;
                            main.preferences.putInteger(Constants.LEVEL, 1).flush();
                            buildTable(false, "You didn't take\ncare of yourself");
                        }
                    }
                }
            }
        }

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

    private void buildTable(boolean levelWon, String message) {
        Label label = new Label(message, main.lblStyleGameScreen);
        label.setAlignment(Align.center);
        TextButton btnPlay;
        TextButton btnQuit = new TextButton("QUIT", main.btnStyleGameScreen);
        if (levelWon) {
            btnPlay = new TextButton("LEVEL " + level, main.btnStyleGameScreen);
            if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
                main.assetManager.get("secret_robot_parade.ogg", Music.class).setVolume(0.1f);
            } else {
                main.assetManager.get("secret_robot_parade.mp3", Music.class).setVolume(0.1f);
            }
            table.addAction(Actions.sequence(Actions.delay(2), new Action() {
                @Override
                public boolean act(float delta) {
                    if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
                        main.assetManager.get("secret_robot_parade.ogg", Music.class).setVolume(1);
                    } else {
                        main.assetManager.get("secret_robot_parade.mp3", Music.class).setVolume(1);
                    }
                    return true;
                }
            }));
            main.assetManager.get("Jingle_Achievement_00.mp3", Sound.class).play(0.5f);
        } else {
            btnPlay = new TextButton("LEVEL 1", main.btnStyleGameScreen);
            if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
                main.assetManager.get("secret_robot_parade.ogg", Music.class).setVolume(0.1f);
            } else {
                main.assetManager.get("secret_robot_parade.mp3", Music.class).setVolume(0.1f);
            }
            table.addAction(Actions.sequence(Actions.delay(4), new Action() {
                @Override
                public boolean act(float delta) {
                    if (Gdx.app.getType() == Application.ApplicationType.WebGL && main.customMethodsCallback.canGWTPlayOgg()) {
                        main.assetManager.get("secret_robot_parade.ogg", Music.class).setVolume(1);
                    } else {
                        main.assetManager.get("secret_robot_parade.mp3", Music.class).setVolume(1);
                    }
                    return true;
                }
            }));
            main.assetManager.get("Jingle_Lose_00.mp3", Sound.class).play(0.5f);
            time = 0;
        }

        btnQuit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    main.setScreen(new StartScreen(main));
                    dispose();
                }
            }
        });

        btnPlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (event.getType() == InputEvent.Type.touchUp) {
                    main.setScreen(new GameScreen(main, time));
                    dispose();
                }
            }
        });

        table.add(label).padBottom(5).row();
        table.add(btnPlay).padBottom(5).padTop(5).row();
        table.add(btnQuit).padTop(5);

        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

}
