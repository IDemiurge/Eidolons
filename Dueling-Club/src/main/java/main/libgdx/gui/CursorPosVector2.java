package main.libgdx.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class CursorPosVector2 extends Vector2 {
    public CursorPosVector2() {
        super(Gdx.input.getX(), Gdx.input.getY());
    }
}
