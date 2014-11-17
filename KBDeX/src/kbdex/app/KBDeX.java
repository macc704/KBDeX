/*
 * KBDeX.java
 * Created on Jun 27, 2010 
 * Copyright(c) 2010 Yoshiaki Matsuzawa, Shizuoka University. All rights reserved.
 */

package kbdex.app;

import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import kbdex.app.manager.KDDiscourseManager;
import kbdex.app.manager.KDiscourseManagerFrame;
import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CFileSystem;
import clib.common.system.CEncoding;
import clib.common.system.CJavaSystem;
import clib.view.dialogs.CErrorDialog;
import clib.view.progress.CPanelProcessingMonitor;

/**
 * KBDeX(Knowledge Building Discourse eXplorer) Application
 * Copyright(c) 2010-2011 Yoshiaki Matsuzawa, Jun Oshima, Ritsuko Oshima at Shizuoka University. All rights reserved.
 * 
 * <バグ>
 * TODO 100 戻るときに，ボタン連打すると，計算途中で戻るが反応してしまい，結果，たくさん戻ってしまうように見える．
 * 
 * ＜懸案事項＞
 * TODO 10 Hide機能のテスト(リファクタリング後できていなかったものをできるようにしたので)
 * TODO 10 Graph表示で，タイトルを変えられるようにしたい（何が何だかわからなくなる）
 * TODO 10 Graph表示, Table表示からのcsv, xls吐出機能，（R，Gnuplotもイイネ！）
 * 
 * ＜更新履歴＞
 * 1.9.4 2014.10.10
 * 		・fixed Turkish problem -> utf8
 * 		・fixed bug of lifetime
 * 
 * 1.9.2 2014.09.23
 * 		・Word Bag for frequency
 * 		 (plus mark to create a word bag for frequency)
 * 		・ignore word list
 * 		 (minus mark to suppress detecting the particular word in word selection)
 * 
 * 1.9.1 2014.09.19
 * 		・Bugfix for WordBag
 * 		・copy pasting for mac java7
 * 
 * 1.9.0 2014.09.19
 * 		・WordBag
 * 		(exclamation mark to make a word bag)
 * 
 * 1.8.2 2014.08.28
 * 		・Fixed bug of output-R 
 * 
 * 1.8.1 2014.08.20
 * 		・CFileがBOMをつけないように，かつBOMを読み飛ばすように変更
 * 		・辞書なしのエラー処理
 * 
 * 1.8.0 2014.08.20
 * 		・GPL License
 * 		・Launch
 * 		・non-japanese version
 * 
 * 1.7.2 2014.08.20
 * 		・HongKongの人の要請で，UTF-8のテキストを扱えるようにする（DefaultをUTF-8，読み込み自動判定にした）
 * 		・WordSelectionでDirtyStateの検知，表示，確認．
 * 		・WordSelection他のDialogSizeを変更 (screenの3/4)
 * 
 * 1.7.1 2014.07.18
 *		・KF5, ファイル名，キャンセルなど細かいミスなどの修正． 		
 * 
 * 1.7.0 2014.07.17
 * 		・KF5に対応
 * 		
 * 1.6.1 2013.07.13
 * 		・Java1.7以上でない場合にエラーダイアログを表示する．
 * 
 * 1.6.0 2013.06.19
 * 		・大島先生のご要望によりノードアイコン追加
 * 
 * 1.5.8 2012.11.09
 * 		・バグ修正　KDiscourseViewerPanelでTextがソートされない
 * 		・公開にあたり，付属の例を修正
 * 
 * 1.5.7 2012.10.26
 * 		・Monicaのバグ報告により修正
 * 		・KFから取得時に，時間をModify時でとっていた=>しかしTimeFilterは最初と最後のノートの時刻で判断=>最後のノート以降に更新されたノートが範囲外
 * 		・対策（１），Crea時間にした
 * 		・対策（２），Defaultの時間範囲を，全てのノートを参照して計算するようにした（多少冗長であるが）
 * 
 * 1.5.6 2012.10.04
 * 		・Bodongの要求により viewを指定してKFを読み込めるようにする
 * 		・CommonLibraryを新しいバージョンに入替
 * 
 *　1.5.5 2012.03.19
 *		・BSLボタン グラフレイアウト機能　指標をDegree Centralityの総和に変更
 *
 *　1.5.4 2012.02.03
 *		・BSLボタン グラフレイアウト機能　指標のactive状態をリセットするように対応．
 * 
 * 1.5.3 2011.12.02
 * 		・BSLボタン グラフレイアウト機能 (途中だがCLE研究会で発表)
 * 
 * 1.5.2 2011.11.28　
 * 		・Fontを変えられるようにtemporaryの修正
 * 
 * 1.5.1 2011.11.27　学マネKBDeX実験(2)投入
 * 		・TableMetricsでSSがとれなかったバグを修正
 * 		・TableMetrics表示時に最新の指標が反映できていなかったバグを修正
 * 		・Reloadボタンの追加
 * 
 * 1.5.0 2011.11.27　学マネKBDeX実験(2)投入 RC
 * 		・時系列メトリクス計算方針の大幅変更
 * 			・全部キャッシュし，グラフには基本的に全部表示する．
 * 			・現在の位置をインジケータで示す．
 * 			・そこで選んだものだけでなく、シリーズで出てくるすべてのノードを対象とする
 * 		・メトリクス作成インタフェイスの大幅変更
 * 			・Graph, Nodeが別れていてわかりにくかったので統一．
 * 			・Graph, Tableを作るインタフェイスをActivationと統一し，かつ直感的に分かりやすいように変更
 * 
 * 1.4.0 2011.11.14
 * 		・アニメーション速度調整機能の追加
 * 
 * 1.3.9 2011.11.13 学マネKBDeX実験(1)投入
 * 		・メイン画面全体のスクリーンキャプチャ機能
 * 		・Noスキップ機能UI調整
 * 		
 * 1.3.8 2011.11.13
 * 		・KaniChatCSVの複数同時読み込み機能を追加
 * 		・スクリーンキャプチャ機能，currentIDがnullの時の不具合を修正
 * 
 * 1.3.7 2011.11.12
 * 		・文字コード周りの整理
 * 			・data.csv形式の読込み，書出しの文字コードをShift_JISに固定
 * 		・Import機能の拡充
 * 			・KaniChatCSV形式の読み込み機能を追加
 * 			・KF読込も含めて，読み込みまわりのリファクタリングを実施
 * 			・CSVをcommonsライブラリで読み込むように変更した関係で，CSVの仕様が若干変わった．(改行が入っても良い，下位互換性あり)
 * 			・読込んだときのデフォルトlifetimeを20から無しに変更 
 * 		・スクリーンキャプチャ機能の追加
 * 
 * 1.3.6 2011.08.02
 * 		・DiscourseViewerにNo追加
 * 		・DiscourseコントローラにNo指定を追加
 * 		・RelationパネルでUnitダブルクリックでUnit内容表示ウインドウを表示するように
 * 
 * 1.3.5 2011.07.24
 * 		・選ぶ単語に重複箇所（ex 自動車，車）単語ハイライト表示部（label）のアサーションに引っかかり，
 * 			正常動作しなくなる問題を修正
 * 
 * 1.3.4 2011.05.07
 * 		・toLast時にGraphが途中描画されないように調整
 * 		・選択したものが，resetすると消去されてしまう(Tableの選択が解除され，そこで選択がクリアされてしまう）問題をFix
 * 
 * 1.3.3 2011.05.06
 *		・Graphの調整
 *			・グラフのラベル選択をできるようにする
 *			・グラフを直接選択できるようにする
 * 			・グラフの設定画面のレイアウト変更し，少なくても多くても散らばることなく表示される．
 * 		・Graph表示での選択機能に対応．（Graph->Network Network->Graph）両対応
 *		・Table表示での選択機能に対応．（新しくNetwork->Tableに対応）
 * 		・英語版サンプルを追加
 * 
 * 1.3.2 2011.05.06
 * 		・時間がかかる処理での進捗表示，およびキャンセル機能を追加（2.5秒後に出る設定）
 * 
 * 1.3.1 2011.05.05
 *		・（解決）Graph表示で，NaNの表示がおかしい．
 *		・（解決）この先，structureChanged()でソート条件がリセットされてしまう．（暫定的に解決）
 *		・（解決）リセット時，グラフが以前のバージョンの最後の値で初期化される
 *		・（解決） Graph表示で，Explanationが見にくい．→Overviewを分離
 * 		・（解決） Graph表示で，そのものを残しておきたい（コピーしたい．）→コピー，Pinをできるようにした
 * 		・Graph->Table表示をできるようにした．
 * 		・Table->Graph表示をできるようにした．
 * 		・Activeの状態を変えられるようにした(現在の設計上Chartのみ）
 * 		・Overview, Export JPGを分離し，ファイルメニューとした
 * 		・TableにselectAllメニューをつけた（便利）
 * 
 * 1.3.0 2011.05.05
 * 		・（内部リファクタリング終了）
 * 		・全体的に内部デザイン一新
 * 			・InternalFrame版
 * 			・高速化
 * 				・プロファイリングに基づくボトルネック部分の高速化．（hashcodeのキャッシングなどで解決）
 * 				・描画タイミングの整理により，不必要なところでの描画をしないようにした
 * 				・メトリクス計算アーキテクチャの整理により，不必要なところで計算しない，かつ一度計算したらキャッシングする
 * 			・MVCアーキテクチャの一段階明確化
 * 				・Viewがいくつでも作れるようになった．
 * 				・選択をModelにし，メトリクス画面でも選択が可能になった
 * 			・メトリクスのModel化とLazyチェーンアーキテクチャ
 * 				・選択したものだけ計算し，かつ重複計算しない
 * 				・Rを使用して，メトリクスの正確さを検証し直した（invalidにしたときの値の検証は未定）
 * 			・言語プロセッサアーキテクチャ
 * 				・言語が切り替え可能
 *  		・TimeFilter画面の一新
 *  			・拡大，縮小が可能
 *  			・TimeFilterの入れ替えなど，高機能にし，かつ操作の簡便化
 * 		・新機能
 * 			・中心化傾向などGraphのメトリクスに対応
 * 			・新メトリクス対応（クラスター係数など）
 * 			・グラフ表示の便利機能（選択しているもの，およびそのメトリクス列をすべてグラフにする）
 * 			・関係Modelを追加．ダブルクリックで，その詳細が見られる＋weightメトリクスに今後対応可能
 * 			・英語版，日本語版切り替え
 * 			・Discourse Unit単位切り替え
 * 			・生存期間（試し機能）
 * 			・RへのExport機能
 * 1.2.1 2011.04.13
 * 		・WordSelectionViewで ResizeWeightがおかしい→解決しました
 *　1.2.0 2011.04.12
 *		・（ リファクタリングの半分が終わった所） 
 * 		・数値整列問題はNewTableSorterを使えば解決するらしい ？-> Integerを返すように再設計し解決した
 * 		・HTML表示が遅すぎる ->ボトルネックはJTable->cashで解決した 	
 *		・指標でN/Aにすべき所を0にしている（BCの離れ小島など）→対応しました
 * 		・Nの値，NodeがValidかどうかを計算に入れること（BCはOK, 近接中心は未）→対応しました	
 */
