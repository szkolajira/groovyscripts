import groovyx.net.http.ContentType
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient

final apiUrl = "https://api.open-meteo.com/v1/forecast"


def cities = ["wroclaw": ["51", "17"], "warszawa": ["52","21"]]


def getWeather(String url, String city, cities) {
    def params = "&current_weather=true&hourly=temperature_2m"
    def cityCoor = "?latitude="+(cities[city])[0]+"&longitude="+(cities[city])[1]
    
    def finalUrl = url + cityCoor + params
    def client = new RESTClient(finalUrl)

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

getWeather(apiUrl, "warszawa", cities)
