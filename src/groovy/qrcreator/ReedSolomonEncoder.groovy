/*
 * Copyright 2008 ZXing authors
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
 * <p>Implements Reed-Solomon enbcoding, as the name implies.</p>
 *
 * @author Sean Owen
 * @author William Rucklidge
 */
@CompileStatic
class ReedSolomonEncoder {

    private GenericGF field
    private List<GenericGFPoly> cachedGenerators = []

    ReedSolomonEncoder(GenericGF field) {
        this.field = field
        cachedGenerators << new GenericGFPoly(field,[1] as int[])
    }

    private GenericGFPoly buildGenerator(int degree) {
        if (degree >= cachedGenerators.size()) {
            GenericGFPoly lastGenerator = cachedGenerators[-1]
            for (int d = cachedGenerators.size(); d <= degree; d++) {
                GenericGFPoly nextGenerator = lastGenerator.multiply(
                        new GenericGFPoly(field, [ 1, field.exp(d - 1 + field.generatorBase) ]  as int[]))
                cachedGenerators << nextGenerator
                lastGenerator = nextGenerator
            }
        }
        return cachedGenerators[degree]
    }

    void encode(int[] toEncode, int ecBytes) {
        if (ecBytes == 0) {
            throw new IllegalArgumentException("No error correction bytes")
        }
        int dataBytes = toEncode.size() - ecBytes
        if (dataBytes <= 0) {
            throw new IllegalArgumentException("No data bytes provided")
        }
        GenericGFPoly generator = buildGenerator(ecBytes)
        int[] infoCoefficients = new int[dataBytes]
        System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes)
        GenericGFPoly info = new GenericGFPoly(field, infoCoefficients)
        info = info.multiplyByMonomial(ecBytes, 1)
        GenericGFPoly remainder = info.divide(generator)[1]
        int[] coefficients = remainder.coefficients
        int numZeroCoefficients = ecBytes - coefficients.size()
        numZeroCoefficients.times { int i -> toEncode[dataBytes + i] = 0 }
        System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.size())
    }
}
