package com.lightbend.akka.sample.cooker;


import com.lightbend.akka.sample.Printer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class ChefCooker extends Cooker {
	public static ActorSystem system = ActorSystem.create("Restaurant");
	public static ActorRef PrintActor = system.actorOf(Printer.props(), "PrintActor");
	public static ActorRef chef = system.actorOf(ChefCooker.props("Paul",PrintActor), "Paul");
	
	public static ActorSystem getSystem() {
		return system;
		
	}public static ActorRef getPrintActor() {
		return PrintActor;
	}
	
	public static ActorRef get_ChefCooker() {
		return chef;
	}
	private ChefCooker(String name, ActorRef printer) {
		super(name, printer);
		// TODO Auto-generated constructor stub
	}
	
}
