import javax.swing.*;


public class AppLauncher
{
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
    {
        @Override
        public void run()
        {
            //Open GUI window
            //new WeatherAppGui().setVisible(true);

            System.out.println(WeatherApp.getLocationData("Las Vegas"));
        }
    });
    }
}
