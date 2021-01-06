package kniffel.gamelogic;

import dummyImplementations.StreamBindingSenderDummy;
import kniffel.data.ScoreTable;
import kniffel.data.ScoreTableImpl;
import kniffel.data.ScoreTableRowNames;
import kniffel.data.ScoreTableRows;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertArrayEquals;


public class KniffelEngineTests {

    @Test
    public void loadGameConstructorTest() throws KniffelException {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        scoreTable.setScore(ScoreTableRows.CHANCE, 12, 1);
        scoreTable.setScore(ScoreTableRows.CHANCE, 12, 2);

        KniffelEngine engine = new KniffelEngine(scoreTable, new StreamBindingSenderDummy());

        //Game should have initialized with player 3's turn
        assert(engine.getState() == GameState.OtherPlayerTurn);
        assert(engine.getActivePlayer() == 3);
    }

    //////////////////////////////////////////////////////////////
    //                                                          //
    //                   KniffelFacade Methods                  //
    //                                                          //
    //////////////////////////////////////////////////////////////

    @Test
    public void rollDiceTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        assertArrayEquals(engine.getDiceValues(), new int[] {-1, -1, -1, -1,- 1});
        assert(engine.getRollsRemaining() == 3);

        engine.rollDice();

        int[] rolledDiceValues = engine.getDiceValues();

