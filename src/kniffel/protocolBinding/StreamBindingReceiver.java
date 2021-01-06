package kniffel.protocolBinding;

import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;
import kniffel.gamelogic.KniffelReceiver;

import java.io.DataInputStream;
import java.io.IOException;

public class StreamBindingReceiver extends Thread {

    private DataInputStream dis;
    private KniffelReceiver receiver;
    private static final int NUMBER_OF_DICE = 5;

    public StreamBindingReceiver(DataInputStream dis, KniffelReceiver receiver) {
        this.dis = dis;
        this.receiver = receiver;
    }

    public void readRollDice() throws IOException, IllegalStateException {
        int[] newValues = new int[NUMBER_OF_DICE];
        int playerID;
        for(int i = 0; i < NUMBER_OF_DICE; i++) {
            newValues[i] = dis.readInt();
        }
        playerID = dis.readInt();

        receiver.receiveRollDice(newValues, playerID);
    }

    public void readEndTurn() throws IOException, IllegalStateException, KniffelException {
        int scoreTableRow = dis.readInt();
        int score = dis.readInt();
        int playerID = dis.readInt();

        receiver.receiveEndTurn(scoreTableRow, score, playerID);
    }

    public void readChangeDiceState() throws IOException, IllegalStateException {
        int diceIndex = dis.readInt();
        int playerID = dis.readInt();

        receiver.receiveChangeDiceState(diceIndex, playerID);
    }

    public void readSaveGame() throws IOException {
        int playerID = dis.readInt();

        receiver.receiveEndGame(playerID);
    }

    @Override
    public void run() {

        boolean again = true;

        while(again) {

            try {
                int command = this.dis.readInt();

                switch(command) {
                    case Commands.ROLL_DICE:
                        this.readRollDice();
                        break;
                    case Commands.END_TURN:
                        this.readEndTurn();
                        break;
                    case Commands.CHANGE_DICE_STATE:
                        this.readChangeDiceState();
                        break;
                    case Commands.END_GAME:
                        this.readSaveGame();
                        break;
                    default:
                        again = false;
                        System.err.println("Unknown command received: "+ command);

                }
            } catch (IOException e) {
                System.err.println("IOException: "+e.getLocalizedMessage());
                again = false;
            } catch (IllegalStateException e) {
                System.err.println("IllegalStateException: "+e.getLocalizedMessage());
                again = false;
            } catch (KniffelException e) {
                System.err.println("KniffelException: "+e.getLocalizedMessage());
                again = false;
            }
        }
    }
}
