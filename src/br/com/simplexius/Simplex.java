package br.com.simplexius;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import br.com.simplexius.model.Fraction;
import br.com.simplexius.model.Pair;
import br.com.simplexius.tools.Utils;

public class Simplex {

    private static final String MIN = "min";
    private static final String MAX = "max";
    // private Fraction tableux[][];
    private Fraction CjZj[];
    private List<Integer> basis;
    private Fraction Xb[];
    private Fraction Y[][];
    private Fraction BInv[][];
    private int nConstraints;
    private int nVars;
    private String fType;
    private Fraction zValue;

    private Fraction tableux[][];
    private String latexCode;

    // BASIS CjZj
    // BASIS Xb Y BInv
    public Simplex() {

    }

    public Simplex(String fType, Fraction Z[], Fraction A[][], String relations[], Fraction b[], int nConstraints,
            int nVars) {
        // treat exceptions about dimensions
        this.nConstraints = nConstraints;
        this.nVars = nVars;
        this.CjZj = Z;
        this.Y = A;
        this.Xb = b;
        this.fType = fType;
        this.zValue = new Fraction();        
        ConvertAxbInTableux(Z, A, relations, b);
        this.tableux = createTableux();
        System.out.println(Utils.matrixToStr(tableux, 4));

    }

    private void ConvertAxbInTableux(Fraction Z[], Fraction[][] A, String[] relations, Fraction[] b) {
        // TODO: if on of those number were negative, multiply the entire line
        // for -1, including Y Matrix
        // Var's number including artificial vars.
        int nVars = this.nVars + this.nConstraints;
        this.BInv = new Fraction[this.nConstraints][this.nConstraints];
        for (int i = 0; i < this.nConstraints; i++) {
            for (int j = 0; j < this.nConstraints; j++) {
                if (i == j) {

                    this.BInv[i][j] = new Fraction("1", "1");

                    if (relations[i].compareTo(">=") == 0) {
                        this.BInv[i][j] = new Fraction("1", "1");
                        b[i] = b[i].multip(new Fraction("-1", "1"));

                        for (int k = 0; k < A[0].length; k++) {
                            A[i][k] = A[i][k].multip(new Fraction("-1", "1"));
                        }
                    }

                } else {
                    this.BInv[i][j] = new Fraction("0", "1");
                }
            }
        }

        CjZj = new Fraction[nVars];

        for (int i = 0; i < nVars; i++) {
            if (i < Z.length) {
                if (this.fType.compareTo("min") == 0) {
                    CjZj[i] = Z[i].multip(new Fraction("-1", "1"));
                } else {
                    CjZj[i] = Z[i];
                }
            } else {
                CjZj[i] = new Fraction("0", "1");
            }
        }

        // basis
        basis = new ArrayList<Integer>();
        for (int i = 0; i < this.nConstraints; i++) {
            basis.add(this.nVars + i);
        }

    }

    @Override
    public String toString() {
        String tb[][] = getStringMatrix();

        return Utils.matrixToStr(tb, 3);
        //return Utils.matrixToStr(tableux, 3);
    }

    private String[][] getStringMatrix() {
        String tb[][] = new String[tableux.length + 1][tableux[0].length + 1];
        tb[0][0] = tb[0][1] = " ";
        tb[1][0] = "Z";
        for (int j = 2; j < tb[0].length; j++) {
            tb[0][j] = "x" + (j - 1);
        }

        for (int i = 2; i < tb.length; i++) {
            tb[i][0] = "x" + (basis.get(i - 2) + 1);
        }

        for (int i = 1; i < tb.length; i++) {
            for (int j = 1; j < tb[0].length; j++) {
                tb[i][j] = tableux[i - 1][j - 1].toString();
            }
        }

        return tb;
    }

    public Fraction[][] createTableux() {
        Fraction tb[][] = new Fraction[this.nConstraints + 1][this.nVars + this.nConstraints + 1];
        tb[0][0] = new Fraction("0");

        //costs
        for (int i = 1; i < tb[0].length; i++) {
            tb[0][i] = this.CjZj[i - 1];
        }
        // Matrix Y
        for (int i = 0; i < Y.length; i++) {
            for (int j = 0; j < Y[0].length; j++) {
                tb[i + 1][j + 1] = Y[i][j];
            }
        }
        // Matrix I
        for (int i = 0; i < BInv.length; i++) {
            for (int j = 0; j < BInv[0].length; j++) {
                tb[i + 1][j + this.nVars + 1] = BInv[i][j];
            }
        }

        // b vector
        for (int i = 1; i < tb.length; i++) {
            tb[i][0] = Xb[i - 1];
        }

        return tb;
    }

