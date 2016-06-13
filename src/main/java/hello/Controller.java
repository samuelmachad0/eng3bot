package hello;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.db4o.ObjectSet;
import org.json.JSONException;

public class Controller {
	
	Model m;

	Controller() throws JSONException, IOException{

		m = new Model();
        try {
            m.pegarDadosDoArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

    public void inline(){
        m.inline();
    }

    public void read(byte[] body) throws IOException {

        m.inicialize(body);
        if(m.checkChat())
            m.readMessage();
        else
            m.inline();


    }
    public void send(String text){
        m.sendMessage("-133969837",text);
    }
    public void criarBanco(){
        try {
            m.pegarDadosDoArquivo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
