package components.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import utils.Constants;

import java.util.ArrayList;


    public final class HeroCard {

    @Getter @Setter private int health;

    @Getter @Setter private String description;

    @Getter @Setter private int mana;

    @Getter @Setter private ArrayList<String> colors;

    @Getter @Setter private String name;

    @Getter @Setter private String status;
    public HeroCard(final String description, final int mana, final ArrayList<String> colors,
                    final String name) {
        this.setColors(colors);
        this.setName(name);
        this.setDescription(description);
        this.setMana(mana);
        this.setHealth(Constants.INITIALHERROHEALTH);
        this.setStatus("Normal");
    }

        /**
         * gets mapper as input and returns card as ObjecNnode for JSON output
         */
    public ObjectNode getHero(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("mana", this.getMana());
        node.put("description", this.getDescription());
        ArrayNode array = mapper.valueToTree(this.getColors());
        node.putArray("colors").addAll(array);
        node.put("name", this.getName());
        node.put("health", this.getHealth());
        return node;
    }
}
