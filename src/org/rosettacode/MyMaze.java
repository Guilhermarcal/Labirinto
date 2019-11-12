package org.rosettacode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MyMaze {
    private int dimensionX, dimensionY; // dimensão do labirinto
    public int gridDimensionX, gridDimensionY; // dimensão da grade de saída
    public char[][] grid; // grade de saída
    public Cell[][] cells; // 2d matriz de células
    private Random random = new Random(); // O objeto aleatório

    // inicialize com x e y o mesmo
    public MyMaze(int aDimension) {
        // Inicializar
        this(aDimension, aDimension);
    }

    // constructor
    public MyMaze(int xDimension, int yDimension) {
        dimensionX = xDimension;
        dimensionY = yDimension;
        gridDimensionX = xDimension * 4 + 1;
        gridDimensionY = yDimension * 2 + 1;
        grid = new char[gridDimensionX][gridDimensionY];
        init();
        generateMaze();
    }

    private void init() {
        // criar células
        cells = new Cell[dimensionX][dimensionY];
        for (int x = 0; x < dimensionX; x++) {
            for (int y = 0; y < dimensionY; y++) {
                cells[x][y] = new Cell(x, y, false); // create cell (see Cell constructor)
            }
        }
    }

    // classe interna para representar uma célula
    private class Cell {
        int x, y; // coordenadas
        // células esta célula está conectada
        ArrayList<Cell> neighbors = new ArrayList<>();
        // solucionador: se já estiver em uso
        boolean visited = false;
        // solucionador: a célula antes desta no caminho
        Cell parent = null;
        // solucionador: se usado na última tentativa de resolver o caminho
        boolean inPath = false;
        // solucionador: distância percorrida até aqui
        double travelled;
        // solucionador: distância projetada ao fim
        double projectedDist;
        // célula intransitável
        boolean wall = true;
        // se verdadeiro, ainda tem que ser usado na geração
        boolean open = true;

        // construir célula em x, y
        Cell(int x, int y) {
            this(x, y, true);
        }

        // construa Cell em x, ye com se é Wall
        Cell(int x, int y, boolean isWall) {
            this.x = x;
            this.y = y;
            this.wall = isWall;
        }

        // adicione um vizinho a esta célula, e essa célula como um vizinho à outra
        void addNeighbor(Cell other) {
            if (!this.neighbors.contains(other)) { // evitar duplicatas
                this.neighbors.add(other);
            }
            if (!other.neighbors.contains(this)) { // evitar duplicatas
                other.neighbors.add(this);
            }
        }

        // usado em updateGrid ()
        boolean isCellBelowNeighbor() {
            return this.neighbors.contains(new Cell(this.x, this.y + 1));
        }

        // usado em updateGrid ()
        boolean isCellRightNeighbor() {
            return this.neighbors.contains(new Cell(this.x + 1, this.y));
        }

        // representação celular útil
        @Override
        public String toString() {
            return String.format("Cell(%s, %s)", x, y);
        }

        // equivalência útil de células
        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Cell)) return false;
            Cell otherCell = (Cell) other;
            return (this.x == otherCell.x && this.y == otherCell.y);
        }

        // deve ser substituído por iguais
        @Override
        public int hashCode() {
            // método aleatório de código de hash projetado para ser geralmente único
            return this.x + this.y * 256;
        }
    }

    // gerar a partir do canto superior esquerdo (na computação, o y aumenta frequentemente)
    private void generateMaze() {
        generateMaze(0, 0);
    }

    // gerar o labirinto a partir das coordenadas x, y
    private void generateMaze(int x, int y) {generateMaze(getCell(x, y)); // gerar a partir da celula
    }

    private void generateMaze(Cell startAt) {
        // não gere a partir da célula não existe
        if (startAt == null) return;
        startAt.open = false; // indica célula fechada para geração
        ArrayList<Cell> cells = new ArrayList<>();
        cells.add(startAt);

        while (!cells.isEmpty()) {
            Cell cell;
            // isto é para reduzir, mas não eliminar completamente o número
            // de longos salões de torção com galhos curtos e fáceis de detectar
            // o que resulta em labirintos fáceis
            if (random.nextInt(10) == 0)
                cell = cells.remove(random.nextInt(cells.size()));
            else cell = cells.remove(cells.size() - 1);
            // para coleção
            ArrayList<Cell> neighbors = new ArrayList<>();
            // células que poderiam ser vizinhas
            Cell[] potentialNeighbors = new Cell[]{
                    getCell(cell.x + 1, cell.y),
                    getCell(cell.x, cell.y + 1),
                    getCell(cell.x - 1, cell.y),
                    getCell(cell.x, cell.y - 1)
            };
            for (Cell other : potentialNeighbors) {
                // pule se estiver do lado de fora, for uma parede ou não estiver aberto
                if (other == null || other.wall || !other.open) continue;
                neighbors.add(other);
            }
            if (neighbors.isEmpty()) continue;
            // get random cell
            Cell selected = neighbors.get(random.nextInt(neighbors.size()));
            // adicionar como vizinho
            selected.open = false; // indica célula fechada para geração
            cell.addNeighbor(selected);
            cells.add(cell);
            cells.add(selected);
        }
    }

    // usado para obter uma célula em x, y; retorna nulo fora dos limites
    public Cell getCell(int x, int y) {
        try {
            return cells[x][y];
        } catch (ArrayIndexOutOfBoundsException e) { // sair dos limites
            return null;
        }
    }

    public void solve() {
        // resolução padrão superior esquerda para baixo direita
        this.solve(0, 0, dimensionX - 1, dimensionY - 1);
    }

    // resolver o labirinto a partir do estado inicial (algoritmo A-star)
    public void solve(int startX, int startY, int endX, int endY) {
        // re-inicializar células para encontrar o caminho
        for (Cell[] cellrow : this.cells) {
            for (Cell cell : cellrow) {
                cell.parent = null;
                cell.visited = false;
                cell.inPath = false;
                cell.travelled = 0;
                cell.projectedDist = -1;
            }
        }
        // células ainda estão sendo consideradas
        ArrayList<Cell> openCells = new ArrayList<>();
        // célula sendo considerada
        Cell endCell = getCell(endX, endY);
        if (endCell == null) return; // saia se terminar fora dos limites
        { // bloco anônimo para excluir o início, porque não usado posteriormente
            Cell start = getCell(startX, startY);
            if (start == null) return; // saia se começar fora dos limites
            start.projectedDist = getProjectedDistance(start, 0, endCell);
            start.visited = true;
            openCells.add(start);
        }
        boolean solving = true;
        while (solving) {
            if (openCells.isEmpty()) return; // sair, nenhum caminho
            // classifique openCells de acordo com a menor distância projetada
            Collections.sort(openCells, new Comparator<Cell>() {
                @Override
                public int compare(Cell cell1, Cell cell2) {
                    double diff = cell1.projectedDist - cell2.projectedDist;
                    if (diff > 0) return 1;
                    else if (diff < 0) return -1;
                    else return 0;
                }
            });
            Cell current = openCells.remove(0); // célula pop menos projetada
            if (current == endCell) break; // no final
            for (Cell neighbor : current.neighbors) {
                double projDist = getProjectedDistance(neighbor,
                        current.travelled + 1, endCell);
                if (!neighbor.visited || // ainda não visitado
                        projDist < neighbor.projectedDist) { // melhor caminho
                    neighbor.parent = current;
                    neighbor.visited = true;
                    neighbor.projectedDist = projDist;
                    neighbor.travelled = current.travelled + 1;
                    if (!openCells.contains(neighbor))
                        openCells.add(neighbor);
                }
            }
        }
        // criar caminho do fim ao começo
        Cell backtracking = endCell;
        backtracking.inPath = true;
        while (backtracking.parent != null) {
            backtracking = backtracking.parent;
            backtracking.inPath = true;
        }
    }

    // obtenha a distância projetada
    // (Um algoritmo em estrela consistente)
    public double getProjectedDistance(Cell current, double travelled, Cell end) {
        return travelled + Math.abs(current.x - end.x) +
                Math.abs(current.y - current.x);
    }

    // desenhe o labirinto
    public void updateGrid() {
        char backChar = '|', wallChar = 'I', cellChar = ' ', pathChar = ' ';
        // preencher fundo
        for (int x = 0; x < gridDimensionX; x++) {
            for (int y = 0; y < gridDimensionY; y++) {
                grid[x][y] = backChar;
            }
        }
        // construir paredes
        for (int x = 0; x < gridDimensionX; x++) {
            for (int y = 0; y < gridDimensionY; y++) {
                if (x % 4 == 0 || y % 2 == 0)
                    grid[x][y] = wallChar;
            }
        }
        // fazer representação significativa
        for (int x = 0; x < dimensionX; x++) {
            for (int y = 0; y < dimensionY; y++) {
                Cell current = getCell(x, y);
                int gridX = x * 4 + 2, gridY = y * 2 + 1;
                if (current.inPath) {
                    grid[gridX][gridY] = pathChar;
                    if (current.isCellBelowNeighbor())
                        if (getCell(x, y + 1).inPath) {
                            grid[gridX][gridY + 1] = pathChar;
                            grid[gridX + 1][gridY + 1] = backChar;
                            grid[gridX - 1][gridY + 1] = backChar;
                        } else {
                            grid[gridX][gridY + 1] = cellChar;
                            grid[gridX + 1][gridY + 1] = backChar;
                            grid[gridX - 1][gridY + 1] = backChar;
                        }
                    if (current.isCellRightNeighbor())
                        if (getCell(x + 1, y).inPath) {
                            grid[gridX + 2][gridY] = pathChar;
                            grid[gridX + 1][gridY] = pathChar;
                            grid[gridX + 3][gridY] = pathChar;
                        } else {
                            grid[gridX + 2][gridY] = cellChar;
                            grid[gridX + 1][gridY] = cellChar;
                            grid[gridX + 3][gridY] = cellChar;
                        }
                } else {
                    grid[gridX][gridY] = cellChar;
                    if (current.isCellBelowNeighbor()) {
                        grid[gridX][gridY + 1] = cellChar;
                        grid[gridX + 1][gridY + 1] = backChar;
                        grid[gridX - 1][gridY + 1] = backChar;
                    }
                    if (current.isCellRightNeighbor()) {
                        grid[gridX + 2][gridY] = cellChar;
                        grid[gridX + 1][gridY] = cellChar;
                        grid[gridX + 3][gridY] = cellChar;
                    }
                }
            }
        }
        grid[this.gridDimensionX - 3][this.gridDimensionY - 2] = 'Q';
    }

    // simplesmente imprime o mapa
    public void draw() {
        System.out.print(this);
    }

    public synchronized void drawNew(){
        for (int i = 0; i < gridDimensionY; i++) {
            for (int j = 0; j < gridDimensionX; j++) {
                System.out.print(grid[j][i]);
            }
            System.out.println();
        }
    }

    // forma uma representação significativa
    @Override
    public String toString() {
        updateGrid();
        String output = "";
        for (int y = 0; y < gridDimensionY; y++) {
            for (int x = 0; x < gridDimensionX; x++) {
                output += grid[x][y];
            }
            output += "\n";
        }
        return output;
    }
}