package com.luna.common.constant;

public enum ProcessEnum {
    /** 进度 */
    TEN(Constant.NUMBER_TEN),
    TWENTY(Constant.NUMBER_TWENTY),
    THIRTY(Constant.NUMBER_THIRTY),
    FOURTY(Constant.NUMBER_FOUTY),
    FIFTY(Constant.NUMBER_FIFTY),
    SIXTY(Constant.NUMBER_SIXTY),
    SEVENTY(Constant.NUMBER_SEVENTY),
    EIGHTY(Constant.NUMBER_EIGHTY),
    NINETY(Constant.NUMBER_NINETY),
    HUNDERD(Constant.NUMBER_HUNDERD);

    private int num;

    ProcessEnum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
