package etf.openpgp.vd180005d.keymaterial;

public enum SubkeyMaterial {
    EL_GAMAL_1024(1024), EL_GAMAL_2048(2048), EL_GAMAL_4096(4096);

    private final int bitSize;

    private SubkeyMaterial(int bitSize) {
        this.bitSize=bitSize;
    }

    public int getBitSize() {
        return bitSize;
    }
    public String getName() {
        return "ElGamal";
    }

}