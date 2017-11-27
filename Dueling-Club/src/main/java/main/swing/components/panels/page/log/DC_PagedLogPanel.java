package main.swing.components.panels.page.log;

import main.content.values.properties.G_PROPS;
import main.entity.obj.Obj;
import main.game.bf.Coordinates;
import main.game.core.game.DC_Game;
import main.swing.components.ImageButton;
import main.swing.components.buttons.CustomButton;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.system.audio.DC_SoundMaster;
import main.system.auxiliary.data.ListMaster;
import main.system.graphics.ANIM;
import main.system.graphics.FontMaster;
import main.system.graphics.PhaseAnimation;
import main.system.sound.SoundMaster.STD_SOUNDS;
import main.system.text.EntryNodeMaster;
import main.system.text.LogEntryNode;
import main.system.text.LogManager;
import main.system.text.TextWrapper;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class DC_PagedLogPanel extends G_PagePanel<String> implements MouseListener {

    private static final VISUALS BUTTON_VISUALS = VISUALS.GEARS;
    private static final int PAGE_SIZE = 0;
    private static final int VERSION = 3;

    private static final int SIZE = 16;
    boolean descriptionMode; // TODO for values/... selectable on any page!
    boolean nodeViewMode;
    LogEntryNode entryNode;
    List<LogEntryNode> entryNodes = new ArrayList<>();
    private DC_Game game;
    private int descrWrapLength = 40;
    private int rowCount;
    private boolean flipping;
    private CustomButton toggleButton;
    private CustomButton returnButton;
    private Stack<LogEntryNode> nodeStack = new Stack<>();
    private int cachedTopPageIndex;

    public DC_PagedLogPanel(DC_Game game) {
        super(PAGE_SIZE, true, VERSION);
        this.game = game;
        setFont(EntryNodeMaster.getTopFont());
        rowCount = getRowCount();
        descrWrapLength = getWrapLength();

    }


    private int getWrapLength() {
        return FontMaster.getStringLengthForWidth(getFont(), EntryNodeMaster.INNER_WIDTH);
    }

    private int getRowCount() {
        return EntryNodeMaster.INNER_HEIGHT / EntryNodeMaster.getRowHeight(isTopPage());
    }

    protected boolean isFlipOver() {
        return descriptionMode;
    }

    protected void addComponents() {
        String pos = "pos 0 0";
        add(getCurrentComponent(), pos);
        addControls();

        returnButton = new CustomButton(VISUALS.ADD) {
            public void handleClick() {
                back();
            }

            protected void playClickSound() {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.SLING);
            }

            public VISUALS getVisuals() {
                if (!isEnabled()) {
                    return VISUALS.ADD_BLOCKED;
                }
                return super.getVisuals();
            }

            public boolean isEnabled() {
                return true; // nodeViewMode
            }
        };

        // toggleButton = new CustomButton(BUTTON_VISUALS) {
        // public void handleClick() {
        // toggleMode();
        // }
        //
        // protected void playSound() {
        // DC_SoundMaster.playStandardSound(STD_SOUNDS.CLOCK);
        // }
        //
        // };
        //
        // pos = "pos " + (getPanelWidth() - BUTTON_VISUALS.getWidth()) + " "
        // + (getPanelHeight() - BUTTON_VISUALS.getHeight()) + "";
        // add(toggleButton, pos);

        pos = "pos 0 0";
        add(returnButton, pos);
        int i = 0;
        if (entryNodes != null) {
            for (final LogEntryNode node : entryNodes) {
                int lineIndex = (node.getLineIndex()) % getRowCount();
                if (lineIndex == 0) {
                    lineIndex = getRowCount();
                }
                Integer y = Math.min(getPanelHeight() - EntryNodeMaster.getRowHeight(isTopPage()),
                        EntryNodeMaster.getRowHeight(isTopPage()) * (lineIndex));

                // TODO
                // top
                // vs
                // generic
                // separate!
                ImageButton entryNodeButton = new ImageButton((node.getButtonImagePath())) {
                    @Override
                    public void handleClick() {
                        if (entryNode == null) {
                            setCachedTopPageIndex(getCurrentIndex());
                        }
                        if (node.getLinkedAnimation() != null) {
                            for (ANIM anim : node.getLinkedAnimations()) {
                                PhaseAnimation animation = (PhaseAnimation) anim;
                                // DC_Game.game.getAnimationManager()
                                // .getAnimation(key);
                                animation.setPhaseFilter(node.getAnimPhasesToPlay());
                                if (animation != null) {
                                    animation.setReplay(true);
                                    animation.start();
                                    animation.setAutoFinish(false);
                                }
                            }

                            Coordinates bufferedOffset = game.getAnimationManager().updatePoints();
                            game.getManager().refreshGUI();
                            game.getBattleField().getGrid().setNextOffsetCoordinate(bufferedOffset);
                        }

                        setNodeView(node);

                    }

                    @Override
                    protected void playClickSound() {
                        DC_SoundMaster.playStandardSound(STD_SOUNDS.DIS__OPEN_MENU);
                    }

                };

                pos = "pos " + 0 + " " + (y);
                add(entryNodeButton, pos);
                entryNodeButton.activateMouseListener();
                setComponentZOrder(entryNodeButton, i);
                i++;
            }
        }

        setComponentZOrder(returnButton, i);
        i++;
        // setComponentZOrder(toggleButton, i);
        // i++;
        setComponentZOrder(forwardButton, i);
        i++;
        setComponentZOrder(backButton, i);
        i++;
        setComponentZOrder(getCurrentComponent(), i);

    }

    protected void back() {
        if (!nodeStack.isEmpty()) {
            entryNode = nodeStack.pop();

        }
        boolean refreshAll = false;
        if (entryNode != null) {
            if (entryNode.getLinkedAnimation() != null) {
                for (ANIM anim : entryNode.getLinkedAnimations()) {
                    PhaseAnimation animation = (PhaseAnimation) anim;
                    animation.setAutoFinish(true);
                    if (!animation.isPaused()) {
                        if (animation.stopAndRemove()) {

                            refreshAll = true;
                        }
                    }
                }
            }
        }
        if (nodeStack.isEmpty()) {
            entryNode = null;
            nodeViewMode = false;
            currentIndex = getCachedTopPageIndex();
        } else {
            currentIndex = entryNode.getPageIndex();
        }
        // if null?
        if (refreshAll) {
            game.getManager().refresh(false);
        } else {
            refresh();
        }
    }

    protected void setNodeView(LogEntryNode node) {
        if (entryNode != null) {
            nodeStack.push(this.entryNode);
        }
        this.entryNode = node;
        nodeViewMode = true;
        refresh();
    }

    @Override
    protected int getArrowOffsetX2() {

        return -2 * forwardButton.getImageWidth();
    }

    @Override
    protected int getArrowOffsetY2() {

        return -2 * forwardButton.getImageHeight();
    }

    @Override
    public void initPages() {
        super.initPages();
        if (LogManager.isDirty()) {
            currentIndex = pages.size() - 1;
            main.system.text.LogManager.setDirty(false);
        }
        setDirty(true);
    }

    @Override
    public void flipPage(boolean forward) {
        flipping = (true);
        super.flipPage(!forward);
        flipping = (false);
    }

    @Override
    public void refresh() {

        setFont(EntryNodeMaster.getFont(isTopPage()));

        if (nodeViewMode && entryNode != null) {
            entryNodes = entryNode.getChildren();
            // only for this page TODO
            setFont(EntryNodeMaster.getSubNodeFont());
        } else {
            setFont(EntryNodeMaster.getTopFont()); // TODO will the index be
            // correct??
            entryNodes = game.getLogManager().getTopEntryNodesMap().get(getCurrentIndex());
            if (entryNodes == null) {
                entryNodes = new ArrayList<>();
                for (LogEntryNode node : game.getLogManager().getTopNodes()) {
                    if (node.getLineIndex() > getRowCount() * getCurrentIndex()
                            && node.getLineIndex() <= (getCurrentIndex() + 1) * getRowCount())
                    // if (node.getPageIndex() == getCurrentIndex())
                    // line index range for the page
                    {
                        entryNodes.add(node);
                    }
                }
                // cache into a map if page is complete
                if (getPageData().size() - 1 > getCurrentIndex()) {
                    game.getLogManager().getTopEntryNodesMap().put(getCurrentIndex(), entryNodes);
                }
            }
        }
        setDirty(true);
        super.refresh();

    }

    @Override
    protected G_Component createEmptyPageComponent() {
        // TODO ???
        return null;
    }

    @Override
    protected G_Component createPageComponent(List<String> list) {
        return new TextPage(list, this);
    }

    @Override
    protected List<List<String>> getPageData() {
        if (descriptionMode && game.getManager().getInfoObj() != null) {
            Obj entity = game.getManager().getInfoObj();
            String descr = entity.getDescription();
            String lore = entity.getProperty(G_PROPS.LORE);
            List<String> descrPages = TextWrapper.wrap(descr, descrWrapLength);

            List<String> lorePages = TextWrapper.wrap(lore, descrWrapLength);
            List<List<String>> list = new ArrayList<>();
            list.addAll(new ListMaster<String>().splitList(rowCount, descrPages));
            list.addAll(new ListMaster<String>().splitList(rowCount, lorePages));
            return list;
        }
        if (nodeViewMode) {
            List<String> lines = entryNode.getTextLines();
            // if (lines == null)
            // lines = TextWrapper.wrap(node.getText(), wrapLength);

            return new ListMaster<String>().splitList(rowCount, lines);
        }

        ArrayList<String> lines = new ArrayList<>();
        List<String> entries = game.getLogManager().getTopDisplayedEntries();
        for (String entry : entries) {
            for (String subString : TextWrapper.wrapIntoArray(entry, EntryNodeMaster
                    .getWrapLength(isTopPage()))) {
                lines.add(subString);
            }
        }
        // splitList(list)
        return new ListMaster<String>().splitList(getRowCount(), lines);
    }

    @Override
    protected List<List<String>> splitList(Collection<String> list) {
        // split entries (knowing their assumed line number+gap), then try to
        // fit into one page at a time
        // don't re-init old pages! make a cache to update
        List<List<String>> lists = new ListMaster<String>().splitList(pageSize, list);
        if (lists.isEmpty()) {
            List<String> lastList = new ArrayList<>();
            ListMaster.fillWithNullElements(lastList, pageSize);
            lists.add(lastList);
            return lists;
        }
        List<String> lastList = lists.get(lists.size() - 1);
        if (isFillWithNullElements()) {
            ListMaster.fillWithNullElements(lastList, pageSize);
        }
        return lists;
    }

    private boolean isTopPage() {
        return !nodeViewMode;
    }

    protected boolean isControlsInverted() {
        return false;
    }

    public void toggleMode() {
        descrWrapLength = getWrapLength();
        descriptionMode = !descriptionMode;
        this.vertical = true;
        forwardButton = null;
        backButton = null;
        // if (pageBuffer == null){
        // getPageData();
        // }
        // buffer = pageBuffer;
        // pageBuffer = pages;
        // pages = buffer

        refresh();

    }

    @Override
    public int getPanelWidth() {
        return VISUALS.INFO_PANEL.getImage().getWidth(null);
    }

    @Override
    public int getPanelHeight() {
        return VISUALS.INFO_PANEL.getImage().getHeight(null);
    } // or LogString?

    public int getCachedTopPageIndex() {
        return cachedTopPageIndex;
    }

    public void setCachedTopPageIndex(int cachedTopPageIndex) {
        this.cachedTopPageIndex = cachedTopPageIndex;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (entryNode != null) {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.SLING);
            } else {
                DC_SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            }
            back();
        }

    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

}
