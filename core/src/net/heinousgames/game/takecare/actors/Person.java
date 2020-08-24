package net.heinousgames.game.takecare.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import net.heinousgames.game.takecare.Main;

public class Person extends Image {

    public boolean isHappy, isHealing;
    public Circle circle;
    public float hp, velocityX, velocityY;
    public Main main;
    private float healTimeNeeded, countedHealTime;

    public Person(Main main, float x, float y, boolean isHappy, Texture texture) {
        super(texture);
        this.main = main;

        setPosition(x, y);
        circle = new Circle(getX()+32, getY()+32, 32);

        velocityX = 150;
        velocityY = 150;

        hp = 0;
        countedHealTime = 0;
        healTimeNeeded = 3;

        this.isHappy = isHappy;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (!isHealing) {
            setX(getX() + velocityX * delta);
            setY(getY() + velocityY * delta);
        } else {
            if (countedHealTime < healTimeNeeded) {
                countedHealTime += delta;
                hp = (countedHealTime / 3) * 100;
            } else if (countedHealTime >= healTimeNeeded) {
                countedHealTime = 0;
            }
        }

        circle.setPosition(getX() + circle.radius, getY() + circle.radius);

        if (getX() > 1280 - circle.radius*2) {
            velocityX *= -1;
            setX(1280 - circle.radius*2);
        }

        if (getX() < 0) {
            velocityX *= -1;
            setX(0);
        }

        if (getY() > 720 - circle.radius*2) {
            velocityY *= -1;
            setY(720 - circle.radius*2);
        }

        if (getY() < 0) {
            velocityY *= -1;
            setY(0);
        }

    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();

        main.shapeRenderer.setColor(0, 0, 0, 1);
        main.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        main.shapeRenderer.rect(getX(), getY() + getHeight() + 2, getWidth(), 20);
        main.shapeRenderer.end();
        main.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        main.shapeRenderer.rect(getX() + 2, getY() + getHeight() + 4, (getWidth() - 4) * hp / 100f, 16);
        main.shapeRenderer.end();

        batch.begin();
    }

}
