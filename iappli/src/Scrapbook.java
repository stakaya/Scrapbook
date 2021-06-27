import java.io.ByteArrayInputStream;

import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.IApplication;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;
import com.nttdocomo.ui.MediaManager;
import com.nttdocomo.ui.TextBox;
import com.nttdocomo.ui.VisualPresenter;

/**
 * �X�N���b�v�u�b�N<BR>
 * �A�v���P�[�V�����̃��C���N���X�ł��B.
 * <br>
 * <p>
 * @version 1.0
 * </p>
 */
public final class Scrapbook extends IApplication {

    /**
     * <code>screen</code> ���C�����
     */
    private static Screen screen = null;

    /**
     * <code>ENTRY_CGI</code> �摜�o�^CGI
     */
    public static final String ENTRY_CGI = "entry.php";

    /**
     * <code>TYPE_SOUND</code> �������ʎq
     * <code>TYPE_MOVIE</code> ���掯�ʎq
     * <code>TYPE_PICTURE</code> �Î~�掯�ʎq
     */
    public static final String
        TYPE_SOUND   = "WAV",
        TYPE_MOVIE   = "MOV",
        TYPE_PICTURE = "PIC";

    /**
     * <code>PINK  </code> �\���F�u�s���N�v��\���萔
     * <code>ORANGE</code> �\���F�u�I�����W�v��\���萔
     * <code>WHITE </code> �\���F�u���v��\���萔
     * <code>LIME  </code> �\���F�u���C���v��\���萔
     * <code>SILVER</code> �\���F�u��v��\���萔
     * <code>BLACK </code> �\���F�u���v��\���萔
     * <code>YELLOW</code> �\���F�u���F�v��\���萔
     * <code>GRAY  </code> �\���F�u�D�F�v��\���萔
     * <code>RED   </code> �\���F�u�ԁv��\���萔
     * <code>BLUE  </code> �\���F�u�v��\���萔
     */
    public static final int
        PINK   = Graphics.getColorOfRGB(248, 222, 194),
        ORANGE = Graphics.getColorOfRGB(255, 128, 0),
        WHITE  = Graphics.getColorOfName(Graphics.WHITE) ,
        LIME   = Graphics.getColorOfName(Graphics.LIME)  ,
        SILVER = Graphics.getColorOfName(Graphics.SILVER),
        BLACK  = Graphics.getColorOfName(Graphics.BLACK) ,
        YELLOW = Graphics.getColorOfName(Graphics.YELLOW),
        GRAY   = Graphics.getColorOfName(Graphics.GRAY)  ,
        RED    = Graphics.getColorOfName(Graphics.RED)   ,
        BLUE   = Graphics.getColorOfName(Graphics.BLUE);

