# RunnersHigh
ほぼ、毎朝走るする習慣があるので、いっそのこと自前のランニングアプリを作ってしまえ！というノリで作りました。  
※アプリのスクショは位置情報の関係東京駅周辺に緯度と経度を合わせました。  
***
![start縮小版](https://user-images.githubusercontent.com/65647834/110730103-3a837900-8263-11eb-9ebd-e65cc8b5afb1.jpg)
![18721syukusyoubann](https://user-images.githubusercontent.com/65647834/110730120-40795a00-8263-11eb-9e49-6947bc0ce231.jpg)
![18711syukusyoubann](https://user-images.githubusercontent.com/65647834/110730309-9817c580-8263-11eb-9e43-499f9cb9c0cc.jpg)
![18709 min](https://user-images.githubusercontent.com/65647834/110730508-e927b980-8263-11eb-9f62-4e60de0a7ed4.jpg)
![18713 minjpg](https://user-images.githubusercontent.com/65647834/110730808-7834d180-8264-11eb-88b8-80a9e622834d.jpg)
![18708min](https://user-images.githubusercontent.com/65647834/110730823-7c60ef00-8264-11eb-9086-f4c74fc2f74b.jpg)
***
# どんなアプリ？
#### ランニングフラグメント(runFragment)
スタートボタンを押すと、カウントダウンが始まり走った距離と時間、消費カロリーを計測できます。  
また、ランニング中に写真を撮ることもできます。
#### リザルトフラグメント(resultFragment)
走り終えた後に、表示されます。  
走った日の日付、距離、消費カロリーが、結果として表示されます。
そして、ROOMにそれらのデータが記録されます。  
写真を撮ったり、自己評価やメモを残すことが出来ます。  
#### グラフフラグメント(graphFragment)
走った距離と、消費カロリーを折れ線グラフで、表示します。
#### プロフィールグラグメント(profileFragment)
プロフィール写真、名前、目標を表示します。  
また、Googleログインをすることで、距離レベルと消費カロリーレベルを表示させることが出来ます。  
これらのレベルの意味は、走行距離と総消費カロリーを何かに例えて、表現します。  
例えば、走った総距離が100kmを越えた時、それを縦にすると宇宙に到達するよ！や、  
総消費カロリーが7200kcalを越えたら、脂肪1kg分だよ！など。
さらに、ランニングフラグメントかリザルト画面で撮った、写真をROOMに記録したデータと共に表示させます。
#### プロフィール設定フラグメント(profileSettingFragment)
プロフィール写真、名前、目標、体重（消費カロリーを正確に測るためのもの）を設定できます。



# 使用したライブラリ
 GoogleMapAPI
 ViewModel
 LiveData
 Coroutines  
 Room  
 Navigation Component  
 Firebase-Auth  
 Firebase-RealTimeDatabase  
 Firebase-Storage
 CardView  
 RecyclerView  
 CameraX  
 MPAndroidChart  
 Git-flow

# 製作期間
1か月と3週間
