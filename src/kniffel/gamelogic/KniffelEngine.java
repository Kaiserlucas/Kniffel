package kniffel.gamelogic;

import kniffel.KniffelFacade;
import kniffel.data.ScoreTable;
import kniffel.data.ScoreTableImpl;
import kniffel.data.ScoreTableRowNames;
import kniffel.data.ScoreTableRows;
import kniffel.protocolBinding.KniffelSender;

import java.io.IOException;
import java.util.Arrays;
import java.util.Observable;

public class KniffelEngine extends Observable implements KniffelFacade, KniffelReceiver {

    private KniffelSender sender;
    private ScoreTable scoreTable;
    private GameState state;
    private int rollsLeft;
    private int[] diceValues;
    private boolean[] diceIsSetAside;
    private static final int NUMBER_OF_DICE = 5;

    //Constructor used to create a new game
    public KniffelEngine(int numberOfPlayers, String[] playerNames, int ownPlayerID, KniffelSender sender) {
        this.sender = sender;
        this.scoreTable = new ScoreTableImpl(numberOfPlayers, playerNames, ownPlayerID);
        this.diceValues = new int[NUMBER_OF_DICE];
        this.diceIsSetAside = new boolean[NUMBER_OF_DICE];
        initializeNewTurn();

        if(ownPlayerID == 1) {
            this.state = GameState.OwnTurn;
        } else {
            this.state = GameState.OtherPlayerTurn;
        }

    }

    //Constructor used to load a saved game
    public KniffelEngine(ScoreTable scoreTable, KniffelSender sender) {
        this.sender = sender;
        this.scoreTable = scoreTable;
        this.rollsLeft = 3;
        this.diceValues = new int[NUMBER_OF_DICE];
        this.diceIsSetAside = new boolean[NUMBER_OF_DICE];
        initializeNewTurn();

        if(scoreTable.getOwnPlayerID() == scoreTable.getNextPlayer()) {
            this.state = GameState.OwnTurn;
        } else {
            this.state = GameState.OtherPlayerTurn;
        }
    }

    //////////////////////////////////////////////////////////////
    //                                                          //
    //                       Facade Methods                     //
    //                                                          //
    //////////////////////////////////////////////////////////////

