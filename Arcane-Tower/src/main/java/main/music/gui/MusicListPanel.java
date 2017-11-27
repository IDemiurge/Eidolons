package main.music.gui;

import main.content.VALUE;
import main.content.values.parameters.PARAMETER;
import main.content.values.properties.PROPERTY;
import main.data.DataManager;
import main.data.XLinkedMap;
import main.entity.type.ObjType;
import main.enums.StatEnums.MUSIC_TAGS;
import main.logic.AT_OBJ_TYPE;
import main.music.MusicCore;
import main.music.ahk.AHK_Master;
import main.swing.generic.components.G_Panel;
import main.system.SortMaster;
import main.system.auxiliary.StringMaster;
import main.system.auxiliary.data.FileManager;
import main.system.graphics.GuiManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class MusicListPanel extends G_Panel {
    public static final int max_length = 12;
    static Map<String, String> lineMap = new HashMap<>();
    protected String filterValue;
    protected MusicViewsPanel viewsPanel;
    protected G_Panel view;
    protected String key;
    protected MusicDisplayPanel displayPanel;
    protected boolean sortByTag;
    protected boolean sortDescending;
    protected Map<String, List<String>> lastMap;
    VALUE sortValue;
    String tagFilter;
    String groupFilter;
    PROPERTY filterProp;
    Boolean filterOut;
    Boolean highlight_disable_remove;
    Map<String, List<String>> listMap;
    private MC_ControlPanel controlPanel;
    private G_Panel sidePanel;
    private Map<G_Panel, JScrollPane> scrollsMap=new HashMap();

    public MusicListPanel(String key, Map<String, List<String>> map) {
        super("flowy");
        this.key = key;
        listMap = map;
        controlPanel = new MC_ControlPanel();
        viewsPanel = new MusicViewsPanel(this);
        displayPanel = new MusicDisplayPanel(this);
        setPanelSize(new Dimension(GuiManager.getScreenWidthInt(), GuiManager.getScreenHeightInt()));

        sidePanel = new G_Panel("flowy");
        sidePanel.add(displayPanel);
        sidePanel.add(viewsPanel, "x 100");
        sidePanel.add(controlPanel);
    }

    public static String formatListName(String funcPart) {
        funcPart = StringMaster.cropFormat(StringMaster.getLastPathSegment(funcPart));
        if (funcPart.indexOf(".") != -1) {
            funcPart = funcPart.substring(funcPart.indexOf(".") + 1);
        }
        funcPart = funcPart.replace("Battle Mix ", "Bat.");
        funcPart = StringMaster.getWellFormattedString(funcPart);

        return funcPart;
    }

    public static int getMaxLength() {
        return max_length;
    }

    public void init() {
        // map.put(letter, line);
        initViews();
        refresh();

    }

    protected Comparator<String> getComparator(final String chars) {
        return new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                // TODO need to group per letter and sort by modifiers same
                // way...
                int code = AHK_Master.getLetterCode(o1, chars);
                int code2 = AHK_Master.getLetterCode(o2, chars);
                if (code == code2) {
                    return 0;
                }
                if (code > code2) {
                    return 1;
                }
                return -1;
            }
        };
    }

    @Override
    public void refresh() {
        removeAll();

        if (view != null) {
            add(getScrolledView(view) );
        } else {
            view = initView(listMap, isLetterShown());
            add(view);
        }
        displayPanel.refresh();
        viewsPanel.refresh();
        add(sidePanel, "pos " + getControlPanelX() + " 0");
        // add(displayPanel, "pos @max_x 0");
        // add(displayPanel, "pos " + GuiManager.getScreenWidthInt() +
        // "-210 0");
        // displayPanel.refresh();
        // add(viewsPanel, "pos " + GuiManager.getScreenWidthInt() + "-210 "
        // + GuiManager.getScreenHeightInt() + "-880");
        // viewsPanel.refresh();
        // add(controlPanel, "pos " + getControlPanelX() + " " +
        // getControlPanelY());
        super.refresh();
        revalidate();
    }

    private JScrollPane getScrolledView(G_Panel view) {
        JScrollPane scroll=scrollsMap.get(view);
        if (scroll == null ){
            scroll = new JScrollPane(view);

            scrollsMap.put(view, scroll);
        }
        return scroll;
    }

    protected String getControlPanelX() {
        return GuiManager.getScreenWidthInt() + "-325";
    }

    protected String getControlPanelY() {
        return GuiManager.getScreenHeightInt() + "-420";
    }

    protected void initFilteredViews() {
        int i = 0;
        while (true) {
            MusicListPanel v = MusicCore.getGroupedView(i);
            viewsPanel.addView(v.getView().getName(), v);
            if (i >= MusicCore.std_groups.length) {
                break;
            }
            i++;

        }
        i = 0;
        while (true) {
            MusicListPanel v = MusicCore.getGroupedView(i, MUSIC_TAGS.class);
            viewsPanel.addView(v.getView().getName(), v);
            if (i >= MusicCore.std_tags.length) {
                break;
            }
            i++;

        }
    }

    protected void initViews() {
        view = initView(listMap, isLetterShown());
        viewsPanel.addView(key, this);
        for (String script : AHK_Master.scripts) {
            Map<String, List<String>> map = AHK_Master.getScriptViewMap(script);
            MusicListPanel panel = new MusicListPanel(script, map);
            panel.setView(panel.initView(map, isLetterShown()));
            viewsPanel.addView(script, panel);
            // viewsPanel.addView(script, map));
        }
        initCustomViews();
        if (!MusicCore.initMusicListTypes)// TODO into combobox!
        {
            initGeneratedViews();
        }
        initFilteredViews();
    }

    protected void initGeneratedViews() {
        Map<String, List<String>> customMap = null;

        List<File> rootDirs = FileManager.getFilesFromDirectory(AHK_Master.GENERATED_LISTS_FOLDER,
         true);
        for (File dir : rootDirs) {
            String rootFolder = dir.getPath();
            List<File> filesFromDirectory = FileManager.getFilesFromDirectory(rootFolder, true);
            Map<String, List<String>> map = getCustomMap(filesFromDirectory, customMap);
            if (checkNewMap(customMap)) {
                customMap = new XLinkedMap<>();
                String viewName = "Generated-" + StringMaster.getLastPathSegment(rootFolder);
                MusicListPanel panel = new MusicListPanel(key, map);
                panel.setView(panel.initView(map, isLetterShown()));
                viewsPanel.addView(viewName, panel);

            }

        }
    }

    protected boolean checkNewMap(Map<String, List<String>> customMap) {
        if (customMap == null) {
            return true;
        }
        return customMap.size() > 4;
    }

    protected void initCustomViews() {
        List<File> topList = new ArrayList<>();
        Map<String, List<String>> customMap = new XLinkedMap<>();
        for (File file : FileManager.getFilesFromDirectory(AHK_Master.CUSTOM_LISTS_FOLDER, true)) {
            if (file.isDirectory()) {
                String name = file.getName();
                G_Panel view = initView(getCustomMap(name, FileManager.getFilesFromDirectory(file
                 .getPath(), false), customMap), isLetterShown());
                // viewsPanel.addView(file.getName(), view);
            } else {
                topList.add(file);
            }
        }
        // add root to the subpanel map
        Map<String, List<String>> map = getCustomMap(topList, customMap);
        MusicListPanel panel = new MusicListPanel(key, map);
        panel.setView(panel.initView(map, isLetterShown()));

        viewsPanel.addView("Custom", panel);
    }

    protected Map<String, List<String>> getCustomMap(List<File> filesFromDirectory,
                                                     Map<String, List<String>> map) {
        return getCustomMap(null, filesFromDirectory, map);
    }

    protected Map<String, List<String>> getCustomMap(String mappedLetters,
                                                     List<File> filesFromDirectory, Map<String, List<String>> map) {
        if (map == null) {
            map = new XLinkedMap<>();
        } else {

        }
        List<String> list = new ArrayList<>();
        for (File sub : filesFromDirectory) {
            String letter = getCustomListLetter(map, list, sub);
            String listString = letter + "::" + sub.getPath();
            list.add(listString);
        }
        if (mappedLetters == null) {
            mappedLetters = getCustomListMappedLetters(map);
        }
        map.put(mappedLetters, list);
        return map;
    }

    protected String getCustomListLetter(Map<String, List<String>> map, List<String> list, File sub) {
        String chars = getCustomListMappedLetters(map);
        return "" + chars.charAt(list.size() % chars.length());
    }

    protected String getCustomListMappedLetters(Map<String, List<String>> map) {
        String chars = AHK_Master.qwerty.split(" ")[0];
        if (!map.isEmpty()) {
            chars = AHK_Master.qwerty.split(" ")[map.size() % AHK_Master.qwerty.split(" ").length];
        }
        return chars;
    }

    public G_Panel initView(Map<String, List<String>> map, Boolean letterShown) {
        return initView(map, letterShown, 0, null);
    }

    public void reinitView() {
        view.refresh();
        // view.repaint();
    }

    protected List<String> sort(List<String> lines, String chars) {
        if (isSortByTag()) {
            // SortMaster.getSublistSorter(enumClass)
        } else if (getSortValue() != null) {
            removeLineFormat(lines);
            boolean descending = isSortDescending();
            if (getSortValue() instanceof PARAMETER) {
                descending = !descending;
            }
            lines = SortMaster.sortByValue(lines, getSortValue(), AT_OBJ_TYPE.MUSIC_LIST,
             descending);
            restoreLineFormat(lines);
        } else {
            try {
                Collections.sort(lines, getComparator(chars));
            } catch (Exception e) {
                e.printStackTrace();
                main.system.auxiliary.log.LogMaster.log(1,"sorting failed: " +chars );
            }
        }

        return lines;
    }

    protected void restoreLineFormat(List<String> lines) {
        ArrayList<String> list = new ArrayList<>(lines);
        lines.clear();
        for (String sub : list) {
            sub = DataManager.getType(sub, AT_OBJ_TYPE.MUSIC_LIST).getName();// StringMaster.getWellFormattedString(generic);
            if (lineMap.get(sub) == null) {
                continue;
            }
            lines.add(lineMap.get(sub));
        }
    }

    protected void removeLineFormat(List<String> lines) {
        ArrayList<String> list = new ArrayList<>(lines);
        lines.clear();
        for (String sub : list) {

            String formatted = StringMaster.getWellFormattedString(StringMaster
             .getLastPathSegment(StringMaster.cropFormat(sub)));
            ObjType type = DataManager.getType(formatted, AT_OBJ_TYPE.MUSIC_LIST);
            if (type == null) {
                // formatted = StringMaster.getTypeNameFormat(generic);
                type = DataManager.findType(formatted, AT_OBJ_TYPE.MUSIC_LIST);
            }
            // else
            if (type != null) {
                formatted = type.getName();
            }
            lines.add(formatted);

            lineMap.put(formatted, sub);
        }

    }

    public G_Panel initView(final Map<String, List<String>> map, Boolean letterShown,
                            final int customWrap, final List<String> musicConsts) {
        lastMap = map;
        final G_Panel view = new ListButtonPanel(map, this, customWrap, musicConsts);
        view.refresh();
        return view;
    }

    public void setViewAndRefresh(G_Panel view) {
        this.view = view;
        refresh();
    }

    protected boolean checkWrap(int i, int customWrap, Character lastLetter, char letter) {
        if (customWrap != 0) {
            return (customWrap <= i);
        }

        if (lastLetter != null) {
            return letter != lastLetter;
        }
        return false;
    }

    protected JButton getButton(String keyPart, String funcPart, Font font, String name) {
        JButton button = new JButton(formatButtonText(name));
        button.setToolTipText("Press " + AHK_Master.getKeyModifiers(keyPart));
        button.setFont(font);
        MusicMouseListener mouseListener = new MusicMouseListener(name, keyPart, funcPart);

        if (filterProp != null) {
            boolean result = DataManager.getType(name, AT_OBJ_TYPE.MUSIC_LIST).
             // mouseListener.getList().
              checkProperty(filterProp, filterValue);
            if (result) {
                if (filterOut) {
                    applyResult(button);
                }
            }
            if (!result) {
                if (!filterOut) {
                    applyResult(button);
                }
            }

        }

        button.addActionListener(mouseListener);
        button.addMouseListener(mouseListener);
        return button;
    }

    protected void applyResult(JButton button) {
        if (highlight_disable_remove == null) {
            button.setVisible(false);
        } else if (highlight_disable_remove) {
            button.setForeground(Color.white);
            button.setBackground(Color.black);
        } else {
            button.setEnabled(false);
        }

    }

    protected String formatButtonText(String name) {
        if (name.length() > max_length) {
            name = name.replace(" ", "");
        }
        if (name.length() > max_length) {
            name = name.replace("And", "&");
        }
        if (name.length() > max_length) {
            name = name.substring(0, max_length) + "�";
        }
        return name;
    }

    protected boolean isLetterShown() {
        return false;
    }

    public Map<String, List<String>> getListMap() {
        return listMap;
    }

    public MusicViewsPanel getViewsPanel() {
        return viewsPanel;
    }

    public MusicDisplayPanel getDisplayPanel() {
        return displayPanel;
    }

    public G_Panel getView() {
        return view;
    }

    public void setView(G_Panel view) {
        this.view = view;
    }

    public String getKey() {
        return key;
    }

    public VALUE getSortValue() {
        return sortValue;
    }

    public void setSortValue(VALUE sortParameter) {
        this.sortValue = sortParameter;
    }

    public String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.tagFilter = tagFilter;
    }

    public String getGroupFilter() {
        return groupFilter;
    }

    public void setGroupFilter(String groupFilter) {
        this.groupFilter = groupFilter;
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public void resetFilter() {
        filterValue = null;
        filterProp = null;
        filterOut = null;
        view = null;
        refresh();
    }

    public PROPERTY getFilterProp() {
        return filterProp;
    }

    public void setFilterProp(PROPERTY filterProp) {
        this.filterProp = filterProp;
    }

    public Boolean getFilterOut() {
        return filterOut;
    }

    public void setFilterOut(Boolean filterOut) {
        this.filterOut = filterOut;
    }

    public Boolean getHighlight_disable_remove() {
        return highlight_disable_remove;
    }

    public void setHighlight_disable_remove(Boolean highlight_disable_remove) {
        this.highlight_disable_remove = highlight_disable_remove;
    }

    public boolean isSortByTag() {
        return sortByTag;
    }

    public void setSortByTag(boolean sortByTag) {
        this.sortByTag = sortByTag;
    }

    protected boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }

    public MC_ControlPanel getControlPanel() {
        return controlPanel;
    }
}
