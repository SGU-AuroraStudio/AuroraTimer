package junit;

import aurora.timer.client.view.util.SmartHttpUtil;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Yao
 * @Date 2021/4/9 21:49
 * @Description
 */
public class SmartHttpUtilTest {
    @Test
    public void testGet() throws Exception {
        Map<String,String> params = new HashMap<>();
        Map<String,String> header = new HashMap<>();
        params.put("id", "18125061059");
        String res = SmartHttpUtil.sendGet("http://47.99.134.104:8083/timer/findById", params, header);
        JSONObject object = (JSONObject)JSONValue.parse(res);
        System.out.println(object.get("id"));
        System.out.println(object.get("name"));
        System.out.println(object);
    }
}
