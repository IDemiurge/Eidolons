package main.client.cc.gui.neo.tree;

import main.ability.conditions.req.SkillPointCondition;
import main.client.cc.HC_Master;
import main.client.cc.gui.neo.tree.logic.HT_MapBuilder;
import main.client.cc.gui.neo.tree.logic.StaticTreeLink;
import main.client.cc.gui.neo.tree.logic.TreeLink;
import main.client.cc.gui.neo.tree.logic.TreeMap;
import main.client.cc.gui.neo.tree.logic.TreeMap.LINK_VARIANT;
import main.client.dc.Launcher;
import main.content.ContentManager;
import main.content.DC_TYPE;
import main.content.PARAMS;
import main.content.enums.entity.HeroEnums.CLASS_GROUP;
import main.content.enums.entity.SkillEnums.SKILL_GROUP;
import main.content.values.parameters.PARAMETER;
import main.data.DataManager;
import main.elements.conditions.Requirements;
import main.elements.conditions.RequirementsManager;
import main.entity.Ref;
import main.entity.obj.unit.Unit;
import main.entity.type.ObjType;
import main.swing.XLine;
import main.swing.generic.components.G_Panel;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.MapMaster;
import main.system.auxiliary.log.LogMaster;
import main.system.auxiliary.secondary.InfoMaster;
import main.system.graphics.ColorManager;
import main.system.graphics.FontMaster;
import main.system.graphics.FontMaster.FONT;
import main.system.graphics.MigMaster;
import main.system.images.ImageManager;
import main.system.images.ImageManager.BORDER;
import main.system.images.ImageManager.STD_IMAGES;
import main.system.launch.CoreEngine;
import main.system.math.Formula;
import main.system.text.SmartText;
import main.system.text.TextParser;
import main.system.text.TextWrapper;

import java.awt.*;
import java.util.*;
import java.util.List;

public class HC_Tree {
    G_Panel panel;
    Unit hero;
    TreeMap map;
    Map<Point, Image> underlayMap = new HashMap<>();
    Map<Point, Image> overlayMap = new HashMap<>();
    Map<Point, Image> overlayMap2 = new HashMap<>();

    Map<XLine, LINK_TYPE> linkMap = new HashMap<>();

    Image bufferImage;
    // private ObjType selectedType;
    Image backgroundImage;
    List<HT_Node> selectionPathNodes = new LinkedList<>();
    TREE_VIEW_MODE mode;
    private Object arg;
    private ObjType lastSelected;
    private List<TreeLink> dynamicLinks;
    private List<ObjType> types;
    private Map<Rectangle, HT_Node> rankBoostMouseMap = new HashMap<>();
    private Map<Point, SmartText> textMap = new HashMap<>();
    private boolean viewMode;
    private LinkedList<StaticTreeLink> linksToHighlight;
    private boolean displayRequirements;
    private Point textBgPoint1;
    private Point textBgPoint2;
    private List<String> reasons;

    // so the tree itself is static, no dynamic node changes?
    /*
     * what's the point of making it so? performance for 3-tree view? for refreshes to be lightning fast...
	 * but that's not about buffer/no buffer - it's about how I manage overlays!  
	 */
    private ObjType reqTextType;

    public HC_Tree(Unit hero, TreeMap map, Object arg) {
        this.map = map;
        this.types = map.getTypes();
        this.hero = hero;
        this.arg = arg;
        bufferImage = generateTreeImage();
        backgroundImage = ImageManager.getImage(getBackGroundPath());
        if (backgroundImage == null) {
            backgroundImage = ImageManager.getImage(getGroupBackgroundPath());
        }
        if (backgroundImage == null) {
            backgroundImage = ImageManager.getImage(getEmptyBackgroundPath());
        }
        panel = new G_Panel() {
            @Override
            public void paint(Graphics g) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, null);
                }
                if (bufferImage != null) {
                    g.drawImage(bufferImage, 0, 0, null); // say, background//
                }
                // background_highlight_64

