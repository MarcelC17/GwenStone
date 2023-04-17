package components;

import com.fasterxml.jackson.databind.node.ObjectNode;
import components.cards.HeroCard;
import components.cards.DefaultCard;
import fileio.CardInput;
import fileio.DecksInput;
import lombok.Getter;
import lombok.Setter;
import utils.Constants;
import utils.GameCommands;
import utils.StartGameUtils;
import java.util.ArrayList;

public final class Player {
    @Getter @Setter private int playerIdx;

    @Getter @Setter private HeroCard playerHeroCard;
    @Getter @Setter private Deck gameDeck;
    @Getter @Setter private ArrayList<Deck> playerDecks = new ArrayList<>();
    @Getter @Setter private ArrayList<DefaultCard> hand = new ArrayList<>();
    @Getter @Setter private int mana;

    /**
     * moves card from player hand to deck
     */
    public void getCard() {
        if (getGameDeck().getDeck().size() > 0) {
            hand.add(getGameDeck().getDeck().get(0));
            getGameDeck().getDeck().remove(0);
        }
    }

    public Player(final DecksInput decksInput, final int idx) {
        for (ArrayList<CardInput> singleDeckInput: decksInput.getDecks()) {
            Deck singleDeck = new Deck(singleDeckInput);
            getPlayerDecks().add(singleDeck);
        }
        setPlayerIdx(idx);
        setMana(0);
    }

    /**
     * sets hero traits and player game deck
     */
    public void setPlayerNewGame(final CardInput heroInput,
                                 final int seed, final int playerDeckId) {
        setPlayerHeroCard(new HeroCard(heroInput.getDescription(),
                heroInput.getMana(), heroInput.getColors(), heroInput.getName()));

        setGameDeck(new Deck(playerDecks.get(playerDeckId)));

        StartGameUtils.shuffleDeck(gameDeck, seed);

        getCard();

    }

    /**
     * moves card from player hand to table
     */
    public ObjectNode placeCard(final Table gameBoard, final int handId) {
        if (handId < getHand().size()) {
            DefaultCard cardToPlace = getHand().get(handId);
            int playerRow;
            int maxRow;
            //sets max row (closest to the center of the board) for player
            if (getPlayerIdx() == 1) {
                playerRow = gameBoard.getPlayer1Row();
                maxRow = 2;
            } else {
                playerRow = gameBoard.getPlayer2Row();
                maxRow = 1;
            }
            int maxCardsOnRow = Constants.MAXCARDSONROW;

            //checks for place on row
            if ((playerRow == maxRow)
                    && gameBoard.getTable().get(playerRow).size() == maxCardsOnRow) {
                return GameCommands.placeCard("noplace");

            } else if (this.getMana() >= cardToPlace.getMana()) {
                //checks if card is minion
                if (cardToPlace.getType().equals("Minion")) {
                    gameBoard.addCardOnTable(cardToPlace, getPlayerIdx() - 1);
                    getHand().remove(handId);
                    this.mana -= cardToPlace.getMana();
                } else {
                    return  GameCommands.placeCard("envcard");
                }
            } else {
                return GameCommands.placeCard("nomana");
            }
        }
        return null;

    }

}
