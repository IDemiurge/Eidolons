package apps.prompt.data;

import apps.prompt.PromptModel;
import apps.prompt.enums.PromptEnums;
import main.system.auxiliary.data.FileManager;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.yaml.snakeyaml.Yaml;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Alexander on 9/2/2023
 * How can we structure this data?
 * >> many single yml files? 
 *
 */
public class PromptDataManager {

    private static final String PATH = "C:\\code\\Eidolons\\Aphos-Engine\\src\\main\\java\\apps\\prompt\\data\\tokens.yml" ;

    public static void read() {
        String contents = FileManager.readFile(PATH);
        Object data = new Yaml().load(contents);

        Set<Pair<String, String>> set= new HashSet<>();
        Map map = (Map) data;
        List<Map> list = (List<Map>) map.get("content");
        for (Map sub : list) {
            sub = (Map) sub.get("token");
            set.add(new ImmutablePair<>(sub.get("text").toString(), sub.get("meta_data").toString()));
        }
        PromptTextData.init(PromptEnums.TokenType.content, set);
    }

    public void saveLast(){

    }
    public void savePrompt(PromptModel promptModel){

        Object data = null;
        new Yaml().dump(data);

    }
}
