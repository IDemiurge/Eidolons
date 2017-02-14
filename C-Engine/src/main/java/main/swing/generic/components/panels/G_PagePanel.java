package main.swing.generic.components.panels;

import main.entity.obj.Obj;
import main.swing.SwingMaster;
import main.swing.generic.components.G_Component;
import main.swing.generic.components.G_Panel;
import main.swing.generic.components.misc.G_VisualComponent;
import main.system.auxiliary.log.LogMaster;
import main.system.graphics.GuiManager;
import main.system.auxiliary.data.ListMaster;
import main.system.auxiliary.Utilities;
import main.system.images.ImageManager;
import main.system.sound.SoundMaster;
import main.system.sound.SoundMaster.STD_SOUNDS;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.Transient;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * common data?
 *
 * @author JustMe
 */
public abstract class G_PagePanel<E> extends G_Panel {
    protected static final String CONTROLS_POS = "btn1";
    protected static final String CONTROLS_POS_2 = "btn2";
    protected List<G_Component> pages;
    protected List<E> data;
    protected int currentIndex = 0;
    protected G_Component currentComponent;

    protected boolean vertical;
    protected int pageSize;
    protected int wrap = 1;

    protected Integer itemSize;

    protected List<List<E>> pageData;
    protected Obj obj;
    protected int arrowWidth;
    protected int arrowHeight;
    protected boolean empty;
    protected int version;
    protected G_VisualComponent forwardButton;
    protected G_VisualComponent backButton;
    protected G_VisualComponent forwardButton2;
    protected G_VisualComponent backButton2;
    protected int createPageIndex;
    private boolean dirty = true;
    private MouseListener pageMouseListener;
    private PageWheelListener pageWheelListener;
    private PageKeyListener pageKeyListener;

    public G_PagePanel(int pageSize, boolean vertical, int version) {
        this.pageSize = pageSize;
        this.vertical = vertical;
        this.version = version;
        pageWheelListener = new PageWheelListener(this);
        pageKeyListener = new PageKeyListener(this);
        // refresh();
    }

    public void highlightsOff() {

    }

    // or set?
    public void highlight(E e) {

    }

    @Override
    @Transient
    public Dimension getPreferredSize() {

        return new Dimension(getPanelWidth(), getPanelHeight());
    }

    @Override
    @Transient
    public Dimension getMaximumSize() {

        return getPreferredSize();
    }

    // public Dimension getMinimumSize() {
    // return getPreferredSize();
    // } //crops!..

    public void initPages() {
        resetData();
        if (!isDirty() && (ListMaster.isNotEmpty(pages))) {
            return;
        }
        pages = new LinkedList<>();
        empty = false;
        if (!ListMaster.isNotEmpty(pageData)) {
            empty = true;
            pages.add(createEmptyPageComponent());
            setDirty(true);
        } else {
            createPageIndex = 0;
            for (List<E> list : pageData) {
                if (!ListMaster.isNotEmpty(list)) {
                    pages.add(createEmptyPageComponent());
                } else {
                    G_Component pageComponent = createPageComponent(list);
                    createPageIndex++;
                    if (pageMouseListener != null) {
                        SwingMaster.addMouseListener(pageComponent, pageMouseListener);
                        // if (createPageComponent instanceof G_PagePanel) {
                        // G_PagePanel g_PagePanel = (G_PagePanel)
                        // createPageComponent;
                        // g_PagePanel.setPageMouseListener(pageMouseListener);
                        // } else
                        // createPageComponent
                        // .addMouseListener(pageMouseListener);
                    }
                    pages.add(pageComponent);
                }
            }

        }
    }

    protected boolean isWheelMotionEnabled() {
        return true;
    }

    protected void resetData() {
        // dt flag

        List<List<E>> newData = getPageData();
        if (!new ListMaster<E>().compareNested(newData, pageData)) {
            setDirty(true);
            pageData = newData;
        } else {
            setDirty(false);
        }

    }