        for (int rolledDiceValue : rolledDiceValues) {
            assert (rolledDiceValue != -1);
        }
        assert(engine.getRollsRemaining() == 2);
    }

    @Test
    public void rollDiceNoRollsLeftTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();
        engine.rollDice();
        engine.rollDice();

        try {
            engine.rollDice();
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void rollDiceWrongStateTest() throws IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        //Engine was initialized as player 2, so it should be another player's turn
        assert(engine.getState() == GameState.OtherPlayerTurn);

        try {
            engine.rollDice();
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void endTurnTest() throws IllegalStateException, IOException, KniffelException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();

        //Checks that both the score writing to the score table as well as the state change were successful
        assert(engine.getState() == GameState.OwnTurn);
        assert(engine.getScoreTable().getScore(ScoreTableRows.CHANCE, 1) == -1);
        engine.endTurn(ScoreTableRows.CHANCE);
        assert(engine.getState() == GameState.OtherPlayerTurn);
        assert(engine.getScoreTable().getScore(ScoreTableRows.CHANCE, 1) != -1);
    }

    @Test
    public void endTurnWrongStateTest() throws KniffelException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        //Engine was initialized as player 2, so it should be another player's turn
        assert(engine.getState() == GameState.OtherPlayerTurn);

        try {
            engine.endTurn(ScoreTableRows.CHANCE);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void endTurnWithoutRollingDiceTest() throws KniffelException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        try {
            engine.endTurn(ScoreTableRows.CHANCE);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void endTurnWithWrongTableRowTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();

        try {
            engine.endTurn(ScoreTableRows.SCORE_TABLE_DIM);
            fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void changeDiceStateTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();

        //Checks if the dice state was changed correctly
        assertArrayEquals(engine.areDicesSetAside(), new boolean[] {false, false, false, false, false});
        engine.changeDiceState(0);
        assertArrayEquals(engine.areDicesSetAside(), new boolean[] {true, false, false, false, false});
    }

    @Test
    public void areDiceStatesHandledCorrectlyTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();

        engine.changeDiceState(0);
        engine.changeDiceState(1);
        engine.changeDiceState(2);
        engine.changeDiceState(3);
        engine.changeDiceState(4);

        //Checks if no dices are rolled as intended
        int[] diceValuesBeforeReroll = engine.getDiceValues();
        engine.rollDice();
        assertArrayEquals(diceValuesBeforeReroll, engine.getDiceValues());
    }

    @Test
    public void changeDiceStateWrongStateTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        //Simulates player 1 rolling dice once. Values do not matter for this test so they are left as null
        engine.receiveRollDice(null, 1);

        //Since this engine is player 2 they are not allowed to change dice states at the moment
        try{
            engine.changeDiceState(0);
            fail();
        } catch(IllegalStateException e) {
            //Exception was thrown as expected
        }

    }

    @Test
    public void changeDiceStateWithoutRollingTest() throws IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        try {
            engine.changeDiceState(0);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void endGameTest() throws IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        assert(engine.getState() == GameState.OwnTurn);
        engine.endGame();
        assert(engine.getState() == GameState.GameEnded);
    }

    @Test
    public void getActivePlayerTest() throws IllegalStateException, IOException, KniffelException {
        KniffelEngine engine = new KniffelEngine(2, new String[] {"Hans", "Peter"}, 1, new StreamBindingSenderDummy());

        assert(engine.getActivePlayer() == 1);

        engine.rollDice();
        engine.endTurn(ScoreTableRows.CHANCE);

        assert(engine.getActivePlayer() == 2);

        engine.receiveRollDice(new int[] {1, 1, 1, 1, 1}, 2);
        engine.receiveEndTurn(ScoreTableRows.CHANCE, 10, 2);

        assert(engine.getActivePlayer() == 1);
    }

    @Test
    public void getScoreTableRowNamesTest() throws KniffelException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        assert(engine.getScoreTableRowName(ScoreTableRows.ONES).equals(ScoreTableRowNames.Einser.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.TWOS).equals(ScoreTableRowNames.Zweier.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.THREES).equals(ScoreTableRowNames.Dreier.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.FOURS).equals(ScoreTableRowNames.Vierer.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.FIVES).equals(ScoreTableRowNames.Fuenfer.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.SIXES).equals(ScoreTableRowNames.Sechser.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.BONUS).equals(ScoreTableRowNames.Bonus.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.UPPER_BLOCK_TOTAL).equals(ScoreTableRowNames.Oben.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.THREE_OF_A_KIND).equals(ScoreTableRowNames.Dreierpasch.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.FOUR_OF_A_KIND).equals(ScoreTableRowNames.Viererpasch.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.FULL_HOUSE).equals(ScoreTableRowNames.FullHouse.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.SMALL_STRAIGHT).equals(ScoreTableRowNames.KleineStrasse.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.LARGE_STRAIGHT).equals(ScoreTableRowNames.GrosseStrasse.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.KNIFFEL).equals(ScoreTableRowNames.Kniffel.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.CHANCE).equals(ScoreTableRowNames.Chance.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.LOWER_BLOCK_TOTAL).equals(ScoreTableRowNames.Unten.name));
        assert(engine.getScoreTableRowName(ScoreTableRows.GRAND_TOTAL).equals(ScoreTableRowNames.Gesamt.name));

    }

    @Test
    public void getScoreTableRowNamesOutOfBoundsTest(){
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        try {
            engine.getScoreTableRowName(ScoreTableRows.SCORE_TABLE_DIM);
            fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

        try {
            engine.getScoreTableRowName(-1);
            fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

    }

    //////////////////////////////////////////////////////////////
    //                                                          //
    //                   KniffelReceiver Methods                //
    //                                                          //
    //////////////////////////////////////////////////////////////

    @Test
    public void receiveRollDiceTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        assertArrayEquals(engine.getDiceValues(), new int[] {-1, -1, -1, -1,- 1});

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        int[] rolledDiceValues = engine.getDiceValues();

        for (int rolledDiceValue : rolledDiceValues) {
            assert (rolledDiceValue != -1);
        }
    }

    @Test
    public void receiveRollDiceNoRollsLeftTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);
        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);
        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        try {
            engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveRollDiceWrongStateTest() throws IOException{
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        //An exception should be thrown if another player attempts to "perform a turn for you"
        try {
            engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveRollDiceWrongPlayerIDTest() throws IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        assertArrayEquals(engine.getDiceValues(), new int[] {-1, -1, -1, -1,- 1});

        try {
            engine.receiveRollDice(new int[] {1,2,3,4,5}, 3);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveEndTurnTest() throws IOException, IllegalStateException, KniffelException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        //Checks that both the score writing to the score table as well as the state change were successful
        assert(engine.getState() == GameState.OtherPlayerTurn);
        assert(engine.getScoreTable().getScore(ScoreTableRows.CHANCE, 1) == -1);
        engine.receiveEndTurn(ScoreTableRows.CHANCE, 12, 1);
        assert(engine.getState() == GameState.OwnTurn);
        assert(engine.getScoreTable().getScore(ScoreTableRows.CHANCE, 1) != -1);
    }

    @Test
    public void receiveEndTurnWrongStateTest() throws IllegalStateException, IOException, KniffelException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();

        //An exception should be thrown if another player attempts to "perform a turn for you"
        try {
            engine.receiveEndTurn(ScoreTableRows.CHANCE, 12, 1);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveEndTurnWithoutRollingDiceTest() throws KniffelException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        try {
            engine.receiveEndTurn(ScoreTableRows.CHANCE, 12, 1);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveEndTurnWithWrongTableRowTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        try {
            engine.receiveEndTurn(ScoreTableRows.SCORE_TABLE_DIM, 12, 1);
            fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveEndTurnWrongPlayerIDTest() throws IllegalStateException, IOException, KniffelException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        try {
            engine.receiveEndTurn(ScoreTableRows.SCORE_TABLE_DIM, 12, 3);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveChangeDiceStateTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        //Checks if the dice state was changed correctly
        assertArrayEquals(engine.areDicesSetAside(), new boolean[] {false, false, false, false, false});
        engine.receiveChangeDiceState(0, 1);
        assertArrayEquals(engine.areDicesSetAside(), new boolean[] {true, false, false, false, false});
    }

    @Test
    public void receiveChangeDiceStateWrongStateTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 1, new StreamBindingSenderDummy());

        engine.rollDice();

        try {
            engine.receiveChangeDiceState(0, 1);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveChangeDiceStateWithoutRollingTest() throws IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        try {
            engine.receiveChangeDiceState(0, 1);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveChangeDiceWrongPlayerIDTest() throws IllegalStateException, IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        engine.receiveRollDice(new int[] {1,2,3,4,5}, 1);

        try {
            engine.receiveChangeDiceState(0, 3);
            fail();
        } catch (IllegalStateException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void receiveEndGameTest() throws IOException {
        KniffelEngine engine = new KniffelEngine(3, new String[] {"Hans", "Peter", "Karl"}, 2, new StreamBindingSenderDummy());

        assert(engine.getState() == GameState.OtherPlayerTurn);
        engine.receiveEndGame(1);
        assert(engine.getState() == GameState.GameEnded);
    }

}
