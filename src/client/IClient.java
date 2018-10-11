/**
 * 
 */
package client;

public interface IClient{
	
	public boolean isReady(); //return if the client is ready
	
	public String getUsername(); //returns the username of the client
	
	public void playGame(); //tells the client that the multiplayer-game starts
	
	public void endGame(String message); //tells the client that the multiplayer-game ended

	

}
