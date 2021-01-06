package kniffel.gamelogic;

import kniffel.data.ScoreTableRows;

import java.util.Arrays;

public class KniffelScoreCalculator {

    public static final int FULL_HOUSE_VALUE = 25;
    public static final int SMALL_STRAIGHT_VALUE = 30;
    public static final int LARGE_STRAIGHT_VALUE = 40;
    public static final int KNIFFEL_VALUE = 50;

    public static int calculateScore(int scoreTableRow, int[] diceValues) throws KniffelException {

        if(diceValues.length != 5) {
            throw new KniffelException();
        }

        switch(scoreTableRow) {
            case ScoreTableRows.ONES:
                return calculateUpperBlockRows(diceValues, 1);
            case ScoreTableRows.TWOS:
                return calculateUpperBlockRows(diceValues, 2);
            case ScoreTableRows.THREES:
                return calculateUpperBlockRows(diceValues, 3);
            case ScoreTableRows.FOURS:
                return calculateUpperBlockRows(diceValues, 4);
            case ScoreTableRows.FIVES:
                return calculateUpperBlockRows(diceValues, 5);
            case ScoreTableRows.SIXES:
                return calculateUpperBlockRows(diceValues, 6);
            case ScoreTableRows.THREE_OF_A_KIND:
                return calculateOfAKinds(diceValues, 3);
            case ScoreTableRows.FOUR_OF_A_KIND:
                return calculateOfAKinds(diceValues, 4);
            case ScoreTableRows.FULL_HOUSE:
                return calculateFullHouse(diceValues);
            case ScoreTableRows.SMALL_STRAIGHT:
                return calculateStraights(diceValues, 4);
            case ScoreTableRows.LARGE_STRAIGHT:
                return calculateStraights(diceValues, 5);
            case ScoreTableRows.KNIFFEL:
                return calculateKniffel(diceValues);
            case ScoreTableRows.CHANCE:
                return calculateSumOfAllDice(diceValues);
            default:
                throw new KniffelException();
        }
    }

    private static int calculateUpperBlockRows(int[] diceValues, int rowNumber) {
        int result = 0;
        for(int i : diceValues) {
            if(i == rowNumber) {
                result += i;
            }
        }
        return result;
    }

    private static int calculateOfAKinds(int[] diceValues, int xOfAKind) {
        int[] sortedArray = diceValues.clone();
        Arrays.sort(sortedArray);

        int sameInARow = 0;
        int checkedNumber = -1;

        for(int i : sortedArray) {

            if(i == checkedNumber) {
                sameInARow++;
            } else {
                sameInARow = 1;
                checkedNumber = i;
            }

            if(sameInARow >= xOfAKind) {
                return calculateSumOfAllDice(diceValues);
            }
        }

        return 0;
    }

    private static int calculateFullHouse(int[] diceValues) {
        int[] sortedArray = diceValues.clone();
        Arrays.sort(sortedArray);

        if(sortedArray[0] == sortedArray[1] && sortedArray[3] == sortedArray[4] && (sortedArray[2] == sortedArray[1] || sortedArray[2] == sortedArray[3])) {
            return FULL_HOUSE_VALUE;
        } else {
            return 0;
        }
    }

    private static int calculateStraights(int[] diceValues, int requiredInARow) {
        int[] sortedArray = diceValues.clone();
        Arrays.sort(sortedArray);

        int inARow = 0;
        int lastNumber = -1;

        for(int i : sortedArray) {

            if(i == lastNumber + 1) {
                lastNumber++;
                inARow++;
            } else if(i != lastNumber) {
                inARow = 1;
                lastNumber = i;
            }

            if(inARow >= requiredInARow) {

                if(requiredInARow == 4) {
                    return SMALL_STRAIGHT_VALUE;
                } else{
                    return LARGE_STRAIGHT_VALUE;
                }
            }
        }

        return 0;
    }

    private static int calculateKniffel(int[] diceValues) {
        int firstElement = diceValues[0];
        boolean differentElement = false;

        for(int i : diceValues) {
            if (firstElement != i) {
                differentElement = true;
                break;
            }
        }

        if(differentElement) {
            return 0;
        } else {
            return KNIFFEL_VALUE;
        }

    }

    private static int calculateSumOfAllDice(int[] diceValues) {
        int result = 0;
        for(int i : diceValues) {
            result += i;
        }
        return result;
    }
}
