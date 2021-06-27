<?php
    // IPフィルタ
    include_once('ipfilter.php');
    $key = @$_GET['key'];

    // 未入力チェック
    if (!$key) {
        $mail = @$_GET['mail'];
        if (!$mail) {
            header('Location: regist.php');
            return;
        }

        // メールアドレスチェック
        if (!preg_match('/^([a-zA-Z0-9])+([a-zA-Z0-9\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\._-]+)+$/', $mail)) {
            header('Location: regist.php');
            return;
        }

        // メールアドレスエンコーディング
        $key = base64_encode($mail);
    }
?>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=Shift-JIS">
  <title>写ﾒﾓﾀﾞｳﾝﾛｰﾄﾞ</title>
</head>
  <body>
    <object declare id="Scrapbook"
            data="jam.php?key=<?= $key ?>"
            type="application/x-jam">
    </object>
    <br>
    <a ijam="#Scrapbook" href="index.html">写ﾒﾓをﾀﾞｳﾝﾛｰﾄﾞ</a><br>
    <hr size="1">
    ●利用上の注意
    <hr size="1">
    &#63879;ﾀﾞｳﾝﾛｰﾄﾞしたｱﾌﾟﾘは通信を許可してご利用ください。<br>
    &#63880;携帯端末情報の参照を許可してご利用ください。<br>
    <hr size="1">
  </body>
</html>
