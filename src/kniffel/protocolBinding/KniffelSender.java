package kniffel.protocolBinding;

import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;

import java.io.IOException;

public interface KniffelSender {

    /**
     * Sends roll results to other players
     * Format: {int command, int diceValues x5, int playerID}
     * Always sends command to all known players other than the one who matches the playerID argument
     * @param newValues Rolled dice values
     * @param playerID Own player ID
     */
    public void sendRollDice(int[] newValues, int playerID) throws IOException, IllegalStateException;

    /**
     * Sends turn results to other players
     * Format: {int command, int scoreTableRow, int score, int playerID}
     * Always sends command to all known players other than the one who matches the playerID argument
     * @param scoreTableRow Row in the ScoreTable that the score is supposed to be written to
     * @param score The score that is supposed to be written to the ScoreTable
     * @param playerID Own player ID
     */
    public void sendEndTurn(int scoreTableRow, int score, int playerID) throws IOException, IllegalStateException, KniffelException;

    /**
     * Sends dice state changes to other players
     * Format: {int command, int diceIndex , int playerID}
     * Always sends command to all known players other than the one who matches the playerID argument
     * @param diceIndex Index of the dice that is supposed to switch states
     * @param playerID Own player ID
     */
    public void sendChangeDiceState(int diceIndex, int playerID) throws IOException, IllegalStateException;

    /**
     * Sends a notification that the game was saved and suspended to other players. Ends the game
     * Format: {int command, int playerID}
     * @param playerID Own player ID
     */
    public void sendEndGame(int playerID) throws IOException;
}
