import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.microedition.io.Connector;

import com.nttdocomo.io.HttpConnection;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.IApplication;

/**
 * MediaCollection <BR>
 * このアプリケーションで使用するメディアデータを管理するクラスです。
 * @version 1.0
 */
public final class ScratchPad {

    /**
     * <code>bar</code> プログレスバー
     */
    private static Progress bar = new Progress(false);

    /**
     * <code>COMMENT</code> コメント
     */
    private static final int COMMENT = 500;

    /**
     * <code>HEAD</code> ヘッダサイズ
     */
    private static final int HEAD = 2;

    /**
     * <code>MAX</code> 最大値
     */
    public static final int MAX = 200000;

    /**
     * <code>mediaLength</code> スクラッチパッドの保存サイズ
     */
    private static int mediaLength = 0;

    /**
     * <code>mediaIndex</code> スクラッチパッドのオフセット
     */
    private static int mediaIndex = 1;

    /**
     * <code>URL</code> 使用リソース一覧
     */
    private static final String[] URL = {
		"image/bg_main.gif",
		"image/bg_percent.gif",
		"image/bg.gif"
    };

    /**
     * <code>resource</code> リソースデータ
     */
    private static Vector resource = new Vector();

    /**
     * <code>mediaData</code> メディアデータ
     */
    private static Vector mediaData = new Vector();

    /**
     * <code>SP</code> スクラッチパッド識別子
     */
    private static final String SP = "scratchpad:///0";

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
     * イメージオブジェクトを保存します。
     * @param in  画像データ読み込みオブジェクト
     * @param type オブジェクトタイプ
     * @param comment コメント
     * @return 処理結果
     */
    public static boolean addImage(final InputStream in, final String type, final String comment) {
    	MediaData md = new MediaData(in, type, comment);
    	mediaLength += md.length();
    	mediaData.addElement(md);

    	if (mediaLength > MAX) {
    		return false;
    	}

		return save();
    }

    /**
     * イメージオブジェクトを保存します。
     * @param md  画像データ
     * @return 処理結果
     */
    public static boolean addImage(final MediaData md) {
    	mediaLength += md.length();
    	mediaData.addElement(md);

    	if (mediaLength > MAX) {
    		return false;
    	}

		return save();
    }

