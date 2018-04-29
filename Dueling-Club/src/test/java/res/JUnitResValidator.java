package res;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import eidolons.entity.active.DC_ActiveObj;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.libgdx.anims.AnimMaster3d;
import eidolons.libgdx.anims.AnimMaster3d.PROJECTION;
import eidolons.libgdx.anims.AnimMaster3d.WEAPON_ANIM_CASE;
import eidolons.libgdx.anims.anim3d.Weapon3dAnim;
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

import static junit.framework.TestCase.assertTrue;

/**
 * Created by JustMe on 4/28/2018.
 */
public class JUnitResValidator extends LibgdxTest {
    @Override
    public void init() {
        TestMasterContent.setWeaponTest(true);
        super.init();
    }

    @Test
    public void validateAtlases() {
        for (String name : StringMaster.openContainer(TestMasterContent.TEST_WEAPONS)) {
            ObjType sub = DataManager.getType(name, DC_TYPE.WEAPONS);
            DC_WeaponObj weapon = new DC_WeaponObj(sub, getHero());
            getHero().equip(weapon, ITEM_SLOT.MAIN_HAND);
            helper.resetAll();
            DC_ActiveObj action = getHero().getAttacks(false).get(0);
            for (PROJECTION projection : PROJECTION.values()) {
                final Weapon3dAnim[] anim = {null};
                Gdx.app.postRunnable(() -> {

//                    anim[0] = new Weapon3dAnim(action);
//                    anim[0].start(action.getRef());

                    Array<AtlasRegion> regions =AnimMaster3d.getRegions
                     (WEAPON_ANIM_CASE.NORMAL, action, projection.bool);
                    WaitMaster.receiveInput(WAIT_OPERATIONS.SELECTION, regions.size > 0);

                });

                assertTrue((Boolean) WaitMaster.waitForInput(WAIT_OPERATIONS.SELECTION));
//                assertTrue(!anim[0].getSprites().isEmpty());

            }
        }
    }
}
