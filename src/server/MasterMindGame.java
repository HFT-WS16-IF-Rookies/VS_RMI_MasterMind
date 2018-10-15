package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import client.IClient;

public class MasterMindGame extends UnicastRemoteObject implements IMasterMindGame
{

	private static final long serialVersionUID = 1l;

	private int mGameID;
	private static int mLastUsedGameID = 1;
	private boolean mGameRunning;

	private int[] mSecretNumber;

	private IClient mCreatingClient;
	private List<IClient> mClients;

	public MasterMindGame(IClient aCreatingClient) throws RemoteException
	{
		super();
		this.mCreatingClient = aCreatingClient;
		mGameID = mLastUsedGameID++;
		mClients = new ArrayList<IClient>();

		generateSecretNumber();
	}

	private void generateSecretNumber()
	{
		mSecretNumber = new int[IMasterMindServer.BOARD_WIDTH];

		boolean[] vNumberInUse = new boolean[IMasterMindServer.MAX_DIGIT + 1];
		for (int i = 0; i < vNumberInUse.length; i++)
		{
			vNumberInUse[i] = false;
		}
		System.out.print("Secret number: ");
		for (int i = 0; i < mSecretNumber.length; i++)
		{
			int vCurrentNumber;
			do
			{
				vCurrentNumber = getRandomNumber();
			} while (vNumberInUse[vCurrentNumber]);
			mSecretNumber[i] = vCurrentNumber;
			vNumberInUse[vCurrentNumber] = true;
			System.out.print(vCurrentNumber);
		}
		System.out.println();
	}

	private int getRandomNumber()
	{
		Random rand = new Random();
		return 1 + rand.nextInt(IMasterMindServer.MAX_DIGIT);
	}

	/**
	 * checks the numbers provided by a player against the hidden solution (sequence
	 * of digits)
	 * 
	 * @param aClient        the guessing client
	 * @param aGuessedDigits the digits guessed by the client in order of array
	 *                       indices
	 * @return the number of exact matches and the number of digits appearing
	 *         anywhere in the solution as an integer-array of length 2
	 */
	public int[] checkNumbers(IClient aClient, int[] aGuessedDigits) throws RemoteException
	{
		int vExactMatches = 0;
		int vAnywhereMatches = 0;

		for (int i = 0; i < mSecretNumber.length; i++)
		{
			if (mSecretNumber[i] == aGuessedDigits[i])
			{
				vExactMatches++;
			} else
			{
				for (int a : aGuessedDigits)
				{
					if (a == mSecretNumber[i])
					{
						vAnywhereMatches++;
					}
				}
			}
		}

		if (vExactMatches == IMasterMindServer.BOARD_WIDTH)
		{
			for (IClient vClient : mClients)
			{
				vClient.endGame(aClient.getUsername() + " won the game " + mGameID);
			}
		}

		int[] result = new int[]
		{ vExactMatches, vAnywhereMatches };
		return result;
	}

	public int getGameID() throws RemoteException
	{
		return mGameID;
	}

	@Override
	public List<IClient> getClients() throws RemoteException
	{
		return mClients;
	}

	@Override
	public void addClient(IClient aClient) throws RemoteException
	{
		mClients.add(aClient);
	}

	@Override
	public void removeClient(IClient aClient) throws RemoteException
	{
		mClients.remove(aClient);
	}

	// Notifies the clients that the game has started
	@Override
	public boolean startGame() throws RemoteException
	{
		boolean vAllClientsReady = true;
		for (IClient vCurrentClient : mClients)
		{
			// ignore the creating client
			if (vCurrentClient != mCreatingClient && !vCurrentClient.isReady())
			{
				vAllClientsReady = false;
				return false;
			}
		}

		if (vAllClientsReady)
		{
			for (IClient vCurrentClient : mClients)
			{
				vCurrentClient.playGame();
			}
			mGameRunning = true;
			return true;
		} else
		{
			return false;
		}
	}

	public boolean isGameRunning() throws RemoteException
	{
		return mGameRunning;
	}

	@Override
	public IClient getCreatingClient() throws RemoteException
	{
		return mCreatingClient;
	}

	public void setCreatingClient(IClient aCreatingClient) throws RemoteException
	{
		mCreatingClient = aCreatingClient;
	}

}
