package gdx.views;

import gdx.dto.EntityDto;
import gdx.general.view.ADtoView;
import libgdx.bf.generic.FadeImageContainer;

public class FieldView<T extends EntityDto> extends ADtoView<T> {

    FadeImageContainer portrait= new FadeImageContainer(); //empty mirror?
    private final boolean side;

    public FieldView(boolean side) {
        this.side = side;
    }

    @Override
    protected void update() {
        //TODO hierarchy of DTOs - good idea?
        portrait.setImage(dto.getEntity().getImagePath());
        if (!side){
            portrait.setFlipX(true);
        }
    }
}
