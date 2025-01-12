package graphics.assets

interface Asset {
    fun getId(): Int
    fun create()
    fun destroy()
    fun bind()
    fun unbind()
}