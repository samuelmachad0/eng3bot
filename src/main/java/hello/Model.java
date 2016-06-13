package hello;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.db4o.query.Query;
import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import org.json.JSONException;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.*;
import com.pengrad.telegrambot.*;

import com.pengrad.telegrambot.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.pengrad.telegrambot.model.request.*;

import com.db4o.config.EmbeddedConfiguration;

import javax.imageio.ImageIO;
import java.io.*;

public class Model{
	
	ObjectContainer allArtists;
	TelegramBot bot;
	String response;
	JSONObject obj;
	String texto;
	String chat;

	public Connection c;
	
	Model() throws MalformedURLException, JSONException, UnsupportedEncodingException {
		bot = TelegramBotAdapter.build("190311592:AAHx75xY3p1WscwG8hs4EuSq3SQCiYCquyQ");

		EmbeddedConfiguration conf = Db4oEmbedded.newConfiguration();
		conf.common().objectClass(Artist.class).cascadeOnUpdate(true);
		conf.common().objectClass(Artist.class).cascadeOnDelete(true);
		allArtists = Db4oEmbedded.openFile(conf, "artistas.db4o");

		c = new Connection(this);

	}
	void inicialize(byte[] body){
		try {
			response = new String(body, "UTF-8");
			obj = new JSONObject(response);
			texto = obj.getJSONObject("message").getString("text");
			chat = Integer.toString(obj.getJSONObject("message").getJSONObject("chat").getInt("id"));
		}
		catch(Exception e){

		}
	}

	public boolean checkChat(){
		if(obj.toString().indexOf("chat") != -1){
			return true;
		}
			return false;

	}

	protected void sendMessage(String chatId, String text){
		bot.sendMessage(chatId, text);
	}
	protected void readMessage() throws IOException  {

		try {

			if(texto.indexOf("/pais") != -1){
				if(texto.equals("/pais")){
					//Pega um aleatorio
					sendMessage(chat, "Preciso pegar um pais aleatorio");

				} else {
					//Realiza a busca pois o comando foi /pais algumacoisa
					sendMessage(chat, "Preciso buscar o pais especificado");
					this.sendResultPorLoc(texto.substring(texto.indexOf("/pais")+5, texto.length()));
				}

			} else if(texto.indexOf("/artista") != -1){

				if(texto.equals("/artista")){
					// Pega um artista aleatorio

					sendMessage(chat, "Preciso pegar um artista aleatorio");
                    this.sendResultPorNome();
				} else {
					//Realiza a busca pois o comando foi /artista algumacoisa
					//sendMessage(chat, "Preciso buscar o artista especificado");
					this.sendResultPorNome(texto.substring(texto.indexOf("/artista")+9, texto.length()));
				}
			} else {
				// Nenhum Comando encontrado
				sendMessage(chat, "Ops, comando incorreto");
			}

		} catch (Exception ex) {
			sendMessage("-133969837", "Deu merda:\n" + ex.toString());
		}
	}

	public void AtualizarDatabase() {
		Thread t = new Thread() {
		    public void run() {
		    	while(true){
		    		try {
		    			c.generateJson(c.getData());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
		    }
		};
		t.start();
		
	}

	public void sendResult(List<Artist> result){
		int i = 0;
			for (Artist o : result) {
				i++;
				if(i > 3)
					break;
				try {
					sendPhoto(chat, o.getArte(), o.getNome() + "\n Localização: " + o.getLocation() + "\n Saiba Mais: " + o.getLink());
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

	}
    public void sendResultArt(List<Artist> result){
        for(int i = 0; i < 3; ++i){
            try {
                Random r = new Random();
                Artist art = result.get(r.nextInt(result.size()));
                sendPhoto(chat, art.getArte(), art.getNome() +"\n Localização: " + art.getLocation() + "\n Saiba Mais: " + art.getLink());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

	public void sendResultPorLoc(String loc){
		List<Artist> result = pegarArtistaPorLoc(loc);
		sendResult(result);
	}

	public void sendResultPorNome(String nome){
		List<Artist> result = pegarArtistaPorNome(nome);
		sendResult(result);
	}
    public void sendResultPorNome(){
        List<Artist> result = pegarArtistaPorNome("");
        sendResultArt(result);
    }

	
	public void pegarDadosDoArquivo() throws IOException{
		
		FileReader fr = new FileReader("ArtistasRecuperadosAtravesdaAPI.txt");
		BufferedReader bf = new BufferedReader(fr);
		
		String linha;
		
		while((linha = bf.readLine()) != null){
			Artist a = new Artist(linha.substring(0, linha.indexOf("@")),linha.substring( linha.indexOf("@") + 1,linha.indexOf("#")),linha.substring( linha.indexOf("#") + 1,linha.indexOf("%")),linha.substring( linha.indexOf("%") + 1,linha.length()));
			allArtists.store(a);
		}
		
		allArtists.commit();
		//allArtists.close();
	}

	public ObjectSet pegarArtistaPorNome(String nome){
		Query query = allArtists.query();
		query.constrain(Artist.class);
		query.descend("nome").constrain(nome).like();
        ObjectSet result = query.execute();
				//allArtists.queryByExample(new Artist(nome, null, null, null));
        return result;
	}


	public  ObjectSet pegarArtistaPorLoc(String loc){
        Query query = allArtists.query();
        query.constrain(Artist.class);
        query.descend("location").constrain(loc).like();
        ObjectSet result = query.execute();
        //allArtists.queryByExample(new Artist(nome, null, null, null));
        return result;
	}

	protected void sendPhoto(String chat, String photoLink,String caption) throws IOException  {
		Image image = null;
		URL url = new URL(photoLink);
		image = ImageIO.read(url);
		BufferedImage originalImage = (BufferedImage) image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(originalImage, "jpg", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();

		bot.sendPhoto(chat, InputFileBytes.photo(imageInByte), caption, null, new ReplyKeyboardHide());
	}

	protected void inline(){
		Update update = BotUtils.parseUpdate(obj.toString()); // Decodifica o JSON que vem do telegram

		InlineQuery inlineQuery = update.inlineQuery(); // Pega somente a string a ser buscado (@eng3bot dilma)

		InlineQueryResult[] resultado = new InlineQueryResult[3]; // Declarou a lista
		List<Artist> artistas  = pegarArtistaPorNome( inlineQuery.query() ); // Buscando no DB4O com parâmetro
		int i=0;
		for ( Artist o : artistas ) {
		if(i==3)
			break;

				resultado[i] =  new InlineQueryResultPhoto(String.valueOf(i), o.getLink(), o.getArte()); // Popula a lista
				i++;


		}
		bot.execute(new AnswerInlineQuery(inlineQuery.id(),  resultado)); // Devolve para o usuário a lista




	}

}
