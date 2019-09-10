package lyu.network.connect.observer;

import java.util.Observable;

public class NetworkObservable extends Observable {

        @Override
        public void notifyObservers(Object data) {
            setChanged();
            super.notifyObservers(data);
        }
    }