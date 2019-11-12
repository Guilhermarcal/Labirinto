package org.rosettacode;

import java.util.ArrayList;
import java.util.Random;

public class OlaRunner implements Runnable {

    private Random random = new Random();
    private MyMaze maze;
    private ArrayList<String> vencedor = new ArrayList<>();
    private long inicio = System.currentTimeMillis();

    public synchronized void setMaze(MyMaze maze) {
        this.maze = maze;
    }

    public void andaRato(int x,int y) throws InterruptedException {

        if(maze.grid[x][y + 1] == 'Q'){
            maze.grid[x][y] = '@';
            long fim = System.currentTimeMillis();
            vencedor.add("Tempo rato: " + (fim - inicio) / 1000 + " segundos");
        }

        else if(maze.grid[x + 1][y] == 'Q'){
            maze.grid[x][y] = '@';
            long fim = System.currentTimeMillis();
            vencedor.add("Tempo rato: " + (fim - inicio) / 1000 + " segundos");
        }
        else if(maze.grid[x - 1][y] == 'Q'){
            maze.grid[x][y] = '@';
            long fim = System.currentTimeMillis();
            vencedor.add("Tempo rato: " + (fim - inicio) / 1000 + " segundos");
        }
        else if (maze.grid[x][y + 1] == ' '){
            Thread.sleep(150);
            System.out.println("Desce");
            maze.grid[x][y] = '@';
            maze.grid[x][y + 1] = 'R';
            maze.drawNew();
            andaRato(x,y+1);
        }
        else if(maze.grid[x + 1][y] == ' '){
            Thread.sleep(150);
            System.out.println("Direita");
            maze.grid[x][y] = '@';
            maze.grid[x+1][y] = 'R';
            maze.drawNew();
            andaRato(x + 1,y);
        }
        else if(maze.grid[x - 1][y] == ' '){
            Thread.sleep(150);
            System.out.println("Esquerda");
            maze.grid[x][y] = '@';
            maze.grid[x-1][y] = 'R';
            maze.drawNew();
            andaRato(x - 1,y);

        }
        else if(maze.grid[x][y-1] == ' '){
            Thread.sleep(150);
            System.out.println("Sobe");
            maze.grid[x][y] = '@';
            maze.grid[x][y - 1] = 'R';
            maze.drawNew();
            andaRato(x, y - 1);
        }
        else if(maze.grid[x][y+1] == '@') {
            Thread.sleep(150);
            System.out.println("Desce");
            maze.grid[x][y] = 'x';
            maze.grid[x][y + 1] = 'R';
            maze.drawNew();
            andaRato(x, y + 1);
        }
        else if(maze.grid[x + 1][y] == '@'){
            Thread.sleep(150);
            System.out.println("Direita");
            maze.grid[x][y] = 'x';
            maze.grid[x + 1][y] = 'R';
            maze.drawNew();
            andaRato(x + 1, y);
        }
        else if(maze.grid[x - 1][y] == '@'){
            Thread.sleep(150);
            System.out.println("Esquerda");
            maze.grid[x][y] = 'x';
            maze.grid[x - 1][y] = 'R';
            maze.drawNew();
            andaRato(x - 1, y);
        }
        else if(maze.grid[x][y-1] == 'I' || maze.grid[x][y-1] == 'x' || maze.grid[x][y-1] == '|'){
            if(maze.grid[x][y+1] == '@') {
                Thread.sleep(150);
                System.out.println("Desce");
                maze.grid[x][y] = 'x';
                maze.grid[x][y + 1] = 'R';
                maze.drawNew();
                andaRato(x, y + 1);
            }
            else if(maze.grid[x + 1][y] == '@'){
                Thread.sleep(150);
                System.out.println("Direita");
                maze.grid[x][y] = 'x';
                maze.grid[x + 1][y] = 'R';
                maze.drawNew();
                andaRato(x + 1, y);
            }
            else if(maze.grid[x - 1][y] == '@'){
                Thread.sleep(150);
                System.out.println("Esquerda");
                maze.grid[x][y] = 'x';
                maze.grid[x - 1][y] = 'R';
                maze.drawNew();
                andaRato(x - 1, y);
            }
        }
        else if(maze.grid[x][y-1] != 'I'){
            Thread.sleep(150);
            System.out.println("Sobe");
            maze.grid[x][y] = 'x';
            maze.grid[x][y - 1] = 'R';
            maze.drawNew();
            andaRato(x, y - 1);
        }



    }

