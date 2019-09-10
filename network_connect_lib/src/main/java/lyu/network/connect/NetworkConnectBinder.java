package lyu.network.connect;

public interface NetworkConnectBinder<T> {

    void bind(T t);

    void unBind(T t);

}
