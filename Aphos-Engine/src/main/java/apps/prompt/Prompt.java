package apps.prompt;

import apps.prompt.token.Token;

import java.util.List;

/**
 * Created by Alexander on 9/2/2023
 */
public class Prompt {
    private final PromptModel model;
    private final List<Token> tokens;
    private final String text;

    public Prompt(PromptModel model, List<Token> tokens, String text) {
        this.model = model;
        this.tokens = tokens;
        this.text = text;
    }
}
