package main.swing.components;

import main.swing.generic.components.G_Component;
import main.swing.generic.components.panels.G_PagePanel;
import main.swing.listeners.OptionListener;
import main.system.auxiliary.EnumMaster;
import main.system.auxiliary.StringMaster;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class PagedOptionsComp<E> extends G_PagePanel<E> {

    List<OptionListener<E>> listeners;
    private Class<E> clazz;
    private String tooltip;

    public PagedOptionsComp(String tooltip, Class clazz) {
        super(1, false, 4);
        this.tooltip = tooltip;
        this.clazz = clazz;
        setData(new EnumMaster<E>().getEnumList(clazz));
    }

    @Override
    protected int getArrowOffsetX() {
        return super.getArrowOffsetX();
    }

    @Override
    protected int getArrowOffsetX2() {
        return super.getArrowOffsetX2();
    }

    @Override
    protected G_Component createPageComponent(List<E> list) {
        return

                new TextComp(VISUALS.OPTION_COMP, getTooltip()
                        + StringMaster.getWellFormattedString(list.get(0).toString()));
    }

    private String getTooltip() {
        return tooltip;
    }

    @Override
    public void flipPage(boolean forward) {
        super.flipPage(forward);

        for (OptionListener<E> listener : listeners) {
            listener.optionSelected(getData().get(getCurrentIndex()));
        }
    }

    @Override
    protected List<List<E>> getPageData() {
        return splitList(Arrays.asList(clazz.getEnumConstants()));
    }

    public Class<E> getClazz() {
        return clazz;
    }

    public List<OptionListener<E>> getListeners() {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        return listeners;
    }

    public boolean addListener(OptionListener<E> e) {
        return getListeners().add(e);
    }

}
