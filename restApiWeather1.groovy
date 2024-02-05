import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

final apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=51.107883&longitude=17.038538&current_weather=true&hourly=temperature_2m"

def getWeather(String url) {

    def client = new RESTClient(url)

    client.handler.success = { HttpResponseDecorator response, json ->
        return json.current_weather.temperature
    }

    client.handler.failure = { HttpResponseDecorator response, json ->
        log.error(json)
    }

    client.get(
        contentType: ContentType.JSON
    )
}


getWeather(apiUrl)
