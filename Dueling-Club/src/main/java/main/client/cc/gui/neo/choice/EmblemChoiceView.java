package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.content.CONTENT_CONSTS.ASPECT;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.GuiManager;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.images.ImageManager;

import java.util.LinkedList;
import java.util.List;

public class EmblemChoiceView extends PortraitChoiceView {

    public EmblemChoiceView(ChoiceSequence sequence, DC_HeroObj hero) {
        super(sequence, hero);
    }

    protected boolean isSaveHero() {
        return true;
    }

    @Override
    public String getInfo() {
        return InfoMaster.CHOOSE_EMBLEM;
    }

    @Override
    public boolean isInfoPanelNeeded() {
        return false;
    }

    @Override
    protected VISUALS getBackgroundVisuals() {
        return null;
    }

    @Override
    protected int getItemSize() {
        return GuiManager.getSmallObjSize() / 2;
    }

    @Override
    protected void initData() {
        data = new LinkedList<>();
        String heroAspect = hero.getProperty(G_PROPS.ASPECT);

        List<String> list = ImageManager.getEmblems(heroAspect);
        if (!heroAspect.equalsIgnoreCase(ASPECT.NEUTRAL.toString())) {
            data.addAll(list);
        }

        String deityAspect = hero.getDeity().getType().getProperty(G_PROPS.ASPECT);
        if (!deityAspect.equalsIgnoreCase(ASPECT.NEUTRAL.toString())) {
            if (!deityAspect.equalsIgnoreCase(heroAspect)) {
                list = ImageManager.getEmblems(deityAspect);
                data.addAll(list);
            }
        }
        data.addAll(ImageManager.getEmblems(ASPECT.NEUTRAL.toString()));
    }

    @Override
    protected void applyChoice() {
        CharacterCreator.getHeroManager().saveHero(hero);
        hero.setProperty(G_PROPS.EMBLEM, data.get(getSelectedIndex()), true);

    }

    protected boolean isVertical() {
        return false; // TODO
    }

    @Override
    protected int getPageSize() {
        return 200;
        // return 192;
    }

    @Override
    protected int getColumnsCount() {
        return 10;
    }
}
