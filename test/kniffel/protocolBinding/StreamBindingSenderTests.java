package kniffel.protocolBinding;

import kniffel.data.ScoreTableRows;
import kniffel.gamelogic.IllegalStateException;
import kniffel.gamelogic.KniffelException;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertArrayEquals;

public class StreamBindingSenderTests {

    @Test
    public void sendRollDiceTest() throws IOException, IllegalStateException {
        //Setting up streams
        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        ByteArrayOutputStream o3 = new ByteArrayOutputStream();

        DataOutputStream do1 = new DataOutputStream(o1);
        DataOutputStream do2 = new DataOutputStream(o2);
        DataOutputStream do3 = new DataOutputStream(o3);

        DataOutputStream[] dos = new DataOutputStream[] {do1, do2, do3};

        KniffelSender sender = new StreamBindingSenderImpl(dos, true);

        //Sends the command over the streams
        sender.sendRollDice(new int[] {1,2,3,4,5}, 2);

        //Creates a stream with the expected values
        ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        DataOutputStream expectedDos = new DataOutputStream(expectedBos);
        //Command ID
        expectedDos.writeInt(Commands.ROLL_DICE);
        //Dice Values
        expectedDos.writeInt(1);
        expectedDos.writeInt(2);
        expectedDos.writeInt(3);
        expectedDos.writeInt(4);
        expectedDos.writeInt(5);
        //Player ID
        expectedDos.writeInt(2);

        ByteArrayOutputStream emptyBos = new ByteArrayOutputStream();

        //Checks if the streams match. The first stream is supposed to be empty since it belongs to the same player the command came from
        assertArrayEquals(emptyBos.toByteArray(), o1.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o2.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o3.toByteArray());

    }

    @Test
    public void sendEndTurnTest() throws IOException, IllegalStateException, KniffelException {
        //Setting up streams
        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        ByteArrayOutputStream o3 = new ByteArrayOutputStream();

        DataOutputStream do1 = new DataOutputStream(o1);
        DataOutputStream do2 = new DataOutputStream(o2);
        DataOutputStream do3 = new DataOutputStream(o3);

        DataOutputStream[] dos = new DataOutputStream[] {do1, do2, do3};

        KniffelSender sender = new StreamBindingSenderImpl(dos, true);

        //Sends the command over the streams
        sender.sendEndTurn(ScoreTableRows.CHANCE, 20, 2);

        //Creates a stream with the expected values
        ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        DataOutputStream expectedDos = new DataOutputStream(expectedBos);
        //Command ID
        expectedDos.writeInt(Commands.END_TURN);
        //Score table row
        expectedDos.writeInt(ScoreTableRows.CHANCE);
        //Score
        expectedDos.writeInt(20);
        //Player ID
        expectedDos.writeInt(2);

        ByteArrayOutputStream emptyBos = new ByteArrayOutputStream();

        //Checks if the streams match. The first stream is supposed to be empty since it belongs to the same player the command came from
        assertArrayEquals(emptyBos.toByteArray(), o1.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o2.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o3.toByteArray());
    }

    @Test
    public void sendChangeDiceStateTest() throws IOException, IllegalStateException {
        //Setting up streams
        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        ByteArrayOutputStream o3 = new ByteArrayOutputStream();

        DataOutputStream do1 = new DataOutputStream(o1);
        DataOutputStream do2 = new DataOutputStream(o2);
        DataOutputStream do3 = new DataOutputStream(o3);

        DataOutputStream[] dos = new DataOutputStream[] {do1, do2, do3};

        KniffelSender sender = new StreamBindingSenderImpl(dos, true);

        //Sends the command over the streams
        sender.sendChangeDiceState(1, 2);

        //Creates a stream with the expected values
        ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        DataOutputStream expectedDos = new DataOutputStream(expectedBos);
        //Command ID
        expectedDos.writeInt(Commands.CHANGE_DICE_STATE);
        //Dice Index
        expectedDos.writeInt(1);
        //Player ID
        expectedDos.writeInt(2);

        ByteArrayOutputStream emptyBos = new ByteArrayOutputStream();

        //Checks if the streams match. The first stream is supposed to be empty since it belongs to the same player the command came from
        assertArrayEquals(emptyBos.toByteArray(), o1.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o2.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o3.toByteArray());
    }

    @Test
    public void sendSaveGameTest() throws IOException {
        //Setting up streams
        ByteArrayOutputStream o1 = new ByteArrayOutputStream();
        ByteArrayOutputStream o2 = new ByteArrayOutputStream();
        ByteArrayOutputStream o3 = new ByteArrayOutputStream();

        DataOutputStream do1 = new DataOutputStream(o1);
        DataOutputStream do2 = new DataOutputStream(o2);
        DataOutputStream do3 = new DataOutputStream(o3);

        DataOutputStream[] dos = new DataOutputStream[] {do1, do2, do3};

        KniffelSender sender = new StreamBindingSenderImpl(dos, true);

        //Sends the command over the streams
        sender.sendEndGame(2);

        //Creates a stream with the expected values
        ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        DataOutputStream expectedDos = new DataOutputStream(expectedBos);
        //Command ID
        expectedDos.writeInt(Commands.END_GAME);
        //Player ID
        expectedDos.writeInt(2);

        ByteArrayOutputStream emptyBos = new ByteArrayOutputStream();

        //Checks if the streams match. The first stream is supposed to be empty since it belongs to the same player the command came from
        assertArrayEquals(emptyBos.toByteArray(), o1.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o2.toByteArray());
        assertArrayEquals(expectedBos.toByteArray(), o3.toByteArray());

    }

    @Test
    public void sendTwoPlayerCommandFromPlayerTwoTest() throws IOException {
        //Test for a very specific command distribution bug that came up during development
        //Prevented player 2 specifically from sending any commands with their own player ID in a two player game
        //Can mostly be ignored now that is is fixed, but I'll keep the test anyways

        //Setting up streams
        ByteArrayOutputStream o1 = new ByteArrayOutputStream();

        DataOutputStream do1 = new DataOutputStream(o1);

        DataOutputStream[] dos = new DataOutputStream[] {do1};

        KniffelSender sender = new StreamBindingSenderImpl(dos, false);

        //Sends the command over the streams
        sender.sendEndGame(2);

        //Creates a stream with the expected values
        ByteArrayOutputStream expectedBos = new ByteArrayOutputStream();
        DataOutputStream expectedDos = new DataOutputStream(expectedBos);
        //Command ID
        expectedDos.writeInt(Commands.END_GAME);
        //Player ID
        expectedDos.writeInt(2);

        //Checks if the stream matches. Clients used to not send anything if they were using player ID 2 which is incorrect behaviour
        assertArrayEquals(expectedBos.toByteArray(), o1.toByteArray());

    }
}
