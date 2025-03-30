package app.DTOS;

import Core.Entities.Wallet;

import java.security.KeyPair;

public class KeyPairWalletDTO {
    private KeyPair keyPair;
    private Wallet wallet;

    public KeyPairWalletDTO(KeyPair keyPair, Wallet wallet) {
        this.keyPair = keyPair;
        this.wallet = wallet;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public Wallet getWallet() {
        return wallet;
    }
}