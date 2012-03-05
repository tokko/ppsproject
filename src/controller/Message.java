package controller;

public class Message {

	private final char type;
	private final int elevator;
	private final int modifier;
	private final double floor;

	public Message(char type, int elevator, int modifier, double floor) {
		this.type = type;
		this.elevator = elevator;
		this.modifier = modifier;
		this.floor = floor;
	}

	public char getType() {
		return type;
	}

	public int getElevator() {
		return elevator;
	}

	public double getModifier() {
		return modifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Message) {
			Message m = (Message) obj;
			return type == m.type && elevator == m.elevator
					&& modifier == m.modifier;
		}
		return false;
	}

	@Override
	public String toString() {
		if(type == 'f')
			return type + " " + elevator + " " + floor;
		else 
			return type + " " + elevator + " " + modifier;
	}

	public double getFloor() {
		return floor;
	}
}
