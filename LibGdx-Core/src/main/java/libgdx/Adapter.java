package libgdx;

import eidolons.game.core.game.DC_Game;
import eidolons.system.libgdx.GdxAdapter;
import eidolons.system.libgdx.GdxEventAdapter;
import eidolons.system.libgdx.api.ControllerApi;
import eidolons.system.libgdx.api.GdxManagerApi;
import eidolons.system.options.OptionsMaster;
import libgdx.adapters.*;

public class Adapter implements ControllerApi {

    public static void afterGameInit(DC_Game game){
         game.getMetaMaster().setGdxBeans(new GdxBeansImpl());
    }
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
