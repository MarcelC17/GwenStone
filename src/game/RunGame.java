package game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import components.Player;
import components.Table;
import fileio.ActionsInput;
import fileio.GameInput;
import fileio.Input;
import lombok.Getter;
import lombok.Setter;
import utils.DebugCommands;
import utils.GameCommands;

import java.util.ArrayList;

//
public class RunGame {
    @Setter @Getter private Input inputData;
    @Setter @Getter private Player player1;
    @Setter @Getter private Player player2;
    @Setter @Getter private ArrayNode outputToJson;
    @Setter @Getter private Table gameBoard;
    @Setter @Getter private int playerTurn;

    @Setter @Getter private int manaEndTurn;

    @Setter @Getter private int turnCounter;

    @Setter @Getter private int playerOneWins;
    @Setter @Getter private int playerTwoWins;
    @Setter @Getter private int totalGamesPlayed;

    private final int player1IDX = 1;

    private final int player2IDX = 2;

    private final int maxMana = 10;

    @Setter @Getter private ObjectMapper mapper;


    public RunGame(final Input gameInput, final ArrayNode output) {

        setInputData(gameInput);
        setOutputToJson(output);
        setPlayerOneWins(0);
        setPlayerTwoWins(0);
        setTotalGamesPlayed(0);


        for (GameInput singleGameInput : gameInput.getGames()) {
            //sets players' decks
            player1 = new Player(inputData.getPlayerOneDecks(), player1IDX);

            player2 = new Player(inputData.getPlayerTwoDecks(), player2IDX);

            setPlayerTurn(singleGameInput.getStartGame().getStartingPlayer());
            setGameBoard(new Table());
            setMapper(new ObjectMapper());
            setTurnCounter(0);
            setManaEndTurn(1);
            getPlayer1().setMana(this.getManaEndTurn());
            getPlayer2().setMana(this.getManaEndTurn());

            startGame(singleGameInput);
        }



    }

    /**
     * sets players game decks and heroes
     */
    public void startGame(final GameInput singleGameInput) {
        getPlayer1().setPlayerNewGame(singleGameInput.getStartGame().getPlayerOneHero(),
                singleGameInput.getStartGame().getShuffleSeed(),
                singleGameInput.getStartGame().getPlayerOneDeckIdx());

       getPlayer2().setPlayerNewGame(singleGameInput.getStartGame().getPlayerTwoHero(),
                singleGameInput.getStartGame().getShuffleSeed(),
                singleGameInput.getStartGame().getPlayerTwoDeckIdx());

        runActions(singleGameInput);
    }

    /**
     * executes game commands
     */
    public void runActions(final GameInput singleGameInput) {

        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);


        for (ActionsInput actInput : singleGameInput.getActions()) {
            ObjectNode commandOutput = mapper.createObjectNode();
            switch (actInput.getCommand()) {
                case "getPlayerDeck":
                    commandOutput = DebugCommands.getDeck(players, actInput.getPlayerIdx());
                    break;
                case "getPlayerHero":
                    commandOutput = DebugCommands.getHero(players, actInput.getPlayerIdx());
                    break;
                case "getPlayerTurn":
                    commandOutput = DebugCommands.getTurn(playerTurn);
                    break;
                case "endPlayerTurn":
                    turnCounter++;
                    playerTurn = GameCommands.endTurn(playerTurn);
                    //players receive mana every 2 turns
                    if (turnCounter % 2 == 0) {
                        if (manaEndTurn < maxMana) {
                            manaEndTurn++;
                        }
                        for (Player eachPlayer : players) {
                            eachPlayer.getCard();
                            eachPlayer.setMana(eachPlayer.getMana() + manaEndTurn);
                        }
                    }
                    //resets hero cards which attacked
                    for (Player player : players) {
                        player.getPlayerHeroCard().setStatus("Normal");
                    }

                    gameBoard.setFrozenCards();

                    continue;
                case "placeCard":
                    commandOutput = players.get(playerTurn - 1).placeCard(gameBoard,
                            actInput.getHandIdx());
                    break;
                case "getCardsInHand":
                    commandOutput = DebugCommands.getHand(players.get(actInput.getPlayerIdx() - 1));
                    break;
                case "getPlayerMana":
                    commandOutput = DebugCommands.getMana(players.get(actInput.getPlayerIdx() - 1));
                    break;
                case "getCardsOnTable":
                    commandOutput = DebugCommands.getTable(gameBoard);
                    break;
                case "getCardAtPosition":
                    commandOutput = DebugCommands.getCard(gameBoard,
                            actInput.getX(), actInput.getY());
                    break;
                case "useEnvironmentCard":
                    commandOutput = GameCommands.useEnvironment(gameBoard,
                            players.get(playerTurn - 1), actInput.getAffectedRow(),
                            actInput.getHandIdx());
                    break;
                case "getEnvironmentCardsInHand":
                    commandOutput = DebugCommands.getEnvironmentCards(
                            players.get(actInput.getPlayerIdx() - 1));
                    break;
                case "getFrozenCardsOnTable":
                    commandOutput = DebugCommands.getFrozenCards(gameBoard);
                    break;
                case "cardUsesAttack":
                    commandOutput = GameCommands.useAttack(gameBoard, actInput.getCardAttacker(),
                            actInput.getCardAttacked());
                    break;
                case "cardUsesAbility":
                    commandOutput = GameCommands.useAbility(gameBoard, actInput.getCardAttacker(),
                            actInput.getCardAttacked());
                    break;
                case "useAttackHero":
                    commandOutput = GameCommands.attackHero(gameBoard,
                            players, actInput.getCardAttacker());
                    //searches for dead heroes
                    for (Player player : players) {
                        if (player.getPlayerHeroCard().getHealth() <= 0
                                && player.getPlayerIdx() == 1) {
                            playerTwoWins++;
                            totalGamesPlayed++;
                        } else if (player.getPlayerHeroCard().getHealth() <= 0
                                && player.getPlayerIdx() == 2) {
                            playerOneWins++;
                            totalGamesPlayed++;
                        }
                    }
                    break;
                case "useHeroAbility":
                    commandOutput = GameCommands.useHero(gameBoard,
                            players.get(playerTurn - 1), actInput.getAffectedRow());
                    break;
                case "getTotalGamesPlayed":
                    commandOutput.put("command", "getTotalGamesPlayed");
                    commandOutput.put("output", getTotalGamesPlayed());
                    break;
                case "getPlayerOneWins":
                    commandOutput.put("command", "getPlayerOneWins");
                    commandOutput.put("output", getPlayerOneWins());
                    break;
                case "getPlayerTwoWins":
                    commandOutput.put("command", "getPlayerTwoWins");
                    commandOutput.put("output", getPlayerTwoWins());
                    break;
                default:
                    System.out.println("No such command");
            }
            //case attack and use ability commands don't generate errors
            if (commandOutput == null) {
                continue;
            }
            this.outputToJson.add(commandOutput);
        }
    }
}
