package apps.prompt;

import apps.prompt.data.PromptDataManager;
import apps.prompt.enums.PromptEnums;
import campaign.data.enums.AssetEnums;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Created by Alexander on 9/2/2023
 * <p>
 * on hotkey, ... F[x] for prompt type of x? constructor via Joption sequence
 * <p>
 * i: AHK => hotkey + ctrl enter
 * <p>
 * GPT will have SEPARATE UTIL?
 *
 * Wanna use it ESPECIALLY on the MAC! Hotkeys for mac? Or some other way?
 * Button panel - maybe
 * Data persistence - just append to some yaml file?
 * What we'll save: successful prompt remixes w/ fixed input text (which can then be further mixed or used AS IS )
 *
 */
public class SdPromptGen {
    public static void main(String[] args) {

        PromptDataManager.read();

        PromptEnums.PromptStyle style=null ;
        PromptEnums.PromptType type= PromptEnums.PromptType.Event_Pic;
        String input="radiant";
        String arg=""; //more info for subtype?
        Object subType= AssetEnums.AphosEventType.fascination ;
        PromptModel promptModel = new PromptModel(style, type, subType,input);

        String build = new PromptBuilder().build(promptModel);

        StringSelection selection = new StringSelection(build);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}






















