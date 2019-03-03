package ru.cdfe.deal.game.exceptions;

@SuppressWarnings("serial")
public class GameDoesNotExistException extends RuntimeException {
	public GameDoesNotExistException(String message) {
		super(message);
	}
}