    protected List<List<E>> splitList(Collection<E> list) {

        List<List<E>> lists = new ListMaster<E>().splitList(pageSize, list);

        if (lists.isEmpty()) {
            List<E> lastList = new LinkedList<>();
            ListMaster.fillWithNullElements(lastList, pageSize);
            lists.add(lastList);
            return lists;
        }

        List<E> lastList = lists.get(lists.size() - 1);
        if (isFillWithNullElements()) {
            ListMaster.fillWithNullElements(lastList, pageSize);
        }
        return lists;
    }

    protected boolean isFillWithNullElements() {
        return true;
    }

    @SuppressWarnings("unchecked")
    protected G_Component createEmptyPageComponent() {
        return createPageComponent((List<E>) ListMaster.fillWithNullElements((new LinkedList<E>()),
                pageSize));
    }

    protected abstract G_Component createPageComponent(List<E> list);

    protected abstract List<List<E>> getPageData();

    protected G_VisualComponent getButton(boolean forward) {
        G_VisualComponent button;
        if (isControlPosInverted()) {
            forward = !forward;
        }
        if (isControlPosInverted()) {
            button = new G_VisualComponent(ImageManager.getArrowImagePath(!vertical, forward,
                    version));
        } else {
            button = new G_VisualComponent(ImageManager.getArrowImagePath(vertical, forward,
                    version));
        }

        button.addMouseListener(new PageMouseListener(this, button, forward));
        arrowWidth = button.getImageWidth();
        arrowHeight = button.getImageHeight();
        return button;
    }

    public void flipPage(boolean forward) {
        if (pages.size() < 2) {
            SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
            return;
            // TODO replace controls with other visuals?
        }
        if (isControlsInverted()) {
            forward = !forward;
        }
        if (forward) {
            currentIndex++;
            if (currentIndex > pages.size() - 1) {
                if (!isFlipOver()) {
                    SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
                    currentIndex = pages.size() - 1;
                    return;
                }
                currentIndex = 0;
            }
        } else {
            currentIndex--;
            if (currentIndex < 0) {
                if (!isFlipOver()) {
                    SoundMaster.playStandardSound(STD_SOUNDS.CLICK_BLOCKED);
                    currentIndex = 0;
                    return;
                }
                currentIndex = pages.size() - 1;
            }
        }
        SoundMaster.playStandardSound(STD_SOUNDS.PAGE_TURNED);
        refresh();

    }

    protected boolean isFlipOver() {
        return true;
    }

    protected boolean isControlsInverted() {
        if (isControlPosInverted()) {
            return !vertical;
        }
        return vertical;
    }

    public void adjustPageIndexToSelectTab(E e) {
        if (pageData == null) {
            return;
        }
        int i = 0;

        for (List<E> page : pageData) {
            if (page.contains(e)) {
                break;
            }
            i++;
        }
        if (getCurrentIndex() != i) {
            currentIndex = i;
            refresh();
        }

    }

    @Override
    public void refresh() {
        // if (isReinitOnRefresh() || !ListMaster.isNotEmpty(pages) ||
        // isDirty())
        if (isReinitOnRefresh()) {
            initPages();
        }
        if (currentIndex >= pages.size()) {
            currentIndex = 0;
        }
        try {
            if (!getCurrentComponent().equals(pages.get(currentIndex))
                    || getComponents().length == 0) {
                setDirty(true);
            }
        } catch (Exception e) {

        }
        setCurrentComponent(pages.get(currentIndex));

        if (currentComponent != null) {
            if (isWheelMotionEnabled()) {
                if (!pageWheelListener.getComponents().contains(currentComponent)) {
                    currentComponent.addMouseWheelListener(pageWheelListener);
                    pageWheelListener.getComponents().add(currentComponent);
                }
            }
        }
        if (!isDirty()) {
            if (getCurrentComponent() instanceof G_ListPanel) {
                G_ListPanel listPanel = (G_ListPanel) getCurrentComponent();
                listPanel.refresh();
            }
            repaint();
            return;
        }
        if (getCurrentComponent() != null) {
            removeAll();

            addComponents();
            if (isRevalidateOnRefresh() || !getCurrentComponent().isValidated()) {
                revalidate();
                getCurrentComponent().setValidated(true);
            }

        } else {
            LogMaster.log(1, "Null component on "
                    + getClass().getSimpleName() + " with " + getData());
        }
        repaint();
    }

