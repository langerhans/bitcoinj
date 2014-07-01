/**
 * Copyright 2014 Andreas Schildbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.dogecoin.core;

import com.google.dogecoin.utils.CoinFormat;

import java.io.Serializable;
import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Represents a monetary Bitcoin value. This class is immutable.
 */
public final class Coin implements Comparable<Coin>, Serializable {

    public static final Coin ZERO = new Coin(BigInteger.ZERO);
    public static final Coin ONE = new Coin(BigInteger.ONE);
    public static final Coin SATOSHI = ONE;
    public static final Coin TEN = new Coin(BigInteger.TEN);
    public static final Coin FIFTY_COINS = new Coin(new BigInteger("5000000000"));
    public static final Coin COIN = new Coin(new BigInteger("100000000"));
    public static final Coin NEGATIVE_SATOSHI = new Coin(BigInteger.valueOf(-1));
    public static final Coin CENT = new Coin(BigInteger.valueOf(1000000));

    /**
     * 0.001 Bitcoins, also known as 1 mBTC.
     */
    public static final Coin MILLICOIN = COIN.divide(valueOf(1000));

    /**
     * 0.000001 Bitcoins, also known as 1 µBTC or 1 uBTC.
     */
    public static final Coin MICROCOIN = MILLICOIN.divide(valueOf(1000));

    /**
     * Number of decimals for one Bitcoin. This constant is useful for quick adapting to other coins because a lot of
     * constants derive from it.
     */
    public static final int NUM_COIN_DECIMALS = 8;

    private final BigInteger value;

    public Coin(final BigInteger value) {
        this.value = value;
    }

    public Coin(final String value, final int radix) {
        this(new BigInteger(value, radix));
    }

    public Coin(final byte[] value) {
        this(new BigInteger(value));
    }

    public static Coin valueOf(final long value) {
        return new Coin(BigInteger.valueOf(value));
    }
    /**
     * Convert an amount expressed in the way humans are used to into satoshis.
     */
    public static Coin valueOf(final int coins, final int cents) {
        checkArgument(cents < 100);
        checkArgument(cents >= 0);
        checkArgument(coins >= 0);
        final Coin coin = COIN.multiply(coins).add(CENT.multiply(cents));
        checkArgument(coin.compareTo(NetworkParameters.MAX_MONEY) <= 0);
        return coin;
    }


    public static Coin parseCoin(final String value) {
        return new Coin(new BigInteger(value));
    }

    public Coin add(final Coin value) {
        return new Coin(this.value.add(value.value));
    }

    public Coin subtract(final Coin value) {
        return new Coin(this.value.subtract(value.value));
    }

    public Coin multiply(final Coin value) {
        return new Coin(this.value.multiply(value.value));
    }

    public Coin multiply(final long value) {
        return multiply(Coin.valueOf(value));
    }

    public Coin divide(final Coin value) {
        return new Coin(this.value.divide(value.value));
    }

    public Coin[] divideAndRemainder(final Coin value) {
        final BigInteger[] result = this.value.divideAndRemainder(value.value);
        return new Coin[] { new Coin(result[0]), new Coin(result[1]) };
    }

    public Coin shiftLeft(final int n) {
        return new Coin(this.value.shiftLeft(n));
    }

    public Coin shiftRight(final int n) {
        return new Coin(this.value.shiftRight(n));
    }

    public int signum() {
        return this.value.signum();
    }

    public Coin negate() {
        return new Coin(this.value.negate());
    }

    public byte[] toByteArray() {
        return this.value.toByteArray();
    }

    public long longValue() {
        return this.value.longValue();
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public BigInteger toBigInteger() {
        return value;
    }

    private static final CoinFormat FRIENDLY_FORMAT = CoinFormat.BTC.minDecimals(2).repeatOptionalDecimals(1, 6);
    private static final CoinFormat PLAIN_FORMAT = CoinFormat.BTC.minDecimals(0).repeatOptionalDecimals(1, 8);

    /**
     * Returns the value as a 0.12 type string. More digits after the decimal place will be used
     * if necessary, but two will always be present.
     */
    public String toFriendlyString() {
        return FRIENDLY_FORMAT.format(this).toString();
    }

    /**
     * <p>
     * Returns the value as a plain string denominated in BTC.
     * The result is unformatted with no trailing zeroes.
     * For instance, a value of 150000 satoshis gives an output string of "0.0015" BTC
     * </p>
     */
    public String toPlainString() {
        return PLAIN_FORMAT.format(this).toString();
    }

    /**
     * Returns true if and only if this instance represents a monetary value greater than zero,
     * otherwise false.
     */
    public boolean isPositive() {
        return signum() == 1;
    }

    /**
     * Returns true if and only if this instance represents a monetary value less than zero,
     * otherwise false.
     */
    public boolean isNegative() {
        return signum() == -1;
    }

    /**
     * Returns true if and only if this instance represents zero monetary value,
     * otherwise false.
     */
    public boolean isZero() {
        return signum() == 0;
    }

    /**
     * Returns true if the monetary value represented by this instance is greater than that
     * of the given other Coin, otherwise false.
     */
    public boolean isGreaterThan(Coin other) {
        return compareTo(other) > 0;
    }

    /**
     * Returns true if the monetary value represented by this instance is less than that
     * of the given other Coin, otherwise false.
     */
    public boolean isLessThan(Coin other) {
        return compareTo(other) < 0;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (o == null || o.getClass() != getClass())
            return false;
        final Coin other = (Coin) o;
        if (!this.value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public int compareTo(final Coin other) {
        return this.value.compareTo(other.value);
    }
}
