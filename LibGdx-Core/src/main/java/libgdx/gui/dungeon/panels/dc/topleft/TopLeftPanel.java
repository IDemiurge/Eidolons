package libgdx.gui.dungeon.panels.dc.topleft;

import libgdx.GdxMaster;
import libgdx.bf.generic.ImageContainer;
import libgdx.gui.generic.GroupX;
import libgdx.gui.dungeon.panels.dc.topleft.atb.AtbPanel;
import eidolons.content.consts.Images;

public class TopLeftPanel extends GroupX {

    AtbPanel atbPanel;
    ClockPanel clockPanel= new ClockPanel();
    StatusPanel statusPanel;
    LevelInfoPanel levelInfoPanel;
    CombatOptionsPanel optionsPanel;
    ImageContainer vertical;

    public TopLeftPanel() {
        setSize(GdxMaster.getWidth(), GdxMaster.getHeight());
        addActor(vertical = new ImageContainer(Images.SEPARATOR_METAL_VERTICAL));
        addActor(optionsPanel = new CombatOptionsPanel());
        addActor(atbPanel = new AtbPanel(clockPanel));
        addActor(clockPanel  );
        addActor(statusPanel = new StatusPanel());
        addActor(levelInfoPanel = new LevelInfoPanel());

        GdxMaster.top(clockPanel);
        GdxMaster.top(atbPanel);
        GdxMaster.top(optionsPanel);
        atbPanel.setX(30);
        atbPanel.setY(atbPanel.getY()-40);

        levelInfoPanel.setPosition(0,
                GdxMaster.getHeight() - levelInfoPanel.getHeight() - atbPanel.getHeight() - 70);
        optionsPanel.setPosition(55,
                levelInfoPanel.getY() - levelInfoPanel.getHeight()+  15);


    }

    @Override
    public void act(float delta) {
        super.act(delta);
        vertical.setX(92);
        vertical.setY(optionsPanel.getY()-75);
        optionsPanel.setPosition(1,
                levelInfoPanel.getY()   -optionsPanel.getPrefHeight()+53);
    }

    protected void bindEvents() {

    }

    public AtbPanel getAtbPanel() {
        return atbPanel;
    }
}
