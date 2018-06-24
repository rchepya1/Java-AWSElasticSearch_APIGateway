import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


/**
 * Created by rohith on 6/22/18.
 */

/**
 * Tests to directly call Elastic Search and API Gateway(and internally call Elastic search) to return the response and print it.
 */
public class MainMethodToTestES {

    @Test
    public void elasticSearchTest() {
        StringBuffer response = new StringBuffer();
        String url = "https://search-awselasticsearch-rohith-lqottp4vgjrlzbexoggeth5rxa.us-east-1.es.amazonaws.com/plans/_search?q=";
        try {
            url += "PLAN_NAME:\"" + URLEncoder.encode("MICHAEL G. BLAKE & ASSOC. 401K PROFIT SHARING PLAN", "UTF-8") + "\"";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            //Building a simple HTTP connection and sending the request (<a href="http://www.baeldung.com/java-http-request"> Java-HTTP-REQUEST</a>
            URL obj = new URL(url);
            System.out.println(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
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

        System.out.println("ElasticSearch Response: " + response);
    }

    @Test
    public void apiGatewayTest() {
        {
            StringBuffer response = new StringBuffer();
            String url = "https://8f004003g2.execute-api.us-east-1.amazonaws.com/prod/AWSElasticSearch_personalCapital";
            try {
                url += "?" + "plan_name=" + URLEncoder.encode("MICHAEL G. BLAKE & ASSOC. 401K PROFIT SHARING PLAN", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                //Building a simple HTTP connection and sending the request (<a href="http://www.baeldung.com/java-http-request"> Java-HTTP-REQUEST</a>
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
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

            System.out.println("Api Gateway Response: " + response.toString());
        }
    }

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(MainMethodToTestES.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        System.out.println("Is Successful: " + result.wasSuccessful());
    }
}
