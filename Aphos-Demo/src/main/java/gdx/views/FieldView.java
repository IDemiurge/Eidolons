package gdx.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import gdx.dto.EntityDto;
import gdx.general.view.ADtoView;
import libgdx.bf.generic.FadeImageContainer;

public class FieldView<T extends EntityDto> extends ADtoView<T> {

    FadeImageContainer portrait= new FadeImageContainer(); //empty mirror?
    private final boolean side;

    public FieldView(boolean side) {
        this.side = side;
        addActor(portrait);
    }

    @Override
    protected void update() {
        //TODO hierarchy of DTOs - good idea?
        portrait.setImage(dto.getEntity().getImagePath());
        if (!side){
            portrait.setFlipX(true);
        }
    }

    @Override
    public void setPosition(float x, float y) {

        System.out.printf(this+ " Pos set: x=%2.0f,y=%2.0f \n", x, y);
        super.setPosition(x, y);
    }

    @Override
    public void setX(float x) {
        System.out.printf(this+ " x=%2.0f\n", x);
        super.setX(x);
    }
    @Override
    public void setY(float y) {
        System.out.printf(this+ " y=%2.0f\n", y);
        super.setY(y);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
