package libgdx.gui.dungeon.overlay.choice;

import main.system.auxiliary.StringMaster;

public class VC_Option {
    Object arg;
    public String title;
    public String tooltip;
    public String image;

    public VC_Option(Object arg, String title, String tooltip, String image) {
        this.title = title;
        this.tooltip = tooltip;
        this.image = image;
        this.arg = arg;
    }

    public VC_Option(VC_DataSource.VC_OPTION type) {
        this.arg = type;
        this.title = StringMaster.format(type.toString());
        image = (type.img);
        //TODO localize
        tooltip = "Choose " + title;
    }

    public boolean checkDisabled() {
        //to override for custom
        return false;
        // return VisualChoiceHandler.checkOptionDisabled(type);
    }

    public Object getArg() {
        return arg;
    }

    public String getTitle() {
        return title;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getImage() {
        return image;
    }
}
