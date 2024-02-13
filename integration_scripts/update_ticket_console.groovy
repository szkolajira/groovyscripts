import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import com.atlassian.jira.component.ComponentAccessor

def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueByCurrentKey("INTERNAL-1")

def appUrl = "http://localhost:8080/"
def apiEndpoint = "rest/api/2/issue/"+issue.getCustomFieldValue("External ID")

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def changeHistories = changeHistoryManager.getChangeHistories(issue)
def changes = changeHistories.last().getChangeItemBeans() 

// log.error(changes.field[0])

def updateTicket(String appUrl, String apiEndpoint, changes, issue) {

    def jsonBody

    if(changes.field[0] == "description") {
        jsonBody = ["update": ["description": [["set": issue.description]]]]
    } 
    else if(changes.field[0] == "summary") {
        jsonBody = ["update": ["summary": [["set": issue.summary]]]]
    }
    else {
        return
    }

    def finalUrl = appUrl + apiEndpoint
    def client = new RESTClient(finalUrl)

    client.setHeaders([
        Authorization      : "Bearer ODc2NjE3MjExNDE4OryU0OY4R4CPk7PM0Kig1bwuXZU/"
    ])

    client.handler.success = { HttpResponseDecorator response, json  ->
        
        return json
        
    }

    client.handler.failure = { HttpResponseDecorator response, json ->
        log.error(json)
    }

    client.put(
        contentType: ContentType.JSON,
        body: jsonBody
    )
}

updateTicket(appUrl, apiEndpoint, changes, issue)