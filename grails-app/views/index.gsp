<%--
  Created by IntelliJ IDEA.
  User: tobi
  Date: 29.03.15
  Time: 23:16
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>QR code generator</title>
    <style>
        .center {
            margin-left: auto;
            margin-right: auto;
            width: 80%;
            background-color: #b0e0e6;
        }
        #container {
            margin: 100 30px;
            background: #fff;
        }
        #content {
            clear: left;
            padding: 20px;
            background-color: rgba(217, 216, 212, 0.100);
            border: 2px solid #8AC007;
            border-radius: 25px;
        }
        #content h2 {
            color: #000;
            font-size: 160%;
            margin: 0 0 .5em;
        }
        .codebox {
            border:1px solid #a1a1a1;
            border-radius: 10px;
            background-color:#EEEEFF;
            overflow:auto;
            padding:10px;

        }
        .codebox code {
            font-size:0.9em;
        }
    </style>
</head>

<body>
<div id="container">
    <div id="content" class="center">
        <h1>DemoPage <a href="https://github.com/tobisinghania/qrcreator">QRCreator</a> Grails Plugin</h1>
        <h3>Embed QR code with class MyClass and default width and height</h3>
        This QR code has a default size of 200x200px. All QR codes generated with the TagLib are embedded as
        base64 encoded images into the html page. <br><br>
        <b>@param class</b> The html class which the resulting 'img' tag will have <br/>
        <b>@param alt</b>   The alt attribute of the 'img' tag <br/>
        <b>@param text</b>  The text encoded in the qr code<br/><br/>
        <div class="codebox">
            ${'<qrcode:text class="MyCLass" alt="Failed" text="A message"/>'.encodeAsHTML()}
        </div>
        <qrcode:text class="MyCLass" alt="Failed" text="A message"/>
        <br/>

        <h3>Create QR code with custom logo in the center</h3>
        <b>@param width</b> The width (in px) of the QR code (default 200px)<br/>
        <b>@param height</b> The height (in px) of the QR code (default 200px)<br/>
        <b>@param lwidth</b>  The width of the logo in percent of the QR code width (default is 15%)<br/>
        <b>@param lheight</b>  The height of the logo in percent of the QR code height (default is 15%)<br/>
        <b>@param logo</b>  The source of the logo (can be path on the server or url, here a path is used)<br/>
        <b>@param qzs</b>  QuiteZoneSize, refers to the white border around the code, default is 4<br/>
        <b>@param text</b>  The text encoded in the qr code<br/><br/>

        <div class="codebox">
            ${'<qrcode:logo height="400" width="400" lwidth="10" lheight="10" logo="images/logo.png qzs="2" text="Another messages"/>'.encodeAsHTML()}
        </div>
        <qrcode:logo height="400" width="400" class="MyCLass" alt="Failed" lwidth="10" lheight="10" logo="images/logo.png" qzs="2"
                     text="Another messages"/>
        <br/>


        <h3>Create QR code with custom logo in the center and specify the QR code error correction level</h3>
        <b>@param width</b> The width (in px) of the QR code (default 200px)<br/>
        <b>@param height</b> The height (in px) of the QR code (default 200px)<br/>
        <b>@param lwidth</b>  The width of the logo in percent of the QR code width (default is 15%)<br/>
        <b>@param lheight</b>  The height of the logo in percent of the QR code height (default is 15%)<br/>
        <b>@param logo</b>  The source of the logo (can be path on the server or url, here a URL is used)<br/>
        <b>@param ecl</b>  The error correction level of the QR code. Possible values: L ~ 7%, M ~ 15%, Q ~ 25%, H ~ 30% <br/>
        <b>@param text</b>  The text encoded in the qr code<br/><br/>

        <div class="codebox">
            ${'<qrcode:logo height="200" width="200" class="MyCLass" alt="Failed" lwidth="25" lheight="25" logo="http://upload.wikimedia.org/wikipedia/commons/2/28/Ubuntu-sur.png" ecl="H" text="Last but not least"/>'.encodeAsHTML()}
        </div>
        <qrcode:logo height="200" width="200" class="MyCLass" alt="Failed" lwidth="25" lheight="25"
                     logo="http://upload.wikimedia.org/wikipedia/commons/2/28/Ubuntu-sur.png" ecl="H"
                     text="Last but not least"/>
        <br/>



    </div>
</div>
</body>
</html>