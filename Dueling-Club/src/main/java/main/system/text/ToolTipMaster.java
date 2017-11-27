package main.system.text;

import main.client.cc.CharacterCreator;
import main.content.VALUE;
import main.content.enums.entity.ActionEnums;
import main.content.enums.system.AiEnums.AI_LOGIC;
import main.entity.active.DC_ActiveObj;
import main.entity.active.DC_SpellObj;
import main.entity.active.DC_UnitAction;
import main.entity.obj.DC_Obj;
import main.entity.obj.unit.Unit;
import main.game.battlecraft.ai.tools.future.FutureBuilder;
import main.game.battlecraft.ai.tools.target.AI_SpellMaster;
import main.game.battlecraft.rules.combat.damage.DamageCalculator;
import main.game.core.Eidolons;
import main.game.core.game.DC_Game;
import main.swing.components.obj.CellComp;
import main.swing.generic.components.G_Panel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class ToolTipMaster {

    private List<TextItem> toolTipTextItems = new ArrayList<>();
    private DC_Game game;
    private TextItem actionRequirementText;
    private TextItem unitToolTip;
    private TextItem targetingText;
    private TextItem buffTooltip;
    private TextItem itemTooltip;
    private TextItem paramTooltip;
    private G_Panel panel;

    // separate by type? - req, anim, hover-info ...
    public ToolTipMaster(DC_Game game) {
        this.game = game;
    }

    public static String getObjTooltip(CellComp cellComp) {
        if (cellComp.isMultiObj()) {
            return getMultiObjTooltip(cellComp);
        }
        // mapping mouse hover point ... ah! I got a mouse-map, sure... but
        // resetting tooltip ... override createTooltip? Make it dynamic...
        return getObjTooltip(cellComp.getTopObjOrCell());
    }

    private static String getMultiObjTooltip(CellComp cellComp) {
        return "Multiple objects: ";
    }

    public static String getObjTooltip(DC_Obj target) {
        String tooltip = Eidolons.game.getVisionMaster().getHintMaster().getTooltip(target);
        if (tooltip != null) {
            return tooltip;
        }
        return target.getToolTip();

    }

    public static String getActionTargetingTooltip(DC_Obj target, DC_ActiveObj active) {
        if (active == null) {
            return "";
        }
        String tooltip = "";
        ACTION_TOOL_TIP_CASE _case = null;
        AI_LOGIC spellLogic = AI_SpellMaster.getSpellLogic(active);
        if (spellLogic == null) {
            if (active instanceof DC_UnitAction) {
                if (active.getActionGroup() == ActionEnums.ACTION_TYPE_GROUPS.ATTACK) {
                    _case = ACTION_TOOL_TIP_CASE.DAMAGE;
                }
            }
        }
        if (spellLogic != null) {
            switch (spellLogic) {
                case BUFF_NEGATIVE:
                case BUFF_POSITIVE:
                    _case = ACTION_TOOL_TIP_CASE.BUFF;
                    break;
                case CUSTOM_HOSTILE:
                case CUSTOM_SUPPORT:
                    _case = ACTION_TOOL_TIP_CASE.SPECIAL;
                    break;
                case AUTO_DAMAGE:
                case DAMAGE:
                case DAMAGE_ZONE:
                    _case = ACTION_TOOL_TIP_CASE.DAMAGE;
                    break;
                case RESTORE:
                    break;
            }
        }
        if (_case == null) {
            return "";
        }
        switch (_case) {
            case BUFF:
                break;
            case DAMAGE:
                boolean attack = !(active instanceof DC_SpellObj);
                int damage = FutureBuilder.precalculateDamage(active, target, attack);
                // ++ damage type
                tooltip += "avrg. damage: " + damage; // MIN-MAX is really in
                // order
                if (DamageCalculator.isLethal(damage, target)) {
                    tooltip += "(lethal)"; // TODO possibly lethal
                } else {
                    if (attack) {
                        if (((Unit) target).canCounter(active)) {
                            tooltip += "(will retaliate)"; // TODO precalc dmg?
                        }
                    }
                }
                break;
            case SPECIAL:
                break;
        }

        return tooltip;
    }

    public static MouseListener getValueMouseListener(final VALUE value) {
        return new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (DC_Game.game.isSimulation()) {
                    CharacterCreator.getPanel().getMiddlePanel().getScc().setValueToolTip(value);
                } else {
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

        };
    }

//    public void drawToolTips(G_Panel panel) {
//        this.panel = panel;
//        Graphics g = panel.getGraphics();
//
//        // to TextMaster !
//        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        ArrayList<TextItem> list = new ArrayList<>(toolTipTextItems);
//
//        for (TextItem textItem : list) {
//
//            drawTextItem(g, textItem);
//        }
//        // main.system.auxiliary.LogMaster.log(1, "tooltips drawn ");
//
//    }
//
//    public void initRuleTooltip(RULE rule) {
//        Point portrait = getPointForRule(rule);
//        TextItem textItem = getCustomTooltip(portrait, rule.getTooltip(), 400, null);
//        adjustPointForTextItem(textItem, portrait);
//
//    }
//
//    private Point getPointForRule(RULE rule) {
//        switch (rule) {
//            case TIME:
//                return new Point(DC_Builder.getBfGridPosX2(), DC_Builder.getBfGridPosY2());
//        }
//        return null;
//    }
//
//    public void drawTextItem(Graphics g, TextItem textItem) {
//        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        Rectangle rectangle = textItem.getRectangle();
//        g.setColor(ColorManager.BACKGROUND_MORE_TRANSPARENT);
//        g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
//        g.setColor(textItem.getColor());
//        Boolean alignment = textItem.getAlignment();
//        int x = textItem.getPoint().x;
//        int y = textItem.getPoint().y;
//        for (Object line : textItem.getLines()) {
//            if (line instanceof String) {
//                g.setFont(textItem.getFont());
//                String text = (String) line;
//                int width = FontMaster.getStringWidth(g.getFont(), text);
//                int x1 = getX(rectangle, alignment, x, width);
//                g.drawString(text, x1, y + FontMaster.getFontHeight(g.getFont()) / 2);
//                y += FontMaster.getFontHeight(g.getFont());
//            } else if (line instanceof ImageLine) {
//
//                g.setFont(textItem.getImageLineFont());
//                ImageLine imgLine = (ImageLine) line;
//                Image img = imgLine.getImage();
//                String text = imgLine.getText();
//                int width = img.getWidth(null);
//                int x1 = getX(rectangle, textItem.getImageLineAlignment(), x, width);
//                g.drawImage(img, x1, y - FontMaster.getFontHeight(g.getFont()) / 2, null);
//
//                x1 += img.getWidth(null) + imgLine.getOffsetX();
//                g.drawString(text, x1, y + imgLine.getOffsetY()
//                 + FontMaster.getFontHeight(g.getFont()) / 2);
//                y += img.getHeight(null); // max(fontHeight+offsetY
//            }
//        }
//    }
//
//    private int getX(Rectangle rectangle, Boolean alignment, int x, int width) {
//        int x1 = x;
//        if (alignment == null) {
//            x1 = x + (rectangle.width - width) / 2;
//        } else if (!alignment) {
//            x1 = x + rectangle.width - width;
//        }
//        return x1;
//    }
//
//    public void addTooltip(SCREEN_POSITION pos, String string) {
//        switch (pos) {
//
//        }
//        List<Object> lines = new ArrayList<>();
//        new TextItem(lines, TEXT_TYPE.INFO);
//    }
//
//    // attack tooltip, targeting tooltip,
//    public void initUnitTooltip(Unit unit, boolean targeting) {
//        List<Object> lines = new ArrayList<>();
//        toolTipTextItems.remove(unitToolTip);
//        String prefix = "";
//        if (unit.getOutlineTypeForPlayer() == null) {
//            if (!unit.getOwner().isNeutral()) {
//                prefix = !unit.getOwner().isEnemy() ? "[Ally] " : "[Enemy] ";
//            }
//        } else {
//            // lines.add(unit.getOutlineTypeForPlayer().g); TODO
//
//        }
//        String string = prefix + unit.getNameIfKnown();
//
//        lines.add(string);
//
//        unitToolTip = new TextItem(lines, TEXT_TYPE.UNIT_TOOLTIP);
//        // TODO for stacked?
//        Point portrait = new Point(DC_Builder.getBfGridPosX()
//         + (unit.getX() - game.getBattleField().getGrid().getOffsetX())
//         * GuiManager.getCellWidth()
//         + MigMaster.getCenteredPosition(GuiManager.getCellWidth(), FontMaster
//         .getStringWidth(unitToolTip.getFont(), string)), DC_Builder.getBfGridPosY()
//         + (unit.getY() - game.getBattleField().getGrid().getOffsetY())
//         * GuiManager.getCellHeight() + 3);
//
//        // alignPoint(portrait, ALIGNMENT.NORTH);
//
//        adjustPointForTextItem(unitToolTip, portrait);
//
//        // zoom!
//        // unit.getCellCompPoint()
//        // lines.add(status);
//
//    }
//
//    public void initItemTooltip(MouseEvent e, CustomList<DC_HeroAttachedObj> list, boolean info) {
//        Point point = e.getLocationOnScreen();
//        DC_HeroAttachedObj entity = list.locationToItem(e.getPoint());
//        String durability = "Durability: " + entity.getIntParam(PARAMS.C_DURABILITY) + "/"
//         + entity.getIntParam(PARAMS.DURABILITY);
//
//        String counters = "";
//        for (COUNTER c : CoatingRule.COATING_COUNTERS) {
//            Integer n = entity.getCounter(c);
//            if (n > 0) {
//                counters += c.getName() + " " + n + "; ";
//            }
//        }
//        String[] lines = new String[]{entity.getName(), durability,};
//        if (!counters.isEmpty()) {
//            lines = new String[]{entity.getName(), durability, counters,};
//        }
//
//        itemTooltip = new TextItem(point, TEXT_TYPE.INFO, lines);
//        adjustPointForTextItem(itemTooltip, point);
//    }
//
//    public TextItem getCustomTooltip(Point pointX, String tooltip, int width) {
//        return getCustomTooltip(pointX, tooltip, width, TextItem.getDefaultTextItemFont());
//    }
//
//    public TextItem getCustomTooltip(Point pointX, String tooltip, int width, Font font) {
//
//        TextItem tooltipTextItem = new TextItem(TextWrapper.wrap(tooltip, FontMaster
//         .getStringLengthForWidth(font, width)), pointX, TEXT_TYPE.INFO);
//        tooltipTextItem.setFont(font);
//
//        return tooltipTextItem;
//        // adj?
//
//    }
//
//    public boolean initCustomTooltip(Point pointX, String tooltip, int width) {
//        TextItem t = getCustomTooltip(pointX, tooltip, width);
//        adjustPointForTextItem(t, pointX);
//        return true;
//    }
//
//    public void initBuffTooltip(SmallItem item, boolean info_active_panel) {
//        DC_BuffObj buffObj = (DC_BuffObj) item.getEntity();
//        List<Object> lines = new ArrayList<>();
//        if (buffObj.isPermanent()) {
//            lines.add(buffObj.getName() + ", Permanent");
//        } else {
//            lines.add(buffObj.getName() + ", duration: " + buffObj.getDuration());
//        }
//        if (buffObj.getEffects() != null) {
//            for (Effect e : buffObj.getEffects()) {
//                lines.add(e.getTooltip());
//            }
//        }
//        toolTipTextItems.remove(buffTooltip);
//        buffTooltip = new TextItem(lines, TEXT_TYPE.BUFF_TOOLTIP);
//        buffTooltip.setColor(ColorManager.getStandardColor(buffObj.isNegative()));
//
//        G_List list = ((G_ListPanel) (info_active_panel ? getBuilder().getUnitInfoPanel()
//         .getBuffPanel().getCurrentComponent() : getBuilder().getActiveUnitPanel()
//         .getBuffPanel().getCurrentComponent())).getList();
//        Point portrait = list.getItemPosition(item);
//        // right above
//        portrait = new Point(MigMaster.getCenteredPosition(GuiManager.getBfGridWidth(), (int) buffTooltip
//         .getRectangle().getWidth())
//         + DC_Builder.getBfGridPosX(), portrait.y);
//
//        adjustPointForTextItem(buffTooltip, portrait);
//    }
//
//    public void initTargetingTooltip(DC_ActiveObj activeObj) {
//
//        List<Object> lines = new ArrayList<>();
//        for (Condition c : activeObj.getTargeting().getConditions()) {
//            lines.add(c.getTooltip()); // preCheck can concatenate
//        }
//        toolTipTextItems.remove(targetingText);
//        targetingText = new TextItem(lines, TEXT_TYPE.TARGETING);
//        Point portrait = getBuilder().getUap().getLocation();
//        // right above
//        portrait = new Point(MigMaster.getCenteredPosition(GuiManager.getBfGridWidth(),
//         (int) targetingText.getRectangle().getWidth())
//         + DC_Builder.getBfGridPosX(), portrait.y);
//        adjustPointForTextItem(targetingText, portrait);
//
//    }
//
//    public TextItem initActionToolTip(DC_ActiveObj t, Point point) {
//        return initActionTooltip(t, true, point);
//    }
//
//    public TextItem initActionToolTip(DC_ActiveObj activeObj, boolean info) {
//        // game.getBattleField().getBuilder().getUap()
//
//        Point portrait = getBuilder().getScreenPointForAction(activeObj);
//        portrait = new Point(portrait.x + GuiManager.getSmallObjSize() / 4 * 3, portrait.y
//         + GuiManager.getSmallObjSize() / 2);
//        return initActionTooltip(activeObj, info, portrait);
//    }
//
//    private TextItem initActionTooltip(DC_ActiveObj activeObj, boolean info, Point portrait) {
//        Costs costs = activeObj.getCosts();
//        if (activeObj.isAttackGeneric()) {
//            if (activeObj.getModeAction() != null)// TODO ??
//            {
//                costs = activeObj.getModeAction().getCosts();
//            }
//        }
//        List<String> list = info ? costs.toStrings(getValueNameSeparatorForImageLine()) : costs
//         .getReasonList();
//        List<Object> lines = replaceRequirementValueNamesWithIcons(list);
//        if (!info) {
//            if (activeObj.getCustomTooltip() != null) {
//                lines = new ListMaster<>().toList(activeObj.getCustomTooltip());
//            }
//        }
//
//        lines.add(0,
//         // "Activate " +
//         activeObj.getName());
//
//        toolTipTextItems.remove(actionRequirementText);
//        actionRequirementText = new TextItem(lines, info ? TEXT_TYPE.INFO : TEXT_TYPE.REQUIREMENT);
//        if (!activeObj.isSpell()) {
//            adjustPointForTextItem(actionRequirementText, portrait);
//        } else {
//            toolTipTextItems.add(actionRequirementText);
//        }
//
//        return actionRequirementText;
//    }
//
//    public void initQuickItemTooltip(DC_QuickItemObj quickItemObj, boolean info) {
//        initActionToolTip(quickItemObj.getActive(), info);
//
//    }
//
//    public boolean initTooltip(TOOLTIP_TYPE type, MouseEvent e, Object arg) {
//        return initTooltip(type, e, e.getPoint(), arg);
//    }
//
//    public boolean initTooltip(TOOLTIP_TYPE type, MouseEvent e, Point portrait, Object arg) {
//
//        switch (type) {
//            case CUSTOM_TOOLTIP:
//                return initCustomTooltip(portrait, arg.toString(), 300);
//            case DC_INFO_PAGE_PASSIVE:
//                return initPassiveTooltip(e);
//            case DC_INFO_PAGE_PARAMETER:
//                return initParamTooltip(e);
//            case DC_INFO_PAGE:
//                return initInfoPageTooltip(e);
//            case DC_DYNAMIC_PARAM:
//                return initDynamicParamTooltip(e, arg);
//            case DC_ACTIVE_PANEL_BUFF:
//                break;
//            case DC_ACTIVE_PANEL_ITEM:
//                break;
//            case DC_INFO_PANEL_BUFF:
//                break;
//            case DC_INFO_PANEL_ITEM:
//                break;
//            case DC_QUICK_ITEM:
//                break;
//            case DC_SPELL:
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//    private boolean initInfoPageTooltip(MouseEvent e) {
//        // TODO Auto-generated method stub
//        return true;
//    }
//
//    private boolean initDynamicParamTooltip(MouseEvent e, Object arg) {
//        // getBuilder().getActiveUnitPanel()
//        PARAMETER param = (PARAMETER) arg;
//        Point point = e.getLocationOnScreen();
//        return initParamTooltip(point, param, 260);
//
//    }
//
//    private boolean initParamTooltip(Point portrait, PARAMETER param, int w) {
//        int wrapLength = FontMaster.getStringLengthForWidth(
//         TextItem.getFontForType(TEXT_TYPE.INFO), w);
//        List<String> lines = TextWrapper.wrap(param.getDescription(), wrapLength);
//        lines.add(0, param.getName());
//        toolTipTextItems.remove(paramTooltip);
//        paramTooltip = new TextItem(portrait, TEXT_TYPE.INFO, lines);
//        toolTipTextItems.add(paramTooltip);
//        adjustPointForTextItem(paramTooltip, portrait);
//        return true;
//    }
//
//    private boolean initParamTooltip(MouseEvent e) {
//        Point portrait = e.getLocationOnScreen();
//        ValueIconPanel iconPanel = getBuilder().getUnitInfoPanel().getValueIconPanel();
//        VALUE v = iconPanel.getValueAt(e.getPoint());
//        int minX = DC_Builder.getBfGridPosX() + GuiManager.getSmallObjSize()
//         + GuiManager.getBattleFieldWidth();
//        int x = MathMaster.getMinMax(portrait.x, minX, minX + iconPanel.getWidth()); // /
//        // 2?
//        int y = MathMaster.getMinMax(portrait.y, iconPanel.getY(), iconPanel.getY()
//         + iconPanel.getHeight());
//        portrait = new Point(x, y);
//        // TODO far-right params will need adjustment!!!
//        return initParamTooltip(portrait, (PARAMETER) v, iconPanel.getWidth());
//
//    }
//
//    private boolean initPassiveTooltip(MouseEvent e) {
//        PropertyPage page = (PropertyPage) getBuilder().getInfoPanel().getCurrentComponent();
//        // ContainerIconElement comp= (ContainerIconElement)
//        // page.getComponentAt(e.getPoint());
//        // IconListPanel<SmallItem> list= (IconListPanel<SmallItem>)
//        // comp.getCurrentComponent();
//        // list.getList().getItemPosition(e);
//        Point portrait = e.getLocationOnScreen();
//        G_List<SmallItem> list = (G_List<SmallItem>) e.getSource();
//        SmallItem item = (SmallItem) list.locationToItem(e.getPoint());
//        Object arg = item.getArg();
//        int wrapLength = FontMaster.getStringLengthForWidth(
//         TextItem.getFontForType(TEXT_TYPE.INFO), page.getWidth());
//        String text = null;
//        if (arg == null) {
//            Entity entity = item.getEntity();
//            text = entity.getToolTip();
//            ;
//        } else {
//            if (arg instanceof CLASSIFICATIONS) {
//                CLASSIFICATIONS classifications = (CLASSIFICATIONS) arg;
//                text = classifications.getToolTip();
//            }
//            if (arg instanceof STANDARD_PASSIVES) {
//                STANDARD_PASSIVES std = (STANDARD_PASSIVES) arg;
//                text = std.getToolTip();
//            }
//        }
//        List<String> lines = TextWrapper.wrap(text, wrapLength);
//        toolTipTextItems.remove(paramTooltip);
//        paramTooltip = new TextItem(portrait, TEXT_TYPE.INFO, lines.toArray());
//        toolTipTextItems.add(paramTooltip);
//        adjustPointForTextItem(paramTooltip, portrait);
//        return true;
//    }
//
//    private DC_Builder getBuilder() {
//        return game.getBattleField().getBuilder();
//    }
//
//    private void adjustPointForTextItem(TextItem textItem, Point portrait) {
//        Rectangle rect = textItem.getRectangle();
//        int x = Math.min(GuiManager.getScreenWidthInt() - rect.width, portrait.x);
//        int y = Math.min(portrait.y, GuiManager.getScreenHeightInt() - rect.height);
//        x = Math.max(0, x);
//        y = Math.max(y, 0);
//        portrait = new Point(x, y);
//        textItem.setPoint(portrait);
//        toolTipTextItems.remove(textItem);
//        for (TextItem text : new ArrayList<>(toolTipTextItems)) {
//            Rectangle rectangle = text.getRectangle();
//            if (rect.intersects(rectangle)) {
//                panel.repaint(rectangle);
//            }
//            toolTipTextItems.remove(text);
//        }
//        // toolTipTextItems.clear();
//        addTooltip(textItem);
//        // panel.repaint();
//        if (game.isSimulation()) {
//            CharacterCreator.getPanel().refresh();
//        } else {
//            game.getBattleField().refresh();
//        }
//        // drawToolTips(panel);
//    }
//
//    private void addTooltip(TextItem textItem) {
//        toolTipTextItems.add(textItem);
//        LogMaster.log(1, textItem + " tooltip added");
//
//    }
//
//    private List<Object> replaceRequirementValueNamesWithIcons(List<String> lines) {
//        return replaceValueNamesWithIcons(lines, false, 4, 2);
//    }
//
//    private List<Object> replaceValueNamesWithIcons(List<String> lines, boolean wrap, int offsetX,
//                                                    int offsetY) {
//
//        List<Object> list = new ArrayList<>();
//        for (String line : lines) {
//            String[] array = line.split(getValueNameSeparatorForImageLine());
//            if (array.length <= 1) {
//                list.add(line);
//                continue;
//            }
//            String valueName = array[0];
//            VALUE value = ContentManager.getPARAM(valueName);
//            if (value == null) {
//                value = ContentManager.find(valueName, true);
//            }
//            if (value == null) {
//                list.add(line);
//                continue;
//            }
//            String text = line.replace(valueName + getValueNameSeparatorForImageLine(), "");
//
//            list.add(new ImageLine(ImageManager.getValueIcon(value), text, wrap, offsetX, offsetY));
//
//        }
//        return list;
//    }
//
//    private String getValueNameSeparatorForImageLine() {
//        return ": ";
//    }
//
//    public void toolTipClicked(MouseEvent e) {
//        // hide?
//    }
//
//    public void updateTextItems() {
//
//    }
//
//    public void removeToolTip(TextItem tooltip) {
//        toolTipTextItems.remove(tooltip); // TODO selective!
//
//    }
//
//    public void removeToolTips() {
//        toolTipTextItems.clear(); // TODO selective!
//        // main.system.auxiliary.LogMaster.log(1, "Tooltips cleared!");
//    }
//
//    public void removeToolTipByClick(Point point) {
//
//    }
//
//    public void toggleToolTip(TextItem textItem) {
//        if (toolTipTextItems.contains(textItem)) {
//            toolTipTextItems.remove(textItem); // ?
//        } else {
//            addTooltip(textItem);
//        }
//
//    }
//
//    public void addTooltip(PhaseAnimation anim, Point point, Rectangle rectangle, MouseItem item) {
//        List lines = (List) item.getArg();
//        TextItem textItem = new TextItem(lines, TEXT_TYPE.ANIMATION);
//        addTooltip(textItem);
//        adjustPointForTextItem(textItem, point);
//    }
//
//    public void addListItemTooltip(MouseEvent e, ObjType item) {
//        // TODO Auto-generated method stub
//
//    }
//
//    public boolean isTargetingTooltipShown(DC_ActiveObj active) {
//
//        return false;
//    }

    public enum ACTION_TOOL_TIP_CASE {
        DAMAGE, BUFF, SPECIAL,
    }

    public enum SCREEN_POSITION {
        BF_BOTTOM,
        BF_TOP,
        BF_ABOVE,
        BF_BELOW,
        ACTIVE_UNIT_BELOW,
        INFO_UNIT_BELOW,
        ACTIVE_UNIT_ABOVE,
        INFO_UNIT_ABOVE,
        ACTIVE_UNIT_TOP,
        INFO_UNIT_TOP,
        ACTIVE_UNIT_BOTTOM,
        INFO_UNIT_BOTTOM,

        CENTER,
        BOTTOM,
        TOP,
        LOG_BELOW,

    }

    public enum TOOLTIP_TYPE {
        DC_INFO_PAGE_PASSIVE,
        DC_ACTIVE_PANEL_BUFF,
        DC_INFO_PANEL_BUFF,
        DC_ACTIVE_PANEL_ITEM,
        DC_INFO_PANEL_ITEM,
        DC_QUICK_ITEM,
        DC_SPELL,
        DC_INFO_PAGE_PARAMETER,
        DC_INFO_PAGE,
        DC_DYNAMIC_PARAM,
        CUSTOM_TOOLTIP,
    }

}
