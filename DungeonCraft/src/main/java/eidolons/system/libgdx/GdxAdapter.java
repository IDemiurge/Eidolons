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
    GdxApi gdxApp; //global stuff
    GdxManagerApi manager; // general helper funcs
    GridManagerApi gridManager; // grid funcs
    GdxEventAdapter eventsAdapter;
    GdxAudio audio;
    DataSourceApi dataSourceApi;
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

    public static void onInputGdx_(Runnable r) {
        instance.onInputGdx(r);
    }

    public static void onInput(Runnable o, Boolean aBoolean, boolean bool) {
    }

    public void onInputGdx(Runnable r) {
        manager.onInputGdx(r);
    }

    public static void inputPass() {
        getInstance().getController().inputPass();
    }
    /*
        Fluctuating.setAlphaFluctuationOn(!paused)
        DungeonScreen.getInstance().getController().inputPass() - via interface

        waitForAnimations - how to?

        interface for OptionsMaster ?

        duplicate
        GdxMaster.isLwjglThread()
         */


    public GridManagerApi getGridManager() {
        return gridManager;
    }

    public void setGridManager(GridManagerApi gridManager) {
        this.gridManager = gridManager;
    }

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


    public void setAudio(GdxAudio audio) {
        this.audio = audio;
    }

    public void setMap(ScreenApi map) {
        this.map = map;
    }

    public void setDungeon(ScreenApi dungeon) {
        this.dungeon = dungeon;
    }

    public void setDataSourceApi(DataSourceApi dataSourceApi) {
        this.dataSourceApi = dataSourceApi;
    }

    public void setOptions(GdxOptions options) {
        this.options = options;
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

    public GdxEventAdapter getEventsAdapter() {
        return eventsAdapter;
    }

    public void setEventsAdapter(GdxEventAdapter eventsAdapter) {
        this.eventsAdapter = eventsAdapter;
    }

    public GdxApi getGdxApp() {
        return gdxApp;
    }

    public GdxManagerApi getManager() {
        return manager;
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


    public DataSourceApi getDataSourceApi() {
        return dataSourceApi;
    }

}
