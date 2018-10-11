package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

import de.hft_stuttgart.rmi.IDateProvider;
import server.IMasterMindGame;
import server.IMasterMindServer;
import server.MasterMindServer;

public class Client implements IClient {

	public boolean playGame = false;
	private boolean endGame = false;
	
	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return "Erik";
	}

	@Override
	public void playGame() {
		playGame = true;
	}

	@Override
	public void endGame(String message) {
		endGame  = true;
	}


	public static void main(String[] args) {
		
		
        try
		{
        	Registry vRegistry = LocateRegistry.getRegistry();
			IClient vDateProvider = (IClient) vRegistry.lookup(IClient.class.getName());
		} catch (RemoteException | NotBoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Scanner in = new Scanner(System.in);
		//locally: create a server
		MasterMindServer server = new MasterMindServer();
		Client client = new Client();
		//while the user wishes
			//create a game
			System.out.println("creating game");
			int gameID = server.createNewGame(client);
			
			//start the game
			server.startGame(gameID);
			
			int[] result;
			do
			{
				while(!client.playGame);
				System.out.println("Rate " + IMasterMindServer.BOARD_WIDTH + " Zahlen von 1 bis " + IMasterMindServer.MAX_DIGIT);
				int[] guess = new int[IMasterMindServer.BOARD_WIDTH];
				
					while(true)
					{
						String input;
						try
						{
							input = in.nextLine();
							String[] split = input.split(" ");
							if (split.length != IMasterMindServer.BOARD_WIDTH)
								throw new InputMismatchException();
							
							for (int i=0; i < split.length; i++)
								{
									guess[i] = Integer.valueOf(split[i]);
									if (guess[i] < 1 || guess[i] > IMasterMindServer.MAX_DIGIT)
										throw new InputMismatchException();
								}
							
							
							break;
						}
						catch (InputMismatchException e)
						{
							System.out.println("nicht valide Eingabe! nochmal:");
						}
						catch (NumberFormatException e)
						{
							System.out.println("nur zahlen erlaubt Huatsempel!");
						}
					}
				
				result = server.checkNumbers(gameID, client, guess);
				System.out.println(result[0] + " Zahlen richtig geraten, " + result[1] + " falsch platziert");
			}
			while(!client.endGame && result[0] != IMasterMindServer.BOARD_WIDTH);
		//ask for a possible next game
			in.close();
	}

}
