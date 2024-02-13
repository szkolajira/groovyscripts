import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient


def getChange(String url) {

    def client = new RESTClient(url)

    client.setHeaders([
        Authorization      : "Bearer OTYyOTQ4MjM3ODU0OhNldBBpW2XXdoDowsVeE4Y2KrJs"
        // "X-Atlassian-Token": "no-check"
    ])

    client.handler.success = { HttpResponseDecorator response, json  ->
        return json.changelog.histories.last()

    def issue = json.changelog
    def changelog = issue.changelog
    def lastChange = changelog.histories.last()

    if (lastChange) {
        def lastAuthor = lastChange.author
        println "Last change made by: ${lastAuthor.displayName} (${lastAuthor.name})"
    } else {
        println "No change history found for the issue."
    }
    }

    client.handler.failure = { HttpResponseDecorator response, json ->
        log.error(json)
    }

    client.get(
        contentType: ContentType.JSON
    )
}

getChange("http://localhost:8080/rest/api/2/issue/HELPDESK-51?expand=changelog")



