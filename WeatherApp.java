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

    public static JSONObject getWeatherData(String locationName)
    {
        //store location data from getLocationData in locationData object
        JSONArray locationData = getLocationData(locationName);

        //store the very first city within the JSON structure given via getLocationData which is located at index 0
        JSONObject location = (JSONObject)  locationData.get(0);
        //put latitude data from location object into variable
        double latitude = (double) location.get("latitude");
        //put longitude data from location object into variable
        double longitude = (double) location.get("longitude");

        //the api that will be called for weather data using geolocation data retrieved via getLocationData
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "& longitude="+ longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FNew_York";

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

            //explicitly get hourly weather data in the JSON structure and store it in hourly object
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");

            int index = findIndexOfCurrentTime(time);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


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
                //results in this situation is the key and the array of data(locationData) the API gives us is the value
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                //give locationData back to the caller so other functions(getWeatherData in this situation) can use it
                return locationData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }




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
        //sore the current time in currentTime object
        String currentTime = getCurrentTime();
        //as long as i is smaller than time List...
        for(int i = 0; i < timeList.size(); i++)
        {
            //create a time string and grab the time strings index(i)
            String time = (String) timeList.get(i);
            //if the time-string and timeString are identical regardless of casing...
            if(time.equalsIgnoreCase(currentTime))
            {
                //return the index timeList to the caller
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime()
    {
        //store current date and time in currentDateTime
        LocalDateTime currentDateTime = LocalDateTime.now();
        //format currentDateTime to liking
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        //give currentDateTime back to caller
        return currentDateTime.format(formatter);
    }
}



