package io.github.meatwo310.nayutachest.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<NumberTypeEnum> NUMBER_TYPE = BUILDER
            .comment("""
                    The type of number to display.
                    KANSUJI: 一, 一千, 一万, 百二十三穣, 一那由多
                    NUMERIC_KANSUJI: 1, 1千, 1万, 123穣, 1那由多
                    SCIENTIFIC: 1, 1.00E3, 1.00E6, 1.23E30, 1.00E60
                    SHORT: 1, 1k, 1M, 1.23No, 1.00Nd
                    """
            )
//            .defineEnum("numberType", NumberTypeEnum.KANSUJI);
            .defineEnum("numberType", NumberTypeEnum.SCIENTIFIC);

    public static final ForgeConfigSpec.IntValue PRECISION = BUILDER
            .comment("""
                    The number of decimal places to display.
                    This only applies to SCIENTIFIC and SHORT.
                    """
            )
            .defineInRange("precision", 3, 0, 10);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public enum NumberTypeEnum {
        KANSUJI,
        NUMERIC_KANSUJI,
        SCIENTIFIC,
        SHORT,
    }
}
