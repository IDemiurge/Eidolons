package main.utilities.gdx;

import eidolons.utils.GdxUtil;
import libgdx.shaders.ShaderMaster;
import libgdx.shaders.ShaderMaster.SHADER;

import main.system.threading.WaitMaster;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by JustMe on 10/27/2018.
 */
public class ShaderTester extends GdxUtil {

    static List<SHADER> failed = new ArrayList<>();
    SHADER shader;

    public ShaderTester(SHADER shader) {
        this.shader = shader;
    }

    @Test
    public void test() {
        for (SHADER shader : SHADER.values()) {
            new ShaderTester(shader).create();
        }
        WaitMaster.WAIT(500);
        assertTrue(""+failed, failed.isEmpty() );
    }

    @Override
    protected void execute() {
        try {
            if (ShaderMaster.getShader(shader).isCompiled())
                return;
        } catch (Exception e) {
            main.system.ExceptionMaster.printStackTrace(e);
        }
        failed.add(shader);
    }
}
