package hello;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class Connection {
	
	private URL url;
	private String token;
	private Model m;
	
	Connection(Model m) throws MalformedURLException, JSONException {
        this.m = m;
		try {
			token = "JvTPWe4WsQO-xqX6Bts49urZwRwb5rAS-8weIXlwmKsOpLMTw_8aoBGBvAD2QdpR4yd8pwl2sanMkXk8D1DOnQLJSNltTxtsZbWeTs4Qj42WMoYwF0ASW56FP1gi1jXcd-C0ht86k1qum2_7R02DzeNUOA1AQxUMvCCxcAuPVEJ05srrS7fOzmGZPJbSBllETYNyssR8bSXm2SPDrvLukMU1Il-tFtBj6kkPDmXjnuI=";
			url = new URL("https://api.artsy.net/api/artists");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setToken (String s){
		token = s;
	}
	
	public void changeURL(StringBuilder r) throws JSONException, MalformedURLException{
		  JSONObject obj = new JSONObject(r.toString()).getJSONObject("_links");
		  url = new URL(obj.getJSONObject("next").getString("href"));

	}
	
	
public void salvarDados(Artist a) throws IOException{

    m.allArtists.store(a);

}
	
	
	
	public JSONObject getData() throws JSONException, MalformedURLException {
    	
    	String Token = token; 
        final StringBuilder result = new StringBuilder();


        HttpURLConnection urlConnection = null;
        try {
            
           
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setRequestProperty("Accept", "application/vnd.artsy-v2+json");
            urlConnection.setRequestProperty("X-Xapp-Token", Token);
            
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);



            int data = isw.read();
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                result.append(current);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                urlConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      
    	
        changeURL(result);
    	
    	return new JSONObject(result.toString());
    }
	
	public void generateJson(JSONObject json) throws IOException {
		    		try {
		    			
		    			for(int i = 0; i<5;i++){
		            		JSONObject objeto = json.getJSONObject("_embedded");
		            		
		            		if(!objeto.getJSONArray("artists").getJSONObject(i).getString("location").equals("") && 
		            				!objeto.getJSONArray("artists").getJSONObject(i).getString("location").equals(null) && 
		            				!objeto.getJSONArray("artists").getJSONObject(i).getJSONObject("_links").getJSONObject("thumbnail").getString("href").equals(null) 
		            						&& !objeto.getJSONArray("artists").getJSONObject(i).getJSONObject("_links").getJSONObject("thumbnail").getString("href").equals("")){
		            			Artist artist = new Artist(objeto.getJSONArray("artists").getJSONObject(i).getString("name"),objeto.getJSONArray("artists").getJSONObject(i).getString("location"), objeto.getJSONArray("artists").getJSONObject(i).getJSONObject("_links").getJSONObject("thumbnail").getString("href"), objeto.getJSONArray("artists").getJSONObject(i).getJSONObject("_links").getJSONObject("permalink").getString("href"));
		            			 System.out.println("Nome: "+ artist.getNome() + "\nLocaliza��o: " + artist.getLocation()+ "\nFoto: " + artist.getArte() + "\nLink do Artista: "+ artist.getLink());
		            			salvarDados(artist);


		            		}
		    			}
		    			

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	}

}
