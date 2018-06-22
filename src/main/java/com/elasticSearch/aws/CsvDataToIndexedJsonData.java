package com.elasticSearch.aws;

import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * Created by rohith on 6/21/18.
 */

/**
 * TO read data from CSV and parse it:(<a href="https://github.com/FasterXML/jackson-databind"> Jackson-databind</a>)
 */
public class CsvDataToIndexedJsonData {

    public static void main(String[] args) throws Exception {
        File inputFile=new File("/Users/rohith/Documents/Exercise/F_5500_2017_Latest/f_5500_2017_latest.csv");
        List<Map<?, ?>> plans = readObjectsFromCsv(inputFile);
        writeToJsonOutput(plans);
    }


    /**
     * Read the CSV into a List of Maps of plans.
     * Map - Each plan with key and value as the fieldName and fieldValue respectively
     * List of these maps indicates the entire CSV file converted to objects
     * @param inputFile
     * @return
     * @throws IOException
     */
    public static List<Map<?, ?>> readObjectsFromCsv(File inputFile) throws IOException {
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator = csvMapper.readerFor(Map.class).with(csvSchema).readValues(inputFile);
        return mappingIterator.readAll();
    }


    /**
     * Writing the data as a string of JSON.
     * Adding Index line for each plan to match the Elastic search's indexing
     * (<a href="https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-gsg-upload-data.html"> AWS Documentation</a>)
     * @param plans
     * @throws IOException
     */
    public static void writeToJsonOutput(List<Map<?, ?>> plans) throws IOException {
        int jsonIndex = 1;
        try {
            while (plans.size() >= jsonIndex) {
                FileWriter writer = new FileWriter(multipleFileNames(jsonIndex / 1000 + 1), true); //Boolean true indicates the File can be appended by the next rows
                int i = jsonIndex;
                while (i < jsonIndex + 1000) {
                    writer.write("{ \"index\" : { \"_index\": \"plans\", \"_type\" : \"plan\", \"_id\" : \"" + i + "\" } }\n");
                    writer.write(new JSONObject(plans.get(i - 1)) + "\n");
                    i++;
                }
                jsonIndex = i;
                writer.close();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static String multipleFileNames (int i) {
        return "/Users/rohith/Documents/Exercise/F_5500_2017_Latest/output"+String.valueOf(i)+".json";
        }
}