package Matrix;

public final class Matrix {
    private final int[] packedValues;
    private final int size;

    public Matrix(int size) {
        this.size = size;
        this.packedValues = new int[size * size];
    }
    public Matrix(int[] values) {
        this.size = (int) Math.sqrt(values.length);
        this.packedValues = values;
    }
    public int get(int row, int col) {
        return packedValues[row * this.size + col];
    }

    public int[] getPackedValues() {
        return packedValues;
    }

    public int getSize() {
        return this.size;
    }

    public int[][] unpackMatrix(int size) {
        int k = 0;
        int[][] unpack = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                unpack[i][j] = this.packedValues[k];
                k++;
            }
        }
        return unpack;
    }

    public int[] packMatrix(int size) {
        int k = 0;
        int[] packed = new int[size * size];
        int[][] a = unpackMatrix(this.size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                packed[k] = a[i][j];
                k++;
            }
        }
        return packed;
    }

    public Matrix multiplyMatrix(Matrix b, int size){
        Matrix c = new Matrix(size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    c.increment(i, j, this.get(i, k) * b.get(k, j));
                }
            }
        }
        return c;
    }

    public void set(int row, int col, int val) {
        packedValues[row * size + col] = val;
    }

    public void increment(int row, int col, int val) {
        packedValues[row * size + col] += val;
    }

    public void incrementMatrix(Matrix val){
        for(int i = 0; i < val.getSize(); ++i){
            for(int j = 0; j < val.getSize(); ++j){
                this.increment(i,  j, val.get(i, j));
            }
        }
    }

    public void initialise() {
        for (int i = 0; i < packedValues.length; i++) {
            packedValues[i] = (int) (Math.random() * 10);
        }
    }

    public void show() {
        System.out.println("======================================================================================");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(packedValues[i * size + j] + " ");
            }
            System.out.println();
        }
        System.out.println("======================================================================================");
    }
}
