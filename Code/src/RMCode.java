import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;

public class RMCode implements IECC {

    private int r;
    private int k;
    private int lgr;

    private static final int[][] RM_DIMS = {
            null, null,
            {1, 3},
            {1, 4, 7},
            {1, 5, 11, 15},
            {0, 0, 16, 26, 31}};

    RMCode(int k, int r){
        this.k = k;
        this.r = r;
    }

    private static BigInteger encode(BigInteger word, int r) {

        int length = (int)Math.pow(2, r);
        BigInteger code = new BigInteger("0");

        for (int i = 0; i < length; i++) {
            String iBinary = Integer.toBinaryString(i);
            iBinary = fillZeroLeft(iBinary, r);
            iBinary = "1" + iBinary;
            int sum = 0;
            for (int j = 0; j < r + 1; j++) {
                int bit = word.testBit(j) ? 1 : 0;
                sum += bit * Character.getNumericValue((iBinary.charAt(r - j)));
            }
            sum %= 2;
            if (sum == 1) {
                code = code.setBit(i);
            }
        }
        return code;
    }

    private static BigInteger doDec(BigInteger word,int len) {

        String wordBin = word.toString(2);
        wordBin = fillZeroLeft(wordBin, (int)Math.pow(2, len));

        ArrayList<Integer> Flist = new ArrayList<>();
        for (int i = 0; i < wordBin.length(); i++) {
            if (wordBin.charAt(i) == '1') {
                Flist.add(-1);
            }
            else {
                Flist.add(1);
            }
        }

        String iBinary;

        for (int n = len - 1; n >= 0; n--) { 
            ArrayList<Integer> Slist = new ArrayList<>();
            for (int i = 0; i < Math.pow(2, len); i++) { 
                iBinary = Integer.toBinaryString(i);
                iBinary = fillZeroLeft(iBinary, len);

                int posK = Integer.parseInt(Character.toString(iBinary.charAt(len - 1 - n)));

                if (posK == 1) {
                    Slist.add(Flist.get(i) - Flist.get(i - (int)Math.pow(2, n)));
                }
                else {
                    Slist.add(Flist.get(i) + Flist.get(i + (int)Math.pow(2, n)));
                }
            }
            Flist = Slist;  
        }

        ArrayList<Integer> Fpos = new ArrayList<>();
        for (Integer i : Flist) {
            Fpos.add(Math.abs(i));
        }

        int maxF = Collections.max(Fpos);

        int wordFin;
        int maximus = Fpos.indexOf(maxF);

        if (Flist.get(maximus) < 0) {
            wordFin = maximus + (int)Math.pow(2, len);
        }
        else {
            wordFin = maximus;
        }

        return encode(new BigInteger(String.valueOf(wordFin)), len);
    }


    private static BigInteger doDone(BigInteger code, int len) {
        BigInteger word = new BigInteger("0");

        if (code.testBit(0)) {
            code = code.not();
            word = word.setBit(len);
        }

        for (int i = 0; i < len; i++) {
            word = code.testBit((int)Math.pow(2, i)) ? word.setBit(i) : word.clearBit(i);
        }
        return word;
    }


    private static String fillZeroLeft(String sBinary, int length) {
        StringBuilder sBinaryBuilder = new StringBuilder(sBinary);
        while (sBinaryBuilder.length() < length) {
            sBinaryBuilder.insert(0, "0");
        }
        sBinary = sBinaryBuilder.toString();
        return sBinary;
    }



    private static BitSet convertTo(BigInteger bs) {
        long bi = bs.longValue();
        return BitSet.valueOf(new long[]{bi});
    }

    private static BigInteger convertFrom(BitSet bs,int len) {
        long value = 0L;
        for (int i = 0; i < len; ++i) {
            value += bs.get(i) ? (1L << i) : 0L;
        }
        return BigInteger.valueOf(value);

    }

    @Override
    public int getLength() {
        return 1 << k;
    }

    @Override
    public int getDimension() {
        return RM_DIMS[k][r];
    }

    @Override
    public BitSet encode(BitSet plaintext, int len) {
        len = len+1;
        if (len == 2){
            len = len+1;
        }
        this.lgr = len;
        BigInteger word = convertFrom(plaintext, len);
        BigInteger code = encode(word, len);
        System.out.println("enc Passed");

        return convertTo(code);
    }

    @Override
    public BitSet decodeAlways(BitSet codetext, int len) {
        len = codetext.length();
        BigInteger dec = convertFrom(codetext, len);
        BigInteger ult;
        ult = doDec(dec, lgr);
        BigInteger fin;
        fin = doDone(ult, lgr);
        System.out.println("dec Passed");
        return convertTo(fin);
    }

    @Override
    public BitSet decodeIfUnique(BitSet codetext, int len) throws UncorrectableErrorException {
        len = codetext.length();
        BigInteger dec = convertFrom(codetext, len);
        BigInteger ult;
        ult = doDec(dec, lgr);
        BigInteger fin;
        fin = doDone(ult, lgr);
        System.out.println("dec Passed");

        return convertTo(fin);
    }
}