    /**
     * スクラッチパッドからメディアデータを読み込み、MediaDataを作成します。
     * @param offset オフセット
     * @param textSize テキストのサイズ
     * @param dataSize メディアデータのサイズ
     * @param type タイプ
     * @return MediaData 返却されるメディアデータはuse()されています。
     */
    private static MediaData createMediaData(
    	final int offset, final int textSize, final int dataSize, final String type) {
        MediaData md = null;

        try {
        	md = new MediaData(Connector.openInputStream(SP + ";pos=" + offset), textSize, dataSize, type);
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, e.getClass().getName());
            dialog.setText("メディアデータの作成に失敗しました。\n" + e.getMessage());
            dialog.show();
        }
        return md;
    }

    /**
     * メディアデータが何枚保存されているか返却する。 <br>
     * クラッチパッドの１バイト目を読み込み、設定されている場合は それを返却します。
     * @return メディア数を返却
     */
    private static int getCount() {
        InputStream in = null;
        int count = 0;

        try {
            in = Connector.openDataInputStream(SP);
            count = in.read();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, "障害情報");
            dialog.setText("スクラッチパッドの読み込みに失敗しました。");
            dialog.show();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return count;
    }

    /**
     * イメージオブジェクトを取得します。
     * @return イメージオブジェクト
     */
    public static MediaData getMediaData() {
        return (MediaData) mediaData.firstElement();
    }

    /**
     * イメージオブジェクトを取得します。
     * @param index イメージインデックス
     * @return イメージオブジェクト
     */
    public static MediaData getMediaData(final int index) {
        if (index > mediaData.size()) {
            return null;
        }
        return (MediaData) mediaData.elementAt(index);
    }

    /**
     * イメージオブジェクトを取得します。
     * @param index イメージインデックス
     * @return イメージオブジェクト
     */
    public static MediaData getResource(final int index) {
        if (index > resource.size()) {
            return null;
        }
        return (MediaData) resource.elementAt(index);
    }

    /**
     * スクラッチパッドからイメージデータのサイズを読み込みます。
     * @param offset オフセット
     * @return イメージのサイズ
     */
    private static int getMediaSize(final int offset) {
        InputStream in = null;
        int length = 0;

        try {
            in = Connector.openInputStream(SP + ";pos=" + offset);
            length |= (in.read() << 8);
            length |= in.read();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, e.getClass().getName());
            dialog.setText("メディアサイズの取得に失敗しました。\n" + e.getMessage());
            dialog.show();
            length = 0;
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return length;
    }

    /**
     * スクラッチパッドからイメージデータのサイズを読み込みます。
     * @param offset オフセット
     * @return イメージのサイズ
     */
    private static int getMediaType(final int offset) {
        InputStream in = null;
        int length = 0;

        try {
            in = Connector.openInputStream(SP + ";pos=" + offset);
            length = in.read();
        } catch (Exception e) {
            Dialog dialog = new Dialog(Dialog.DIALOG_INFO, e.getClass().getName());
            dialog.setText("メディアサイズの取得に失敗しました。\n" + e.getMessage());
            dialog.show();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return length;
    }

    /**
     * サーバから URL で指定されたファイルをダウンロードします。
     * @param file ダウンロードするファイルのファイル
     * @return ダウンロードされたデータ
     *         <br>正常にダウンロードできない場合、nullが返却されます。
     */
    private static MediaData getServerResource(final String file) {
        HttpConnection http = null;
        MediaData md = null;

        // データのダウンロード
        try {
            int contentLength = 0;
            String url = IApplication.getCurrentApp().getSourceURL() + file;
            String type = file.substring(file.length() - 3);
            http = (HttpConnection) Connector.open(url, Connector.READ);
            http.setRequestMethod(HttpConnection.GET);
            http.connect();
        	md = new MediaData(http.openInputStream(), file, type);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (null != http) {
                    http.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return md;
    }

    /**
     * オブジェクトの数を返します。
     * @return オブジェクト数
     */
    public static int length() {
    	return mediaData.size();
    }

    /**
     * スクラッチパッドからイメージをロードします。
     * @return 読み込みが完了した場合、true <br>
     *          読み込みに失敗した場合、false
     */
    public static boolean load() {
        int length = getCount();
        int textSize = 0;
        int dataSize = 0;
        int offset = 1;
        String type = null;

        // リソースの準備ができてない場合
        if (length == 0) {
            bar.reset(URL.length);
            bar.setMessage("ダウンロードしています。");
            bar.show();

            for (int i = 0; i < URL.length; i++) {
                // ファイルのダウンロード
            	MediaData md = getServerResource(URL[i]);
                if (null == md) {
                	System.out.println("ダウンロードファイル異常");
                    return false;
                }

            	byte[] data = md.toByteArray();
            	byte[] text = md.getComment();

                // 容量オーバーの場合
            	if (mediaIndex + 1 + HEAD + text.length + HEAD + data.length > MAX) {
                    return false;
            	}

            	// スクラッチパッドに保存
                if (!save(text, data, mediaIndex, md.getType())) {
                	System.out.println("スクラッチパッド保存失敗");
                    return false;
                }

                // リソース保存
                resource.addElement(md);

                // スクラッチパッドへの書き込み位置を
                // 種別 + ヘッダ + コメント + ヘッダ + データ分シフトする。
                mediaIndex += 1 + HEAD + text.length + HEAD + data.length;
                bar.add();
            }

            // スクラッチパッドの１バイト目のサイズを更新する
            if (!setSize(URL.length)) {
            	System.out.println("スクラッチパッドサイズ保存失敗");
                return false;
            }
        }

        // プログレスバーの準備
        bar.reset(length);
        bar.setMessage("データを読み込んでいます。");
        bar.show();

        for (int i = 0; i < length; i++) {

        	// タイプを取得
        	if (getMediaType(offset++) == 0) {
        		type = TYPE_PICTURE;
        	} else {
        		type = TYPE_MOVIE;
        	}

            // スクラッチパッドからテキストサイズを取得
        	textSize = getMediaSize(offset);
            if (0 > textSize) {
            	System.out.println("スクラッチパッド読み出し失敗");
                return false;
            }

            // スクラッチパッドからイメージサイズを取得
            dataSize = getMediaSize(offset + HEAD + textSize);
            if (0 >= dataSize) {
            	System.out.println("スクラッチパッド読み出し失敗");
                return false;
            }

            // スクラッチパッドからメディアイメージを作成
            MediaData md = createMediaData(HEAD + offset, textSize, dataSize, type);

            // スクラッチパッドからの読み込み位置をデータの長さ分シフト
            offset += HEAD + textSize + HEAD + dataSize;

            // リソース判定
            if (i < URL.length) {
            	mediaIndex = offset;
                resource.addElement(md);
            } else {
                mediaLength += md.length();
                mediaData.addElement(md);
            }

            // バー進行
            bar.add();
        }
        return true;
    }

    /**
     * イメージオブジェクトを破棄します。
     * @param index インデックス
     * @return メディアデータの保存が完了した場合、true <br>
     *          メディアデータの保存に失敗した場合、false
     */
    public static boolean remove(final int index) {
        if (index > mediaData.size()) {
            return false;
        }

    	MediaData md = (MediaData) mediaData.elementAt(index);
    	mediaLength -= md.length();
    	mediaData.removeElementAt(index);
		return save();
    }

    /**
     * イメージオブジェクトを破棄します。
     * @param index インデックス
     * @return メディアデータの保存が完了した場合、true <br>
     *          メディアデータの保存に失敗した場合、false
     */
    public static boolean remove(final Object index) {
    	MediaData md = (MediaData) index;
    	mediaLength -= md.length();
    	mediaData.removeElement(index);
		return save();
    }

    /**
     * イメージオブジェクトを破棄します。
     * @return メディアデータの保存が完了した場合、true <br>
     *          メディアデータの保存に失敗した場合、false
     */
    public static boolean removeAll() {
    	mediaLength = 0;
    	mediaData.removeAllElements();
		return save();
    }

    /**
     * メディアデータをスクラッチパッドに保存します。
     * @return メディアデータの保存が完了した場合、true <br>
     *          メディアデータの保存に失敗した場合、false
     */
    public static boolean save() {
    	int index = mediaIndex;

        for (int i = 0; i < mediaData.size(); i++) {
        	MediaData md = (MediaData) mediaData.elementAt(i);
        	byte[] data = md.toByteArray();
        	byte[] text = md.getComment();

            // 容量オーバーの場合
        	if (index + 1 + HEAD + text.length + HEAD + data.length > MAX) {
                return false;
        	}

        	// スクラッチパッドに保存
            if (!save(text, data, index, md.getType())) {
                // スクラッチパッドへの保存失敗
                return false;
            }

            // スクラッチパッドの１バイト目のサイズを更新する
            if (!setSize(i + 1 + URL.length)) {
                // スクラッチパッドへの保存失敗
                return false;
            }

            // スクラッチパッドへの書き込み位置を
            // 種別 + ヘッダ + コメント + ヘッダ + データ分シフトする。
            index += 1 + HEAD + text.length + HEAD + data.length;
        }

        return setSize(mediaData.size() + URL.length);
    }

    /**
     * データをスクラッチパッドに保存します。
     * @param text 保存するコメント
     * @param data 保存するデータ
     * @param offset オフセット
     * @param type コンテンツタイプ
     * @return 保存が完了した場合、true <br>
     *          保存に失敗した場合、false
     */
    private static boolean save(final byte[] text, final byte[] data, final int offset, final String type) {
        OutputStream out = null;
        int length = 0;

        try {
            out = Connector.openOutputStream(SP + ";pos=" + offset);

            // 動画/静止画判定
            if (type.equals(TYPE_PICTURE)) {
                out.write(0 & 0xff);
            } else {
                out.write(1 & 0xff);
            }

            // レングス
            length = text.length;
            out.write((length >>> 8) & 0xff);
            out.write(length & 0xff);

            // コメント書き込み
            out.write(text);

            // レングス
            length = data.length;
            out.write((length >>> 8) & 0xff);
            out.write(length & 0xff);

            // データ書き込み
            out.write(data);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
	        	e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * スクラッチパッド上にメディアデータが数を保持します。
     * @param size サイズ
     * @return 処理結果
     */
    private static boolean setSize(final int size) {
        OutputStream out = null;
        if (size > 255) {
            return false;
        }

        try {
            out = Connector.openOutputStream(SP);
            out.write(size & 0xff);
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * コンストラクタ
     */
    private ScratchPad() {
    }
}