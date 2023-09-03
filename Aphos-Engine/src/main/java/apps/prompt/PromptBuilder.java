package apps.prompt;

import apps.prompt.token.Token;
import apps.prompt.token.TokenMixer;
import apps.prompt.token.TokenParser;

import java.util.List;

/**
 * Created by Alexander on 9/2/2023 duplicates? word limit
 */
public class PromptBuilder {
    private static Prompt lastPrompt;

    public static String build(PromptModel model) {
        StringBuilder sb = new StringBuilder();
        TokenMixer mixer = new TokenMixer();
        List<Token> tokens= mixer.createTokens(model);

        String fixedInput = model.getFixedInput();
        //how to position?
        // >> insert into tokens?!
        for (Token token : tokens) {
            if (token.checkInputToken(fixedInput)) {
                break;
            }
        }
        //parse token based on what has been parsed already?
        //avoid repeating prompts somehow ?
        tokens.forEach(token -> sb.append(TokenParser.parse(token)));

        String text=sb.toString();
        lastPrompt = new Prompt(model, tokens, text);
        // prompts.push(last);
        return text;
    }
    public String remix(Prompt prompt) {
        //TODO
        return null;
    }
}
