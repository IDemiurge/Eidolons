package eidolons.libgdx.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.shaders.ShaderMaster;
import main.entity.Entity;
import main.system.GuiEventManager;
import main.system.GuiEventType;

/**
 * Created by JustMe on 5/24/2018.
 */
public class DragManager extends FadeImageContainer {
    private static DragManager instance;
    private Entity draggedEntity;
    private GuiStage guiStage;

    private DragManager() {
        setTouchable(Touchable.disabled);
    }

    public static DragManager getInstance() {
        if (instance==null )
            instance = new DragManager();
        return instance;
    }

    public static void setInstance(DragManager instance) {
        DragManager.instance = instance;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (parentAlpha==ShaderMaster.SUPER_DRAW)
            super.draw(batch, 1);
        else
            ShaderMaster.drawWithCustomShader(this, batch, null, false,false);
//        b.draw(draggedRegion, x, y);
    }

    protected boolean isResetImageAlways() {
        return true;
    }

    @Override
    public float getFadeDuration() {
        return 0.25f;
    }

    @Override
    protected float getFadeOutDuration() {
        return getFadeDuration();
    }

    @Override
    protected float getFadeInDuration() {
        return getFadeDuration();
    }

    @Override
    public void act(float delta) {
        checkDrawDraggedItemChanged(delta);
        if (draggedEntity != null) {
            float x = Gdx.input.getX()  - getWidth() / 3  ;//draggedOffsetX;
            float y = GdxMaster.getHeight()-
             (Gdx.input.getY() + getHeight()  );// draggedOffsetY;
            setPosition(x, y);
        }
        super.act(delta);
    }

    @Override
    public void setEmpty() {
        super.setEmpty();
    }

    @Override
    public void fadeOut() {
        super.fadeOut();
        ActorMaster.addScaleAction(this, 0, getFadeDuration());
    }

    @Override
    public void fadeIn() {
        super.fadeIn();
        ActorMaster.addScaleAction(this, 1, getFadeDuration());
    }

    public void setDraggedEntity(Entity draggedEntity) {
        if (draggedEntity == null)
            if (this.draggedEntity != null) {
                setEmpty();
                this.draggedEntity = draggedEntity;
            }

        if (draggedEntity != null)
            if (this.draggedEntity != draggedEntity)
            {
                setImage((draggedEntity.getImagePath()));
                fadeIn();
                GuiEventManager.trigger(GuiEventType.SHOW_TOOLTIP, null);
            }

        this.draggedEntity = draggedEntity;
    }

    public void checkDrawDraggedItemChanged(float delta) {
        setDraggedEntity(guiStage.getDraggedEntity());
        //additional - red cross if outside active zone
    }

    public void setGuiStage(GuiStage guiStage) {
        this.guiStage = guiStage;
    }

    public GuiStage getGuiStage() {
        return guiStage;
    }
}
