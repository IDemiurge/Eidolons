package gdx.visuals.lanes;

import gdx.dto.LaneDto;
import gdx.dto.UnitDto;
import gdx.general.view.ADtoView;
import gdx.views.UnitView;
import logic.entity.Unit;
import logic.lane.LanePos;

import java.util.HashMap;
import java.util.Map;

public class Lane extends ADtoView<LaneDto> {

    private final int index ;
    private final boolean side;
    private final int laneSlots = LaneConsts.LANE_SLOTS;
    Map<LanePos, UnitView> views= new HashMap<>();

    public Lane(int index, boolean side) {
        this.index = index;
        this.side = side;
        for (int i = laneSlots; i < 0; i--) { //zOrder!
            UnitView unitView = new UnitView(side);
            addActor(unitView);
            LanePos pos= new LanePos(this.index, i);
            views.put(pos, unitView);
        }
    }

    //this would only be used... on 'new Game()'
    public void update(){
        //instead of doing it via graphic events?..
        for (UnitDto dto : this.dto.getUnits()) {
            UnitView unitView = views.get(dto.getUnit().getPos());
            if (unitView == null) {
                throw new RuntimeException("Wrong unit pos in lane!" + dto);
            }
            unitView.setDto(dto);
        }
    }

    public UnitView getView(Unit unit){
        return views.get(unit.getPos());
    }
    public UnitView getView(LanePos pos){

        return views.get(pos);
    }
}
