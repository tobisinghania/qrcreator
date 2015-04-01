/*
 * Copyright 2015 Tobias Singhania
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

import java.awt.image.BufferedImage

/**
 * @author Tobias Singhania
 */
@CompileStatic
abstract class Design {
    final int width
    final int height

    protected int inputWidth
    protected int inputHeight
    protected int qrWidth
    protected int outputHeight
    protected int outputWidth
    protected int qrHeight
    protected int blockSize
    protected int leftPadding
    protected int topPadding
    protected int quietZone

    Design(int width, int height, int qz) {
        this.width = width
        this.height = height
        this.quietZone = qz
    }

    abstract BufferedImage getImage(QRCode val)

    protected void loadSizes(int inputWidth, int inputHeight) {
        this.inputWidth = inputWidth
        this.inputHeight = inputHeight
        qrWidth = inputWidth + (quietZone * 2)
        qrHeight = inputHeight + (quietZone * 2)
        outputWidth = Math.max(width, qrWidth)
        outputHeight = Math.max(height, qrHeight)

        blockSize = Math.min((int)(outputWidth / qrWidth), (int)(outputHeight / qrHeight))
        leftPadding = ((outputWidth - (inputWidth * blockSize)) / 2) as int
        topPadding = ((outputHeight - (inputHeight * blockSize)) / 2) as int
    }
}
