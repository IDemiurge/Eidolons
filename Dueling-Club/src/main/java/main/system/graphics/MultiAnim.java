package main.system.graphics;

import main.entity.obj.Obj;
import main.system.graphics.AnimationManager.ANIM_TYPE;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public abstract class MultiAnim extends PhaseAnimation {

    protected List<PhaseAnimation> animations;

    public MultiAnim(ANIM_TYPE type, Object mainArg, Obj... targets) {
        super(type);
        this.animations = new LinkedList<>();
        for (Obj targetArg : targets) {
            animations.add(createAnimation(mainArg, targetArg));
        }
        // TODO WHAT IS TARGET? WHERE ARE THE CONTROLS/... DRAWN?
    }

    protected abstract PhaseAnimation createAnimation(Object mainArg, Obj targetArg);

    @Override
    public Object getKey() {
        // TODO Auto-generated method stub
        return super.getKey();
    }

    @Override
    public boolean draw(Graphics g) {
        for (PhaseAnimation a : animations) {
            a.draw(g);
        }
        return true;
    }

    @Override
    protected void initArea() {
        super.initArea();
    }

    @Override
    protected void drawControls() {

        super.drawControls();
    }

    @Override
    public Object getArg() {
        return null;
    }

    @Override
    public String getArgString() {
        return null;
    }

    @Override
    public ANIM clone() {
        // TODO Auto-generated method stub
        return null;
    }

}
