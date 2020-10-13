package eidolons.libgdx.gui.panels.dc.actionpanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.ObjectMap;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.generic.ValueContainer;
import eidolons.libgdx.gui.panels.TablePanel;
import main.system.launch.CoreEngine;

import java.util.Collections;
import java.util.List;

import static eidolons.libgdx.texture.TextureCache.getRegionUI_DC;

public class BaseSlotPanel extends TablePanel {
    protected final int imageSize;
    protected ObjectMap<PagesMod, TablePanel> modTableMap = new ObjectMap<>(5);

    protected PagesMod activePage = PagesMod.NONE;
    private float beforeReset;
    private boolean hovered;
    public static boolean hoveredAny;
    private boolean firstUpdateDone;

    public BaseSlotPanel(int imageSize) {
        this.imageSize = imageSize;
        left().bottom();
        setUpdateRequired(true);
    }

    @Override
    public void clear() {
        for (Actor child : GdxMaster.getAllChildren(this)) {
            child.clear();
        }
        super.clear();
        modTableMap.clear();
    }

    @Override
    protected void updateAllOnAct(float delta) {
        updateAct(delta);
        invalidate();
        afterUpdateAct(delta);
        super.setUpdateRequired(false);
    }

    @Override
    public void setUpdateRequired(boolean updateRequired) {
        super.setUpdateRequired(updateRequired);
    }

    @Override
    public void act(float delta) {
        beforeReset -= delta;
        //TODO Gdx Review
        if (hoveredAny) {
            super.setUpdateRequired(false);
        } else
        if (beforeReset <= 0) {
            beforeReset = getResetPeriod();
            super.setUpdateRequired(true);
        } else {
            super.setUpdateRequired(false);
        }
        if (!firstUpdateDone) {
            super.setUpdateRequired(true);
        }
        super.act(delta);
//        if (!updateRequired) {
//            return;
//        }
        firstUpdateDone=true;
        if (isModPages()){
        PagesMod mod = PagesMod.NONE;

            PagesMod[] pagesMods = PagesMod.getValues();
            for (int i = 0, pagesModsLength = pagesMods.length; i < pagesModsLength; i++) {
                PagesMod pagesMod = pagesMods[i];
                if (Gdx.input.isKeyPressed(pagesMod.getKeyCode())) {
                    mod = pagesMod;
                    break;
                }
            }
        if (mod != activePage) {
            if (modTableMap.containsKey(mod)) {
                setActivePage(mod);
            }
        }
        }
    }

    private boolean isModPages() {
        return false;
    }

    private float getResetPeriod() {
        if (CoreEngine.TEST_LAUNCH) {
            return 20f;
        }
        float resetPeriod = 11f;
        return resetPeriod;
    }

    protected void initContainer(List<ValueContainer> sources, String emptyImagePath) {
        int pagesCount = sources.size() / getPageSize();

        if (sources.size() % getPageSize() > 0) {
            pagesCount++;
        }

        pagesCount = Math.min(PagesMod.values().length, pagesCount);

        for (int i = 0; i < pagesCount; i++) {
            final int indx = i * getPageSize();
            final int toIndx = Math.min(sources.size(), indx + getPageSize());
            addPage(sources.subList(indx, toIndx), emptyImagePath);
        }

        if (modTableMap.size == 0) {
            addPage(Collections.EMPTY_LIST, emptyImagePath);
        }

        setActivePage(PagesMod.NONE);
    }

    protected void addValueContainer(TablePanel page, ValueContainer valueContainer,
                                     TextureRegion emptySlotTexture) {
        if (valueContainer == null) {
            valueContainer = new ValueContainer(emptySlotTexture);
        }
        if (imageSize > 0) {
            valueContainer.overrideImageSize(imageSize, imageSize);
            float scale = imageSize / valueContainer.getImageContainer().getActor().getWidth();
            valueContainer.setScale(scale, scale);
        }
        page.add(valueContainer).left().bottom().size(imageSize);

    }

    protected void addPage(List<ValueContainer> list, String emptyImagePath) {
        final TablePanel page = initPage(list, emptyImagePath);
        modTableMap.put(PagesMod.values()[modTableMap.size], page);
        addElement(page).left().bottom();

        page.setVisible(false);
        page.setTouchable(Touchable.disabled);
    }

    protected void setActivePage(PagesMod page) {
        TablePanel view = modTableMap.get(activePage);
        if (view == null) {
            if (modTableMap.size==0)
                return;
            else
                view = modTableMap.values().iterator().next();
        }
        view.setTouchable(Touchable.disabled);
        view.setVisible(false);
        activePage = page;
        modTableMap.get(activePage).setVisible(true);
        modTableMap.get(activePage).setTouchable(Touchable.enabled);

    }

    protected TablePanel initPage(List<ValueContainer> sources, String emptyImagePath) {
        TablePanel page = new TablePanel();
        for (int i = 0; i < getPageSize(); i++) {
            ValueContainer valueContainer = null;
            if (sources.size() > i)
                valueContainer = sources.get(i);
            addValueContainer(page, valueContainer, getRegionUI_DC(emptyImagePath));
        }

        return page;
    }

    protected int getPageSize() {
        return 6;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
//        Actor actor = modTableMap.getVar(activePage).hit(x, y, touchable);
//        if (actor!=null )
//            return actor;
        return super.hit(x, y, touchable);
    }


    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }

    public boolean isHovered() {
        return hovered;
    }
}
