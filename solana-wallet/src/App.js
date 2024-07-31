import React, { useEffect, useState } from 'react';
import {
  WalletAdapterNetwork,
  WalletProvider,
  useWallet,
} from '@solana/wallet-adapter-react';
import {
  PhantomWalletAdapter,
} from '@solana/wallet-adapter-wallets';
import { WalletModalProvider, WalletMultiButton } from '@solana/wallet-adapter-react-ui';
import axios from 'axios';
import Trasaction from "./components/Trasaction";

// Import CSS của Wallet Adapter UI
require('@solana/wallet-adapter-react-ui/styles.css');

const App = () => {
  const [balance, setBalance] = useState(null);
  const { publicKey } = useWallet();

  useEffect(() => {
      const publickey = "CpjzDUM3vxag59u4PyCAUSx95aGoCTRvY21CAimWos3z";
    if (publicKey) {
      // Gửi yêu cầu đến Spring Boot để lấy số dư ví
      axios.get(`/api/solana/balance/${publickey}`)
        .then(response => {
          setBalance(response.data);
        })
        .catch(error => {
          console.error('Error fetching balance:', error);
        });
    }
  }, [publicKey]);

  return (
    <div>
      <h1>Solana Wallet</h1>
      <WalletMultiButton />
      {publicKey && (
        <div>
          <p>Public Key: {publicKey.toString()}</p>
          <p>Balance: {balance !== null ? balance + ' SOL' : 'Loading...'}</p>
        </div>
      )}
      <Trasaction></Trasaction>
    </div>
  );
};

const Wallet = () => {
  const wallets = [new PhantomWalletAdapter()];
  return (
    <WalletProvider wallets={wallets} autoConnect>
      <WalletModalProvider>
        <App />
      </WalletModalProvider>
    </WalletProvider>
  );
};

export default Wallet;

