package hello;

import static java.awt.SystemColor.text;
import static spark.Spark.*;
import spark.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.lang.*;

import spark.template.mustache.*;



public class MainServer {

    public static void main(String[] args) throws IOException {
        //Token do bot: 190311592:AAHx75xY3p1WscwG8hs4EuSq3SQCiYCquyQ



        ProcessBuilder process = new ProcessBuilder();
        Controller c = new Controller();

        Integer myPort;
        if (process.environment().get("PORT") != null) {
            myPort = Integer.parseInt(process.environment().get("PORT"));
        } else {
            myPort = 8080;
        }
        port(myPort);

		//Delivery static file
		staticFileLocation("/static");


        get("/", (req, res) -> {



            return "www.telegram.me/eng3bot";
        });
        post("/readMessages", (req, res) -> {
            c.read(req.bodyAsBytes());
            return "Sucesso";
        });

        get("/testMessages", (req, res) -> {
            c.send("Mensagem de teste enviada.");
            return "Uma mensagem de teste foi enviada para o grupo";
        });

        get("/populabanco", (req, res) -> {
            c.criarBanco();
            return "Gogo pOWER rANGERS";
        });

        get("/da", (req, res) -> {
            c.send("Olá, vim cobrar os 10% do DA");
            return "Gogo pOWER rANGERS";
        });
    }
}
