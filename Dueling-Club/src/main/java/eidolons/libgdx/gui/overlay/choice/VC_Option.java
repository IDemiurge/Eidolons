package eidolons.libgdx.gui.overlay.choice;

import main.system.auxiliary.StringMaster;

public class VC_Option {
    VC_DataSource.VC_OPTION type;
    public String title;
    public String tooltip;
    public String image;

    //any custom?!
    public VC_Option(VC_DataSource.VC_OPTION type) {
        this.type = type;
        this.title = StringMaster.getWellFormattedString(type.toString());
        image = (type.img);
        //TODO localize
        tooltip = "Choose " + title;
    }

    public boolean checkDisabled() {
        //to override for custom
        return false;
        // return VisualChoiceHandler.checkOptionDisabled(type);
    }
}
