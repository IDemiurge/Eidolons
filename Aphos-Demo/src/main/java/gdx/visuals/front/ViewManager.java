package gdx.visuals.front;

import gdx.dto.HeroDto;
import gdx.general.AScreen;
import gdx.views.HeroView;
import gdx.visuals.lanes.LaneConsts;
import logic.entity.Entity;
import logic.entity.Hero;
import logic.functions.GameController;
import logic.lane.HeroPos;
import logic.lane.LanePos;

public class ViewManager {
    private static final int LANE_OFFSET_Y = 120;
    private static final int LANE_OFFSET_X = 180;
    private static final int UV_OFFSET_Y = 200;
    private static final int UV_OFFSET_X = 140;

    private static final int HV_OFFSET_Y = 200;
    private static final int HV_OFFSET_X = 140;

//    public static final int[][] cacheX= new int[10][];
//    public static final int[][] cacheY= new int[10][];


    public static HeroView createView(boolean side, Entity entity) {
        HeroView heroView = new HeroView(side, new HeroDto((Hero) entity));
        GameController.getInstance().setHeroView(heroView);
        GameController.getInstance().setHero((Hero) entity);
        return heroView;
    }

    public static float getHeroYInverse(HeroPos pos) {
        int offset = pos.getCell() * 70;
        if (!pos.isFront()) {
            offset += 60;
        }
        return offset;
    }


    public static float getYInverse(LanePos pos) {
//        if (cache)
        float y = getLaneYFromTop(pos.lane);
        y -= pos.cell * UV_OFFSET_Y;
        return y;
    }

    public static float getLaneYFromTop(int lane) {
        int posY = lane;
        if (lane > LaneConsts.LANES_PER_SIDE) {
            posY -= LaneConsts.LANES_PER_SIDE;
        }
        return posY * LANE_OFFSET_Y;
    }
    ///////////////// CENTER-based

    public static float getX(LanePos pos) {
        float offset = getLaneX(pos.lane, pos.leftSide);
        boolean side = pos.leftSide;
        offset += pos.cell * UV_OFFSET_X;
        int x = AScreen.geometry.fromCenterX((int) offset, side);
        return x;
    }

    public static float getLaneX(int lane, boolean leftSide) {
        if (leftSide) {
            return LANE_OFFSET_X * (1+lane);
        }
        return LANE_OFFSET_X * (1+lane-LaneConsts.LANES_PER_SIDE);
    }
    public static float getHeroX(HeroPos pos) {
        int offset = pos.getCell() * HV_OFFSET_X/2;
        if (pos.isFront()) {
            offset += HV_OFFSET_X/2;
        }
        if (pos.isLeftSide()){
            offset += HV_OFFSET_X;
        }
        // it goes more or less like diagonal with ... %2
        int x = AScreen.geometry.fromCenterX(offset, pos.isLeftSide());
        return x;
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
}
