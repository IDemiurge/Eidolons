package main.libgdx.bf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.concurrent.atomic.AtomicInteger;

public class UnitView extends BaseView {
    private static AtomicInteger lastId = new AtomicInteger(1);
    private final int curId;
    private Image arrow;
    private Image clock;
    private Image icon;
    private int baseHeight;
    private int baseWidth;
    private int arrowRotation;
    private int clockVal;
    private FrameBuffer fbo;
    private Texture portraitTexture;
    private Container<Image> imageContainer;
    private Texture clockTexture;

    public UnitView(UnitViewOptions o) {
        super(o);
        curId = lastId.getAndIncrement();
        init(o.getDirectionPointerTexture(), o.getDirectionValue(), o.getClockTexture(), o.getClockValue(), o.getPortrateTexture(), o.getIconTexture());
    }

    private void init(Texture arrowTexture, int arrowRotation, Texture clockTexture, int clockVal, Texture portraitTexture, Texture iconTexture) {
        this.arrowRotation = arrowRotation + 90;
        this.clockVal = clockVal;
        this.portraitTexture = portraitTexture;
        portrait = new Image(portraitTexture);

        baseHeight = portraitTexture.getHeight();
        baseWidth = portraitTexture.getHeight();
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());

        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
//        fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        TextureRegion textureRegion = new TextureRegion(fbo.getColorBufferTexture());
        //back = new PortraitContainer(new Image(portraitTexture));
        //addActor(back);
        imageContainer = new Container<>(new Image(textureRegion));
        imageContainer.width(getW()).height(getH()).bottom().left();
        addActor(imageContainer);

        this.clockTexture = clockTexture;

        if (arrowTexture != null) {
            arrow = new Image(arrowTexture);
            addActor(arrow);
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setY(0);
        }

        if (iconTexture != null) {
            icon = new Image(iconTexture);
            addActor(icon);
            icon.setX(0);
            icon.setY(getHeight() - icon.getImageHeight());
        }
//        setDebug(true);
    }

    @Override
    public void setScale(float scaleXY) {
        super.setScale(scaleXY);
        setHeight(baseHeight * getScaleY());
        setWidth(baseWidth * getScaleX());
        if (arrow != null) {
            arrow.setOrigin(getWidth() / 2 + arrow.getWidth(), getHeight() / 2 + arrow.getHeight());
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //setHeight(baseHeight * getScaleY());
        //setWidth(baseWidth * getScaleX());
        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            //arrow.rotateBy(1);
            arrow.setRotation(arrowRotation);
            //arrow.setOrigin(getWidth()/2 , getHeight()/2);
        }


        batch.end();
        SpriteBatch sp = new SpriteBatch(1);
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        sp.begin();
        sp.draw(portraitTexture, 0, 0, getW(), getH());
        if (clockTexture != null) {
            sp.draw(clockTexture, getW() - clockTexture.getWidth(), 0);

        }
        sp.end();
        fbo.end();

        TextureRegion textureRegion = new TextureRegion(fbo.getColorBufferTexture(), 0, 0, getW(), getH());
        textureRegion.flip(false, true);
        imageContainer.setActor(new Image(textureRegion));
        imageContainer.width(getW()).height(getH()).bottom().left().pack();


        batch.begin();
        super.draw(batch, parentAlpha);
    }

    public void updateRotation(int val) {
        if (arrow != null) {
            arrowRotation = val + 90;
            arrow.setRotation(arrowRotation);
        }
    }

    @Override
    protected void sizeChanged() {
        //setHeight(baseHeight * getScaleY());
        //setWidth(baseWidth * getScaleX());

        if (portrait != null) {
            portrait.setSize(getWidth(), getHeight());
        }

        if (imageContainer != null) {
            imageContainer.width(getWidth()).height(getHeight());
        }

        if (clock != null) {
            clock.setPosition(getWidth() - clock.getHeight(), 0);
        }

        if (icon != null) {
            icon.setPosition(0, getHeight() - icon.getImageHeight());
        }

        if (arrow != null) {
            arrow.setOrigin(arrow.getWidth() / 2, getHeight() / 2);
            arrow.setX(getWidth() / 2 - arrow.getWidth() / 2);
            arrow.setRotation(arrowRotation);
        }
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        if (touchable && this.getTouchable() != Touchable.enabled) return null;
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight() ? this : null;
    }

}
