package com.smarttable.listener;

import java.util.ArrayList;
import java.util.List;

public abstract  class Observable<T> {

    public final ArrayList<T> observables = new ArrayList<>();

    public void register(T observer){
        if(observer==null) throw new NullPointerException();
        synchronized(observables){
            if(!observables.contains(observer)){
                observables.add(observer);
            }
        }
    }

    public void unRegister(T observer){
        if(observer==null) throw new NullPointerException();
        if(observables.contains(observer)){
            observables.remove(observer);
        }
    }

    public void unRegisterAll(){
        synchronized(observables){
            observables.clear();
        }
    }

    public int countObservers(){
        synchronized(observables){
            return observables.size();
        }
    }

    public abstract void notifyObservers(List<T> observers);

}
