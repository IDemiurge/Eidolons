package main.swing.components.panels.page.info.element;

import main.client.cc.CharacterCreator;
import main.client.cc.logic.HeroCreator;
import main.content.enums.entity.HeroEnums.PRINCIPLES;
import main.content.DC_ContentManager;
import main.content.DC_TYPE;
import main.content.PROPS;
import main.content.enums.entity.HeroEnums;
import main.content.values.properties.G_PROPS;
import main.content.values.properties.PROPERTY;
import main.entity.Entity;
import main.entity.Ref;
import main.entity.Ref.KEYS;
import main.entity.type.ObjType;
import main.game.core.game.Game;
import main.swing.components.panels.page.info.ValueInfoPage;
import main.swing.components.panels.page.log.WrappedTextComp;
import main.system.graphics.FontMaster;
import main.system.auxiliary.StringMaster;
import main.system.text.TextGenerator;
import main.system.text.TextParser;

import java.awt.*;

/**
 * Similar to what I'll need for the Log, ain't it? Wrap the text into lines
 * Render text with custom colors
 */

public class ContainerTextElement extends WrappedTextComp implements EntityValueComponent {

    private static final int FONT_SIZE = 16;
    protected Entity entity;
    private PROPERTY property;

    public ContainerTextElement(PROPERTY p) {
        super(null);
        this.property = p;
    }

    public ContainerTextElement(String text) {
        super(null);
        this.permanent = true;
        this.text = text;
    }

    @Override
    protected Ref getRef() {
        if (entity == null) {
            return null;
        }
        int id = entity.getId();
        Ref ref;
        // if (entity.getGame().isSimulation())
        // ref = CharacterCreator.getHeroPanel().getHero().getRef();
        // else TODO
        ref = entity.getRef();

        if (entity.getOBJ_TYPE_ENUM() instanceof DC_TYPE) {
            switch ((DC_TYPE) entity.getOBJ_TYPE_ENUM()) {
                case UNITS:
                case CHARS:
                    ref = entity.getRef();
                    break;
                case ACTIONS:
                    ref.setID(KEYS.ACTIVE, id);
                    break;
                case ARMOR:
                    ref.setID(KEYS.ARMOR, id);
                    break;
                case ITEMS:
                    ref.setID(KEYS.ITEM, id);
                    break;
                case SPELLS:
                    ref.setID(KEYS.SPELL, id);
                    ref.setID(KEYS.ACTIVE, id);
                    break;
                case WEAPONS:
                    ref.setID(KEYS.WEAPON, id);
                    break;
            }
        }
        ref.setID(KEYS.INFO, id);

        ref.setInfoEntity(entity);
        if (CharacterCreator.getHero() != null) {
            if (Game.game.isSimulation()) {
                if (entity instanceof ObjType) {
                    entity = HeroCreator
                            .getObjForType(CharacterCreator.getHero(), (ObjType) entity);
                }
            }
            ref.setSource(CharacterCreator.getHero().getId());
            // if (item!=null )
            // ref.setSource(item.getRef().getSource());
            CharacterCreator.getHero().getRef().setInfoEntity(entity);
        }
        return ref;
    }

    @Override
    public Dimension getPreferredSize() {
        int size = 2;
        if (textLines != null) {
            size = textLines.size();
        }

        panelSize = new Dimension(ValueInfoPage.INNER_WIDTH, FontMaster
                .getFontHeight(getDefaultFont())
                * size + 7);
        return panelSize;
    }

    @Override
    protected Font getDefaultFont() {
        return FontMaster.getDefaultFont(FONT_SIZE);
    }

    @Override
    protected int getDefaultX() {
        return super.getDefaultX();
    }

    public String getText() {
        if (permanent) {
            return text;
        }
        if (getEntity() == null) {
            return text;
        }
        String propertyValue = getEntity().getProperty(property);
        propertyValue = StringMaster.getFormattedContainerString(propertyValue);
        if (property == G_PROPS.PRINCIPLES) {
            String formattedValue = "";
            for (PRINCIPLES principle : HeroEnums.PRINCIPLES.values()) {
                // for (String pr :
                // StringMaster.openFormattedContainer(propertyValue)) {
                // PRINCIPLES principle = new
                // EnumMaster<PRINCIPLES>().retrieveEnumConst(
                // PRINCIPLES.class, pr);
                Integer identification = getEntity().getIntParam(
                        DC_ContentManager.getIdentityParamForPrinciple(principle));
                if (identification == 0) {
                    continue;
                }
                String values = getEntity().getIntParam(
                        DC_ContentManager.getAlignmentForPrinciple(principle))
                        + "/" + identification;
                formattedValue += principle.toString() + StringMaster.wrapInParenthesis(values)
                        + StringMaster.getFormattedContainerSeparator();
            }
            propertyValue = StringMaster.cropLast(formattedValue, 2);
        } else if (property == PROPS.REQUIREMENTS) {
            propertyValue = TextParser.formatRequirements(propertyValue);
            if (entity.getOBJ_TYPE_ENUM() == DC_TYPE.CLASSES) {
                propertyValue += StringMaster.NEW_LINE
                        + TextGenerator.generatePerkParamBonuses(entity);
            }
        }

        return getPrefix() + propertyValue;
    }

    protected boolean isPaintBlocked() {
        return entity == null;
    }

    protected boolean isCentering() {
        return false;
    }

    protected boolean isAutoWrapText() {
        return true;
    }

    public synchronized PROPERTY getProperty() {
        return property;
    }

    public synchronized void setProperty(PROPERTY property) {
        this.property = property;
    }

    public synchronized Entity getEntity() {
        return entity;
    }

    public synchronized void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
