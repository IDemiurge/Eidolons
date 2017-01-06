package main.libgdx.anims.phased;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import main.libgdx.texture.Texture2D;
import main.system.graphics.Animation;

import java.awt.*;

/**
 * Created by JustMe on 1/6/2017.
 */
public class PhaseAnim extends Group {


    Animation anim;
    private Texture2D texture;
    private Image image;
    int w=228; int h=228;

public PhaseAnim( Animation anim){
    this.anim=anim;
    anim.setPhaseAnim(this);
}

    public void update(){
    if (!anim.isDrawReady())
    {
        setVisible(false);
        return ;
    }
    texture=new Texture2D(w, h);
        Graphics2D g2d = texture.begin();
        
        anim.draw(g2d);
        texture.end();
        removeActor(image);
        image  = new Image(texture);
        addActor(image);
        setVisible(true);

    }

    public Animation getAnim() {
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
