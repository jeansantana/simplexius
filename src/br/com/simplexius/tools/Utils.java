package br.com.simplexius.tools;

import br.com.simplexius.model.Fraction;
import de.nixosoft.jlr.JLRConverter;
import de.nixosoft.jlr.JLRGenerator;
import de.nixosoft.jlr.JLROpener;
import java.io.File;

public class Utils {

    public static final String beginDocLaTeX = "\\documentclass[a4paper,12pt]{article}\n"
            + "\\usepackage[top=1cm, left=1cm, right=1cm, bottom=1cm, landscape]{geometry}\n"
            + "\\usepackage[brazilian]{babel}\n"
            + "\\usepackage[utf8]{inputenc}\n"
            + "\\usepackage[table,xcdraw]{xcolor}\n"
            + "\\title{Método Simplex Primal Dual}\n"
            + "\\author{Jean Silva}\n"
            + "\\begin{document}\n"
            + "\\maketitle\n";

    public static final String endDocLaTeX = "\\end{document}\n";

    public static String matrixToStr(Fraction M[][], int nSpace) {
        String res = "";
        int gLi = getTheMostGreatItem(M);
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                res += M[i][j] + getStringNSpaces((gLi - (M[i][j].toString()).length()) + nSpace);
            }
            res += "\n";
        }
        return res;
    }

    private static int getTheMostGreatItem(Fraction[][] M) {
        int gLi = 0;

        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                int lng = M[i][j].toString().length();
                if (lng > gLi) {
                    gLi = lng;
                }
            }
        }

        return gLi;
    }

    public static String matrixToStr(String M[][], int nSpace) {
        String res = "";
        int gLi = getTheMostGreatItem(M);
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                res += M[i][j] + getStringNSpaces((gLi - M[i][j].length()) + nSpace);
            }
            res += "\n";
        }
        return res;
    }

    private static int getTheMostGreatItem(String[][] M) {
        int gLi = 0;

        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M[0].length; j++) {
                int lng = M[i][j].length();
                if (lng > gLi) {
                    gLi = lng;
                }
            }
        }

        return gLi;
    }

    public static String getStringNSpaces(int nSpace) {
        String spaces = "";
        for (int i = 0; i < nSpace; i++) {
            spaces += " ";
        }
        return spaces;
    }

    public static void printVectorFraction(Fraction[] cjZj) {
        for (int i = 0; i < cjZj.length; i++) {
            System.out.print(cjZj[i] + " ");
        }
        System.out.println();
    }

    public static Fraction[][] transposeMatrix(Fraction M[][]) {
        Fraction A[][] = new Fraction[M[0].length][M.length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                A[i][j] = M[j][i];
            }
        }

        return A;
    }

    public static void exportToPDF(String fileName, String LaTeXCode, File path) {

        if (!path.isDirectory()) {
            path.mkdir();
        }

        File myFile = new File(path + File.separator + fileName + ".tex");

        try {
            JLRConverter converter = new JLRConverter(path);

            converter.parse(LaTeXCode, myFile);
            
            JLRGenerator pdfGen = new JLRGenerator();

            if (!pdfGen.generate(myFile, path, path)) {
                System.out.println(pdfGen.getErrorMessage());
            }

            JLROpener.open(pdfGen.getPDF());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}
