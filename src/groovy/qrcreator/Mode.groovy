/*
 * Copyright 2007 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package qrcreator

import groovy.transform.CompileStatic

/**
 * <p>See ISO 18004:2006, 6.4.1, Tables 2 and 3. This enum encapsulates the various modes in which
 * data can be encoded to bits in the QR code standard.</p>
 *
 * @author Sean Owen
 *
 * Modified for Groovy compatibility
 * @author Tobias Singhania
 */
@CompileStatic
enum Mode {

//    TERMINATOR([0, 0, 0], 0x00), // Not really a mode...
//    NUMERIC([10, 12, 14], 0x01),
//    ALPHANUMERIC([9, 11, 13], 0x02),
//    STRUCTURED_APPEND([0, 0, 0], 0x03), // Not supported
//    BYTE([8, 16, 16], 0x04),
//    ECI([0, 0, 0], 0x07), // character counts don't apply
//    KANJI([8, 10, 12], 0x08),
//    FNC1_FIRST_POSITION([0, 0, 0], 0x05),
//    FNC1_SECOND_POSITION([0, 0, 0], 0x09),
//    /** See GBT 18284-2000; "Hanzi" is a transliteration of this mode name. */
//            HANZI([8, 10, 12], 0x0D)

    TERMINATOR, // Not really a mode...
    NUMERIC,
    ALPHANUMERIC,
    STRUCTURED_APPEND, // Not supported
    BYTE,
    ECI, // character counts don't apply
    KANJI,
    FNC1_FIRST_POSITION,
    FNC1_SECOND_POSITION,
    /** See GBT 18284-2000; "Hanzi" is a transliteration of this mode name. */
    HANZI

    private int[] characterCountBitsForVersions
    private int bits

//    Mode(/*int[] characterCountBitsForVersions, int bits*/) {
//        print "Enum Constructor"
//        this.characterCountBitsForVersions = getCharacterCountBitsForVersions(this)
//        this.bits = getBits(this)
//    }


    static int[] getCharacterCountBitsForVersions(Mode m) {
        switch (m) {
            case TERMINATOR:           return [0, 0, 0] as int[]
            case NUMERIC:              return [10, 12, 14] as int[]
            case ALPHANUMERIC:         return [9, 11, 13] as int[]
            case STRUCTURED_APPEND:    return [0, 0, 0] as int[]
            case BYTE:                 return [8, 16, 16] as int[]
            case FNC1_FIRST_POSITION:  return [0, 0, 0] as int[]
            case ECI:                  return [0, 0, 0] as int[]
            case KANJI:                return [8, 10, 12] as int[]
            case FNC1_SECOND_POSITION: return [0, 0, 0] as int[]
            case HANZI:                return [8, 10, 12] as int[] // 0xD is defined in GBT 18284-2000, may not be supported in foreign country
            default:                   throw new IllegalArgumentException()
        }
    }

    /**
     * @param bits four bits encoding a QR Code data mode
     * @return Mode encoded by these bits
     * @throws IllegalArgumentException if bits do not correspond to a known mode
     */
    static Mode forBits(int bits) {
        switch (bits) {
            case 0x0: return TERMINATOR
            case 0x1: return NUMERIC
            case 0x2: return ALPHANUMERIC
            case 0x3: return STRUCTURED_APPEND
            case 0x4: return BYTE
            case 0x5: return FNC1_FIRST_POSITION
            case 0x7: return ECI
            case 0x8: return KANJI
            case 0x9: return FNC1_SECOND_POSITION
            case 0xD: return HANZI // 0xD is defined in GBT 18284-2000, may not be supported in foreign country
            default:  throw new IllegalArgumentException()
        }
    }

    static int getBits(Mode m) {
        switch (m) {
            case TERMINATOR:           return 0x0
            case NUMERIC:              return 0x1
            case ALPHANUMERIC:         return 0x2
            case STRUCTURED_APPEND:    return 0x3
            case BYTE:                 return 0x4
            case FNC1_FIRST_POSITION:  return 0x5
            case ECI:                  return 0x7
            case KANJI:                return 0x8
            case FNC1_SECOND_POSITION: return 0x9
            case HANZI:                return 0xD // 0xD is defined in GBT 18284-2000, may not be supported in foreign country
            default:                   throw new IllegalArgumentException()
        }
    }

    /**
     * @param version version in question
     * @return number of bits used, in this QR Code symbol {@link Version}, to encode the
     *         count of characters that will follow encoded in this Mode
     */
    int getCharacterCountBits(Version version) {
        int number = version.versionNumber
        int offset
        if (number <= 9) {
            offset = 0
        } else if (number <= 26) {
            offset = 1
        } else {
            offset = 2
        }
        return getCharacterCountBitsForVersions(this)[offset]
    }

    int getBits() {
        return getBits(this)
    }
}
