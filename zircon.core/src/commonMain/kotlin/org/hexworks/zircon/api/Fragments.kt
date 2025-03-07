package org.hexworks.zircon.api

import org.hexworks.cobalt.databinding.api.collection.ObservableList
import org.hexworks.cobalt.databinding.api.extension.toProperty
import org.hexworks.zircon.api.builder.fragment.ColorThemeSelectorBuilder
import org.hexworks.zircon.api.builder.fragment.SelectorBuilder
import org.hexworks.zircon.api.builder.fragment.TableBuilder
import org.hexworks.zircon.api.builder.fragment.TilesetSelectorBuilder
import org.hexworks.zircon.api.component.ColorTheme
import org.hexworks.zircon.api.component.Fragment
import org.hexworks.zircon.api.fragment.Selector
import org.hexworks.zircon.api.resource.TilesetResource
import kotlin.jvm.JvmStatic

/**
 * This *facade* object provides builders for the built-in [Fragment]s
 * (more complex Components that come with their own logic).
 */
object Fragments {

    /**
     * Creates a [SelectorBuilder] to create [Selector]s.
     */
    @JvmStatic
    fun <M : Any> selector(width: Int, values: List<M>) = SelectorBuilder.newBuilder(width, values)

    /**
     * Creates a new [TilesetSelectorBuilder] to build [Selector]s for [TilesetResource]s.
     */
    @JvmStatic
    fun tilesetSelector(
        width: Int,
        tileset: TilesetResource
    ): TilesetSelectorBuilder = TilesetSelectorBuilder.newBuilder(width, tileset)

    /**
     * Creates a new [TilesetSelectorBuilder] to build [Selector]s for [ColorTheme]s.
     */
    @JvmStatic
    fun colorThemeSelector(
        width: Int,
        theme: ColorTheme
    ): ColorThemeSelectorBuilder = ColorThemeSelectorBuilder.newBuilder(width, theme)

    /**
     * Creates a new [TableBuilder] to build a [org.hexworks.zircon.api.fragment.Table] with its [TableColumns].
     *
     * @param data a simple list that will be converted to an [ObservableList] and passed to [table]. It is
     * generally recommended to directly pass an [ObservableList] to the table fragment.
     */
    @Beta
    fun <M: Any> table(data: List<M>): TableBuilder<M> =
        table(data.toProperty())

    /**
     * Creates a new [TableBuilder] to build a [org.hexworks.zircon.api.fragment.Table] with its [TableColumns].
     *
     * @param data an [ObservableList] containing the data to display in the resulting table. Changes
     * in the list will be reflected in the UI.
     */
    @Beta
    fun <M: Any> table(data: ObservableList<M>): TableBuilder<M> =
        TableBuilder(data)

}
