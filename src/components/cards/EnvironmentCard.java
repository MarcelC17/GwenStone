package components.cards;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public final class EnvironmentCard  implements DefaultCard {

    @Getter @Setter private String description;

    @Getter @Setter private int mana;

    @Getter @Setter private ArrayList<String> colors;

    @Getter @Setter private String name;

    @Getter @Setter private int frozenRoundCounter;

    public EnvironmentCard(final String description, final int mana, final ArrayList<String> colors,
                           final String name) {
        setDescription(description);
        setMana(mana);
        setName(name);
        setColors(colors);
    }

    @Override
    public ObjectNode getCard(final ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        ArrayNode array = mapper.valueToTree(this.getColors());
        node.put("mana", this.getMana());
        node.put("description", this.getDescription());
        node.putArray("colors").addAll(array);
        node.put("name", this.getName());
        return node;
    }

    public String getType() {
        return "Environment";
    }

    @Override
    public int getHealth() {
        return 0;
    }

    @Override
    public void setHealth(final int health) {
        System.out.println("No Health");
    }

    @Override
    public String getStatus() {
        return "Environment";
    }
    @Override
    public void setStatus(final String string) {
        System.out.println("EnvironmentCard");
    }

    @Override
    public int getAttackDamage() {
        return 0;
    }

    @Override
    public void setAttackDamage(final int attackDamage) {
        System.out.println("EnvironmentCard");
    }
}
