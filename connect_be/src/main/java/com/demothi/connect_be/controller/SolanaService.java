package com.demothi.connect_be.controller;

import org.bitcoinj.core.Base58;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.LatestBlockhash;
import org.p2p.solanaj.rpc.types.RecentBlockhash;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
            System.out.println("Sender Public Key: " + fromAccount.getPublicKey());

            // Tạo public key cho người nhận
            PublicKey toPublicKeyObj = new PublicKey(toPublicKey);

            // Lấy recentBlockhash từ Solana RPC
            LatestBlockhash latestBlockhash = rpcClient.getApi().getLatestBlockhash();

            // Đúng cách lấy blockhash

            // Tạo instruction cho giao dịch
            long lamport = (long) (amount * 1000000000); // Chuyển SOL thành lamports (1 SOL = 1 tỷ lamports)
            Transaction transaction = new Transaction();
            transaction.addInstruction(
                    SystemProgram.transfer(
                            fromAccount.getPublicKey(),
                            toPublicKeyObj,
                            lamport
                    )
            );

            // Thiết lập recentBlockhash cho giao dịch
//
//            // Ký transaction
            List<Account> signers = Arrays.asList(fromAccount);
            String result = rpcClient.getApi().sendTransaction(transaction,signers,latestBlockhash.getValue().getBlockhash());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error in RPC request: " + e.getMessage();
        }
    }

}
