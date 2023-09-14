package apps.prompt.token;

import apps.prompt.PromptModel;
import apps.prompt.enums.PromptEnums;
import main.system.util.DialogMaster;
import main.system.util.EnumChooser;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static apps.prompt.enums.PromptEnums.TokenType.*;

/**
 * Created by Alexander on 9/2/2023
 */
public class TokenMixer {
    public enum PromptTemplate{
        autumn_env(content_env_autumn,
                content_character_autumn,
                content_env_autumn,
                content_env_autumn),
        
        autumn_char(
                content_character_autumn,
                content_character_autumn,
                content_env_autumn,
                content_env_autumn),
        autumn_scene(content_scene_temple,
                content_env_autumn,
                content_character_autumn,
                content_character_autumn,
                content_env_autumn),
        ;

        PromptTemplate(PromptEnums.TokenType... tokens) {
            this.tokens = tokens;
        }

        PromptEnums.TokenType[] tokens;
    }

    public static final PromptTemplate DEFAULT = PromptTemplate.autumn_scene;


    public List<Token> createTokens(PromptModel promptModel) {
        // List<Token> list = new LinkedList<>();
        // List<PromptEnums.TokenType> tokenPlan = Arrays.asList(base);
        PromptTemplate template =  new EnumChooser().choose(PromptTemplate.class);
        if (template==null ){
             template = DEFAULT;
        }
        return  Arrays.stream(template.tokens).map(type -> createToken(type, promptModel)).collect(Collectors.toList());
    }

    private Token createToken(PromptEnums.TokenType type, PromptModel model) {
        return new Token(type, model.getType(), model.getSubtype());
    }

    //randomize order to some extent?
    public void mix(List<Token> tokens) {

    }
}
