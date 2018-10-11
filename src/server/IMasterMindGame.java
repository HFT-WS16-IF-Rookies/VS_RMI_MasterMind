package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.IClient;

/**
 *
 */
public interface IMasterMindGame extends Remote
{

	public List<IClient> getClients() throws RemoteException;

	public void addClient(IClient aClient) throws RemoteException;

	public void removeClient(IClient aClient) throws RemoteException;

	public IClient getCreatingClient() throws RemoteException;

	public int[] checkNumbers(IClient aClient, int[] aGuessedDigits) throws RemoteException;

	public int getGameID() throws RemoteException;

	public boolean startGame() throws RemoteException;

	public boolean isGameRunning() throws RemoteException;
}
