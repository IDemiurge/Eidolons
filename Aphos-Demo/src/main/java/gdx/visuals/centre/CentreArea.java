package gdx.visuals.centre;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import eidolons.content.consts.libgdx.GdxColorMaster;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.sprite.SpriteX;
import libgdx.gui.LabelX;
import libgdx.gui.generic.GroupX;
import libgdx.screens.batch.CustomSpriteBatch;
import libgdx.screens.handlers.ScreenMaster;
import logic.core.Aphos;
import main.content.enums.GenericEnums;

public class CentreArea extends GroupX {

//    SpriteX crystal;
//    SpriteX wards;
//    ChainView chain;

    SpriteX gate;
    LabelX dummyInfo;

    public CentreArea() {
//        addActor(fire = new SpriteX(""));
//        if (ScreenMaster.isFullscreen())
          //  addActor(gate = new SpriteX("sprite/core/portal hell.txt"));
//        else
          addActor("gate", gate = new SpriteX("sprite/core/white portal loop.txt"));// add pillars?
        gate.setBlending(GenericEnums.BLENDING.SCREEN);
        addActor(dummyInfo = new LabelX());
        dummyInfo.setStyle(StyleHolder.getAVQLabelStyle(30));
        dummyInfo.pack();
        setWidth(gate.getWidth());
        GdxMaster.centerWidth(dummyInfo);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (batch instanceof CustomSpriteBatch) {
            ((CustomSpriteBatch) batch).resetBlending();
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        dummyInfo.setText("" + Aphos.core.getHp());
        dummyInfo.setColor(GdxColorMaster.getColorForAtbValue(Aphos.core.getHp()));
        gate.setFps(11);// + 8 * (100 - Aphos.core.getHp()));
        //color too?
        GdxMaster.centerWidth(dummyInfo);

    }
}
