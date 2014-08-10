package com.google.dogecoin.examples;

import java.io.File;
import java.net.InetAddress;

import com.google.dogecoin.core.*;
import com.google.dogecoin.kits.WalletAppKit;
import com.google.dogecoin.params.MainNetParams;
import com.google.dogecoin.params.TestNet3Params;
import com.google.dogecoin.utils.Threading;


public class TestWallet {

	private WalletAppKit appKit;

	public static void main(String[] args) throws Exception {
		new TestWallet().run();
	}

	public void run() throws Exception {
		NetworkParameters params = TestNet3Params.get();
		
		appKit = new WalletAppKit(params, new File("."), "dogecoins") {
			@Override
			protected void onSetupCompleted() {
				if (wallet().getKeychainSize() < 1) {
					ECKey key = new ECKey();
					wallet().addKey(key);
				}
				
				peerGroup().setConnectTimeoutMillis(1000);

				System.out.println(appKit.wallet());
				
				peerGroup().addEventListener(new AbstractPeerEventListener() {
					@Override
					public void onPeerConnected(Peer peer, int peerCount) {
						super.onPeerConnected(peer, peerCount);
						System.out.println(String.format("onPeerConnected: %s %s",peer,peerCount));
					}
					@Override
					public void onPeerDisconnected(Peer peer, int peerCount) {
						super.onPeerDisconnected(peer, peerCount);
						System.out.println(String.format("onPeerDisconnected: %s %s",peer,peerCount));
					}
					@Override public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
						super.onBlocksDownloaded(peer, block, blocksLeft);
						System.out.println(String.format("%s blocks left (downloaded %s)",blocksLeft,block.getHashAsString()));
					}
					
					@Override public Message onPreMessageReceived(Peer peer, Message m) {
						System.out.println(String.format("%s -> %s",peer,m.getClass()));
						return super.onPreMessageReceived(peer, m);
					}
				},Threading.SAME_THREAD);
			}
		};

        PeerAddress[] peers = new PeerAddress[7];
        peers[0] = new PeerAddress(InetAddress.getByName("54.237.28.96"), params.getPort());
        peers[1] = new PeerAddress(InetAddress.getByName("107.170.14.48"), params.getPort());
        peers[2] = new PeerAddress(InetAddress.getByName("dogetest.jrn.me.uk"), params.getPort());
        peers[3] = new PeerAddress(InetAddress.getByName("54.74.34.153"), params.getPort());
        peers[4] = new PeerAddress(InetAddress.getByName("178.201.149.20"), params.getPort());
        peers[5] = new PeerAddress(InetAddress.getByName("54.217.8.3"), params.getPort());
        peers[6] = new PeerAddress(InetAddress.getByName("192.168.178.89"), params.getPort());
        appKit.setPeerNodes(peers);

		appKit.startAndWait();
	}

}
