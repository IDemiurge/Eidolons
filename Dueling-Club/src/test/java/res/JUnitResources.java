package res;

import main.content.DC_TYPE;
import main.data.DataManager;
import main.data.filesys.PathFinder;
import main.data.xml.XML_Reader;
import main.system.auxiliary.StrPathBuilder;
import main.system.auxiliary.data.FileManager;

import static org.junit.Assert.assertTrue;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitResources {
    public static final String EMPTY_DUNGEON = StrPathBuilder.build("TEST", "empty.xml");
    public static final String DEFAULT_UNIT = "ShamanX";
    public static final DC_TYPE[] RESOURCE_TYPE = {
     DC_TYPE.UNITS,
    };

    public JUnitResources() {
        injectJUnitResources();
        assertTrue(DataManager.getType(DEFAULT_UNIT, DC_TYPE.UNITS) != null);
    }

    private void injectJUnitResources() {
        for (DC_TYPE TYPE : RESOURCE_TYPE) {
            XML_Reader.readCustomTypeFile(
             FileManager.getFile(getJUnitXml(TYPE)), TYPE, null );

        }

    }

    private String getJUnitXml(DC_TYPE type) {
        return StrPathBuilder.build(PathFinder.getTYPES_PATH(), "test", type + ".xml");
    }

    public String getDefaultUnit() {
        return DEFAULT_UNIT;
    }
}
