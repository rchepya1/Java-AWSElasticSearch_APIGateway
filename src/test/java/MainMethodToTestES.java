import com.amazonaws.util.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by rohith on 6/22/18.
 */

public class MainMethodToTestES {

    public static void main(String[] args) throws UnsupportedEncodingException {
        StringBuffer response = new StringBuffer();
        JSONObject outputJson = null;
        String url = "https://search-awselasticsearch-rohith-lqottp4vgjrlzbexoggeth5rxa.us-east-1.es.amazonaws.com/plans/_search?q=";
        url += "PLAN_NAME:\"" + URLEncoder.encode("MICHAEL G. BLAKE & ASSOC. 401K PROFIT SHARING PLAN", "UTF-8") + "\"";
        try {
            //Building a simple HTTP connection and sending the request (<a href="http://www.baeldung.com/java-http-request"> Java-HTTP-REQUEST</a>
            URL obj = new URL(url);
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //con.setConnectTimeout(100000);
            //con.setReadTimeout(100000);
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            con.disconnect();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            outputJson = new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(outputJson);
    }
}
