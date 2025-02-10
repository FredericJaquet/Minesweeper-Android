# Minesweeper-Android
**Title**: Minesweeper Game for Android (Java, Android Studio)

**Description**: Implementation of the classic Minesweeper game for Android, developed in Java using Android Studio. The game offers different difficulty levels, character customization, and an intuitive user interface.

**Key Features**:

**User Interface**: Graphical interface developed with Android components (GridLayout, ImageView, TextView) to display the game board, the remaining bomb counter, and the chronometer.

**Game Logic**: Implementation of the core game logic, including:
- Random generation of the board with bombs.
- Calculation of the number of bombs adjacent to each cell.
- Recursive revelation of empty cells (without adjacent bombs).
- Mechanic for marking cells with flags.
- Detection of victory and defeat.

**Difficulty Management**: Allows the player to select different difficulty levels (Easy, Medium, Hard), adjusting the board size and the number of bombs.

**Character Customization**: Allows the player to select a different character (bomb, dynamite, grenade, etc.) to represent the bombs in the game.

**Chronometer**: Displays the elapsed time during the game.

**Dialogs**: Uses AlertDialog dialogs to display instructions, configuration options, victory, and defeat messages.

**Custom Spinner Adapter**: Uses a custom adapter (CharacterSpinnerAdapter) to display the list of characters in the character selection Spinner.

**Custom BombButton Class**: Extends the standard Button class (BombButton) to add game-specific attributes, such as:
- Information on whether the cell contains a bomb or not.
- The number of adjacent bombs.
- Whether the cell has been visited or marked with a flag.

**Demonstrated Skills**:
- Android application development with Java and Android Studio.
- User interface design and implementation with Android components.
- Event handling (clicks, long presses).
- Using AlertDialog dialogs to display information to the user.
- Creating custom adapters for Spinners.
- Extending standard Android classes to add specific functionalities.
- Implementation of recursive search algorithms.
- Resource management (images, text strings).
- Code organization into classes and methods to improve readability and maintainability.
