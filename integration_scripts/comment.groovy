package integration

import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

def appUrl = "http://localhost:8080/"
def apiEndpoint = "rest/api/2/issue/"+event.issue.getCustomFieldValue("External ID")+"/comment"

def addComment(String appUrl, String apiEndpoint, event) {

    def author = event.comment.getAuthorFullName()
    def comment = event.comment.getBody()

    def jsonBody = [
        "body": "Comment added by " + author + "\n\n" + comment + ""
    ]

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

    client.post(
        contentType: ContentType.JSON,
        body: jsonBody
    )
}

addComment(appUrl, apiEndpoint, event)