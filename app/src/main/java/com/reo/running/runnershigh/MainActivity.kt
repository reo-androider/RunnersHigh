package com.reo.running.runnershigh
/**
 * 目的
 * ・とにかく、ランニングを習慣にしている人、あるいはしようとしている人がランニング出来るぞとワクワクするような工夫を凝らしたアプリを作る
 * ・時間や距離の数を何かに例えて成長実感を感じる仕組みを作る
 *
 * 処理概要具体化
 * 1月中
 * 下部ボタンを押すとUI切り替え
 * firebase 認証
 * firebase DBで走った距離と時間を管理
 * 2月中
 * 位置情報取得
 * 移動距離取得
 * 走っている時間タイマーを進める
 */

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(bottom_navigation, navController)
    }
}

/**
 * ViewModel
 * LiveData
 * Navigation Component
 * Hilt
 * ViewPager2
 * Flow
 *
 * アーキテクチャ
 * 責務を分離
 * UI・ビジネスロジック・HTTP通信・DB触る場所
 *
 * MVC
 * MODEL・VIEW・CONTROLLER
 */