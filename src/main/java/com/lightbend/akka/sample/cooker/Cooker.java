package com.lightbend.akka.sample.cooker;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.lightbend.akka.sample.Printer.Greeting;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * @author Franco Davy Irakoze & Jérémy Vienne
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
	 * @param printer
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
	 * @param printer
	 * @return Props
	 */
	static public Props props(String name, ActorRef printer) {
		return Props.create(Cooker.class, () -> new Cooker(name, printer));
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(WhoToTell.class, wtg -> {
			if (!wtg.ready) {
					System.out.println("the cooker " + this.name + " received the command " + wtg.who);
					TimeUnit.SECONDS.sleep(30);
					System.out.println("the cooker " + this.name + " has finished to prepare the command");
					wtg.free_cookers.add(getSelf());
					wtg.ready = true;
					wtg.chef.tell(new Cooker.WhoToTell(wtg.who, wtg.free_cookers, wtg.ready, wtg.chef, wtg.commands),
							ActorRef.noSender());
			} else {
				System.out.println("The Chef cooker " + this.name + " has received the command " + wtg.who
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
						"the Chef cooker " + this.name + " has received the command " + wtg.who + " from the client");
				ActorRef cooker = wtg.free_cookers.get(0);
				wtg.free_cookers.remove(0);
				cooker.tell(new Cooker.WhoToTell(wtg.who, wtg.free_cookers, false, wtg.chef, wtg.commands),
						ActorRef.noSender());
			} else {
				wtg.commands.add(wtg.who);
				System.out.println("The Chef cooker " + this.name + " has received the command " + wtg.who
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
		 * WhoToTell constructor
		 * 
		 * @param who
		 * @param ready2
		 * @param free_cookers2
		 * @param cookers
		 * @param free_cookers
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
		 * @param ready2
		 * @param free_cookers2
		 * @param cookers
		 * @param free_cookers
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
