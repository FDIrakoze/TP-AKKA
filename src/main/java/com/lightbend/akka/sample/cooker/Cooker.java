package com.lightbend.akka.sample.cooker;

import java.util.HashMap;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class Cooker extends AbstractActor {
	

	// Defining the Cooker properties
	private final String name;
	private final ActorRef printer;

	/**
	 * Cooker constructor
	 * 
	 * @param name
	 * @param printer
	 */
	public Cooker(String name, ActorRef printer) {
		this.name = name;
		this.printer = printer;
	}

	/**
	 * Initialize the properties of the cooker
	 * 
	 * @param name
	 * @param printer
	 * @return Props
	 */
	static public Props props(String name, ActorRef printer) {
		return Props.create(Cooker.class, () -> new Cooker(name, printer));
	}

	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The Actor in charge of printing in AKKA system
	 * 
	 * @return printer
	 */
	public ActorRef getPrinter() {
		return printer;
	}

	/**
	 * Get the name of the cooker
	 *  
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	static public class WhoToTell {
		public final String who;
		public final HashMap<String, ActorRef> cookers;
		
		/**
		 * WhoToTell constructor
		 * 
		 * @param who
		 * @param cookers
		 */
		public WhoToTell(String who, HashMap<String, ActorRef> cookers) {
			this.who = who;
			this.cookers = cookers;
		}
	}
}
