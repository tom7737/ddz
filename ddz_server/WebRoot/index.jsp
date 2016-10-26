<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML>
<html class="js canvas canvastext geolocation rgba hsla no-multiplebgs borderimage borderradius boxshadow opacity no-cssanimations csscolumns no-cssgradients no-cssreflections csstransforms no-csstransforms3d no-csstransitions  video audio cufon-active fontface cufon-ready">
<head>
 <script language="JavaScript" src="/js/modernizr-1.6.min.js"></script>
 <style type="text/css">
  #nice {   
   background: url(background-one.png) top left repeat-x;
   } 
  .multiplebgs #nice {    
   background: url(background-one.png) top left repeat-x,
   url(background-two.png) bottom left repeat-x;
   }
 </style>
</head>

<body>
    Temperature: <span id="push"></span><br>
    <IFRAME ID='ifm2' WIDTH='189' HEIGHT='190' ALIGN='CENTER' MARGINWIDTH='0' MARGINHEIGHT='0' HSPACE='0' VSPACE='0' FRAMEBORDER='0' SCROLLING='NO' SRC='http://weather.qq.com/inc/ss258.htm'></IFRAME>
    <br>
    <button onclick="start();">Start</button>
    <script type="text/javascript">
    function start() {
        var eventSource = new EventSource("hello/test");
        eventSource.onmessage = function(event) {
            document.getElementById('push').innerHTML = event.data;
        };
    }
    </script>
</body>
</html>

