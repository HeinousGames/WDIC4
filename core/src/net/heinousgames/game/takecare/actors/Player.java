package net.heinousgames.game.takecare.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.heinousgames.game.takecare.Main;

public class Player extends Image implements InputProcessor {

    public boolean isDead;
    public Circle circle;
    public float hp;
    public Main main;
    private float speed, clickX, clickY;
    private OrthographicCamera camera;
    private Texture deadTexture;
    private Vector2 tmp;
    private Vector3 projected;

    public Player(Main main, float x, float y, Texture texture, Texture deadTexture, OrthographicCamera camera) {
        super(texture);
        this.main = main;
        this.camera = camera;
        this.deadTexture = deadTexture;

        setPosition(x, y);
        clickX = x;
        clickY = y;
        circle = new Circle(x+32, y+32, 32);

        speed = 300;

        tmp = new Vector2();

        hp = 100;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (hp < 0) {
            hp = 0;
            isDead = true;
            setDrawable(new TextureRegionDrawable(deadTexture));
        }

        if (!isDead) {
            if (hp > 100) {
                hp = 100;
            }

            // how far the player can move this frame (distance = speed * time):
            float maxDistance = speed * Gdx.graphics.getDeltaTime();

            // a vector from the player to the touch point:
            tmp.set(clickX, clickY).sub(getX(), getY());

            if (tmp.len() <= maxDistance) {// close enough to just set the player at the target
                setPosition(clickX, clickY);
            } else { // need to move along the vector toward the target
                tmp.nor().scl(maxDistance); // reduce vector length to the distance traveled this frame
                setX(getX() + tmp.x); // move rectangle by the vector length
                setY(getY() + tmp.y);
            }

            circle.setPosition(getX() + circle.radius, getY() + circle.radius);
        }
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();

        if (!isDead) {
            main.shapeRenderer.setColor(0, 0, 0, 1);
            main.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            main.shapeRenderer.rect(getX(), getY() + getHeight() + 2, getWidth(), 20);
            main.shapeRenderer.end();
            main.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            main.shapeRenderer.rect(getX() + 2, getY() + getHeight() + 4, (getWidth() - 4) * hp / 100f, 16);
            main.shapeRenderer.end();
        }

        batch.begin();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        projected = new Vector3(screenX, screenY, 0);
        camera.unproject(projected);

        clickX = projected.x - 32;
        clickY = projected.y - 32;

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        clickX = getX();
        clickY = getY();

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        projected = new Vector3(screenX, screenY, 0);
        camera.unproject(projected);

        clickX = projected.x - 32;
        clickY = projected.y - 32;

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
