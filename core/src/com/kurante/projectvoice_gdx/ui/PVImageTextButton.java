package com.kurante.projectvoice_gdx.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Scaling;
import com.kurante.projectvoice_gdx.util.UserInterface;
import com.kurante.projectvoice_gdx.util.extensions.LabelKt;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PVImageTextButton extends Button {
    private final Image image;
    private Label label;
    private final HorizontalGroup group;
    private ImageTextButtonStyle style;

    public PVImageTextButton(@Null String text, Skin skin) {
        this(text, skin.get(ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public PVImageTextButton(@Null String text, Skin skin, String styleName) {
        this(text, skin.get(styleName, ImageTextButtonStyle.class));
        setSkin(skin);
    }

    public PVImageTextButton(@Null String text, ImageTextButtonStyle style) {
        super(style);
        this.style = style;

        group = new HorizontalGroup();
        add(group);

        image = newImage();

        label = newLabel(text, new LabelStyle(style.font, style.fontColor));
        label.setAlignment(Align.center);

        group.addActor(image);
        group.addActor(label);

        setStyle(style);
        setSize(getPrefWidth(), getPrefHeight());

        setColor(UserInterface.INSTANCE.getMainColor());
        UserInterface.INSTANCE.getMainColorEvent().plusAssign(mainColor);
        //pad(UserInterface.INSTANCE.scaledUi(8f));
    }

    protected Image newImage() {
        return new Image((Drawable) null, Scaling.fit);
    }

    protected Label newLabel(String text, Label.LabelStyle style) {
        return new Label(text, style);
    }

    public void setStyle(ButtonStyle style) {
        if (!(style instanceof ImageTextButtonStyle))
            throw new IllegalArgumentException("style must be a ImageTextButtonStyle.");
        this.style = (ImageTextButtonStyle) style;
        super.setStyle(style);

        if (image != null) updateImage();

        if (label != null) {
            ImageTextButtonStyle textButtonStyle = (ImageTextButtonStyle) style;
            LabelStyle labelStyle = label.getStyle();
            labelStyle.font = textButtonStyle.font;
            labelStyle.fontColor = getFontColor();
            label.setStyle(labelStyle);
        }
    }

    public ImageTextButtonStyle getStyle() {
        return style;
    }

    protected @Null Drawable getImageDrawable() {
        if (isDisabled() && style.imageDisabled != null) return style.imageDisabled;
        if (isPressed()) {
            if (isChecked() && style.imageCheckedDown != null) return style.imageCheckedDown;
            if (style.imageDown != null) return style.imageDown;
        }
        if (isOver()) {
            if (isChecked()) {
                if (style.imageCheckedOver != null) return style.imageCheckedOver;
            } else {
                if (style.imageOver != null) return style.imageOver;
            }
        }
        if (isChecked()) {
            if (style.imageChecked != null) return style.imageChecked;
            if (isOver() && style.imageOver != null) return style.imageOver;
        }
        return style.imageUp;
    }

    protected void updateImage() {
        image.setDrawable(getImageDrawable());
    }

    protected @Null Color getFontColor() {
        if (isDisabled() && style.disabledFontColor != null) return style.disabledFontColor;
        if (isPressed()) {
            if (isChecked() && style.checkedDownFontColor != null)
                return style.checkedDownFontColor;
            if (style.downFontColor != null) return style.downFontColor;
        }
        if (isOver()) {
            if (isChecked()) {
                if (style.checkedOverFontColor != null) return style.checkedOverFontColor;
            } else {
                if (style.overFontColor != null) return style.overFontColor;
            }
        }
        boolean focused = hasKeyboardFocus();
        if (isChecked()) {
            if (focused && style.checkedFocusedFontColor != null)
                return style.checkedFocusedFontColor;
            if (style.checkedFontColor != null) return style.checkedFontColor;
            if (isOver() && style.overFontColor != null) return style.overFontColor;
        }
        if (focused && style.focusedFontColor != null) return style.focusedFontColor;
        return style.fontColor;
    }

    public void draw(Batch batch, float parentAlpha) {
        updateImage();
        label.getStyle().fontColor = getFontColor();
        super.draw(batch, parentAlpha);
    }

    public Image getImage() {
        return image;
    }

    public void setLabel(Label label) {
        if (this.label != null)
            group.removeActor(this.label);
        group.addActor(label);
        this.label = label;
    }

    public Label getLabel() {
        return label;
    }

    public HorizontalGroup getGroup() {
        return group;
    }

    public void setText(CharSequence text) {
        label.setText(text);
    }

    public CharSequence getText() {
        return label.getText();
    }

    @Override
    @NotNull
    public String toString() {
        String name = getName();
        if (name != null) return name;
        String className = getClass().getName();
        int dotIndex = className.lastIndexOf('.');
        if (dotIndex != -1) className = className.substring(dotIndex + 1);
        return (className.indexOf('$') != -1 ? "PVImageTextButton " : "") + className + ": " + image.getDrawable() + " "
                + label.getText();
    }

    private final Function1<? super Color, kotlin.Unit> mainColor = new Function1<Color, Unit>() {
        @Override
        public Unit invoke(Color color) {
            addAction(Actions.color(color, 0.25f));
            return null;
        }
    };

    @Override
    public void setDisabled(boolean disabled) {
        super.setDisabled(disabled);

        if (disabled) {
            UserInterface.INSTANCE.getMainColorEvent().minusAssign(mainColor);
            setColor(UserInterface.INSTANCE.getFOREGROUND1_COLOR());
        } else {
            UserInterface.INSTANCE.getMainColorEvent().plusAssign(mainColor);
            setColor(UserInterface.INSTANCE.getMainColor());
        }
    }

    @Override
    public float getPrefWidth() {
        return Math.max(super.getPrefWidth(), UserInterface.INSTANCE.scaledUI(160f));
    }

    @Override
    public float getPrefHeight() {
        return Math.max(super.getPrefHeight(), UserInterface.INSTANCE.scaledUI(48f));
    }

    public void setLocalizedText(String key) {
        LabelKt.setLocalizedText(label, key);
    }

    public void localizationChanged(Function1<? super I18NBundle, kotlin.Unit> callback) {
        LabelKt.localizationChanged(label, callback);
    }
}
