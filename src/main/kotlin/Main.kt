package minesweeper

import kotlin.random.Random

const val UN_EXPLORED_CELL = "."
const val EXPLORED_FREE_CELL = "/"
const val MINES = "X"
const val UN_EXPLORED_MARKED_CELL = "*"

/**
 * Then, calculate how many mines there are around each empty cell.
 * Check 8 cells if the current cell is in the middle of the field,
 * 5 cells if it's on the side, and 3 cells if it's in the corner.
 **********************************************************************************************************************/
class MineSweeper {

    private var totalMine = 0
    private val maxRow = 9
    private val maxCol = 9
    private var isFirstInput = true

    private val minesCoordinate = mutableSetOf<Pair<Int, Int>>()
    private val markedCoordinate = mutableSetOf<Pair<Int, Int>>()
    private val exploredFreeCells = mutableSetOf<Pair<Int, Int>>()
    private val boardFreeCell : MutableSet<Pair<Int,Int>> by lazy {
        boardFreeCell()
    }

    private var board = MutableList(maxRow) { MutableList(maxCol) { UN_EXPLORED_CELL } }

    fun startTheGame() {
        gameSetup()
        displayMines()
        setMines()

        while (true) {

            deleteMine()
            displayMines()

            if (winCondition()) {
                println("Congratulations! You found all the mines!")
                break
            }
        }
    }

    private fun deleteMine() {

        while (true) {
            println("Set/unset mines marks or claim a cell as free: ")
            val userInput = readln()

            val col = userInput.split(" ").first().toInt() - 1
            val row = userInput.split(" ")[1].toInt() - 1
            val freeOrMine = userInput.split(" ").last()

            if (freeOrMine.equals("free",ignoreCase = true)) {

                // for the first time set the mines
                if (isFirstInput) {
                    while (isMine(row,col)){
                        setMines()
                    }
                }
                exploreCell(row,col)
            }
            else if (freeOrMine.equals("mine",ignoreCase = true)){
                if (!markUnMarkTheMine(row,col)) continue
            }

            break
        }
    }


    private fun exploreCell(row: Int, col: Int) {

        val totalAvailableMineAround = getAdjacent(row,col)

        if (totalAvailableMineAround == 0) {

            exploredFreeCells.add(row to col)
            board[row][col] = EXPLORED_FREE_CELL

            if (row - 1 >= 0 && col - 1 >= 0) exploreCell(row - 1,col - 1)
            if (row - 1 >= 0) exploreCell(row - 1,col)
            if (row - 1 >= 0 && col + 1 < maxCol) exploreCell(row - 1,col + 1)
            if (col + 1 < maxCol) exploreCell(row,col + 1)
            if (row + 1 < maxRow && col + 1 < maxCol) exploreCell(row + 1,col + 1)
            if (row + 1 < maxRow) exploreCell(row + 1,col)
            if (row + 1 < maxRow && col - 1 >= 0) exploreCell(row + 1,col - 1)
            if (col - 1 >= 0) exploreCell(row,col - 1)

        }
        else {
            board[row][col] = getAdjacent(row, col).toString()
        }
    }


    private fun markUnMarkTheMine(row: Int, col: Int): Boolean {

        val currentItem = board[row][col]
        return if (isThereNumber(row,col)) {
            println("There is a number here!")
            false
        } else if (currentItem == UN_EXPLORED_MARKED_CELL){
            markedCoordinate.remove(row to col)
            board[row][col] = UN_EXPLORED_CELL
            true
        } else{
            markedCoordinate.add(row to col)
            board[row][col] = UN_EXPLORED_MARKED_CELL
            true
        }
    }


    private fun winCondition(): Boolean {
       val condition1 = markedCoordinate == minesCoordinate
        val condition2 = exploredFreeCells == boardFreeCell
        return condition1 || condition2
    }

    private fun boardFreeCell() : MutableSet<Pair<Int,Int>> {

        val boardFreeCell = mutableSetOf<Pair<Int, Int>>()
        for (row in 0 until maxRow) {
            for (col in 0 until maxCol) {

                if (board[row][col].equals(EXPLORED_FREE_CELL, ignoreCase = true)) {
                    boardFreeCell.add(row to col)
                }
            }
        }
        return boardFreeCell
    }


    private fun gameSetup() {
        println("How many mines do you want on the field?")
        totalMine = readln().toInt()
    }


    /**
     * Generate mines like in the original game: the first cell explored with the free command cannot be a mine;
     * it should always be empty. You can achieve this in many ways – it's up to you.
     ******************************************************************************************************************/
    private fun locateMines() {

        board = MutableList(maxRow) { row ->
            MutableList(maxCol) { col ->
                if (Pair(row, col) in minesCoordinate) {
                    "x"
                } else "."
            }
        }
    }

    /**
     * this function will the mines in the field according to user input
     *****************************************************************************************************************/
    private fun setMines() {
        do {
            minesCoordinate.add(Pair(Random.nextInt(0, 9), Random.nextInt(0, 9)))
        } while (minesCoordinate.size < totalMine)
    }

    private fun displayMines() {

        println(" │123456789│")
        println("—│—————————│")

        var index = 1
        for (row in board) {
            print("$index│")
            print(row.joinToString(""))
            println("|")
            index++
        }
        println("—│—————————│")
    }

    private fun isThereNumber(row: Int, col: Int) = board[row][col].toIntOrNull() != null
    private fun isMine(row: Int, col: Int) = (row to col) in minesCoordinate

    private fun lookAround() {
        for (row in 0 until maxRow) {
            for (col in 0 until maxCol) {
                if (!board[row][col].equals("x", ignoreCase = true)) {
                    val totalMine = getAdjacent(row, col)
                    board[row][col] = if (totalMine > 0) totalMine.toString() else "."
                }
            }
        }
    }

    private fun getAdjacent(row: Int, col: Int): Int {

        var totalAvailableMineAround = 0

        if (row - 1 >= 0 && col - 1 >= 0 && minesCoordinate.contains(row-1 to col -1)) {
            totalAvailableMineAround++
        }
        if (row - 1 >= 0 && minesCoordinate.contains(row-1 to col)) {
            totalAvailableMineAround++
        }
        if (row - 1 >= 0 && col + 1 < maxCol && minesCoordinate.contains(row-1 to col + 1)) {
            totalAvailableMineAround++
        }
        if (col + 1 < maxCol && minesCoordinate.contains(row to col + 1)) {
            totalAvailableMineAround++
        }
        if (row + 1 < maxRow && col + 1 < maxCol && minesCoordinate.contains(row + 1 to col +1)) {
            totalAvailableMineAround++
        }
        if (row + 1 < maxRow && minesCoordinate.contains(row + 1 to col)) {
            totalAvailableMineAround++
        }
        if (row + 1 < maxRow && col - 1 >= 0 && minesCoordinate.contains(row + 1 to col -1)) {
            totalAvailableMineAround++
        }
        if (col - 1 >= 0 && minesCoordinate.contains(row to col -1)) {
            totalAvailableMineAround++
        }

        return totalAvailableMineAround
    }

}