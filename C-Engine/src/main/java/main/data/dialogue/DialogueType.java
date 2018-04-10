package main.data.dialogue;

import main.content.ContentValsManager;
import main.content.values.properties.PROPERTY;
import main.entity.type.XmlHoldingType;

/**
 * Created by JustMe on 5/17/2017.
 */
public class DialogueType extends XmlHoldingType {
    @Override
    public PROPERTY getXmlProperty() {
        return ContentValsManager.getPROP("DIALOGUE_DATA");
    }
}
