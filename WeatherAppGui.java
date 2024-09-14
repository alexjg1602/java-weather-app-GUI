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
    //the WeatherAppGui constructor will be called when WeatherAppGui object is created so the object is ready for use
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
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String userInput = searchTextField.getText();
                //
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                weatherData = WeatherApp.getWeatherData(userInput);

                String weatherCondition = (String)weatherData.get("weather_condition");

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

                double temperature = (Double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (Long) weatherData.get("humidity");
                humidityText.setText("<html><b><Humidity> "+ humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b><windspeed> "+ windspeed + "km/h</html>");
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
