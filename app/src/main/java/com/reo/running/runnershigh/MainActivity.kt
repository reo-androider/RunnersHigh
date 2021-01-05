package com.reo.running.runnershigh

/**
 * 処理概要具体化
 * フラグメントで、上部画面にタイトルバー
 * 下部画面に操作画面（ホーム画面、写真投稿、検索、プロフィール（高さ）画像はキャラクターを選択できると良いね）
 * firebase 認証
 * firebase DB
 * 位置情報取得
 * 移動距離取得
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}