package eidolons.libgdx.gui.panels.headquarters.datasource.hero;

import eidolons.content.PARAMS;
import eidolons.libgdx.gui.datasource.EntityDataSource;
import eidolons.libgdx.gui.panels.dc.unitinfo.datasource.AttributesDataSource;
import eidolons.libgdx.gui.panels.headquarters.datasource.HeroDataModel;
import main.content.ContentValsManager;
import main.system.auxiliary.StringMaster;

/**
 * Created by JustMe on 4/16/2018.
 */
public class HqHeroDataSource extends EntityDataSource<HeroDataModel>
implements AttributesDataSource
{

    private boolean editable;

    public HqHeroDataSource(HeroDataModel entity) {
        super(entity);
    }


    @Override
    public String getStrength() {
        return null;
    }

    @Override
    public String getVitality() {
        return null;
    }

    @Override
    public String getAgility() {
        return null;
    }

    @Override
    public String getDexterity() {
        return null;
    }

    @Override
    public String getWillpower() {
        return null;
    }

    @Override
    public String getSpellpower() {
        return null;
    }

    @Override
    public String getIntelligence() {
        return null;
    }

    @Override
    public String getKnowledge() {
        return null;
    }

    @Override
    public String getWisdom() {
        return null;
    }

    @Override
    public String getCharisma() {
        return null;
    }

    @Override
    public String getAttribute(String name) {
        return null;
    }

    public int getLevel() {
        return  entity.getLevel() ;
    }

    public int getDefaultAttribute(PARAMS sub) {
        return entity.getIntParam(ContentValsManager.
         getDefaultAttribute(sub));
    }

    public String getFullPreviewImagePath() {
        return StringMaster.getAppendedImageFile(getImagePath(), " full");
    }

    public boolean isDead() {
        return entity.isDead();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
