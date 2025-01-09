package core.math.helpers

fun highestBitSet(input: Int): Int {
    require(input != 0) { "Zero is invalid input!" }
    //require(input.toUInt().countOneBits() <= 1) { "Input should have only one bit set!" }
    return 31 - Integer.numberOfLeadingZeros(input)
}