<?php
    // IPフィルタ
    include_once('ipfilter.php');

    // プロトコル判定
    if (!strpos($_SERVER['SERVER_PROTOCOL'], 'HTTPS')) {
        $url = 'http://';
    } else {
        $url = 'https://';
    }

    $week = array('Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat');
    $month = array('Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec');
    $modified = $week[date('w', time())] . ', '
              . date('d', time()) . ' '
              . $month[date('n', time()) -1]
              . date(' Y H:i:s', time());
    $appSize = filesize('Scrapbook.jar');

    $appname = '写メモ';
    $param = @$_GET['key'];
    $param = base64_decode($param);

    $url = $url
         . $_SERVER['SERVER_NAME']
         . dirname($_SERVER['PHP_SELF'])
         . 'Scrapbook.jar?d=' . $param;
?>
PackageURL = <?= $url . "\n" ?>
AppSize = <?= $appSize . "\n" ?>
AppName = <?= $appname . "\n" ?>
AppParam = <?= $param . "\n" ?>
AppClass = Scrapbook
SPsize = 204800
UseNetwork = http
AccessUserInfo = yes
LastModified = <?= $modified . "\n" ?>
GetUtn = terminalid,userid
AppIcon = icon.gif
