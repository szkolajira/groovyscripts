import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

import com.atlassian.jira.event.type.EventDispatchOption

def updateTicket(issue, fieldName, fieldValue) {

    if(fieldName == "summary") {
        issue.update {
            setSummary(fieldValue)
            setEventDispatchOption(EventDispatchOption.DO_NOT_DISPATCH)
            setSendEmail(false)
        }
    }

    else if(fieldName == "description") {
        issue.update {
            setDescription(fieldValue)
            setEventDispatchOption(EventDispatchOption.DO_NOT_DISPATCH)
            setSendEmail(false)
        }
    }

    else if(fieldName == "status") {
        issue.transition(fieldValue)
    }
}

def getChange(issue, String url) {

    def client = new RESTClient(url)

    client.setHeaders([
        Authorization      : "Bearer token"
    ])

    client.handler.success = { HttpResponseDecorator response, json  ->


        if(json.changelog.total == 0) {
            return
        }
        
        def lastChange = json.changelog.histories.last()
        def lastAuthor = lastChange.author.name
        
        if(lastAuthor == "external_user") {
            return 
        }

        else {
            def fieldName = lastChange.items[0]["field"]
            def fieldValue = lastChange.items[0]["toString"]

            updateTicket(issue, fieldName, fieldValue)
        }
    }

    client.handler.failure = { HttpResponseDecorator response, json ->
        log.error(json)
    }

    client.get(
        contentType: ContentType.JSON
    )
}


Issues.search("project = 'INTERNAL' AND 'External ID' is not EMPTY").each { issue ->
    def externalId = issue.getCustomFieldValue("External ID")
    getChange(issue, "http://localhost:8080/rest/api/2/issue/" + externalId +"?expand=changelog")
}
