package co.yaggle.simpleci.server;

import java.io.File;

public class TestUtil {
    public static File getDirectory(String resourcePath) {
        return new File(TestUtil.class.getResource(resourcePath + "/simple-ci.xml").getFile()).getParentFile();
    }
}
