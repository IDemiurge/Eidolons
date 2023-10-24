package logic.rules.combat.wounds;

import elements.exec.EntityRef;
import elements.exec.effect.Effect;
import elements.exec.effect.ModifyStatEffect;
import elements.stats.UnitParam;
import elements.stats.UnitProp;
import logic.rules.combat.wounds.content.BodyWound;
import logic.rules.combat.wounds.content.Wound;
import main.content.enums.GenericEnums.DieType;
import system.log.result.WoundResult;

/**
 * Created by Alexander on 8/22/2023
 *
 * 1-3: Ранение в Тело
 * От лишнего урона: кровотечение
 * Эффект: -2 Категории Броска Защиты
 * Порог Смерти: не восстанавливает очки перемещения
 * 4-5: Ранение в Конечности
 * От лишнего урона: потеря очков действий или Усталость
 * Эффект: -2 Категории Броска Атаки
 * Порог Смерти: восстанавливает половину очков действий (в большую сторону)
 * 6: Ранение в Голову
 * От лишнего урона: потеря очков Разума или Веры
 * Эффект: -2 Категории Броска Сопротивления
 * Порог Смерти: не восстанавливает очки Разума/Веры
 *
 */
public class HpWounds extends WoundsRule{
    @Override
    public WoundResult apply(int excessDamage, EntityRef ref) {
        //TODO DeathDoorRule.apply(ref);
        return super.apply(excessDamage, ref);
    }

    @Override
    protected Effect getEffect(Wound wound) {
        return new ModifyStatEffect().setValue(0, getValue(wound)).setValue(1, 2);
    }

    @Override
    protected UnitProp getWoundValue(Wound wound) {
        if (wound instanceof BodyWound bodyWound) {
            return switch ( bodyWound){
                case Body -> UnitProp.Wound_Body;
                case Limbs -> UnitProp.Wound_Limbs;
                case Head -> UnitProp.Wound_Head;
            };
        }
        return null;
    }

    private String getValue(Wound wound) {
        if (wound instanceof BodyWound bodyWound) {
            return switch ( bodyWound){
                case Body -> UnitParam.Defense_Auto_Fail.getName();
                case Limbs -> UnitParam.Attack_Auto_Fail.getName();
                case Head -> UnitParam.Resist_Auto_Fail.getName(); // | spell_auto_fail
            };
        }
        return null;
    }

    @Override
    protected DieType getDie() {
        return DieType.d6;
    }

    @Override
    protected Wound getWound(int rolled) {
        return switch(rolled){
            case 1, 2, 3  -> BodyWound.Body;
            case 4, 5 -> BodyWound.Limbs;
            case 6 -> BodyWound.Head;
            default -> throw new IllegalStateException("Expected value between 1 and 6: " + rolled);
        };
    }
}
