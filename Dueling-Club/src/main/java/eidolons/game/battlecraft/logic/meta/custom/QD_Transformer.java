package eidolons.game.battlecraft.logic.meta.custom;


import eidolons.game.battlecraft.logic.meta.custom.model.QD_Model;

public class QD_Transformer {

    public String constructXmlFromModel(QD_Model model){
        /*
        1. transform modules
        2. assemble optimal grid ( get(isAllowTransform) => assign locations, alter coordinate strings
        3. write it all with Floor wrap and so

        So the trickiest part is actually the transform?! Which is essentially a little nugget for novelty, no
        functional value in it eh?!
        IDEA: Perhaps we can transform on save() so we have all trans-ed versions in XML always and just pick which
        to read?..
         */
        return null;
    }
    //nice way to make sure it all checks out, pregen some good ones etc
}
