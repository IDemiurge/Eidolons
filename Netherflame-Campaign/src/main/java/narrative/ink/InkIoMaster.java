package narrative.ink;

import com.bladecoder.ink.runtime.Choice;
import com.bladecoder.ink.runtime.Story;
import main.data.filesys.PathFinder;
import main.system.auxiliary.Loop;
import main.system.auxiliary.RandomWizard;
import main.system.auxiliary.data.FileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by JustMe on 11/19/2018.
 */
public class InkIoMaster {

    public InkIoMaster() {
        try {
            test();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
    }

    public static void main(String[] args) {
        new InkIoMaster();
    }

    public void test() throws Exception {
        // 1) Load story
        String sourceJsonString = "";
        try {
            sourceJsonString = readTest();
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        Story story = new Story(sourceJsonString);


        // 2) Game content, line by line
        while (story.canContinue()) {
            String line = story.Continue();
            System.out.print("Full Contents: \n"+line+"\n\n\n");
        }
        Loop loop = new Loop(5);
        while (loop.continues()) {
            System.out.println(Loop.getCounter() );
            System.out.println(story.getCurrentText());
        // 3) Display story.currentChoices list, allow player to choose one
        if (story.getCurrentChoices().size() > 0) {

            for (Choice c : story.getCurrentChoices()) {
                System.out.print("Choices: \n");
                System.out.println(c.getText());
            }

            story.chooseChoiceIndex(
             RandomWizard.getRandomInt(story.getCurrentChoices().size()));

            System.out.print("State: \n");
            System.out.println(story.getCurrentText());
        }
        }
    }

    public static String readTest() throws IOException {
        return readJson("ink/test.ink.json");
    }
    public static String readJson(String path) throws IOException {

        try (BufferedReader br = new BufferedReader(
                new FileReader(FileManager.getFile(PathFinder.getRootPath() + PathFinder.getTextPath() +
                        path)))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

            return sb.toString().replace('\uFEFF', ' ');
        }

    }
}
