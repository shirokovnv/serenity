package graphics.animation

enum class MtlColorKey(val value: String) {
    DIFFUSE("\$clr.diffuse"),
    SPECULAR("\$clr.specular"),
    AMBIENT("\$clr.ambient"),
    EMISSIVE("\$clr.emissive"),
}

enum class MtlKey(val value: String) {
    OPACITY("\$mat.opacity"),
    SHININESS("\$mat.shininess"),
    SHININESS_STRENGTH("\$mat.shinpercent")
}