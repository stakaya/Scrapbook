import com.nttdocomo.ui.Canvas;
import com.nttdocomo.ui.Display;
import com.nttdocomo.ui.Graphics;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;
import com.nttdocomo.ui.MediaManager;

/**
 * Progress<BR>
 * 状況画面です。
 * <br>
 * 処理状況をプログレスバーに表示します。
 * <p>
 * @version 1.0
 * </p>
 */
public final class Progress extends Canvas {

    /**
     * <code>current</code> プログレスバーの進捗
     */
    private static int current = 0;

    /**
     * <code>maximum</code> プログレスバーの最大値
     */
    private static int maximum = 1;

    /**
     * <code>message</code> メッセージ
     */
    private static String message = "";

    /**
     * <code>progress</code> プログレスバー
     */
    private static Image progress = null;

    /**
     * <code>sending</code> 送信中画像
     */
    private static Image sending = null;

    /**
     * <code>PINK  </code> 表示色「ピンク」を表す定数
     * <code>ORANGE</code> 表示色「オレンジ」を表す定数
     * <code>WHITE </code> 表示色「白」を表す定数
     * <code>BLACK </code> 表示色「黒」を表す定数
     * <code>RED   </code> 表示色「赤」を表す定数
     * <code>BLUE  </code> 表示色「青」を表す定数
     */
    public static final int
        PINK   = Graphics.getColorOfRGB(248, 222, 194)   ,
        ORANGE = Graphics.getColorOfRGB(255, 128, 0)     ,
        WHITE  = Graphics.getColorOfName(Graphics.WHITE) ,
        BLACK  = Graphics.getColorOfName(Graphics.BLACK) ,
        RED    = Graphics.getColorOfName(Graphics.RED)   ,
        BLUE   = Graphics.getColorOfName(Graphics.BLUE);

    /**
     * コンストラクタ.
     * @param picture 画像モード
     */
    public Progress(final boolean picture) {

    	if (!picture) {
    		return;
    	}

        try {
        	// プログレスバーの画像を取得
        	MediaData md = ScratchPad.getResource(1);
        	if (md != null) {
                MediaImage mi = MediaManager.getImage(md.toByteArray());
                mi.use();
                progress = mi.getImage();
        	}

        	// 背景がの画像を取得
        	md = ScratchPad.getResource(2);
        	if (md != null) {
                MediaImage mi = MediaManager.getImage(md.toByteArray());
                mi.use();
                sending = mi.getImage();
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * プログレスバーの進捗を進めます。
     */
    public void add() {
        current++;
        this.repaint();
    }

    /**
     * プログレスバーを表示する。
     * @param g グラフィック
     */
    public void paint(final Graphics g) {
        g.lock();
        if (sending != null && progress != null) {
            g.drawImage(sending, 0, 0);
            g.drawImage(progress, 15, 135);
        } else {
            g.setColor(PINK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(BLACK);
        g.drawString(message, 30, 152);
        g.setColor(WHITE);
        g.fillRect(30, 163, 177, 14);
        g.setColor(ORANGE);
        g.fillRect(30, 163, 177 * current / maximum, 14);
        g.setColor(WHITE);
        g.drawString(current * 100 / maximum + "%", 40, 177);
        g.unlock(true);
    }

    /**
     * プログレスバーを初期化します。
     * @param max プログレスバーの最大値
     */
    public void reset(final int max) {

    	if (max > 0) {
            maximum = max;
    	}

        current = 0;
        message = "";
    }

    /**
     * メッセージを設定します。
     * @param msg メッセージ
     */
    public void setMessage(final String msg) {
        message = msg;
    }

	/**
	 * 画面を表示
	 */
	public void show() {
        Display.setCurrent(this);
	}
}
