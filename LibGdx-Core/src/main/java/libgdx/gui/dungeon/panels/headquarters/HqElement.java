package libgdx.gui.dungeon.panels.headquarters;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import libgdx.gui.dungeon.panels.TablePanelX;
import libgdx.gui.dungeon.panels.headquarters.datasource.hero.HqHeroDataSource;

/**
 * Created by JustMe on 4/16/2018.
 */
public abstract class HqElement extends TablePanelX implements HqActor{

    protected HqHeroDataSource dataSource;

    public HqElement(int width, int height) {
        super(width, height);
    }

    public HqElement() {
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
    }

    @Override
    protected Drawable getDefaultBackground() {
        return super.getDefaultBackground();
    }

    @Override
    public void updateAct(float delta) {
        dataSource = getUserObject();
        update(delta);
    }

    protected abstract void update(float delta);

    @Override
    public float getPrefWidth() {
        return getWidth();
    }
    @Override
    public float getPrefHeight() {
        return getHeight();
    }

    @Override
    public void setUserObject(Object userObject) {
        if (!(userObject instanceof HqHeroDataSource))
        {
            main.system.auxiliary.log.LogMaster.verbose( getClass().getSimpleName()+ " hq elements can't have this userObject: " +userObject);
            return;
        }
        super.setUserObject(userObject);
    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }
}
