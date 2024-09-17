import netscape.javascript.JSObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp
{

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static JSONObject getWeatherData(String locationName)
    {
        //store location data from getLocationData in locationData object
        JSONArray locationData = getLocationData(locationName);

        //store the very first city within the JSON structure given via getLocationData which is located at index 0
        JSONObject location = (JSONObject)  locationData.get(0);
        //put latitude data from location object into variable for use in urlString
        double latitude = (double) location.get("latitude");
        //put longitude data from location object into variable for use in urlString
        double longitude = (double) location.get("longitude");

        //CELSIUS AND KMP/H
        //the api that will be called for weather data using geolocation data retrieved via getLocationData
        //String urlString = "https://api.open-meteo.com/v1/forecast?" +
          //     "latitude=" + latitude + "&longitude="+ longitude +
            //   "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FNew_York";

        //FAHRENHEIT AND MP/H
        //the api that will be called for weather data using geolocation data retrieved via getLocationData
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude="+ longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&temperature_unit=fahrenheit&wind_speed_unit=mph&timezone=America%2FNew_York";


        try
        {
            //store the response from urlString into the conn object via fetchApiResponse
            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn.getResponseCode() != 200)
            {
                System.out.println("Error: Could not connect to the API.");
                return null;
            }

            //build an empty string to store the JSON into
            StringBuilder resultJson = new StringBuilder();
            //build scanner to scan conn via getInputStream
            Scanner scanner = new Scanner(conn.getInputStream());
            //if the scanner reads input from conn.getInputStream...
            while (scanner.hasNext()){
                //put it in resultJson that was built earlier
                resultJson.append(scanner.nextLine());
            }

            //close scanner object
            scanner.close();

            //disconnect input stream
            conn.disconnect();

            /*The JSONParser object is responsible for taking the raw JSON string (which is stored in resultJson)
                and converting it into a Java object that can be worked with.*/
            JSONParser parser = new JSONParser();
            //create resultJsonObj and transfer the data from resultJson and put it into the object called resultJsonObj
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //get the hourly key from resultJsonObj and put it into hourly object
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");
            //get the time key from the hourly key and put it into the time JSONArray
            //time data should now be identical to this... "hourly": {
            //    "time": [
            //"hourly" is an object with complex values (keys). "time" is one of the keys, making timestamps the value of "time"
            JSONArray time = (JSONArray) hourly.get("time");
            //the value of the JSONArray time are the timestamps themselves, so JSONArray time is full of timestamps
            //findIndexOfCurrentTime will sort through the timestamps and return the one that matches the currentDateTime
            //currentDateTime returns from getCurrentTime, which is used in findIndexOfCurrentTime
            //i(returned) from findIndexOfCurrentTime is in int index
            int index = findIndexOfCurrentTime(time);
            //temperature_2m is also a complex value of hourly, making it a key and the temps themselves the value
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            /*grab the index of the current temperatureData that matches
             the index from findIndexOfCurrentTime and cast it into a double called temperature*/
            double temperature = (double) temperatureData.get(index);
            //weather_code is also a complex value of hourly, making it a key and the weather-codes themselves the value
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            /*grab the index of the current weathercode that matches
             the index from findIndexOfCurrentTime and cast it into a
             long called weatherCondition then store into String weatherCondition*/
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));
            //relative_humidity_2m is also a complex value of hourly, making it a key and the humidity percentages themselves the value
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            /*grab the index of the current relativeHumidity that matches
             the index from findIndexOfCurrentTime and cast it into a long called humidity*/
            long humidity = (long) relativeHumidity.get(index);
            //wind_speed_10m is also a complex value of hourly, making it a key and the wind speeds themselves the value
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
             /*grab the index of the current windSpeedData that matches
             the index from findIndexOfCurrentTime and cast it into a double called windSpeed*/
            double windSpeed = (double) windSpeedData.get(index);

            //create new weatherData object that will connect to frontend
            //the frontend will access the values via the keys
            JSONObject weatherData = new JSONObject();
            //tie "temperature" key to value temperature
            weatherData.put("temperature", temperature);
            //tie "weather_condition" key to value weatherCondition
            weatherData.put("weather_condition", weatherCondition);
            //tie "humidity" key to value humidity
            weatherData.put("humidity", humidity);
            //tie "windspeed" key to value windSpeed
            weatherData.put("windspeed", windSpeed);


            //return weatherData object to caller
            return weatherData;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //getLocationData will give locationData in the form of a JSONArray by passing locationName when invoking getLocationData
    public static JSONArray getLocationData(String locationName)
    {
        //replace spaces in locationName argument with pluses to fit formating
        locationName = locationName.replaceAll(" ","+");

        //the API url that will be called geolocation data
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try{
            //store the response from urlString into the conn object via fetchApiResponse
            HttpURLConnection conn = fetchApiResponse(urlString);

            //if the response code in conn is bad, draw error
            if(conn.getResponseCode() != 200)
            {
                System.out.println("Error: Could not connect to the API.");
                return null;
            }else{
                //build an empty string to store the JSON into
                StringBuilder resultJson = new StringBuilder();
                //build scanner to scan conn via getInputStream
                Scanner scanner = new Scanner(conn.getInputStream());
                //if the scanner reads input from conn.getInputStream...
                while (scanner.hasNext()){
                    //put it in resultJson that was built earlier
                    resultJson.append(scanner.nextLine());
                }

                //close scanner object
                scanner.close();

                //disconnect input stream
                conn.disconnect();

                /*The JSONParser object is responsible for taking the raw JSON string (which is stored in resultJson)
                and converting it into a Java object that can be worked with.*/
                JSONParser parser = new JSONParser();
                //create resultJsonObj and transfer the data from resultJson and put it into the object called resultJsonObj
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                //now we can put our API response into a JSON ARRAY object called locationData
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                //give locationData back to the caller so other functions(getWeatherData in this situation) can use it
                return locationData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // only used in getWeatherData to call Weather Forecast API and getLocationData to call Geolocation API
    private static HttpURLConnection fetchApiResponse(String urlString)
    {
        try {
            //store url string param/arg into url object
            URL url = new URL(urlString);
            //store the open connection to the url into the conn object
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //Retrieves data
            conn.setRequestMethod("GET");

            //now to actually initiate the connection to the url
            conn.connect();

            //return the conn object to the caller
            return conn;
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    private static int findIndexOfCurrentTime(JSONArray timeList)
    {
        //sore the current time in currentTime object via getCurrentTime method
        String currentTime = getCurrentTime();
        //loop through the arg/param as long as i is smaller than the arg/param...
        for(int i = 0; i < timeList.size(); i++)
        {
            //Store each index of the arg/param into time
            String time = (String) timeList.get(i);
            //if one of the times in the arg/param = currentTime via getCurrentTime...
            if(time.equalsIgnoreCase(currentTime))
            {
                //return that 1 particular matching index to the caller
                return i;
            }
        }

        return 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static String getCurrentTime()
    {
        //store current date and time in currentDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        //format currentDateTime to JSON liking
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        //give currentDateTime back to caller
        return currentDateTime.format(formatter);
    }

    //convert various weather codes to be more user-friendly
    private static String convertWeatherCode(long weatherCode)
    {
        String weatherCondition = "";
        if (weatherCode == 0L)
        {
            weatherCondition = "Clear";
        }
        else if (weatherCode > 0L && weatherCode <= 3L)
        {
            weatherCondition = "Cloudy";
        }
        else if ((weatherCode >= 51L && weatherCode <= 67L)
            || (weatherCode >= 80L && weatherCode <= 99L))
        {
            weatherCondition = "Rain";
        }
        else if (weatherCode >= 71L && weatherCode <= 77L)
        {
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }
}



