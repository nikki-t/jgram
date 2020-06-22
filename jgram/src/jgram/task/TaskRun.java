package jgram.task;

import java.nio.file.Path;

import jgram.security.Secret;

public abstract class TaskRun implements Runnable {
	
	// Class variable(s)
	private static Secret secret;
	
	// Instance variable(s)
	private Path path;
	
	public Secret getSecret() {
		return secret;
	}
	
	public Path getPath() {
		return path;
	}

	public TaskRun(Path inputPath) {
		path = inputPath;
	}
	
	public TaskRun(Path inputPath, Secret inputSecret) {
		path = inputPath;
		secret = inputSecret;
	}
	
	/**
	 * Intent: Runnable interface method run which is invoked by the start 
	 * method of Thread.
	 */
	abstract public void run();

}
