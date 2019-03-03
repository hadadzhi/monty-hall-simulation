package ru.cdfe.deal.game;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import ru.cdfe.deal.game.exceptions.GameDoesNotExistException;

/**
 * Thread-safe in-memory {@link GameRepository}
 */
public final class InMemoryGameRepository implements GameRepository {
	private final ConcurrentMap<Integer, Game> games = new ConcurrentHashMap<>();
	private final AtomicInteger lastGameId = new AtomicInteger(0);
	
	@Override
	public Collection<Game> findAll() {
		return games.values();
	}

	@Override
	public Game findOne(Integer id) {
		Game game = games.get(id);
		
		if (game != null) {
			return game;	
		} else {
			throw new GameDoesNotExistException("Game " + id + " does not exist!");
		}
	}

	@Override
	public Game create() {
		Game game = new Game(lastGameId.getAndIncrement());

		games.put(game.getId(), game);

		return game;
	}

	@Override
	public void delete(Integer id) {
		Game game = games.remove(id);
		
		if (game == null) {
			throw new GameDoesNotExistException("Game " + id + " does not exist!");
		}
	}
}
