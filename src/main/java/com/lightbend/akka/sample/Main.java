package com.lightbend.akka.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.lightbend.akka.sample.cooker.Cooker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Main {
	private static HashMap<String, ActorRef> cookers;
	
	public static ActorSystem system;
	public static ActorRef printActor;
	public static ActorRef chef;
	public static ActorRef cooker_1;
	public static ActorRef cooker_2;
	public static ActorRef cooker_3;
	
	
	/**
	 * the main method use to launch the app
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			generateActor();
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String message;

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
		system = ActorSystem.create("Restaurant");
		printActor =system.actorOf(Printer.props(), "PrintActor");
		chef = createActor("chef_Paul");
		cooker_1 = createActor("cooker_Leo");
		cooker_2 = createActor("cooker_Bob");
		cooker_3 = createActor("cooker_John");
	}

	/**
	 * Create a Cooker actor
	 * 
	 * @param name
	 * @return an Actor 
	 */
	private static ActorRef createActor(String name) {
		ActorRef actor = system.actorOf(Cooker.props(name, printActor),name);
		cookers.put(name, actor);
		return actor ;
	}
	
	/**
	 * Method enabling the Chef to send command to cooker
	 * 
	 * @param message
	 */
	private static void command(String message) {
		cookers.get("chef_Paul").tell(new Cooker.WhoToTell(message, cookers), ActorRef.noSender());
	}

}
