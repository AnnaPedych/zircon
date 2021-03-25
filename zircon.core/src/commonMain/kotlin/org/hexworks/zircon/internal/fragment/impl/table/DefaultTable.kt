package org.hexworks.zircon.internal.fragment.impl.table

import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.cobalt.databinding.api.property.Property
import org.hexworks.cobalt.databinding.api.value.ObservableValue
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.component.AttachedComponent
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.HBox
import org.hexworks.zircon.api.component.VBox
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.fragment.Table
import org.hexworks.zircon.api.uievent.MouseEventType
import org.hexworks.zircon.api.uievent.UIEventPhase
import org.hexworks.zircon.api.uievent.UIEventResponse

/**
 * The **internal** default implementation of [Table].
 */
class DefaultTable<M: Any>(
    private val data: List<M>,
    private val columns: List<TableColumn<M, *, *>>,
    /**
     * The height this fragment will use. Keep in mind that the first row will be used as header row.
     */
    fragmentHeight: Int,
    private val rowSpacing: Int = 0,
    private val colSpacing: Int = 0
): Table<M> {

    init {
        require(data.isNotEmpty()) {
            "A table may not be empty! Please feed it some data to display."
        }
        require(columns.isNotEmpty()) {
            "A table must have at least one column."
        }
        val minHeight = 2
        require(fragmentHeight >= minHeight) {
            "A table requires a height of at least $minHeight."
        }
    }

    private val selectedElement: Property<M> = data.first().toProperty()

    override val selectedRowValue: ObservableValue<M> = selectedElement

    override val selectedRow: M
        get() = selectedRowValue.value

    override val size: Size = Size.create(
        width = columns.sumBy { it.width } + ((columns.size - 1) * colSpacing),
        height = fragmentHeight
    )

    override val root: VBox = Components
        .vbox()
        .withSpacing(0)
        .withSize(size)
        .build()

    private val currentRows: MutableList<AttachedComponent> = mutableListOf()

    init {
        val headerRow = headerRow()
        root
            .addComponents(
                headerRow,
                dataPanel(size.withRelativeHeight(- headerRow.height))
            )
    }

    private fun dataPanel(panelSize: Size): VBox {
        return Components
            .vbox()
            .withSize(panelSize)
            .withSpacing(rowSpacing)
            .build()
            .apply {
                var remainingHeight = panelSize.height
                // TODO: Improve this loop to not loop over all elements
                data.forEach { model ->
                    val newRow = newRowFor(model)
                    if(remainingHeight > 0) {
                        currentRows.add(addComponent(newRow))
                    }
                    remainingHeight -= newRow.height + rowSpacing
                }
            }
    }

    private fun newRowFor(model: M): Component {
        val cells: List<Component> = columns
            .map { it.newCell(model) }
        val rowHeight = cells.maxOf { it.height }
        val row = Components
            .hbox()
            .withSpacing(colSpacing)
            .withSize(size.width, rowHeight)
            .build()
        cells.forEach { row.addComponent(it) }
        row.handleMouseEvents(MouseEventType.MOUSE_CLICKED) { _, phase ->
            // allow for the cells to implement custom mouse event handling
            if (phase == UIEventPhase.BUBBLE) {
                selectedElement.updateValue(model)
                UIEventResponse.processed()
            } else {
                UIEventResponse.pass()
            }
        }
        return row
    }

    private fun headerRow(): HBox {
        return Components
            .hbox()
            .withSize(size.width, 1)
            .withSpacing(colSpacing)
            .build()
            .apply {
                columns
                    .forEach { column ->
                        addComponent(column.header)
                    }
            }
    }
}