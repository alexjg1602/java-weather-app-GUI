import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

public class WeatherAppGui extends JFrame
{
    public JSONObject weatherData;
    //the WeatherAppGui constructor will be called when WeatherAppGui object is created in AppLauncher so the object is ready for use
    public WeatherAppGui()
    {
        //object GUI and title
        super("Weather App");

        //kill object on closure
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //object dimensions
        setSize(450, 650);

        //load gui object to center screen
        setLocationRelativeTo(null);

        //manually position components in gui object with coordinates(no layout manager)
        setLayout(null);

        //no resizing object
        setResizable(false);

        //calling so that components can be added to the JFrame object content pane
        addGuiComponents();
    }

    /*addGuiComponents(); definition, calling will create whatever object component and its configs and will add
    to the JFrame content pane object with the help of add();*/
    private void addGuiComponents()
    {
        //searchbar object component creation
        JTextField searchTextField = new JTextField();

        //searchbar location and size
        searchTextField.setBounds(15, 15, 351, 45);

        //font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        /*this is what calling addGuiComponents() calls... add() is inherited from one of JFrames parent classes
        (container). add() will add object components such as button and text fields to a JFrame object content pane */
        //adds searchbar to JFrame
        add(searchTextField);

        /*cloudy image object component creation from JLabel class which is inherited from swing and object creation
        will invoke loadImage which will load cloudy.png*/
        //cloudy icon
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //temp text object creation and config
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description object creation and config
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);


        //humidity icon object creation and config
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //humidity text object creation and config
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //wind speed icon object creation and configs
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        //wind speed text object creation and configs
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/hr</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        /*search button object component creation from JButton class which is inherited from swing and object creation
        will invoke loadImage which will load search.png*/
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //hand cursor will appear when hovering over search button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        //button object component dimensions and coordinates
        searchButton.setBounds(375, 13, 47, 45);
        //



        searchButton.addActionListener(new ActionListener()
        {
            //actionPerformed invoked when searchButton is clicked
            @Override
            public void actionPerformed(ActionEvent e)
            {
                //gather the input from searchTextField object and put it into userInput
                String userInput = searchTextField.getText();
                //if userInput contains more than 1 space b2b, replace it with one space
                //if userInput is empty, return
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                //now lets store getWeatherData invocation from the backend logic into weatherData
                //also userInput will be the getWeatherData arg/param
                weatherData = WeatherApp.getWeatherData(userInput);

                /*grab weather_condition key from weatherData JSONObject which
                 was crated via getWeatherData in backend logic*/
                String weatherCondition = (String)weatherData.get("weather_condition");

                //display appropriate data depending on what weatherCondition value contains
                switch (weatherCondition)
                {
                    case "Clear":
                        weatherConditionImage.setIcon(new ImageIcon("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(new ImageIcon("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(new ImageIcon("src/assets/rain.png"));
                        break;
                    case "Snowr":
                        weatherConditionImage.setIcon(new ImageIcon("src/assets/snow.png"));
                        break;
                }

                //CELSIUS
                //display appropriate data depending on what temperature value contains
                //double temperature = (Double) weatherData.get("temperature");
                //temperatureText.setText(temperature + " C");

                //FAHRENHEIT
                //display appropriate data depending on what temperature value contains
                double temperature = (Double) weatherData.get("temperature");
                temperatureText.setText(temperature + " F");

                weatherConditionDesc.setText(weatherCondition);

                //display appropriate data depending on what humidity value contains
                long humidity = (Long) weatherData.get("humidity");
                humidityText.setText("<html><b><Humidity> "+ humidity + "%</html>");

                //KMP/H
                //display appropriate data depending on what windspeed value contains
                //double windspeed = (double) weatherData.get("windspeed");
                //windspeedText.setText("<html><b><windspeed> "+ windspeed + "km/h</html>");

                //MP/H
                //display appropriate data depending on what windspeed value contains
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b><windspeed> "+ windspeed + " MP/H</html>");
            }

        });
        /*this is what calling addGuiComponents() calls... add() is inherited from one of JFrames parent classes
        (container). add() will add object components such as button and text fields to a JFrame object content pane */
        //adds search button to JFrame
        add(searchButton);
    }


    private ImageIcon loadImage(String resourcePath)
    {
        try
        {
            BufferedImage image;
            image = ImageIO.read(new File(resourcePath));

            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();

            System.out.println("Error loading image");
            return null;
        }
    }
}
