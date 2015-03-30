# QRCreator Grails Plugin
Grails Plugin for embedding QR codes in a webpage which is based on code from the 
[zxing library ](https://github.com/zxing/zxing)

It allows you to place a logo in the center of the QR code and gives full control over the error correction level and quite zone size.  
Logos can be loaded from a local file on the server or from a URL.  
The QR code is embedded directly into the HTML page as Base64 encoded PNG image when created with the provided TagLib.


## QRCreator TagLib

### Available tags

* `<qrcode:text .../>` Encode text in QR code
* `<qrcode:logo .../>` Encode text in QR code with custom logo 

### Examples  

Create a simple QR code, which encodes the text: "A message".   
The QR code has a default width of 200x200 px.

```html
 <qrcode:text class="MyClass" alt="myAlt" text="A message"/>
```
This code creates 
```html
<img src="data:image/png;base64,iVBORw...AAElFTkSuQmCC" class="MyCLass" alt="myAlt">
```
![QR 1](/img/qr1.png)
<br/>
<br/>
The next example creates a QR code with a logo in the center, which has
10% of the QR code width (`lwidth="10"`) and 10% of the
height (`lheight="10"`).  The default values are 15%.  
The `logo` stored in `logo.png` is loaded from a local file on the server.  
The parameter `qz` refers to the quietZone, that is the white border around the QR code. Its dimension is in blocksize of the QR code, so a 
value of 2 means, that the quiteZone will be 2 times the size of a block in the QR code. Its default value is 4.
```html
<qrcode:logo height="400" width="400" class="MyCLass" lwidth="10" lheight="10" logo="images/logo.png" qzs="2" text="Another messages"/>
```
![QR 1](/img/qr2.png)
<br/>
<br/>

In this example another QR code with a logo is created, but here the logo is retrieved from an URL.  
Also note the parameter `ecl` which specifies the error correction level of the QR code.  
Possible values are 
* `L` ... 7% of codewords can be restored.
* `M` ... 15% of codewords can be restored.
* `Q` ... 25% of codewords can be restored.
* `H` ... 30% of codewords can be restored.

```html
<qrcode:logo height="200" width="200" class="MyCLass" alt="myAlt" lwidth="25" lheight="25" logo="http://upload.wikimedia.org/wikipedia/commons/2/28/Ubuntu-sur.png" ecl="H" text="Last but not least"/>

```
![QR 1](/img/qr3.png)
<br/>
<br/>

### Attributes

#### Commom Attributes

* `text`  
	The text to be encoded in the QR code
* `width, height`  
	The width and the height of the QR code. Those values refer to the actual height of the PNG image in px, not the attributes
    of the `<img>` tag. Default: 200px
* `qzs`  
	The QuiteZoneSize is the size of the white border around the QR code. Its dimension is in blocksize of the QR code. Default: 4
* `ecl`  
	The error correction level defines the redundancy of the QR code.
    * `L` ... 7% of codewords can be restored.
	* `M` ... 15% of codewords can be restored.
	* `Q` ... 25% of codewords can be restored.
	* `H` ... 30% of codewords can be restored.
    
    
#### Attributes for logo placement

* `logo`  
	Specifies the source of the logo image. May be a local path or a URL.
* `width, lheight`  
	Defines the width, height of the logo in percent of the QR code size.
	

