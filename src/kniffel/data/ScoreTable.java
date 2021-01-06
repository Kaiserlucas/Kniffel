package kniffel.data;

import kniffel.gamelogic.KniffelException;

import java.io.Serializable;

public interface ScoreTable extends Serializable {

    /**
     * Sets a score in the ScoreTable
     * @param scoreTableRow row that the score should be written to
     * @param score score to set
     * @param playerID player for which to set the score
     */
    public void setScore(int scoreTableRow, int score, int playerID) throws KniffelException;

    /**
     * Gets a specific score from the ScoreTable
     * @param scoreTableRow row that the score should be read from
     * @param playerID player for which to read the score
     * @return retrieved score
     */
    public int getScore(int scoreTableRow, int playerID) throws KniffelException;

    /**
     * Returns the ID of the next player that is expected to set a score
     * @return ID of the next player | -1 if all turns are made and the game is over
     */
    public int getNextPlayer();

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
     * Returns the number of players
     * @return number of players
     */
    public int getNumberOfPlayers();
}
