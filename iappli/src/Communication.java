import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;

import javax.microedition.io.Connector;

import com.nttdocomo.io.HttpConnection;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.IApplication;
import com.nttdocomo.util.Phone;

/**
 * Communication<BR>
 * このアプリケーションで使用する通信を定義するクラスです。
 * <p>
 * @version 1.0
 * @author takaya
 * </p>
 */
public final class Communication {

    /**
     * <code>bar</code> プログレスバー
     */
    private static Progress bar = new Progress(true);

    /**
     * <code>uniq</code> 発行済み日付
     */
    private static String uniq = "";

    /**
     * 通信サイズと通信ヘッダサイズ
     * DoJa2.0は5Kバイト  mova 504i及び504iSシリーズ
     * DoJa3.0は10Kバイト mova 505i及び505iS、506iシリーズ
     * DoJa3.5は80Kバイト FOMA 900iシリーズ
     * <code>DATE_SIZE    </code> 日付サイズ
     * <code>SIM_ID_SIZE  </code> SIMIDサイズ
     * <code>TERM_ID_SIZE </code> 製造番号サイズ
     * <code>SEQ_NO_SIZE  </code> シーケンスNOサイズ
     * <code>TYPE_SIZE    </code> 識別子サイズ
     * <code>CONTRACT_SIZE</code> 契約IDサイズ
     * <code>LENGTH_SIZE  </code> データレングスサイズ
     * <code>BAR_SIZE     </code> プログレスバーの位置合わせ用
     * <code>MAX_SEND_SIZE</code> 送信バッファサイズ
     */
    public static final int
        DATE_SIZE     =   14,
        SIM_ID_SIZE   =   20,
        TERM_ID_SIZE  =   15,
        SEQ_NO_SIZE   =    5,
        TYPE_SIZE     =    5,
        CONTRACT_SIZE =  256,
        LENGTH_SIZE   =    6,
        BAR_SIZE      =    2,
        MAX_SEND_SIZE = 80000
		              - DATE_SIZE
				      - SIM_ID_SIZE
				      - TERM_ID_SIZE
				      - SEQ_NO_SIZE
				      - TYPE_SIZE
				      - CONTRACT_SIZE
				      - LENGTH_SIZE;

    /**
     * 携帯電話のID情報
     * <code>SIM_ID </code> SIM ID
     * <code>TERM_ID</code> TERM ID
     */
    public static final String
        SIM_ID  = Phone.getProperty(Phone.USER_ID),
        TERM_ID = Phone.getProperty(Phone.TERMINAL_ID);

    /**
     * 現在日付と時間を返す
     * @return 現在日付(YYYYMMDDHHMMSS)
     */
    public static String getSystemDateTime() {
        Calendar calendar = Calendar.getInstance();
        String temp = Long.toString(calendar.get(Calendar.YEAR)  * 10000000000L
                           + (calendar.get(Calendar.MONTH) + 1)  * 100000000
                           + calendar.get(Calendar.DAY_OF_MONTH) * 1000000
                           + calendar.get(Calendar.HOUR_OF_DAY)  * 10000
                           + calendar.get(Calendar.MINUTE)       * 100
                           + calendar.get(Calendar.SECOND));

        // 同じ日付の場合は再取得する
        if (temp.equals(uniq)) {
        	temp = getSystemDateTime();
        }

        return uniq = temp;
    }

    /**
     * 通信用パッディング.
     * 指定レングスに文字列を加工する。
     * レングスの後ろをスペースで埋める。
     * @param data データ
     * @param length 指定レングス
     * @return 処理結果
     */
    public static String padding(final String data, final int length) {

        // nullの場合
        if (data == null) {
            return null;
        }

        // 指定サイズより大きい場合
        if (data.length() > length) {
            return data.substring(length);
        } else if (data.length() == length) {
            return data;
        }

        // 後ろにスペースを埋める
        String temp = data;
        for (int i = data.length(); i < length; i++) {
            temp += " ";
        }

        return temp;
    }

    /**
     * データ送信
     * @param url URL
     * @param data 送信データ
     * @return 処理結果
     */
    public static synchronized boolean send(final String url, final byte[] data) {

        HttpConnection    conn = null;
        HttpConnection    rcon = null;
        OutputStream      out  = null;
        InputStream       in   = null;
        InputStreamReader rec  = null;
        Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "登録エラー");

