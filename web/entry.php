<?php
    require('fpdf/mbfpdf.php');
    require('util.php');

    define('TEMP_PATH', '/home/stakaya/temp/');

    // ʸ��������
    mb_internal_encoding('EUC-JP');

    // ��������
    mb_language('Japanese');

    // �ѥ�᡼����󥰥�
    $pos = array(14, 20, 15, 5, 5, 256, 6); $i = 0;

    // POST�������
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

    // �إå��������
    $date   = substr($stdin,       0, $pos[$i]); $offset  = $pos[$i++];
    $simid  = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $termid = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $seqno  = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $type   = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $mail   = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];
    $length = substr($stdin, $offset, $pos[$i]); $offset += $pos[$i++];

    // ���ڡ�������
    $seqno  = trim($seqno );
    $type   = trim($type  );
    $mail   = trim($mail  );
    $length = trim($length);

    // �ǡ�����������
    if ($length > 0) {
        $data = substr($stdin, $offset, $length);
    } else {
        $data = '';
    }

    // �ޤ��ե������³����������
    if ($seqno != 'END') {
        // �ƥ�ݥ��ե���������
        $temp = @fopen(TEMP_PATH . $date . $simid . $termid, 'ab');

        // �ե����륪���ץ�����å�
        if ($temp) {
            flock($temp, LOCK_EX);
            fputs($temp, $data);
            flock($temp, LOCK_UN);
            fclose($temp);
        }

        // ���饤����Ȥ������ͤ��ֵѤ���λ
        die('OK');
    }

    // �ե�����̾����
    $filename = TEMP_PATH . $date . $simid . $termid;
    $pdffile  = TEMP_PATH . "$date.$simid.$termid.pdf";

    $fp = @fopen($filename, 'rb');
    if ($fp) {
        $temp = fread($fp, 10);
        fclose($fp);
    }

    // �����ե�����Ƚ��
    if (preg_match('/^\xff\xd8/', $temp)) {
        createThumbNail($filename,  $filename . '.jpg');
        unlink($filename);
        $filename .= '.jpg';
    } elseif (preg_match('/^GIF8[79]a/', $temp)) {
        rename($filename,  $filename . '.gif');
        $filename .= '.gif';
    }

    // PDF�ե��������
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
    $pdf->Write(10, "\n" . '��������:' . date("Y/m/d H:i:s"));
    $pdf->Output($pdffile);

    // �᡼������
    $from_addr  = mb_encode_mimeheader('�̥��');
    $from_addr .= '<memo@monysong.com>';

    $to_addr    = mb_encode_mimeheader($mail);
    $to_addr   .= '<' . $mail . '>';

    $subject    = '[�̥��]�ե��������դΤ��Τ餻(' . cutComment($comment) . ')';
    $body       = '��' . $comment . '�פΥե����뤬����夬��ޤ����Τ����դ������ޤ���';

    attachMail($subject, $body, $pdffile, 'application/pdf', $to_addr, $from_addr);

    unlink($filename);
    unlink($pdffile);

    // ���饤����Ȥ������ͤ��ֵ�
    print('OK');
?>
