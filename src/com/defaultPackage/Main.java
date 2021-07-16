package com.defaultPackage;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.Graphics;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;


public class Main extends Frame implements ActionListener,FocusListener{

    Image image;
    String jsonString;

    Panel p1 = new Panel();
    Panel p2 = new Panel();

    Button GetWeather = new Button("Get Weather Details");

    TextField cityName = new TextField("Enter City Name");

    Label Temperature = new Label("TEMP");
    Label Description = new Label("DESCRIPTION");

    public static String GetJsonString(String URL)
    {
        // Request
        var request = HttpRequest.newBuilder().GET().uri(URI.create(URL)).build();

        // Creating client
        var client = HttpClient.newBuilder().build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().toString();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "error fetching data from the api";
    }

    public void GetImage(String name){
        try {
            File snowManImageFile = new File("C:\\Users\\User\\Desktop\\JavaGroupProject\\"+ name +".png");
            image = ImageIO.read(snowManImageFile);
        }
        catch (IOException e) {
            System.out.println("cant load file");
        }
    }

    public Main() {
        // get image
        GetImage("Icon");

        // configure frame
        setLocation(300,75);
        setTitle("Weather Application");
        setBackground(Color.decode("#25BBFC"));
        setSize(800,600);
        setVisible(true);
        setLayout(null);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });

        // setup UI components
        p1.setBounds(0,100,800,100);
        //p1.setBackground(Color.RED);
        p2.setBounds(0,250,400,350);
        //p2.setBackground(Color.BLUE);

        GetWeather.addActionListener(this);
        GetWeather.setActionCommand("getWeather");
        GetWeather.setForeground(Color.decode("#25BBFC"));
        GetWeather.setBackground(Color.white);
        GetWeather.setFont(new Font("Sheriff",Font.BOLD,40));

        cityName.addFocusListener(this);
        cityName.setForeground(Color.gray);
        cityName.setFont(new Font("Sheriff",Font.ITALIC ,40));

        Temperature.setForeground(Color.white);
        Temperature.setAlignment(Label.CENTER);
        Temperature.setFont(new Font("Sheriff",Font.BOLD,100));


        Description.setForeground(Color.white);
        Description.setAlignment(Label.CENTER);
        Description.setFont(new Font("Sheriff",Font.PLAIN,30));

        // add UI components
        add(p1);
        add(p2);
        p1.add(cityName);
        p1.add(GetWeather);
        p2.add(Temperature);
        p2.add(Description);

    }

    public void paint(Graphics g) {
        // draw image
        g.drawImage(image,400,200,325,300,this);
    }
    public static void main(String[] args) {
        Main project = new Main();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand() == "getWeather") {
            if(cityName.getText() != null){
                System.out.println("get weather details button pressed");
                String URL = "https://api.openweathermap.org/data/2.5/weather?q="+ cityName.getText() + "&appid=b7eda33dc613ce08c2dd0aa117bfa1af";
                jsonString = GetJsonString(URL);
                System.out.println(jsonString);

                Gson gson = new Gson();
                var obj = gson.fromJson(jsonString, WeatherResponse.class);

                String des = (obj.weather[0].description);
                if(des.equals("broken clouds") || des.equals("overcast clouds") || des.equals("moderate rain")) {
                    des = "rainy";
                    System.out.println("rainy");
                }
                else if(des.equals("scattered clouds")) {
                    des = "cloudy";
                    System.out.println("cloudy");
                }


                des = des.toUpperCase(Locale.ROOT);
                Description.setText(des);

                String temp = Integer.toString((int)obj.main.temp-272) + "°C";
                Temperature.setText(temp);

                if((int)obj.main.temp-273 < 25)
                {
                    // cold
                    GetImage("Cold");
                    repaint();
                }
                else if((int)obj.main.temp-273 > 35)
                {
                    // too hot
                    GetImage("Hot");
                    repaint();
                }
                else
                {
                    //moderate
                    GetImage("Moderate");
                    repaint();
                }
            }

        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        cityName.setText("");
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
