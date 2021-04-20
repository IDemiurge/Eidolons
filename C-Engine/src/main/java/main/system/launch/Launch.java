package main.system.launch;

import main.system.GuiEventManager;
import main.system.GuiEventType;
import main.system.auxiliary.StringMaster;

import java.util.LinkedHashMap;
import java.util.Map;

import static main.system.auxiliary.log.LogMaster.important;
import static main.system.auxiliary.log.LogMaster.log;

public class Launch {
    public static final Launch instance = new Launch();
    Map<LaunchPhase, StringBuilder> errors = new LinkedHashMap<>();
    LaunchPhase phase;
    LaunchPhase lastFinished;
    StringBuilder launchLog = new StringBuilder();
    //how to sync with gdx? Gdx can just easily access this and display!

    static int N=0;

    private Launch() {
    }

    public static void END(LaunchPhase phase) {
        instance.setLastFinished(phase);
    }

    public static void START(LaunchPhase phase) {
        instance.setPhase(phase);
    }

    public enum LaunchPhase {
        _1_dc_setup,
        _2_systemInit,
        _3_optionsInit,
        _4_xml_read,
        _5_xml_init,
        _6_gdx_init,
        _7_menu_show,
        _8_dc_init,
        _9_meta_init,
        _10_level_init,
        _11_dc_start,
        _12_dc_show
        ;
        int n;
        LaunchPhase() {
            n=N++;
        }
    }

    public StringBuilder getLaunchLog() {
        return launchLog; // to file too?
    }

    public void setPhase(LaunchPhase phase) {
        this.phase = phase;
        errors.put(phase, new StringBuilder());
        printNewPhase(true, phase);
    }

    public void setLastFinished(LaunchPhase lastFinished) {
        this.lastFinished = lastFinished;
        printNewPhase(false, lastFinished);
    }

    public static void ERROR(String s) {
        instance.error(s);
    }

    public void error(String s) {
        errors.get(phase).append(s);
        important(s);
    }

    public void printNewPhase(boolean start, LaunchPhase phase) {
        log(1, StringMaster.lineSeparator + StringMaster.getStringXTimes(10, ">") + (start ? "START" : "END") +
                " PHASE: " + phase
                + StringMaster.getStringXTimes(10, "<") + StringMaster.lineSeparator);
        //how to sync with gdx?

        String update = StringMaster.wrapInBrackets(phase.toString()) + " phase "
                + (start ? "started" : "done");
        launchLog.append(update);
        if (phase.n > 2)
            GuiEventManager.trigger(GuiEventType.UPDATE_LOAD_STATUS, update);
    }

}
