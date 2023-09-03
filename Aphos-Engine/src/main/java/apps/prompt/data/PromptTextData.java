package apps.prompt.data;

import elements.content.enums.EnumFinder;
import main.system.datatypes.WeightMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static apps.prompt.enums.PromptEnums.*;

/**
 * Created by Alexander on 9/2/2023
 */
public class PromptTextData {

    public static PromptTextData current;
    private Object nullKey="!";

    static Map<TokenType, Map<String, Set<String>>> typeMap_= new HashMap<>();
    Map<TokenType, Map<String, Set<String>>> typeMap;

    public static void reset() {
        current = new PromptTextData();
        // cloneData(current);
    }

    public static void init(TokenType type, Set<Pair<String, String>> data) {
        Map<String, Set<String>> map= new HashMap();
        for (Pair<String, String> pair : data) {
            String text = pair.getLeft();
            String metaData = pair.getRight();
            add(map, text, metaData);
        }
        typeMap_.put(type, map);
    }

    private static void add(Map<String, Set<String>> map, String text, String metaData) {
        //add to all maps that are referenced
        for (String s : metaData.split(",")) {
            s = s.trim();
            map.computeIfAbsent(s, k -> new LinkedHashSet<>()).add(text);
        }
    }

    /*
    this one is more critical - it will define how we form the data
    >> we want some chance of getting cross-type tokens !
    >> Maybe it's better to define relationship between data-set categories and prompt enums??
    Yeah, in that case tokens will just have kinda tags ... which I will map to actual Enums!

    weightmap per subtype then? And for each tag, we will have a list of token-texts!
     */
    public String get(TokenType type, PromptType promptType, WeightMap<String> subtype) {

        if (type == TokenType.generic){

        }
        if (type == TokenType.author){

        }
        if (type == TokenType.pic_type){

        }

        Map<String, Set<String>> map = typeMap.get(type);
        String random = subtype.getRandomByWeight();
        //will it always have this key?

        Set<String> strings = map.get(random);
        return system.CollectionsX.getRandomWithOrderPriority(strings);

        //how to implement weights here?
        // map = (Map) map.get(promptType);
        // List<String> list=null;
        // if (subtype != null) {
        //     list = (List<String>) map.get(subtype);
        // } else {
        //     list = (List<String>) map.get(nullKey);
        // }
        // //seed?
        // int n = new Random().nextInt(list.size());
        // return list.remove(n);
        // weightMap = createWeightMap(list);
        // static weights.get(s) => stream map

        //TokenData list that we can filter?

        //filter? nested maps won't work - because some tokens can have MULTIPLE prT's or subs or more!
        //especially generics! Maybe we should have diff mechanisms for that?
        //generics could be created not via tokens but in a more blunt way!!!!

        //how would I fill the dataset with powerful words?

        // tokenMap.get(type).get(promptType).
        // WeightMap - to string? to enums?
    }
    /*
    weight maps? how to structure it in such a way that it will be a joy to expand and use?

    i'd like to have my own database that i can also automatically expand - when a prompt is GOOD, I must be able to SAVE IT!

    What about static data - like for tokens etc? Write it out by hand? And what about expanding it?
    Perhaps I can ask GPT to format yaml somehow?
    Don't want data to be over-nested
    Separate for tokens?
     */
}
