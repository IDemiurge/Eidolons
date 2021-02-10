package eidolons.system.libgdx;

import eidolons.system.libgdx.api.*;

/**
 * create from LibGdx launcher, managed there - we will set screen and stuff
 *
 * Eidolons static class
 * List functions:
 *
 * custom adapters
 * - GridCommentHandler
 *
 */
public class GdxAdapter {
    ScreenApi map;
    ScreenApi dungeon;
    ScreenApi screen; //our real screens will impl this interface
    ControllerApi controller; //apart from dungeon controller, ...?
    GdxApi gdxApp;
    GdxManagerApi manager; // helper funcs
    GdxEvents events;
    GdxAudio audio;
    GdxOptions options;

    private static GdxAdapter instance;

    private GdxAdapter() {
    }

    public static GdxAdapter getInstance() {
        if (instance == null) {
            instance = new GdxAdapter();
        }
        return instance;
    }

    public void setInstance(GdxAdapter instance) {
        this.instance = instance;
    }

    public static void onInputGdx(Runnable r) {
        instance.onInputGdx(r);
    }

    public void onInputGdx(Runnable r) {
        manager.onInputGdx(r);
    }

    /*
        Fluctuating.setAlphaFluctuationOn(!paused)
        DungeonScreen.getInstance().getController().inputPass() - via interface

        waitForAnimations - how to?

        interface for OptionsMaster ?

        duplicate
        GdxMaster.isLwjglThread()
         */




    public void setScreen(ScreenApi screen) {
        this.screen = screen;
    }

    public void setController(ControllerApi controller) {
        this.controller = controller;
    }

    public void setGdxApp(GdxApi gdxApp) {
        this.gdxApp = gdxApp;
    }

    public void setManager(GdxManagerApi manager) {
        this.manager = manager;
    }

    public void setEvents(GdxEvents events) {
        this.events = events;
    }

    public void setAudio(GdxAudio audio) {
        this.audio = audio;
    }

    public ScreenApi getDungeonScreen() {
        return dungeon;
    }
    public ScreenApi getMapScreen() {
        return map;
    }
    public ScreenApi getCurrentScreen() {
        return screen;
    }

    public ControllerApi getController() {
        return controller;
    }

    public GdxApi getGdxApp() {
        return gdxApp;
    }

    public GdxManagerApi getManager() {
        return manager;
    }

    public GdxEvents getEvents() {
        return events;
    }

    public GdxAudio getAudio() {
        return audio;
    }

    public GdxOptions getOptionsApi() {
        return options;
    }

    public static GdxOptions getOptions() {
        return getInstance().getOptionsApi();
    }

    public void inputPass() {
        getInstance().getController().inputPass();
    }
}
