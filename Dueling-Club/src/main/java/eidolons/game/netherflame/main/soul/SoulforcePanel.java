package eidolons.game.netherflame.main.soul;

import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.datasource.GraphicData;
import eidolons.libgdx.bf.generic.FadeImageContainer;
import eidolons.libgdx.gui.generic.GroupX;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SmartButton;
import eidolons.libgdx.gui.tooltips.DynamicTooltip;
import main.system.GuiEventManager;
import main.system.GuiEventType;

public class SoulforcePanel extends GroupX {
    private final FadeImageContainer bg;
    private final SmartButton lordBtn;
    private final SmartButton paleBtn;
    private final SoulCounter soulCounter;
    SoulforceBar bar;

    public SoulforcePanel() {
//        RollDecorator.RollableGroup decorated;
//        addActor(decorated = RollDecorator.decorate(
//                bg = new FadeImageContainer(Images.COLUMNS), FACING_DIRECTION.NORTH, true));
//       decorated.setRollPercentage(0.78f);
//       decorated.setRollIsLessWhenOpen(true);
//        decorated.toggle(false);
        addActor(soulCounter = new SoulCounter());
        addActor(bg = new FadeImageContainer("ui/components/dc/soulforce/background.png"));
        addActor(bar = new SoulforceBar( ));
        addActor(lordBtn = new SmartButton(ButtonStyled.STD_BUTTON.LORD_BTN, ()-> leftButton()));
        addActor(paleBtn = new SmartButton(ButtonStyled.STD_BUTTON.PALE_BTN, ()-> rightButton()));

        bar.addListener(new DynamicTooltip(() ->  SoulforceMaster.getTooltip()).getController());
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
        EidolonLord.lord.useArts();
    }
    private void rightButton() {
        GuiEventManager.trigger(GuiEventType.SHOW_HQ_SCREEN);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private void openLordPanel() {
    }
}
