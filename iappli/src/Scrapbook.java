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
 * スクラップブック<BR>
 * アプリケーションのメインクラスです。.
 * <br>
 * <p>
 * @version 1.0
 * </p>
 */
public final class Scrapbook extends IApplication {

    /**
     * <code>screen</code> メイン画面
     */
    private static Screen screen = null;

    /**
     * <code>ENTRY_CGI</code> 画像登録CGI
     */
    public static final String ENTRY_CGI = "entry.php";

    /**
     * <code>TYPE_SOUND</code> 音声識別子
     * <code>TYPE_MOVIE</code> 動画識別子
     * <code>TYPE_PICTURE</code> 静止画識別子
     */
    public static final String
        TYPE_SOUND   = "WAV",
        TYPE_MOVIE   = "MOV",
        TYPE_PICTURE = "PIC";

    /**
     * <code>PINK  </code> 表示色「ピンク」を表す定数
     * <code>ORANGE</code> 表示色「オレンジ」を表す定数
     * <code>WHITE </code> 表示色「白」を表す定数
     * <code>LIME  </code> 表示色「ライム」を表す定数
     * <code>SILVER</code> 表示色「銀」を表す定数
     * <code>BLACK </code> 表示色「黒」を表す定数
     * <code>YELLOW</code> 表示色「黄色」を表す定数
     * <code>GRAY  </code> 表示色「灰色」を表す定数
     * <code>RED   </code> 表示色「赤」を表す定数
     * <code>BLUE  </code> 表示色「青」を表す定数
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
     * アプリケーションが起動したら最初に呼ばれるメソッドです。
     */
    public void start() {
        try {
            // 保存データ読み出し
            ScratchPad.load();

            // メイン画面の作成
            screen = new Screen();

            // メイン画面の表示
            screen.show();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "障害情報");
            dialog.setText(e.getClass().getName());
            dialog.show();
            terminate();
        }
    }

    /**
     * Screen<BR>
     * メイン画面の定義クラスです。
     * <p>
     * @version 1.0
     * </p>
     */
    private final class Screen extends Canvas {

        /**
         * <code>unSupportMovie</code> ムービーをサポートしない機種
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
         * <code>back</code> 背景画像
         */
        private Image back = null;

        /**
         * <code>comment</code> コメント
         */
        private String comment = "";

        /**
         * <code>function</code> 機能リスト
         */
        private String[] function;

        /**
         * <code>mode</code> モード(動画・静止画/選択)
         */
        private int mode = 0;

        /**
         * <code>quality</code> 画質
         */
        private int quality = 0;

        /**
         * <code>qualityList</code> 画質リスト
         */
        private String[] qualityList;

        /**
         * <code>visual</code> 動画再生イメージ
         */
        private VisualPresenter visual = new VisualPresenter();

        /**
         * コンストラクタ
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

            // 機種判別
            for (int i = 0; i < unSupportMovie.length; i++) {
                if (phoneName.indexOf(unSupportMovie[i]) != -1) {
                    isSupport = false;
                    break;
                }
            }

            // 動画がサポートされている場合
            if (isSupport) {
                function = new String[2];
                function[0] = (char) 0xE681 + " 撮る";
                function[1] = (char) 0xE681 + " 選ぶ";
            } else {
                function = new String[2];
                function[0] = (char) 0xE681 + " 撮る";
                function[1] = (char) 0xE681 + " 選ぶ";
            }

            // カメラサイズ取得
            qualityList = new String[CameraDevice.PIC_SIZE.length];
            for (int i = 0; i < CameraDevice.PIC_SIZE.length; i++) {
                qualityList[i] = Integer.toString(CameraDevice.PIC_SIZE[i][0])
                               + " * "
                               + Integer.toString(CameraDevice.PIC_SIZE[i][1]);
            }
        }

        /**
         * 上下キーが押された時に呼び出されるメソッドです。.
         * <br>
         * @param param パラメータ
         */
        private void componentActionLeftRight(final int param) {

            // 撮影画像確認
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // 左キーの場合
            if (param == Display.KEY_LEFT) {
                if (0 == this.mode) {
                    this.mode = this.function.length - 1;
                } else {
                    this.mode--;
                }

            // 右キーの場合
            } else if (param == Display.KEY_RIGHT) {
                if (this.function.length == (this.mode + 1)) {
                    this.mode = 0;
                } else {
                    this.mode++;
                }
            }

            // 画質クリア
            this.quality = 0;

            // 静止画の場合
            if (this.mode == 0) {
                this.qualityList = new String[CameraDevice.PIC_SIZE.length];
                for (int i = 0; i < CameraDevice.PIC_SIZE.length; i++) {
                    this.qualityList[i] = Integer.toString(CameraDevice.PIC_SIZE[i][0])
                                        + " * "
                                        + Integer.toString(CameraDevice.PIC_SIZE[i][1]);
                }

            // 選択の場合
            } else {
                this.qualityList = new String[1];
                this.qualityList[0] = "---------";
            }
        }

        /**
         * 上下キーが押された時に呼び出されるメソッドです。.
         * <br>
         * @param param パラメータ
         */
        private void componentActionUpDown(final int param) {

            // 撮影画像確認
            if (CameraDevice.getNumberOfImages() > 0) {
                return;
            }

            // 上キーの場合
            if (param == Display.KEY_UP) {
                if (0 == this.quality) {
                    this.quality = this.qualityList.length - 1;
                } else {
                    this.quality--;
                }

            // 下キーの場合
            } else if (param == Display.KEY_DOWN) {
                if (this.qualityList.length == (this.quality + 1)) {
                    this.quality = 0;
                } else {
                    this.quality++;
                }
            }
        }

        /**
         * 描画イベント
         * @see com.nttdocomo.ui.Canvas#paint(com.nttdocomo.ui.Graphics)
         */
        public void paint(final Graphics g) {

            // 描画ストップ
            g.lock();

            if (back != null) {
                g.drawImage(back, 0, 0);
            }

            // コメントが長い場合
            g.setColor(GRAY);
            if (this.comment.length() > 14) {
                g.drawString(this.comment.substring(0, 14), 55, 185);
            } else {
                g.drawString(this.comment, 55, 185);
            }

            // 撮影画像確認
            if (CameraDevice.getNumberOfImages() > 0) {
                // 枠表示
                g.fillRect(39, 2, 162, 162);

                // ソフトキーラベルの設定
                setSoftLabel(SOFT_KEY_1, "消す");
                setSoftLabel(SOFT_KEY_2, "送る");
            } else {
                // ソフトキーラベルの設定
                setSoftLabel(SOFT_KEY_1, "終り");
                if (this.mode == 0) {
                    setSoftLabel(SOFT_KEY_2, "撮る");
                } else {
                    setSoftLabel(SOFT_KEY_2, "選ぶ");
                }
            }

            // カメライメージ取得
            Image image = null;
            image = CameraDevice.getCameraImage();

            // イメージが取得できた場合
            if (image != null) {
                int x = 0;
                int y = 0;
                int width = 160;
                int height = 160;
                int imageWidth  = width;
                int imageHeight = height;

                // 横長の場合
                if (image.getWidth() * height < image.getHeight() * width) {
                   imageWidth  = image.getWidth()  * height / image.getHeight();
                    x = (width - imageWidth) / 2;
                }

                // 縦長の場合
                if (image.getWidth() * height > image.getHeight() * width) {
                   imageHeight = image.getHeight() * width / image.getWidth();
                    y = (height - imageHeight) / 2;
                }

                // 画像表示
                g.drawScaledImage(image,
                                  x + 40, y + 3,
                                  imageWidth, imageHeight,
                                  0, 0,
                                  image.getWidth(), image.getHeight());
            }

            // 機能描画
            g.setColor(GRAY);
            g.setPictoColorEnabled(true);
            g.drawString(this.function[this.mode], 70, 209);
            g.setPictoColorEnabled(false);
            // サイズ描画
            g.setColor(GRAY);
            g.drawString(this.qualityList[this.quality], 64, 234);

            // 描画
            g.unlock(true);
        }

        /**
         * キーイベント
         * @see com.nttdocomo.ui.Canvas#processEvent(int, int)
         */
        public void processEvent(final int type, final int param) {
            if (type == Display.KEY_RELEASED_EVENT) {
                switch(param) {

                // 右左キー
                case Display.KEY_LEFT:
                case Display.KEY_RIGHT:
                    this.componentActionLeftRight(param);
                    this.repaint();
                    break;

                // 上下キー
                case Display.KEY_DOWN:
                case Display.KEY_UP:
                    this.componentActionUpDown(param);
                    this.repaint();
                    break;

                // ソフトキー
                case Display.KEY_SOFT1:
                case Display.KEY_SOFT2:
                    this.softKeyReleased(param);
                    this.repaint();
                    break;

                // 決定キー
                case Display.KEY_SELECT:
                    imeOn(this.comment, TextBox.DISPLAY_ANY, TextBox.KANA);
                    break;

                // 数字の２キー
                case Display.KEY_2:
                    if (this.mode == 1 || this.mode == 3) {
                        // カメライメージ取得
                        MediaImage image = CameraDevice.getMediaImage();

                        // イメージが取得できた場合
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
         * IMEイベント
         * @see com.nttdocomo.ui.Canvas#processIMEEvent(int, java.lang.String)
         */
        public void processIMEEvent(final int type, final String text) {
            if (type == IME_COMMITTED) {
                this.comment = text;
                this.repaint();
            }
        }

        /**
         * 再送信
         */
        private void resend() {
            // 送れなかったデータを送る
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
         * 画像を送信する。
         */
        private void send() {
            boolean result = false;
            MediaData md = null;

            // 動画・静止画を判定
            if (this.mode == 1 || this.mode == 3) {
                // メディアデータに入れる
                md = new MediaData(CameraDevice.getCameraData(),
                                   TYPE_MOVIE,
                                   this.comment);
            } else {
                // メディアデータに入れる
                md = new MediaData(CameraDevice.getCameraData(),
                                   TYPE_PICTURE,
                                   this.comment);
            }

            // データ送信
            result = Communication.sendData(
                         getSourceURL() + ENTRY_CGI,
                         md.getType(),
                         md.getText(),
                         md.toInputStream());

            // 通信結果判定
            if (result) {

            	// 残っているデータを削除
                CameraDevice.dispose();
            } else {
                Dialog dialog = null;
                dialog = new Dialog(Dialog.DIALOG_WARNING, "保存容量");
                dialog.setText("通信中にエラーが発生しました。\n電波状況の良いところで使用ください。\n");
                dialog.show();
            }

            // 画面を戻す
            this.show();
        }

        /**
         * 画面を表示
         */
        public void show() {
            Display.setCurrent(this);
        }

        /**
         * 押下されたソフトキーが離された時に呼び出されるメソッドです。.
         * <br>
         * このメソッドは、<code>setSoftKeyListener()</code>を使って
         * ソフトキーリスナーを登録している場合に有効となります。
         * @param key 離されたキー
         */
        private void softKeyReleased(final int key) {

            // 終了キー押下
            if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() == 0) {
                if (ScratchPad.length() > 0) {
                    Dialog dialog = new Dialog(Dialog.DIALOG_YESNO, "終了確認");
                    dialog.setText("未送信データがあります。\n終了しますか？\n\n");
                    if (Dialog.BUTTON_YES == dialog.show()) {
                        // アプリ終了
                        IApplication.getCurrentApp().terminate();
                    }
                } else {
                    // アプリ終了
                    IApplication.getCurrentApp().terminate();
                }

            // 撮影キー押下
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() == 0) {
                // 動画・静止画を判定
                if (this.mode == 0) {
                    CameraDevice.takePicture(this.quality);
                } else {
                    CameraDevice.selectPicture();
                }

            // 取消キー押下
            } else if (Display.KEY_SOFT1 == key && CameraDevice.getNumberOfImages() != 0) {
                // 残っているデータを削除
                CameraDevice.dispose();

            // 送信キー押下
            } else if (Display.KEY_SOFT2 == key && CameraDevice.getNumberOfImages() != 0) {
                // データ送信
                this.send();
            }
        }
    }
}
