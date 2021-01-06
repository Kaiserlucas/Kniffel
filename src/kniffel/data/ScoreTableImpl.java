package kniffel.data;

import kniffel.gamelogic.KniffelException;

import java.io.Serializable;

public class ScoreTableImpl implements ScoreTable, Serializable {

    private int ownPlayerID;
    private int numberOfPlayers;
    private String[] playerNames;
    private int[][] scores;
    private int nextPlayer;
    private int playedTurns;
    public static final int UNDEFINED = -1;

    //Constructor used for starting new games
    public ScoreTableImpl(int numberOfPlayers, String[] playerNames, int ownPlayerID) {
        this.numberOfPlayers = numberOfPlayers;
        this.playerNames = playerNames;
        this.ownPlayerID = ownPlayerID;
        this.scores = new int[ScoreTableRows.SCORE_TABLE_DIM][numberOfPlayers];

        for(int i = 0; i < ScoreTableRows.SCORE_TABLE_DIM; i++) {
            for(int j = 0; j < numberOfPlayers; j++) {
                scores[i][j] = UNDEFINED;
            }
        }

        this.nextPlayer = 1;
    }

    //Constructor used for loading saved games
    public ScoreTableImpl(int numberOfPlayers, int nextPlayer, String[] playerNames, int ownPlayerID, int[][] scores) {
        this.numberOfPlayers = numberOfPlayers;
        this.playerNames = playerNames;
        this.ownPlayerID = ownPlayerID;
        this.nextPlayer = nextPlayer;
        this.scores = scores;

        this.playedTurns = countPlayedTurns();
    }

    @Override
    public void setScore(int scoreTableRow, int score, int playerID) throws KniffelException{
        if(scoreTableRow >= 0 && scoreTableRow <= ScoreTableRows.SCORE_TABLE_DIM-1 && scores[scoreTableRow][playerID - 1] == UNDEFINED && getNextPlayer() == playerID
                && scoreTableRow != ScoreTableRows.BONUS && scoreTableRow != ScoreTableRows.UPPER_BLOCK_TOTAL && scoreTableRow != ScoreTableRows.LOWER_BLOCK_TOTAL && scoreTableRow != ScoreTableRows.GRAND_TOTAL) {
            scores[scoreTableRow][playerID - 1] = score;
            incrementNextPlayer();
            playedTurns++;
            checkForbiddenScoreTableRows(playerID);
        } else {
            throw new KniffelException();
        }
    }

    @Override
    public int getScore(int scoreTableRow, int playerID) throws KniffelException{
        if(scoreTableRow >= 0 && scoreTableRow <= ScoreTableRows.SCORE_TABLE_DIM-1 && playerID <= numberOfPlayers) {
            return scores[scoreTableRow][playerID - 1];
        } else {
            throw new KniffelException();
        }
    }

    @Override
    public int getNextPlayer() {
        if(playedTurns == ScoreTableRows.SETTABLE_SCORE_TABLE_ROWS_NUMBER * numberOfPlayers) {
            return -1;
        }

        return nextPlayer;
    }

    @Override
    public String[] getPlayerNames() {
        return playerNames;
    }

    @Override
    public int getOwnPlayerID() {
        return ownPlayerID;
    }

    @Override
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    private void incrementNextPlayer() {
        if(nextPlayer == numberOfPlayers) {
            nextPlayer = 1;
        } else {
            nextPlayer++;
        }
    }

