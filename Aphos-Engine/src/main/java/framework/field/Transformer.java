package framework.field;

import elements.content.enums.FieldConsts;
import framework.entity.field.FieldEntity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static combat.sub.BattleManager.combat;

/**
 * Created by Alexander on 10/19/2023
 */
public class Transformer {
    public static List<FieldConsts.Cell> toCells(List<FieldEntity> list) {
        return list.stream().map(e -> e.getPos().getCell()).collect(Collectors.toList());
    }

    public static Set<FieldPos> toPos(Set<FieldConsts.Cell> set) {
        return set.stream().map(cell -> combat().getField().getPos(cell)).collect(Collectors.toSet());
    }
}
