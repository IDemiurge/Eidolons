package hotkey.dialog;

import data.C3Enums;
import framework.C3Handler;
import framework.C3Manager;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;
import main.swing.generic.services.DialogMaster;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.data.ArrayMaster;
import session.C3Session;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static data.C3Enums.*;
import static data.C3Enums.C3Option.*;

public class C3DialogHandler extends C3Handler {

    public C3DialogHandler(C3Manager manager) {
        super(manager);
    }

    public static final C3Option[] topOptions =
            {Session, Task, Query};

    public void showBreakMenu() {
        String tip=getBreakTip(manager.getSessionHandler().getCurrentSession());
        boolean result = DialogMaster.confirm(manager.getSessionHandler().getSessionInfo()
               + "\nPro Tip: " + tip + "!!"
               + "\n" +
               "Ready to bounce back?!");
       if (result ){
           playSound(C3Sound.ONWARD);
       } else {
           showOptionsMenu();
       }

    }

    public enum BreakTip{
        Breath,
        Hydrate,
        Sit_Up,
        Commit(Direction.Code),
        Try_Silence,
        Stretch,
        Structure(Direction.Design, Direction.Project),
        Reality_check(Direction.Design, Direction.Project),

        ; Direction[] directions;

        BreakTip(Direction... directions) {
            this.directions = directions;
        }
    }

    private String getBreakTip(C3Session currentSession) {
        BreakTip tip=null ;
        while (true){
         tip = new EnumMaster<BreakTip>().getRandomEnumConst(BreakTip.class);
         if (!ArrayMaster.isNotEmpty(tip.directions) ||
                 ArrayMaster.contains_(tip.directions, currentSession.getDirection())){
            break;
        }
        }
        return tip.toString();
    }

    private void playSound(C3Sound onward) {
        try {
            new Player(getInputStream(onward.getPath()), getAudioDevice()).play();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    protected AudioDevice getAudioDevice()
            throws JavaLayerException {
        return FactoryRegistry.systemRegistry().createAudioDevice();
    }

    protected InputStream getInputStream(String filename)
            throws IOException {
        FileInputStream fin = new FileInputStream(filename);
        BufferedInputStream bin = new BufferedInputStream(fin);
        return bin;
    }

    public void showOptionsMenu() {
        pick(topOptions);
    }

    public void pick(C3Option[] options) {
        C3Option picked = (C3Option) DialogMaster.getChosenOption("", options);
        if (picked == null) {
            return;
        }
        if (picked.children == null) {
            handleLeaf(picked);
        } else {
            pick(picked.children);
        }
    }

    private void handleLeaf(C3Option picked) {
        switch (picked) {
            case EZ_Choice -> {
                ezChoiceDraft( manager.getSessionHandler().getCurrentSession());
            }
            case Music_Reset -> {
                manager.getSessionHandler().resetMusic();
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
                manager.getSessionHandler().shiftBreak();
            }
        }
    }

}