    public void run() {

        int x = maze.gridDimensionX;
        int y = maze.gridDimensionY;
        int xRato = x / 2;
        int yRato = y / 2;
        int numRandomX = random.nextInt(x-2);
        int numRandomY = random.nextInt(y-2);

        if(maze.grid[numRandomX][numRandomY] == 'R'){
            numRandomX = random.nextInt(x-2);
        }
        if(numRandomX > xRato && numRandomY > yRato){
            numRandomX -= xRato;
        }
        if(numRandomX == 0 || numRandomX == 1){
            numRandomX = 2;
        }
        if(numRandomY == 0){
            numRandomY = 1;
        }
        if(maze.grid[numRandomX][numRandomY] == 'I'){
            if(maze.grid[numRandomX + 1][numRandomY] == '|'){
                maze.grid[numRandomX + 2][numRandomY] = 'R';
                numRandomX += 2;
            }
            else if(maze.grid[numRandomX - 1][numRandomY] == '|'){
                maze.grid[numRandomX - 2][numRandomY] = 'R';
                numRandomX -= 2;
            }
            else if(maze.grid[numRandomX][numRandomY + 1] == 'I'){
                if(maze.grid[numRandomX + 1][numRandomY + 1] == '|'){
                    maze.grid[numRandomX + 2][numRandomY + 1] = 'R';
                    numRandomX += 2;
                    numRandomY += 1;
                }
                else if(maze.grid[numRandomX - 1][numRandomY + 1] == '|') {
                    maze.grid[numRandomX - 2][numRandomY + 1] = 'R';
                    numRandomX -= 2;
                    numRandomY += 1;
                }
            }
            else if(maze.grid[numRandomX][numRandomY - 1] == 'I'){
                if(maze.grid[numRandomX + 1][numRandomY - 1] == '|'){
                    maze.grid[numRandomX + 2][numRandomY - 1] = 'R';
                    numRandomX += 2;
                    numRandomY -= 1;
                }
                else if(maze.grid[numRandomX - 1][numRandomY - 1] == '|'){
                    maze.grid[numRandomX - 2][numRandomY - 1] = 'R';
                    numRandomX -= 2;
                    numRandomY -= 1;
                }
            }
            else if(maze.grid[numRandomX][numRandomY + 1] == '|') {
                if (maze.grid[numRandomX + 1][numRandomY + 1] == 'I'){
                    maze.grid[numRandomX - 1][numRandomY + 1] = 'R';
                    numRandomX -= 1;
                    numRandomY += 1;
                }
                else if (maze.grid[numRandomX - 1][numRandomY + 1] == 'I'){
                    maze.grid[numRandomX + 1][numRandomY + 1] = 'R';
                    numRandomX += 1;
                    numRandomY += 1;
                }

            }
            else if(maze.grid[numRandomX][numRandomY + 1] == ' '){
                maze.grid[numRandomX][numRandomY + 1] = 'R';
                numRandomY += 1;
            }
            else if(maze.grid[numRandomX][numRandomY - 1] == ' '){
                maze.grid[numRandomX][numRandomY - 1] = 'R';
                numRandomY -= 1;
            }

        }
        else if(maze.grid[numRandomX][numRandomY] == '|'){
            if(maze.grid[numRandomX + 1][numRandomY] == 'I'){
                maze.grid[numRandomX - 1][numRandomY] = 'R';
                numRandomX -= 1;
            }
            else if(maze.grid[numRandomX - 1][numRandomY] == 'I'){
                maze.grid[numRandomX + 1][numRandomY] = 'R';
                numRandomX += 1;
            }

        }
        else {
            maze.grid[numRandomX][numRandomY] = 'R';
        }

        try {
            Thread.sleep(1500);
            andaRato(numRandomX,numRandomY);

            for(String s : vencedor){
                System.out.println(s);
            }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
