package World16Economy.Utils;

import World16Economy.Main.Main;

public class API {

    private Main plugin;

    //Finals
    public static final Integer VERSION = 1;
    public static final String DATE_OF_VERSION = "7/15/2019";
    public static final String PREFIX = "[&9World1-6Economy &r]";
    public static final String USELESS_TAG = "" + PREFIX + "->[&bUSELESS&r]";
    public static final String EMERGENCY_TAG = "" + PREFIX + "->&c[EMERGENCY]&r";
    public static final String TOO_DAMN_OLD = "Your mc version is too damn old 1.11 up too 1.14.3 please.";
    public static final String SOMETHING_WENT_WRONG = "Something went wrong.";

    public API(Main plugin) {
        this.plugin = plugin;
    }

}
