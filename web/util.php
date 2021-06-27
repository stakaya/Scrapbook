<?php

    /**
     * �Y�t�t�@�C���t�����[���𑗐M����B
     * @param  String $subject   ����
     * @param  String $body      �{��
     * @param  String $filename  �t�@�C����
     * @param  String $mime      �}�C���^�C�v
     * @param  String $to_addr   ���M��A�h���X
     * @param  String $from_addr ���M���A�h���X
     * @return String ���s����
     */
    function attachMail($subject, $body, $filename, $mime, $to_addr, $from_addr) {
        ($attach = file_get_contents($filename)) || die('Open Error:' . $filename);
        $filename = basename($filename);

        $boundary = "_Boundary_" . uniqid(rand(1000,9999) . '_') . "_";

        // �����Ɩ{���̃G���R�[�h
        $subject = mb_encode_mimeheader($subject);
        $body    = mb_convert_encoding($body, 'ISO-2022-JP', 'auto');

        // �Y�t�f�[�^�̃G���R�[�h
        // ���{��̃t�@�C������RFC�ᔽ�ł����A�����̃��[���͗������܂�
        $filename = mb_encode_mimeheader($filename);
        $attach   = chunk_split(base64_encode($attach), 76, "\n"); // Base64�ɕϊ���76Byte����

        // ���f�B�A�^�C�v���w��̏ꍇ�͔ėp�̃^�C�v���w��
        if (!$mime) $mime = "application/octet-stream";

        // �w�b�_�[
        $header = "To: $to_addr\n"
                . "From: $from_addr\n"
                . "X-Mailer: PHP/" . phpversion() . "\n"
                . "MIME-Version: 1.0\n"
                . "Content-Type: Multipart/Mixed; boundary=\"$boundary\"\n"
                . "Content-Transfer-Encoding: 7bit";

        // ���[���{�f�B
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
     * ��������R�����g���ȗ�����
     * @param     String    $data    �R�����g
     * @return    String    �ȗ�����
     */
    function cutComment($data, $len = 20) {
        return strlen($data) > $len ? mb_strcut($data, 0, $len) . '...' : $data;
    }

    /**
     * �T���l�C���摜���쐬����
     * @param  String $jpgpath �p�X�{�t�@�C����
     * @param  String $jpgout  �o�̓t�@�C����
     * @param  Number $width   ��
     * @param  Number $height  ����
     * @return boolean ���s����
     */
    function createThumbNail($jpgpath, $jpgout = '', $width = 500, $height = 650) {

        if (!file_exists($jpgpath)) {
            return false;
        }

        if ($jpgout == '') {
            $jpgout = $jpgpath;
        }

        // �T���l�C���pJPEG�t�@�C���쐬
        $in = imageCreateFromJpeg($jpgpath);

        // �摜�̕��ƍ������擾
        $size = getImageSize($jpgpath);
        $imageWidth  = $width;
        $imageHeight = $height;

        // �����̏ꍇ
        if ($size[0] * $height < $size[1] * $width) {
           $imageWidth  = $size[0]  * $height / $size[1];
        }

        // �c���̏ꍇ
        if ($size[0] * $height > $size[1] * $width) {
           $imageHeight = $size[1] * $width / $size[0];
        }

        // �T���l�C���̍쐬
        $out = imageCreateTrueColor($imageWidth, $imageHeight);
        imagecopyresampled($out, $in, 0, 0, 0, 0, $imageWidth, $imageHeight, $size[0], $size[1]);

        // �T���l�C���摜���쐬
        imageJpeg($out, $jpgout);

        // �쐬�����C���[�W���폜
        imageDestroy($in);
        imageDestroy($out);

        return true;
    }
?>
