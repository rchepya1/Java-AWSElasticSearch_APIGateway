package com.elasticSearch.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
/**
 * Created by rohith on 6/21/18.
 */


/**
 * Lambda function to hit Elastic Search.
 * Implements RequestStreamHandler from AWS lambda services.
 * (<a href="https://docs.aws.amazon.com/lambda/latest/dg/java-handler-io-type-pojo.html"> REQUEST-PARAMS</a>)
 * Input - InputStream to consume parameters from API Gateway
 * Output - OutputStream to parse the output, build and send it as a response to the API gateway
 * URL to hit is the Elastic Search URl with the param names and values being added as part of handleRequest method
 * Param names are from the test data shared: f_5500_2017_latest_layout.txt
 */
public class ElasticSearchService implements RequestStreamHandler {

    /**
     * Handle the request and generate the response returned by ElasticSearch
     * @param inputStream - Consists of Parameters passed to the SearchServcie
     * @param outputStream - Response created to be returned to the API Gateway
     * @param context -  General context of AWS
     * @throws IOException
     */
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

        LambdaLogger logger = context.getLogger();
        logger.log("In Java Elastic Search Lambda function");
        String plan_name = null;
        String sponsor_name = null;
        String sponsor_state = null;
        StringBuffer response = new StringBuffer();
        String responseCode = "200";
        JSONObject responseJson = new JSONObject();
        JSONObject headerJson = new JSONObject();
        JSONParser parser = new JSONParser();

        //Reading the input
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));

        //Parsing the input into required queries
        try {
            JSONObject event = (JSONObject) parser.parse(inputReader);
            inputReader.close();
            if (event.get("queryStringParameters") != null) {
                JSONObject inputParams = (JSONObject) event.get("queryStringParameters");
                if (inputParams.get("plan_name") != null) {
                    plan_name = (String) inputParams.get("plan_name");
                }
                if (inputParams.get("sponsor_name") != null) {
                    sponsor_name = (String) inputParams.get("sponsor_name");
                }
                if (inputParams.get("sponsor_state") != null) {
                    sponsor_state = (String) inputParams.get("sponsor_state");
                }
            }

            String inputParamsToString = "Input Params: " + "plan_name: " + plan_name + " sponsor_name: " + sponsor_name + " sponsor_state: " + sponsor_state + "\n";
            //Actual URL of the Elastic Search domain
            String url = "https://search-awselasticsearch-rohith-lqottp4vgjrlzbexoggeth5rxa.us-east-1.es.amazonaws.com/plans/_search?q=";
            String planNameQuery = "PLAN_NAME:\"";
            String sponsorNameQuery = "SPONSOR_DFE_NAME:\"";
            String sponsorStateQuery = "SPONS_DFE_MAIL_US_STATE:\"";

            if (plan_name != null) {
                try {
                    url += planNameQuery + URLEncoder.encode(plan_name, "UTF-8") + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (sponsor_name != null) {
                try {
                    url += sponsorNameQuery + URLEncoder.encode(sponsor_name, "UTF-8") + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
            if (sponsor_state != null) {
                try {
                    url += sponsorStateQuery + URLEncoder.encode(sponsor_state, "UTF-8") + "\"";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            //Final URL to hit the ElasticSearch
            logger.log("ElasticSearch URL: "+ url);
            response.append(inputParamsToString);
            try {
                //Building a simple HTTP connection and sending the request (<a href="http://www.baeldung.com/java-http-request"> Java-HTTP-REQUEST</a>
                URL obj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.setRequestMethod("GET");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                connection.disconnect();
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                headerJson.put("x-custom-header", "my custom header value");
                responseJson.put("isBase64Encoded", false);
                responseJson.put("statusCode", responseCode);
                responseJson.put("headers", headerJson);
                responseJson.put("body", response.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ParseException e) {
            responseJson.put("statusCode", "400");
            responseJson.put("exception", e);
        }
        logger.log(responseJson.toJSONString());
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toJSONString());
        writer.close();
    }
}
