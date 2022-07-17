package gdx.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import gdx.dto.HeroDto;
import libgdx.gui.LabelX;

public class HeroView extends FieldView<HeroDto> {
    private final LabelX infoLabel;

    public HeroView(boolean side) {
        this(side, null);
    }
    public HeroView(boolean side, HeroDto dto) {
        super(side);
        setDto(dto);
        addActor(infoLabel = new LabelX(""));

    }
    @Override
    protected void update() {
        super.update();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        infoLabel.setText(dto.getEntity().getPos().getCell()+"");
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
    /*
    we shouldn't use DTO for heroes... we won't be changing them all that much. OR?!

    Integrate     Eidolon Rebirth
    :: transform the same HeroView?
> Could apply some AB-based transform anim into Anphis and from into Generic Eidolon
    Movement - classic?
    Hit
     */

}
