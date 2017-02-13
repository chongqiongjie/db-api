package db.api

import grails.transaction.Transactional
import groovy.json.JsonSlurper
import thrift.hive.TCLIServiceClient
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.JSON

@Transactional
class DbService {
    def hiveClient
    def http
    DbService() {
        hiveClient = TCLIServiceClient.openHiveClient("192.168.1.3", 10000, "spiderdt", "spiderdt")
        http = new HTTPBuilder('http://192.168.1.3:1113')
    }
    
    def createQuery(connectorId,name,sql){
        log.info("sql:" + sql)
        def result =  http.request(POST,JSON){req -> 
                          uri.path = '/v1/statement'
                          headers.'X-Presto-User' =  'spiderdt'
                          headers.'X-Presto-Catalog' = 'hive'
                          body = sql.toString()
                      }
        log.info("result:" + result)
        def next_url_resp= http.request(GET,JSON){req -> uri.path = result.nextUri}
        def data_buffer = []
        while(next_url_resp.nextUri) {
            log.info("next_url data: ${next_url_resp.nextUri}")
            if(next_url_resp.data){data_buffer += next_url_resp.data}
            next_url_resp = http.request(GET,JSON){req -> uri.path = next_url_resp.nextUri}
        }
        def header = next_url_resp.columns.name
        def data = data_buffer.transpose()
        log.info("header:" + header)
        log.info("data:" + data)
        [header:header,data:data]
    }
    def getDatasets(connectorId){
        TCLIServiceClient.getSchemas(hiveClient)
    }
    def getTables(connectorId,datasetId){
        TCLIServiceClient.getTables(hiveClient,datasetId)
    }
    def getColumns(connectorId,datasetId,tableId){
        TCLIServiceClient.getColumns(hiveClient, datasetId,tableId)
    }
    def getData(connectorId,datasetId,tableId){
        TCLIServiceClient.getDataFrame(hiveClient, datasetId, tableId)
    }
}
