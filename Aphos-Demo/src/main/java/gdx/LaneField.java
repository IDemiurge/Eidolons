package gdx;

import com.badlogic.gdx.graphics.g2d.Batch;
import gdx.dto.LaneFieldDto;
import libgdx.gui.generic.GroupX;

public class LaneField  extends GroupX {

    LaneFieldDto dto;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Custom Render code pls!
        // actually, we can go for Actors easily here. But if we were to do heavy anims of weapons etc, some render optimization'd be needed

        // I want to be able to use setDto => update whole image


        super.draw(batch, parentAlpha);
    }

}
