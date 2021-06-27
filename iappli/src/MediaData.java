import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * イメージデータ格納用クラス
 */
public final class MediaData {

    /**
     * <code>comment</code> コメント
     */
    private String comment;

    /**
     * <code>data</code> イメージデータ
     */
    private byte[] data;

    /**
     * <code>type</code> タイプ
     */
    private String type;

    /**
     * コンストラクタ
     * @param in イメージデータ入力ストリーム
     * @param textSize テキストのサイズ
     * @param dataSize メディアデータのサイズ
     * @param tp タイプ
     */
    public MediaData(final InputStream in, final int textSize, final int dataSize, final String tp) {
        try {
            this.type = tp;
            byte[] temp = new byte[textSize];
            this.data = new byte[dataSize];

            // コメント読み込む
            in.read(temp);
            this.comment = new String(temp);

            // ヘッダ読み飛ばし
            in.read();
            in.read();

            // データ読み込み
            in.read(this.data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * コンストラクタ
     * @param in イメージデータ入力ストリーム
     * @param tp タイプ
     * @param text コメント
     */
    public MediaData(final InputStream in, final String tp, final String text) {
        try {
            this.type = tp;
            this.data = new byte[in.available()];
            this.comment = text;
            in.read(this.data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * コメントデータを返却
     * @return コメントデータ
     */
    public byte[] getComment() {
        return this.comment.getBytes();
    }

    /**
     * レングスを返却
     * @return レングス
     */
    public int length() {
        return this.data.length;
    }

    /**
     * コメントを返却
     * @return コメント
     */
    public String getText() {
        return this.comment;
    }

    /**
     * イメージデータを返却
     * @return イメージデータ
     */
    public byte[] toByteArray() {
        return this.data;
    }

    /**
     * イメージデータを返却
     * @return イメージデータ
     */
    public InputStream toInputStream() {
        return new ByteArrayInputStream(this.data);
    }

    /**
     * タイプを返却
     * @return タイプ
     */
    public String getType() {
        return this.type;
    }
}
