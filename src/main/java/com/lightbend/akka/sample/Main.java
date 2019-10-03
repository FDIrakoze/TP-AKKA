package com.lightbend.akka.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.lightbend.akka.sample.cooker.ChefCooker;
import com.lightbend.akka.sample.cooker.Cooker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {
	private static ArrayList<ActorRef> free_cookers = new ArrayList<ActorRef>();
	private static ArrayList<String> commands = new ArrayList<String>();

	
	public static ActorSystem system = ChefCooker.getSystem();
	public static ActorRef chef = ChefCooker.get_ChefCooker();
	public static ActorRef printActor = ChefCooker.getPrintActor();
	
	public static ActorRef cooker_1;
	public static ActorRef cooker_2;
	public static ActorRef cooker_3;

	/**
	 * The main method use to launch the app
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			generateActor();
			String message;
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			
			  while (!br.readLine().equals("QUIT")) {
				  System.out.println("Veuillez saisir Plat à transmettre au Chef: ");
				  message = br.readLine();
				  command(message);
			  }
			 

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize different Actor needed in the app
	 */
	public static void generateActor() {
		cooker_1 = createActor("Leo");
		cooker_2 = createActor("Bob");
		cooker_3 = createActor("John");
	}

	/**
	 * Create a Cooker actor
	 * 
	 * @param name
	 * @return an Actor
	 */
	private static ActorRef createActor(String name) {
		ActorRef actor = system.actorOf(Cooker.props(name, printActor), name);
		free_cookers.add(actor);
		return actor;
	}

	/**
	 * Method enabling the Chef to send command to cooker
	 * 
	 * @param message
	 */
	private static void command(String message) {
		chef.tell(new Cooker.ToTell(message, free_cookers,false, chef,commands), ActorRef.noSender());
	}

}
