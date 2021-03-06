package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;
import server.IMasterMindGame;
import server.IMasterMindServer;
import server.MasterMindServer;

public class Client extends UnicastRemoteObject implements IClient
{

	private static final long serialVersionUID = 1l;
	private static Registry reg;
	private static IMasterMindServer server;

	public Client() throws RemoteException
	{
		super();
	}

	public boolean playGame = false;
	private boolean endGame = false;

	@Override
	public boolean isReady() throws RemoteException
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getUsername() throws RemoteException
	{
		// TODO Auto-generated method stub
		return "Erik";
	}

	@Override
	public void playGame() throws RemoteException
	{
		playGame = true;
	}

	@Override
	public void endGame(String message) throws RemoteException
	{
		endGame = true;
	}

	public void run()
	{
		Scanner in = new Scanner(System.in);
		try
		{
			System.out.println("creating game");
			int gameID = server.createNewGame(this);

			// start the game
			server.startGame(gameID);

			int[] result;
			do
			{
				while (!playGame)
					;
				System.out.println(
						"Rate " + IMasterMindServer.BOARD_WIDTH + " Zahlen von 1 bis " + IMasterMindServer.MAX_DIGIT);
				int[] guess = new int[IMasterMindServer.BOARD_WIDTH];

				while (true)
				{
					String input;
					try
					{
						input = in.nextLine();
						String[] split = input.split(" ");
						if (split.length != IMasterMindServer.BOARD_WIDTH)
							throw new InputMismatchException();

						for (int i = 0; i < split.length; i++)
						{
							guess[i] = Integer.valueOf(split[i]);
							if (guess[i] < 1 || guess[i] > IMasterMindServer.MAX_DIGIT)
								throw new InputMismatchException();
						}

						break;
					} catch (InputMismatchException e)
					{
						System.out.println("nicht valide Eingabe! nochmal:");
					} catch (NumberFormatException e)
					{
						System.out.println("nur zahlen erlaubt Huatsempel!");
					}
				}

				result = server.checkNumbers(gameID, this, guess);
				System.out.println(result[0] + " Zahlen richtig geraten, " + result[1] + " falsch platziert");
			} while (!endGame && result[0] != IMasterMindServer.BOARD_WIDTH);
			server.deleteGame(gameID, this);
		} catch (RemoteException e)
		{
			e.printStackTrace();
		}
		// ask for a possible next game
		in.close();

	}

	public static void main(String[] args)
	{
		// locally: create a server
		try
		{
			reg = LocateRegistry.getRegistry();
			server = (IMasterMindServer) reg.lookup(IMasterMindServer.class.getName());
			IClient client = new Client();
			((Client) client).run();
		} catch (RemoteException | NotBoundException e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}

}
