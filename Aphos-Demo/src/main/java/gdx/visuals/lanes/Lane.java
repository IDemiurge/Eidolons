package gdx.visuals.lanes;

import gdx.dto.LaneDto;
import gdx.dto.UnitDto;
import gdx.general.view.ADtoView;
import gdx.views.UnitView;
import gdx.visuals.front.ViewManager;
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
        setHeight((600+laneSlots*150)/2);
        for (int i = laneSlots-1; i  >= 0; i--) { //zOrder!
            LanePos pos= new LanePos(this.index, i);
            UnitView unitView = new UnitView(side, pos);
            addActor(unitView);
            views.put(pos, unitView);
        }
    }

    private void initPositions() {
        for (LanePos pos : views.keySet()) {
        float x =  ViewManager.getX(pos);
        float y = getHeight() - ViewManager.getYInverse(pos);
        System.out.printf("Lane '%s', Pos '%s': x=%2.0f,y=%2.0f \n", ""+pos.lane, ""+pos.cell, x, y);
        views.get(pos).setPosition(x, y);
        }
    }

    //this would only be used... on 'new Game()'
    public void update(){
        initPositions();
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
