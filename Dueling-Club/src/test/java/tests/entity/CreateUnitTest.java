package tests.entity;

import init.JUnitDcInitializer;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.values.properties.G_PROPS;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.item.DC_WeaponObj;
import main.entity.obj.attach.DC_FeatObj;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by JustMe on 3/6/2017.
 */
public class CreateUnitTest{


    private String typeName = "Pirate";
    private String skillName = "Greater Strength";
    private String itemName = "inferior bronze dagger";
    private JUnitDcInitializer judi;
    private Unit entity;
    private DC_FeatObj skill;
    private DC_WeaponObj dagger;



    @Before
    public void createEntity() {
        judi = new JUnitDcInitializer();
        ObjType type= DataManager.getType(typeName, DC_TYPE.UNITS);
        entity = (Unit) judi.game.getManager().getObjCreator().createUnit(type, 0, 0, judi.game.getPlayer(true), new Ref(judi.game));
        skill = new DC_FeatObj(DataManager.getType(skillName, DC_TYPE.SKILLS), entity.getRef());
        dagger = new DC_WeaponObj(DataManager.getType(itemName, DC_TYPE.WEAPONS), entity);

    }





    @Test
    public void testUnitTest() {

        assert (entity!=null );
        assert (entity.getName().equals(typeName));
        entity.setParam(PARAMS.ACID_ARMOR, entity.getType().getParam(PARAMS.ACID_ARMOR)+5);
        entity.toBase();
        assert (entity.getParam(PARAMS.ACID_ARMOR) == entity.getType().getParam(PARAMS.ACID_ARMOR));




    }

    @Test
    public void testAssignSkill() {

        assert (entity!=null );
        assert (entity.getName().equals(typeName));
        int temp = entity.getIntParam(PARAMS.TOUGHNESS);
        assert(!entity.getSkills().contains(skill));
        entity.getSkills().add(skill);
        entity.fullReset(judi.game);
        assert(entity.getSkills().contains(skill));
        System.out.println(temp);
        System.out.println(entity.getIntParam(PARAMS.TOUGHNESS));
        assert(temp < entity.getIntParam(PARAMS.TOUGHNESS));

    }


    @Test
    public void testEquipWeapon() {

        assert (dagger!=null );
        assert (dagger.getName().equalsIgnoreCase(itemName));


        entity.setProperty(G_PROPS.MAIN_HAND_ITEM, itemName);
        int temp = entity.getCalculator().calculateDamage(false);

        entity.toBase();
        entity.getResetter().resetObjects();
        entity.afterEffects();

        System.out.println(temp);
        System.out.println(entity.getCalculator().calculateDamage(false));

        assert(temp < entity.getCalculator().calculateDamage(false));



    }
}