    /**
     * �A�v���P�[�V�������N��������ŏ��ɌĂ΂�郁�\�b�h�ł��B
     */
    public void start() {
        try {
            // �ۑ��f�[�^�ǂݏo��
            ScratchPad.load();

            // ���C����ʂ̍쐬
            screen = new Screen();

            // ���C����ʂ̕\��
            screen.show();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "��Q���");
            dialog.setText(e.getClass().getName());
            dialog.show();
            terminate();
        }
    }

    /**
     * Screen<BR>
     * ���C����ʂ̒�`�N���X�ł��B
     * <p>
     * @version 1.0
     * </p>
     */
    private final class Screen extends Canvas {

        /**
         * <code>unSupportMovie</code> ���[�r�[���T�|�[�g���Ȃ��@��
         */
        private final String[] unSupportMovie = {
            "F900i",
            "F901i",
            "P900i",
            "P901i",
            "N900i",
            "N901i",
            "SH900i",
            "SH901i",
            "D800iDS",
            "M702iG",
            "M702iS"
        };

        /**
         * <code>back</code> �w�i�摜
         */
        private Image back = null;

        /**
         * <code>comment</code> �R�����g
         */
        private String comment = "";

        /**
         * <code>function</code> �@�\���X�g
         */
        private String[] function;

        /**
         * <code>mode</code> ���[�h(����E�Î~��/�I��)
         */
        private int mode = 0;

        /**
         * <code>quality</code> �掿
         */
        private int quality = 0;

        /**
         * <code>qualityList</code> �掿���X�g
         */
        private String[] qualityList;

        /**
         * <code>visual</code> ����Đ��C���[�W
         */
        private VisualPresenter visual = new VisualPresenter();

        /**
         * �R���X�g���N�^
         */
        public Screen() {
            super();
            String[] args = IApplication.getCurrentApp().getArgs();
            String phoneName = System.getProperty("microedition.platform");
            boolean isSupport = true;

            try {
                MediaData md = ScratchPad.getResource(0);
                if (md != null) {
                    MediaImage mi = MediaManager.getImage(md.toByteArray());
                    mi.use();
                    back = mi.getImage();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // �@�픻��
            for (int i = 0; i < unSupportMovie.length; i++) {
                if (phoneName.indexOf(unSupportMovie[i]) != -1) {
                    isSupport = false;
                    break;
                }
            }

            // ���悪�T�|�[�g����Ă���ꍇ
            if (isSupport) {
                function = new String[2];
                function[0] = (char) 0xE681 + " �B��";
                function[1] = (char) 0xE681 + " �I��";
            } else {
                function = new String[2];
                function[0] = (char) 0xE681 + " �B��";
                function[1] = (char) 0xE681 + " �I��";
            }

            // �J�����T�C�Y�擾
            qualityList = new String[CameraDevice.PIC_SIZE.length];
            for (int i = 0; i < CameraDevice.PIC_SIZE.length; i++) {
                qualityList[i] = Integer.toString(CameraDevice.PIC_SIZE[i][0])
                               + " * "
                               + Integer.toString(CameraDevice.PIC_SIZE[i][1]);
            }
        }

        /**
         * �㉺�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * @param param �p�����[�^
         */
        private void componentActionLeftRight(final int param) {

            // �B�e�摜�m�F
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // ���L�[�̏ꍇ
            if (param == Display.KEY_LEFT) {
                if (0 == this.mode) {
                    this.mode = this.function.length - 1;
                } else {
                    this.mode--;
                }

            // �E�L�[�̏ꍇ
            } else if (param == Display.KEY_RIGHT) {
                if (this.function.length == (this.mode + 1)) {
                    this.mode = 0;
                } else {
                    this.mode++;
                }
            }

            // �掿�N���A
            this.quality = 0;

            // �Î~��̏ꍇ
            if (this.mode == 0) {
                this.qualityList = new String[CameraDevice.PIC_SIZE.length];
                for (int i = 0; i < CameraDevice.PIC_SIZE.length; i++) {
                    this.qualityList[i] = Integer.toString(CameraDevice.PIC_SIZE[i][0])
                                        + " * "
                                        + Integer.toString(CameraDevice.PIC_SIZE[i][1]);
                }

            // �I���̏ꍇ
            } else {
                this.qualityList = new String[1];
                this.qualityList[0] = "---------";
            }
        }

        /**
         * �㉺�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * @param param �p�����[�^
         */
        private void componentActionUpDown(final int param) {

            // �B�e�摜�m�F
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // ��L�[�̏ꍇ
            if (param == Display.KEY_UP) {
                if (0 == this.quality) {
                    this.quality = this.qualityList.length - 1;
                } else {
                    this.quality--;
                }

            // ���L�[�̏ꍇ
            } else if (param == Display.KEY_DOWN) {
                if (this.qualityList.length == (this.quality + 1)) {
                    this.quality = 0;
                } else {
                    this.quality++;
                }
            }
        }

        /**
         * �`��C�x���g
         * @see com.nttdocomo.ui.Canvas#paint(com.nttdocomo.ui.Graphics)
         */
        public void paint(final Graphics g) {

            // �`��X�g�b�v
            g.lock();

            if (back != null) {
                g.drawImage(back, 0, 0);
            }

            // �R�����g�������ꍇ
            g.setColor(GRAY);
            if (this.comment.length() > 14) {
                g.drawString(this.comment.substring(0, 14), 55, 185);
            } else {
                g.drawString(this.comment, 55, 185);
            }

            // �B�e�摜�m�F
            if (CameraDevice.getNumberOfImages() > 0) {
                // �g�\��
                g.fillRect(39, 2, 162, 162);

                // �\�t�g�L�[���x���̐ݒ�
                setSoftLabel(SOFT_KEY_1, "����");
                setSoftLabel(SOFT_KEY_2, "����");
            } else {
                // �\�t�g�L�[���x���̐ݒ�
                setSoftLabel(SOFT_KEY_1, "�I��");
                if (this.mode == 0) {
                    setSoftLabel(SOFT_KEY_2, "�B��");
                } else {
                    setSoftLabel(SOFT_KEY_2, "�I��");
                }
            }

            // �J�����C���[�W�擾
            Image image = null;
            image = CameraDevice.getCameraImage();

            // �C���[�W���擾�ł����ꍇ
            if (image != null) {
                int x = 0;
                int y = 0;
                int width = 160;
                int height = 160;
                int imageWidth  = width;
                int imageHeight = height;

                // �����̏ꍇ
                if (image.getWidth() * height < image.getHeight() * width) {
                   imageWidth  = image.getWidth()  * height / image.getHeight();
                    x = (width - imageWidth) / 2;
                }

                // �c���̏ꍇ
                if (image.getWidth() * height > image.getHeight() * width) {
                   imageHeight = image.getHeight() * width / image.getWidth();
                    y = (height - imageHeight) / 2;
                }

                // �摜�\��
                g.drawScaledImage(image,
                                  x + 40, y + 3,
                                  imageWidth, imageHeight,
                                  0, 0,
                                  image.getWidth(), image.getHeight());
            }

            // �@�\�`��
            g.setColor(GRAY);
            g.setPictoColorEnabled(true);
            g.drawString(this.function[this.mode], 70, 209);
            g.setPictoColorEnabled(false);
            // �T�C�Y�`��
            g.setColor(GRAY);
            g.drawString(this.qualityList[this.quality], 64, 234);

            // �`��
            g.unlock(true);
        }

        /**
         * �L�[�C�x���g
         * @see com.nttdocomo.ui.Canvas#processEvent(int, int)
         */
        public void processEvent(final int type, final int param) {
            if (type == Display.KEY_RELEASED_EVENT) {
                switch(param) {

                // �E���L�[
                case Display.KEY_LEFT:
                case Display.KEY_RIGHT:
                    this.componentActionLeftRight(param);
                    this.repaint();
                    break;

                // �㉺�L�[
                case Display.KEY_DOWN:
                case Display.KEY_UP:
                    this.componentActionUpDown(param);
                    this.repaint();
                    break;

                // �\�t�g�L�[
                case Display.KEY_SOFT1:
                case Display.KEY_SOFT2:
                    this.softKeyReleased(param);
                    this.repaint();
                    break;

                // ����L�[
                case Display.KEY_SELECT:
                    imeOn(this.comment, TextBox.DISPLAY_ANY, TextBox.KANA);
                    break;

                // �����̂Q�L�[
                case Display.KEY_2:
                    if (this.mode == 1 || this.mode == 3) {
                        // �J�����C���[�W�擾
                        MediaImage image = CameraDevice.getMediaImage();

                        // �C���[�W���擾�ł����ꍇ
                        if (image != null) {
                            visual.setImage(image);
                            visual.play();
                        }
                    }
                    break;
                default:
                }
            }
        }

        /**
         * IME�C�x���g
         * @see com.nttdocomo.ui.Canvas#processIMEEvent(int, java.lang.String)
         */
        public void processIMEEvent(final int type, final String text) {
            if (type == IME_COMMITTED) {
                this.comment = text;
                this.repaint();
            }
        }

        /**
         * �đ��M
         */
        private void resend() {
            // ����Ȃ������f�[�^�𑗂�
            while (0 != ScratchPad.length()) {
                MediaData temp = ScratchPad.getMediaData();
                if (Communication.sendData(
                        getSourceURL() + ENTRY_CGI,
                        temp.getType(),
                        temp.getText(),
                        new ByteArrayInputStream(temp.toByteArray()))) {
                    ScratchPad.remove(temp);
                } else {
                    break;
                }
            }
        }

        /**
         * �摜�𑗐M����B
         */
        private void send() {
            boolean result = false;
            MediaData md = null;

            // ����E�Î~��𔻒�
            if (this.mode == 1 || this.mode == 3) {
                // ���f�B�A�f�[�^�ɓ����
                md = new MediaData(CameraDevice.getCameraData(),
                                   TYPE_MOVIE,
                                   this.comment);
            } else {
                // ���f�B�A�f�[�^�ɓ����
                md = new MediaData(CameraDevice.getCameraData(),
                                   TYPE_PICTURE,
                                   this.comment);
            }

            // �f�[�^���M
            result = Communication.sendData(
                         getSourceURL() + ENTRY_CGI,
                         md.getType(),
                         md.getText(),
                         md.toInputStream());

            // �ʐM���ʔ���
            if (result) {

            	// �c���Ă���f�[�^���폜
                CameraDevice.dispose();
            } else {
                Dialog dialog = null;
                dialog = new Dialog(Dialog.DIALOG_WARNING, "�ۑ��e��");
                dialog.setText("�ʐM���ɃG���[���������܂����B\n�d�g�󋵂̗ǂ��Ƃ���Ŏg�p���������B\n");
                dialog.show();
            }

            // ��ʂ�߂�
            this.show();
        }

        /**
         * ��ʂ�\��
         */
        public void show() {
            Display.setCurrent(this);
        }

        /**
         * �������ꂽ�\�t�g�L�[�������ꂽ���ɌĂяo����郁�\�b�h�ł��B.
         * <br>
         * ���̃��\�b�h�́A<code>setSoftKeyListener()</code>���g����
         * �\�t�g�L�[���X�i�[��o�^���Ă���ꍇ�ɗL���ƂȂ�܂��B
         * @param key �����ꂽ�L�[
         */
        private void softKeyReleased(final int key) {

            // �I���L�[����
            if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() == 0) {
                if (ScratchPad.length() > 0) {
                    Dialog dialog = new Dialog(Dialog.DIALOG_YESNO, "�I���m�F");
                    dialog.setText("�����M�f�[�^������܂��B\n�I�����܂����H\n\n");
                    if (Dialog.BUTTON_YES == dialog.show()) {
                        // �A�v���I��
                        IApplication.getCurrentApp().terminate();
                    }
                } else {
                    // �A�v���I��
                    IApplication.getCurrentApp().terminate();
                }

            // �B�e�L�[����
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() == 0) {
                // ����E�Î~��𔻒�
                if (this.mode == 0) {
                    CameraDevice.takePicture(this.quality);
                } else {
                    CameraDevice.selectPicture();
                }

            // ����L�[����
            } else if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() != 0) {
                // �c���Ă���f�[�^���폜
                CameraDevice.dispose();

            // ���M�L�[����
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() != 0) {
                // �f�[�^���M
                this.send();
            }
        }
    }
}
