package main.client.cc.gui.neo.choice;

import main.client.cc.CharacterCreator;
import main.content.CONTENT_CONSTS.BACKGROUND;
import main.content.CONTENT_CONSTS.GENDER;
import main.content.CONTENT_CONSTS.SOUNDSET;
import main.content.properties.G_PROPS;
import main.entity.obj.DC_HeroObj;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;

public class PortraitChoiceView extends ChoiceView<String> {

    public PortraitChoiceView(ChoiceSequence sequence, DC_HeroObj hero) {
        super(sequence, hero);
    }

    @Override
    protected void addControls() {
        super.addControls();

    }

    protected int getPageSize() {
        return 24;
    }

    protected int getColumnsCount() {
        return 4;
    }

    @Override
    public String getInfo() {
        return InfoMaster.CHOOSE_PORTRAIT;
    }

    protected PagedSelectionPanel<String> createSelectionComponent() {
        pages = new PagedSelectionPanel<String>(this, getPageSize(), getItemSize(),
                getColumnsCount(), isVertical(), // TODO vertical
                // fix
                PagedSelectionPanel.VERSION) {
            @Override
            protected boolean isControlPosInverted() {
                return true;
            }

            public int getPanelHeight() {
                return super.getPanelWidth();
            }

            public int getPanelWidth() {
                return super.getPanelHeight();
            }
        };

        return pages;
    }

    protected String getOkButtonPos() {
        return "@id ok, pos pages.x2+width "
                + MigMaster.getCenteredHeight(okButton.getVisuals().getHeight());
    }

    protected String getBackButtonPos() {
        return "@id back, pos pages.x-width "
                + MigMaster.getCenteredHeight(backButton.getVisuals().getHeight());
    }

    protected boolean isVertical() {
        return false;
    }

    @Override
    protected void initData() {
        data = ImageManager.getPortraitsForBackground(hero.getProperty(G_PROPS.BACKGROUND));
    }

    protected boolean isSaveHero() {
        return true;
    }

    @Override
    public boolean isInfoPanelNeeded() {
        return false;
    }

    @Override
    protected void applyChoice() {
        CharacterCreator.getHeroManager().saveHero(hero);
        String portrait = data.get(getSelectedIndex());
        hero.setProperty(G_PROPS.IMAGE, portrait, true);

        boolean female = StringMaster.isFemalePortrait(portrait);

        if (female) {
            BACKGROUND bg = hero.getBackground().getFemale();
            hero.setProperty(G_PROPS.GENDER, GENDER.FEMALE.toString(), true);
            hero.setProperty(G_PROPS.BACKGROUND, bg.toString(), true);
            // int i = 0;
            // if (bg.getRace() != RACE.HUMAN || bg == BACKGROUND.STRANGER)
            // i = 1;
            // SOUNDSET soundset = bg.getSoundsets()[i]; TODO
            hero.setProperty(G_PROPS.SOUNDSET, SOUNDSET.FEMALE.toString(), true);
        } else {
            BACKGROUND bg = hero.getBackground().getMale();
            hero.setProperty(G_PROPS.GENDER, GENDER.MALE.toString(), true);
            hero.setProperty(G_PROPS.BACKGROUND, bg.toString(), true);
        }
    }

    @Override
    protected boolean isReady() {
        return false;
    }

    @Override
    public void activate() {
        initData();
        init();
    }
}
