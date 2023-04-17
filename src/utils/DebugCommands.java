package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import components.Player;
import components.Table;
import components.cards.DefaultCard;
import java.util.ArrayList;

public final class DebugCommands {

    /**
     * returns player's deck
     */
    public static ObjectNode getDeck(final ArrayList<Player> players, final int idx) {

        Player playerToShowCards;
        playerToShowCards = players.get(idx - 1);


        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getPlayerDeck");
        commandNode.put("playerIdx", idx);

        ArrayList<ObjectNode> deckOutputToJson = new ArrayList<>();

        for (DefaultCard cardToGet : playerToShowCards.getGameDeck().getDeck()) {
            deckOutputToJson.add(cardToGet.getCard(mapper));
        }
        ArrayNode array = mapper.valueToTree(deckOutputToJson);
        commandNode.putArray("output").addAll(array);
        return commandNode;
    }

    /**
     * returns player hero
     */
    public static ObjectNode getHero(final ArrayList<Player> players, final int idx) {

        Player playerToGetHero;
        playerToGetHero = players.get(idx - 1);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getPlayerHero");
        commandNode.put("playerIdx", idx);
        commandNode.set("output", playerToGetHero.getPlayerHeroCard().getHero(mapper));
        return commandNode;
    }

    /**
     * returns player turn
     */
    public static ObjectNode getTurn(final int turn) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getPlayerTurn");
        commandNode.put("output", turn);
        return commandNode;
    }

    /**
     * returns player's hand
     */
    public static ObjectNode getHand(final Player player) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getCardsInHand");
        commandNode.put("playerIdx", player.getPlayerIdx());

        ArrayList<ObjectNode> playerHand = new ArrayList<>();

        for (DefaultCard cardToGet : player.getHand()) {
            playerHand.add(cardToGet.getCard(mapper));
        }
        ArrayNode array = mapper.valueToTree(playerHand);
        commandNode.putArray("output").addAll(array);
        return commandNode;
    }

    /**
     * returns player mana
     */
    public static ObjectNode getMana(final Player player) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getPlayerMana");
        commandNode.put("playerIdx", player.getPlayerIdx());
        commandNode.put("output", player.getMana());
        return commandNode;
    }

    /**
     * returns cards on the table
     */
    public static ObjectNode getTable(final Table table) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getCardsOnTable");



        ArrayList<ArrayList<ObjectNode>> tableCards = new ArrayList<>();

        int rowCount = 0;

        while (rowCount < Constants.MAXROWS) {
            ArrayList<ObjectNode> initList = new ArrayList<>();
            tableCards.add(initList);
            rowCount++;
        }

        rowCount = 0;
        //adds cards from to array of nodes
        for (ArrayList<DefaultCard> rows : table.getTable()) {
            for (DefaultCard card : rows) {
                tableCards.get(rowCount).add(card.getCard(mapper));
            }
            rowCount++;
        }

        ArrayNode array = mapper.valueToTree(tableCards);
        commandNode.putArray("output").addAll(array);
        return commandNode;

    }

    /**
     * returns card at particular position
     */
    public static ObjectNode getCard(final Table table, final int x, final int y) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();

        commandNode.put("command", "getCardAtPosition");
        commandNode.put("x", x);
        commandNode.put("y", y);

        if (y < table.getTable().get(x).size()) {
            commandNode.set("output", table.getTable().get(x).get(y).getCard(mapper));
        } else {
            commandNode.put("output", "No card available at that position.");
        }


        return commandNode;
    }

    /**
     * returns environment cards in player's hand
     */
    public static ObjectNode getEnvironmentCards(final Player player) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getEnvironmentCardsInHand");
        commandNode.put("playerIdx", player.getPlayerIdx());

        ArrayList<ObjectNode> environmentCardsList = new ArrayList<>();

        for (DefaultCard envCardToGet : player.getHand()) {
            if (envCardToGet.getType().equals("Environment")) {
                environmentCardsList.add(envCardToGet.getCard(mapper));
            }
        }

        ArrayNode array = mapper.valueToTree(environmentCardsList);
        commandNode.putArray("output").addAll(array);
        return commandNode;
    }

    /**
     * returns frozen card from the table
     */
    public static ObjectNode getFrozenCards(final Table table) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "getFrozenCardsOnTable");

        ArrayList<ObjectNode> frozenCards = new ArrayList<>();

        for (ArrayList<DefaultCard> row : table.getTable()) {
            for (DefaultCard card : row) {
                if (card.getStatus().equals("Frozen")) {
                    frozenCards.add(card.getCard(mapper));
                }
            }
        }

        ArrayNode array = mapper.valueToTree(frozenCards);
        commandNode.putArray("output").addAll(array);
        return commandNode;
    }
}


