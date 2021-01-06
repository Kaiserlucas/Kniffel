package kniffel.gamelogic;

import kniffel.data.ScoreTableRows;
import org.junit.Test;

import static junit.framework.TestCase.fail;

public class KniffelScoreCalculatorTests {

    @Test
    public void onesTest() throws KniffelException {
        int[] diceValues = {1,2,3,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.ONES, diceValues) == 2);
        diceValues[0] = 2;
        diceValues[3] = 3;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.ONES, diceValues) == 0);
    }

    @Test
    public void twosTest() throws KniffelException {
        int[] diceValues = {1,2,3,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.TWOS, diceValues) == 2);
        diceValues[0] = 2;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.TWOS, diceValues) == 4);
    }

    @Test
    public void threesTest() throws KniffelException {
        int[] diceValues = {1,2,3,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.THREES, diceValues) == 3);
        diceValues[3] = 3;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.THREES, diceValues) == 6);
    }

    @Test
    public void foursTest() throws KniffelException {
        int[] diceValues = {1,2,3,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FOURS, diceValues) == 4);
        diceValues[4] = 0;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FOURS, diceValues) == 0);
    }

    @Test
    public void fivesTest() throws KniffelException {
        int[] diceValues = {1,2,3,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FIVES, diceValues) == 0);
        diceValues[0] = 5;
        diceValues[3] = 5;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FIVES, diceValues) == 10);
    }

    @Test
    public void sixesTest() throws KniffelException {
        int[] diceValues = {1,2,3,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.SIXES, diceValues) == 0);
        diceValues[0] = 6;
        diceValues[3] = 6;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.SIXES, diceValues) == 12);
    }

    @Test
    public void threeOfAKindTest() throws KniffelException {
        int[] diceValues = {1,2,1,1,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.THREE_OF_A_KIND, diceValues) == 9);
        diceValues[0] = 6;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.THREE_OF_A_KIND, diceValues) == 0);
    }

    @Test
    public void fourOfAKindTest() throws KniffelException {
        int[] diceValues = {1,2,1,1,1};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FOUR_OF_A_KIND, diceValues) == 6);
        diceValues[0] = 6;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FOUR_OF_A_KIND, diceValues) == 0);
    }

    @Test
    public void smallStraightTest() throws KniffelException {
        int[] diceValues = {4,2,3,5,3};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.SMALL_STRAIGHT, diceValues) == 30);
        diceValues[0] = 6;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.SMALL_STRAIGHT, diceValues) == 0);
    }

    @Test
    public void largeStraightTest() throws KniffelException {
        int[] diceValues = {4,2,3,5,1};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.LARGE_STRAIGHT, diceValues) == 40);
        diceValues[0] = 3;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.LARGE_STRAIGHT, diceValues) == 0);
    }

    @Test
    public void fullHouseTest() throws KniffelException {
        int[] diceValues = {4,2,2,4,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FULL_HOUSE, diceValues) == 25);
        diceValues[0] = 3;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.FULL_HOUSE, diceValues) == 0);
    }

    @Test
    public void kniffelTest() throws KniffelException {
        int[] diceValues = {4,4,4,4,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.KNIFFEL, diceValues) == 50);
        diceValues[0] = 3;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.KNIFFEL, diceValues) == 0);
    }

    @Test
    public void chanceTest() throws KniffelException {
        int[] diceValues = {4,2,2,4,4};
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.CHANCE, diceValues) == 16);
        diceValues[0] = 3;
        assert(KniffelScoreCalculator.calculateScore(ScoreTableRows.CHANCE, diceValues) == 15);
    }

    @Test
    public void incorrectArraySizeTest() {
        int[] diceValues = {4,2,2,4};

        try {
            KniffelScoreCalculator.calculateScore(ScoreTableRows.CHANCE, diceValues);
            fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }

    @Test
    public void incorrectScoreTableRowTest() {
        int[] diceValues = {4,2,2,4,4};

        try {
            KniffelScoreCalculator.calculateScore(ScoreTableRows.SCORE_TABLE_DIM, diceValues);
            fail();
        } catch (KniffelException e) {
            //Exception was thrown as expected
        }
    }
}
