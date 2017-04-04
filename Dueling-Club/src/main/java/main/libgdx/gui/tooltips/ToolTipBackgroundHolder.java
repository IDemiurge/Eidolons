package main.libgdx.gui.tooltips;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import main.libgdx.texture.TextureCache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ToolTipBackgroundHolder {

    private static Lock lock = new ReentrantLock();
    private static ToolTipBackgroundHolder instance;
    private final TextureRegion top;
    private final TextureRegion middle;
    private final TextureRegion bot;
    private final TextureRegion single;
    private final Texture imageTexture;

    public ToolTipBackgroundHolder() {
        imageTexture = TextureCache.getOrCreate("UI\\components\\VALUE_BOX_BIG111.png");
        single = new TextureRegion(imageTexture, 0, 0, 240, 45);
        top = new TextureRegion(imageTexture, 0, 45, 240, 45);
        middle = new TextureRegion(imageTexture, 0, 90, 240, 45);
        bot = new TextureRegion(imageTexture, 0, 135, 240, 45);
    }

    private static ToolTipBackgroundHolder getSingleton() {
        if (instance == null) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new ToolTipBackgroundHolder();
                }
            } finally {
                lock.unlock();
            }
        }

        return instance;
    }

    public static TextureRegion getSingle() {
        return getSingleton().single;
    }

    public static TextureRegion getTop() {
        return getSingleton().top;
    }

    public static TextureRegion getMid() {
        return getSingleton().middle;
    }

    public static TextureRegion getBot() {
        return getSingleton().bot;
    }

}
