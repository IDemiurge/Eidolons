package src.main.hotkey.dialog;

import src.main.framework.C3Handler;
import src.main.framework.C3Manager;
import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.ArrayMaster;
import main.system.sound.SoundMaster;
import src.main.session.C3Session;

import java.util.Timer;
import java.util.TimerTask;

import static src.main.data.C3Enums.*;
import static src.main.data.C3Enums.C3Option.*;

public class C3DialogHandler extends C3Handler {

    public C3DialogHandler(C3Manager manager) {
        super(manager);
    }

    public static final C3Option[] topOptions =
            {Session, Task, Query};

    public void showBreakMenu(long breakTime) {
        playSound(C3Sound. PAUSE);
        String tip = getBreakTip(manager.getSessionHandler().getCurrentSession());
        Timer breakTimer = new Timer();
        TimerTask t=new TimerTask() {
            @Override
            public void run() {
                playSound(RandomWizard.random()? C3Sound.BACK_TO_WORK : C3Sound.GET_INTO_IT);
            }
        };
        breakTimer.schedule(t, breakTime);
        boolean result = DialogMaster.confirm(manager.getSessionHandler().getSessionInfo()
                + StringMaster.lineSeparator
                + "Pro Tip >>>>  " + tip + "!!!"
                + StringMaster.lineSeparator +
                "Ready to bounce back?!");
        if (result) {
            playSound(C3Sound.ONWARD);
        } else {
            showOptionsMenu();
        }

    }

    private String getBreakTip(C3Session currentSession) {
        BreakTip tip = null;
        while (true) {
            //TODO remove given tips
            tip = new EnumMaster<BreakTip>().getRandomEnumConst(BreakTip.class);
            if (!ArrayMaster.isNotEmpty(tip.directions) ||
                    ArrayMaster.contains_(tip.directions, currentSession.getDirection())) {
                break;
            }
        }
        return tip.toString();
    }

    private void playSound(C3Sound sound) {
        SoundMaster.play(sound.getPath());
    }

    public void showOptionsMenu() {

        if (DialogMaster.confirm(manager.getSessionHandler().getSessionInfo()+
                StringMaster.lineSeparator+
                "Go to functions menu?")) {
            pick(topOptions);
        }

    }

    public void pick(C3Option[] options) {
        C3Option picked = (C3Option) DialogMaster.getChosenOption("", options);
        if (picked == null) {
            return;
        }
        playSound(C3Sound.ONWARD);
        if (!ArrayMaster.isNotEmpty(picked.children)) {
            handleLeaf(picked);
        } else {
            pick(picked.children);
        }
    }

    private void handleLeaf(C3Option picked) {
        switch (picked) {
            case EZ_Choice -> {
                ezChoiceDraft(manager.getSessionHandler().getCurrentSession());
            }
            case Music_Reset -> {
                manager.getSessionHandler().resetMusic();
            }
            case Task_Report -> {
                manager.getSessionHandler().taskDone();
            }
            case New_Task -> {
                manager.getSessionHandler().addTask(main.system.util.DialogMaster.confirm("Custom src.main.task?"));
            }
        }
    }

    private void ezChoiceDraft(C3Session currentSession) {
        EZ_Option[] options = new EZ_Option[]{
                EZ_Option.comfy_chair,
                EZ_Option.query,
                EZ_Option.shift_break
        };
        EZ_Option picked = (EZ_Option) DialogMaster.getChosenOption("", options);

        switch (picked) {
            case comfy_chair -> {
                DialogMaster.inform("You got it!");
            }
            case query -> {
                manager.getQueryManager().createRandomQuery();
            }
            case shift_break -> {
                manager.getTimerHandler().shiftBreak();
            }
        }
    }

}
