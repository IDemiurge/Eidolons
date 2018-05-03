package res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.anims.AnimMaster3d.PROJECTION;
import eidolons.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;
import eidolons.system.test.TestMasterContent;
import main.content.DC_TYPE;
import main.content.enums.entity.ItemEnums.ITEM_SLOT;
import main.data.DataManager;
import main.entity.type.ObjType;
import main.system.auxiliary.StringMaster;
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
    private List <String> missing=     new ArrayList<>() ;

    @Override
    public void init() {
        TestMasterContent.setWeaponTest(true);
        super.init();
        atbHelper.startCombat();
    }

    @Test
    public void validateAtlases() {
        for (String name : StringMaster.openContainer(TestMasterContent.TEST_WEAPONS)) {
            ObjType sub = DataManager.getType(name, DC_TYPE.WEAPONS);
            if (sub==null )
            {
                main.system.auxiliary.log.LogMaster.log(1,  "No weapon " + name);
                continue;
            }

            DC_WeaponObj weapon = new DC_WeaponObj(sub, getHero());
            getHero().equip(weapon, ITEM_SLOT.MAIN_HAND);
            atbHelper.startCombat();
            game.getLoop().setExited(true);
            helper.resetAll();
//            if (weapon.getAttackActions()==null )
//                continue;
            for ( DC_ActiveObj action :weapon.getAttackActions())
                proj:   for (PROJECTION projection : PROJECTION.values()) {
                Gdx.app.postRunnable(() -> {

                    Array<AtlasRegion> regions =AnimMaster3d.getRegions
                     (WEAPON_ANIM_CASE.NORMAL, action, projection.bool);
                    WaitMaster.receiveInput(
                     WAIT_OPERATIONS.SELECTION, regions  );

                });
                Array<AtlasRegion> regions = (Array<AtlasRegion>) WaitMaster.
                 waitForInput(WAIT_OPERATIONS.SELECTION, 3000);
                if (regions!=null )
                if (regions.size>0)
                System.out.println(projection+ " Regions for " +action+ weapon +
                 ": " + regions);
                else {
                    if (isCheckAll())
                    {
                        missing.add(weapon + "  " + action);
                        break proj;
                    }
                   else
                       fail(projection+ " no regions for " +action + weapon );
                }
//                assertTrue((Boolean)
//                 WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION));
//                assertTrue(!anim[0].getSprites().isEmpty());

            }
        }
        if (missing.size()!=0){
            String message= "Missing:";
            for (String sub : missing) {
                message+="\n" + sub;
            }
            fail(message);
        }
    }

    private boolean isCheckAll() {
        return true;
    }
}
