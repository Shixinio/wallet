package brs.grpc.handlers;

import brs.Account;
import brs.Block;
import brs.Blockchain;
import brs.Generator;
import brs.crypto.Crypto;
import brs.grpc.GrpcApiHandler;
import brs.grpc.proto.ApiException;
import brs.grpc.proto.BrsApi;
import brs.services.AccountService;
import brs.services.BlockService;

import java.math.BigInteger;
import java.util.Objects;

import static brs.Constants.MAX_BASE_TARGET;
import static brs.Constants.ONE_BURST;

public class SubmitNonceHandler implements GrpcApiHandler<BrsApi.SubmitNonceRequest, BrsApi.SubmitNonceResponse> {

    private final Blockchain blockchain;
    private final AccountService accountService;
    private final Generator generator;
    private final BlockService blockService;

    public SubmitNonceHandler(Blockchain blockchain, AccountService accountService, Generator generator,BlockService blockService) {
        this.blockchain = blockchain;
        this.accountService = accountService;
        this.generator = generator;
        this.blockService = blockService;
    }

    @Override
    public BrsApi.SubmitNonceResponse handleRequest(BrsApi.SubmitNonceRequest request) throws Exception {
        String secret = request.getSecretPhrase();
        long nonce = request.getNonce();
        long accountId = request.getAccount();
        int submissionHeight = request.getBlockHeight();

        if (submissionHeight != 0 && submissionHeight != blockchain.getHeight() + 1) {
            throw new ApiException("Given block height does not match current blockchain height");
        }

        if (Objects.equals(secret, "")) {
            throw new ApiException("Missing Passphrase");
        }

        byte[] secretPublicKey = Crypto.getPublicKey(secret);
        Account secretAccount = accountService.getAccount(secretPublicKey);
        if(secretAccount != null) {
            verifySecretAccount(accountService, blockchain, secretAccount, accountId,blockService);
        }

        Generator.GeneratorState generatorState;
        if (accountId == 0 || secretAccount == null) {
            generatorState = generator.addNonce(secret, nonce);
        }
        else {
            Account genAccount = accountService.getAccount(accountId);
            if (genAccount == null || genAccount.getPublicKey() == null) {
                throw new ApiException("Passthrough mining requires public key in blockchain");
            }
            else {
                byte[] publicKey = genAccount.getPublicKey();
                generatorState = generator.addNonce(secret, nonce, publicKey);
            }
        }

        if (generatorState == null) {
            throw new ApiException("Failed to create generator");
        }

        return BrsApi.SubmitNonceResponse.newBuilder().setDeadline(generatorState.getDeadline().longValueExact()).build();
    }

    public static void verifySecretAccount(AccountService accountService, Blockchain blockchain, Account secretAccount, long accountId, BlockService blockService) throws ApiException {
        Account genAccount;
        if (accountId != 0) {
            genAccount = accountService.getAccount(accountId);
        }
        else {
            genAccount = secretAccount;
        }

        if (genAccount != null) {
            Account.RewardRecipientAssignment assignment = accountService.getRewardRecipientAssignment(genAccount);
            long rewardId;
            if (assignment == null) {
                rewardId = genAccount.getId();
            } else if (assignment.getFromHeight() > blockchain.getLastBlock().getHeight() + 1) {
                rewardId = assignment.getPrevRecipientId();
            } else {
                rewardId = assignment.getRecipientId();
            }
            if (rewardId != secretAccount.getId()) {
                throw new ApiException("Passphrase does not match reward recipient");
            }

            int month = blockchain.getLastBlock().getHeight() / 10800;
            long mortgage = 0;
            for(int i=0; i <= month; i++) {
                mortgage += BigInteger.valueOf(50).multiply(BigInteger.valueOf(98).pow(i)).divide(BigInteger.valueOf(100).pow(i)).longValue();
            }

            if(genAccount.getBalanceNQT() < 1000 + mortgage) {
                throw new ApiException("Account balance not enough " + 1000+mortgage);
            }
            if(blockService.getAccountCapacity(genAccount) != 0) {
                if (blockService.getMortgageState(genAccount)<10) {
                    throw new ApiException("Account balance not enough 10%");
                }
            }
        }
        else {
            throw new ApiException("Passphrase is for a different account");
        }
    }

}
