package dummyImplementations;

import kniffel.gamelogic.*;
import kniffel.gamelogic.IllegalStateException;
import kniffel.protocolBinding.KniffelSender;

import java.io.IOException;

public class StreamBindingSenderDummy implements KniffelSender {

    private KniffelReceiver[] receivers;
    private boolean isHost;

    public StreamBindingSenderDummy() {
        receivers = new KniffelReceiver[] {new KniffelReceiverDummy()};
    }

    public void setReceivers(KniffelReceiver[] receivers, boolean isHost) {
        this.receivers = receivers;
        this.isHost = isHost;
    }

    @Override
    public void sendRollDice(int[] newValues, int playerID) throws IOException, IllegalStateException {
        if(isHost) {
            for(int i = 0; i < receivers.length; i++) {
                if(i+2 != playerID) {
                    receivers[i].receiveRollDice(newValues, playerID);
                }
            }
        } else {
            receivers[0].receiveRollDice(newValues, playerID);
        }


    }

    @Override
    public void sendEndTurn(int scoreTableRow, int score, int playerID) throws IOException, IllegalStateException, KniffelException {
        if(isHost) {
            for(int i = 0; i < receivers.length; i++) {
                if(i+2 != playerID) {
                    receivers[i].receiveEndTurn(scoreTableRow, score, playerID);
                }
            }
        } else {
            receivers[0].receiveEndTurn(scoreTableRow, score, playerID);
        }
    }

    @Override
    public void sendChangeDiceState(int diceIndex, int playerID) throws IOException, IllegalStateException {
        if(isHost) {
            for(int i = 0; i < receivers.length; i++) {
                if(i+2 != playerID) {
                    receivers[i].receiveChangeDiceState(diceIndex, playerID);
                }
            }
        } else {
            receivers[0].receiveChangeDiceState(diceIndex, playerID);
        }
    }

    @Override
    public void sendEndGame(int playerID) throws IOException {
        if(isHost) {
            for(int i = 0; i < receivers.length; i++) {
                if(i+2 != playerID) {
                    receivers[i].receiveEndGame(playerID);
                }
            }
        } else {
            receivers[0].receiveEndGame(playerID);
        }
    }
}
