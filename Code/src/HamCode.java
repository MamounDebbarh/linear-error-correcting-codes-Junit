import java.util.BitSet;

public class HamCode implements IECC {

    private int r;

    private static final int[][] RM_DIMS = {
            null, null,
            {1, 3},
            {1, 4, 7},
            {1, 5, 11, 15},
            {0, 0, 16, 26, 31}};

    HamCode(int r){
        this.r = r;
    }


    private static int[] bitToArr(BitSet a, int len){
        int[] r = new int[len];
        for (int i = 0; i<r.length; i++){
            int myInt = a.get(i) ? 1 : 0;
            r[i] = myInt;
        }
        return r;
    }
    private static BitSet arrToBit(int a[]){
        BitSet bs = new BitSet(a.length);
        for (int i = 0; i<a.length; i++){
            bs.set(i, a[i] == 1);
        }
        return bs;

    }

    private static int getPar(int b[], int power) {
        int par = 0;
        for(int i=0 ; i < b.length ; i++) {
            if(b[i] != 2) {
                int k = i+1;
                String s = Integer.toBinaryString(k);
                int x = ((Integer.parseInt(s))/((int) Math.pow(10, power)))%10;
                if(x == 1) {
                    if(b[i] == 1) {
                        par = (par+1)%2;
                    }
                }
            }
        }
        return par;
    }

    private BitSet doEncHamming(BitSet plaintext, int len){
        int[] a = bitToArr(plaintext,len);
        int b[];

        int i=0, parity_count=0 ,j=0, k=0;
        while(i < a.length) {
            if(Math.pow(2,parity_count) == i+parity_count + 1) {
                parity_count++;
            }
            else {
                i++;
            }
        }

        b = new int[a.length + parity_count];


        for(i=1 ; i <= b.length ; i++) {
            if(Math.pow(2, j) == i) {
                b[i-1] = 2;
                j++;
            }
            else {
                b[k+j] = a[k++];
            }
        }
        for(i=0 ; i < parity_count ; i++) {
            b[((int) Math.pow(2, i))-1] = getPar(b, i);
        }

        return arrToBit(b);
//        return b;
    }


    private static BitSet doDecHamming(BitSet codetext, int len) {
        if (len == 0){
            return codetext;
        }else {
            int[] a = bitToArr(codetext, len);
            int parity_count = (int) Math.floor(Math.log(a.length) / Math.log(2)) + 1;
            int power;

            int parity[] = new int[parity_count];
            StringBuilder syndrome = new StringBuilder();
            for (power = 0; power < parity_count; power++) {
                for (int i = 0; i < a.length; i++) {

                    int k = i + 1;
                    String s = Integer.toBinaryString(k);
                    int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, power))) % 10;
                    if (bit == 1) {
                        if (a[i] == 1) {
                            parity[power] = (parity[power] + 1) % 2;
                        }
                    }
                }
                syndrome.insert(0, parity[power]);
            }

            int error_location = Integer.parseInt(syndrome.toString(), 2);
            if (error_location != 0 && error_location<= a.length) {
                a[error_location - 1] = (a[error_location - 1] + 1) % 2;
            }
            power = parity_count-1;
            int outLen = a.length-parity_count;
            int[] out = new int[outLen];
            for(int i=a.length ; i > 0 ; i--) {
                if(Math.pow(2, power) != i) {
                    out[outLen-1] = a[i-1];
                    outLen--;
                }
                else {
                    power--;
                }
            }
            return arrToBit(out);
        }
    }

    @Override
    public int getLength() {
        return (1 << r) - 1;
    }

    @Override
    public int getDimension() {
            return (1 << r) - r - 1;

    }

    @Override
    public BitSet encode(BitSet plaintext, int len) {
        return doEncHamming(plaintext,len);
    }

    @Override
    public BitSet decodeAlways(BitSet codetext, int len) {
        return doDecHamming(codetext,len);
    }

    @Override
    public BitSet decodeIfUnique(BitSet codetext, int len) throws UncorrectableErrorException {
        return doDecHamming(codetext,len);
    }

}
