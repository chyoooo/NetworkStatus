package lyu.network.connect;

public enum  NetStatus {

    NONE(0),

    WIFI(1),

    MOBILE(2),

    OTHER(3);

    private int value;
    private NetStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
