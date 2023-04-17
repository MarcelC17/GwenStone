package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import components.Player;
import components.Table;
import components.cards.DefaultCard;
import components.cards.HeroCard;
import fileio.Coordinates;
import java.util.ArrayList;

public class GameCommands {
    /**
     * ends player turn
     */
    public static int endTurn(final int turnIdx) {
        int turn;
        if (turnIdx == 1) {
            turn = 2;
        } else {
            turn = 1;
        }
        return turn;
    }

    /**
     * error output(not enough mana to place card on table)
     */
    public static ObjectNode placeCard(final String result) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "placeCard");

        switch (result) {
            case "envcard" -> {
                commandNode.put("handIdx", 0);
                commandNode.put("error", "Cannot place environment card on table.");
            }
            case "nomana" -> {
                commandNode.put("handIdx", 0);
                commandNode.put("error", "Not enough mana to place card on table.");
            }
            case "noplace" -> {
                commandNode.put("handIdx", 0);
                commandNode.put("error", "Cannot place card on table since row is full.");
            }
            default -> System.out.println("error");
        }
        return commandNode;
    }

    /**
     * use Environment card on a row on the table
     */
    public static ObjectNode useEnvironment(final Table table, final Player player,
                                            final int row, final int handIdx) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();
        DefaultCard cardToUse = player.getHand().get(handIdx);
        commandNode.put("command", "useEnvironmentCard");
        commandNode.put("handIdx", handIdx);
        commandNode.put("affectedRow", row);

        if (!cardToUse.getType().equals("Environment")) {
            commandNode.put("error", "Chosen card is not of type environment.");
            return commandNode;
        }

        if (player.getMana() < cardToUse.getMana()) {
            commandNode.put("error", "Not enough mana to use environment card.");
            return  commandNode;
        }

        if ((player.getPlayerIdx() == 2 && (row == 1 || row == 0))
                || (player.getPlayerIdx() == 1 && (row == 2 || row == Constants.FIRSTROWPLAYER2))) {
            commandNode.put("error", "Chosen row does not belong to the enemy.");
            return  commandNode;
        }

        if (handIdx < player.getHand().size()) {
            switch (cardToUse.getName()) {
                case "Firestorm":
                    for (DefaultCard card : table.getTable().get(row)) {
                        card.setHealth(card.getHealth() - 1);
                    }
                    break;
                case  "Winterfell":
                    for (DefaultCard card : table.getTable().get(row)) {
                        card.setStatus("Frozen");
                    }
                    break;
                case "Heart Hound":
                    DefaultCard mostHealthMinion = null;
                    int mostHealth = 0;
                    for (DefaultCard card : table.getTable().get(row)) {
                        if (mostHealth < card.getHealth()) {
                            mostHealth = card.getHealth();
                            mostHealthMinion = card;
                        }
                    }
                    int mirrorRow = switch (row) {
                        case 0 -> Constants.FIRSTROWPLAYER2;
                        case 1 -> 2;
                        case 2 -> 1;
                        default -> 0;
                    };

                    if (table.getTable().get(mirrorRow).size() == Constants.MAXCARDSONROW) {
                        commandNode.put("error",
                                "Cannot steal enemy card since the player's row is full.");
                        return commandNode;
                    }
                    table.getTable().get(mirrorRow).add(mostHealthMinion);
                    table.getTable().get(row).remove(mostHealthMinion);
                    break;
                default:
                    System.out.println("error");
            }
            table.removeDeadCards();
            player.setMana(player.getMana() - cardToUse.getMana());
            player.getHand().remove(handIdx);
            return null;
        }
        return commandNode;
    }

    /**
     * minion card attack action on a card on the table
     */
    public static ObjectNode useAttack(final Table table,
                                       final Coordinates attacker, final Coordinates attacked) {

        DefaultCard attackerCard = table.getTable().get(attacker.getX()).get(attacker.getY());
        DefaultCard attackedCard = table.getTable().get(attacked.getX()).get(attacked.getY());
        int attackDamage = attackerCard.getAttackDamage();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();

        ObjectNode coordAttackerNode = mapper.createObjectNode();
        ObjectNode coordAttackedNode = mapper.createObjectNode();

        commandNode.put("command", "cardUsesAttack");

        coordAttackerNode.put("x", attacker.getX());
        coordAttackerNode.put("y", attacker.getY());
        commandNode.set("cardAttacker", coordAttackerNode);

        coordAttackedNode.put("x", attacked.getX());
        coordAttackedNode.put("y", attacked.getY());
        commandNode.set("cardAttacked", coordAttackedNode);

        boolean isFirstPlayer = attacker.getX() == 0 || attacker.getX() == 1;
        boolean isSecondPlayer = attacker.getX() == 2 || attacker.getX() == Constants.FIRSTROWPLAYER2;
        boolean firstPlayerAttacked = attacked.getX() == 1 || attacked.getX() == 0;
        boolean secondPlayerAttacked = attacked.getX() == 2 || attacked.getX() == Constants.FIRSTROWPLAYER2;

        if ((isFirstPlayer && firstPlayerAttacked) || (isSecondPlayer && secondPlayerAttacked)) {
            commandNode.put("error", "Attacked card does not belong to the enemy.");
            return  commandNode;
        }

        if (attackerCard.getStatus().equals("Attacked")) {
            commandNode.put("error", "Attacker card has already attacked this turn.");
            return  commandNode;
        }



        if (attackerCard.getStatus().equals("Frozen")) {
            commandNode.put("error", "Attacker card is frozen.");
            return  commandNode;
        }


        //checks for tank cards on enemy rows
        if ((!attackedCard.getName().equals("Goliath"))
                && (!attackedCard.getName().equals("Warden"))) {
            DefaultCard card = GameCommands.findTank(table, attacked);
            if (card != null) {
                commandNode.put("error", "Attacked card is not of type 'Tank'.");
                return  commandNode;
            }
        }

        attackerCard.setStatus("Attacked");
        attackedCard.setHealth(attackedCard.getHealth() - attackDamage);

        table.removeDeadCards();
        return null;
    }

    /**
     * finds tank on attacked player's rows
     */
    public static DefaultCard findTank(final Table table, final Coordinates attacked) {

        DefaultCard tankCard;
        //check for tanks on player 2 rows
        if (attacked.getX() == 0 || attacked.getX() == 1) {
            for (DefaultCard card : table.getTable().get(1)) {
                if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
                    tankCard = card;
                    return tankCard;
                }
            }
        } else { //check for tanks on player 1 rows
            for (DefaultCard card : table.getTable().get(2)) {
                if (card.getName().equals("Goliath") || card.getName().equals("Warden")) {
                    tankCard = card;
                    return  tankCard;
                }
            }
        }

        return null;

    }

    /**
     * apply ability of special ability card on a card on the table
     */
    public static ObjectNode useAbility(final Table table, final Coordinates attacker,
                                        final Coordinates attacked) {

        DefaultCard attackerCard = table.getTable().get(attacker.getX()).get(attacker.getY());
        DefaultCard attackedCard = table.getTable().get(attacked.getX()).get(attacked.getY());
        String attackedName = attackedCard.getName();
        String attackerName = attackerCard.getName();
        int attackedHealth;

        boolean firstPlayerAttacked = attacked.getX() == 1 || attacked.getX() == 0;
        boolean isFirstPlayer = attacker.getX() == 0 || attacker.getX() == 1;
        boolean isSecondPlayer = attacker.getX() == 2 || attacker.getX() == Constants.FIRSTROWPLAYER2;
        boolean secondPlayerAttacked = attacked.getX() == 2 || attacked.getX() == Constants.FIRSTROWPLAYER2;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode commandNode = mapper.createObjectNode();

        ObjectNode coordAttackerNode = mapper.createObjectNode();
        ObjectNode coordAttackedNode = mapper.createObjectNode();

        commandNode.put("command", "cardUsesAbility");

        coordAttackerNode.put("x", attacker.getX());
        coordAttackerNode.put("y", attacker.getY());
        commandNode.set("cardAttacker", coordAttackerNode);

        coordAttackedNode.put("y", attacked.getY());
        coordAttackedNode.put("x", attacked.getX());
        commandNode.set("cardAttacked", coordAttackedNode);

        if (attackerCard.getStatus().equals("Frozen")) {
            commandNode.put("error", "Attacker card is frozen.");
            return  commandNode;
        }

        if (attackerCard.getStatus().equals("Attacked")) {
            commandNode.put("error", "Attacker card has already attacked this turn.");
            return  commandNode;
        }
        if (attackerName.equals("Disciple") && ((isFirstPlayer && secondPlayerAttacked)
                || (isSecondPlayer && firstPlayerAttacked))) {
            commandNode.put("error", "Attacked card does not belong to the current player.");
            return  commandNode;
        }

        if ((attackerName.equals("Miraj") || attackerName.equals("The Cursed One")
                || attackerName.equals("The Ripper"))
                && (isFirstPlayer && firstPlayerAttacked)
                || (isSecondPlayer && secondPlayerAttacked)) {
            commandNode.put("error", "Attacked card does not belong to the enemy.");
            return  commandNode;
        }

        if ((!attackedName.equals("Goliath")) && (!attackedName.equals("Warden"))
                && (attackerName.equals("Miraj")
                || attackerName.equals("The Ripper") || attackerName.equals("The Cursed One"))
        ) {
            DefaultCard card = GameCommands.findTank(table, attacked);
            if (card != null) {
                commandNode.put("error", "Attacked card is not of type 'Tank'.");
                return  commandNode;
            }
        }
        //minions use special ability
        switch (attackerCard.getName()) {
            case "The Ripper" -> {
                attackedCard.setAttackDamage(attackedCard.getAttackDamage() - 2);
                if (attackedCard.getAttackDamage() < 0) {
                    attackedCard.setAttackDamage(0);
                }
            }
            case "Miraj" -> {
                attackedHealth = attackedCard.getHealth();
                attackedCard.setHealth(attackerCard.getHealth());
                attackerCard.setHealth(attackedHealth);
            }
            case "The Cursed One" -> {
                attackedHealth = attackedCard.getHealth();
                attackedCard.setHealth(attackedCard.getAttackDamage());
                attackedCard.setAttackDamage(attackedHealth);
            }
            case "Disciple" -> attackedCard.setHealth(attackedCard.getHealth() + 2);
            default -> System.out.println("error");
        }

        table.removeDeadCards();
        attackerCard.setStatus("Attacked");
        return null;
    }

    /**
     * minion card attacks hero
     */
    public static ObjectNode attackHero(final Table table, final ArrayList<Player> players,
                                        final Coordinates attacker) {
        DefaultCard attackerCard = table.getTable().get(attacker.getX()).get(attacker.getY());
        HeroCard attackedHero;
        int attackedPlayerIdx;

        Coordinates findAttackedTank = new Coordinates();
        findAttackedTank.setY(0);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode gameResult = mapper.createObjectNode();
        ObjectNode commandNode = mapper.createObjectNode();
        ObjectNode coordAttackerNode = mapper.createObjectNode();

        commandNode.put("command", "useAttackHero");
        coordAttackerNode.put("x", attacker.getX());
        coordAttackerNode.put("y", attacker.getY());
        commandNode.set("cardAttacker", coordAttackerNode);

        if (attackerCard.getStatus().equals("Frozen")) {
            commandNode.put("error", "Attacker card is frozen.");
            return  commandNode;
        }

        if (attackerCard.getStatus().equals("Attacked")) {
            commandNode.put("error", "Attacker card has already attacked this turn.");
            return  commandNode;
        }
        //set tank row coordinates to use in findTank
        if ((attacker.getX() == 0 || attacker.getX() == 1)) {
            attackedPlayerIdx = 0;
            findAttackedTank.setX(2);
        } else {
            attackedPlayerIdx = 1;
            findAttackedTank.setX(0);
        }

        if (GameCommands.findTank(table, findAttackedTank) != null) {
                commandNode.put("error", "Attacked card is not of type 'Tank'.");
                return  commandNode;
        }


        attackedHero = players.get(attackedPlayerIdx).getPlayerHeroCard();
        attackedHero.setHealth(attackedHero.getHealth() - attackerCard.getAttackDamage());
        attackerCard.setStatus("Attacked");
        //checks if hero is dead
        if (attackedHero.getHealth() <= 0) {
            if (attackedPlayerIdx == 1) {
                gameResult.put("gameEnded", "Player one killed the enemy hero.");
            } else {
                gameResult.put("gameEnded", "Player two killed the enemy hero.");
            }
            return gameResult;
        }

        return null;
    }

    /**
     * hero uses ability on a row
     */
    public static ObjectNode useHero(final Table table, final Player player, final int row) {
        ObjectMapper mapper = new ObjectMapper();
        HeroCard hero = player.getPlayerHeroCard();
        boolean isFirstPlayer = player.getPlayerIdx() - 1 == 0;
        boolean isSecondPlayer = player.getPlayerIdx() - 1 == 1;

        ObjectNode commandNode = mapper.createObjectNode();
        commandNode.put("command", "useHeroAbility");
        commandNode.put("affectedRow", row);

        if (player.getMana() < hero.getMana()) {
            commandNode.put("error", "Not enough mana to use hero's ability.");
            return commandNode;
        }

        if (hero.getStatus().equals("Attacked")) {
            commandNode.put("error", "Hero has already attacked this turn.");
            return commandNode;
        }

        if ((hero.getName().equals("Lord Royce") || hero.getName().equals("Empress Thorina"))
                && ((isFirstPlayer && (row == 2 || row == Constants.FIRSTROWPLAYER2))
                || (isSecondPlayer && (row == 1 || row == 0)))) {
            commandNode.put("error", "Selected row does not belong to the enemy.");
            return commandNode;
        }

        if ((hero.getName().equals("General Kocioraw") || hero.getName().equals("King Mudface"))
                && ((isFirstPlayer && (row == 1 || row == 0))
                || (isSecondPlayer && (row == 2 || row == Constants.FIRSTROWPLAYER2)))) {
            commandNode.put("error", "Selected row does not belong to the current player.");
            return commandNode;
        }
        //hero ability used on row
        switch (hero.getName()) {
            case "Lord Royce":
                int biggestAtt = -1;
                DefaultCard biggestAttCard = null;

                for (DefaultCard card : table.getTable().get(row)) {
                    if (biggestAtt < card.getAttackDamage()) {
                        biggestAtt = card.getAttackDamage();
                        biggestAttCard = card;
                    }
                }

                if (biggestAttCard != null) {
                    biggestAttCard.setStatus("Frozen");

                }
                break;

            case "Empress Thorina":
                int biggestHealth = -1;
                DefaultCard biggestHealthCard = null;

                for (DefaultCard card : table.getTable().get(row)) {
                    if (biggestHealth < card.getHealth()) {
                        biggestHealth = card.getHealth();
                        biggestHealthCard = card;
                    }
                }

                if (biggestHealthCard != null) {
                    biggestHealthCard.setHealth(0);
                    table.removeDeadCards();
                }

                break;

            case "King Mudface":
                for (DefaultCard card : table.getTable().get(row)) {
                    card.setHealth(card.getHealth() + 1);
                }
                break;

            case "General Kocioraw":
                for (DefaultCard card : table.getTable().get(row)) {
                    card.setAttackDamage(card.getAttackDamage() + 1);
                }
                break;
            default:
                System.out.println("error");
        }

        player.setMana(player.getMana() - hero.getMana());
        hero.setStatus("Attacked");
        return null;
    }
}
