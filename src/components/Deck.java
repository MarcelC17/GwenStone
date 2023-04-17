package components;

import components.cards.DefaultCard;
import fileio.CardInput;
import lombok.Getter;
import lombok.Setter;
import utils.StartGameUtils;
import java.util.ArrayList;

public class Deck {
    @Getter @Setter private ArrayList<DefaultCard> deck = new ArrayList<>();

    public Deck(final ArrayList<CardInput> deckInput) {
        StartGameUtils.setPlayerDeck(deck, deckInput);
    }

    public Deck(final Deck deckToCopy) {
        this.deck = new ArrayList<>();
        deck.addAll(deckToCopy.getDeck());
    }
}
