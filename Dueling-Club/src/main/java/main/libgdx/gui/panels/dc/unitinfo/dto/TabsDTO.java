package main.libgdx.gui.panels.dc.unitinfo.dto;

import java.util.ArrayList;
import java.util.List;

public class TabsDTO {
    private List<List<ValueDTO>> tabs;
    private List<ValueDTO> current;



    public TabsDTO() {
        tabs = new ArrayList<>();
    }


        public TabsDTO newTab() {
        current = new ArrayList<>();
        tabs.add(current);
        return this;
    }

    public TabsDTO addValue(ValueDTO valueDTO) {
        current.add(valueDTO);
        return this;
    }

    public List<List<ValueDTO>> getTabs() {
        return tabs;
    }
}
