package eidolons.libgdx.gui.generic;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.StyleHolder;
import eidolons.libgdx.anims.ActionMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.LabelX;
import eidolons.libgdx.texture.Images;

public class LargeText extends GroupX {

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
        ActionMaster.addFadeInAndOutAction(this, dur, false);
        mainText.setText(main);
        subText.setText(sub);
        GdxMaster.center(mainText);
        GdxMaster.center(subText);
        subText.setY(subText.getY()-mainText.getHeight()-10);
    }
}
