package eidolons.libgdx.gui.panels.headquarters;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import eidolons.content.PARAMS;
import eidolons.entity.obj.unit.Unit;
import eidolons.libgdx.GDX;
import eidolons.libgdx.anims.ActorMaster;
import eidolons.libgdx.gui.NinePatchFactory;
import eidolons.libgdx.gui.panels.TablePanel;
import eidolons.libgdx.gui.panels.headquarters.datasource.HqDataMaster;
import eidolons.libgdx.gui.panels.headquarters.datasource.hero.HqHeroDataSource;
import eidolons.libgdx.gui.panels.headquarters.hero.*;
import eidolons.libgdx.gui.panels.headquarters.party.HqPartyMembers;
import eidolons.libgdx.gui.panels.headquarters.tabs.HqTabs;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqAttributeTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqMasteryTable;
import eidolons.libgdx.gui.panels.headquarters.tabs.stats.HqNewMasteryPanel;
import eidolons.libgdx.gui.tooltips.SmartClickListener;
import eidolons.libgdx.stage.Blocking;
import eidolons.libgdx.stage.StageWithClosable;
import main.content.values.properties.G_PROPS;
import main.system.GuiEventManager;
import main.system.GuiEventType;

import java.util.List;

import static main.system.GuiEventType.RADIAL_MENU_CLOSE;

/**
 * Created by JustMe on 4/13/2018.
 */
public class HqPanel extends TablePanel implements Blocking {

    HqPartyMembers partyMembers;
    HqHeroViewPanel heroViewPanel;
    HqTabs hqTabs;
    HqHeroHeader header;
    HqHeroXp heroXp;
    HqVerticalValueTable heroValues;
    HqHeroTraits traits;
    HqControlPanel controlPanel;
    private TablePanel infoTable;
    private boolean editable;
    HqButtonPanel buttonPanel;
    private HqMasteryTable masteryTable;
    private HqAttributeTable attributeTable;
    private static HqPanel activeInstance;
    HqParamPanel dynamicParams;
    HqParamPanel staticParams;

//    PartyDataSource partyDataSource;

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setShader(null);
        super.draw(batch, parentAlpha);
    }

    public HqPanel() {

        setBackground(new NinePatchDrawable(NinePatchFactory.getLightPanelFilled()));
        partyMembers = createPartyMembers();
        hqTabs = createTabs();
        heroViewPanel = new HqHeroViewPanel();
        header = new HqHeroHeader();
        heroXp = new HqHeroXp();
        heroValues = new HqVerticalValueTable(PARAMS.LEVEL, G_PROPS.RACE, G_PROPS.DEITY);
        dynamicParams = new HqParamPanel(true);
        staticParams = new HqParamPanel(false);
        traits = new HqHeroTraits();
        controlPanel = new HqControlPanel();
        buttonPanel = new HqButtonPanel();
        infoTable = createInfoTable();
        setSize(GDX.size(1600), GDX.size(900) );
        addElements();
        addListener(new SmartClickListener(this){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Actor actor = hit(x, y, true);
                if (actor==HqPanel.this)
                {
                    GuiEventManager.trigger(RADIAL_MENU_CLOSE);
                    super.clicked(event, x, y);
                }
            }
        });
//        debugAll();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {

        Actor actor=super.hit(x, y, touchable);
            if (actor==null )
                if (isVisible())
                    return this;
        return actor;
    }

    public static void setActiveInstance(HqPanel activeInstance) {
        HqPanel.activeInstance = activeInstance;
    }

    public static HqPanel getActiveInstance() {
        return activeInstance;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void addElements() {
        left();
        add(header);
        add(partyMembers).bottom();
        row();
        add(heroViewPanel).left();
        add(infoTable).left();
        add(hqTabs);

        row();
        add(buttonPanel).colspan(3);
    }


    private TablePanel createInfoTable() {
        infoTable = new TablePanel<>();
        infoTable.top();
//        infoTable.addActor(traits);
        infoTable.add(heroValues).left();
        infoTable.add(heroXp).right(). row();
        infoTable.add(dynamicParams).center().colspan(2) . row();
        infoTable.add(staticParams).center().colspan(2).row();

          masteryTable = new HqMasteryTable();
          attributeTable = new HqAttributeTable();

        infoTable.add(attributeTable).left().top();
        //separator
        infoTable.add(masteryTable).right().top();
        masteryTable.setEditable(isEditable());
        attributeTable.setEditable(isEditable());

        HqNewMasteryPanel newMastery = new HqNewMasteryPanel();
//        newMastery.setPosition();
        infoTable.addActor(newMastery);
        infoTable.row();
        infoTable.add(controlPanel).padTop(100).bottom().center().colspan(2).row();

        infoTable.setFixedSize(true);
        infoTable.setSize(400, 800);
        return infoTable;
    }
    private HqTabs createTabs() {
        return new HqTabs();
    }

    private HqPartyMembers createPartyMembers() {
        return new HqPartyMembers(this, isVerticalPartyMembers());
    }

    private boolean isVerticalPartyMembers() {
        return false;
    }

    public void memberSelected(HqHeroDataSource source) {
        setUserObject(source);
    }
    public void memberSelected(Unit hero) {
        memberSelected(HqDataMaster.getHeroDataSource(hero));
    }

    public HqHeroDataSource getSelectedHero() {
        return getUserObject();
    }

    @Override
    public void close() {
        ActorMaster.addFadeOutAction(this, 0.3f );
        ActorMaster.addHideAfter(this );
        GuiEventManager.trigger(GuiEventType.GAME_RESUMED);
        HqPanel.setActiveInstance(null  );
        HqDataMaster.exit();
    }

    @Override
    public void open() {
        if ( ((StageWithClosable) this.getStage()).getDisplayedClosable()!=this)
            ((StageWithClosable) this.getStage()).closeDisplayed();
        ((StageWithClosable) this.getStage()).setDisplayedClosable(this);

        GuiEventManager.trigger(GuiEventType.GAME_PAUSED);
        HqPanel.setActiveInstance(this);
        fadeIn();
    }

    @Override
    public void setUserObject(Object userObject) {


        boolean first=false;
        List<HqHeroDataSource> heroes = partyMembers.getUserObject();
            if (userObject instanceof List) {
                if (heroes==null ){
                    heroes = (List<HqHeroDataSource>) userObject;
                    first = true;
                }
                userObject=((List) userObject).get(0);
        }
        if (userObject instanceof HqHeroDataSource) {
            HqHeroDataSource source = (HqHeroDataSource) userObject;
            source.setEditable(editable);
            HqMaster.setActiveHero(source.getEntity().getHero());
        }
        super.setUserObject(userObject);

        partyMembers.setUserObject(heroes);
        partyMembers.setUpdateRequired(first);
    }

    @Override
    public HqHeroDataSource getUserObject() {
        return (HqHeroDataSource) super.getUserObject();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        masteryTable.setEditable(isEditable());
        attributeTable.setEditable(isEditable());
    }

    public void modelChanged() {
        setUserObject(getUserObject());
    }
}
