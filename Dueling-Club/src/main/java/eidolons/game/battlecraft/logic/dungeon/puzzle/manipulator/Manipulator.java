package eidolons.game.battlecraft.logic.dungeon.puzzle.manipulator;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.FloatAction;
import eidolons.game.battlecraft.logic.dungeon.puzzle.Puzzle;
import eidolons.libgdx.anims.sprite.SpriteX;
import eidolons.libgdx.texture.Sprites;
import main.content.DC_TYPE;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.game.bf.Coordinates;

/**
 * rotating,
 *
 * attach to a single bf object?
 * coordinate
 *
 * visibility?
 * if any of the cells is visible...
 *
 * btw, this could be same for bosses etc
 */
public class Manipulator extends GridObject {

    Manipulator_template template;
    Manipulator_type type;
    float timePeriod;
    int width;
    int height;
    private int interval;

    FloatAction floatAction;
    private float timer;
    private long cycles;
    private  float rotationTime;
    Puzzle puzzle;

    SpriteX underlay;

    public Manipulator(Puzzle puzzle, Manipulator_template template, Coordinates coordinates, String data) {
        super(coordinates, Sprites.ROTATING_ARROW );
        floatAction = new FloatAction();
        floatAction.setInterpolation(Interpolation.swing);
        this.puzzle= puzzle;

        this.template = template;
        this.type = template.type;
        interval = template.interval;
        this.height = template.height;
        this.width = template.width;
        this.timePeriod = template.period;
        this.rotationTime = template.rotationTime;


        initObject();
    }


    private void initObject() {
//        setUserObject(Eidolons.getGame().createUnit(getObjType(), getCoordinates(), DC_Player.NEUTRAL));
    }

    private ObjType getObjType() {
        return DataManager.getType("Manipulator", DC_TYPE.BF_OBJ);
    }

    @Override
    protected void init() {
        addActor(underlay = new SpriteX(Sprites.HELL_WHEEL));
        underlay.setFps(getFps());
        super.init();
        sprite.setOrigin(sprite.getWidth()/2, 0);
    }

    @Override
    protected boolean isClearshotRequired() {
        return true;
    }

    @Override
    protected double getDefaultVisionRange() {
        return 7;
    }

    @Override
    protected int getFps() {
        return 15;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        sprite.setOrigin(sprite.getWidth()/2, 0);
        sprite.setY(sprite.getHeight()/2);
        sprite.setRotation(sprite.getRotation() + (360 / rotationTime)*delta);
        timer+=delta;
        if (timer>=timePeriod) {
            timer=0;
            period();
        }

//        GearCluster gears;

    }

    private void period() {
        cycles++;
        switch (type) {
            case rotating:
                if (cycles%interval==0)
                    doAction();
                break;
        }
    }

    protected void doAction() {
        puzzle.manipulatorActs(this);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public Manipulator_type getType() {
        return type;
    }


    public enum Manipulator_template {
        rotating_cross(Manipulator_type.rotating,3, 3,  12, 4, 1),
;

        Manipulator_template(Manipulator_type type, int width, int height, float rotationTime, int interval, float period) {
            this.type = type;
            this.period = period;
            this.rotationTime = rotationTime;
            this.width = width;
            this.height = height;
            this.interval = interval;
        }

        Manipulator_type type;
        float rotationTime;
        float period;
        int interval;
        int width;
        int height;
    }
    public enum Manipulator_type {
        rotating,

    }
}
