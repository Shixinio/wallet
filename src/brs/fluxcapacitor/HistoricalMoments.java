package brs.fluxcapacitor;

import brs.props.Prop;
import brs.props.Props;

public enum HistoricalMoments {
    REWARD_RECIPIENT_ENABLE(6500, 8850, Props.DEV_REWARD_RECIPIENT_ENABLE_BLOCK_HEIGHT),
    DIGITAL_GOODS_STORE_BLOCK(11800, 8850, Props.DEV_DIGITAL_GOODS_STORE_BLOCK_HEIGHT),
    AUTOMATED_TRANSACTION_BLOCK(16301, 8850, Props.DEV_AUTOMATED_TRANSACTION_BLOCK_HEIGHT),
    AT_FIX_BLOCK_2(16301, 8850, Props.DEV_AT_FIX_BLOCK_2_BLOCK_HEIGHT),
    AT_FIX_BLOCK_3(16301, 8850, Props.DEV_AT_FIX_BLOCK_3_BLOCK_HEIGHT),
    AT_FIX_BLOCK_4(16301, 8850, Props.DEV_AT_FIX_BLOCK_4_BLOCK_HEIGHT),
    PRE_DYMAXION(16301, 8850, Props.DEV_PRE_DYMAXION_BLOCK_HEIGHT),
    POC2(16301, 8850, Props.DEV_POC2_BLOCK_HEIGHT),
    NEXT_FORK(Integer.MAX_VALUE, Integer.MAX_VALUE, Props.DEV_NEXT_FORK_BLOCK_HEIGHT);

    private final int mainnetHeight;
    private final int testnetHeight;
    private final Prop<Integer> overridingProperty;

    HistoricalMoments(int mainnetHeight, int testnetHeight, Prop<Integer> overridingProperty) {
        this.mainnetHeight = mainnetHeight;
        this.testnetHeight = testnetHeight;
        this.overridingProperty = overridingProperty;
    }

    public int getMainnetHeight() {
        return mainnetHeight;
    }

    public int getTestnetHeight() {
        return testnetHeight;
    }

    public Prop<Integer> getOverridingProperty() {
        return overridingProperty;
    }
}
