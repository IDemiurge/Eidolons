package gdx.views;

import gdx.dto.HeroDto;

public class HeroView extends FieldView<HeroDto> {
    public HeroView(boolean side) {
        super(side);
    }
    public HeroView(boolean side, HeroDto dto) {
        super(side);
        setDto(dto);
    }
    /*
    we shouldn't use DTO for heroes... we won't be changing them all that much. OR?!

    Integrate     Eidolon Rebirth
    :: transform the same HeroView?
> Could apply some AB-based transform anim into Anphis and from into Generic Eidolon

    Movement - classic?
    Hit
     */

    @Override
    protected void update() {
        super.update();
    }
}
