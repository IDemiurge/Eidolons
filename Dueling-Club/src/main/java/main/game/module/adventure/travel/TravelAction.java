package main.game.module.adventure.travel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import main.libgdx.screens.map.layers.AlphaMap.ALPHA_MAP;
import main.libgdx.screens.map.obj.PartyActor;
import main.system.auxiliary.secondary.ReflectionMaster;

/**
 * Created by JustMe on 3/14/2018.
 */
public class TravelAction extends MoveToAction {
    ALPHA_MAP[] preferredMapTypes;
    ALPHA_MAP[] disallowedMapTypes;
    ALPHA_MAP[] std_preferredMapType = new ALPHA_MAP[]{
     ALPHA_MAP.ROADS, ALPHA_MAP.PATHS, null, ALPHA_MAP.WILDERNESS,
    };
    ALPHA_MAP[] std_disallowedMapTypes = new ALPHA_MAP[]{
     ALPHA_MAP.OCEAN, ALPHA_MAP.INLAND_WATER, ALPHA_MAP.IMPASSABLE
    };
    FreeTravelMaster master;
    private boolean complete;
    private ALPHA_MAP currentMapType;
    private float destinationX;
    private float destinationY;
    private ALPHA_MAP lastMapType;

    public TravelAction(
     FreeTravelMaster master) {
        this.preferredMapTypes = std_preferredMapType;
        this.master = master;
        this.disallowedMapTypes = std_disallowedMapTypes;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        destinationX = x;
        destinationY = y;
    }

    @Override
    public PartyActor getActor() {
        return (PartyActor) super.getActor();
    }

    @Override
    public boolean act(float delta) {
        if (complete)
            return true;
        return super.act(delta);
    }

    @Override
    protected void update(float percent) {

        Vector2 pos = new Vector2(getActor().getX(), getActor().getY());
        //try to find adjacent pos that is ok

//find current alpha map?
        //only use this per time?

        for (ALPHA_MAP sub : ALPHA_MAP.values()) {
            if (search(10, sub, pos, true, false)) {
                main.system.auxiliary.log.LogMaster.log(1, getActor().getParty() +
                 " is on " + currentMapType);
                break;
            }
            if (sub == ALPHA_MAP.IMPASSABLE)
                setCurrentMapType(null);
        }
        if (currentMapType != lastMapType) {
            main.system.auxiliary.log.LogMaster.log(1, getActor().getParty() +
             " NEW TERRAIN: " + currentMapType);

            for (ALPHA_MAP sub : disallowedMapTypes) {
                if (sub == currentMapType) {
                    complete = true;
                    main.system.auxiliary.log.LogMaster.log(1, getActor().getParty() +
                     " STOPPED: " + currentMapType);
                    return;
                }
                //once per 0.1sec
//                if (search(searchRange, sub, pos, false, false)) {
//                    complete = true;
//                    main.system.auxiliary.log.LogMaster.log(1,getActor().getParty()+
//                     " STOPPED: " +currentMapType);
//                    return;
//                }
            }

//             searchRange = 5;
//            for (ALPHA_MAP sub : preferredMapTypes) {
//                if (sub == null) {
//                    super.update(percent);
//                    return;
//                }
//                if (search(searchRange, sub, pos, true, false)) {
//                    main.system.auxiliary.log.LogMaster.log(1,getActor().getParty()+
//                     " REINIT: " +currentMapType);
//                    reinit();
//                    return;
//                }
//            }
        }
        super.update(percent);

//               newPos = new Vector2(x, y);
//               cache(newPos);


//        target.setPosition(startX + (endX - startX)
//         * percent, startY + (endY - startY)
//         * percent, alignment);

    }

    public void setCurrentMapType(ALPHA_MAP currentMapType) {
        lastMapType = this.currentMapType;
        this.currentMapType = currentMapType;
    }

    private boolean search(float searchRange, ALPHA_MAP mapType, Vector2 pos,
                           boolean goTo, boolean check) {
        float x = pos.x;
        float y = pos.y;
        boolean upOnY = destinationY > y;
        boolean rightOnX = destinationX > x;
        if (!goTo) {
            rightOnX = !rightOnX;
            upOnY = !upOnY;
        }
        for (int i = 1; i <= searchRange; i++) {
            for (int j = 1; j <= searchRange; j++) {
                if (check(x, y, i, j, goTo, check, mapType, rightOnX, upOnY))
                    return true;

            }

        }
        return false;
    }

    private boolean check(float x, float y, int i, int j, boolean goTo, boolean check, ALPHA_MAP mapType, boolean rightOnX, boolean upOnY) {
        if (upOnY && rightOnX)
            if (check(mapType, x + i, y + j)) {
                if (goTo || check) {
                    getActor().setPosition(x + i, y + j);
                    setCurrentMapType(mapType);
                }
                return true;
            }
        if (!upOnY && rightOnX)
            if (check(mapType, x - i, y + j)) {
                if (goTo || check) {
                    getActor().setPosition(x - i, y + j);
                    setCurrentMapType(mapType);
                }
                return true;
            }
        if (upOnY && !rightOnX)
            if (check(mapType, x + i, y - j)) {
                if (goTo || check) {
                    getActor().setPosition(x + i, y - j);
                    setCurrentMapType(mapType);
                }
                return true;
            }
        if (!upOnY && !rightOnX)
            if (check(mapType, x - i, y - j)) {
                if (goTo || check) {
                    getActor().setPosition(x - i, y - j);
                    setCurrentMapType(mapType);
                }
                return true;
            }
        return false;
    }

    private void reinit() {

        new ReflectionMaster<>().setValue("startX", getActor().getX(), this);
        new ReflectionMaster<>().setValue("startY", getActor().getY(), this);
        new ReflectionMaster<>().setValue("time", 0, this);

//        float x = new ReflectionMaster<Float>().
//         getFieldValue("endX", this, MoveToAction.class);
//        float y = new ReflectionMaster<Float>().
//         getFieldValue("endY", this, MoveToAction.class);
//        Vector2 pos = new Vector2(getActor().getX(), getActor().getY());
//        float dur = getActor().getParty().getCoordinates().dst(
//         new Coordinates(true,(int) x,(int) y)) / getActor().getSpeed();
        //TODO adjust for terrain speed!

        float remaining = getDuration() - getTime();
        new ReflectionMaster<>().setValue("duration", remaining, this);
    }

    private boolean check(ALPHA_MAP mapType, float x, float y) {
        if (master.check(mapType, x, y)) {
            return true;
        }

        return false;
    }

}
