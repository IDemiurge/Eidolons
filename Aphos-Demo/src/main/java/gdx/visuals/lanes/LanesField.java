package gdx.visuals.lanes;

import com.badlogic.gdx.graphics.g2d.Batch;
import gdx.dto.LaneDto;
import gdx.dto.LaneFieldDto;
import gdx.general.AScreen;
import gdx.general.view.ADtoView;
import libgdx.gui.generic.GroupX;

public class LanesField extends ADtoView<LaneFieldDto> {


    private final int laneCount = LaneConsts.LANES_PER_SIDE;
    private final Lane[] lanes = new Lane[laneCount*2];

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
}
