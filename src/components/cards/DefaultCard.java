package components.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represents a card from the deck
 */

public interface DefaultCard {
    /**
     *returns card type
     */
    String getType();

    /**
     * returns card for json mapping
     */
    ObjectNode getCard(ObjectMapper mapper);

    /**
     * //returns card name
     */
    String getName();

    /**
     * returns minion health
     */
    int getHealth();

    /**
     * sets minion health
     */
    void setHealth(int health);

    /**
     * returns card status
     */
    String getStatus();

    /**
     * sets card status
     */
    void setStatus(String status);

    /**
     * returns card mana cost
     */
    int getMana();

    /**
     * returns minion damage
     */
    int getAttackDamage();

    /**
     * sets minion attack damage
     */
    void setAttackDamage(int damage);

    /**
     * returns number of rounds a minion was frozen
     */
    int getFrozenRoundCounter();

    /**
     * sets number of rounds a minion was frozen
     */
    void setFrozenRoundCounter(int roundNumber);
}
