package org.rosettacode;

import java.util.ArrayList;
import java.util.List;

public class Pilha {
    private List<Character> buffer = new ArrayList<Character>(400);

    public synchronized void empilha(char c) {
        this.notify();
        buffer.add(c);
    }

    public synchronized char desempilha() {
        char c;

        while ( buffer.size() == 0) {
            try {
                this.wait();
            } catch ( InterruptedException e) {
                //TODO nada
            }
        }

        c = buffer.remove(buffer.size()-1);
        return c;
    }
}
