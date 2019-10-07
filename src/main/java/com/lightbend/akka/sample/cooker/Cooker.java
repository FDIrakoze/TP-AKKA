package com.lightbend.akka.sample.cooker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.lightbend.akka.sample.Printer.Greeting;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * @author Franco Davy Irakoze
 * @author Jeremy Vienne
 *
 */
public class Cooker extends AbstractActor {
	// Defining the Cooker properties
	protected final String name;
	protected final ActorRef printer;
	protected String msg;

	/**
	 * Cooker constructor
	 * 
	 * @param name
	 * 			Name of the cooker actor
	 * @param printer
	 * 			Actor in charge of printing
	 */
	public Cooker(String name, ActorRef printer) {
		this.name = name;
		this.printer = printer;
		this.msg = "";
	}

	/**
	 * Initialize the properties of the cooker
	 * 
	 * @param name
	 * 			Name of the actor
	 * @param printer
	 * 				Actor in charge of printing message
	 * @return Props
	 */
	static public Props props(String name, ActorRef printer) {
		return Props.create(Cooker.class, () -> new Cooker(name, printer));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(WhoToTell.class, wtg -> {
			if (!wtg.ready) {
					System.out.println(">The cooker " + this.name + " received the command " + wtg.who);
					System.out.println("The number of cooker available is now: " + wtg.free_cookers.size());
					TimeUnit.SECONDS.sleep(30);
					System.out.println(">>The cooker " + this.name + " has finished to prepare the command");
					wtg.free_cookers.add(getSelf());
					System.out.println("The number of cooker available is now: " + wtg.free_cookers.size());
					wtg.ready = true;
					wtg.chef.tell(new Cooker.WhoToTell(wtg.who, wtg.free_cookers, wtg.ready, wtg.chef, wtg.commands),
							ActorRef.noSender());
			} else {
				System.out.println("**The Chief cooker " + this.name + " has received the command " + wtg.who
						+ " It can be served to the client");
				if (wtg.commands.size() != 0) {
					String command = wtg.commands.remove(0);
					ActorRef cooker = wtg.free_cookers.get(0);
					wtg.free_cookers.remove(0);
					cooker.tell(new Cooker.WhoToTell(command , wtg.free_cookers, false, wtg.chef, wtg.commands),
							ActorRef.noSender());
				}

			}

		}).match(ToTell.class, wtg -> {
			if (wtg.free_cookers.size() != 0) {
				System.out.println(
						"*The Chief cooker " + this.name + " has received the command " + wtg.who + " from the client");
				ActorRef cooker = wtg.free_cookers.get(0);
				wtg.free_cookers.remove(0);
				cooker.tell(new Cooker.WhoToTell(wtg.who, wtg.free_cookers, false, wtg.chef, wtg.commands),
						ActorRef.noSender());
			} else {
				wtg.commands.add(wtg.who);
				System.out.println("!! The Chief cooker " + this.name + " has received the command " + wtg.who
						+ " But no cooker available");
			}
		}).match(Tell.class, x -> {
			printer.tell(new Greeting(msg), getSelf());
		}).build();
	};

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

	/**
	 * Class sending message
	 */
	static public class Tell {
		public Tell() {
		}
	}

	static public class ToTell {
		public String who;
		public ArrayList<ActorRef> free_cookers;
		public Boolean ready;
		public ActorRef chef;
		public ArrayList<String> commands;

		/**
		 * @param who
		 * 		Message to send
		 * @param free_cookers
		 * 		List of cookers available
		 * @param ready
		 * 		Boolean to specify if the command is ready
		 * @param chef
		 * 		Actor (chief cooker)
		 * @param commands
		 * 		List of commands waiting for cooker to be available
		 */
		public ToTell(String who, ArrayList<ActorRef> free_cookers, Boolean ready, ActorRef chef,
				ArrayList<String> commands) {
			this.who = who;
			this.free_cookers = free_cookers;
			this.ready = ready;
			this.chef = chef;
			this.commands = commands;
		}
	}

	static public class WhoToTell {
		public String who;
		public ArrayList<ActorRef> free_cookers;
		public Boolean ready;
		public ActorRef chef;
		public ArrayList<String> commands;

		/**
		 * WhoToTell constructor
		 * 
		 * @param who
		 * 		Message to send
		 * @param free_cookers
		 * 		List of cookers available
		 * @param ready
		 * 		Boolean to specify if the command is ready
		 * @param chef
		 * 		Actor (chief cooker)
		 * @param commands
		 * 		List of commands waiting for cooker to be available
		 */
		public WhoToTell(String who, ArrayList<ActorRef> free_cookers, Boolean ready, ActorRef chef,
				ArrayList<String> commands) {
			this.who = who;
			this.free_cookers = free_cookers;
			this.ready = ready;
			this.chef = chef;
			this.commands = commands;
		}
	}
}
