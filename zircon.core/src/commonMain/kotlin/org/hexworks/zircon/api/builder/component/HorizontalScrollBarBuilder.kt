package org.hexworks.zircon.api.builder.component

import org.hexworks.zircon.api.component.RangeSelect
import org.hexworks.zircon.api.component.builder.base.BaseComponentBuilder
import org.hexworks.zircon.api.component.data.ComponentMetadata
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.internal.component.impl.DefaultHorizontalScrollBar
import org.hexworks.zircon.internal.component.renderer.DefaultComponentRenderingStrategy
import org.hexworks.zircon.internal.component.renderer.HorizontalScrollBarRenderer
import kotlin.jvm.JvmStatic

@Suppress("UNCHECKED_CAST")
/**
 * Builder for the scrollbar. By default, it creates a scrollbar with a number of items of 100.
 */
class HorizontalScrollBarBuilder(
        private var minValue: Int = 0,
        private var maxValue: Int = 100)
    : BaseComponentBuilder<RangeSelect, HorizontalScrollBarBuilder>(HorizontalScrollBarRenderer()) {

    fun withNumberOfScrollableItems(items: Int) = also {
        require(items > 0) { "Number of items must be greater than 0." }
        this.maxValue = items
    }

    override fun build(): RangeSelect = DefaultHorizontalScrollBar(
            componentMetadata = ComponentMetadata(
                    size = size,
                    relativePosition = position,
                    componentStyleSet = componentStyleSet,
                    tileset = tileset),
            minValue = minValue,
            maxValue = maxValue,
            itemsShownAtOnce = size.width,
            numberOfSteps = size.width,
            renderingStrategy = DefaultComponentRenderingStrategy(
                    decorationRenderers = decorationRenderers,
                    componentRenderer = props.componentRenderer as ComponentRenderer<RangeSelect>)).apply {
        colorTheme.map {
            theme = it
        }
    }

    override fun createCopy() = newBuilder().withProps(props.copy())
            .withNumberOfScrollableItems(maxValue)

    companion object {

        @JvmStatic
        fun newBuilder() = HorizontalScrollBarBuilder()
    }
}
