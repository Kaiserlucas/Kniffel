package kniffel.data;

import kniffel.gamelogic.KniffelException;
import org.junit.Assert;
import org.junit.Test;

public class ScoreTableTests {

    @Test
    public void setAndGetScoreSuccessfullyTest() throws KniffelException {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        scoreTable.setScore(ScoreTableRows.FULL_HOUSE, 25, 1);
        assert(scoreTable.getScore(ScoreTableRows.FULL_HOUSE, 1) == 25);
    }

    @Test
    public void attemptToSetScoreTwiceTest() throws KniffelException {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        scoreTable.setScore(ScoreTableRows.FULL_HOUSE, 25, 1);
        scoreTable.setScore(ScoreTableRows.FULL_HOUSE, 25, 2);

        try {
            scoreTable.setScore(ScoreTableRows.FULL_HOUSE, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void attemptToSetScoreOutOfBoundsTest() {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        try {
            scoreTable.setScore(-1, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

        try {
            scoreTable.setScore(ScoreTableRows.SCORE_TABLE_DIM, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void setScoreWithIncorrectPlayerTest() {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        try {
            scoreTable.setScore(ScoreTableRows.SCORE_TABLE_DIM, 25, 2);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void attemptToSetScoreToForbiddenRowsTest() {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        try {
            scoreTable.setScore(ScoreTableRows.BONUS, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

        try {
            scoreTable.setScore(ScoreTableRows.UPPER_BLOCK_TOTAL, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

        try {
            scoreTable.setScore(ScoreTableRows.LOWER_BLOCK_TOTAL, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

        try {
            scoreTable.setScore(ScoreTableRows.GRAND_TOTAL, 25, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void getScoreOutOfBoundsTest() {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        try {
            scoreTable.getScore(-1, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }

        try {
            scoreTable.getScore(ScoreTableRows.SCORE_TABLE_DIM, 1);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void getScoreIncorrectPlayerIDTest() {
        String[] playerNames = {"Frank", "Karl", "Hans"};
        ScoreTable scoreTable = new ScoreTableImpl(3, playerNames, 1);

        try {
            scoreTable.getScore(ScoreTableRows.CHANCE, 4);
            Assert.fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void getNextPlayerTest() throws KniffelException {
        String[] playerNames = {"Frank", "Karl"};
        ScoreTable scoreTable = new ScoreTableImpl(2, playerNames, 1);

        assert(scoreTable.getNextPlayer() == 1);
        scoreTable.setScore(ScoreTableRows.CHANCE, 12, 1);
        assert(scoreTable.getNextPlayer() == 2);
        scoreTable.setScore(ScoreTableRows.CHANCE, 12, 2);
        assert(scoreTable.getNextPlayer() == 1);
    }

    @Test
    public void getNextPlayerGameEndTest() throws KniffelException {
        String[] playerNames = {"Frank", "Karl"};
        ScoreTable scoreTable = new ScoreTableImpl(2, playerNames, 1);

        for(int i = 0; i <= ScoreTableRows.CHANCE; i++) {
            try {
                scoreTable.setScore(i,i,1);
                scoreTable.setScore(i,i,2);
            } catch(KniffelException e) {
                //One of the forbidden scoreTableRows was written to.
                //Not relevant for this test. Can be ignored
            }
        }

        assert(scoreTable.getNextPlayer() == ScoreTableImpl.UNDEFINED);
    }


    @Test
    public void verifyAutomatedTableRows() throws KniffelException {
        String[] playerNames = {"Frank", "Karl"};
        ScoreTable scoreTable = new ScoreTableImpl(2, playerNames, 1);

        //Check if upper block totals are set correctly
        for(int i = 0; i < ScoreTableRows.SIXES; i++) {
            try {
                scoreTable.setScore(i,1,1);
                scoreTable.setScore(i,15,2);
            } catch(KniffelException e) {
                //One of the forbidden scoreTableRows was written to.
                //Not relevant for this test. Can be ignored
            }
        }

        assert(scoreTable.getScore(ScoreTableRows.BONUS, 1) == -1);
        assert(scoreTable.getScore(ScoreTableRows.BONUS, 2) == -1);
        assert(scoreTable.getScore(ScoreTableRows.UPPER_BLOCK_TOTAL, 1) == -1);
        assert(scoreTable.getScore(ScoreTableRows.UPPER_BLOCK_TOTAL, 2) == -1);

        scoreTable.setScore(ScoreTableRows.SIXES,1,1);
        scoreTable.setScore(ScoreTableRows.SIXES,15,2);

        assert(scoreTable.getScore(ScoreTableRows.BONUS, 1) == 0);
        assert(scoreTable.getScore(ScoreTableRows.BONUS, 2) == 35);
        assert(scoreTable.getScore(ScoreTableRows.UPPER_BLOCK_TOTAL, 1) == 6);
        assert(scoreTable.getScore(ScoreTableRows.UPPER_BLOCK_TOTAL, 2) == 125);


        //Check if lower block totals are set correctly
        for(int i = ScoreTableRows.THREE_OF_A_KIND; i < ScoreTableRows.CHANCE; i++) {
            try {
                scoreTable.setScore(i,1,1);
                scoreTable.setScore(i,15,2);
            } catch(KniffelException e) {
                //One of the forbidden scoreTableRows was written to.
                //Not relevant for this test. Can be ignored
            }
        }

        assert(scoreTable.getScore(ScoreTableRows.LOWER_BLOCK_TOTAL, 1) == -1);
        assert(scoreTable.getScore(ScoreTableRows.LOWER_BLOCK_TOTAL, 2) == -1);
        assert(scoreTable.getScore(ScoreTableRows.GRAND_TOTAL, 1) == -1);
        assert(scoreTable.getScore(ScoreTableRows.GRAND_TOTAL, 2) == -1);

        scoreTable.setScore(ScoreTableRows.CHANCE,1,1);
        scoreTable.setScore(ScoreTableRows.CHANCE,15,2);

        assert(scoreTable.getScore(ScoreTableRows.LOWER_BLOCK_TOTAL, 1) == 7);
        assert(scoreTable.getScore(ScoreTableRows.LOWER_BLOCK_TOTAL, 2) == 105);
        assert(scoreTable.getScore(ScoreTableRows.GRAND_TOTAL, 1) == 13);
        assert(scoreTable.getScore(ScoreTableRows.GRAND_TOTAL, 2) == 230);
    }

}
