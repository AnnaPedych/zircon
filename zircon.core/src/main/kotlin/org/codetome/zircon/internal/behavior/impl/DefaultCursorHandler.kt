package org.codetome.zircon.internal.behavior.impl

import org.codetome.zircon.api.Position
import org.codetome.zircon.api.Size
import org.codetome.zircon.internal.behavior.Dirtiable
import org.codetome.zircon.internal.behavior.InternalCursorHandler

class DefaultCursorHandler(private var cursorSpace: Size,
                           private val dirtiable: Dirtiable = DefaultDirtiable())
    : InternalCursorHandler, Dirtiable by dirtiable {

    private var cursorPosition = Position.DEFAULT_POSITION
    private var cursorVisible = false

    init {
        setPositionDirty(cursorPosition)
    }

    @Synchronized
    override fun drainDirtyPositions(): Set<Position> {
        return dirtiable.drainDirtyPositions().also {
            setPositionDirty(cursorPosition)
        }
    }

    override fun isCursorVisible() = cursorVisible

    @Synchronized
    override fun setCursorVisibility(cursorVisible: Boolean) =
            if (this.cursorVisible == cursorVisible) {
                false
            } else {
                this.cursorVisible = cursorVisible
                true
            }

    override fun getCursorPosition(): Position = cursorPosition

    @Synchronized
    override fun putCursorAt(cursorPosition: Position): Boolean {
        val newCursorPos = cursorPosition
                .withColumn(Math.min(cursorPosition.column, cursorSpace.columns - 1))
                .withRow(Math.min(cursorPosition.row, cursorSpace.rows - 1))
        return if (this.cursorPosition == newCursorPos) {
            false
        } else {
            this.cursorPosition = newCursorPos
            setPositionDirty(this.cursorPosition)
            true
        }
    }

    @Synchronized
    override fun moveCursorForward() =
            putCursorAt(getCursorPosition().let { (column) ->
                if (cursorIsAtTheEndOfTheLine(column)) {
                    getCursorPosition().withColumn(0).withRelativeRow(1)
                } else {
                    getCursorPosition().withRelativeColumn(1)
                }
            })

    @Synchronized
    override fun moveCursorBackward() =
            putCursorAt(getCursorPosition().let { (column) ->
                if (cursorIsAtTheStartOfTheLine(column)) {
                    if (getCursorPosition().row > 0) {
                        getCursorPosition().withColumn(cursorSpace.columns - 1).withRelativeRow(-1)
                    } else {
                        getCursorPosition()
                    }
                } else {
                    getCursorPosition().withRelativeColumn(-1)
                }
            })

    override fun getCursorSpaceSize() = cursorSpace

    @Synchronized
    override fun resizeCursorSpace(size: Size) {
        this.cursorSpace = size
        putCursorAt(getCursorPosition())
    }

    override fun isCursorAtTheEndOfTheLine() = cursorPosition.column == cursorSpace.columns - 1

    override fun isCursorAtTheStartOfTheLine() = cursorPosition.column == 0

    override fun isCursorAtTheFirstRow() = cursorPosition.row == 0

    override fun isCursorAtTheLastRow() = cursorPosition.row == cursorSpace.rows - 1

    private fun cursorIsAtTheEndOfTheLine(column: Int) = column + 1 == cursorSpace.columns

    private fun cursorIsAtTheStartOfTheLine(column: Int) = column == 0
}