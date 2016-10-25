package main.swing.components.panels.page.info;

import main.content.VALUE;
import main.entity.Entity;
import main.swing.components.panels.page.info.element.ParamElement;
import main.swing.components.panels.page.info.element.ValueTextComp;

import java.util.List;

public class ParameterPage extends ValueInfoPage {

    String header;
    int columns = 2;

    public ParameterPage(String header, List<VALUE> list, Entity entity) {
        super(list, entity, header);
        this.header = header;
        initialized = true;
        init();
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    protected void addHeader() {
        i++;
        super.addHeader();
    }

    @Override
    protected int getDefaultY() {
        return super.getDefaultY();
        // return (header != null) ? super.getDefaultY()
        // + PropertyElement.ALT_HEADER_COMPONENT.getImg().getHeight(null)
        // : super.getDefaultY();
    }// values.size()/getColumns() > height/rowheight

    protected int getColumns() {
        return 2;
    }

    @Override
    protected ValueTextComp getComponent(VALUE p) {
        ParamElement paramElement = new ParamElement(p);
        paramElement.setSize(getColumnWidth(), getRowHeight());
        paramElement.setEntity(entity);
        return paramElement;
    }

    @Override
    protected int getRowHeight() {
        return VISUALS.VALUE_BOX_SMALL.getImage().getHeight(null);
        // 24;
    }

    @Override
    protected int getColumnWidth() {
        return VISUALS.VALUE_BOX_SMALL.getImage().getWidth(null);
        // VISUALS.PANEL.getImg().getWidth(null) / 2;
    }

}
