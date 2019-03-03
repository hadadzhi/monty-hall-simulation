package ru.cdfe.deal.game.exceptions;

@SuppressWarnings("serial")
public class IllegalGameStateException extends RuntimeException {
	public IllegalGameStateException(String message) {
		super(message);
	}
}
