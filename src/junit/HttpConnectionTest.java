package junit;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hao on 17-4-19.
 */
public class HttpConnectionTest {
    @Test
    public void httpTest() {
        String url = "http://www.baidu.com";
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("accept", "application/json"); //向服务器表示我要的是json
            connection.connect();
            inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buffer = new StringBuffer("");
            String temp;
            while ((temp = reader.readLine()) != null) {
                buffer.append(temp);
            }
            reader.close();

            System.out.println(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect();
                inputStream.close();
                connection.disconnect();
            } catch (Exception er) {

            }
        }
    }
}
