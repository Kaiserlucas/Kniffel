package kniffel;

import dummyImplementations.StreamBindingSenderDummy;
import kniffel.data.ScoreTableRows;
import kniffel.gamelogic.*;
import kniffel.gamelogic.IllegalStateException;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertArrayEquals;

public class KniffelIntegrationsTests {

    //Tests the entire package at once via the KniffelFacade

    @Test
    public void sendAndDistributeRollDiceCommandTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 3;

        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 3, sender3);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);

        engine1.rollDice();

        //Check if the dice values were correctly set for every player
        assertArrayEquals(engine1.getDiceValues(), engine2.getDiceValues());
        assertArrayEquals(engine2.getDiceValues(), engine3.getDiceValues());

        engine1.endTurn(ScoreTableRows.CHANCE);

        engine2.rollDice();

        //Check if the dice values were correctly set for every player
        //This second check is necessary since distribution between host and players works differently if
        //the command is sent from a player other than 1
        assertArrayEquals(engine1.getDiceValues(), engine2.getDiceValues());
        assertArrayEquals(engine2.getDiceValues(), engine3.getDiceValues());
    }

    @Test
    public void endAndDistributeEndTurnCommandTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 3;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 3, sender3);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);


        //One whole turn of each player is simulated to check if the transition Player3 -> Player1 works as well
        assert(engine1.getState() == GameState.OwnTurn);
        assert(engine2.getState() == GameState.OtherPlayerTurn);
        assert(engine3.getState() == GameState.OtherPlayerTurn);

        engine1.rollDice();
        engine1.endTurn(ScoreTableRows.CHANCE);

        assert(engine1.getState() == GameState.OtherPlayerTurn);
        assert(engine2.getState() == GameState.OwnTurn);
        assert(engine3.getState() == GameState.OtherPlayerTurn);

        engine2.rollDice();
        engine2.endTurn(ScoreTableRows.CHANCE);

        assert(engine1.getState() == GameState.OtherPlayerTurn);
        assert(engine2.getState() == GameState.OtherPlayerTurn);
        assert(engine3.getState() == GameState.OwnTurn);

        engine3.rollDice();
        engine3.endTurn(ScoreTableRows.CHANCE);

        assert(engine1.getState() == GameState.OwnTurn);
        assert(engine2.getState() == GameState.OtherPlayerTurn);
        assert(engine3.getState() == GameState.OtherPlayerTurn);
    }

    @Test
    public void endAndDistributeChangeDiceStateCommandTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 3;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 3, sender3);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);

        engine1.rollDice();
        engine1.changeDiceState(2);

        //Check if the dice states were correctly set for every player
        assertArrayEquals(engine1.areDicesSetAside(), new boolean[] {false, false, true, false, false});
        assertArrayEquals(engine2.areDicesSetAside(), new boolean[] {false, false, true, false, false});
        assertArrayEquals(engine3.areDicesSetAside(), new boolean[] {false, false, true, false, false});

        engine1.rollDice();

        //Check if the dice state change is properly reflected after a dice roll
        assertArrayEquals(engine1.getDiceValues(), engine2.getDiceValues());
        assertArrayEquals(engine2.getDiceValues(), engine3.getDiceValues());

        engine1.endTurn(ScoreTableRows.CHANCE);

        engine2.rollDice();

        //Check if the dice states were correctly reset for every player due to the start of the new turn
        assertArrayEquals(engine1.areDicesSetAside(), new boolean[] {false, false, false, false, false});
        assertArrayEquals(engine2.areDicesSetAside(), new boolean[] {false, false, false, false, false});
        assertArrayEquals(engine3.areDicesSetAside(), new boolean[] {false, false, false, false, false});

        engine2.changeDiceState(1);
        engine2.changeDiceState(3);

        //Check if the dice states were correctly set for every player
        //This second check is necessary since distribution between host and players works differently if
        //the command is sent from a player other than 1
        assertArrayEquals(engine1.areDicesSetAside(), new boolean[] {false, true, false, true, false});
        assertArrayEquals(engine2.areDicesSetAside(), new boolean[] {false, true, false, true, false});
        assertArrayEquals(engine3.areDicesSetAside(), new boolean[] {false, true, false, true, false});
    }

    @Test
    public void endAndDistributeEndGameFromHostCommandTest() throws IOException {
        final int NUMBER_OF_PLAYERS = 3;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 3, sender3);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);

        engine1.endGame();

        assert(engine1.getState() == GameState.GameEnded);
        assert(engine2.getState() == GameState.GameEnded);
        assert(engine3.getState() == GameState.GameEnded);

    }

    @Test
    public void endAndDistributeEndGameFromNonHostCommandTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 3;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 3, sender3);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);

        engine1.rollDice();

        engine1.endTurn(ScoreTableRows.CHANCE);

        engine2.endGame();

        assert(engine1.getState() == GameState.GameEnded);
        assert(engine2.getState() == GameState.GameEnded);
        assert(engine3.getState() == GameState.GameEnded);

    }

    @Test
    public void fourPlayerTurnOrderTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 4;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender4 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl", "Heinz"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl", "Heinz"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl", "Heinz"}, 3, sender3);
        KniffelEngine engine4 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl", "Heinz"}, 4, sender4);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3, engine4}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender4.setReceivers(new KniffelReceiver[] {engine1}, false);

        engine1.rollDice();

        engine1.endTurn(ScoreTableRows.CHANCE);

        engine2.rollDice();

        engine2.endTurn(ScoreTableRows.CHANCE);

        engine3.rollDice();

        engine3.endTurn(ScoreTableRows.CHANCE);

        engine4.rollDice();

        engine4.endTurn(ScoreTableRows.CHANCE);

        assert(engine1.getState() == GameState.OwnTurn);
        assert(engine2.getState() == GameState.OtherPlayerTurn);
        assert(engine3.getState() == GameState.OtherPlayerTurn);
        assert(engine4.getState() == GameState.OtherPlayerTurn);

    }

    @Test
    public void twoPlayerTurnOrderTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 2;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter"}, 2, sender2);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);

        assert(engine1.getState() == GameState.OwnTurn);
        assert(engine2.getState() == GameState.OtherPlayerTurn);

        engine1.rollDice();

        engine1.endTurn(ScoreTableRows.CHANCE);

        assert(engine1.getState() == GameState.OtherPlayerTurn);
        assert(engine2.getState() == GameState.OwnTurn);

        engine2.rollDice();

        engine2.endTurn(ScoreTableRows.CHANCE);

        assert(engine1.getState() == GameState.OwnTurn);
        assert(engine2.getState() == GameState.OtherPlayerTurn);

    }

    @Test
    public void threePlayerFullGameTest() throws IllegalStateException, IOException, KniffelException {
        final int NUMBER_OF_PLAYERS = 3;
        StreamBindingSenderDummy sender1 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender2 = new StreamBindingSenderDummy();
        StreamBindingSenderDummy sender3 = new StreamBindingSenderDummy();

        KniffelEngine engine1 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 1, sender1);
        KniffelEngine engine2 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 2, sender2);
        KniffelEngine engine3 = new KniffelEngine(NUMBER_OF_PLAYERS, new String[] {"Hans", "Peter", "Karl"}, 3, sender3);

        //Set receivers. Player 1 knows players 2 and 3 but players 2 and 3 only know player 1
        sender1.setReceivers(new KniffelReceiver[] {engine2, engine3}, true);
        sender2.setReceivers(new KniffelReceiver[] {engine1}, false);
        sender3.setReceivers(new KniffelReceiver[] {engine1}, false);

        for(int i = 0; i <= ScoreTableRows.CHANCE; i++) {

            assert(engine1.getState() == GameState.OwnTurn);
            assert(engine2.getState() == GameState.OtherPlayerTurn);
            assert(engine3.getState() == GameState.OtherPlayerTurn);

            try {
                engine1.rollDice();
                engine1.endTurn(i);
                engine2.rollDice();
                engine2.endTurn(i);
                engine3.rollDice();
                engine3.endTurn(i);
            } catch(KniffelException e) {
                //One of the forbidden scoreTableRows was written to.
                //Not relevant for this test. Can be ignored
            }

        }

        assert(engine1.getState() == GameState.GameEnded);
        assert(engine2.getState() == GameState.GameEnded);
        assert(engine3.getState() == GameState.GameEnded);

    }


}
