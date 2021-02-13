package libgdx;

import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.libgdx.GdxEventAdapter;
import eidolons.system.libgdx.api.ControllerApi;
import eidolons.system.libgdx.api.GdxManagerApi;
import eidolons.system.options.OptionsMaster;
import libgdx.adapters.ApiAdapter;
import libgdx.adapters.DataSourceApiImpl;
import libgdx.adapters.EventAdapter;
import libgdx.adapters.GdxManagerImpl;

public class Adapter implements ControllerApi {

    public void init(){
        GdxAdapter gdxAdapter = GdxAdapter.getInstance();
        gdxAdapter.setManager(new GdxManagerImpl());
        gdxAdapter.setEventsAdapter(new EventAdapter());
        gdxAdapter.setDataSourceApi(new DataSourceApiImpl());
        // gdxAdapter.setAudio(new aGdxManagerImpl());
        gdxAdapter.setController(this);
        gdxAdapter.setOptions(new GdxOptionsImpl());
        gdxAdapter.setGdxApp(new ApiAdapter());

        OptionsMaster.applyOptionsGdx();
    }

    @Override
    public void inputPass() {

    }
}
