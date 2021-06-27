<?php
    require('fpdf/mbfpdf.php');
    require('util.php');

    define('TEMP_PATH', '/home/stakaya/temp/');

    // 文字コード
    mb_internal_encoding('EUC-JP');

    // 言語設定
    mb_language('Japanese');

    // パラメータレングス
    $pos = array(14, 20, 15, 5, 5, 256, 6); $i = 0;

    // POST情報取得
    $stdin = '';
    if (isset($HTTP_RAW_POST_DATA)) {
        $stdin = $HTTP_RAW_POST_DATA;
    } else {
        $fp = fopen('php://input', 'r');
        if(!$fp) {
            die('ER');
        }

        while(!feof($fp)) {
            $stdin .= fgets($fp);
        }
        fclose($fp);
    }

    // ヘッダ情報取得
    $date   = substr($stdin,       0, $pos[$i]); $offset  = $pos[$i++];
    $simid  = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $termid = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $seqno  = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $type   = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $mail   = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $length = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];

    // スペースを削除
    $seqno  = trim($seqno );
    $type   = trim($type  );
    $mail   = trim($mail  );
    $length = trim($length);

    // データがある場合
    if ($length > 0) {
        $data = substr($stdin, $offset, $length);
    } else {
        $data = '';
    }

    // まだファイルに続きがある場合
    if ($seqno != 'END') {
        // テンポラリファイルを作成
        $temp = @fopen(TEMP_PATH . $date . $simid . $termid, 'ab');

        // ファイルオープンチェック
        if ($temp) {
            flock($temp, LOCK_EX);
            fputs($temp, $data);
            flock($temp, LOCK_UN);
            fclose($temp);
        }

        // クライアントに正常値を返却し終了
        die('OK');
    }

    // ファイル名設定
    $filename = TEMP_PATH . $date . $simid . $termid;
    $pdffile  = TEMP_PATH . "$date.$simid.$termid.pdf";

    $fp = @fopen($filename, 'rb');
    if ($fp) {
        $temp = fread($fp, 10);
        fclose($fp);
    }

    // 画像ファイル判定
    if (preg_match('/^\xff\xd8/', $temp)) {
        createThumbNail($filename,  $filename . '.jpg');
        unlink($filename);
        $filename .= '.jpg';
    } elseif (preg_match('/^GIF8[79]a/', $temp)) {
        rename($filename,  $filename . '.gif');
        $filename .= '.gif';
    }

    // PDFファイル作成
    $comment = mb_convert_encoding($data, mb_internal_encoding(), 'SJIS');
    $GLOBALS['EUC2SJIS'] = true;

    $pdf = new MBFPDF();
    $pdf->AddMBFont(GOTHIC, 'SJIS');
    $pdf->Open();
    $pdf->AddPage();
    $pdf->SetFont(GOTHIC);
    $pdf->Cell(180, 5, cutComment($comment, 70), 1, 1, "C", 0);
    $pdf->Write(10, "\n");
    $pdf->Image($filename);
    $pdf->Write(10, "\n" . '作成日時:' . date("Y/m/d H:i:s"));
    $pdf->Output($pdffile);

    // メール送信
    $from_addr  = mb_encode_mimeheader('写メモ');
    $from_addr .= '<memo@monysong.com>';

    $to_addr    = mb_encode_mimeheader($mail);
    $to_addr   .= '<' . $mail . '>';

    $subject    = '[写メモ]ファイル送付のお知らせ(' . cutComment($comment) . ')';
    $body       = '「' . $comment . '」のファイルが出来上がりましたので送付いたします。';

    attachMail($subject, $body, $pdffile, 'application/pdf', $to_addr, $from_addr);

    unlink($filename);
    unlink($pdffile);

    // クライアントに正常値を返却
    print('OK');
?>
