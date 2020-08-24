package net.heinousgames.game.takecare;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.takecare.helpers.Constants;
import net.heinousgames.game.takecare.screens.StartScreen;

import java.util.Random;

public class Main extends Game {

	public AssetManager assetManager;
	public BitmapFont fntStartScreen;
	public HTMLCustomMethods customMethodsCallback;
	public Label.LabelStyle lblStyleGameScreen, lblStyleStartScreen;
	public Preferences preferences;
	public Random random;
	public ShapeRenderer shapeRenderer;
	public SpriteBatch batch;
	public Texture smile, angry, me, dead;
	public TextButton.TextButtonStyle btnStyleGameScreen, btnStyleStartScreen;
	private BitmapFont fntGameScreen;
	private boolean assetsLoaded;
	private Texture buttonUp, buttonDown;

	public interface HTMLCustomMethods {
		boolean canGWTPlayOgg();
	}

	public Main() {}

	public Main(HTMLCustomMethods customMethodsCallback) {
		this.customMethodsCallback = customMethodsCallback;
	}

	@Override
	public void create () {
		assetManager = new AssetManager();
		batch = new SpriteBatch();
		preferences = Gdx.app.getPreferences("HG_EC_TAKE_CARE");
		preferences.putInteger(Constants.LEVEL, 1).flush();
		random = new Random();
		shapeRenderer = new ShapeRenderer();

		smile = new Texture("smile.png");
		angry = new Texture("angry.png");
		me = new Texture("me.png");
		dead = new Texture("dead.png");
		buttonUp = new Texture("grey_button03.png");
		buttonDown = new Texture("grey_button04.png");

		assetManager.load("Jingle_Achievement_00.mp3", Sound.class);
		assetManager.load("Jingle_Lose_00.mp3", Sound.class);
		if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
			if (customMethodsCallback.canGWTPlayOgg()) {
				assetManager.load("secret_robot_parade.ogg", Music.class);
			} else {
				assetManager.load("secret_robot_parade.mp3", Music.class);
			}
		} else {
			assetManager.load("secret_robot_parade.mp3", Music.class);
		}

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("kenpixel_square.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

		parameter.color = Color.BLACK;

		parameter.size = 32;
		fntStartScreen = generator.generateFont(parameter);

		parameter.size = 20;
		fntGameScreen = generator.generateFont(parameter);

		generator.dispose();

		lblStyleStartScreen = new Label.LabelStyle();
		lblStyleStartScreen.font = fntStartScreen;

		lblStyleGameScreen = new Label.LabelStyle();
		lblStyleGameScreen.font = fntGameScreen;

		btnStyleStartScreen = new TextButton.TextButtonStyle();
		btnStyleStartScreen.font = fntStartScreen;
		btnStyleStartScreen.up = new TextureRegionDrawable(new TextureRegion(buttonUp));
		btnStyleStartScreen.down = new TextureRegionDrawable(new TextureRegion(buttonDown));

		btnStyleGameScreen = new TextButton.TextButtonStyle();
		btnStyleGameScreen.font = fntGameScreen;
		btnStyleGameScreen.up = new TextureRegionDrawable(new TextureRegion(buttonUp));
		btnStyleGameScreen.down = new TextureRegionDrawable(new TextureRegion(buttonDown));
	}

	@Override
	public void render () {
		super.render();

		if (assetManager.update() && !assetsLoaded) {
			assetsLoaded = true;
			if (Gdx.app.getType() == Application.ApplicationType.WebGL && customMethodsCallback.canGWTPlayOgg()) {
				assetManager.get("secret_robot_parade.ogg", Music.class).setLooping(true);
				assetManager.get("secret_robot_parade.ogg", Music.class).play();
			} else {
				assetManager.get("secret_robot_parade.mp3", Music.class).setLooping(true);
				assetManager.get("secret_robot_parade.mp3", Music.class).play();
			}
			setScreen(new StartScreen(this));
		}
	}
	
	@Override
	public void dispose () {
		assetManager.dispose();
		batch.dispose();
		shapeRenderer.dispose();
		smile.dispose();
		angry.dispose();
		me.dispose();
		dead.dispose();
		buttonUp.dispose();
		buttonDown.dispose();
		fntStartScreen.dispose();
		fntGameScreen.dispose();
	}
}
