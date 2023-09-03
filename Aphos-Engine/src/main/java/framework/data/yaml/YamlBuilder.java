package framework.data.yaml;

import content.GenericLinkedStringMap;
import content.LinkedStringMap;
import elements.exec.ExecBuilder;
import elements.exec.Executable;
import elements.stats.ActionProp;
import framework.data.DataManager;
import main.data.XLinkedMap;
import main.system.PathUtils;
import main.system.auxiliary.data.FileManager;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;

import java.io.File;
import java.util.*;

/**
 * Created by Alexander on 8/25/2023 Special rules: [param] triplet / / / #tag => .setProcessComments(true)?
 * <p>
 * Entity - easy? We sure can have a mapper for that ; dataMap is good enough BTW - consider that there is no backward
 * compatibility for creating YAML from java object... Do we need it? // yaml.parse(null).forEach(event -> event.);
 * IDEA: process comments first? //for selective loading, gonna have to use parse() as GPT suggested
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class YamlBuilder {
    private static String ROOT_PATH;

    public void buildYamlFiles() {
        buildYamlFile("units", "Unit", "Faction", false);
        buildYamlFile("actions", "Action", "Faction", true);
        buildYamlFile("passives", "Passive", "Faction", true);
        //PERKS can really just be an ENUM !
    }
    class CustomMapConstructor extends SafeConstructor {
        @Override
        protected Map<Object, Object> constructMapping(MappingNode node) {
            // Create an instance of your custom Map subclass here
            // For example, if your custom class is CustomHashMap, you can do:
            // return new CustomHashMap<>();
            return new GenericLinkedStringMap<>(); // Using HashMap for demonstration
        }
    }
    public void buildYamlFile(String filename, String typeKey, String listNameProp, boolean parseVars) {
        // Yaml yaml = new Yaml(new CustomMapConstructor());
        Yaml yaml = new Yaml();


        //set map constructor to STR!
        // Map<String, List<Map>> load = new LinkedStringMap();
        File temp = new File(getClass().getClassLoader().getResource("").getFile());
        ROOT_PATH = PathUtils.fixSlashes(new File(temp.getParentFile().toURI()).getPath());
        String path = PathUtils.cropLastPathSegment(ROOT_PATH) + "src/main/java/framework/data/yaml/" +
                filename + ".yml";

        String content = FileManager.readFile(path);

        Map loaded = yaml.load(content);
        for (Object key : loaded.keySet()) {
            processTypesMap(typeKey, key.toString(), (List<Map>) loaded.get(key), listNameProp, parseVars);
        }
    }

    private void processTypesMap(String typeKey, String docName, List<Map> typeData, String listNameProp, boolean parseVars) {
        for (Map types : typeData) {
            for (Object typeNode : types.keySet()) {
                Map typeMap = (Map) types.get(typeNode);

                typeMap.put(listNameProp, docName);
                typeMap.put("Type", typeKey);
                String name = typeNode.toString();
                typeMap.put("Name", name);
                Set set = new HashSet(typeMap.keySet());
                if (parseVars) {
                    for (Object o : set) {
                        if (o.toString().equals(ActionProp.Exec_data.getName())) {
                            String execKey = parseExec(name, typeMap.get(o));
                            typeMap.put(o, execKey);
                        }
                    }
                    if (!docName.toLowerCase().contains("exec"))
                        DataManager.addTypeData(typeKey, name, typeMap);
                } else {
                    DataManager.addTypeData(typeKey, name, typeMap);
                }
            }

        }
    }

    private String parseExec(String typeName, Object o) {
        Executable exec = elements.exec.ExecBuilder.build(o);

        StringBuilder execKey = new StringBuilder(typeName);

        if (exec.isMultiExec()){
            // Map<String, Object> effectArgMap = new LinkedStringMap<>();
            // ((List)o).
            //         forEach(key -> {
            //     String strKey = key.toString().toLowerCase();
            //     if (strKey.startsWith("effect_")) {
            //         strKey = strKey.replace("effect_", "");
                    //args node!
                    // effectArgMap.put(strKey, o.get(key));
                    // execKey.append("_").append(strKey).append("=").append(o.get(key));
            //     }
            // });
            // applyEffectArgs(exec, effectArgMap);
        } else {
            Map args =getMap(o, "args");
            if (args!=null){
                for (Object key : args.keySet()) {
                    execKey.append("_").append(key).append("=").append(args.get(key));
                }
            }
        }
        ExecBuilder.addExec(execKey.toString(), exec);
        return execKey.toString();
    }


    public Executable applyEffectArgs(Executable base, Map<String, Object> args) {
        // Executable clone = clone(preset);
        for (String s : args.keySet()) {
            setVariable(s, args.get(s), base);
        }
        return base;
    }


    private void setVariable(String s, Object o, Executable base) {
        //TODO
        // Pair<Targeting, Effect> pair = base.getTargetedEffects().get(i);
        // pair.getRight().getData().set(name, o);
        // pair.getLeft().getData().set(name, o);
    }


    public static Map getMap(Object node, String type) {
        return (Map) ((Map) node).get(type);
    }
    // private int getInt(Object node, String name) {
    //     return (int) ((Map) node).get(name);
    // }
    //
    // private String getS(Object node, String name) {
    //     return ((Map) node).get(name).toString();
    // }
    //
    private String getMapName(Object node) {
        return ((Map) node).keySet().toArray()[0].toString();
    }

    /*
    doc is map of name + type=> list of types
    type is a map with inlaid lists: [Pas, Act]
    >> active type is created from nested data

    exec
    inside action/passive,

    using anchors
    what's the advantage over having a managed map of templates?

    > support multi exec's as list

     */
}