                drawStaticLinks(g);
                drawUnderlays(g);
                drawNodes(g);
                drawLinks(g);
                drawOverlays(g);
                drawTextBackground(g);
                drawText(g);

            }
        };
        panel.setPanelSize(map.getDimension());
    }

    public static int getYOffsetForLink(LINK_VARIANT variant) {
        switch (variant) {
            case ANGLE_TO_LEFT_0:
                return 64;
            case ANGLE_TO_RIGHT_0:
                return 64;
        }
        return 0;
    }

    protected void drawTextBackground(Graphics g) {
        if (textBgPoint1 == null) {
            return;
        }
        if (textBgPoint2 == null) {
            return;
        }
        g.setColor(getTipBackgroundColor());
        g.fillRect(textBgPoint2.x - 6, textBgPoint1.y - 4, textBgPoint1.x - textBgPoint2.x + 6,
                textBgPoint2.y - textBgPoint1.y + 12);

    }

    protected void drawStaticLinks(Graphics g) {
        // sortLinksForZ(getMap().getStaticLinkMap());

        for (StaticTreeLink link : getMap().getStaticLinkMap().keySet()) {
            if (linksToHighlight != null) {
                if (linksToHighlight.contains(link)) {
                    continue;
                }
            }
            drawLink(g, link);
        }
        if (linksToHighlight != null) {
            for (StaticTreeLink link : linksToHighlight) {
                drawLink(g, link, LIGHTING_VERSION.HIGHLIGHTED);
            }
        }

        for (StaticTreeLink link : map.getAltLinks().keySet()) {
            drawLink(g, link);
        }
    }

    private void sortLinksForZ(Map<StaticTreeLink, Point> staticLinkMap) {
        List<StaticTreeLink> list = new LinkedList<>(staticLinkMap.keySet());
        Collections.sort(list, new Comparator<StaticTreeLink>() {

            @Override
            public int compare(StaticTreeLink o1, StaticTreeLink o2) {
                // o1.getZ();

                for (ObjType c : o1.getChildren()) {
                    if (map.getNodeForType(c).isSelected()) {

                        break;
                    }
                }
                return 0;
            }
        });
        List<Point> values = new LinkedList<>();
        for (StaticTreeLink l : list) {
            values.add(staticLinkMap.get(l));
        }
        staticLinkMap = new MapMaster<StaticTreeLink, Point>().constructMap(list, values);

        map.setStaticLinkMap(staticLinkMap);
    }

    protected void drawLink(Graphics g, StaticTreeLink link) {
        LIGHTING_VERSION lighting = LIGHTING_VERSION.DARKENED;
        for (ObjType c : link.getChildren()) {
            if (map.getNodeForType(c) == null) {
                continue;
            }
            if (map.getNodeForType(c).isAcquired()) {
                lighting = LIGHTING_VERSION.NORMAL;
                break;
            }
            if (!CoreEngine.isArcaneVault()) {
                if (map.getNodeForType(c).isAvailable()) {
                    lighting = LIGHTING_VERSION.AVAILABLE;
                    break;
                }
            }
            if (map.getNodeForType(c).isSelected()) {
                lighting = LIGHTING_VERSION.HIGHLIGHTED;
                break;
            }
            // 2+ only in skills? Well also in GroupNodes so far...
        }
        drawLink(g, link, lighting);
    }

    protected void drawLink(Graphics g, StaticTreeLink link, LIGHTING_VERSION variant) {
        if (link == null) {
            return;
        }
        Point point = link.getPoint();
        // getXOffsetForLink(variant);
        // getXOffsetForLink(variant);
        int x = point.x;
        int y = point.y;
        Image image = null;
        switch (variant) {
            case AVAILABLE:
                image = link.getVariant().getAvailableImage();
                break;
            case DARKENED:
                image = link.getVariant().getDarkenedImage();
                break;
            case HIGHLIGHTED:
                image = link.getVariant().getSelectedImage();
                int offsetX = 0;
                int offsetY = 0;
                if (link.getVariant().isVertical()) {
                    offsetX = 5;
                }
                // TODO selected variant
                // offsetX=offsetX* Math.sin(getAngleForLinkVariant());
                x = point.x - offsetX;
                y = point.y - offsetY;
                break;
            case NORMAL:
                image = link.getVariant().getImage();
                break;
        }
        if (image == null) {
            image = link.getVariant().getImage();
        }
        g.drawImage(image, x, y, null);

    }

    protected void drawText(Graphics g) {
        for (Point p : textMap.keySet()) {
            SmartText smartText = textMap.get(p);
            g.setFont(smartText.getFont());
            g.setColor(smartText.getColor());
            g.drawString(smartText.getText(), p.x, p.y);
        }
        if (isNodeNamesDrawn()) {
            Font defaultFont = getDefaultFont();
            for (Point p : map.getNodeMap().keySet()) {
                int wrapLength = 15;
                ObjType type = map.getNodeMap().get(p).getType();
                String text = type.getName();
                List<String> list = TextWrapper.wrap(text, wrapLength);
                int width = FontMaster.getStringLengthForWidth(defaultFont, Math.min(wrapLength,
                        text.length()));

                int offsetX = (width - map.getNodeSize()) / 2;
                int x = Math.max(p.x - offsetX, 0);

                // boolean hasOverlap = false;
                int offsetY = 5;
                int y = Math.max(p.y - offsetY, 0); // lower if overlap
                int fontHeight = FontMaster.getFontHeight(defaultFont) + 3;
                int height = list.size() * fontHeight + 5;

                List<ObjType> overlapping = map.getTypesWithinRange(type, 0, 0, x, y, Math.max(20,
                        (width - map.getNodeSize())), fontHeight * 3 / 2);

                if (y < 5) {
                    y = 5;
                }
                if (x <= offsetX) {
                    x = offsetX;
                }

                if (overlapping.size() > 0) {

                    LogMaster.log(1, type + " has " + overlapping);

                    y += map.getNodeSize() + offsetY * 2;

                    while (overlapping.size() > 0) {
                        y += fontHeight;
                        if (y >= map.getDimension().height) {
                            y -= fontHeight;
                            break;
                        }
                        overlapping = map.getTypesWithinRange(type, 0, 0, x, y, Math.max(20,
                                (width - map.getNodeSize())), fontHeight * 3 / 2);
                        LogMaster.log(1, type + " has " + overlapping);
                    }
                    while (overlapping.size() > 0) {
                        y -= fontHeight;
                        if (y <= offsetY) {
                            y = offsetY;
                            break;
                        }
                        overlapping = map.getTypesWithinRange(type, 0, 0, x, y, Math.max(20,
                                (width - map.getNodeSize())), fontHeight * 3 / 2);
                        LogMaster.log(1, type + " has " + overlapping);

                    }
                }

                // height += offsetY;

                g.setColor(ColorManager.getTranslucent(getTipBackgroundColor(), 90));
                g.fillRect(x - 12, y - 12, width, height);

                for (String str : list) {
                    g.setColor(getTextColor());
                    g.drawString(str, x, y);
                    y += fontHeight;

                }
            }
        }
    }

    private int getNodeSize() {
        return 64;
    }

    public void setMode(TREE_VIEW_MODE mode) {
        this.mode = mode;
    }

    private boolean isNodeNamesDrawn() {
        return (mode == TREE_VIEW_MODE.INFO);
    }

    private Color getTextColor() {
        if (isSkill()) {
            SKILL_GROUP masteryGroup = new EnumMaster<SKILL_GROUP>().retrieveEnumConst(
                    SKILL_GROUP.class, ContentManager.getMasteryGroup((PARAMETER) arg));
            if (masteryGroup == null) {
                return ColorManager.GOLDEN_WHITE;
            }
            return ColorManager.getColorForMastery(masteryGroup);
        }
        return ColorManager.getColorForClass((CLASS_GROUP) arg);
    }

    private Color getTipBackgroundColor() {
        return ColorManager.BACKGROUND_TRANSPARENT;
    }

    private String getGroupBackgroundPath() {
        String imageName;
        if (arg instanceof CLASS_GROUP) {
            CLASS_GROUP group = (CLASS_GROUP) arg;

            // morph based on highest class?
            imageName = getBgImageForClass(group);
        } else {
            imageName = ContentManager.getMasteryGroup((PARAMETER) arg);
            switch (imageName) {
                case "Heavy":
                    imageName = "war";
                    break;
                case "Combat":
                    imageName = "combat";
                    break;
                case "Weapons":
                    imageName = "weapons";
                    break;
                case "Misc":
                    imageName = "misc";
                    break;
                case "Stealth":
                    imageName = "stealth";
                    break;
            }
            if (imageName.isEmpty()) {
                switch ((PARAMS) arg) {
                    case ARMORER_MASTERY:
                        imageName = "war";
                        break;
                    case BLADE_MASTERY:
                    case POLEARM_MASTERY:
                    case BLUNT_MASTERY:
                    case AXE_MASTERY:
                        imageName = "weapons";
                        break;
                }
            }
        }
        return "big\\trees\\" + imageName + ".jpg";
    }

    private String getBgImageForClass(CLASS_GROUP group) {
        return "classes\\" + group.getName();
    }

    private String getBackGroundPath() {
        return "big\\trees\\" + arg.toString().replace(" Mastery", "") + ".jpg";
    }

    private String getEmptyBackgroundPath() {
        return "big\\trees\\empty.jpg";
    }

    public void refresh() {
        for (HT_Node n : getNodes()) {
            n.setViewMode(isViewMode());
            try {
                n.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        underlayMap.clear();
        overlayMap.clear();
        overlayMap2.clear();

        try {
            updateLinks();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updateInfoIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updateRankComponents();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updateOverlayFrames();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            updateText();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // addSizedIcons();

    }

    private void updateRankComponents() {
        getRankBoostMouseMap().clear();

        if (mode == TREE_VIEW_MODE.INFO) {
            return;
        }
        for (HT_Node n : getNodes()) {
            if (n.isGrouped()) {
                continue;
            }
            if (mode == TREE_VIEW_MODE.BASIC) {
                if (!n.isAcquired()) {
                    continue;
                }
            }
            Integer max = n.getObj().getIntParam(PARAMS.RANK_MAX);
            // if (RANK_TEST_MODE)
            // max = 5 - n.getObj().getIntParam(PARAMS.CIRCLE);
            if (max <= 0) {
                continue;
            }
            Integer rank = 0;
            if (hero.getFeat(isSkill(), n.getType()) != null) {
                rank = hero.getFeat(isSkill(), n.getType()).getIntParam(PARAMS.RANK);
            }

            STD_IMAGES rankComp = n.isAcquired() ? STD_IMAGES.RANK_COMP
                    : STD_IMAGES.RANK_COMP_DARKENED;
            int w = rankComp.getWidth();
            int h = rankComp.getHeight();
            Image compImage = ImageManager.getNewBufferedImage(w, h);
            boolean acquired = n.isAcquired();
            if (acquired) {
                rank++;
            }
            String str = rank + "/" + max;

            Graphics graphics = compImage.getGraphics();
            graphics.drawImage(rankComp.getImage(), 0, 0, null);
            Font font = FontMaster.getDefaultFont(16);
            graphics.setFont(font);

            int x = MigMaster.getCenteredTextPosition(str, font, w);
            int y = MigMaster.getCenteredTextPositionY(font, h) + 5;
            ((Graphics2D) graphics).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.drawString(str, x, y);
            x = (map.getPointForType(n.getType()).x + 40 - w / 2);
            y = map.getPointForType(n.getType()).y - h;
            Image plusImage = null;
            if (acquired) {
                plusImage = ImageManager.getSizedVersion(STD_IMAGES.CROSS.getImage(), 75);
            }
            // if (available) _HIGHLIGHTED
            if (plusImage != null) {
                int x1 = x + w / 2 + 6;
                int y1 = y + h / 2 - plusImage.getHeight(null) / 3 * 2 + 2;
                overlayMap2.put(new Point(x1, y1), plusImage);
                getRankBoostMouseMap().put(
                        new Rectangle(x1, y1, plusImage.getWidth(null), plusImage.getWidth(null)),
                        n); // dynamic

            }
            x -= STD_IMAGES.CROSS.getWidth() / 2;
            Point p = new Point(x, y);

            if (n.isSelected()) {
                overlayMap.put(p, compImage);
            } else {
                underlayMap.put(p, compImage);
            }

            // map!
        }
    }

    private Collection<HT_Node> getNodes() {
        return map.getNodeMap().values();
    }

    public Map<Rectangle, HT_Node> getRankBoostMouseMap() {
        return rankBoostMouseMap;
    }

    private void updateText() {
        textMap.clear();
        textBgPoint1 = null;
        textBgPoint2 = null;
        if (isViewMode()) {
            return;
        }
        if (Launcher.DEV_MODE) {
            if (CoreEngine.isArcaneVault()) {
                return;
            }
        }
        // TODO selected - reqs or rank/sd costs

        // TODO draw black background for this!
        if (isDisplayRequirements()) {
            boolean rank = false;
            if (reqTextType == null) {
                reqTextType = getSelectedType();
            } else {
                rank = true;
            }
            if (types.contains(reqTextType)) {

                Integer x = null;
                Integer y = map.getPointForType(reqTextType).y + 58;
                boolean above = false;
                boolean centered = false;
                Integer originX = null;
                if (y >= HT_MapBuilder.defTreeHeight - 130) {
                    y = map.getPointForType(reqTextType).y - 20;
                    above = true;

                }
                int mode = 0;
                // TODO sometimes above!!!
                // display SD ?
                if (rank) {
                    mode = RequirementsManager.RANK_MODE;
                }
                Requirements reqs = reqTextType.getGame().getRequirementsManager().getRequirements(
                        (rank ? hero.getFeat(isSkill(), reqTextType) : reqTextType), mode);

                Point point;
                Color color = ColorManager.CRIMSON;
                Font font = getDefaultFont();
                List<String> list;
                if (reasons != null) {
                    list = reasons;
                } else {
                    Ref ref = new Ref(hero);
                    ref.setMatch((reqTextType).getId());
                    reqs.preCheck(ref);
                    list = reqs.getReasons();
                    reasons = new LinkedList<>();
                }
                for (String text : list) {
                    if (text.equals(InfoMaster.NOT_ENOUGH_MASTERY)) {
                        text = InfoMaster.NOT_ENOUGH_MASTERY_SLOTS
                                + ((SkillPointCondition) reqs.getReqMap().get(text))
                                .getPointsRequired();
                    }
                    text = TextParser.parse(text, new Ref(getHero()));
                    text = text.replace(StringMaster.MASTERY + " ", "");
                    text = text.replace(" needed", "");
                    text = text.replace(" required", "");
                    if (!text.contains(" or ")) {
                        if (text.contains(": ")) {
                            String varPart = text.split(": ")[1];
                            if (varPart.startsWith("(")
                                    || StringMaster.isInteger("" + varPart.charAt(0))) {
                                String parsedVarPart = "";
                                try {
                                    parsedVarPart = ""
                                            + new Formula(varPart).getInt(new Ref(getHero()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (!parsedVarPart.isEmpty())
                                // TextParser.parse(varPart, new
                                // Ref(getHero()));
                                {
                                    text = text.replace(varPart, parsedVarPart);
                                }
                            }
                        }
                    }
                    reasons.add(text);

                }
                Collections.sort(reasons, new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        if (o1.length() < o2.length()) {
                            return 1;
                        }
                        if (o1.length() > o2.length()) {
                            return -1;
                        }
                        return 0;
                    }
                });
                int stringWidth;
                for (String text : reasons) {
                    SmartText smartText = new SmartText(text, color);
                    smartText.setFont(font);

                    stringWidth = FontMaster.getStringWidth(font, text);
                    if (x == null) {
                        x = map.getPointForType(reqTextType).x;
                        if (x + stringWidth >= getPanel().getWidth() - 10) {
                            x = getPanel().getWidth() - stringWidth - 10;
                            originX = x;
                            centered = true;
                        }
                    } else if (centered) {
                        x = originX;
                        // x = stringWidth - originX / 2 + getPanel().getWidth()
                        // - stringWidth / 2;
                    }
                    if (x == map.getPointForType(reqTextType).x) {
                        x = map.getPointForType(reqTextType).x - stringWidth / 4;
                    }
                    if (x < 0) {
                        x = 0;
                    }
                    if (above) {
                        y -= 20;
                    } else {
                        y += 20;
                    }
                    if (x == null) {
                        x = map.getPointForType(reqTextType).x;
                    }
                    point = new Point(x, y);
                    textMap.put(point, smartText);
                    // TODO if above... vice versa
                    if (textBgPoint1 == null) {
                        textBgPoint1 = new Point(x + stringWidth, y
                                - FontMaster.getFontHeight(font));
                    }
                }
                textBgPoint2 = new Point(x, y);
                if (above) {
                    Point buffer = textBgPoint1;
                    textBgPoint1 = new Point(textBgPoint2.x, textBgPoint2.y
                            - FontMaster.getFontHeight(font));
                    textBgPoint2 = new Point(buffer.x, buffer.y + FontMaster.getFontHeight(font));
                }

                // mouse map on reqs -> goto req-skill or so
            }
        }
        reasons = null;
        reqTextType = null;
    }

    private boolean isSkill() {
        if (types.isEmpty()) {
            return false;
        }
        return types.get(0).getOBJ_TYPE_ENUM() == DC_TYPE.SKILLS;
    }

    public ObjType getReqTextType() {
        return reqTextType;
    }

    public void setReqTextType(ObjType reqTextType) {
        this.reqTextType = reqTextType;
    }

    private boolean isDisplayRequirements() {
        return displayRequirements;
    }

    public void setDisplayRequirements(boolean displayRequirements) {
        this.displayRequirements = displayRequirements;
    }

    private void updateInfoIcons() {

        // TODO display sd? rank?
    }

    private void updateOverlayFrames() {
        addNodeHighlightsAndShadows();
    }

    private void addNodeHighlightsAndShadows() {
        for (HT_Node n : getNodes()) {
            Point p = map.getPointForType(n.getType());

            if (selectionPathNodes.contains(n)) {
                Image img = BORDER.NEO_PATH_HIGHLIGHT_SQUARE_64.getImage();
                overlayMap2.put(new Point(p.x - 6, p.y - 6), img);
                continue;
            }
            if (n.isAcquired()) {
                // Image img =
                // BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getEmitterPath();
                // overlayMap2.put(new Point(p.x - 6, p.y - 6), img);
            } else if (!CoreEngine.isArcaneVault()) {
                if (n.isAvailable()) {
                    Image img = BORDER.NEO_CYAN_HIGHLIGHT_SQUARE_64.getImage();
                    overlayMap2.put(new Point(p.x - 6, p.y - 6), img);
                    continue;
                }
            }
            if (n.isSelected()) {
                Image img = BORDER.NEO_INFO_SELECT_HIGHLIGHT_SQUARE_64.getImage();
                overlayMap2.put(new Point(p.x - 6, p.y - 6), img);
                continue;
            }

            Image img = BORDER.SHADOW_64.getImage();
            underlayMap.put(new Point(p.x - 4, p.y - 2), img);

        }
    }

    private void updateLinks() {
        if (HC_Master.getPreviousSelectedTreeNode() == HC_Master.getSelectedTreeNode()) {
            return;
        }

        HT_Node node = HC_Master.getSelectedTreeNode();
        linksToHighlight = new LinkedList<>();
        selectionPathNodes.clear();
        if (node != null) {
            ObjType type = node.getType();
            // could cache per type too
            while (true) {
                node = map.getNodeForType(node.getParentType());
                if (node == null) {
                    break;
                }
                selectionPathNodes.add(node);
                StaticTreeLink link = map.getLinkForChildType(node.getType());
                if (link == null) {

                }
                if (link == null) {
                    continue;
                }
                linksToHighlight.add(link);
            }
            linksToHighlight.add(map.getLinkForChildType(type));
        }

        dynamicLinks = new LinkedList<>();
        // add req links
        for (ObjType t : HC_Master.getRequiredSkills(getSelectedType(), false)) {
            XLine line = getLine(getSelectedType(), t);
            TreeLink link = new TreeLink(LINK_TYPE.REQUIRED, line);
            dynamicLinks.add(link);
        }
        // add node from another
        // special requirements (e.g. Mastery, deity, class...)

        DataManager.getChildren(getSelectedType(), getTypes());

    }

    private XLine getLine(ObjType source, ObjType target) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ObjType> getTypes() {
        return types;
    }

    protected void drawNodes(Graphics g) {
        Point selected = null;
        for (Point p : map.getNodeMap().keySet()) {
            HT_Node node = map.getNodeMap().get(p);
            if (node == HC_Master.getSelectedTreeNode()) {
                selected = p;
            } else {
                g.drawImage(node.getImage(), p.x, p.y, null);
            }
        }
        if (selected != null) {
            g.drawImage(HC_Master.getSelectedTreeNode().getImage(), selected.x, selected.y, null);
        }

    }

    private Image generateTreeImage() {
        // map.getDimension();
        // image = ImageManager.getNewBufferedImage(w, h);
        // VISUALS.SKILL_TREE;
        return null;
    }

    protected void drawUnderlays(Graphics g) {
        for (Point p : underlayMap.keySet()) {
            g.drawImage(underlayMap.get(p), p.x, p.y, null);
        }
    }

    protected void drawOverlays(Graphics g) {
        for (Point p : overlayMap.keySet()) {
            g.drawImage(overlayMap.get(p), p.x, p.y, null);
        }
        for (Point p : overlayMap2.keySet()) {
            g.drawImage(overlayMap2.get(p), p.x, p.y, null);
        }

    }

    public void updateReqText(ObjType type, List<String> reasons) {
        textMap.clear();
        Point basePoint = map.getPointForType(type);
        Color color = ColorManager.CRIMSON;
        int y = basePoint.y;
        for (String r : reasons) {
            int x = basePoint.x;
            Font font = getDefaultFont();
            y += FontMaster.getFontHeight(font) + 4;
            Point point = new Point(x, y);
            SmartText smartText = new SmartText(r, color);
            smartText.setFont(font);
            textMap.put(point, smartText);
        }
        // clear?

    }

    private Font getDefaultFont() {
        return FontMaster.getFont(FONT.MAIN, 16, Font.PLAIN);
    }

    protected void drawLinks(Graphics g) {
        for (XLine line : linkMap.keySet()) {
            g.setColor(linkMap.get(line).getColor());
            g.drawLine(line.getX1(), line.getX2(), line.getY1(), line.getY2());
        }
    }

    public ObjType getSelectedType() {
        if (HC_Master.getSelectedTreeNode() == null) {
            return null;
        }
        return HC_Master.getSelectedTreeNode().getType();
    }

    public G_Panel getPanel() {
        return panel;
    }

    public Unit getHero() {
        return hero;
    }

    public TreeMap getMap() {
        return map;
    }

    public Map<Point, Image> getUnderlayMap() {
        return underlayMap;
    }

    public Map<Point, Image> getOverlayMap() {
        return overlayMap;
    }

    public Map<XLine, LINK_TYPE> getLinkMap() {
        return linkMap;
    }

    public Image getBufferImage() {
        return bufferImage;
    }

    public void toggleViewMode() {
        viewMode = !viewMode;
        refresh();
        panel.repaint();
    }

    public boolean isViewMode() {
        if (mode == TREE_VIEW_MODE.FREE) {
            return true;
        }

        return viewMode;
    }

    public void setViewMode(boolean viewMode) {
        this.viewMode = viewMode;
    }

    public void setReqTextStrings(List<String> reasons) {
        this.reasons = reasons;

    }

    public enum LINK_TYPE {
        REQUIRED(ColorManager.ORANGE),
        UNLOCKS(ColorManager.ESSENCE),
        PROHIBITS(ColorManager.CRIMSON),
        OR(ColorManager.FOCUS);

        private Color c;

        LINK_TYPE(Color c) {
            this.c = c;
        }

        public Color getColor() {
            return c;
        }
    }

    public enum LIGHTING_VERSION {
        HIGHLIGHTED, DARKENED, AVAILABLE, NORMAL
    }

    public enum TREE_VIEW_MODE {
        FREE, BASIC, NORMAL, INFO
    }

}
