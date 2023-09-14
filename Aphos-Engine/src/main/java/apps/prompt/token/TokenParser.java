package apps.prompt.token;

import apps.prompt.data.PromptTextData;
import apps.prompt.enums.PromptEnums;
import main.system.datatypes.WeightMap;

/**
 * Created by Alexander on 9/2/2023
 *
 * we need to avoid parsing to SAME TEXT!
 *
 * Let's break down into tokens:
 *
 */
public class TokenParser {
    public static String parse(Token token) {
        //what if we just remove
        if (token.getType()== PromptEnums.TokenType.input)
            return token.getInput();
        return PromptTextData.current.get(token.getType(), token.getPromptType(), null );
        // return PromptTextData.current.get(token.getType(), token.getPromptType(), createWeightMap((Weighted) token.getSubtype()));
    }

    private static WeightMap<String> createWeightMap(Weighted subtype) {
        return new WeightMap<>(subtype.getWeights(), String.class);
    }
}