        try {
            // HTTP接続の準備
            conn = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
            conn.setRequestMethod(HttpConnection.POST);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // データを送信
            out = conn.openOutputStream();
            out.write(data);

        } catch (Exception e) {
           	dialog.setText("HTTP接続例外発生\n");
           	dialog.show();
            return false;
        } finally {
        	try {
            	if (out != null) {
            		out.close();
            	}
        	} catch (Exception e) {
               	dialog.setText("HTTPデータ送信失敗\n");
               	dialog.show();
                return false;
        	}
        }

        try {
            // サーバ接続
            conn.connect();
            if (conn.getResponseCode() != HttpConnection.HTTP_OK) {
                return false;
            }

            // データ受信
            in = conn.openInputStream();
            rec = new InputStreamReader(in);
            StringBuffer temp = new StringBuffer();
            int buffer;
            while ((buffer = rec.read()) != -1) {
                temp.append((char) buffer);
            }

            // 受信結果判定
            if (temp.toString().startsWith("OK")) {
                return true;
            } else {
            	System.out.println("サーバエラー発生:");
            	System.out.println(temp.toString());
            	return false;
            }
        } catch (Exception e) {
           	dialog.setText("通信エラーが発生いたしました\n");
           	dialog.show();
            return false;
        } finally {
        	try {
                // コネクション切断
            	if (in != null) {
            		in.close();
            	}
            	if (conn != null) {
            		conn.close();
            	}
            	if (rec != null) {
            		rec.close();
            	}
        	} catch (Exception e) {
               	dialog.setText("通信切断に失敗しました\n");
               	dialog.show();
                return false;
        	}
        }
    }

    /**
     * データ送信処理
     * @param url URL
     * @param type 識別子
     * @param comment コメント
     * @param data 送信データ
     * @return 処理結果
     */
    public static boolean
        sendData(final String url, final String type, final String comment, final InputStream data) {

        byte[] packet = new byte[MAX_SEND_SIZE];
    	String[] args = IApplication.getCurrentApp().getArgs();
        String header = null;
        String date = getSystemDateTime();
        ByteArrayOutputStream out = null;
        int length = 0;
        int i = 0;

        try {

        	// データが無い場合
        	if (data == null) {
        		return false;
        	}

            // プログレスバーの準備
            bar.reset(data.available() / MAX_SEND_SIZE + BAR_SIZE);
            bar.setMessage("データを送信しています。");
            bar.show();

            // cameraからのデータを送信
            while ((length = data.read(packet)) != -1) {
                header = padding(date,                     DATE_SIZE)
                       + padding(SIM_ID,                   SIM_ID_SIZE)
                       + padding(TERM_ID,                  TERM_ID_SIZE)
                       + padding(Integer.toString(++i),    SEQ_NO_SIZE)
                       + padding(type,                     TYPE_SIZE)
                       + padding(args[0],                  CONTRACT_SIZE)
                       + padding(Integer.toString(length), LENGTH_SIZE);
                byte[] sendHeader = header.getBytes();
                out = new ByteArrayOutputStream(sendHeader.length + length);
                out.write(sendHeader);
                out.write(packet);
                out.close();
                bar.add();

                // データ送信
                if (!send(url, out.toByteArray())) {
                	System.out.println("データ送信失敗");
                    return false;
                }
            }

            // コメントデータ送信
            byte[] sendComment = comment.getBytes();
            length = sendComment.length;

            // コメントデータ送信
            header = padding(date,                     DATE_SIZE)
                   + padding(SIM_ID,                   SIM_ID_SIZE)
                   + padding(TERM_ID,                  TERM_ID_SIZE)
                   + padding("END",                    SEQ_NO_SIZE)
                   + padding(type,                     TYPE_SIZE)
                   + padding(args[0],                  CONTRACT_SIZE)
                   + padding(Integer.toString(length), LENGTH_SIZE);

            // データ編集
            byte[] sendHeader = header.getBytes();
            out = new ByteArrayOutputStream(sendHeader.length + sendComment.length);
            out.write(sendHeader);
            out.write(sendComment);
            out.close();
            bar.add();

            // データ送信
            if (!send(url, out.toByteArray())) {
            	System.out.println("データ送信失敗");
                return false;
            }
        } catch (Exception e) {
        	System.out.println("データ編集で例外発生");
            return false;
        }

        return true;
    }

    /**
     * コンストラクタ.
     * <br>アプリケーションが直接インスタンスを生成することはできません。
     */
    private Communication() {
    }
}
