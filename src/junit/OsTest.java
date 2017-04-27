package junit;

import org.junit.Test;

/**
 * Created by hao on 17-4-27.
 */
public class OsTest {
    @Test
    public void osTest() {
        System.out.println(System.getProperty("os.name") + "\n" + System.getProperty("os.version"));
    }
}
