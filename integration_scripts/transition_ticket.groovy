import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import com.atlassian.jira.component.ComponentAccessor

def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def changeHistories = changeHistoryManager.getChangeHistories(event.issue)
def lastChange = changeHistories.last().getChangeItemBeans()

def appUrl = "http://localhost:8080/"
def apiEndpoint = "/rest/api/2/issue/"+event.issue.getCustomFieldValue("External ID") + "/transitions"


def newStatusName
def newStatusId

def transitions = [
    "To Do": 11,
    "In Progress": 21,
    "Done": 31
]

if(lastChange["field"][0] == "status") {
    newStatusName = lastChange["toString"][0]
    newStatusId = transitions[newStatusName]
}
else {
    return
}

def transitionTicket(String appUrl, String apiEndpoint, newStatusId) {

    
    def jsonBody  = [
        "transition": ["id": newStatusId]
    ]

    def finalUrl = appUrl + apiEndpoint
    def client = new RESTClient(finalUrl)

    client.setHeaders([
        Authorization      : "Bearer token"
    ])

    client.handler.success = { HttpResponseDecorator response, json  ->
        
        return json
        
    }

    client.handler.failure = { HttpResponseDecorator response, json ->

        log.error(json)
    }

    client.post(
        contentType: ContentType.JSON,
        body: jsonBody
    )
}

transitionTicket(appUrl, apiEndpoint, newStatusId)
