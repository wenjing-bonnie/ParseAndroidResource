package com.wj.parse.androidresource.entity.typespec6

import com.wj.parse.androidresource.entity.ResChunkHeader
import com.wj.parse.androidresource.interfaces.ChunkParseOperator
import com.wj.parse.androidresource.interfaces.ChunkProperty
import com.wj.parse.androidresource.utils.Logger
import com.wj.parse.androidresource.utils.Utils

/**
 * create by wenjing.liu at 2023/7/31
 * https://android.googlesource.com/platform/frameworks/base/+/master/libs/androidfw/include/androidfw/ResourceTypes.h#946
 * ResTable_config
 */
class ResTypeInfoTableConfigChunkChild(
    /**
     * the string pool chunk byte array which index has started from 0 for this child chunk
     */
    override val inputResourceByteArray: ByteArray,
    /**
     * the child offset in the parent byte array
     */
    override val startOffset: Int,
) : ChunkParseOperator {

    /**
     * <Attribute_1>
     * uint32_t size;
     */
    var size: Int = -1

    /**
     * <Union_2>
     * union {
     *   struct {
     *       // Mobile country code (from SIM).  0 means "any".
     *       uint16_t mcc;
     *       // Mobile network code (from SIM).  0 means "any".
     *        uint16_t mnc;
     *   };
     *   uint32_t imsi;
     * };
     */
    data class Union2Mobile(
        var mcc: Short = -1,
        var mnc: Short = -1,
        var imsi: Int = -1
    )

    lateinit var union2Mobile: Union2Mobile

    /**
     * <Union_3>
     * union {
     *   struct {
     *      char language[2];
     *      char country[2];
     *   };
     *    uint32_t locale;
     * };
     */
    data class Union3Locale(
        var language: ByteArray = ByteArray(2),
        var country: ByteArray = ByteArray(2),
        var locale: Int = -1
    )

    lateinit var union3Locale: Union3Locale

    /**
     * <Union_4>
     * union {
     *   struct {
     *      uint8_t orientation;
     *      uint8_t touchscreen;
     *      uint16_t density;
     *   };
     *   uint32_t screenType;
     * };
     */
    data class Union4ScreenType(
        var orientation: Byte = -1,
        var touchscreen: Byte = -1,
        var density: Short = -1,
        var screenType: Int = -1
    )

    lateinit var union4ScreenType: Union4ScreenType

    /**
     * <Union_5>
     * union {
     *    struct {
     *      uint8_t keyboard;
     *      uint8_t navigation;
     *      uint8_t inputFlags;
     *      uint8_t inputPad0;
     *    };
     *    uint32_t input;
     * };
     */
    data class Union5Input(
        var keyboard: Byte = -1,
        var navigation: Byte = -1,
        var inputFlags: Byte = -1,
        var inputPad0: Byte = -1,
        var input: Int = -1
    )

    lateinit var union5Input: Union5Input

    /**
     * <Union_6>
     * union {
     *    struct {
     *      uint16_t screenWidth;
     *      uint16_t screenHeight;
     *    };
     *    uint32_t screenSize;
     * };
     */
    data class Union6ScreenSize(
        var screenWidth: Short = -1,
        var screenHeight: Short = -1,
        var screenSize: Int = -1
    )

    lateinit var union6ScreenSize: Union6ScreenSize

    /**
     * <Union_7>
     * union {
     *    struct {
     *      uint16_t sdkVersion;
     *      // For now minorVersion must always be 0!!!  Its meaning
     *      // is currently undefined.
     *      uint16_t minorVersion;
     *    };
     *    uint32_t version;
     * };
     */
    data class Union7Version(
        var sdkVersion: Short = -1,
        var minorVersion: Short = -1,
        var version: Int = -1
    )

    lateinit var union7Version: Union7Version

    /**
     * <Union_8>
     * union {
     *    struct {
     *       uint8_t screenLayout;
     *       uint8_t uiMode;
     *       uint16_t smallestScreenWidthDp;
     *     };
     *    uint32_t screenConfig;
     * };
     */
    data class Union8ScreenConfig(
        var screenLayout: Byte = -1,
        var uiMode: Byte = -1,
        var smallestScreenWidthDp: Short = -1,
        var screenConfig: Int = -1
    )

    lateinit var union8ScreenConfig: Union8ScreenConfig

    /**
     * <Union_9>
     * union {
     *    struct {
     *      uint16_t screenWidthDp;
     *      uint16_t screenHeightDp;
     *    };
     *    uint32_t screenSizeDp;
     * };
     */
    data class Union9ScreenSizeDp(
        var screenWidthDp: Short = -1,
        var screenHeightDp: Short = -1,
        var screenSizeDp: Int = -1
    )

    lateinit var union9ScreenSizeDp: Union9ScreenSizeDp

    /**
     * <Attribute_10 ~ Attribute_11>
     * char localeScript[4];
     * char localeVariant[8];
     */
    var localeScript: ByteArray = ByteArray(4)
    var localeVariant: ByteArray = ByteArray(8)

    init {
        chunkParseOperator()
        checkChunkAttributes()
    }

    // TODO
    override val chunkEndOffset: Int
        get() = 48

    override val header: ResChunkHeader?
        get() = kotlin.run {
            Logger.debug("Not need header, because this is a child chunk without header.")
            null
        }

    override fun chunkProperty(): ChunkProperty = ChunkProperty.CHUNK_CHILD

    override fun chunkParseOperator(): ChunkParseOperator {
        // <Attribute_1>
        var attributeOffset = 0
        var attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SIZE_BYTE)
        size = Utils.byte2Int(attributeByteArray)

        // <Union_2>
        union2Mobile = Union2Mobile()
        attributeOffset += SIZE_BYTE // 4 >> 2
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, MCC_BYTE)
        union2Mobile.mcc = Utils.byte2Short(attributeByteArray)
        attributeOffset += MCC_BYTE // 6 >> 2
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, MNC_BYTE)
        union2Mobile.mnc = Utils.byte2Short(attributeByteArray)
        // ->-> reset
        attributeOffset -= MCC_BYTE // 4 >> 4
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_2_IMSI_BYTE)
        union2Mobile.imsi = Utils.byte2Int(attributeByteArray)

        // <Union_3>
        union3Locale = Union3Locale()
        attributeOffset += UNION_2_IMSI_BYTE // 8 >> 2
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, LANGUAGE_BYTE)
        union3Locale.language = attributeByteArray?.let { languageByte ->
            languageByte
        } ?: ByteArray(2)
        attributeOffset += LANGUAGE_BYTE // 10 >> 2
        attributeByteArray = Utils.copyByte(resArrayStartZeroOffset, attributeOffset, COUNTRY_BYTE)
        union3Locale.country = attributeByteArray?.let { countryByte ->
            countryByte
        } ?: ByteArray(2)
        // ->-> reset
        attributeOffset -= LANGUAGE_BYTE // 8 >> 4
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_3_LOCALE_BYTE)
        union3Locale.locale = Utils.byte2Int(attributeByteArray)

        // <Union_4>
        union4ScreenType = Union4ScreenType()
        attributeOffset += UNION_3_LOCALE_BYTE // 12 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, ORIENTATION_BYTE)
        union4ScreenType.orientation = attributeByteArray?.let { orientationByte ->
            orientationByte[0]
        } ?: -1
        attributeOffset += ORIENTATION_BYTE // 13 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, TOUCH_SCREEN_BYTE)
        union4ScreenType.touchscreen = attributeByteArray?.let { orientationByte ->
            orientationByte[0]
        } ?: -1
        attributeOffset += TOUCH_SCREEN_BYTE // 14 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, DENSITY_BYTE)
        union4ScreenType.density = Utils.byte2Short(attributeByteArray)
        // ->-> reset
        attributeOffset = // 12 >> 4
            attributeOffset - TOUCH_SCREEN_BYTE - ORIENTATION_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_4_SCREEN_TYPE_BYTE)
        union4ScreenType.screenType = Utils.byte2Int(attributeByteArray)

        // <Union_5>
        union5Input = Union5Input()
        attributeOffset += UNION_4_SCREEN_TYPE_BYTE // 16 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, KEYBOARD_BYTE)
        attributeByteArray?.let { keyboardByte ->
            union5Input.keyboard = keyboardByte[0]
        }
        attributeOffset += KEYBOARD_BYTE // 17 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, NAVIGATION_BYTE)
        attributeByteArray?.let { navigationByte ->
            union5Input.navigation = navigationByte[0]
        }
        attributeOffset += NAVIGATION_BYTE // 18 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, INPUT_FLAGS_BYTE)
        attributeByteArray?.let { inputFlagsByte ->
            union5Input.inputFlags = inputFlagsByte[0]
        }
        attributeOffset += INPUT_FLAGS_BYTE // 19 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, INPUT_PAD0_BYTE)
        attributeByteArray?.let { inputPad0Byte ->
            union5Input.inputPad0 = inputPad0Byte[0]
        }
        // ->-> reset
        attributeOffset = // 16 >> 4
            attributeOffset - INPUT_FLAGS_BYTE - NAVIGATION_BYTE - KEYBOARD_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_5_INPUT_BYTE)
        union5Input.input = Utils.byte2Int(attributeByteArray)

        // <Union_6>
        union6ScreenSize = Union6ScreenSize()
        attributeOffset += UNION_5_INPUT_BYTE // 20 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SCREEN_WIDTH_BYTE)
        union6ScreenSize.screenWidth = Utils.byte2Short(attributeByteArray)
        attributeOffset += SCREEN_WIDTH_BYTE // 22 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SCREEN_HEIGHT_BYTE)
        union6ScreenSize.screenHeight = Utils.byte2Short(attributeByteArray)
        // ->-> reset
        attributeOffset -= SCREEN_WIDTH_BYTE // 20 >> 4
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_6_SCREEN_SIZE_BYTE)
        union6ScreenSize.screenSize = Utils.byte2Int(attributeByteArray)

        // <Union_7>
        union7Version = Union7Version()
        attributeOffset += UNION_6_SCREEN_SIZE_BYTE // 24 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SDK_VERSION_BYTE)
        union7Version.sdkVersion = Utils.byte2Short(attributeByteArray)
        attributeOffset += SDK_VERSION_BYTE // 26 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, MINOR_VERSION_BYTE)
        union7Version.minorVersion = Utils.byte2Short(attributeByteArray)
        // ->-> reset
        attributeOffset -= SDK_VERSION_BYTE // 24 >> 4
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_7_VERSION_BYTE)
        union7Version.version = Utils.byte2Int(attributeByteArray)

        // <Union_8>
        union8ScreenConfig = Union8ScreenConfig()
        attributeOffset += UNION_7_VERSION_BYTE // 28 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SCREEN_LAYOUT_BYTE)
        attributeByteArray?.let { screenLayoutByte ->
            union8ScreenConfig.screenLayout = screenLayoutByte[0]
        }
        attributeOffset += SCREEN_LAYOUT_BYTE // 29 >> 1
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UI_MODE_BYTE)
        attributeByteArray?.let { uiModeByte ->
            union8ScreenConfig.uiMode = uiModeByte[0]
        }
        attributeOffset += UI_MODE_BYTE // 30 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SMALLEST_SCREEN_WIDTH_DP_BYTE)
        union8ScreenConfig.smallestScreenWidthDp = Utils.byte2Short(attributeByteArray)
        // ->-> reset
        attributeOffset =  // 28 >> 4
            attributeOffset - UI_MODE_BYTE - SCREEN_LAYOUT_BYTE
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_8_SCREEN_CONFIG_BYTE)
        union8ScreenConfig.screenConfig = Utils.byte2Int(attributeByteArray)

        // <Union_9>
        union9ScreenSizeDp = Union9ScreenSizeDp()
        attributeOffset += UNION_8_SCREEN_CONFIG_BYTE // 32 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SCREEN_WIDTH_DP_BYTE)
        union9ScreenSizeDp.screenWidthDp = Utils.byte2Short(attributeByteArray)
        attributeOffset += SCREEN_WIDTH_DP_BYTE // 34 >> 2
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, SCREEN_HEIGHT_DP_BYTE)
        union9ScreenSizeDp.screenHeightDp = Utils.byte2Short(attributeByteArray)
        // ->-> reset
        attributeOffset -= SCREEN_WIDTH_DP_BYTE // 32 >> 4
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, UNION_9_SCREEN_SIZE_DP_BYTE)
        union9ScreenSizeDp.screenSizeDp = Utils.byte2Int(attributeByteArray)

        // <Attribute_10>
        attributeOffset += UNION_9_SCREEN_SIZE_DP_BYTE // 36 >> 4
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, LOCALE_SCRIPT_BYTE)
        attributeByteArray?.let { localeScriptByte ->
            localeScript = localeScriptByte.copyOfRange(0, LOCALE_SCRIPT_BYTE)
        }

        // <Attribute_11>
        attributeOffset += LOCALE_SCRIPT_BYTE // 40 >> 8
        attributeByteArray =
            Utils.copyByte(resArrayStartZeroOffset, attributeOffset, LOCALE_VARIANT_BYTE)
        attributeByteArray?.let { localeVariantByte ->
            localeVariant = localeVariantByte.copyOfRange(0, LOCALE_VARIANT_BYTE)
        }
        return this
    }

    override fun toString(): String {
        return "Res table config:\n" +
                "size: $size,\n" +
                "union 2: $union2Mobile,\n" +
                "union 3: $union3Locale,\n" +
                "union 4: $union4ScreenType,\n" +
                "union 5: $union5Input,\n" +
                "union 6: $union6ScreenSize,\n" +
                "union 7: $union7Version,\n" +
                "union 8: $union8ScreenConfig,\n" +
                "union 9: $union9ScreenSizeDp,\n" +
                "localeScript: $localeScript,\n" +
                "localeVariant: $localeVariant"
    }

    companion object {
        const val SIZE_BYTE = 4

        const val MCC_BYTE = 2
        const val MNC_BYTE = 2
        const val UNION_2_IMSI_BYTE = 4

        const val LANGUAGE_BYTE = 2
        const val COUNTRY_BYTE = 2
        const val UNION_3_LOCALE_BYTE = 4

        const val ORIENTATION_BYTE = 1
        const val TOUCH_SCREEN_BYTE = 1
        const val DENSITY_BYTE = 2
        const val UNION_4_SCREEN_TYPE_BYTE = 4

        const val KEYBOARD_BYTE = 1
        const val NAVIGATION_BYTE = 1
        const val INPUT_FLAGS_BYTE = 1
        const val INPUT_PAD0_BYTE = 1
        const val UNION_5_INPUT_BYTE = 4

        const val SCREEN_WIDTH_BYTE = 2
        const val SCREEN_HEIGHT_BYTE = 2
        const val UNION_6_SCREEN_SIZE_BYTE = 4

        const val SDK_VERSION_BYTE = 2
        const val MINOR_VERSION_BYTE = 2
        const val UNION_7_VERSION_BYTE = 4

        const val SCREEN_LAYOUT_BYTE = 1
        const val UI_MODE_BYTE = 1
        const val SMALLEST_SCREEN_WIDTH_DP_BYTE = 2
        const val UNION_8_SCREEN_CONFIG_BYTE = 4

        const val SCREEN_WIDTH_DP_BYTE = 2
        const val SCREEN_HEIGHT_DP_BYTE = 2
        const val UNION_9_SCREEN_SIZE_DP_BYTE = 4

        const val LOCALE_SCRIPT_BYTE = 4
        const val LOCALE_VARIANT_BYTE = 8
    }
}
