package audio

enum class AudioAttenuationModel {
    NONE,
    LINEAR,
    LINEAR_CLAMPED,
    INVERSE,
    INVERSE_CLAMPED,
    EXPONENT,
    EXPONENT_CLAMPED
}