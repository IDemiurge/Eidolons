package main.client.cc.gui.pages;

import main.content.OBJ_TYPES;
import main.swing.components.panels.page.info.DC_PagedInfoPanel;

public class HC_PagedInfoPanel extends DC_PagedInfoPanel {

    @Override
    public boolean isDirty() {
        // for spell upgrades page re-init
        if (getEntity() != null)
            // if (getCurrentIndex() > 2)
            if (getEntity().getOBJ_TYPE_ENUM() == OBJ_TYPES.SPELLS)
                return true;
        return super.isDirty();
    }
    // private boolean descriptionMode;
    // private int wrapLength;
    // private CustomButton toggleButton;
    // private int rowCount;
    // private static final VISUALS BUTTON_VISUALS = VISUALS.QUESTION;
    // private static final int FONT_SIZE = 0;
    //
    // public HC_PagedInfoPanel(Entity entity) {
    // super(entity);
    // setFont(FontMaster.getDefaultFont(FONT_SIZE));
    // rowCount = ValueInfoPage.INNER_HEIGHT /
    // FontMaster.getFontHeight(getFont());
    // wrapLength = FontMaster
    // .getStringLengthForWidth(getFont(), ValueInfoPage.INNER_WIDTH);
    // }
    //
    // @Override
    // protected List<List<VALUE>> getPageData() {
    //
    // if (descriptionMode) {
    // String descr = entity.getDescription();
    // String lore = entity.getProperty(G_PROPS.LORE);
    // List<String> descrPages = TextWrapper.wrap(descr, wrapLength);
    //
    // List<String> lorePages = TextWrapper.wrap(lore, wrapLength);
    // List<List<String>> list = new LinkedList<>();
    // list.addAll(new ListMaster<String>()
    // .splitList(rowCount, descrPages));
    // list.addAll(new ListMaster<String>().splitList(rowCount, lorePages));
    // return list;
    // }
    //
    // return super.getPageData();
    // }
    //
    // @Override
    // protected G_Component createPageComponent(List<VALUE> list) {
    // return super.createPageComponent(list);
    // }
    //
    // protected void addComponents() {
    // String pos = "pos 0 0";
    // add(getCurrentComponent(), pos);
    // addControls();
    //
    // toggleButton = new CustomButton(BUTTON_VISUALS) {
    // @Override
    // public void handleClick() {
    // toggleMode();
    // }
    //
    // @Override
    // protected void playSound() {
    // super.playSound();
    // }
    //
    // @Override
    // public boolean isEnabled() {
    // return super.isEnabled();
    // }
    // };
    //
    // pos = "pos " + (getPanelWidth() - BUTTON_VISUALS.getWidth()) + " "
    // + (getPanelHeight() - BUTTON_VISUALS.getHeight()) + "";
    // add(toggleButton, pos);
    // toggleButton.activateMouseListener();
    //
    // int i = 0;
    // setComponentZOrder(toggleButton, i);
    // i++;
    // setComponentZOrder(forwardButton, i);
    // i++;
    // setComponentZOrder(backButton, i);
    // i++;
    // setComponentZOrder(getCurrentComponent(), i);
    // }
    //
    // public void toggleMode() {
    // wrapLength = FontMaster
    // .getStringLengthForWidth(getFont(), ValueInfoPage.INNER_WIDTH);
    // descriptionMode = !descriptionMode;
    // this.vertical = !descriptionMode;
    // forwardButton = null;
    // backButton = null;
    // refresh();
    //
    // }
}
