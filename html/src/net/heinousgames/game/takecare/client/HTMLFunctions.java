package net.heinousgames.game.takecare.client;

import net.heinousgames.game.takecare.Main;

/**
 * Created by stevenhanus on 6/13/18
 */
class HTMLFunctions implements Main.HTMLCustomMethods {

    @Override
    public boolean canGWTPlayOgg() {
        return gwtCanPlayOgg();
    }

    private static native boolean gwtCanPlayOgg() /*-{
        var audio = document.createElement('audio');
        return typeof audio.canPlayType === "function" && audio.canPlayType("audio/ogg") !== "";
    }-*/;
}
