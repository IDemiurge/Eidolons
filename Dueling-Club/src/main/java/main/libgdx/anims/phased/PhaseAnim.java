package main.libgdx.anims.phased;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.Texture2D;
import main.system.graphics.PhaseAnimation;

import java.awt.*;

/**
 * Created by JustMe on 1/6/2017.
 */
public class PhaseAnim extends Group {


    protected PhaseAnimation anim;
    protected int w =Gdx.graphics.getWidth(); // GuiManager.getScreenWidthInt();
    protected int h = Gdx.graphics.getHeight(); // GuiManager.getScreenHeightInt();
    protected  Texture2D texture;
    protected  Image image;
    protected boolean dirty;

    public PhaseAnim(PhaseAnimation anim) {
        this.anim = anim;
        anim.setPhaseAnim(this);
       w=Math. max(228, anim.getW());
       h=Math. max(228, anim.getH());

        anim.setThumbnail(true);
        addListener(new PhaseAnimListener(this));
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return super.hit(x, y, touchable);
    }

    @Override
    public String toString() {
        return
         getX()+" "+getY()+ " - "+
         anim.toString();
    }

    public void update() {
//        Application.postRunnable(new Runnable(){
//            public void run(){
//                sb.draw(spike.spikeLeft, x, y);
//            }
//        });
        dirty = true;
//        if (!anim.isDrawReady()) {
//            setVisible(false);
//            return;
//        }
        texture = new Texture2D(w,h);
        image = new Image(texture);
        addActor(image);
        setVisible(true);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (dirty) {
            if (texture!=null )
            texture.dispose();
            texture = new Texture2D(w, h);
            Graphics2D g2d = texture.begin();
            anim.draw(g2d);
            texture.end();
            removeActor(image);
            image = new Image(texture);
//            image.setPosition(w/2, h/2);
            addActor(image);
            dirty = false;
        }
        super.draw(batch, parentAlpha);
    }
    public PhaseAnimation getAnim() {
        return anim;
    }

    public Texture2D getTexture() {
        return texture;
    }

    public Image getImage() {
        return image;
    }


    public void setW(int w) {
        this.w = w;
    }

    public void setH(int h) {
        this.h = h;
    }
}
