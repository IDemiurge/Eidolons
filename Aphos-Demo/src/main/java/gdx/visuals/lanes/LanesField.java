package gdx.visuals.lanes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import gdx.dto.LaneDto;
import gdx.dto.LaneFieldDto;
import gdx.general.AScreen;
import gdx.general.view.ADtoView;
import gdx.views.UnitView;
import libgdx.gui.generic.GroupX;
import logic.entity.Unit;
import logic.lane.LanePos;

import java.util.HashMap;
import java.util.Map;

public class LanesField extends ADtoView<LaneFieldDto> {


    private final int laneCount = LaneConsts.LANES_PER_SIDE;
    private final Lane[] lanes = new Lane[laneCount*2];
    private final static Map<LanePos, UnitView> views= new HashMap<>();

    public LanesField() {
        boolean[] sides = {true, false};

        for (boolean side : sides) {
            GroupX laneSide = new GroupX();
            int max = side?laneCount : laneCount*2;
            int i = side? 0: laneCount;
            for (; i < max; i++) {
                Lane lane = new Lane(i, side);
                laneSide.addActor(lane);
                lanes[i] = lane;
            }
            addActor(laneSide);
        }
    }

    public static Actor createView(boolean side, LanePos pos) {
        UnitView unitView = new UnitView(side, pos);
        views.put(pos, unitView);
        return unitView;
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        //use BFO to mirror?

        // Custom Render code pls!
        // actually, we can go for Actors easily here. But if we were to do heavy anims of weapons etc, some render optimization'd be needed
        // I want to be able to use setDto => update whole image
        super.draw(batch, parentAlpha);
    }

    @Override
    protected void update() {
        int i = 0;
        for (LaneDto lDto : dto.getLanes()) {
            lanes[i++].setDto(lDto);
        }
    }
    public static UnitView getView(Unit unit){
        return views.get(unit.getPos());
    }
    public static UnitView getView(LanePos pos){
        return views.get(pos);
    }
}
