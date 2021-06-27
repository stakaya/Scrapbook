<?php

    /**
     * 添付ファイル付きメールを送信する。
     * @param  String $subject   件名
     * @param  String $body      本文
     * @param  String $filename  ファイル名
     * @param  String $mime      マイムタイプ
     * @param  String $to_addr   送信先アドレス
     * @param  String $from_addr 送信元アドレス
     * @return String 実行結果
     */
    function attachMail($subject, $body, $filename, $mime, $to_addr, $from_addr) {
        ($attach = file_get_contents($filename)) || die('Open Error:' . $filename);
        $filename = basename($filename);

        $boundary = "_Boundary_" . uniqid(rand(1000,9999) . '_') . "_";

        // 件名と本文のエンコード
        $subject = mb_encode_mimeheader($subject);
        $body    = mb_convert_encoding($body, 'ISO-2022-JP', 'auto');

        // 添付データのエンコード
        // 日本語のファイル名はRFC違反ですが、多くのメーラは理解します
        $filename = mb_encode_mimeheader($filename);
        $attach   = chunk_split(base64_encode($attach), 76, "\n"); // Base64に変換し76Byte分割

        // メディアタイプ未指定の場合は汎用のタイプを指定
        if (!$mime) $mime = "application/octet-stream";

        // ヘッダー
        $header = "To: $to_addr\n"
                . "From: $from_addr\n"
                . "X-Mailer: PHP/" . phpversion() . "\n"
                . "MIME-Version: 1.0\n"
                . "Content-Type: Multipart/Mixed; boundary=\"$boundary\"\n"
                . "Content-Transfer-Encoding: 7bit";

        // メールボディ
        $mbody = "--$boundary\n"
               . "Content-Type: text/plain; charset=ISO-2022-JP\n"
               . "Content-Transfer-Encoding: 7bit\n"
               . "\n"
               . "$body\n"
               . "--$boundary\n"
               . "Content-Type: $mime; name=\"$filename\"\n"
               . "Content-Transfer-Encoding: base64\n"
               . "Content-Disposition: attachment; filename=\"$filename\"\n"
               . "\n"
               . "$attach\n"
               . "--$boundary--\n";
        return mail(null, $subject, $mbody, $header);
    }

    /**
     * 長すぎるコメントを省略する
     * @param     String    $data    コメント
     * @return    String    省略文字
     */
    function cutComment($data, $len = 20) {
        return strlen($data) > $len ? mb_strcut($data, 0, $len) . '...' : $data;
    }

    /**
     * サムネイル画像を作成する
     * @param  String $jpgpath パス＋ファイル名
     * @param  String $jpgout  出力ファイル名
     * @param  Number $width   幅
     * @param  Number $height  高さ
     * @return boolean 実行結果
     */
    function createThumbNail($jpgpath, $jpgout = '', $width = 500, $height = 650) {

        if (!file_exists($jpgpath)) {
            return false;
        }

        if ($jpgout == '') {
            $jpgout = $jpgpath;
        }

        // サムネイル用JPEGファイル作成
        $in = imageCreateFromJpeg($jpgpath);

        // 画像の幅と高さを取得
        $size = getImageSize($jpgpath);
        $imageWidth  = $width;
        $imageHeight = $height;

        // 横長の場合
        if ($size[0] * $height < $size[1] * $width) {
           $imageWidth  = $size[0]  * $height / $size[1];
        }

        // 縦長の場合
        if ($size[0] * $height > $size[1] * $width) {
           $imageHeight = $size[1] * $width / $size[0];
        }

        // サムネイルの作成
        $out = imageCreateTrueColor($imageWidth, $imageHeight);
        imagecopyresampled($out, $in, 0, 0, 0, 0, $imageWidth, $imageHeight, $size[0], $size[1]);

        // サムネイル画像を作成
        imageJpeg($out, $jpgout);

        // 作成したイメージを削除
        imageDestroy($in);
        imageDestroy($out);

        return true;
    }
?>
