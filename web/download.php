<?php
    // IP�t�B���^
    include_once('ipfilter.php');
    $key = @$_GET['key'];

    // �����̓`�F�b�N
    if (!$key) {
        $mail = @$_GET['mail'];
        if (!$mail) {
            header('Location: regist.php');
            return;
        }

        // ���[���A�h���X�`�F�b�N
        if (!preg_match('/^([a-zA-Z0-9])+([a-zA-Z0-9\._-])*@([a-zA-Z0-9_-])+([a-zA-Z0-9\._-]+)+$/', $mail)) {
            header('Location: regist.php');
            return;
        }

        // ���[���A�h���X�G���R�[�f�B���O
        $key = base64_encode($mail);
    }
?>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=Shift-JIS">
  <title>�����޳�۰��</title>
</head>
  <body>
    <object declare id="Scrapbook"
            data="jam.php?key=<?= $key ?>"
            type="application/x-jam">
    </object>
    <br>
    <a ijam="#Scrapbook" href="index.html">���ӂ��޳�۰��</a><br>
    <hr size="1">
    �����p��̒���
    <hr size="1">
    &#63879;�޳�۰�ނ������؂͒ʐM�������Ă����p���������B<br>
    &#63880;�g�ђ[�����̎Q�Ƃ������Ă����p���������B<br>
    <hr size="1">
  </body>
</html>
