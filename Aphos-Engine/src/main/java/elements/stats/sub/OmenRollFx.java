package elements.stats.sub;

public enum OmenRollFx {
    Fixed_1_F(0), //can anything affect this? Else call it by Hit Type name!
    Fixed_2_F(-2),
    Fixed_1_S(1),
    Fixed_2_S(3),

    Add_1_F(0), //adds success/fail to roll's outcome
    Add_2_F(-2),
    Add_1_S(1),
    Add_2_S(3),

    Add_1_Adv(2), //offsets roll's DC for fails/successes - more significant!
    Add_2_Adv(4),
    Add_1_Dis(-1),
    Add_2_Dis(-3),

    Roll_Dis(-1),  //lesser/greater of 2 rolls (Q: what about exploding?)
    Roll_Adv(2), //what does this mean actually? counts as single roll, can explode if hits DC
    Roll_Twice(1),
    Roll_Thrice(2), //just another roll and whatever outcomes are, will accumulate
    ;
    int chaosMod;

    OmenRollFx(int chaosMod) {
        this.chaosMod = chaosMod;
    }
}
