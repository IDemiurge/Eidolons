package tests.logic.combat;

import init.JUnitDcInitializer;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.active.DC_UnitAction;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.system.threading.WaitMaster;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Nyx on 3/16/2017.
 */
public class AttackTest {

    /**
     * Created by JustMe on 3/6/2017.
     */



        private String typeName = "Pirate";
        private String skillName = "Greater Strength";
        private String itemName = "inferior bronze dagger";
        private JUnitDcInitializer judi;
        private Unit source;
        private Unit target;
        private DC_FeatObj skill;
        private DC_WeaponObj dagger;



        @Before
        public void createEntity() {
            judi = new JUnitDcInitializer();
            ObjType type= DataManager.getType(typeName, DC_TYPE.UNITS);
            source = (Unit) judi.game.getManager().getObjCreator().createUnit(type, 0, 0, judi.game.getPlayer(true), new Ref(judi.game));
            target = (Unit) judi.game.getManager().getObjCreator().createUnit(type, 0, 0, judi.game.getPlayer(true), new Ref(judi.game));


        }

        @Test
        public void attackTest() {

            assertTrue (source !=null );
            assertTrue (target !=null );

            int origToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
            int origEndurance = target.getIntParam(PARAMS.C_ENDURANCE);


            assertTrue(source.getNaturalWeapon()!=null);

            DC_UnitAction attackAction = source.getAction("punch");
            assertTrue (attackAction !=null );

            attackAction.activateOn(target);
            WaitMaster.waitForInput(WaitMaster.WAIT_OPERATIONS.ACTION_COMPLETE);


            Integer newToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
            Integer newEndurance = target.getIntParam(PARAMS.C_ENDURANCE);
            assertTrue(newToughness < origToughness);
            assertTrue(newEndurance < origEndurance);


        }



}
