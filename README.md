# MCAAS
這是Mobile Content as a Service(MCAAS)客戶端，伺服器端請從另一個專案下載，但server端等mail註冊更改完後會放上連結

本專案提供mobile to mobile(M2M)的方式傳送多媒體內容，並採用UPnP的方式解決不同網域下NAT問題。
但要注意的是，若行動網路底下的使用者只能接收多媒體體內容，不能提供多媒體內容。
原因在於電信業者不可能再提供手機用戶使用實體位址，再加上電信業者採用的是Carrier-grade NAT(GCN)，屬於兩層NAT以上的結構，UPnP無法解決。
以下將簡易DEMO本專案的內容

初始畫面
====
![image](https://github.com/ggininder5566/MCAAS/raw/master/DEMO/%E7%99%BB%E5%85%A5/%E5%88%9D%E5%A7%8B%E7%95%AB%E9%9D%A2.png) 

註冊
====
取消簡訊註冊，將來會加入mail註冊(因為要簡訊註冊花錢啊!!)

![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E8%A8%BB%E5%86%8A(%E5%B7%B2%E7%84%A1%E7%B0%A1%E8%A8%8A%E8%A8%BB%E5%86%8A)/Inked%E8%A8%BB%E5%86%8A1_LI.jpg?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E8%A8%BB%E5%86%8A(%E5%B7%B2%E7%84%A1%E7%B0%A1%E8%A8%8A%E8%A8%BB%E5%86%8A)/%E8%A8%BB%E5%86%8A2.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E8%A8%BB%E5%86%8A(%E5%B7%B2%E7%84%A1%E7%B0%A1%E8%A8%8A%E8%A8%BB%E5%86%8A)/%E8%A8%BB%E5%86%8A3.png?raw=true)

登入
====
![image](https://github.com/ggininder5566/MCAAS/raw/master/DEMO/%E7%99%BB%E5%85%A5/%E7%99%BB%E5%85%A51.png) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E7%99%BB%E5%85%A5/%E7%99%BB%E5%85%A52.png?raw=true)

發佈內容
====
bob登入後，就選擇一份多媒體內容送給alice

![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E7%99%BC%E4%BD%88/%E7%99%BC%E4%BD%881.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E7%99%BC%E4%BD%88/%E7%99%BC%E4%BD%882.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E7%99%BC%E4%BD%88/%E7%99%BC%E4%BD%883.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E7%99%BC%E4%BD%88/%E7%99%BC%E4%BD%884.png?raw=true)

接收訊息
===
收到bob傳來的訊息，alice向bob索取多媒體內容；

![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E6%8E%A5%E5%8F%97%E8%A8%8A%E6%81%AF/%E8%B3%87%E8%A8%8A%E6%9B%B4%E6%96%B01.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E6%8E%A5%E5%8F%97%E8%A8%8A%E6%81%AF/%E8%B3%87%E8%A8%8A%E6%9B%B4%E6%96%B02.png?raw=true) ![image]

bob可以選擇自己要開放多久的時間供alice進行m2m，畢竟一直對外開放存取手機內容也有安全上的疑慮

(https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E6%8E%A5%E5%8F%97%E8%A8%8A%E6%81%AF/%E8%B3%87%E8%A8%8A%E6%9B%B4%E6%96%B03.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E6%8E%A5%E5%8F%97%E8%A8%8A%E6%81%AF/%E8%B3%87%E8%A8%8A%E6%9B%B4%E6%96%B04.png?raw=true)

下載(含失敗)
===
如果alcie有開啟m2m功能，bob就能直接下載。反之，就必須提醒alcie打開m2m功能。當alice偵測到bob開啟m2m後，會自動下載。

![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E4%B8%8B%E8%BC%89(%E5%90%AB%E5%A4%B1%E6%95%97)/%E4%B8%8B%E8%BC%891.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E4%B8%8B%E8%BC%89(%E5%90%AB%E5%A4%B1%E6%95%97)/%E4%B8%8B%E8%BC%892.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E4%B8%8B%E8%BC%89(%E5%90%AB%E5%A4%B1%E6%95%97)/%E4%B8%8B%E8%BC%893.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS/blob/master/DEMO/%E4%B8%8B%E8%BC%89(%E5%90%AB%E5%A4%B1%E6%95%97)/%E4%B8%8B%E8%BC%894.png?raw=true)

瀏覽
===
alice再次讀取bob傳來的訊息，可以看到多媒體內容以下載完畢

![image](https://github.com/ggininder5566/MCAAS-android-M2M/blob/master/DEMO/%E7%80%8F%E8%A6%BD/%E7%80%8F%E8%A6%BD1.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS-android-M2M/blob/master/DEMO/%E7%80%8F%E8%A6%BD/%E7%80%8F%E8%A6%BD2.png?raw=true) ![image](https://github.com/ggininder5566/MCAAS-android-M2M/blob/master/DEMO/%E7%80%8F%E8%A6%BD/%E7%80%8F%E8%A6%BD3.png?raw=true)
