package aurora.timer.client.plugin;

/**
 * Created by hao on 17-2-25.
 */
public class TimerPlugin {
    public TimerPlugin() {
    }

    public String getName() {
        return "name";
    }

    public void start() {
        System.out.println("开启");
    }
}
