/**
 * 
 */
package client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClient extends Remote
{

	public boolean isReady() throws RemoteException; // return if the client is ready

	public String getUsername() throws RemoteException; // returns the username of the client

	public void playGame() throws RemoteException; // tells the client that the multiplayer-game starts

	public void endGame(String message) throws RemoteException; // tells the client that the multiplayer-game ended

}
