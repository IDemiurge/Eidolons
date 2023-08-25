package elements.exec.preset.yaml;

import elements.exec.Executable;
import org.snakeyaml.engine.v2.constructor.StandardConstructor;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

/**
 * Created by Alexander on 8/25/2023
 * Special rules:
 * [param]
 * triplet / / /
 * #tag
 *
 * Entity - easy? We sure can have a mapper for that ; dataMap is good enough
 *
 */
public class YamlBuilder {

    //list of maps
    public Executable buildExecutable(){
        //should we call it abil after all?
//templates.yml?

        Representer representer = new Representer();
        // representer.addClassTag(YamlEntity.class, new Tag("!yah"));
        Yaml yaml = new Yaml(representer);
        // TypeDescription descr= new TypeDescription();
        // yaml.addTypeDescription(descr);
        // StandardConstructor
        // yaml.loadAs()
        return null;
    }
}
