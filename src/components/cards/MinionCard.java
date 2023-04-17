package components.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public final class MinionCard implements DefaultCard {
    @Getter @Setter private int health;

    @Getter @Setter private int attackDamage;

    @Getter @Setter private String description;

    @Getter @Setter private int mana;

    @Getter @Setter private ArrayList<String> colors;

    @Getter @Setter private String name;

    @Getter @Setter private String status;

    @Getter @Setter private int frozenRoundCounter;

    public MinionCard(final int health, final int attackDamage, final String description,
                      final int mana, final ArrayList<String> colors, final String name) {
        setHealth(health);
        setAttackDamage(attackDamage);
        setDescription(description);
        setMana(mana);
        setName(name);
        setColors(colors);
        setStatus("Normal");
        setFrozenRoundCounter(0);
    }

    @Override
    public ObjectNode getCard(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("mana", this.getMana());
        node.put("attackDamage", this.getAttackDamage());
        node.put("health", this.getHealth());
        node.put("description", this.getDescription());

        ArrayNode arrayNode = node.putArray("colors");
        for (String color : this.getColors()) {
            arrayNode.add(color);
        }

        node.put("name", this.getName());
        return node;
    }

    @Override
    public String getType() {
        return "Minion";
    }
}
