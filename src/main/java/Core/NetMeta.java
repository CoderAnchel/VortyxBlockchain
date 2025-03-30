package Core;

public class NetMeta {
    private String lastBlock;
    private String netSize;

    public String lastBlock() {
        return lastBlock;
    }

    public void setLastBlock(String lastBlock) {
        this.lastBlock = lastBlock;
    }

    public String netSize() {
        return netSize;
    }

    public void setNetSize(String netSize) {
        this.netSize = netSize;
    }
}
