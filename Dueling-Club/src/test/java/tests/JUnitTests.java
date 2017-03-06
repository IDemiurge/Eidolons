package tests;

import java.init.JUnitDcInitializer;
import java.tests.entity.CreateUnitTest;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JustMe on 3/6/2017.
 */
public class JUnitTests implements Runnable {
    List<JUnitTest> tests  ;

    /*

     */

    public JUnitTests(JUnitDcInitializer initializer){
        tests = new LinkedList<>();
        tests.add(new CreateUnitTest(initializer));
    }

    @Override
    public void run() {
        tests.forEach(test-> test.testUnitTest());
    }

}
