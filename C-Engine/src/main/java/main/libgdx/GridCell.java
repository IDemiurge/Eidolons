package main.libgdx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class GridCell extends Group {
    protected Image backImage;
    protected Texture backTexture;
    protected String imagePath;

    public GridCell(String imagePath, Texture backTexture) {
        this.imagePath = imagePath;
        this.backTexture = backTexture;
    }

    public GridCell init(){
        backImage = new Image(backTexture);
        addActor(backImage);
        setWidth(backImage.getWidth());
        setHeight(backImage.getHeight());
        return this;
    }
}
