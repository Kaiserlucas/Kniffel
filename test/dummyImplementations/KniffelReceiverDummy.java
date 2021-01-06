package dummyImplementations;

import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;
import kniffel.gamelogic.KniffelReceiver;

import java.io.IOException;

public class KniffelReceiverDummy implements KniffelReceiver {

    private boolean rollDiceReceived = false;
    private boolean endTurnReceived = false;
    private boolean changeDiceStateReceived = false;
    private boolean saveGameReceived = false;


    public boolean isRollDiceReceived() {
        return rollDiceReceived;
    }

    public boolean isEndTurnReceived() {
        return endTurnReceived;
    }

    public boolean isChangeDiceStateReceived() {
        return changeDiceStateReceived;
    }

    public boolean isSaveGameReceived() {
        return saveGameReceived;
    }


    @Override
    public void receiveRollDice(int[] newValues, int playerID) throws IllegalStateException, IOException {
        rollDiceReceived = true;
    }

    @Override
    public void receiveEndTurn(int scoreTableRow, int score, int playerID) throws IllegalStateException, KniffelException, IOException {
        endTurnReceived = true;
    }

    @Override
    public void receiveChangeDiceState(int diceIndex, int playerID) throws IllegalStateException, IOException {
        changeDiceStateReceived = true;
    }

    @Override
    public void receiveEndGame(int playerID) throws IOException {
        saveGameReceived = true;
    }


}
