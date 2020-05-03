package eidolons.libgdx.gui.panels.dc.topleft;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.panels.dc.topleft.atb.AtbPanel;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class TopLeftPanel extends GroupX {

    AtbPanel atbPanel;
    ClockPanel clockPanel= new ClockPanel();
    StatusPanel statusPanel;
    LevelInfoPanel levelInfoPanel;
    CombatOptionsPanel optionsPanel;

    public TopLeftPanel() {
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(optionsPanel = new CombatOptionsPanel());
        addActor(atbPanel = new AtbPanel(clockPanel));
        addActor(clockPanel  );
//        addActor(statusPanel = new StatusPanel());
        addActor(levelInfoPanel = new LevelInfoPanel());

        GdxMaster.top(clockPanel);
        GdxMaster.top(atbPanel);
        GdxMaster.top(optionsPanel);
        atbPanel.setX(30);
        atbPanel.setY(atbPanel.getY()-40);

        levelInfoPanel.setPosition(15,
                GdxMaster.getHeight() - levelInfoPanel.getHeight() - atbPanel.getHeight() - 150);
        optionsPanel.setPosition(55,
                levelInfoPanel.getY() - levelInfoPanel.getHeight()+  15);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        optionsPanel.setPosition(99,
                levelInfoPanel.getY() - levelInfoPanel.getHeight() -  15);
    }

    protected void bindEvents() {

        GuiEventManager.bind(GuiEventType.PUZZLE_STARTED, p -> {
//        rollComponent(speedControlPanel, false);
    });
        GuiEventManager.bind(GuiEventType.PUZZLE_FINISHED, p -> {
//        rollComponent(speedControlPanel, true);
    });
    }
}
