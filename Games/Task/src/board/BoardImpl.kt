package board

import board.Direction.*


open class SqBoard(override val width: Int) : SquareBoard {
    val board = mutableListOf<Cell>()

    init {
        for (i in 1..width) {
            for (j in 1..width) {
                board.add(Cell(i, j))
            }
        }
    }

    override fun getAllCells(): Collection<Cell> {
        return board
    }

    override fun getCell(i: Int, j: Int): Cell {
        val cell = board.find { cell -> cell.i == i && cell.j == j }
        if (cell != null) {
            return cell
        }
        throw IllegalArgumentException("Incorrect values of i and j.")
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return board.find { cell -> cell.i == i && cell.j == j }
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val row = board.filter { cell -> cell.i == i && cell.j in jRange }
        if (jRange.first < jRange.last) {
            return row
        }
        return row.sortedByDescending(Cell::j)
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val column = board.filter { cell -> cell.j == j && cell.i in iRange }
        if (iRange.first < iRange.last) {
            return column
        }
        return column.sortedByDescending(Cell::i)
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            Direction.UP -> board.find { cell -> cell.i == i - 1 && cell.j == j }
            Direction.DOWN -> board.find { cell -> cell.i == i + 1 && cell.j == j }
            Direction.RIGHT -> board.find { cell -> cell.j == j + 1 && cell.i == i }
            else -> board.find { cell -> cell.j == j - 1 && cell.i == i }
        }
    }

}

class GBoard<T>(override val width: Int) : SqBoard(width), GameBoard<T> {
    val sqBoard = SqBoard(width)
    val gBoard = mutableMapOf<Cell, T?>()

    init {
        for (i in 1..width) {
            for (j in 1..width) {
                gBoard[sqBoard.getCell(i, j)] = null
            }
        }
    }

    override fun getAllCells(): Collection<Cell> {
        return sqBoard.board
    }

    override fun getCell(i: Int, j: Int): Cell {
        return sqBoard.getCell(i, j)
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        return sqBoard.getCellOrNull(i, j)
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        return sqBoard.getRow(i, jRange)
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        return sqBoard.getColumn(iRange, j)
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return when (direction) {
            Direction.UP -> sqBoard.board.find { cell -> cell.i == i - 1 && cell.j == j }
            Direction.DOWN -> sqBoard.board.find { cell -> cell.i == i + 1 && cell.j == j }
            Direction.RIGHT -> sqBoard.board.find { cell -> cell.j == j + 1 && cell.i == i }
            else -> sqBoard.board.find { cell -> cell.j == j - 1 && cell.i == i }
        }
    }

    override fun get(cell: Cell): T? {
        return gBoard[cell]
    }

    override fun set(cell: Cell, value: T?) {
        gBoard[cell] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        return gBoard.filterValues(predicate).keys
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        val cell = gBoard.filterValues(predicate).map { it.key }
        return cell[0]
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        return gBoard.values.any(predicate)
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        return gBoard.values.all(predicate)
    }

}

fun createSquareBoard(width: Int): SquareBoard = SqBoard(width)
fun <T> createGameBoard(width: Int): GameBoard<T> = GBoard(width)

