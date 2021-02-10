package eidolons.libgdx.gui.panels.headquarters.tabs.spell;

import eidolons.libgdx.GDX;
import eidolons.libgdx.GdxMaster;
import eidolons.libgdx.bf.generic.ImageContainer;
import eidolons.libgdx.gui.generic.btn.ButtonStyled;
import eidolons.libgdx.gui.generic.btn.SymbolButton;
import eidolons.libgdx.gui.panels.ScrollPaneX;
import eidolons.libgdx.gui.panels.headquarters.HqElement;
import eidolons.libgdx.gui.panels.headquarters.HqMaster;
import eidolons.libgdx.texture.Images;
import eidolons.system.text.tips.TIP;
import eidolons.system.text.tips.TipMessageMaster;

/**
 * Created by JustMe on 3/14/2018.
 */
public class HqSpellTab extends HqElement {
    private ScrollPaneX scroll;
    SpellbookContainer spellbook;
    VerbatimContainer verbatim;
    MemorizedContainer memorized;

    public HqSpellTab() {
        add(new ImageContainer(Images.SPELLBOOK))
                .colspan(2).row();
        add(spellbook = new SpellbookContainer())
                .colspan(2).row();
//        add(scroll = new ScrollPaneX(spellbook = new SpellbookContainer()){
//            @Override
//            public float getPrefHeight() {
//                return 128;
//            }
//        })
//                .colspan(2).row();


        add(verbatim = new VerbatimContainer());
        add(memorized = new MemorizedContainer());

//        add(new HqSpellScroll(spellbook = new SpellbookContainer(), 2))
//         .colspan(2).row();
//        add(new HqSpellScroll(verbatim = new VerbatimContainer(), 5));
//        add(new HqSpellScroll(memorized = new MemorizedContainer(), 5));
        setFixedSize(true);
        setSize(GDX.size(HqMaster.TAB_WIDTH),
                GDX.size(HqMaster.TAB_HEIGHT));
        SymbolButton infoBtn;
        addActor(infoBtn = new SymbolButton(ButtonStyled.STD_BUTTON.HELP, this::showHelpInfo));
        SymbolButton visBtn;
        addActor(visBtn = new SymbolButton(ButtonStyled.STD_BUTTON.EYE, this::toggleFilters));

        infoBtn.setY(GdxMaster.centerHeight(infoBtn));
        visBtn.setY(GdxMaster.getTopY(visBtn));
    }

    private void toggleFilters() {
        spellbook.toggleFilters();
    }

    private void showHelpInfo() {
        TipMessageMaster.tip(true, TIP.LIBRARY);
    }

    @Override
    protected void update(float delta) {

    }
}
