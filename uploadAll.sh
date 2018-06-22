for filename in /Users/rohith/Documents/Exercise/F_5500_2017_Latest/output*; 
do
curl -XPOST https://search-awselasticsearch-rohith-lqottp4vgjrlzbexoggeth5rxa.us-east-1.es.amazonaws.com/_bulk --data-binary @${filename} -H 'Content-Type: application/json';
done