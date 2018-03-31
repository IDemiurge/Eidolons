package tests.logic.combat;


import tests.FastDcTest;
import tests.init.JUnitDcInitializer;
import main.ability.effects.Effect;
import eidolons.ability.effects.oneshot.DealDamageEffect;
import main.content.DC_TYPE;
import eidolons.content.PARAMS;
import main.content.enums.GenericEnums;
import main.content.enums.GenericEnums.DAMAGE_MODIFIER;
import main.data.DataManager;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import eidolons.entity.item.DC_WeaponObj;
import eidolons.entity.obj.attach.DC_FeatObj;
import eidolons.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import eidolons.game.battlecraft.rules.combat.damage.Damage;
import eidolons.game.battlecraft.rules.combat.damage.DamageDealer;
import eidolons.game.battlecraft.rules.combat.damage.DamageFactory;
import main.system.math.Formula;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * Created by JustMe on 3/6/2017.
 */
public class DealDamageEffectTest extends FastDcTest{


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
        source = (Unit) game.getManager().getObjCreator().createUnit(type, 0, 0, game.getPlayer(true), new Ref(game));
        target = (Unit) game.getManager().getObjCreator().createUnit(type, 0, 0, game.getPlayer(true), new Ref(game));


    }

    @Test
    public void dealDamageEffectTest() {

        assertTrue (source !=null );
        assertTrue (target !=null );

        int origToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
        int origEndurance = target.getIntParam(PARAMS.C_ENDURANCE);



        Effect eff = new DealDamageEffect(new Formula("50"),
         GenericEnums.DAMAGE_TYPE.BLUDGEONING.getName(), DAMAGE_MODIFIER.UNBLOCKABLE);
        Ref ref = new Ref(source);
        ref.setTarget(target.getId());
        ref.setID(KEYS.ACTIVE, source.getAction("Attack").getId());
        eff.apply(ref);
        Integer newToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
        Integer newEndurance = target.getIntParam(PARAMS.C_ENDURANCE);

        assertTrue(newToughness < origToughness);
        assertTrue(newEndurance < origEndurance);


    }

    @Test
    public void dealDamageObjectTest() {

        assertTrue (source !=null );
        assertTrue (target !=null );

        int origToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
        int origEndurance = target.getIntParam(PARAMS.C_ENDURANCE);



        DealDamageEffect eff = new DealDamageEffect(new Formula("50"),
                GenericEnums.DAMAGE_TYPE.BLUDGEONING.getName(), DAMAGE_MODIFIER.UNBLOCKABLE);

        Damage dmg = DamageFactory.getDamageFromEffect(eff,25);
        dmg.getRef().setTarget(target.getId());
        DamageDealer.dealDamage(dmg);


        Integer newToughness = target.getIntParam(PARAMS.C_TOUGHNESS);
        Integer newEndurance = target.getIntParam(PARAMS.C_ENDURANCE);

        assertTrue(newToughness < origToughness);
        assertTrue(newEndurance < origEndurance);


    }

}
