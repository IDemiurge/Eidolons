package res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.anims.anim3d.AnimMaster3d;
import eidolons.libgdx.anims.anim3d.AnimMaster3d.PROJECTION;
import eidolons.libgdx.anims.anim3d.AnimMaster3d.WEAPON_ANIM_CASE;
import eidolons.system.test.TestMasterContent;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.threading.WaitMaster;
import main.system.threading.WaitMaster.WAIT_OPERATIONS;
import org.junit.Test;
import tests.gdx.LibgdxTest;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.fail;

/**
 * Created by JustMe on 4/28/2018.
 */
public class JUnitResValidator extends LibgdxTest {
    private List<String> missing = new ArrayList<>();
    private List<String> missingAtlas = new ArrayList<>();

    @Override
    public void init() {
        TestMasterContent.setWeaponTest(true);
        super.init();
        atbHelper.startCombat();
    }

    @Test
    @org.junit.Ignore
    public void validateAtlases() {
        for (ObjType type : DataManager.getBaseWeaponTypes()) {
            String name=type.getName();
            DC_WeaponObj weapon = helper.equipWeapon(name);
            if (weapon == null ){
                log(name + " ==null ");
                continue;
            }
            atbHelper.startCombat();
            game.getLoop().setExited(true);
            helper.resetAll();
            if (weapon.getAttackActions()==null )
            {
                log(weapon + " getAttackActions ==null ");
                continue;
            }
            for (DC_ActiveObj action : weapon.getAttackActions())
                proj:for (PROJECTION projection : PROJECTION.values()) {


                    Gdx.app.postRunnable(() -> {
                        try {

                            Array<AtlasRegion> regions = AnimMaster3d.getRegions
                             (WEAPON_ANIM_CASE.NORMAL, action, projection.bool);
                            WaitMaster.receiveInput(
                             WAIT_OPERATIONS.SELECTION, regions);
                            if (regions.size<1){
                                missing.add(action.getActiveWeapon().getProperty(G_PROPS.BASE_TYPE) +
                                 "  " + action.getName()
                                + " = "+ AnimMaster3d.getAtlasFileKeyForAction(projection.bool,
                                 action, WEAPON_ANIM_CASE.NORMAL) );
                                WaitMaster.receiveInput(
                                 WAIT_OPERATIONS.SELECTION, null );
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            missingAtlas.add(action.getActiveWeapon().getProperty(G_PROPS.BASE_TYPE)
                             + "  " + action.getName()+" = "
                            + AnimMaster3d.getAtlasPath(action.getActiveWeapon(), action.getName())
                            );

                            WaitMaster.receiveInput(
                             WAIT_OPERATIONS.SELECTION, null );
                        }
                    });
                    Array<AtlasRegion> regions = (Array<AtlasRegion>) WaitMaster.
                     waitForInput(WAIT_OPERATIONS.SELECTION, 3000);
                    if (regions == null)
                        break proj;
//                    else
//                        if (regions.size > 0)
//                            System.out.println(projection + " Regions for " + action + weapon +
//                             ": " + regions);
//                        else {
//                            if (isCheckAll()) {
//                                missing.add(weapon.getProperty(G_PROPS.BASE_TYPE) + "  " + action.getName());
//                                break proj;
//                            } else
//                                fail(projection + " no regions for " + action.getName() + weapon);
//                        }

                }
        }
        if (missing.size() != 0) {
            String message = "Missing regions:";
            for (String sub : missing) {
                message += "\n" + sub;
            }
            message += "\nMissing atlases:";
            for (String sub : missingAtlas) {
                message += "\n" + sub;
            }
            fail(message);
        }
    }


    private boolean isCheckAll() {
        return true;
    }
}
