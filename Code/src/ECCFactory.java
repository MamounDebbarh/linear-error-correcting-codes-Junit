
public class ECCFactory implements IECCFactory {
    @Override
    public IECC makeHammingCode(int r) {
        return new HamCode(r);
    }

    @Override
    public IECC makeReedMullerCode(int k, int r) {
        return new RMCode(k,r);
    }
}
