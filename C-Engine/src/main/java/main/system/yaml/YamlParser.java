package main.system.yaml;

import org.snakeyaml.engine.v2.api.YamlUnicodeReader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Alexander on 2/1/2022
 * <p>
 * Useful functionality so far: 1) loadAll() - for any large data docs (--- multiple documents ) 2) Anchor & Alias -
 * it's *kind of* the same as using my own nested stuff, but easier to impl 3) :<< merge tag - usage to be confirmed -
 * very good for Hierarchy of Types! Children just merge, then ... ah, well, sometimes we need to ADD
 * <p>
 * 4) !className - tag for explicit class (single-constructor only?)
 * <p>
 * <p>
 * Requirements 1) Pre-defined tags 2)
 */
public class YamlParser extends AbstractConstruct {

    public static void main(String[] s) throws FileNotFoundException {
        Representer representer = new Representer();
        representer.addClassTag(YamlEntity.class, new Tag("!yah"));
        Yaml yaml = new Yaml(representer);
        Object load = yaml.load(new FileInputStream(new File("C:\\code\\Eidolons\\DungeonCraft\\src\\main\\resources\\data\\data.yaml")));
        load.getClass();

    }

    @Override
    public Object construct(Node node) {
        Class<?> type = node.getType();
        return null;
    }

    /*
     return new Yaml(new EnvironmentConstructor(environment),
                    new Representer(),
                    new DumperOptions(),
                    new Resolver() {
                        @Override
                        public Tag resolve(NodeId kind, String value, boolean implicit) {
                            if (value != null) {
                                if (value.startsWith("${env.")) {
                                    return new Tag("!env");
                                }
                                if (value.equalsIgnoreCase("on") ||
                                        value.equalsIgnoreCase("off") ||
                                        value.equalsIgnoreCase("yes") ||
                                        value.equalsIgnoreCase("no")) {
                                    return Tag.STR;
                                }
                            }
                            return super.resolve(kind, value, implicit);
                        }
                    });
     */
}
