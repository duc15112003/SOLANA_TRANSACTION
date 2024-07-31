package com.demothi.connect_be.controller;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.RecentBlockhash;
import org.springframework.stereotype.Service;

import java.util.Base64;

import static org.bouncycastle.asn1.x500.style.BCStyle.L;

@Service
public class SolanaService {
    RpcClient rpcClient = new RpcClient(Cluster.DEVNET);

    public String getBalance(String address) {
        try {
            PublicKey publicKey = new PublicKey(address);
            long balance = rpcClient.getApi().getBalance(publicKey);
            return String.valueOf(balance);
        } catch (RpcException e) {
            e.printStackTrace();
            return "Error retrieving balance: " + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unexpected error: " + e.getMessage();
        }
    }


    public String createAndSendTransaction(String fromPrivateKeyBase58, String toPublicKey, double amount) {
        try {
            // Tạo account từ private key
            byte[] fromPrivateKeyBytes = Base58.decode(fromPrivateKeyBase58);
            Account fromAccount = new Account(fromPrivateKeyBytes);
            System.out.println(fromAccount.getPublicKey());
            // Tạo public key cho người nhận
            PublicKey toPublicKeyObj = new PublicKey(toPublicKey);

            // Lấy recentBlockhash từ Solana RPC A

            String recentBlockhash = rpcClient.getApi().getRecentBlockhash();

            // Tạo instruction cho giao dịch
            long lamport = (long) (amount * 1000000000);
            Transaction transaction = new Transaction();
            transaction.addInstruction(
                    SystemProgram.transfer(
                            fromAccount.getPublicKey(),
                            toPublicKeyObj,
                            lamport
                    )
            );
            // Thiết lập recentBlockhash cho giao dịch
            transaction.setRecentBlockHash(recentBlockhash);
            // Ký transaction
//            transaction.sign(fromAccount);

            // Gửi transaction
            String result = rpcClient.getApi().sendTransaction(transaction, fromAccount);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
