package com.koralix.oneforall.utils;

import net.minecraft.text.Text;

public interface IntoText {
    Text toText();

    static IntoText of(Object object) {
        if (object instanceof IntoText) return (IntoText) object;
        if (object instanceof Text) return () -> (Text) object;
        return () -> Text.of(object.toString());
    }

    static Text into(Object object) {
        return IntoText.of(object).toText();
    }
}
