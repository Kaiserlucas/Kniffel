package kniffel.gamelogic;

import java.io.IOException;

public interface KniffelReceiver {

    /**
     * Receives roll results from another player and updates the engine accordingly
     * Additionally, sends the command to all other players aside from the one who matches the playerID if this device is the host
     * @param newValues Rolled dice values
     * @param playerID ID of the player who sent the command
     * @throws IllegalStateException if it's a different player's turn or the game has already ended
     */
    public void receiveRollDice(int[] newValues, int playerID) throws IllegalStateException, IOException;

    /**
     * Receives turn results from another player and updates the engine accordingly
     * Additionally, sends the command to all other players aside from the one who matches the playerID if this device is the host
     * @param scoreTableRow Row in the ScoreTable that the score is supposed to be written to
     * @param score The score that is supposed to be written to the ScoreTable
     * @param playerID ID of the player who sent the command
     * @throws IllegalStateException if it's a different player's turn or the game has already ended
     * @throws KniffelException if the score table row is invalid or was already written to this game
     */
    public void receiveEndTurn(int scoreTableRow, int score, int playerID) throws IllegalStateException, KniffelException, IOException;

    /**
     * Receives dice state changes from another player and updates the engine accordingly
     * Additionally, sends the command to all other players aside from the one who matches the playerID if this device is the host
     * @param diceIndex Index of the dice that is supposed to switch states.
     * @param playerID ID of the player who sent the command
     * @throws IllegalStateException if it's a different player's turn or the game has already ended
     */
    public void receiveChangeDiceState(int diceIndex, int playerID) throws IllegalStateException, IOException;

    /**
     * Receives a notification from another player that the game was saved and suspended. Ends the game
     * Additionally, sends the command to all other players if this device is the host
     * @param playerID ID of the player who sent the command
     */
    public void receiveEndGame(int playerID) throws IOException;


}
