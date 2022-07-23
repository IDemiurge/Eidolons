package gdx.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import gdx.controls.AUnitClicker;
import gdx.dto.UnitDto;
import libgdx.GdxMaster;
import libgdx.StyleHolder;
import libgdx.bf.generic.ImageContainer;
import logic.lane.LanePos;

public class UnitView extends FieldView<UnitDto> {

    private final ImageContainer frame;
    private LanePos pos;

    public UnitView(boolean side, LanePos pos) {
        super(side);
        this.pos = pos;
        frame = new ImageContainer("ui/views/uv_frame.png");
        frame.setFlipX(side);
        addActor(frame);
        frame.setZIndex(0);
        Label label;
        addActor(label=new Label(pos+"", StyleHolder.getAVQLabelStyle(18)));
        label.pack();
    }

    @Override
    public void setScale(float scaleXY) {
        frame.setScale(scaleXY);
        super.setScale(scaleXY);
    }

    @Override
    protected void update() {
        super.update();
        clearListeners();
        setTouchable(Touchable.enabled);
        addListener(new AUnitClicker(dto.getUnit()));
    }

    @Override
    protected void initAtbLabel() {
        super.initAtbLabel();
        atb.setPosition(50, 100);
    }

    @Override
    public String toString() {
        if (dto==null || dto.getUnit()==null )
            return "Empty UnitView at " + pos.lane+":"+ pos.cell;
        return "View of " + dto.getUnit();
    }
}
