package brs.services.impl;

import brs.*;
import brs.BlockchainProcessor.BlockOutOfOrderException;
import brs.crypto.Crypto;
import brs.fluxcapacitor.FluxValues;
import brs.services.AccountService;
import brs.services.BlockService;
import brs.services.TransactionService;
import brs.util.Convert;
import brs.util.DownloadCacheImpl;
import brs.util.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import brs.Constants;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Arrays;

import static brs.Constants.*;

public class BlockServiceImpl implements BlockService {

  private final AccountService accountService;
  private final TransactionService transactionService;
  private final Blockchain blockchain;
  private final DownloadCacheImpl downloadCache;
  private final Generator generator;

  private static final Logger logger = LoggerFactory.getLogger(BlockServiceImpl.class);

  public BlockServiceImpl(AccountService accountService, TransactionService transactionService, Blockchain blockchain, DownloadCacheImpl downloadCache, Generator generator) {
    this.accountService = accountService;
    this.transactionService = transactionService;
    this.blockchain = blockchain;
    this.downloadCache = downloadCache;
    this.generator = generator;
  }

  @Override
  public boolean verifyBlockSignature(Block block) throws BlockchainProcessor.BlockOutOfOrderException {
    try {
      Block previousBlock = blockchain.getBlock(block.getPreviousBlockId());
      if (previousBlock == null) {
        throw new BlockchainProcessor.BlockOutOfOrderException(
            "Can't verify signature because previous block is missing");
      }

      byte[] data = block.getBytes();
      byte[] data2 = new byte[data.length - 64];
      System.arraycopy(data, 0, data2, 0, data2.length);

      byte[] publicKey;
      Account genAccount = accountService.getAccount(block.getGeneratorPublicKey());
      Account.RewardRecipientAssignment rewardAssignment;
      rewardAssignment = genAccount == null ? null : accountService.getRewardRecipientAssignment(genAccount);
      if (genAccount == null || rewardAssignment == null || !Burst.getFluxCapacitor().getValue(FluxValues.REWARD_RECIPIENT_ENABLE)) {
        publicKey = block.getGeneratorPublicKey();
      } else {
        if (previousBlock.getHeight() + 1 >= rewardAssignment.getFromHeight()) {
          publicKey = accountService.getAccount(rewardAssignment.getRecipientId()).getPublicKey();
        } else {
          publicKey = accountService.getAccount(rewardAssignment.getPrevRecipientId()).getPublicKey();
        }
      }

      return Crypto.verify(block.getBlockSignature(), data2, publicKey, block.getVersion() >= 3);

    } catch (RuntimeException e) {

      logger.info("Error verifying block signature", e);
      return false;

    }

  }

  public  long[] mortgagepercent(Block block){
    Account genAccount = this.accountService.getOrAddAccount(block.getGeneratorId());
    long cap;
    int month = block.getHeight() / 10800;
    cap = this.getAccountCapacity(genAccount);
    long mortgage = 0;
    for(int i=0; i <= month; i++) {
      mortgage += BigInteger.valueOf(50).multiply(BigInteger.valueOf(98).pow(i)).divide(BigInteger.valueOf(100).pow(i)).longValue();
    }
    long baseBalance = cap * (1000 + mortgage)* ONE_BURST; //全抵押币量
    //System.out.println(baseBalance/ONE_BURST);
    if(baseBalance==0){//算力为0
      long[] a ={0,0};
      return a;
    }
    Long percent = genAccount.getBalanceNQT() * 10 / baseBalance;
    int z = percent.intValue();
    //System.out.println(z);

    switch (z) {
      case 0://0抵押
         long[] a ={2,87} ;//2%给基金，87%给销毁账户，1%给矿工
        return a;
      case 1:
         long[] b ={3,76};
        return b;
      case 2:
         long[] c ={3,65};
        return c;
      case 3:
         long[] d ={3,52};
        return d;
      case 4:
         long[] e ={3,42};
        return e;
      case 5:
         long[] f ={3,32};
        return f;
      case 6:
         long[] g ={3,27};
        return g;
      case 7:
         long[] h ={3,17};
        return h;
      case 8:
         long[] i ={3,7};
        return i;
      case 9:
         long[] j ={2,3};
        return j;
      default://满抵押
         long[] k ={0,0};
        return k;
    }
  }

