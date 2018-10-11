package server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import client.IClient;

public interface IMasterMindServer extends Remote {
	
	public static int MAX_PLAYERS = 2; //Max players
	public static int MAX_DIGIT = 9; //numbers from 1 to 9
	
	public static int BOARD_WIDTH = 4; 
	public static int ERROR_USER_NAME_EXISTS = -1;
	
	/**
	 * creates a new game on the server. The given client is memorized as the game initiator and automatically joins the game.
	 * @param the client to initially join the new game
	 * @return the ID of the newly created game
	 */
	public int createNewGame(IClient aClient) throws RemoteException;
	
	/**
	 * starts the game. from that point, joining the game is impossible.
	 * @param aGameID the identifier of the game.
	 * @return flag indicating the start success
	 */
	public boolean startGame(int aGameID) throws RemoteException;
	
	/**
	 * checks the numbers provided by a client against the hidden solution (sequence of digits)
	 * @param aGameID the identifier of the game.
	 * @param aClient the client
	 * @param aGuessedDigits the digits guessed by the client in order of array indices
	 * @return the number of exact matches and the number of digits appearing
	 *         anywhere in the solution as an integer-array of length 2
	 */
	public int[] checkNumbers(int aGameID, IClient aClient, int[] aGuessedDigits) throws RemoteException;
	
	/**
	 * deletes a game from the server. must be issued by the creating client if the game is deleted before its end.
	 * @param aGameID the identifier of the game
	 * @param aCreatingClient the client having created the game
	 */
	public void deleteGame(int aGameID, IClient aCreatingClient) throws RemoteException; 

	/**
	 * provides all games currently created on the server, running and open
	 * @return the games created on the server
	 * @throws RemoteException
	 */
	public List<IMasterMindGame> getCurrentGames() throws RemoteException;
		
	/**
	 * try to join the game with ID "gameID"
	 * @param aGameID the id of the game to join
	 * @param aClient the client wishing to join
	 * @return true iff the game was joined successfully, i.e. it is not running and MAX_PLAYERS is not exceeded by the join 
	 * @throws RemoteException
	 */
	public boolean joinGame(int aGameID, IClient aClient) throws RemoteException; //joins a game with given gameid
	
	/**
	 * disconnects the client, i.e. expels it from a joined game
	 * @param aClient the client to disconnect
	 * @throws RemoteException
	 */
	public void disconnect(IClient aClient) throws RemoteException;
}
