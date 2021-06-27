import java.io.InputStream;

import com.nttdocomo.device.Camera;
import com.nttdocomo.system.ImageStore;
import com.nttdocomo.system.MovieStore;
import com.nttdocomo.ui.Dialog;
import com.nttdocomo.ui.Image;
import com.nttdocomo.ui.MediaImage;

/**
 * Camera<BR>
 * このアプリケーションで使用するカメラを定義するクラスです。
 * <p>
 * @version 1.0
 * @author takaya
 * </p>
 */
public final class CameraDevice {

	/**
     * <code>camera</code> カメラ
     */
    private static Camera camera = null;

	/**
     * <code>image</code> 選択画像
     */
    private static ImageStore image = null;

	/**
     * <code>movie</code> 選択動画
     */
    private static MovieStore movie = null;

	/**
     * <code>type</code> 画像フラグ
     */
    private static int type = 0;

	/**
     * <code>dialog</code> エラーダイアログ
     */
    private static Dialog dialog = new Dialog(Dialog.DIALOG_ERROR, "カメラ障害");

    /**
     * <code>PICTURE</code> 画像
     * <code>MOVIE  </code> 動画
     * <code>PHOTO  </code> 画像ファイル
     * <code>VIDEO  </code> 動画ファイル
     */
    public static final int PICTURE = 0,
	                           MOVIE   = 1,
							   PHOTO   = 2,
	                           VIDEO   = 3;
    /**
     * <code>PIC_SIZE</code> 画像サイズ
     * <code>MOV_SIZE</code> 動画サイズ
     */
    public static final int[][] PIC_SIZE, MOV_SIZE;

    /**
     * <code>pictuerSize </code> 画像サイズ数
     * <code>movieSize   </code> 動画サイズ数
     */
    private static int pictuerSize = 0,
                         movieSize   = 0;

    // カメラオブジェクトの初期設定
    static {

        // カメラオブジェクトの取得
        camera = Camera.getCamera(0);

        // 連射機能
        if (camera.isAvailable(Camera.DEV_CONTINUOUS_SHOT)) {
            // 連写機能を抑止
            camera.setAttribute(Camera.DEV_CONTINUOUS_SHOT, Camera.ATTR_CONTINUOUS_SHOT_OFF);
        }

        // 高画質に設定可能なら
        if (camera.isAvailable(Camera.DEV_QUALITY)) {
            // 高画質設定
            camera.setAttribute(Camera.DEV_QUALITY, Camera.ATTR_QUALITY_HIGH);
        }

        // フレーム設定機能ありの時
        if (camera.isAvailable(Camera.DEV_FRAME_SHOT)) {
            // フレーム撮影不可
            camera.setAttribute(Camera.DEV_FRAME_SHOT, Camera.ATTR_FRAME_OFF);
        }

        // サウンド設定
        if (camera.isAvailable(Camera.DEV_SOUND)) {
            camera.setAttribute(Camera.DEV_SOUND, Camera.ATTR_VOLUME_MAX);
        }

        // カメラサイズを取得してソートテーブルへ格納
        int [][] temp = camera.getAvailablePictureSizes();
        PIC_SIZE = sort(temp);
        temp = camera.getAvailableMovieSizes();
        MOV_SIZE = sort(temp);
    }

    /**
     * カメラサイズをソートする
     * @param size カメラサイズ
     * @return ソート結果
     */
    private static int[][] sort(final int[][] size) {
    	int[][] result = new int[size.length][2];
    	for (int i = 0, max = 0; i < size.length; i++) {
    	    for (int j = 0; j < size.length; j++) {
                if (result[i][0] * result[i][1] < size[j][0] * size[j][1]) {
                	result[i][0] = size[j][0];
                	result[i][1] = size[j][1];
                	max = j;
                }
    	    }
        	size[max][0] = 0;
        	size[max][1] = 0;
    	}
		return result;
    }

    /**
     * イメージを削除
     */
    public static void dispose() {

    	// 選択画像初期化
        image = null;
        movie = null;

        // 撮影された場合
        if (camera.getNumberOfImages() > 0) {
            camera.disposeImages();
        }
    }

