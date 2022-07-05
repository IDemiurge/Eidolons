package logic.content.test;

public class TestAbils {

    public enum AOE {
        //I'd dump single-target spells, they're boring.
        line, sides_melee, sides_range, other_side, same_side,
        row, front_row, cross, diagonal,
        //use callbacks?
        ;
        boolean sequential;
        boolean blockable;
        
    }

    public enum TestSpell{
        ;
        AOE aoe;

    }

}
