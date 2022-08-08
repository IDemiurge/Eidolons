package gdx.visuals.front;

import com.badlogic.gdx.math.Vector2;
import gdx.general.AScreen;
import gdx.views.FieldView;
import gdx.views.HeroView;
import gdx.views.UnitView;
import gdx.visuals.lanes.LaneConsts;
import gdx.visuals.lanes.LanesField;
import logic.entity.Entity;
import logic.entity.Unit;
import logic.lane.HeroPos;
import logic.lane.LanePos;

import java.util.function.Consumer;

public class ViewManager {
    private static final int LANE_OFFSET_Y = 160;
    private static final int LANE_OFFSET_X = 180;
    private static final int UV_OFFSET_Y = 200;
    private static final int UV_OFFSET_X = 140;

    private static final int HV_OFFSET_Y = 200;
    private static final int HV_OFFSET_X = 155;

    public static final float[] cacheX = new float[2 * LaneConsts.LANES_PER_SIDE * LaneConsts.LANE_SLOTS];
    public static final float[] cacheY = new float[2 * LaneConsts.LANES_PER_SIDE * LaneConsts.LANE_SLOTS];
    public static final float[] heroCacheX = new float[2 * LaneConsts.CELLS_PER_SIDE];
    public static final float[] heroCacheY = new float[2 * LaneConsts.CELLS_PER_SIDE];

    public static void onViews(Consumer<FieldView> toRun) {
        HeroZone.heroes.values().forEach(view -> toRun.accept(view));
        LanesField.views.values().forEach(view -> toRun.accept(view));

    }

    //        public static List<FieldView> getViews() {
//        return ListMaster.merge(LanesField.getViews(), HeroZone.getViews());
//    }
    public static FieldView getView(Entity entity) {
        if (entity instanceof Unit) {
            return LanesField.getView((Unit) entity);
        } else {
            return HeroZone.getView(entity);
        }
    }


    public static float getHeroX(HeroPos pos) {
        int key = pos.getCell();
        if (pos.isLeftSide())
            key += LaneConsts.CELLS_PER_SIDE;
        float x = heroCacheX[key];
        if (x != 0)
            return x;
        int offsetFromCenter = pos.getCell() * HV_OFFSET_X / 2;
        if (pos.isFront()) {
            offsetFromCenter += HV_OFFSET_X / 3 * 2;
        }
        if (pos.isLeftSide()) {
            offsetFromCenter += HV_OFFSET_X + 65;
        }
        offsetFromCenter += 50;
        // it goes more or less like diagonal with ... %2
        x = AScreen.geometry.fromCenterX(offsetFromCenter, pos.isLeftSide());
        heroCacheX[key] = x;
        return x;
    }

    public static float getHeroY(HeroPos pos) {
        int key = pos.getCell();
        if (pos.isLeftSide())
            key += LaneConsts.CELLS_PER_SIDE;
        float y = heroCacheY[key];
        if (y != 0)
            return y;
        int offset = pos.getCell() * 70;
        if (!pos.isFront()) {
            offset += 60;
        }
        y = HeroZone.HEIGHT - offset;
        heroCacheY[key] = y;
        return y;
    }


    ///////////////// LANES

    public static float getY(LanePos pos) {
        int key = pos.lane * LaneConsts.LANE_SLOTS + pos.cell;
        float y = cacheY[key];
        if (y != 0)
            return y;
        y = getLaneYFromTop(pos.lane, pos.leftSide);
        y -= pos.cell * UV_OFFSET_Y;
        y = LaneConsts.LANEFIELD_HEIGHT - y;
        cacheY[key] = y;
        return y;
    }

    public static float getLaneYFromTop(int lane, boolean leftSide) {
        int posY = lane - 1;
        if (!leftSide) {
            posY -= LaneConsts.LANES_PER_SIDE;
        }
        return posY * LANE_OFFSET_Y - 70;
    }

    public static float getX(LanePos pos) {
        int key = pos.lane * LaneConsts.LANE_SLOTS + pos.cell;
        float x = cacheX[key];
        if (x != 0)
            return x;
        x = getLaneX(pos.lane, pos.leftSide) + pos.cell * UV_OFFSET_X + 20;
        x = AScreen.geometry.fromCenterX((int) x, pos.leftSide);
        cacheX[key] = x;
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
        float x = 0;
        float y = 0;
        if (view instanceof HeroView) {
            HeroPos pos = ((HeroView) view).getDto().getEntity().getPos();
            x = getHeroX(pos);
            y = getHeroY(pos);
        } else if (view instanceof UnitView) {
            LanePos pos = ((UnitView) view).getDto().getEntity().getPos();
            x = getX(pos);
            y = getY(pos);
        }
        return new Vector2(x, y);
    }

    public static Vector2 getCorePos() {
        return new Vector2(1300, 300);
    }
}