    /**
     * カメラにあるデータを取り出して返却する
     * @return カメラから取得したデータ
     */
    public static InputStream getCameraData() {
        try {
            MediaImage mediaImage = null;

            // 保存してある画像の場合
        	if (type == PHOTO) {
	            // 撮影されない場合
	            if (image == null) {
	                return null;
	            }

	            // 端末に保持されている画像を取得
	            return image.getInputStream();
        	} else if (type == VIDEO) {
                // 撮影されない場合
                if (movie == null) {
                    return null;
                }

                // 端末に保持されている動画を取得
                return movie.getInputStream();
    	    }

	        // 撮影されない場合
	        if (camera.getNumberOfImages() == 0) {
	            return null;
	        }

            // カメラに保持されている画像を取得
            return camera.getInputStream(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * カメラにあるデータを取り出して返却する
     * @return カメラから取得したデータ
     */
    public static Image getCameraImage() {
        try {
            MediaImage mediaImage = null;

            // 保存してある画像の場合
        	if (type == PHOTO) {
	            // 撮影されない場合
	            if (image == null) {
	                return null;
	            }

	            // 端末に保持されている画像を取得
	            mediaImage = image.getImage();
        	} else if (type == VIDEO) {
	            // 撮影されない場合
	            if (movie == null) {
	                return null;
	            }

	            // 端末に保持されている動画を取得
	            mediaImage = movie.getImage();
        	} else {
	            // 撮影されない場合
	            if (camera.getNumberOfImages() == 0) {
	                return null;
	            }

	            // カメラに保持されている画像を取得
	            mediaImage = camera.getImage(0);
        	}

            mediaImage.use();
            return mediaImage.getImage();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * カメラにあるデータを取り出して返却する
     * @return カメラから取得したデータ
     */
    public static MediaImage getMediaImage() {
        try {
            MediaImage mediaImage = null;

            // 保存してある画像の場合
        	if (type == PHOTO) {
	            // 撮影されない場合
	            if (image == null) {
	                return null;
	            }

	            // 端末に保持されている動画を取得
	            mediaImage = image.getImage();
        	} else if (type == VIDEO) {
	            // 撮影されない場合
	            if (movie == null) {
	                return null;
	            }

	            // 端末に保持されている動画を取得
	            mediaImage = movie.getImage();
        	} else {
	            // 撮影されない場合
	            if (camera.getNumberOfImages() == 0) {
	                return null;
	            }

	            // カメラに保持されている画像を取得
	            mediaImage = camera.getImage(0);
        	}

            mediaImage.use();
            return mediaImage;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * イメージ数を調べる
     * @return 画像の数
     */
    public static int getNumberOfImages() {

    	// 保存してある画像の場合
    	if (type == PHOTO) {
            // 撮影されない場合
            if (image == null) {
                return 0;
            }

            return 1;
    	} else if (type == VIDEO) {
            // 撮影されない場合
            if (movie == null) {
                return 0;
            }

            return 1;
    	}
        return camera.getNumberOfImages();
    }

    /**
     * 動画を撮ってメディアイメージを格納
     * @return 実行結果
     */
    public static boolean takeMovie() {
        return takeMovie(0);
    }

    /**
     * 動画を撮ってメディアイメージを格納
     * @param quality 画質
     * @return 実行結果
     */
    public static boolean takeMovie(final int quality) {

        try {
        	// エラーチェック
        	if (MOV_SIZE.length < quality) {
                return false;
        	}

            // 指定された画質を設定する
            camera.setImageSize(MOV_SIZE[quality][0], MOV_SIZE[quality][1]);

            // 写真を撮る
            camera.takeMovie();

            // 撮影されない場合
            if (camera.getNumberOfImages() == 0) {
                return false;
            }

            type = MOVIE;
            return true;
        } catch (Exception e) {
           	dialog.setText("現在、動画撮影ができない状態です。\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * 写真を撮ってメディアイメージを格納
     * @return 実行結果
     */
    public static boolean takePicture() {
        return takePicture(0);
    }

    /**
     * 写真を撮ってメディアイメージを格納
     * @param quality 画質
     * @return 実行結果
     */
    public static boolean takePicture(final int quality) {

        try {

        	// エラーチェック
        	if (PIC_SIZE.length < quality) {
                return false;
        	}

            // 指定された画質を設定する
            camera.setImageSize(PIC_SIZE[quality][0], PIC_SIZE[quality][1]);

            // 写真を撮る
            camera.takePicture();

            // 撮影されない場合
            if (camera.getNumberOfImages() == 0) {
                return false;
            }

            type = PICTURE;
            return true;
        } catch (Exception e) {
           	dialog.setText("現在、写真撮影ができない状態です。\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * 写真を選択メディアイメージを格納
     * @return 実行結果
     */
    public static boolean selectPicture() {

        try {
        	// ファイルから読み込み
        	image = ImageStore.selectEntry();
            type = PHOTO;
            return true;
        } catch (Exception e) {
           	dialog.setText("現在、写真選択ができない状態です。\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * 動画を選択メディアイメージを格納
     * @return 実行結果
     */
    public static boolean selectMovie() {

        try {
        	// ファイルから読み込み
		    movie = MovieStore.selectEntry();
            type = VIDEO;
            return true;
        } catch (Exception e) {
           	dialog.setText("現在、動画選択ができない状態です。\n");
           	dialog.show();
            return false;
        }
    }

    /**
     * コンストラクタ
     */
    private CameraDevice() {
    }
}
