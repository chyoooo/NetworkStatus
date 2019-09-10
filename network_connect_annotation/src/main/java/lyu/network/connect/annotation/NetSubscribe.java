package lyu.network.connect.annotation;

public enum NetSubscribe {

    NONE(0),

    ALL(1),

    WIFI(2),

    MOBILE(3),

    OTHER(4);

    private int value = 0;

    private NetSubscribe(int value) {     //必须是private的，否则编译错误
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}