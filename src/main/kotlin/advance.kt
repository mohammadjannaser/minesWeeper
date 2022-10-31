
import java.util.*
import kotlin.random.Random

const val MARKER = "*"
const val HIDDEN = "."
const val EXPLORED = "/"
const val DIMENSION = 9

val scanner = Scanner(System.`in`)


fun main() {
    print("How many mines do you want on the field? ")
    val noOfMines = scanner.nextInt()

    val mineSweeper = MineSweeper(noOfMines)
    mineSweeper.printState()

    do {
        print("Set/unset mines marks or claim a cell as free: ")
        val col = scanner.nextInt() - 1 // column is X coord !!
        val row = scanner.nextInt() - 1 // row is Y coord !!
        val action = scanner.next()

        when (action) {
            "free" -> {
                if (mineSweeper.isMine(row, col)) {
                    mineSweeper.printState()
                    println("You stepped on a mine and failed!")
                    return
                } else {
                    mineSweeper.floodFill(row, col)
                    mineSweeper.printState()
                }
            }
            "mine" -> {
                mineSweeper.toggleMarker(row, col)
                mineSweeper.printState()
            }
            else -> continue
        }
    } while (!mineSweeper.isGameWon())

    println("Congratulations! You found all the mines!")
}


data class Cell(
    var isMine: Boolean = false,
    var isMarked: Boolean = false,
    var isExplored: Boolean = false,
    var numberOfMinesCloseBy: Int = 0
) {
    override fun toString(): String {
        return when {
            isMarked -> MARKER
            isMine -> HIDDEN
            isExplored -> {
                if (numberOfMinesCloseBy > 0)
                    numberOfMinesCloseBy.toString()
                else
                    EXPLORED
            }
            else -> {
                HIDDEN
            }
        }
    }
}

class MineSweeper(noOfMines: Int) {
    private val minefield = Array(DIMENSION) { Array(DIMENSION) { Cell() } }
    private val noOfMines = minOf(noOfMines, DIMENSION * DIMENSION)

    init {
        fillRandomly()
    }

    private fun fillRandomly() {
        var noOfMinesPlaced = 0
        while (noOfMinesPlaced < noOfMines) {
            val row = Random.nextInt(DIMENSION)
            val col = Random.nextInt(DIMENSION)
            if (!minefield[row][col].isMine) {
                minefield[row][col].isMine = true
                increaseAllNeighbors(row, col)
                noOfMinesPlaced++
            }
        }
    }

    // we know 'row'/'col' indicates a mine, so all its neighbors
    // are increased by 1 (if they are not a mine)
    private fun increaseAllNeighbors(row: Int, col: Int) {
        for (x in (row - 1)..(row + 1)) {
            for (y in (col - 1)..(col + 1)) {
                if (isWithinBoundary(x, y)) {
                    if (minefield[x][y].isMine) {
                        continue
                    }
                    minefield[x][y].numberOfMinesCloseBy++
                }
            }
        }
    }

    fun printState() {
        println(" |123456789|")
        println("-|---------|")
        for (row in 0 until DIMENSION) {
            print("${row + 1}|")
            for (col in 0 until DIMENSION) {
                print(minefield[row][col])
            }
            print("|")
            println()
        }
        println("-|---------|")
    }

    fun toggleMarker(row: Int, col: Int) {
        minefield[row][col].isMarked = !minefield[row][col].isMarked
    }

    fun isMine(row: Int, col: Int): Boolean {
        return minefield[row][col].isMine
    }

    fun floodFill(row: Int, col: Int) {
        if (isWithinBoundary(row, col)) {
            val cell = minefield[row][col]
            if (!cell.isMine && !cell.isExplored) {
                minefield[row][col].isExplored = true
                minefield[row][col].isMarked = false

                floodFill(row - 1, col)
                floodFill(row + 1, col)
                floodFill(row, col - 1)
                floodFill(row, col + 1)
            }
        }
    }

    fun isGameWon(): Boolean {
        var countCorrectMarkers = 0
        var countWrongMarkers = 0

        for (row in 0 until DIMENSION) {
            for (col in 0 until DIMENSION) {
                val cell = minefield[row][col]

                if (cell.isMarked && cell.isMine) {
                    countCorrectMarkers++
                }
                if (cell.isMarked && !cell.isMine) {
                    countWrongMarkers++
                }
            }
        }
        return countCorrectMarkers == noOfMines && countWrongMarkers == 0
    }

    private fun isWithinBoundary(x: Int, y: Int) = x in 0 until DIMENSION && y in 0 until DIMENSION
}