public class KBDeX {

	private static final String VERSION = "1.9.4";
	private static final String DATE = "2014.10.10";
	private static final String TITLE = "KBDeX Version " + VERSION
			+ " (build on " + DATE + ")";
	private static final String DATA_DIR_NAME = "data";
	public static CEncoding ENCODING_OUT = CEncoding.UTF8;
	public static final boolean DEBUG = true;

	private static KBDeX instance;

	private KDDiscourseManager dManager;
	private CPanelProcessingMonitor monitor;

	public static final KBDeX getInstance() {
		if (instance == null) {
			instance = new KBDeX();
		}
		return instance;
	}

	private KBDeX() {
	}

	public void run() throws Exception {
		checkJavaVersion();
		initializeEncoding();
		initializeUI();
		dManager = new KDDiscourseManager(getDataDir());
		KDiscourseManagerFrame frame = new KDiscourseManagerFrame(dManager,
				KBDeX.TITLE);
		frame.openWindowInDefaultSize();
	}

	private void initializeEncoding() {
		if (CJavaSystem.getInstance().isJapaneseOS()) {
			ENCODING_OUT = CEncoding.Shift_JIS;
		}
	}

	public void checkJavaVersion() {
		double version = CJavaSystem.getInstance().getVersion();
		if (version < 1.7) {
			JOptionPane.showMessageDialog(null,
					"KBDeX requires Java 1.7+ but your Java version is "
							+ version);
			System.exit(0);
		}
	}

