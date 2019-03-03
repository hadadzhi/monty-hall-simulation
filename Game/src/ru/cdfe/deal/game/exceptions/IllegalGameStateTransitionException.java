package ru.cdfe.deal.game.exceptions;

@SuppressWarnings("serial")
public class IllegalGameStateTransitionException extends RuntimeException {
	public IllegalGameStateTransitionException(String message) {
		super(message);
	}
}
