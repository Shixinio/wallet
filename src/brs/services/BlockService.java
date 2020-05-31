package brs.services;

import brs.Account;
import brs.Block;
import brs.Blockchain;
import brs.BlockchainProcessor;
import brs.BlockchainProcessor.BlockNotAcceptedException;
import brs.BlockchainProcessor.BlockOutOfOrderException;

public interface BlockService {

  void preVerify(Block block) throws BlockchainProcessor.BlockNotAcceptedException, InterruptedException;

  void preVerify(Block block, byte[] scoopData) throws BlockchainProcessor.BlockNotAcceptedException, InterruptedException;

  long getBlockReward(Block block);

  void calculateBaseTarget(Block block, Block lastBlock) throws BlockOutOfOrderException;

  void setPrevious(Block block, Block previousBlock);

  boolean verifyGenerationSignature(Block block) throws BlockNotAcceptedException;

  boolean verifyBlockSignature(Block block) throws BlockOutOfOrderException;

  void apply(Block block);

  int getScoopNum(Block block);

  boolean verifyGenerationBlance(Block block);

  long getAccountCapacity(Account account);

  long getBestMortgage(Account account);

  double getMortgageState(Account account);
}
