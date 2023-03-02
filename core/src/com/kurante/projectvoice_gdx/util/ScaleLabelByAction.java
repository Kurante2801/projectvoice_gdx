package com.kurante.projectvoice_gdx.util;

import com.badlogic.gdx.scenes.scene2d.actions.RelativeTemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ScaleLabelByAction extends RelativeTemporalAction {
    private float amountX, amountY;

    protected void updateRelative (float percentDelta) {
        ((Label)target).setFontScale(amountX * percentDelta, amountY * percentDelta);
    }

    public void setAmount (float x, float y) {
        amountX = x;
        amountY = y;
    }

    public void setAmount (float scale) {
        amountX = scale;
        amountY = scale;
    }

    public float getAmountX () {
        return amountX;
    }

    public void setAmountX (float x) {
        this.amountX = x;
    }

    public float getAmountY () {
        return amountY;
    }

    public void setAmountY (float y) {
        this.amountY = y;
    }

}