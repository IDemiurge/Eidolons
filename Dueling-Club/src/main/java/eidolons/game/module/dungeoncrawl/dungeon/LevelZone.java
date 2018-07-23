package eidolons.game.module.dungeoncrawl.dungeon;

import eidolons.game.module.dungeoncrawl.generator.GeneratorEnums.ROOM_TEMPLATE_GROUP;

/**
 * Created by JustMe on 7/20/2018.
 */
public class LevelZone extends LevelLayer<LevelBlock>{

    private ROOM_TEMPLATE_GROUP templateGroup ;

    public LevelZone(ROOM_TEMPLATE_GROUP templateGroup) {
        this.templateGroup = templateGroup;
    }

    @Override
    public String toXml() {
        return null;
    }

    public ROOM_TEMPLATE_GROUP getTemplateGroup() {
        return templateGroup;
    }

    public void setTemplateGroup(ROOM_TEMPLATE_GROUP templateGroup) {
        this.templateGroup = templateGroup;
    }
}
