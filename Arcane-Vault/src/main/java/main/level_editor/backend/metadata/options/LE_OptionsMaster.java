package main.level_editor.backend.metadata.options;

import eidolons.system.options.OptionsMaster;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Converter;
import main.data.xml.XmlNodeMaster;
import main.system.auxiliary.data.FileManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class LE_OptionsMaster extends OptionsMaster {
    private static LE_OptionsMaster instance;
    LE_Options options;

    public static final void init() {
        getInstance().initialize();
    }

    public static LE_OptionsMaster getInstance() {
        if (instance == null) {
            instance = new LE_OptionsMaster();
        }
        return instance;
    }

    @Override
    public void initialize() {
        options = new LE_Options();
        optionsMap.put(OPTIONS_GROUP.EDITOR, options);
        String data = FileManager.readFile(getOptionsPath());
        if (data.isEmpty()) {
            setDefaults(options, LE_Options.EDITOR_OPTIONS.class);
            save();
        }
        data = FileManager.readFile(getOptionsPath());
        Document doc = XML_Converter.getDoc((data));
        if (doc != null)
            for (Node sub : XmlNodeMaster.getNodeListFromFirstChild(doc, true)) {
                for (Node s : XmlNodeMaster.getNodeList(sub, true)) {
                    options.setValue(s.getNodeName(), s.getTextContent());
                }
            }
    }

    @Override
    protected String getSaveOptionsPath() {
        return getOptionsPath();
    }

    @Override
    public String getOptionsPath() {
        return PathFinder.getXML_PATH() + "options/le.xml";
    }

    public static LE_Options getOptions_() {
        return getInstance().getOptions();
    }

    public LE_Options getOptions() {
        return options;
    }
}
