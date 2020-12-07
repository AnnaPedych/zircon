package org.hexworks.zircon.internal.graphics

import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.TileComposite
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.api.graphics.base.BaseTileGraphics
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.internal.data.DefaultTileGraphicsState
import org.hexworks.zircon.internal.data.TileGraphicsState
import org.hexworks.zircon.internal.graphics.ArrayBackedTileMap.Entry
import kotlin.jvm.Synchronized

/**
 * This is a fast implementation of [TileGraphics] that sacrifices memory footprint
 * for speed and uses an array behind the scenes.
 */
class FastTileGraphics(
    initialSize: Size,
    initialTileset: TilesetResource,
    initialTiles: Map<Position, Tile> = mapOf()
) : BaseTileGraphics(
    initialSize = initialSize,
    initialTileset = initialTileset
) {

    private var arr = arrayOfNulls<Map.Entry<Position, Tile>>(initialSize.width * initialSize.height)

    init {
        for ((pos, tile) in initialTiles.entries) {
            arr[pos.index] = Entry(pos, tile)
        }
    }

    override var tiles: ArrayBackedTileMap = ArrayBackedTileMap(initialSize, arr)
        private set
    override val state: TileGraphicsState
        @Synchronized
        get() = DefaultTileGraphicsState(
            size = size,
            tileset = tileset,
            tiles = tiles.createCopy()
        )

    fun contents() = tiles.contents()

    @Synchronized
    override fun draw(tile: Tile, drawPosition: Position) {
        if (size.containsPosition(drawPosition)) {
            arr[drawPosition.index] = Entry(drawPosition, tile)
        }
    }

    @Synchronized
    override fun draw(tileMap: Map<Position, Tile>, drawPosition: Position, drawArea: Size) {
        tileMap.asSequence()
            .filter { drawArea.containsPosition(it.key) && size.containsPosition(it.key + drawPosition) }
            .map { (key, value) -> key + drawPosition to value }
            .forEach { (pos, tile) ->
                if (tile.isEmpty) {
                    arr[pos.index] = null
                } else {
                    arr[pos.index] = Entry(pos, tile)
                }
            }
    }

    @Synchronized
    override fun draw(tileComposite: TileComposite) {
        if (tileComposite is FastTileGraphics && tileComposite.size == size) {
            tileComposite.arr.copyInto(arr)
        } else super.draw(tileComposite)
    }

    @Synchronized
    override fun clear() {
        arr = arrayOfNulls(size.width * size.height)
        tiles = ArrayBackedTileMap(size, arr)
    }

    @Synchronized
    override fun fill(filler: Tile) {
        if (filler.isNotEmpty) {
            for (i in arr.indices) {
                if (arr[i] == null) {
                    arr[i] = Entry(i.pos, filler)
                }
            }
        }
    }

    @Synchronized
    override fun transform(transformer: (Position, Tile) -> Tile) {
        size.fetchPositions().forEach { pos ->
            val tile = transformer(pos, arr[pos.index]?.value ?: Tile.empty())
            if (tile.isEmpty) {
                arr[pos.index] = null
            } else {
                arr[pos.index] = Entry(pos, tile)
            }
        }
    }

    private val Position.index: Int
        get() = size.width * y + x

    private val Int.pos: Position
        get() {
            val y = this / size.width
            val x = this - (y * size.width)
            return Position.create(x, y)
        }

}
