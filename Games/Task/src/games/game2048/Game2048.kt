package games.game2048

import board.*
import games.game.Game

/*
 * Your task is to implement the game 2048 https://en.wikipedia.org/wiki/2048_(video_game).
 * Implement the utility methods below.
 *
 * After implementing it you can try to play the game running 'PlayGame2048'.
 */
fun newGame2048(initializer: Game2048Initializer<Int> = RandomGame2048Initializer): Game =
        Game2048(initializer)

class Game2048(private val initializer: Game2048Initializer<Int>) : Game {
    private val board = createGameBoard<Int?>(4)

    override fun initialize() {
        repeat(2) {
            board.addNewValue(initializer)
        }
    }

    override fun canMove() = board.any { it == null }

    override fun hasWon() = board.any { it == 2048 }

    override fun processMove(direction: Direction) {
        if (board.moveValues(direction)) {
            board.addNewValue(initializer)
        }
    }

    override fun get(i: Int, j: Int): Int? = board.run { get(getCell(i, j)) }
}

/*
 * Add a new value produced by 'initializer' to a specified cell in a board.
 */
fun GameBoard<Int?>.addNewValue(initializer: Game2048Initializer<Int>) {
    val newValue = initializer.nextValue(this)
    if (newValue != null) {
        this[newValue.first] = newValue.second
    }
}

/*
 * Update the values stored in a board,
 * so that the values were "moved" in a specified rowOrColumn only.
 * Use the helper function 'moveAndMergeEqual' (in Game2048Helper.kt).
 * The values should be moved to the beginning of the row (or column),
 * in the same manner as in the function 'moveAndMergeEqual'.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValuesInRowOrColumn(rowOrColumn: List<Cell>): Boolean {
    val oldValues = mutableListOf<Int?>()
    for (cell in rowOrColumn) {
        oldValues.add(this[cell])
    }

    val newValues = oldValues.moveAndMergeEqual { it * 2 }

    // if cell can not move
    if (oldValues == newValues) return false

    // if cell can move
    for ((i, cell) in rowOrColumn.withIndex()) {
        if (i < newValues.size) {
            this[cell] = newValues[i]
        } else {
            this[cell] = null
        }
    }

    return true
}



/*
 * Update the values stored in a board,
 * so that the values were "moved" to the specified direction
 * following the rules of the 2048 game .
 * Use the 'moveValuesInRowOrColumn' function above.
 * Return 'true' if the values were moved and 'false' otherwise.
 */
fun GameBoard<Int?>.moveValues(direction: Direction): Boolean {
    val fullCells = filter { it != null }.toList()
    val row = fullCells[0].i
    val col = fullCells[0].j
    val fullRow = getRow(row, 1..4)
    val fullColumn = getColumn(1..4, col)
    if (fullCells == fullRow || fullCells == fullColumn) {
        return false
    }

    for (i in 1..width) {
        val rowOrColumn = when (direction) {
            Direction.UP -> getColumn(1..width, i)
            Direction.DOWN -> getColumn(width downTo 1, i)
            Direction.RIGHT -> getRow(i, width downTo 1)
            else -> getRow(i, 1..width)
        }
        moveValuesInRowOrColumn(rowOrColumn)
    }

    return true
}

//fun main() {
//    val gmBrd = GBoard<Int?>(4)
//
//}