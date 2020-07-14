package com.jge.server.space.game.action;

import com.jge.server.net.Task;

public class ActionBasedGameTickTask implements Task {
	private long serverTime;

	private long lastTime;
	
	private ActionBasedGame actionBasedGame;
	
	public ActionBasedGameTickTask(ActionBasedGame actionBasedGame) {
		this.actionBasedGame = actionBasedGame;
		lastTime = System.currentTimeMillis();
	}

	public void run() {
		long currTime = System.currentTimeMillis();
		long delta = currTime - lastTime;
		serverTime += delta;
		actionBasedGame.onDeltaTime(delta, serverTime);
		lastTime = currTime;
	}
}
