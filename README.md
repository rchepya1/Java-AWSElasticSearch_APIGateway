# Java-AWSElasticSearch_APIGateway
Java microservice to query ElasticSearch using Api agetway -  Lambda function

Goal:
Write a Java micro service to invokes AWS elastic search and make it available using API gateway - Using AWS Lambda Function.
Search should be allowed by Plan name, Sponsor name and Sponsor State

Test Data -http://askebsa.dol.gov/FOIA%20Files/2017/Latest/F_5500_2017_Latest.zip

Analysis:
Given Test Data is in CSV format. So converting it to JSON format so that it can be uploaded to the Elastic Search and index it.
API Gateway is a service by AWS to expose the API's easily and it can connect to Elastic Search through HTTPS, AWS service or Lambda functions.
Code to build the Elastic Search URL using the parameters supplied, parse the response and send it back to API gateway. Use Lambda function to deploy the code(.jar). 

Architecture:
API Gateway (Exposes the End Point URL) --> Lambda Function (Consumes request, queries backend and returns response) --> ElasticSearch (Where the actual data is present - Backend)

Prerequisites:
1. Create an Elastic Search domain in AWS - [Documentation](https://aws.amazon.com/elasticsearch-service/getting-started/) 
2. Create a API Gateway with Lambda function in AWS and configure right mapping templates to consume the parameters. [Documentation](https://docs.aws.amazon.com/apigateway/latest/developerguide/getting-started-with-lambda-integration.html)
	
	Update the Gateway by creating an API and a resource and configure the Method Executions like how is request consumed, integrated with Lambda and response generation.
3. Configure the Lambda function to query on Elastic Search.
	Data being present in the Elastic Search domain can be checked by a tool exposed by AWS - kibana.
	URL for the Elastic Search domain created and Kibana are exposed on the overview page of the cluster on AWS console

Code:
1. Convert the CSV to JSON and index the data for Elastic Search - CsvDataToIndexedJsonData.java.
	Bulk data upload to Elastic Search is limited to the size of the file. So, data is chuncked and converted to JSON.
	Once this is done, just upload each of these files. A shell script is available to loop over all the files - uploadAll.sh (Simple curl commands).
2. Put the Elastic Search domain in the code preparing the URL - ElasticSearchService.java
3. ElasticSearchService.java is a class which consumes parameters sent by API Gateway, prepares the url for ElasticSearch by appending them, hits it and sends back the response.
	Read the parameters from the InputStream and map it to the right queryParams and build the URL.
	A simple HTTPURLConnection will hit the Elastic Search with the URL.
4. Package this as a maven project with the dependencies and upload it to the Lambda function. This will connect the Lambda function to ElasticSearch whenever a call is made.

Logs can be checked in the monitoring tab of Lambda function

URLs:
ElasticSearch Endpoint : https://search-awselasticsearch-rohith-lqottp4vgjrlzbexoggeth5rxa.us-east-1.es.amazonaws.com (Internally used in the code)

Kibana: https://search-awselasticsearch-rohith-lqottp4vgjrlzbexoggeth5rxa.us-east-1.es.amazonaws.com/_plugin/kibana/

API Gateway URL: https://8f004003g2.execute-api.us-east-1.amazonaws.com/prod/AWSElasticSearch_personalCapital


Test:
A Get request can be made using any REST client like postMan or an REST client chrome extension or as a URL in a browser on the API Gateway

Accepted params:
	1. plan_name
	2. sponsor_name
	3. sponsor_state

Single accepted param Example: https://8f004003g2.execute-api.us-east-1.amazonaws.com/prod/AWSElasticSearch_personalCapital?sponsor_name=SENTERTAINMENT+TRAVEL%2C+INC.

Multiple accepted params: https://8f004003g2.execute-api.us-east-1.amazonaws.com/prod/AWSElasticSearch_personalCapital?plan_name=SENTERTAINMENT%20TRAVEL,%20INC.%20401K%20PROFIT%20SHARING%20PLAN&SPONSOR_DFE_NAME=SENTERTAINMENT%20TRAVEL,%20INC.
