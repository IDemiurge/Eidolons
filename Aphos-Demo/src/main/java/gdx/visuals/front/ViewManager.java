package gdx.visuals.front;

import com.badlogic.gdx.math.Vector2;
import gdx.dto.HeroDto;
import gdx.general.AScreen;
import gdx.views.FieldView;
import gdx.views.HeroView;
import gdx.views.UnitView;
import gdx.visuals.lanes.LaneConsts;
import gdx.visuals.lanes.LanesField;
import libgdx.screens.handlers.ScreenMaster;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.entity.Unit;
import logic.functions.GameController;
import logic.lane.HeroPos;
import logic.lane.LanePos;

public class ViewManager {
    private static final int LANE_OFFSET_Y = 160;
    private static final int LANE_OFFSET_X = 180;
    private static final int UV_OFFSET_Y = 200;
    private static final int UV_OFFSET_X = 140;

    private static final int HV_OFFSET_Y = 200;
    private static final int HV_OFFSET_X = 155;

//    public static final int[][] cacheX= new int[10][];
//    public static final int[][] cacheY= new int[10][];

    public static FieldView getView(Entity entity) {
        if (entity instanceof Unit) {
           return  LanesField.getView((Unit) entity);
        } else {
            return HeroZone.getView(entity);
        }
    }


    public static float getHeroX(HeroPos pos) {
        int offsetFromCenter = pos.getCell() * HV_OFFSET_X / 2;
        if (pos.isFront()) {
            offsetFromCenter += HV_OFFSET_X/3*2 ;
        }
        if (pos.isLeftSide()) {
            offsetFromCenter += HV_OFFSET_X+65;
        }
        offsetFromCenter+=50;
        // it goes more or less like diagonal with ... %2
        int x = AScreen.geometry.fromCenterX(offsetFromCenter, pos.isLeftSide());
        return x;
    }

    public static float getHeroYInverse(HeroPos pos) {
        int offset = pos.getCell() * 70;
        if (!pos.isFront()) {
            offset += 60;
        }
        return offset;
    }


    ///////////////// LANES

    public static float getYInverse(LanePos pos) {
//        if (cache)
        float y = getLaneYFromTop(pos.lane, pos.leftSide);
        y -= pos.cell * UV_OFFSET_Y;
        return y;
    }

    public static float getLaneYFromTop(int lane, boolean leftSide) {
        int posY = lane-1;
        if (!leftSide) {
            posY -= LaneConsts.LANES_PER_SIDE;
        }
        return posY * LANE_OFFSET_Y - 70;
    }

    public static float getX(LanePos pos) {
        float offset = getLaneX(pos.lane, pos.leftSide);
        boolean side = pos.leftSide;
        offset += pos.cell * UV_OFFSET_X+20;
        int x = AScreen.geometry.fromCenterX((int) offset, side);
        return x;
    }

    public static float getLaneX(int lane, boolean leftSide) {
        if (leftSide) {
            return LANE_OFFSET_X * (3 + lane);
        }
        return LANE_OFFSET_X * (2 + lane - LaneConsts.LANES_PER_SIDE);
    }

    public static float getScale(int pos) {
        return 1 - pos * 0.15f + pos * pos * 0.015f;
        /*
        return 1 - pos*0.2f + pos*pos*0.02f;
        1: 0.82
        2: 0.66
        3: 0.56
        4: 0.52
         */
    }

    public static Vector2 getAbsPos(FieldView view) {
        float x=0;
        float y=0;
        float height= 700;
        if (view instanceof HeroView){
            HeroPos pos = ((HeroView) view).getDto().getEntity().getPos();
            x = getHeroX(pos);
            y = getHeroYInverse(pos);
            y = height - y;
        } else
        if (view instanceof UnitView){
            LanePos pos = ((UnitView) view).getDto().getEntity().getPos();
            x = getX(pos);
            y = getYInverse(pos);
            y = height - y;
        }
        return new Vector2(x, y);
    }
}
