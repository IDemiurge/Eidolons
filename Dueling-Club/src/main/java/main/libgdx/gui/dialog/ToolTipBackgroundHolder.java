package main.libgdx.gui.dialog;

import com.badlogic.gdx.graphics.Pixmap;
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

    public static TextureRegion getWxH(int w, int h) {
        h = Math.max(0, h);
        w = Math.max(0, w);

        Pixmap result = new Pixmap(
                w * getSingleton().single.getRegionWidth(),
                h * getSingleton().single.getRegionHeight(),
                Pixmap.Format.RGB565);

        if (!getSingleton().imageTexture.getTextureData().isPrepared()) {
            getSingleton().imageTexture.getTextureData().prepare();
        }
        Pixmap sourcePixmap = getSingleton().imageTexture.getTextureData().consumePixmap();
        if (h == 1) {
            for (int i = 0; i < w; i++) {
                TextureRegion region = getSingleton().single;
                result.drawPixmap(sourcePixmap,
                        region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(),
                        i * region.getRegionWidth(), 0, region.getRegionWidth(), region.getRegionHeight());
            }
        } else {
            TextureRegion region = getSingleton().top;
            for (int i = 0; i < w; i++) {
                result.drawPixmap(sourcePixmap,
                        region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(),
                        i * region.getRegionWidth(), 0, region.getRegionWidth(), region.getRegionHeight());
            }

            region = getSingleton().middle;
            for (int x = 1; x < w - 1; x++) {
                for (int y = 1; y < h - 1; y++) {
                    result.drawPixmap(sourcePixmap,
                            region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(),
                            x * region.getRegionWidth(), y * region.getRegionHeight(), region.getRegionWidth(), region.getRegionHeight());
                }
            }

            region = getSingleton().bot;
            for (int i = 0; i < w; i++) {
                result.drawPixmap(sourcePixmap,
                        region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(),
                        i * region.getRegionWidth(), (h - 1) * region.getRegionHeight(), region.getRegionWidth(), region.getRegionHeight());
            }
        }

        return new TextureRegion(new Texture(result));
    }
}
