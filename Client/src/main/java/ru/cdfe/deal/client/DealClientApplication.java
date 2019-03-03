package ru.cdfe.deal.client;

import java.net.URI;
import java.text.NumberFormat;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.cdfe.deal.game.DealPlayer;
import ru.cdfe.deal.game.DealStrategy;
import ru.cdfe.deal.game.InMemoryDealPlayer;

public class DealClientApplication {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final URI homeUri = URI.create("http://localhost:8080/deal");
	
	private static final int IN_MEMORY_RUNS = 10000000;
	private static final int REST_RUNS = 10000;
	
	public static void main(String[] args) {
		new DealClientApplication().runSimulations();
	}

	private void runSimulations() {
		runSimulation(new InMemoryDealPlayer(DealStrategy.DUMB), IN_MEMORY_RUNS);
		runSimulation(new InMemoryDealPlayer(DealStrategy.SMART), IN_MEMORY_RUNS);
		runSimulation(new RestDealPlayer(DealStrategy.DUMB, homeUri), REST_RUNS);
		runSimulation(new RestDealPlayer(DealStrategy.SMART, homeUri), REST_RUNS);
		
		log.info("Finished");
	}
	
	private void runSimulation(final DealPlayer player, final int runs) {
		final AtomicInteger wins = new AtomicInteger(0);
		final ForkJoinPool pool = new ForkJoinPool();
				
		log.info(String.format("Running simulation using %s", player.getClass().getSimpleName()));
		final long startms = System.currentTimeMillis();
		
		for (int i = 0; i < runs; i++) {
			pool.submit(() -> {
				try {
					if (player.play()) {
						wins.incrementAndGet();
					}					
				} catch (Exception e) {
					log.error(e.toString());
				}
			});
		}
		
		pool.shutdown();
		pool.awaitQuiescence(Long.MAX_VALUE, TimeUnit.DAYS);
		
		final long elapsedms = System.currentTimeMillis() - startms;
		log.info(String.format("Runs: %d, Time: %d ms, Strategy: %s, Win ratio: %s",
			runs, elapsedms, player.strategy(), NumberFormat.getInstance().format((double) wins.get() / runs)));
	}
}