    @Override
    public void removeAll() {
        if (isBackedByEmptyComponent()) {
            for (int i = getComponentCount() - 1; i > 0; i--) {
                remove(i);
            }
        } else {
            super.removeAll();
        }
    }

    protected boolean isBackedByEmptyComponent() {
        return false;
    }

    protected boolean isReinitOnRefresh() {
        return true;
    }

    protected boolean isRevalidateOnRefresh() {
        return true;
    }

    protected void addComponents() {

        String pos = getPagePos();
        if (isBackedByEmptyComponent()) {
            add(createEmptyPageComponent(), 0);
        }
        add(getCurrentComponent(), pos);
        if (pages.size() > 1 || isAddControlsAlways()) {
            addControls();
        }
    }

    protected String getPagePos() {
        String x = getCompDisplacementX();
        String y = getCompDisplacementY();
        boolean vertical = this.vertical;
        if (isControlPosInverted()) {
            vertical = !vertical;
        }

        if (isComponentAfterControls()) {
            if (vertical) {
                y = (isControlPosInverted() ? CONTROLS_POS_2 : CONTROLS_POS) + ".y2";
            } else {
                x = (!isControlPosInverted() ? CONTROLS_POS_2 : CONTROLS_POS) + ".x2";
            }
        }

        String pos = "pos " + x + " " + y;
        return pos;
    }

    protected String getCompDisplacementY() {
        return "0";
    }

    protected String getCompDisplacementX() {
        return "0";
    }

    protected boolean isComponentAfterControls() {
        return true;
    }

    protected boolean isAddControlsAlways() {
        return true;
    }

    public int getPanelWidth() {
        return !vertical ? pageSize / getWrap() * getItemSize() : getWrap() * getItemSize();
    }

    public int getPanelHeight() {
        return vertical ? pageSize / getWrap() * getItemSize() : getWrap() * getItemSize();
    }

    protected int getItemSize() {
        if (itemSize != null) {
            return itemSize;
        }
        return GuiManager.getSmallObjSize();
    }

    protected void addControls() {
        if (forwardButton == null) {
            forwardButton = getButton(true);
        }
        if (backButton == null) {
            backButton = getButton(false);
        }

        int x = getArrowOffsetX();
        int x2 = getArrowOffsetX2();
        int y = getArrowOffsetY();
        int y2 = getArrowOffsetY2();
        boolean bothEnds = isButtonsOnBothEnds();
        boolean stickTogether = isDoubleButtons();
        boolean verticalPos = vertical;
        if (isControlPosInverted()) {
            vertical = !vertical;
        }
        if (verticalPos) {
            int w = (stickTogether) ? arrowWidth * 2 : arrowWidth;
            x += (getPanelWidth() - w) / 2;
            if (x < 0) {
                stickTogether = false;
                x = (getPanelWidth() - arrowWidth) / 2;
            }
            // y = 0;
            x2 = x;

            y2 += getPanelHeight() + arrowHeight;
        } else {
            int h = (stickTogether) ? arrowHeight * 2 : arrowHeight;
            y += (getPanelHeight() - h) / 2;
            if (y < 0) {
                stickTogether = false;
                y = (getPanelHeight() - arrowHeight) / 2;
            }
            // x = 0;
            y2 = y;
            x2 += getPanelWidth() + arrowWidth;
        }
        if (isControlPosInverted()) {
            vertical = !vertical;
        }
        String pos = ((verticalPos) ? x + " " + y : x2 + " " + y2);
        if (bothEnds) {
            addControl(forwardButton, false, (!verticalPos) ? x2 : x, (!verticalPos) ? y2 : y);
            // add(forwardButton, "id " + CONTROLS_POS + ", pos " + pos);
            if (stickTogether) {
                if (vertical) {
                    pos = "btn1.x2 " + y;
                } else {
                    pos = x + " btn1.y2";
                }
            } else {
                pos = ((vertical) ? x2 + " " + y2 : x + " " + y);
            }
            addControl(backButton, true, (vertical) ? x2 : x, (vertical) ? y2 : y);
            // add(backButton, "id btn2, pos " + pos);
        } else {
            addControl((isForwardPreferred()) ? forwardButton : backButton, !isForwardPreferred(),
                    isForwardPreferred() ? x : y, isForwardPreferred() ? y : x);
            // + ((isForwardPreferred()) ? x + " " + y : +x2 + " " + y2));
        }

        if (stickTogether && bothEnds) {
            if (forwardButton2 == null) {
                forwardButton2 = getButton(true);
            }
            if (backButton2 == null) {
                backButton2 = getButton(false);
            }
            pos = x2 + " " + y2;
            add(backButton2, "id btn3, pos " + pos);
            if (vertical) {
                pos = "btn3.x2 " + y2;
            } else {
                pos = x2 + " btn3.y2";
            }
            add(forwardButton2, "id btn4, pos " + pos);

        }

    }

