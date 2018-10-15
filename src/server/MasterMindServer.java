package server;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import client.IClient;

public class MasterMindServer extends UnicastRemoteObject implements IMasterMindServer
{

	private static Registry reg;
	public List<IMasterMindGame> mActiveGames;
	private static final long serialVersionUID = 1l;

	public MasterMindServer() throws RemoteException
	{
		super();
		mActiveGames = new ArrayList<IMasterMindGame>();
	}

	public static void main(String[] args)
	{
		try
		{
			reg = LocateRegistry.createRegistry(1099);
			reg.rebind(IMasterMindServer.class.getName(), new MasterMindServer());
			System.out.println("MasterMindServer successfully bound to Registry");
//            reg.rebind(IMasterMindGame.class.getName(), new MasterMindGame());
//            System.out.println("MasterMindGame successfully bound to Registry");
		} catch (RemoteException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * @return the ID of the game
	 * @param the name of the player to initially join the new game
	 * 
	 */
	public int createNewGame(IClient aClient) throws RemoteException
	{
		MasterMindGame myGame = new MasterMindGame(aClient);
		myGame.addClient(aClient);
		mActiveGames.add(myGame);
		System.out.println("Game " + myGame.getGameID() + " created by " + aClient.getUsername());
//		reg.rebind(IMasterMindGame.class.getName() + myGame.getGameID(), (IMasterMindGame) myGame);
//		System.out.println("MasterMindGame ID: " + myGame.getGameID() + " successfully bound to Registry");
		return myGame.getGameID();
	}

	/**
	 * checks the numbers provided by a player against the hidden solution (sequence
	 * of digits)
	 * 
	 * @param aUserName      identifier of the player
	 * @param aGuessedDigits the digits guessed by the player in order of array
	 *                       indices
	 * @return the number of exact matches and the number of digits appearing
	 *         anywhere in the solution as an integer-array of length 2
	 */
	public int[] checkNumbers(int gameID, IClient aClient, int[] aGuessedDigits) throws RemoteException
	{

		int[] result =
		{ 0, 0 };

		for (IMasterMindGame mmg : mActiveGames)
		{
			if (mmg.getGameID() == gameID)
			{ // find the game
				if (!mmg.getClients().contains(aClient))
				{
					System.out.println("oh je :/");
				}
				return mmg.checkNumbers(aClient, aGuessedDigits); // Check the guessed digits
			}
		}
		return result;
	}

	@Override
	public List<IMasterMindGame> getCurrentGames() throws RemoteException
	{
		return mActiveGames;
	}

	@Override
	public boolean joinGame(int gameID, IClient aClient) throws RemoteException
	{
		for (IMasterMindGame mmg : mActiveGames)
		{ // search game
			if (mmg.getGameID() == gameID)
			{ // game found
				if (mmg.isGameRunning())
				{
					return false;
				}

				if (mmg.getClients().contains(aClient))
				{ // has the client already joined that game?
					return false;
				}
				mmg.addClient(aClient); // add client to game
				System.out.println("Client '" + aClient.getUsername() + "' joined game " + gameID);
				return true;
			}
		}
		return false;
	}

	@Override
	public void deleteGame(int aGameID, IClient aCreatingClient) throws RemoteException
	{
		IMasterMindGame vGame = null;
		Iterator<IMasterMindGame> vGameIterator = mActiveGames.iterator();
		// for each loop will cause concurrent modification exception when deleting
		for (; vGameIterator.hasNext();)
		{
			vGame = vGameIterator.next();
			if (vGame.getGameID() == aGameID)
			{
				/**
				 * using Objects.equals instead of comparing the references works fine.
				 * in the debugging I saw two different Proxy Id's
				 * in vGame.getCreatingClient() and aCreatingClient even though it's all
				 * the same client instance.
				 */
//				if (vGame.getCreatingClient() == aCreatingClient)
				if (Objects.equals(vGame.getCreatingClient(), aCreatingClient))
				{
					vGameIterator.remove();
				} else
				{
					throw new RuntimeException("Client not creator of the game!");
				}
			}
		}
	}

	@Override
	public void disconnect(IClient aClient) throws RemoteException
	{
		for (IMasterMindGame mmg : mActiveGames)
		{
			if (mmg.getClients().contains(aClient))
			{
				mmg.removeClient(aClient);
			}
		}

	}

	@Override
	public boolean startGame(int gameID) throws RemoteException
	{
		for (IMasterMindGame mmg : mActiveGames)
		{
			if (mmg.getGameID() == gameID)
			{
				return mmg.startGame();
			}
		}
		return false;
	}

}
