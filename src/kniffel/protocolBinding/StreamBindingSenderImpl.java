package kniffel.protocolBinding;

import java.io.DataOutputStream;
import java.io.IOException;

public class StreamBindingSenderImpl implements KniffelSender {

    private DataOutputStream[] dos;
    private boolean isHost;

    public StreamBindingSenderImpl(DataOutputStream[] dos, boolean isHost) {
        this.dos = dos;
        this.isHost = isHost;
    }

    @Override
    public void sendRollDice(int[] newValues, int playerID) throws IOException {
        if(isHost) {
            for(int i = 0; i < dos.length; i++) {
                if(i+2 != playerID) {
                    dos[i].writeInt(Commands.ROLL_DICE);
                    for(int diceValue : newValues) {
                        dos[i].writeInt(diceValue);
                    }
                    dos[i].writeInt(playerID);
                }
            }
        } else {
            dos[0].writeInt(Commands.ROLL_DICE);
            for(int diceValue : newValues) {
                dos[0].writeInt(diceValue);
            }
            dos[0].writeInt(playerID);
        }
    }

    @Override
    public void sendEndTurn(int scoreTableRow, int score, int playerID) throws IOException {
        if(isHost) {
            for(int i = 0; i < dos.length; i++) {
                if(i+2 != playerID) {
                    dos[i].writeInt(Commands.END_TURN);
                    dos[i].writeInt(scoreTableRow);
                    dos[i].writeInt(score);
                    dos[i].writeInt(playerID);
                }
            }
        } else {
            dos[0].writeInt(Commands.END_TURN);
            dos[0].writeInt(scoreTableRow);
            dos[0].writeInt(score);
            dos[0].writeInt(playerID);
        }
    }

    @Override
    public void sendChangeDiceState(int diceIndex, int playerID) throws IOException {
        if(isHost) {
            for(int i = 0; i < dos.length; i++) {
                if(i+2 != playerID) {
                    dos[i].writeInt(Commands.CHANGE_DICE_STATE);
                    dos[i].writeInt(diceIndex);
                    dos[i].writeInt(playerID);
                }
            }
        } else {
            dos[0].writeInt(Commands.CHANGE_DICE_STATE);
            dos[0].writeInt(diceIndex);
            dos[0].writeInt(playerID);
        }
    }

    @Override
    public void sendEndGame(int playerID) throws IOException {
        if(isHost) {
            for(int i = 0; i < dos.length; i++) {
                if(i+2 != playerID) {
                    dos[i].writeInt(Commands.END_GAME);
                    dos[i].writeInt(playerID);
                }
            }
        } else {
            dos[0].writeInt(Commands.END_GAME);
            dos[0].writeInt(playerID);
        }
    }
}
