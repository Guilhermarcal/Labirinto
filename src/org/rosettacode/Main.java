package org.rosettacode;
import java.util.ArrayList;
import java.util.Scanner;
public class Main{
    // run it
    public static void main(String[] args) {
        Scanner var = new Scanner(System.in);
        int tamanho = 0;
        while(tamanho <= 0){
            System.out.print("Digite o tamanho do labirinto: ");
            tamanho = var.nextInt();
            if(tamanho <= 0)
                System.out.println("Valor invalido para geração do labirinto");
        }

        MyMaze maze = new MyMaze(tamanho);
        OlaRunner r = new OlaRunner();
        r.setMaze(maze);

        ArrayList<Thread> threads = new ArrayList<>();

        Scanner thRato = new Scanner(System.in);
        System.out.print("Digite a quantidade de ratos: ");
        int quantRatos = var.nextInt();

        for(int i = 0; i < quantRatos; i++){
            threads.add(new Thread(r));
        }
        for (Thread t : threads){
            t.start();
        }

        maze.solve();
        maze.draw();

    }

}