package db.api

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import static groovy.json.JsonOutput.toJson

@Transactional(readOnly = true)
class QueryController {
    def DbService


    @Transactional
    def save(Query query) {
        log.info("query:" + DbService.createQuery(params.pid,query.name,query.sql))
        render toJson(DbService.createQuery(params.pid,query.name,query.sql))
    }

}
