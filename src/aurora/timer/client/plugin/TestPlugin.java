package aurora.timer.client.plugin;

/**
 * Created by hao on 17-2-26.
 */
public class TestPlugin extends TimerPlugin {
    public TestPlugin() {

    }

    @Override
    public String getName() {
        return "msj";
    }

    @Override
    public void start() {
        System.out.println("Link Start!!!");
    }
}
