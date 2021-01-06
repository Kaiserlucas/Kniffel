package kniffel.protocolBinding;

import dummyImplementations.KniffelReceiverDummy;
import kniffel.data.ScoreTableRows;
import org.junit.Test;

import java.io.*;

public class StreamBindingReceiverTests {

    @Test
    public void receiveDiceRollTest() throws InterruptedException, IOException {
        //Create an InputStream with the arguments needed for the command
        int playerID = 1;
        int[] exampleDiceValues = new int[] {1, 2, 3, 4, 5};

        //Create ByteArrayOutputStream with the arguments required for the command to pass to the DataInputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(Commands.ROLL_DICE);
        for(int diceValue : exampleDiceValues) {
            dos.writeInt(diceValue);
        }
        dos.writeInt(playerID);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        DataInputStream dis = new DataInputStream(is);

        //Set up receiver
        KniffelReceiverDummy engine = new KniffelReceiverDummy();
        StreamBindingReceiver receiver = new StreamBindingReceiver(dis, engine);

        receiver.start();
        receiver.join();

        assert(engine.isRollDiceReceived());

    }

    @Test
    public void receiveEndTurnTest() throws InterruptedException, IOException {
        //Create an InputStream with the arguments needed for the command
        int playerID = 1;
        int score = 20;

        //Create ByteArrayOutputStream with the arguments required for the command to pass to the DataInputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(Commands.END_TURN);
        dos.writeInt(ScoreTableRows.CHANCE);
        dos.writeInt(score);
        dos.writeInt(playerID);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        DataInputStream dis = new DataInputStream(is);

        //Set up receiver
        KniffelReceiverDummy engine = new KniffelReceiverDummy();
        StreamBindingReceiver receiver = new StreamBindingReceiver(dis, engine);

        receiver.start();
        receiver.join();

        assert(engine.isEndTurnReceived());
    }

    @Test
    public void receiveChangeDiceStateTest() throws InterruptedException, IOException {
        //Create an InputStream with the arguments needed for the command
        int playerID = 1;
        int diceIndex = 1;

        //Create ByteArrayOutputStream with the arguments required for the command to pass to the DataInputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(Commands.CHANGE_DICE_STATE);
        dos.writeInt(diceIndex);
        dos.writeInt(playerID);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        DataInputStream dis = new DataInputStream(is);

        //Set up receiver
        KniffelReceiverDummy engine = new KniffelReceiverDummy();
        StreamBindingReceiver receiver = new StreamBindingReceiver(dis, engine);

        receiver.start();
        receiver.join();

        assert(engine.isChangeDiceStateReceived());
    }

    @Test
    public void receiveEndGameTest() throws InterruptedException, IOException {
        //Create an InputStream with the arguments needed for the command
        int playerID = 1;

        //Create ByteArrayOutputStream with the arguments required for the command to pass to the DataInputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        dos.writeInt(Commands.END_GAME);
        dos.writeInt(playerID);

        InputStream is = new ByteArrayInputStream(os.toByteArray());
        DataInputStream dis = new DataInputStream(is);

        //Set up receiver
        KniffelReceiverDummy engine = new KniffelReceiverDummy();
        StreamBindingReceiver receiver = new StreamBindingReceiver(dis, engine);

        receiver.start();
        receiver.join();

        assert(engine.isSaveGameReceived());
    }
}
