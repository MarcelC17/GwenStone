package components;

import components.cards.DefaultCard;
import lombok.Getter;
import lombok.Setter;
import utils.Constants;

import java.util.ArrayList;

public final class Table {
    @Getter @Setter private int player1Row;

    @Getter @Setter private int player2Row;
    @Getter @Setter private ArrayList<ArrayList<DefaultCard>> table;

    public Table() {
        setPlayer1Row(Constants.FIRSTROWPLAYER2);
        setPlayer2Row(Constants.FIRSTROWPLAYER1);
        setTable(new ArrayList<>());

        int rowCount = 0;
        int maxRows = Constants.MAXROWS;
        while (rowCount < maxRows) {
            ArrayList<DefaultCard> initList = new ArrayList<>();
            table.add(initList);
            rowCount++;
        }
    }


    /**
     * adds card on the corresponding row on the table
     */
    public void addCardOnTable(final DefaultCard card, final int playerIdx) {
               switch (card.getName()) {
                   case "The Ripper":
                   case "Miraj":
                   case "Goliath":
                   case "Warden":
                       if (playerIdx == 1) {
                           player1Row = 1;
                       } else  {
                           player1Row = 2;
                       }
                       break;
                   case  "Sentinel":
                   case "Berserker":
                   case "The Cursed One":
                   case "Disciple":
                       if (playerIdx == 1) {
                           player1Row = Constants.FIRSTROWPLAYER1;
                       } else  {
                           player1Row = Constants.FIRSTROWPLAYER2;
                       }
                       break;
                   default:
                       System.out.println("Card from wrong game");
               }
               this.table.get(player1Row).add(card);
    }

    /**
     * removes dead cards from the table
     */
    public void removeDeadCards() {
        ArrayList<DefaultCard> cardsToRemove = new ArrayList<>();
        //searches dead minions
        for (ArrayList<DefaultCard> row : getTable()) {
            for (DefaultCard card : row) {
                if (card.getHealth() <= 0) {
                    cardsToRemove.add(card);
                }
            }
        }
        //removes dead minions
        for (ArrayList<DefaultCard> row : getTable()) {
            for (DefaultCard card : cardsToRemove) {
                row.remove(card);
            }
        }
    }

    /**
     * unsets cards which attacked in the previous turn and frozen cards
     */
    public void setFrozenCards() {
        for (ArrayList<DefaultCard> row : getTable()) {
            for (DefaultCard card : row) {
                if (card.getStatus().equals("Frozen") && card.getFrozenRoundCounter() == 2) {
                    card.setStatus("Normal");
                    card.setFrozenRoundCounter(0);
                }
                if (card.getStatus().equals("Frozen")) {
                    card.setFrozenRoundCounter(card.getFrozenRoundCounter() + 1);
                }
                if (card.getStatus().equals("Attacked")) {
                    card.setStatus("Normal");
                }
            }
        }
    }
}
