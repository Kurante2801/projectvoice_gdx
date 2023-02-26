package com.kurante.projectvoice_gdx.util;

// See https://gist.github.com/Kurante2801/531915a8a6d6d5d9c7ea390cd00ffc73

// PROBLEM 1: I want to use Unity's Animation Curves, AnimationCurve.evaluate is closed source
// SOLUTION: Spend $35 to buy https://assetstore.unity.com/packages/tools/gui/runtime-curve-editor-11835 and get the curve's equation

// PROBLEM 2: I don't have $35 and I don't even know if the asset will work
// SOLUTION: Using Unity, bake the possible values of the AnimationCurve, then interpolate between those values in this class

// Example:
// val curve = BakedAnimationCurve.valueOf("0#100") // Linear func

import org.jetbrains.annotations.NotNull;

public class BakedAnimationCurve {
    float[] values;
    public float multiplier;

    public BakedAnimationCurve(float[] values) {
        this.values = values;
        this.multiplier = 100f;
    }
    public BakedAnimationCurve(float[] values, float multiplier) {
        this.values = values;
        this.multiplier = multiplier;
    }

    @NotNull
    public static BakedAnimationCurve valueOf(String string) {
        return BakedAnimationCurve.valueOf("#", string);
    }

    @NotNull
    public static BakedAnimationCurve valueOf(String separator, String string) {
        return BakedAnimationCurve.valueOf(separator, string, 100f);
    }

    @NotNull
    public static BakedAnimationCurve valueOf(String separator, String string, float multiplier) {
        String[] separated = string.split(separator);
        float[] values = new float[separated.length];

        for (int i = 0; i < separated.length; i++)
            values[i] = Float.parseFloat(separated[i]);

        return new BakedAnimationCurve(values, multiplier);
    }

    public float evaluate(float t) {
        if (values.length < 2)
            throw new IllegalArgumentException("Values array must have at least 2 values");

        float percent = Math.max(0f, Math.min(1f, t)) * (values.length - 1f);
        float rem = percent % 1f;
        // We have a baked value already
        if (rem == 0f)
            return values[(int)percent] / multiplier;

        // We're trying to get a value in between two baked values, interpolate between them
        float a = values[(int)Math.floor(percent)];
        float b = values[(int)Math.ceil(percent)];

        return (rem * (b - a) + a) / multiplier;
    }
}
