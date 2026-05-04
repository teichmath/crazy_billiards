package edu.rice.comp504.model;

import java.util.LinkedList;

public class BallObservable {

    private LinkedList<BallObserver> obs = new LinkedList<>();
    private boolean changed = false;

    public synchronized void addObserver(BallObserver o) {
        if (!obs.contains(o)) obs.add(o);
    }

    public synchronized void deleteObserver(BallObserver o) {
        obs.remove(o);
    }

    public synchronized void deleteObservers() {
        obs.clear();
    }

    public synchronized int countObservers() {
        return obs.size();
    }

    protected synchronized void setChanged() {
        changed = true;
    }

    protected synchronized void clearChanged() {
        changed = false;
    }

    public void notifyObservers(Object arg) {
        LinkedList<BallObserver> copy;
        synchronized (this) {
            if (!changed) return;
            changed = false;
            copy = new LinkedList<>(obs);
        }
        for (BallObserver o : copy) {
            o.update(this, arg);
        }
    }
}
