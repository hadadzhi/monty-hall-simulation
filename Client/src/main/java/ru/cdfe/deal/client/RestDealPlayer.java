package ru.cdfe.deal.client;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.client.RestTemplate;

import ru.cdfe.deal.game.DealPlayer;
import ru.cdfe.deal.game.DealStrategy;
import ru.cdfe.deal.game.DoorStatus;
import ru.cdfe.deal.game.GameStatus;

public final class RestDealPlayer implements DealPlayer {
	private final DealStrategy strategy;
	private final URI homeURI;
	private final RestTemplate rest;

	public RestDealPlayer(DealStrategy strategy, URI homeURI) {
		if (strategy == null || homeURI == null) {
			throw new NullPointerException();
		}

		this.strategy = strategy;
		this.homeURI = homeURI;
		
		this.rest = new RestTemplate();
	}

	@Override
	public boolean play() {
		URI gameURI = createGame();
		int initialSelection = ThreadLocalRandom.current().nextInt(3);

		selectDoor(gameURI, initialSelection);

		switch (strategy) {
			case DUMB: {
				selectDoor(gameURI, initialSelection);
			}
			case SMART: {
				for (int i = 0; i < 3; i++) {
					if (i != initialSelection && getDoorStatus(gameURI, i) == DoorStatus.CLOSED) {
						selectDoor(gameURI, i);
					}
				}
			}
		}

		boolean result = getGameStatus(gameURI) == GameStatus.WON; 
		
		deleteGame(gameURI);
		
		return result;
	}

	@Override
	public DealStrategy strategy() {
		return strategy;
	}

	@SuppressWarnings("rawtypes")
	private URI createGame() {
		Map home = rest.getForObject(homeURI, Map.class);
		Map games = rest.getForObject(getLinkHref(home, "games"), Map.class);

		return rest.postForLocation(getLinkHref(games, "create"), null);
	}

	private void selectDoor(URI gameURI, int doorID) {
		rest.postForLocation(getLinkHref(getDoor(gameURI, doorID), "select"), null);
	}

	@SuppressWarnings("rawtypes")
	private Map getDoor(URI gameURI, int doorID) {
		Map game = rest.getForObject(gameURI, Map.class);
		return rest.getForObject(getLinkHref(game, "doors", doorID), Map.class);
	}

	private DoorStatus getDoorStatus(URI gameURI, int doorID) {
		return Enum.valueOf(DoorStatus.class, (String) getDoor(gameURI, doorID).get("status"));
	}
	
	private GameStatus getGameStatus(URI gameURI) {
		return Enum.valueOf(GameStatus.class, (String) rest.getForObject(gameURI, Map.class).get("status"));
	}

	@SuppressWarnings("rawtypes")
	private String getLinkHref(Map res, String rel) {
		return (String) ((Map) (((Map) res.get("_links")).get(rel))).get("href");
	}

	@SuppressWarnings("rawtypes")
	private String getLinkHref(Map res, String rel, int index) {
		return (String) ((Map) ((List) (((Map) res.get("_links")).get(rel))).get(index)).get("href");
	}
	
	private void deleteGame(URI gameURI) {
		rest.delete(getLinkHref(rest.getForObject(gameURI, Map.class), "delete"));
	}
}
