package com.lightbend.akka.sample.cooker;


import com.lightbend.akka.sample.Printer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ChefCooker extends Cooker {
	public static ActorSystem system = ActorSystem.create("Restaurant");
	public static ActorRef PrintActor = system.actorOf(Printer.props(), "PrintActor");
	public static ActorRef chef = system.actorOf(ChefCooker.props("Paul",PrintActor), "Paul");
	
	/**
	 * @return the actor system
	 */
	public static ActorSystem getSystem() {
		return system;
		
	}/**
	 * @return the printer actor
	 */
	public static ActorRef getPrintActor() {
		return PrintActor;
	}
	
	/**
	 * @return the chief actor
	 */
	public static ActorRef get_ChefCooker() {
		return chef;
	}
	/**
	 * @param name
	 * 			Name of the actor
	 * @param printer
	 * 			Actor in charge of printing
	 */
	private ChefCooker(String name, ActorRef printer) {
		super(name, printer);
		// TODO Auto-generated constructor stub
	}
	
}