    protected void addControl(G_VisualComponent component, boolean second, int x, int y) {
        String pos = "id " + ((second) ? CONTROLS_POS_2 : CONTROLS_POS) + ",pos "
                + (isControlPosInverted() ? y : x) + " " + (!isControlPosInverted() ? y : x);
        add(component, pos);

    }

    protected boolean isControlPosInverted() {
        return false;
    }

    protected boolean isForwardPreferred() {
        return vertical;
    }

    protected int getArrowOffsetY2() {
        return 0;
    }

    protected int getArrowOffsetY() {
        return 0;
    }

    protected int getArrowOffsetX2() {
        return 0;
    }

    protected int getArrowOffsetX() {
        return 0;
    }

    protected boolean isDoubleButtons() {
        return false;
    }

    public boolean isButtonsOnBothEnds() {
        return true;
    }

    public void dataChanged() {
        initPages();
        currentIndex = 0;
    }

    public List<E> getData() {
        if (data == null) {
            data = new ListMaster<E>().openSubLists(getPageData());
        }
        return data;
    }

    public void setData(Collection<E> data) {
        if (data instanceof List) {
            this.data = (List<E>) data;
        } else {
            this.data = new LinkedList<>(data);
        }
    }

    public Obj getObj() {
        return obj;
    }

    public void setObj(Obj obj) {
        if (!Utilities.compare(this.obj, obj)) {
            setDirty(true);
        }
        this.obj = obj;
    }

    public boolean isPreferredEndForward() {
        return true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPageMouseListener(MouseListener mouseListener) {
        this.pageMouseListener = mouseListener;
    }

    public int getArrowWidth() {
        if (arrowWidth == 0) {
            arrowWidth = getButton(true).getImageWidth();
        }
        return arrowWidth;
    }

    public int getArrowHeight() {
        if (arrowHeight == 0) {
            arrowHeight = getButton(true).getImageHeight();
        }
        return arrowHeight;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public G_Component getCurrentComponent() {
        return currentComponent;
    }

    public void setCurrentComponent(G_Component currentComponent) {
        this.currentComponent = currentComponent;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getIndex() {
        return currentIndex;
    }

    public int getWrap() {
        return wrap;
    }

    public class PageMouseListener implements MouseListener {

        protected G_PagePanel<E> panel;
        protected boolean forward;
        protected G_VisualComponent button;

        public PageMouseListener(G_PagePanel<E> panel, G_VisualComponent button, boolean forward) {
            this.panel = panel;
            this.forward = forward;
            this.button = button;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            panel.flipPage(forward);

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

}
