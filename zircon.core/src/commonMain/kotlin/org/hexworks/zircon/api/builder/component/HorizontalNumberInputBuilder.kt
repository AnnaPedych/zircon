package org.hexworks.zircon.api.builder.component

import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.component.NumberInput
import org.hexworks.zircon.api.component.builder.base.BaseComponentBuilder
import org.hexworks.zircon.api.component.data.CommonComponentProperties
import org.hexworks.zircon.api.component.data.ComponentMetadata
import org.hexworks.zircon.api.component.renderer.ComponentRenderer
import org.hexworks.zircon.internal.component.impl.DefaultHorizontalNumberInput
import org.hexworks.zircon.internal.component.renderer.DefaultComponentRenderingStrategy
import org.hexworks.zircon.internal.component.renderer.DefaultNumberInputRenderer
import kotlin.jvm.JvmStatic
import kotlin.math.max

@Suppress("UNCHECKED_CAST")
data class HorizontalNumberInputBuilder(
        val width: Int,
        private var initialValue: Int = 0,
        private var minValue: Int = 0,
        private var maxValue: Int = Int.MAX_VALUE,
        override val props: CommonComponentProperties<NumberInput> = CommonComponentProperties(
                componentRenderer = DefaultNumberInputRenderer()))
    : BaseComponentBuilder<NumberInput, HorizontalNumberInputBuilder>() {

    fun withInitialValue(value: Int) = also {
        initialValue = when {
            value < minValue -> minValue
            value > maxValue -> maxValue
            else -> value
        }
    }

    fun withMaxValue(value: Int) = also {
        maxValue = value
        if (initialValue > maxValue) {
            initialValue = maxValue
        }
        contentSize = contentSize
                .withWidth(max(this.maxValue.toString().length + 1, width))
                .withHeight(1)
    }

    fun withMinValue(value: Int) = also {
        minValue = value
        if (initialValue < minValue) {
            initialValue = minValue
        }
    }

    override fun build(): NumberInput = DefaultHorizontalNumberInput(
            componentMetadata = ComponentMetadata(
                    size = size,
                    relativePosition = position,
                    componentStyleSet = componentStyleSet,
                    tileset = tileset),
            initialValue = initialValue,
            minValue = minValue,
            maxValue = maxValue,
            renderingStrategy = DefaultComponentRenderingStrategy(
                    decorationRenderers = decorationRenderers,
                    componentRenderer = props.componentRenderer as ComponentRenderer<NumberInput>)).also {
        if (colorTheme !== ColorThemes.default()) {
            it.theme = colorTheme
        }
    }

    override fun createCopy() = copy()

    companion object {

        @JvmStatic
        fun newBuilder(width: Int) = HorizontalNumberInputBuilder(width = width)
    }
}