	public void initializeUI() {
		try {
			if (CJavaSystem.getInstance().isMac()) {
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty(
						"com.apple.mrj.application.apple.menu.about.name",
						"KBDeX");
			}
			// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
	}

	public KDDiscourseManager getDiscourseManager() {
		return dManager;
	}

	@Deprecated
	public CDirectory getDataDir() {
		return CFileSystem.getExecuteDirectory().findOrCreateDirectory(
				DATA_DIR_NAME);
	}

	public Image getIconImage32() {
		return getIconImage("kbdex32x32.png");
	}

	public Image getIconImage16() {
		return getIconImage("kbdex16x16.png");
	}

	public ImageIcon getImageIcon(String name) {
		return new ImageIcon(getIconImage(name));
	}

	public Image getIconImage(String name) {
		return getImage("icons/" + name);
	}

	public Image getImage(String path) {
		try {
			URL url = KBDeX.class.getResource(path);
			BufferedImage image = ImageIO.read(url);
			return image;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
		}
	}

	public CPanelProcessingMonitor getMonitor() {
		if (monitor == null) {
			JFrame frame = new JFrame();
			frame.setIconImage(getIconImage16());
			monitor = new CPanelProcessingMonitor(frame, false);
		}
		return monitor;
	}

	public void handleException(Frame owner, Exception ex) {
		if (DEBUG) {
			ex.printStackTrace();
		}
		CErrorDialog.show(owner, "ERROR", ex);
	}
}
