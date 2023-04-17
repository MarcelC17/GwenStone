package utils;

import components.Deck;
import components.cards.DefaultCard;
import components.cards.EnvironmentCard;
import components.cards.MinionCard;
import fileio.CardInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class StartGameUtils {
    /**
     * sets cards and player decks
     */
    public static void setPlayerDeck(final ArrayList<DefaultCard> playerDeck,
                               final ArrayList<CardInput> deckInput) {
            for (CardInput cardsInDeck : deckInput) {
                //makes decks depending on card type
                switch (cardsInDeck.getName()) {
                    case "Sentinel", "Warden", "Berserker", "Goliath",
                            "The Cursed One", "Miraj", "Disciple", "The Ripper" -> {
                        MinionCard minionCardToDeck = new MinionCard(cardsInDeck.getHealth(),
                                cardsInDeck.getAttackDamage(),
                                cardsInDeck.getDescription(), cardsInDeck.getMana(),
                                cardsInDeck.getColors(), cardsInDeck.getName());
                        playerDeck.add(minionCardToDeck);
                    }
                    case "Firestorm", "Winterfell", "Heart Hound" -> {
                        EnvironmentCard environmentToDeck = new EnvironmentCard(cardsInDeck.getDescription(),
                                cardsInDeck.getMana(),
                                cardsInDeck.getColors(), cardsInDeck.getName());
                        playerDeck.add(environmentToDeck);
                    }
                    default -> System.out.println("no such card");
                }
            }

    }

    /**
     * shuffles player deck using seed
     */
    public static void shuffleDeck(Deck toShuffle, final int seed) {
        Collections.shuffle(toShuffle.getDeck(), new Random(seed));
    }

}
