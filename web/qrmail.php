<?php
    // 文字コード
    mb_internal_encoding('EUC-JP');

    // 言語設定
    mb_language('Japanese');

    $to = @$_GET['mail'];

    // 未入力チェック
    if (!$to) {
        header('Location: index.html');
        return;
    }

    // メールアドレスチェック
    if (!preg_match('/^([a-zA-Z0-9])+([a-zA-Z0-9\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\._-]+)+$/', $to)) {
        header('Location: index.html');
        return;
    }

    // プロトコル判定
    if (!strpos($_SERVER['SERVER_PROTOCOL'], 'HTTPS')) {
        $url = 'http://';
    } else {
        $url = 'https://';
    }

    $url = $url
         . $_SERVER['SERVER_NAME']
         . '/qr/?d='
         . $url
         . $_SERVER['SERVER_NAME']
         . dirname($_SERVER['PHP_SELF'])
         . 'download.php?key='
         . base64_encode($to);

    $from = 'memo@monysong.com';
    $subject = '[写メモ]アプリダウンロードのお知らせ';
    $body = '下記に同意の上、リンクをクリックしてください。' . "\n"
          . 'メーラによってはURLに改行が入る場合があります。' . "\n"
          . 'その場合、URLをブラウザへコピー＆ペーストしてください。' . "\n"
          . $url . "\n";
    $name = mb_convert_encoding('写メモ', 'ISO-2022-JP');
    $from = 'From:' . mb_encode_mimeheader($name) . '<' . $from . '>';

$body .=<<<LICENSE_FILE

------------------------------------------------------------------------
                    「写メモ」使用許諾書
                                                    有限会社もにーそんぐ
------------------------------------------------------------------------
本使用許諾書（以下、「本許諾書」といいます）の以下の条件をよくお読みください。
本許諾書は、「写メモ」以下、「本ソフトウェア」といいます）のユーザーと
有限会社もにーそんぐ（以下、「製作会社」といいます）の間の合意書です。
本ソフトウェアをインストールすることによって
ユーザーは本許諾書の条件に拘束されることに同意したものとみなされます。
本許諾書の全ての条件に同意されない場合、ユーザーは本ソフトウェアを
使用することはできません。

・使用許諾
　　製作会社は、本許諾書の条件に基づき、本ソフトウェアをインストールして使
    用する非独占的権利をユーザー1名のみに無償で許諾します。
    ユーザーは、本ソフトウェアを携帯電話上にインストールし
    使用することができます。

・著作権その他の権利の帰属
　　本ソフトウェアおよび付属文書に関する所有権、知的財産権その他一切の権
　　限は製作会社に帰属します。本ソフトウェアは、著作権法および国際著作権条
    約をはじめ、その他の無体財産権に関する法律ならびに条約により保護され
    ています。ユーザーは、本ソフトウェアあるいは本許諾書その他の付属文書
    に付された権利表示を改変あるいは除去してはいけません。

・複製および頒布
　　ユーザーは、本許諾書のあらゆる条項を遵守することを条件に、
　　本ソフトウェアを複製、頒布することができます。ただし、インターネット
    上において頒布する場合、雑誌、書籍等に収録、頒布する場合は、製作会社の
    事前の許諾を得なければならないものとします。

・禁止事項
　　ユーザーは、以下のことを行うことはできません。
　　1）本ソフトウェアを販売ならびに販売を目的とした
       宣伝、展示、使用、複製、営業等を行うこと。
　　2）本ソフトウェアの使用権を譲渡あるいは再許諾すること。
　　3）本ソフトウェアを貸与、リースもしくは担保設定すること。
    4) 本ソフトウエアを、修正、翻訳、解析(リバースエンジニアリング)、
       逆コンパイル、逆アセンブルすること、本ソフトウエアを基に派生品を作ること。
　　5）本ソフトウエアの著作権の商標やレーベルを削除すること。

・免責事項
　　製作会社は、本ソフトウェアおよび付属文書について、
    品質、性能を含め一切保証はいたしません。
    いかなる場合においても、本ソフトウェアおよび付属文書の
    使用または使用不能から生じるコンピュータの故障または損傷、
    情報の消失、その他あらゆる直接的および
    間接的損害に関し、製作会社は一切責任を負いません。
LICENSE_FILE;

    mb_send_mail($to, $subject, $body, $from);
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=EUC-JP">
    <meta name="keywords" content="吉祥寺,写真,PDF,写メモ,写memo,写めも,モバターム,monysong,もにーそんぐ,Webサイト制作,システム開発,システム構築,ウェブ制作,ウェブサイト制作,ホームページ制作,Webサイト構築,サーバ構築,サーバ運用,サーバ保守" >
    <meta name="description" content="写メモ,写memo,写めも,写真でメモ" >
    <meta name="robots" content="index,follow">
    <title>写メモ</title>
    <link rel="shortcut icon" href="image/favicon.ico">
    <link href="style/common.css" rel="stylesheet" type="text/css">
  </head>
  <body>
    <div align="center">
      <div id="territory">
        <div id="header"></div>
        <div id="main">
          <h2>写メモ</h2>
          <p>
            メールが送信されました。<br>
            送信されたメールのリンクからQRコードを読み取ってください。<br>
          </p>
        </div>
        <div id="copyright">Copyright&copy; 2009 Monysong Corporation. All Rights Reserved.</div>
        <div id="footer"><img src="image/footer.gif" border="0" title="" alt=""></div>
      </div>
    </div>
  </body>
</html>
