package com.kurante.projectvoice_gdx.game

// https://github.com/AndrewFM/VoezEditor/blob/master/Assets/Scripts/ProjectData.cs#L548
enum class TransitionEase(
    val easeFunction: (Float, Float, Float) -> Float
) {
    NONE({ _, _, end -> end }),
    LINEAR({ perc, start, end -> (end - start) * perc + start }),
    EXP_IN(LINEAR.easeFunction),
    EXP_OUT(LINEAR.easeFunction),
    EXP_INOUT(LINEAR.easeFunction),
    EXP_OUTIN(LINEAR.easeFunction),
    QUAD_IN(LINEAR.easeFunction),
    QUAD_OUT(LINEAR.easeFunction),
    QUAD_INOUT(LINEAR.easeFunction),
    QUAD_OUTIN(LINEAR.easeFunction),
    CIRC_IN(LINEAR.easeFunction),
    CIRC_OUT(LINEAR.easeFunction),
    CIRC_INOUT(LINEAR.easeFunction),
    CIRC_OUTIN(LINEAR.easeFunction),
    BACK_IN(LINEAR.easeFunction),
    BACK_OUT(LINEAR.easeFunction),
    BACK_INOUT(LINEAR.easeFunction),
    BACK_OUTIN(LINEAR.easeFunction),
    ELASTIC_IN(LINEAR.easeFunction),
    ELASTIC_OUT(LINEAR.easeFunction),
    ELASTIC_INOUT(LINEAR.easeFunction),
    ELASTIC_OUTIN(LINEAR.easeFunction),
    EXIT(LINEAR.easeFunction),
    EXIT_MOVE(LINEAR.easeFunction),
    EXIT_SCALE(LINEAR.easeFunction),
    EXIT_COLOR(LINEAR.easeFunction),
}