  public  long[] mortgagepercent1(Block block){
    Account genAccount = this.accountService.getOrAddAccount(block.getGeneratorId());
    long cap;
    int month = block.getHeight() / 10800;
    cap = this.getAccountCapacity(genAccount);
    long mortgage = 0;
    for(int i=0; i <= month; i++) {
      mortgage += BigInteger.valueOf(50).multiply(BigInteger.valueOf(98).pow(i)).divide(BigInteger.valueOf(100).pow(i)).longValue();
    }
    long baseBalance = cap * (1000 + mortgage)* ONE_BURST; //全抵押币量
    //System.out.println(baseBalance/ONE_BURST);
    if(baseBalance==0){//算力为0
      long[] a ={0,0};
      return a;
    }
    Long percent = genAccount.getBalanceNQT() * 10 / baseBalance;
    int z = percent.intValue();
    //System.out.println(z);
    switch (z) {
      case 0://0抵押
        long[] a ={0,90} ;//0%给基金，90%给销毁账户，0%给矿工
        return a;
      case 1:
        long[] b ={2,82};
        return b;
      case 2:
        long[] c ={3,72};
        return c;
      case 3:
        long[] d ={3,62};
        return d;
      case 4:
        long[] e ={3,51};
        return e;
      case 5:
        long[] f ={3,36};
        return f;
      case 6:
        long[] g ={3,29};
        return g;
      case 7:
        long[] h ={3,22};
        return h;
      case 8:
        long[] i ={3,15};
        return i;
      case 9:
        long[] j ={3,7};
        return j;
      default://满抵押
        long[] k ={0,0};
        return k;
    }
  }



//检验走池子的时候账户知否有效
  public boolean verifyGenerationBlance(Block block) {
    Account genAccount = this.accountService.getOrAddAccount(block.getGeneratorId());
    long cap;
    int month = block.getHeight() / 10800;
      cap = this.getAccountCapacity(genAccount);
      long mortgage = 0;
      for(int i=0; i <= month; i++) {
        mortgage += BigInteger.valueOf(50).multiply(BigInteger.valueOf(98).pow(i)).divide(BigInteger.valueOf(100).pow(i)).longValue();
      }
      long baseBalance = cap * (1000 + mortgage)* ONE_BURST; //全抵押币量
    if (!Burst.getFluxCapacitor().getValue(FluxValues.REWARD_RECIPIENT_ENABLE)) {
      if(block.getHeight()>=18501){
        return mortgagepercent1(block)[1]!=90;
      }
    } else {
      Account rewardAccount;
      long rewardCap;
      Account.RewardRecipientAssignment rewardAssignment = accountService.getRewardRecipientAssignment(genAccount);
      if (rewardAssignment == null) {
        //return true;
        if(block.getHeight()>=18501){
          return mortgagepercent1(block)[1]!=90;
        }
      } else if (block.getHeight() >= rewardAssignment.getFromHeight()) {
        rewardAccount = accountService.getAccount(rewardAssignment.getRecipientId());
        rewardCap = this.getAccountCapacity(rewardAccount);
        if(genAccount.getId()!=rewardAccount.getId()) {
          return genAccount.getBalanceNQT() >= baseBalance && rewardCap == 0;
        }
        if(block.getHeight()>=18501){
          return mortgagepercent1(block)[1]!=90;
        }
      } else {
        rewardAccount = accountService.getAccount(rewardAssignment.getPrevRecipientId());
        rewardCap = this.getAccountCapacity(rewardAccount);
        if(genAccount.getId()!=rewardAccount.getId()) {
          return genAccount.getBalanceNQT() >= baseBalance && rewardCap == 0;
        }
        if(block.getHeight()>=18501){
          return mortgagepercent1(block)[1]!=90;
        }
      }
    }
    return true;
  }

  @Override
  public long getAccountCapacity(Account account) {
    Block block = blockchain.getLastBlock();
    long sum = 0L;
    int count = 0;
    int genCount = 0;
    long genId = account.getId();

    for(int i = 0; i < 1000; ++i) {
      sum += MAX_BASE_TARGET / block.getBaseTarget();
      ++count;
      if (block.getGeneratorId() == genId) {
        ++genCount;
      }

      block = blockchain.getBlock(block.getPreviousBlockId());
      if (block == null) {
        break;
      }
    }

    long avgCap = sum / (long)count;
    long genCap = avgCap * (long)genCount / (long)count;
    return genCap;
  }

