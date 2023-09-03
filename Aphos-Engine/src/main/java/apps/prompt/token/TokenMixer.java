package apps.prompt.token;

import apps.prompt.PromptModel;
import apps.prompt.enums.PromptEnums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Alexander on 9/2/2023
 */
public class TokenMixer {
    public static final PromptEnums.TokenType[] base = {
            PromptEnums.TokenType.style,
            PromptEnums.TokenType.input,
            PromptEnums.TokenType.content,
            PromptEnums.TokenType.content,
            PromptEnums.TokenType.content,
            PromptEnums.TokenType.pic_type,
            PromptEnums.TokenType.author,
            PromptEnums.TokenType.generic,
            PromptEnums.TokenType.generic,
            PromptEnums.TokenType.generic,
    };

    public List<Token> createTokens(PromptModel promptModel) {
        // List<Token> list = new LinkedList<>();
        // List<PromptEnums.TokenType> tokenPlan = Arrays.asList(base);

        return  Arrays.stream(base).map(type -> createToken(type, promptModel)).collect(Collectors.toList());
    }

    private Token createToken(PromptEnums.TokenType type, PromptModel model) {
        return new Token(type, model.getType(), model.getSubtype());
    }

    //randomize order to some extent?
    public void mix(List<Token> tokens) {

    }
}
