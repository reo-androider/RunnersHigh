package com.reo.running.runnershigh
/**
 * 目的は？
 * ・とにかく、ランニングを習慣にしている人、あるいはしようとしている人がランニング出来るぞとワクワクするような工夫を凝らしたアプリを作る
 * 工夫とは？
 * ・時間や距離の数値を他に例えてみる、（ここが目玉機能） 距離だったらそれを縦にしたらどこまでいけるのか？計測する
 * →電車だったらどこまで行けるのか？
 * →飛行機だったら？
 * →水中だったら？
 * ・ランニング中にひらめいたアイデアをためる
 * ・ツイートで○○さんは△△まで走りました！という報告ツイート面白そう（オゾン層とかね（笑）エベレスト10往復）
 * ・天気予報も見れるといいかも！
 * ・ランニングに関する改善点を毎日開くたびに教えてくれる AndroidStudio的に
 * ・画面を開くとラジオ体操のスタンプみたいにスタンプラリーのUIが表示される
 * 処理概要具体化
 * 1月中
 * 下部ボタンを押すとUI切り替え
 * firebase 認証
 * firebase DB
 * 位置情報取得
 * 移動距離取得
 */
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        //setupWithNavController(bottom_navigation_test,)
    }
}