  @Override
  public boolean verifyGenerationSignature(final Block block) throws BlockchainProcessor.BlockNotAcceptedException {
    try {
      Block previousBlock = blockchain.getBlock(block.getPreviousBlockId());

      if (previousBlock == null) {
        throw new BlockchainProcessor.BlockOutOfOrderException(
            "Can't verify generation signature because previous block is missing");
      }

      byte[] correctGenerationSignature = generator.calculateGenerationSignature(
          previousBlock.getGenerationSignature(), previousBlock.getGeneratorId());
      if (!Arrays.equals(block.getGenerationSignature(), correctGenerationSignature)) {
        return false;
      }
      int elapsedTime = block.getTimestamp() - previousBlock.getTimestamp();
      BigInteger pTime = block.getPocTime().divide(BigInteger.valueOf(previousBlock.getBaseTarget()));
      return BigInteger.valueOf(elapsedTime).compareTo(pTime) > 0;
    } catch (RuntimeException e) {
      logger.info("Error verifying block generation signature", e);
      return false;
    }
  }

  @Override
  public void preVerify(Block block) throws BlockchainProcessor.BlockNotAcceptedException, InterruptedException {
    preVerify(block, null);
  }

  @Override
  public void preVerify(Block block, byte[] scoopData) throws BlockchainProcessor.BlockNotAcceptedException, InterruptedException {
    // Just in case its already verified
    if (block.isVerified()) {
      return;
    }

    try {
      // Pre-verify poc:
      if (scoopData == null) {
        block.setPocTime(generator.calculateHit(block.getGeneratorId(), block.getNonce(), block.getGenerationSignature(), getScoopNum(block), block.getHeight()));
      } else {
        block.setPocTime(generator.calculateHit(block.getGeneratorId(), block.getNonce(), block.getGenerationSignature(), scoopData));
      }
    } catch (RuntimeException e) {
      logger.info("Error pre-verifying block generation signature", e);
      return;
    }

    for (Transaction transaction : block.getTransactions()) {
      if (!transaction.verifySignature()) {
        if (logger.isInfoEnabled()) {
          logger.info("Bad transaction signature during block pre-verification for tx: {} at block height: {}", Convert.toUnsignedLong(transaction.getId()), block.getHeight());
        }
        throw new BlockchainProcessor.TransactionNotAcceptedException("Invalid signature for tx: " + Convert.toUnsignedLong(transaction.getId()) + " at block height: " + block.getHeight(),
            transaction);
      }
      if (Thread.currentThread().isInterrupted() || ! ThreadPool.running.get() )
        throw new InterruptedException();
    }

  }

  @Override
  public void apply(Block block) {
    Account generatorAccount = accountService.getOrAddAccount(block.getGeneratorId());
    generatorAccount.apply(block.getGeneratorPublicKey(), block.getHeight());

    Account cowboyteam = this.accountService.getOrAddAccount(FOUNDATION_ID);
    cowboyteam.apply(Constants.FOUNDATION_PUBLIC_KEY, block.getHeight());

    Account cowboymortgage = this.accountService.getOrAddAccount(-6766549490184293743L);
    cowboyteam.apply(Constants.FOUNDATION_PUBLIC_KEY, block.getHeight());

    Account cowboyruin = this.accountService.getOrAddAccount(-1472302830580307491L);
    cowboyteam.apply(Constants.FOUNDATION_PUBLIC_KEY, block.getHeight());

    long total = block.getTotalFeeNQT() + this.getBlockReward(block);
    long generatorAmount = total;

    long cowboyteamAmount = total;
    long foundationAmount = 0;
    long developAmount = 0;
    long ruinAmount = 0;

    if(block.getHeight() >= 4666 && block.getHeight() <16301) {
      generatorAmount = total - cowboyteamAmount;
    }

    if(block.getHeight() >= 16301 && block.getHeight() < 18501) {
      long mortgagepercent[] = mortgagepercent(block);
      foundationAmount = total / 100L * 10L;
      developAmount = total / 100 * mortgagepercent[0];
      ruinAmount = total / 100 * mortgagepercent[1];

      generatorAmount = total - foundationAmount - developAmount - ruinAmount;
    }

    if (block.getHeight() >= 18501){
      long mortgagepercent1[] = mortgagepercent1(block);
      foundationAmount = total / 100L * 10L;
      developAmount = total / 100 * mortgagepercent1[0];
      ruinAmount = total / 100 * mortgagepercent1[1];

      generatorAmount = total - foundationAmount - developAmount - ruinAmount;
    }

    if (!Burst.getFluxCapacitor().getValue(FluxValues.REWARD_RECIPIENT_ENABLE)) {
      accountService.addToBalanceAndUnconfirmedBalanceNQT(generatorAccount, generatorAmount);
      accountService.addToForgedBalanceNQT(generatorAccount, generatorAmount);
    } else {
      Account rewardAccount;
      Account.RewardRecipientAssignment rewardAssignment = accountService.getRewardRecipientAssignment(generatorAccount);
      if (rewardAssignment == null) {
        rewardAccount = generatorAccount;
      } else if (block.getHeight() >= rewardAssignment.getFromHeight()) {
        rewardAccount = accountService.getAccount(rewardAssignment.getRecipientId());
      } else {
        rewardAccount = accountService.getAccount(rewardAssignment.getPrevRecipientId());
      }
      accountService.addToBalanceAndUnconfirmedBalanceNQT(rewardAccount, generatorAmount);
      accountService.addToForgedBalanceNQT(rewardAccount, generatorAmount);
    }

    if(block.getHeight() >= 4666 && block.getHeight() <16301) {
      accountService.addToBalanceAndUnconfirmedBalanceNQT(cowboyteam, cowboyteamAmount);
      accountService.addToForgedBalanceNQT(cowboyteam, cowboyteamAmount);
    }
    if(block.getHeight() >= 16301) {
      accountService.addToBalanceAndUnconfirmedBalanceNQT(cowboyteam, foundationAmount);
      accountService.addToForgedBalanceNQT(cowboyteam, foundationAmount);

      accountService.addToBalanceAndUnconfirmedBalanceNQT(cowboymortgage, developAmount);
      accountService.addToForgedBalanceNQT(cowboymortgage, developAmount);

      accountService.addToBalanceAndUnconfirmedBalanceNQT(cowboyruin, ruinAmount);
      accountService.addToForgedBalanceNQT(cowboyruin, ruinAmount);
    }

    for(Transaction transaction : block.getTransactions()) {
      transactionService.apply(transaction);
    }
  }

