package tests.entity;

import eidolons.content.PARAMS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import main.content.DC_TYPE;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/28/2017.
 */
public class WeaponSkillTest extends JUnitPartyCreated {

    protected String skillName = "Greater Strength";
    protected String itemName = "inferior bronze dagger";
    protected DC_FeatObj skill;
    protected DC_WeaponObj dagger;

    @Before
    public void createEntity() {
//        ObjType type = DataManager.getType(typeName, DC_TYPE.UNITS);
//        entity = (Unit) game.getManager().getObjCreator().createUnit(type, 0, 0, game.getPlayer(true), new Ref(game));
        skill = new DC_FeatObj(DataManager.getType(skillName, DC_TYPE.SKILLS), unit.getRef());
        dagger = new DC_WeaponObj(DataManager.getType(itemName, DC_TYPE.WEAPONS), unit);

    }

    @Test
    public void testAssignSkill() {

        int temp = unit.getIntParam(PARAMS.TOUGHNESS);
        assert (!unit.getSkills().contains(skill));
        unit.getSkills().add(skill);
        unit.fullReset(game);
        assertTrue(unit.getSkills().contains(skill));
        System.out.println(temp);
        System.out.println(unit.getIntParam(PARAMS.TOUGHNESS));
        assertTrue(temp < unit.getIntParam(PARAMS.TOUGHNESS));

    }


    @Test
    public void testEquipWeapon() {

        assert (dagger != null);
        assert (dagger.getName().equalsIgnoreCase(itemName));


        unit.setProperty(G_PROPS.MAIN_HAND_ITEM, itemName);
        int temp = unit.getCalculator().calculateDamage(false);

        unit.toBase();
        unit.getResetter().resetObjects();
        unit.afterEffects();

        System.out.println(temp);
        System.out.println(unit.getCalculator().calculateDamage(false));

        assertTrue(temp < unit.getCalculator().calculateDamage(false));


    }

}
