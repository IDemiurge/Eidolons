package libgdx.gui.dungeon.panels.sf_old;

import eidolons.content.consts.GraphicData;
import eidolons.netherflame.eidolon.chain.SoulforceMaster;
import libgdx.GdxMaster;
import libgdx.bf.generic.FadeImageContainer;
import libgdx.gui.generic.GroupX;
import libgdx.gui.generic.btn.ButtonStyled;
import libgdx.gui.generic.btn.SymbolButton;
import libgdx.gui.dungeon.tooltips.DynamicTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class SoulforcePanel extends GroupX {
    private final SymbolButton lordBtn;
    private final SymbolButton paleBtn;
    private final SoulCounter soulCounter;
    SoulforceBar bar;

    public SoulforcePanel() {
        addActor(soulCounter = new SoulCounter());
        FadeImageContainer bg;
        addActor(bg = new FadeImageContainer("ui/components/dc/soulforce/background.png"));
        addActor(bar = new SoulforceBar( ));
        addActor(lordBtn = new SymbolButton(ButtonStyled.STD_BUTTON.LORD_BTN, this::leftButton));
        addActor(paleBtn = new SymbolButton(ButtonStyled.STD_BUTTON.PALE_BTN, this::rightButton));

        bar.addListener(new DynamicTooltip(SoulforceMaster::getTooltip).getController());
//        bar.addListener(new DynamicTooltip(() -> "Current Soulforce: " + bar.getTooltip()).getController());
        lordBtn.addListener(new DynamicTooltip(() -> "Use Eidolon Arts").getController());
        paleBtn.addListener(new DynamicTooltip(() -> "Eidolon Lord panel").getController());

        setSize(bg.getWidth(), bg.getHeight());
        GdxMaster.center(bar);
        GdxMaster.top(bar);
        bar.setY(bar.getY()+17);

        lordBtn.setX(getWidth()/3-37);
        paleBtn.setX(getWidth()/3*2-1);
        lordBtn.setY(bar.getY()-bar.getHeight()/2+27);
        paleBtn.setY(bar.getY()-bar.getHeight()/2+28);

        GdxMaster.center(soulCounter);
        soulCounter.setY(-15);
        // soulCounter.setX(soulCounter.getX()-41);

        GuiEventManager.bind(GuiEventType.SOULFORCE_GAINED , p->{
            GuiEventManager.triggerWithParams(GuiEventType.GRID_SCREEN,
                    soulCounter, new GraphicData("alpha:0.7f;"));
        }  );
        GuiEventManager.bind(GuiEventType.SOULFORCE_LOST , p->{
            GuiEventManager.triggerWithParams(GuiEventType.GRID_SCREEN,
                    soulCounter, new GraphicData("alpha:0.7f;"));
        }  );
    }

    private void leftButton() {
        // EidolonLord.lord.useArts();
    }
    private void rightButton() {
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        lordBtn.setX(107);
        lordBtn.setY(39);
        paleBtn.setX(434);
        paleBtn.setY(39);
        soulCounter.setX(230);
        soulCounter.setY(51);soulCounter.setZIndex(3465);
    }

    private void openLordPanel() {
    }
}
