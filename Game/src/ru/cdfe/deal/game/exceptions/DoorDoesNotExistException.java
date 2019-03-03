package ru.cdfe.deal.game.exceptions;

@SuppressWarnings("serial")
public class DoorDoesNotExistException extends RuntimeException {
	public DoorDoesNotExistException(String message) {
		super(message);
	}
}
