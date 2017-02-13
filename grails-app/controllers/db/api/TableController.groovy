package db.api

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import static groovy.json.JsonOutput.toJson

@Transactional(readOnly = true)
class TableController {
    def DbService

    def index(Integer max) {
        render toJson(DbService.getTables(params.ppid,params.pid))
    }

}
