package integration

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import com.atlassian.jira.component.ComponentAccessor

def appUrl = "http://localhost:8080/"
def apiEndpoint = "rest/api/2/issue/"

def issue = event.issue

// setting an external ticket ID
def setExternalId(issue, key) {
    issue.update {
	    setCustomFieldValue("External ID", key)
    }
}


def createTicket(String appUrl, String apiEndpoint, issue) {

    def jsonBody = ["fields": [
            "project": [
                "id": "11303"
            ],
            "summary": issue.summary,
            "issuetype": [
                "id": "10801"
            ],
            "description": issue.description
            ]
    ]

    def finalUrl = appUrl + apiEndpoint
    def client = new RESTClient(finalUrl)

    client.setHeaders([
        Authorization      : "Bearer ODc2NjE3MjExNDE4OryU0OY4R4CPk7PM0Kig1bwuXZU/"
    ])

    client.handler.success = { HttpResponseDecorator response, json  ->
        setExternalId(issue, json.key)
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

createTicket(appUrl, apiEndpoint, issue)