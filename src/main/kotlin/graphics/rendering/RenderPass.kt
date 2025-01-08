package graphics.rendering

interface RenderPass {
    val name: String
}

object NormalPass : RenderPass { override val name = "NORMAL_PASS" }
object ReflectionPass : RenderPass { override val name = "REFLECTION_PASS" }
object RefractionPass : RenderPass { override val name = "REFRACTION_PASS" }
object ShadowPass : RenderPass { override val name = "SHADOW_PASS" }