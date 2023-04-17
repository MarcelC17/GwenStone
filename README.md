GWENSTONE
Strategy card game.



Item classes:


Cards:
-MinionCard
-EnvironmentCard
-HeroCard

At the basis of game design are 2 types of cards:
-GameBoard Cards (destined to be placed on game table)
Minion and Environment cards

-Player Cards(main cards which determine if a player wins the game)
Hero cards

Given this structure MinionCard and EnvironmentCard classes extend
DefaultCard interface, with the functionality of being placed on the
game-board.



Deck - list of defaultCards



Player - game participants with the following traits:

-ArrayList<Deck> playerDecks - (list of)two decks given to each at the beggining of a new game

-Deck gameDeck - shuffled deck which was chosen to be played during 1 game

-ArrayList<DefaultCard> hand - cards in player's hand during game

and the ability of placing a card on the table:

-public ObjectNode placeCard()


Table - game-board with 4 rows and 5 columns. Cards placed on the table are stored in a 2D list
ArrayList<ArrayList<DefaultCard>> table. This class implements the following actions on the cards
placed on the table:

-public void setFrozenCards() - sets card status

-public void removeDeadCards() - removes dead cards

-public void addCardOnTable() - finds the position and places the card


Command Classes:

RunGame - main class. Runs game mechanics using the following methods:

- public void startGame() - sets players for a new game

- public void runActions() - executes commands during game

DebugCommands - implements commands aimed to get information about current
items and state of the game

GameCommands - implements game actions of minions, environment and hero
cards(using abilities and attacking).

DebugCommands and GameCommands are used in runActions() method of RunGame
class in order to execute commands.


StartGameUtils - methods used while getting ready for a new game