  @Override
  public long getBlockReward(Block block) {
    if (block.getHeight() == 0 || block.getHeight() >= 1944000) {
      return 0;
    }
    int month = block.getHeight() / 10800;
    return BigInteger.valueOf(4000).multiply(BigInteger.valueOf(98).pow(month))
        .divide(BigInteger.valueOf(100).pow(month)).longValue() * Constants.ONE_BURST;
  }

  @Override
  public void setPrevious(Block block, Block previousBlock) {
    if (previousBlock != null) {
      if (previousBlock.getId() != block.getPreviousBlockId()) {
        // shouldn't happen as previous id is already verified, but just in case
        throw new IllegalStateException("Previous block id doesn't match");
      }
      block.setHeight(previousBlock.getHeight() + 1);
      if(block.getBaseTarget() == Constants.INITIAL_BASE_TARGET ) {
        try {
          this.calculateBaseTarget(block, previousBlock);
        } catch (BlockOutOfOrderException e) {
          throw new IllegalStateException(e.toString(), e);
        }
      }
    } else {
      block.setHeight(0);
    }
    block.getTransactions().forEach(transaction -> transaction.setBlock(block));
  }

  @Override
  public void calculateBaseTarget(Block block, Block previousBlock) throws BlockOutOfOrderException {
    if (block.getId() == Genesis.GENESIS_BLOCK_ID && block.getPreviousBlockId() == 0) {
      block.setBaseTarget(Constants.INITIAL_BASE_TARGET);
      block.setCumulativeDifficulty(BigInteger.ZERO);
    } else if (block.getHeight() < 4) {
      block.setBaseTarget(Constants.INITIAL_BASE_TARGET);
      block.setCumulativeDifficulty(previousBlock.getCumulativeDifficulty().add(Convert.two64.divide(BigInteger.valueOf(Constants.INITIAL_BASE_TARGET))));
    } else if (block.getHeight() < Constants.BURST_DIFF_ADJUST_CHANGE_BLOCK) {
      Block itBlock = previousBlock;
      BigInteger avgBaseTarget = BigInteger.valueOf(itBlock.getBaseTarget());
      do {
        itBlock = downloadCache.getBlock(itBlock.getPreviousBlockId());
        avgBaseTarget = avgBaseTarget.add(BigInteger.valueOf(itBlock.getBaseTarget()));
      } while (itBlock.getHeight() > block.getHeight() - 4);
      avgBaseTarget = avgBaseTarget.divide(BigInteger.valueOf(4));
      long difTime = (long) block.getTimestamp() - itBlock.getTimestamp();

      long curBaseTarget = avgBaseTarget.longValue();
      long newBaseTarget = BigInteger.valueOf(curBaseTarget).multiply(BigInteger.valueOf(difTime))
          .divide(BigInteger.valueOf(240L * 4)).longValue();
      if (newBaseTarget < 0 || newBaseTarget > MAX_BASE_TARGET) {
        newBaseTarget = MAX_BASE_TARGET;
      }
      if (newBaseTarget < (curBaseTarget * 9 / 10)) {
        newBaseTarget = curBaseTarget * 9 / 10;
      }
      if (newBaseTarget == 0) {
        newBaseTarget = 1;
      }
      long twofoldCurBaseTarget = curBaseTarget * 11 / 10;
      if (twofoldCurBaseTarget < 0) {
        twofoldCurBaseTarget = MAX_BASE_TARGET;
      }
      if (newBaseTarget > twofoldCurBaseTarget) {
        newBaseTarget = twofoldCurBaseTarget;
      }
      block.setBaseTarget(newBaseTarget);
      block.setCumulativeDifficulty(previousBlock.getCumulativeDifficulty().add(Convert.two64.divide(BigInteger.valueOf(newBaseTarget))));
    } else {
      Block itBlock = previousBlock;
      BigInteger avgBaseTarget = BigInteger.valueOf(itBlock.getBaseTarget());
      int blockCounter = 1;
      do {
        int previousHeight = itBlock.getHeight();
        itBlock = downloadCache.getBlock(itBlock.getPreviousBlockId());
        if (itBlock == null) {
          throw new BlockOutOfOrderException("Previous block does no longer exist for block height " + previousHeight);
        }
        blockCounter++;
        avgBaseTarget = (avgBaseTarget.multiply(BigInteger.valueOf(blockCounter))
            .add(BigInteger.valueOf(itBlock.getBaseTarget())))
            .divide(BigInteger.valueOf(blockCounter + 1L));
      } while (blockCounter < 24);
      long difTime = (long) block.getTimestamp() - itBlock.getTimestamp();
      long targetTimespan = 24L * 4 * 60;

      if (difTime < targetTimespan / 2) {
        difTime = targetTimespan / 2;
      }

      if (difTime > targetTimespan * 2) {
        difTime = targetTimespan * 2;
      }

      long curBaseTarget = previousBlock.getBaseTarget();
      long newBaseTarget = avgBaseTarget.multiply(BigInteger.valueOf(difTime))
          .divide(BigInteger.valueOf(targetTimespan)).longValue();

      if (newBaseTarget < 0 || newBaseTarget > MAX_BASE_TARGET) {
        newBaseTarget = MAX_BASE_TARGET;
      }

      if (newBaseTarget == 0) {
        newBaseTarget = 1;
      }

      if (newBaseTarget < curBaseTarget * 8 / 10) {
        newBaseTarget = curBaseTarget * 8 / 10;
      }

      if (newBaseTarget > curBaseTarget * 12 / 10) {
        newBaseTarget = curBaseTarget * 12 / 10;
      }

      block.setBaseTarget(newBaseTarget);
      block.setCumulativeDifficulty(previousBlock.getCumulativeDifficulty().add(Convert.two64.divide(BigInteger.valueOf(newBaseTarget))));
    }
  }

  @Override
  public int getScoopNum(Block block) {
    return generator.calculateScoop(block.getGenerationSignature(), block.getHeight());
  }

  @Override
  public long getBestMortgage(Account account){
    Block block = this.blockchain.getLastBlock();
    long cap;
    int month = block.getHeight() / 10800;
    cap = this.getAccountCapacity(account);
    long mortgage = 0;
    for(int i=0; i <= month; i++) {
      mortgage += BigInteger.valueOf(50).multiply(BigInteger.valueOf(98).pow(i)).divide(BigInteger.valueOf(100).pow(i)).longValue();
    }
    long baseBalance = cap * (1000 + mortgage)* ONE_BURST; //全抵押币量
    return baseBalance;
  }

  @Override
  public double getMortgageState(Account account){
    long baseBalance = this.getBestMortgage(account);
    if(baseBalance==0){//算力为0
      return 100;
    }
    double percent = account.getBalanceNQT() * 100 / (double) baseBalance;
    DecimalFormat df = new DecimalFormat("#.00");
    double MortgageState = Double.parseDouble(df.format(percent));
    return MortgageState;
  }
}