    @Override
    public void rollDice() throws IllegalStateException, IOException {
        if(state == GameState.OwnTurn && rollsLeft > 0) {
            for(int i = 0; i < NUMBER_OF_DICE; i++) {
                if(!diceIsSetAside[i]) {
                    diceValues[i] = ((int) (Math.random()*6))+1;
                }
            }
            sender.sendRollDice(diceValues, scoreTable.getOwnPlayerID());
            rollsLeft--;
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void endTurn(int tableRow) throws IllegalStateException, KniffelException, IOException {
        if(state == GameState.OwnTurn && rollsLeft != 3) {
            int score = KniffelScoreCalculator.calculateScore(tableRow, diceValues);
            scoreTable.setScore(tableRow, score, scoreTable.getOwnPlayerID());
            sender.sendEndTurn(tableRow, score, scoreTable.getOwnPlayerID());
            initializeNewTurn();
            if(scoreTable.getNextPlayer() == -1) {
                state = GameState.GameEnded;
            } else {
                state = GameState.OtherPlayerTurn;
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void changeDiceState(int diceIndex) throws IllegalStateException, IOException {
        if(state == GameState.OwnTurn && rollsLeft != 3) {
            diceIsSetAside[diceIndex] = !diceIsSetAside[diceIndex];
            sender.sendChangeDiceState(diceIndex, scoreTable.getOwnPlayerID());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void endGame() throws IOException {
        this.state = GameState.GameEnded;
        sender.sendEndGame(scoreTable.getOwnPlayerID());
    }

    @Override
    public ScoreTable getScoreTable() {
        return scoreTable;
    }

    @Override
    public int getScore(int scoreTableRow, int playerID) throws KniffelException {
        return scoreTable.getScore(scoreTableRow, playerID);
    }

    @Override
    public String getScoreTableRowName(int scoreTableRow) throws KniffelException {
        switch(scoreTableRow) {
            case ScoreTableRows.ONES:
                return ScoreTableRowNames.Einser.name;
            case ScoreTableRows.TWOS:
                return ScoreTableRowNames.Zweier.name;
            case ScoreTableRows.THREES:
                return ScoreTableRowNames.Dreier.name;
            case ScoreTableRows.FOURS:
                return ScoreTableRowNames.Vierer.name;
            case ScoreTableRows.FIVES:
                return ScoreTableRowNames.Fuenfer.name;
            case ScoreTableRows.SIXES:
                return ScoreTableRowNames.Sechser.name;
            case ScoreTableRows.BONUS:
                return ScoreTableRowNames.Bonus.name;
            case ScoreTableRows.UPPER_BLOCK_TOTAL:
                return ScoreTableRowNames.Oben.name;
            case ScoreTableRows.THREE_OF_A_KIND:
                return ScoreTableRowNames.Dreierpasch.name;
            case ScoreTableRows.FOUR_OF_A_KIND:
                return ScoreTableRowNames.Viererpasch.name;
            case ScoreTableRows.FULL_HOUSE:
                return ScoreTableRowNames.FullHouse.name;
            case ScoreTableRows.SMALL_STRAIGHT:
                return ScoreTableRowNames.KleineStrasse.name;
            case ScoreTableRows.LARGE_STRAIGHT:
                return ScoreTableRowNames.GrosseStrasse.name;
            case ScoreTableRows.KNIFFEL:
                return ScoreTableRowNames.Kniffel.name;
            case ScoreTableRows.CHANCE:
                return ScoreTableRowNames.Chance.name;
            case ScoreTableRows.LOWER_BLOCK_TOTAL:
                return ScoreTableRowNames.Unten.name;
            case ScoreTableRows.GRAND_TOTAL:
                return ScoreTableRowNames.Gesamt.name;
            default:
                throw new KniffelException();
        }
    }

    @Override
    public int[] getDiceValues() {
        return diceValues;
    }

    @Override
    public boolean[] areDicesSetAside() {
        return diceIsSetAside;
    }

    @Override
    public int getActivePlayer() {
        return scoreTable.getNextPlayer();
    }

    @Override
    public String[] getPlayerNames() {
        return scoreTable.getPlayerNames();
    }

    @Override
    public int getOwnPlayerID() {
        return scoreTable.getOwnPlayerID();
    }

    @Override
    public int getRollsRemaining() {
        return rollsLeft;
    }

    @Override
    public int getNumberOfPlayers() {
        return scoreTable.getNumberOfPlayers();
    }

    @Override
    public GameState getState() {
        return state;
    }

    //////////////////////////////////////////////////////////////
    //                                                          //
    //                       Receiver Methods                   //
    //                                                          //
    //////////////////////////////////////////////////////////////

    @Override
    public void receiveRollDice(int[] newValues, int playerID) throws IllegalStateException, IOException {
        if(state == GameState.OtherPlayerTurn && rollsLeft > 0 && scoreTable.getNextPlayer() == playerID) {
            this.diceValues = newValues;
            rollsLeft--;
            //Sends command to other players if this is the host
            if(playerID != 1 && scoreTable.getOwnPlayerID() == 1) {
                sender.sendRollDice(newValues, playerID);
            }
            setChanged();
            notifyObservers();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void receiveEndTurn(int scoreTableRow, int score, int playerID) throws IllegalStateException, KniffelException, IOException {
        if(state == GameState.OtherPlayerTurn && rollsLeft != 3 && scoreTable.getNextPlayer() == playerID) {
            scoreTable.setScore(scoreTableRow, score, playerID);
            initializeNewTurn();
            if(scoreTable.getNextPlayer() == scoreTable.getOwnPlayerID()) {
                state = GameState.OwnTurn;
            } else if(scoreTable.getNextPlayer() == -1) {
                state = GameState.GameEnded;
            }
            //Sends command to other players if this is the host
            if(playerID != 1 && scoreTable.getOwnPlayerID() == 1) {
                sender.sendEndTurn(scoreTableRow, score, playerID);
            }
            setChanged();
            notifyObservers();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void receiveChangeDiceState(int diceIndex, int playerID) throws IllegalStateException, IOException {
        if(state == GameState.OtherPlayerTurn && scoreTable.getNextPlayer() == playerID && rollsLeft != 3) {
            this.diceIsSetAside[diceIndex] = !diceIsSetAside[diceIndex];
            //Sends command to other players if this is the host
            if(playerID != 1 && scoreTable.getOwnPlayerID() == 1) {
                sender.sendChangeDiceState(diceIndex, playerID);
            }
            setChanged();
            notifyObservers();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void receiveEndGame(int playerID) throws IOException {
        this.state = GameState.GameEnded;
        if(playerID != 1 && scoreTable.getOwnPlayerID() == 1) {
            sender.sendEndGame(playerID);
        }
        setChanged();
        notifyObservers();
    }

    //////////////////////////////////////////////////////////////
    //                                                          //
    //                       Other Methods                      //
    //                                                          //
    //////////////////////////////////////////////////////////////

    private void initializeNewTurn() {
        rollsLeft = 3;
        Arrays.fill(diceValues, -1);
        Arrays.fill(diceIsSetAside, false);
    }
}