    //Checks if Bonus, Upper Total, Lower Total or Grand Total need to be set
    private void checkForbiddenScoreTableRows(int playerID) {

        playerID--;

        //Checks if the upper block total needs to be set
        if(scores[ScoreTableRows.UPPER_BLOCK_TOTAL][playerID] == UNDEFINED && scores[ScoreTableRows.ONES][playerID] != UNDEFINED && scores[ScoreTableRows.TWOS][playerID] != UNDEFINED && scores[ScoreTableRows.THREES][playerID] != UNDEFINED && scores[ScoreTableRows.FOURS][playerID] != UNDEFINED && scores[ScoreTableRows.FIVES][playerID] != UNDEFINED && scores[ScoreTableRows.SIXES][playerID] != UNDEFINED ) {
            int upperTotalWithoutBonus = scores[ScoreTableRows.ONES][playerID] + scores[ScoreTableRows.TWOS][playerID] + scores[ScoreTableRows.THREES][playerID] + scores[ScoreTableRows.FOURS][playerID] + scores[ScoreTableRows.FIVES][playerID] + scores[ScoreTableRows.SIXES][playerID];
            if(upperTotalWithoutBonus >= 63){
                scores[ScoreTableRows.BONUS][playerID] = 35;
            } else {
                scores[ScoreTableRows.BONUS][playerID] = 0;
            }
            scores[ScoreTableRows.UPPER_BLOCK_TOTAL][playerID] = scores[ScoreTableRows.BONUS][playerID] + upperTotalWithoutBonus;
            if(scores[ScoreTableRows.LOWER_BLOCK_TOTAL][playerID] != UNDEFINED) {
                scores[ScoreTableRows.GRAND_TOTAL][playerID] = scores[ScoreTableRows.LOWER_BLOCK_TOTAL][playerID] + scores[ScoreTableRows.UPPER_BLOCK_TOTAL][playerID];
            }
        }

        //Checks if the lower block total needs to be set
        if(scores[ScoreTableRows.LOWER_BLOCK_TOTAL][playerID] == UNDEFINED && scores[ScoreTableRows.THREE_OF_A_KIND][playerID] != UNDEFINED && scores[ScoreTableRows.FOUR_OF_A_KIND][playerID] != UNDEFINED && scores[ScoreTableRows.FULL_HOUSE][playerID] != UNDEFINED && scores[ScoreTableRows.SMALL_STRAIGHT][playerID] != UNDEFINED && scores[ScoreTableRows.LARGE_STRAIGHT][playerID] != UNDEFINED && scores[ScoreTableRows.KNIFFEL][playerID] != UNDEFINED && scores[ScoreTableRows.CHANCE][playerID] != UNDEFINED) {
            int lowerTotal = scores[ScoreTableRows.THREE_OF_A_KIND][playerID] + scores[ScoreTableRows.FOUR_OF_A_KIND][playerID] + scores[ScoreTableRows.FULL_HOUSE][playerID] + scores[ScoreTableRows.SMALL_STRAIGHT][playerID] + scores[ScoreTableRows.LARGE_STRAIGHT][playerID] + scores[ScoreTableRows.KNIFFEL][playerID] + scores[ScoreTableRows.CHANCE][playerID];
            scores[ScoreTableRows.LOWER_BLOCK_TOTAL][playerID] = lowerTotal;
            if(scores[ScoreTableRows.UPPER_BLOCK_TOTAL][playerID] != UNDEFINED) {
                scores[ScoreTableRows.GRAND_TOTAL][playerID] = scores[ScoreTableRows.LOWER_BLOCK_TOTAL][playerID] + scores[ScoreTableRows.UPPER_BLOCK_TOTAL][playerID];
            }
        }
    }

    private int countPlayedTurns() {
        int playedTurns = 0;

        for(int i = 0; i < numberOfPlayers; i++) {
            for(int j = ScoreTableRows.ONES; j <ScoreTableRows.SCORE_TABLE_DIM; j++) {
                if(j != ScoreTableRows.BONUS && j != ScoreTableRows.UPPER_BLOCK_TOTAL && j != ScoreTableRows.LOWER_BLOCK_TOTAL && j != ScoreTableRows.GRAND_TOTAL) {
                    if(scores[j][i] != -1) {
                        playedTurns++;
                    }
                }
            }
        }

        return playedTurns;
    }
}
