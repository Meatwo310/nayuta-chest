package io.github.meatwo310.nayutachest.config;

import io.github.meatwo310.nayutachest.util.NumberFormatter;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.EnumValue<NumberFormatter.Format> NUMBER_FORMAT = BUILDER
            .comment("""
                    The format of number to display."""
            )
//            .defineEnum("numberType", FormatTypeEnum.KANSUJI);
            .defineEnum("numberFormat", NumberFormatter.Format.SCIENTIFIC);

    public static final ForgeConfigSpec.IntValue PRECISION = BUILDER
            .comment("""
                    The number of decimal places to display.
                    The behavior will slightly vary depending on the numberFormat.
                    Increasing the value provides more detail at the cost of readability.
                    Note that the value may be inaccurate as it is truncated when sent by the server."""
            )
            .defineInRange("precision", 3, 0, 10);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