    /* Format:
	*\begin{center}
	\begin{tabular}{ |c|c|c| } 
	 \hline
	 cell1 & cell2 & cell3 \\ \hline
	 cell4 & cell5 & cell6 \\ \hline
	 cell7 & cell8 & cell9 \\
	 \hline
	\end{tabular}
	\end{center}*/
    public String tableToLaTeXCode(Pair<Integer, Integer> pivot) {
        String[][] M = getStringMatrix();
        if (pivot != null) {
            M[pivot.getFirst() + 1][pivot.getSecond() + 1] = "\\cellcolor[HTML]{FFFE65}" + M[pivot.getFirst() + 1][pivot.getSecond() + 1];
        }
        M[1][0] = "$Z$";
        String code = "\\begin{center}"
                + "\n\\begin{tabular}{ " + getColumnType(M[0].length) + " } "
                + "\n\\hline\n";
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length - 1; j++) {
                String str = M[i][j];
                if (str.charAt(0) == 'x') {
                    str = str.substring(1);
                    str = "$x_{" + str;
                    str += "}$";
                }
                code += str + " & ";

            }
            String str = M[i][M[0].length - 1];
            if (str.charAt(0) == 'x') {
                str = str.substring(1);
                str = "$x_{" + str;
                str += "}$";
            }
            code += str + " \\\\ \\hline\n";
        }

        code += "\\end{tabular}\n"
                + "\\end{center}\n";

        return code;
    }

    private String getColumnType(int length) {
        /*Format to lenght equals to 3 |c|c|c|*/
        String str = "|";

        for (int i = 0; i < length; i++) {
            str += "c|";
        }

        return str;
    }
    
    private boolean cjzjIsPrimalFeasible() {
        for (int j = 1; j < tableux[0].length; j++) {
            if (tableux[0][j].getValue() > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean isPrimalFeasible() {
        if ( cjzjIsPrimalFeasible() && !isDualFeasible() )
            return true;
        return false;
    }

    private boolean isDualFeasible() {
        for (int i = 1; i < tableux.length; i++) {
            if (tableux[i][0].isSignal()) {
                return true;
            }
        }
        return false;
    }
    // modify tableux by pivoting

    private void pivoting(Pair<Integer, Integer> pivotCoord) {

        int x = pivotCoord.getFirst();
        int y = pivotCoord.getSecond();

        Fraction pivot = tableux[x][y];
        // line x divistion by pivot
        for (int i = 0; i < tableux[0].length; i++) {
            tableux[x][i] = tableux[x][i].div(pivot);
        }

        // 
        for (int i = 0; i < tableux.length; i++) {
            if (tableux[i][y].getValue() != 0 && x != i) {
                // for to Y
                Fraction Yiy = tableux[i][y];
                for (int j = 0; j < tableux[0].length; j++) {

                    tableux[i][j] = tableux[i][j].subtract(tableux[x][j].multip(Yiy));
                }
            }
        }
        // reajust basis
        basis.set(x - 1, y - 1);
    }

    private Pair<Integer, Integer> getDualPivot() {
        Pair<Integer, Integer> p = null;
        int x = getMostNegativeXb();
        int y = -1;
        double menor = Double.MAX_VALUE;
        for (int j = 1; j < tableux[0].length; j++) {
            if (tableux[x][j].getValue() < 0) {
                double c = tableux[0][j].getValue() / tableux[x][j].getValue();
                if (c < menor) {
                    menor = c;
                    y = j;
                }
            }
        }
        if (x != -1 && y != -1) {
            p = new Pair(x, y);
        }
        return p;
    }

    private int getMostNegativeXb() {
        int x = -1;
        double maior = Double.MIN_VALUE;
        for (int i = 1; i < tableux.length; i++) {
            if (Math.abs(tableux[i][0].getValue()) > maior) {
                maior = Math.abs(tableux[i][0].getValue());
                x = i;
            }
        }
        return x;
    }

    private Pair<Integer, Integer> getPrimalPivot() {
        Pair<Integer, Integer> p = new Pair<Integer, Integer>();

        int y = getMostGreatCjZj();
        int x = -1;
        double menor = Double.MAX_VALUE;
        for (int i = 1; i < tableux.length; i++) {
            if (tableux[i][y].getValue() > 0) {
                double c = tableux[i][0].getValue() / tableux[i][y].getValue();
                if (c < menor) {
                    menor = c;
                    x = i;
                }
            }
        }

        if (x != -1 && y != -1) {
            p = new Pair(x, y);
        }
        
        return p;
    }

    public int getMostGreatCjZj() {
        int x = -1;
        double maior = Double.MIN_VALUE;
        for (int j = 0; j < tableux[0].length; j++) {
            if (tableux[0][j].getValue() > maior) {
                maior = tableux[0][j].getValue();
                x = j;
            }
        }
        return x;
    }

    public void solve() {
        this.latexCode = Utils.beginDocLaTeX + "\n" + systemToString();
        int i = 0;
        while (isPrimalFeasible()) {
//            System.out.println("\niteration " + i);
            if (i == 0) {
                this.latexCode += "{\n\\bf PRIMAL SIMPLEX é viável:}\n" + "\nQuadro inicial:\n";
            } else {
                this.latexCode += "Iteração " + i + ":\n";
            }
//            System.out.println(toString());
            Pair<Integer, Integer> pivot = getPrimalPivot();
            //System.out.println("PIVOT: " + pivot);
            latexCode += "\n" + tableToLaTeXCode(pivot) + "\n";
//            System.out.println("Pivot:
            //tableux" + pivot + " = " + tableux[pivot.getFirst()][pivot.getSecond()] );
            //highlight pivot on tableux
            pivoting(pivot);
            i++;
        }
        if (i == 0) {
            this.latexCode += "\n{\\bf Primal Simplex não é viável}\n";
            this.latexCode += "\n{\\bf DUAL SIMPLEX:}\n";
        }

        while (isDualFeasible()) {
//            System.out.println("\niteration " + i);
//            System.out.println(toString());
            this.latexCode += "\nIteração " + i + ":\n";
            Pair<Integer, Integer> pivot = getDualPivot();
            latexCode += "\n" + tableToLaTeXCode(pivot) + "\n";
//            System.out.println("Pivot: tableux" + pivot + " = " + tableux[pivot.getFirst()][pivot.getSecond()] );
            //highlight pivot on tableux
            System.out.println(pivot);
            System.out.println(Utils.matrixToStr(tableux, 4));
            pivoting(pivot);
            i++;
        }

        if (i == 0) {
            latexCode += "Dual Simplex não é viável\n";
            latexCode += "Sistema impossível e indeterminado\n";
        } else {
            /*System.out.println("\niteration " + j + " Optimum Tableux: ");
            System.out.println(toString());*/
            latexCode += "Iteração " + i + "({\\bf Quadro ótimo}):\n";
            latexCode += "\n" + tableToLaTeXCode(null) + "\n";
        }
        latexCode += Utils.endDocLaTeX;
        //System.out.println(latexCode);
        //Runtime.getRuntime().exec( "comand" ).waitFor();

    }

    /*private void solvePrimal() {
        while (isPrimalFeasible()) {
            Pair<Integer, Integer> pivot = getPrimalPivot();
            pivoting(pivot);
        }
    }*/
    private String systemToString() {
        String sys = "";
        sys = fType.compareTo(MIN) == 0 ? "Minimizar " : "Maximizar ";
        sys += "$Z = ";
        for (int i = 0; i < CjZj.length; i++) {
            if (CjZj[i].getValue() != 0) {
                if (CjZj[i].isSignal()) {
                    sys += " -" + CjZj[i].toString() + "x_{" + (i + 1) + "}";
                } else if (i == 0) {
                    sys += " " + CjZj[i].toString() + "x_{" + (i + 1) + "}";
                } else {
                    sys += " +" + CjZj[i].toString() + "x_{" + (i + 1) + "}";
                }
            }
        }
        sys += "Sujeito à:\n\n";

        for (int i = 0; i < Y.length; i++) {
            for (int j = 0; j < Y[0].length; j++) {
                if (Y[i][j].getValue() != 0) {
                    if (Y[i][j].isSignal()) {
                        sys += " -" + Y[i][j].toString() + "x_{" + (j + 1) + "}";
                    } else if (j == 0) {
                        sys += " " + Y[i][j].toString() + "x_{" + (j + 1) + "}";
                    } else {
                        sys += " +" + Y[i][j].toString() + "x_{" + (j + 1) + "}";
                    }
                }
            }
        }

        sys += "$\n\n";
        return "\n";
    }

    /*public Fraction solve(){
     *//*solvePrimal();*//*
        solveDual();
        return this.zValue;
	}*/

    public String getLatexCode() {
        return latexCode;
    }

    public String getfType() {
        return fType;
    }

    public int getnVars() {
        return nVars;
    }

    public void setnVars(int nVars) {
        this.nVars = nVars;
    }

    public int getnConstraints() {
        return nConstraints;
    }

    public void setnConstraints(int nConstraints) {
        this.nConstraints = nConstraints;
    }

    public Fraction[] getCjZj() {
        return CjZj;
    }

    public void setCjZj(Fraction[] cjZj) {
        CjZj = cjZj;
    }

    public List<Integer> getBasis() {
        return basis;
    }

    public void setBasis(List<Integer> basis) {
        this.basis = basis;
    }

    public Fraction[] getXb() {
        return Xb;
    }

    public void setXb(Fraction xb[]) {
        Xb = xb;
    }

    public Fraction[][] getY() {
        return Y;
    }

    public void setY(Fraction y[][]) {
        Y = y;
    }

    public Fraction[][] getBInv() {
        return BInv;
    }

    public void setBInv(Fraction bInv[][]) {
        BInv = bInv;
    }
}
