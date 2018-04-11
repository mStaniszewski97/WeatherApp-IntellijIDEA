package controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;

public class MainController implements Initializable {

	@FXML
    private Label top;

    @FXML
    private ImageView icon;

    @FXML
    private Label tempLabel;

    @FXML
    private Label weatherLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label wind;

    @FXML
    private Label cloud;

    @FXML
    private Label pressure;

    @FXML
    private Label humidity;

    @FXML
    private Label sunrise;

    @FXML
    private Label sunset;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TextField cityText;

    @FXML
    private Button pdfButton;
    
    @FXML
    private Button checkButton;

    @FXML
    void checkClick(ActionEvent event) {
    	if(choiceBox.getValue()=="Search by city name") {
    		setWeather(cityText.getText());
    	}
    	else if(choiceBox.getValue()=="Search by your location") {
    		String ip = null;
    		try {
				ip = getPublicIp();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    		System.out.println(ip);
    		String sURL = "http://ip-api.com/json/";
    		sURL = new StringBuilder(sURL).append(ip).toString();
    		System.out.println(sURL);
    		JsonElement root = null;
		    try {
		    	URL url = new URL(sURL);
		    	HttpURLConnection request = (HttpURLConnection) url.openConnection();
				request.connect();
				
				JsonParser jp = new JsonParser();
				root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				alert("Localization not found :(");
				e.printStackTrace();
			}
		    JsonObject rootobj = root.getAsJsonObject();
		    System.out.println(rootobj.toString());
		    String city = rootobj.get("city").getAsString();
		    city = polishFix(city);
		    System.out.println(city);
		    setWeather(city);
    	}
    	else {
    		alert("Choose something, pls....");
    	}
    }
    
	@FXML
    void pdfClick(ActionEvent event) throws FileNotFoundException, DocumentException{
    	if(top.getText()=="") {
    		alert("You need to find weather!");
    	}
    	else {
    		String name = top.getText().substring(11);
    		Date data = new Date();
    		pdf(name, data);
    	}
    }
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	// TODO Auto-generated method stub
    	cityText.setPromptText("city name");
    	top.setText("");
    	tempLabel.setText("");
    	weatherLabel.setText("");
    	dateLabel.setText("");
    	choiceBox.getItems().addAll("Search by city name", "Search by your location");
    }
    void alert(String text) {
    	Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Ups...");
		alert.setHeaderText(null);
		alert.setContentText(text);

		alert.showAndWait();
    }
    String fixTime(String time) {
    		time = new StringBuilder(time).delete(0, 11).toString();
    		time = new StringBuilder(time).delete(7, 16).toString();
    		return time;
    }
    String getPublicIp() throws IOException {
    		URL whatismyip = null;
    		String ip = null;
			try {
				whatismyip = new URL("http://checkip.amazonaws.com");
				BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
	    			ip = in.readLine(); //you get the IP as a String
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return ip;
    }
    String polishFix(String text) {
    	text = text.replace("ł","l");
    	return text;
    }
    void setWeather(String city) {
    	String sURL = "http://api.openweathermap.org/data/2.5/weather?q=";
		sURL = new StringBuilder(sURL).append(city).toString();
		sURL = new StringBuilder(sURL).append("&APPID=c51c62d65d9b1125d513f20cb5107341").toString();
		System.out.println(sURL);
		
		JsonElement root = null;
	    try {
	    	URL url = new URL(sURL);
	    	HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.connect();
			
			JsonParser jp = new JsonParser();
			root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			alert("Localization not found :(");
			e.printStackTrace();
		}

	    //Convert the input stream to a json element
	    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
	    System.out.println(rootobj.toString());
	    JsonObject main = rootobj.get("main").getAsJsonObject();
	    JsonObject windJ = rootobj.get("wind").getAsJsonObject();
	    JsonArray weather = rootobj.get("weather").getAsJsonArray();
	    JsonObject weatherr = weather.get(0).getAsJsonObject();
	    long sunriseUnix = rootobj.get("sys").getAsJsonObject().get("sunrise").getAsLong();
	    long sunsetUnix = rootobj.get("sys").getAsJsonObject().get("sunset").getAsLong();
	    long dateUnix = rootobj.get("dt").getAsLong();
	    Date date = new Date ();
	    date.setTime((long)sunriseUnix*1000);
	    sunrise.setText(fixTime(date.toString()));
	    date.setTime((long)sunsetUnix*1000);
	    sunset.setText(fixTime(date.toString()));
	    date.setTime((long)dateUnix*1000);
	    dateLabel.setText("	" + fixTime(date.toString()));
	    
	    String imageUrl = "http://openweathermap.org/img/w/";
	    imageUrl = new StringBuilder(imageUrl).append(weatherr.get("icon").getAsString()+ ".png").toString();
	    icon.setImage(new Image(imageUrl));
	    int temp = main.get("temp").getAsInt();
	    temp = temp - 273;
	    top.setText("Weather in " + rootobj.get("name").getAsString()+", "+rootobj.get("sys").getAsJsonObject().get("country").getAsString());
	    tempLabel.setText(Integer.valueOf(temp).toString() + " C");
	    weatherLabel.setText(weatherr.get("main").getAsString());
	    wind.setText(windJ.get("speed").getAsString() + " m/s");
	    cloud.setText(rootobj.get("clouds").getAsJsonObject().get("all").getAsString()+"%");
	    pressure.setText(main.get("pressure").getAsString()+"hPA");
	    humidity.setText(main.get("humidity").getAsString()+"%");
    }
    void pdf(String name, Date date) throws DocumentException, FileNotFoundException {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(date);
    	Document document = new Document();
    	PdfWriter.getInstance(document, new FileOutputStream(name + cal.get(Calendar.DAY_OF_MONTH) + "." + cal.get(Calendar.YEAR) + ".pdf"));
    	 
    	document.open();
    	Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
    	Chunk chunk = new Chunk("Weather in "+ name, font);
    	Paragraph paragraph = new Paragraph("Temp: " + tempLabel.getText() + "  Weather status: " + weatherLabel.getText() + "wind speed: " + wind.getText() +" and clouds: "+ cloud.getText());
    	 
    	document.add(chunk);
    	document.add(paragraph);
    	document.close();
    }
}