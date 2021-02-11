package libgdx;

import eidolons.system.libgdx.GdxAdapter;
import libgdx.adapters.GdxManagerImpl;

public class Adapter {

    public void init(){
        GdxAdapter gdxAdapter = GdxAdapter.getInstance();

        gdxAdapter.setManager(new GdxManagerImpl());
        gdxAdapter.setEvents(new GdxManagerImpl());
        gdxAdapter.setEventsAdapter(new GdxManagerImpl());
        gdxAdapter.setDataSourceApi(new GdxManagerImpl());
        gdxAdapter.setAudio(new GdxManagerImpl());
        gdxAdapter.setController(new GdxManagerImpl());
        gdxAdapter.setOptions(new GdxManagerImpl());
        gdxAdapter.setGdxApp(new GdxManagerImpl());
    }
}
