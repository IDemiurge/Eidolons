package res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.anims.Assets;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;

/**
 * Created by JustMe on 4/28/2018.
 */
public class JUnitResValidator {

    @Test
    public void validateAtlases(){
        Gdx.app.postRunnable(()-> {
            Array<AtlasRegion> regions =
             AnimMaster3d.getRegions(c, action, projection);
            WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, regions.size>0);
        });

        assertTrue(WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION));
    }
}
