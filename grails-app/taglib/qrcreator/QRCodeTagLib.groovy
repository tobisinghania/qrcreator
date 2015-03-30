package qrcreator
/**
 * Copyright (C) 2015 Tobias Singhania
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
class QRCodeTagLib {
    static namespace = "qrcode"


    /**
     *  <qrcode:logo text="my text" height="100" width="100" class="MyClass" logo="mylogo.png" ecl="Q"/>
     */
    def logo = { attrs ->
        out << renderQR(attrs)
    }

    /**
     *  <qrcode:text text="another text message" alt="Buhu" class="MyClass">
     */
    def text = { attrs ->
        attrs.remove('logo')
        out << renderQR(attrs)
    }

    private Map getAttribute(Map attrs, List attributeNames) {
        def result = [:]
        for (attr in attributeNames) {
            def a = attrs.remove(attr)
            if (a == null) continue
            if ((a as String).isNumber()) a = a as int
            if (a) result[attr] = a
        }
        return result
    }

    private String encodeImage(LinkedHashMap params) {
        def qrcreator = new ImageWriter()
        if (params.logo) {
            return qrcreator.encodeBase64(
                    params.text ? params.text : "",
                    params.ecl ? params.ecl : 'L',
                    new LogoDesign(params.logo ? params.logo : "logo.png",
                            params.width ? params.width : 200,
                            params.height ? params.height : 200,
                            params.lwidth ? params.lwidth : 15,
                            params.lheight ? params.lheight : 15,
                            params.qzs ? (params.qzs > 1 ? params.qzs : 4) : 4))
        }
        return qrcreator.encodeBase64(
                params.text ? params.text : "",
                params.ecl ? params.ecl : 'L',
                params.width ? params.width : 200,
                params.height ? params.height : 200
        )

    }

    private String renderQR(attrs) {
        def params = getAttribute(attrs, ['height', 'width', 'class', 'logo', 'lwidth', 'lheight', 'alt', 'qzs', 'ecl', 'text'])
        print params
        if(! params['class']) params['class'] = 'qrcode'
        def b64Img = encodeImage(params)
        return """<img src='data:image/png;base64,
            ${b64Img}' class="${params['class']}"
            alt="${params.alt ?: params.text}" ${renderAttributes(attrs)}/>"""
    }

    private String renderAttributes(Map attrs) {
        def s = new StringBuilder()
        attrs.each { key, value ->
            if (value != null) {
                s << """ ${key}='${value}'"""
            }
        }
        s.toString()
    }

}
