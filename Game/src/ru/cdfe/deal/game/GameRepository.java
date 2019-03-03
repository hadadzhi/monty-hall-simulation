package ru.cdfe.deal.game;

import java.util.Collection;

public interface GameRepository {
	Collection<Game> findAll();
	
	Game findOne(Integer id);
	
	Game create();

	void delete(Integer id);
}
