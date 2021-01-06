package kniffel;

import kniffel.data.ScoreTable;
import kniffel.data.ScoreTableRows;
import kniffel.gamelogic.GameState;
import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;

import java.io.IOException;
import java.util.Observer;

public interface KniffelFacade {

    int SCORE_TABLE_DIM = ScoreTableRows.SCORE_TABLE_DIM;

    /**
     * Rolls all dice that have not been moved aside and notifies the other players of the results
     */
    public void rollDice() throws IllegalStateException, IOException;

    /**
     * Ends your turn, enters your score into the score table and notifies the other players
     * @param tableRow The row in the score table you want to save your score to
     */
    public void endTurn(int tableRow) throws IllegalStateException, KniffelException, IOException;

    /**
     * Swaps a dice between the two states "not moved aside" and "moved aside" and notifies the other players of the change
     * @param diceIndex int 0 - 4, number of the dice you want to swap states of
     * @throws IllegalStateException if it's a different player's turn or the game has already ended
     */
    public void changeDiceState(int diceIndex) throws IllegalStateException, IOException;

    /**
     * End the game and notify the other players that the game has been suspended
     */
    public void endGame() throws IOException;

    /**
     * Returns the current ScoreTable
     * @return The current ScoreTable of the game
     */
    public ScoreTable getScoreTable();

    /**
     * Reads the score of a specific cell in the scoreTable
     * @param scoreTableRow Row that you want to read the score from
     * @param playerID ID of the player you want to get the score of
     * @return score of the specified player in the specified row
     * @throws KniffelException when scoreTableRow or playerID is invalid
     */
    public int getScore(int scoreTableRow, int playerID) throws KniffelException;

    /**
     * Returns the name of the specified scoreTableRow
     * @param scoreTableRow ID of the row you want the name of
     * @return Name of the scoreTableRow
     * @throws KniffelException
     */
    public String getScoreTableRowName(int scoreTableRow) throws KniffelException;

    /**
     * Returns the current values of the 5 dice in an array
     * @return values of each dice
     */
    public int[] getDiceValues();

    /**
     * Returns the states of each dice in an array
     * @return true = dice is set aside | false = dice is not set aside
     */
    public boolean[] areDicesSetAside();

    /**
     * Returns the ID of the active player
     * @return ID of the active player
     */
    public int getActivePlayer();

    /**
     * Returns an array with the names of all participating players in order by player ID
     * @return names of all players
     */
    public String[] getPlayerNames();

    /**
     * Returns the own player ID
     * @return own player ID
     */
    public int getOwnPlayerID();

    /**
     * Returns the number of rolls remaining for the current turn
     * @return number of rolls remaining
     */
    public int getRollsRemaining();

    /**
     * Returns the number of players participating in the game
     * @return number of players
     */
    public int getNumberOfPlayers();

    /**
     * Gets the current gameState
     * @return current gameState
     */
    public GameState getState();

    /**
     * Adds an observer to the facade that is notified each time another player makes a move
     * @param observer
     */
    public void addObserver(Observer observer);

}
