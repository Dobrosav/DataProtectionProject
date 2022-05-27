package etf.openpgp.vd180005d.keymaterial;


public enum KeyMaterial {
    DSA_1024(1024), DSA_2048(2048);

    private final int bitSize;

    private KeyMaterial(int bitSize) {
        this.bitSize=bitSize;
    }
    public String getName() {
        return "DSA";
    }


    public int getBitSize() {
        return bitSize;
    }
}