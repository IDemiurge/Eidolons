package libgdx.gui.dungeon.overlay;

import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.anims.actions.ActionMasterGdx;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.LabelX;
import libgdx.gui.generic.NoHitGroup;
import eidolons.content.consts.Images;

public class LargeText extends NoHitGroup {

    LabelX mainText;
    LabelX subText;

    public LargeText() {
        setSize(890, 500);
        ImageContainer bg;
        addActor(bg = new ImageContainer("ui/INK BLOTCH.png"));
        GdxMaster.center(bg);
                ImageContainer separator;
        addActor(separator = new ImageContainer(Images.SEPARATOR));
        GdxMaster.center(separator);

        addActor(mainText = new LabelX("", StyleHolder.getHugeStyle()));
        addActor(subText = new LabelX("", StyleHolder.getHugeStyle())); //TODO a little different?

        setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void show(String main, String sub, float dur){
        setVisible(true);
        float delay;
        //fade in/out one by one!
        ActionMasterGdx.addFadeInAndOutAction(this, dur, false);
        mainText.setText(main);
        subText.setText(sub);
        mainText.pack();
        subText.pack();
        GdxMaster.center(mainText);
        GdxMaster.center(subText);
        subText.setY(subText.getY()-mainText.getHeight()-10);
    }
}
