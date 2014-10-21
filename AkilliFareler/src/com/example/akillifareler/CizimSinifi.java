package com.example.akillifareler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.ActionMode.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class CizimSinifi extends View implements OnTouchListener {
	
	Main sinif_main;
	CizimSinifi cs;
	Context context;
	List<Point> points = new ArrayList<Point>();
	public int[][] harita = new int[26][15];
	public int[][] orjinalharita = new int[26][15];
	public int[][] gizliharita = new int[26][15];
	Bitmap fareozellik,secilenkapi,aciklama,baslat,duraklat,enkisa;
	Canvas canvas;
	Paint paint = new Paint();
	String satirlar;
	int i,j;
	int x, y;
	
	int secilenkapix,secilenkapiy,secilenfarex,secilenfarey,baslatx,baslaty,enkisax,enkisay,kontrol,maliyet,yenidenbaslat;
	int kapi1_x,kapi1_y,kapi2_x,kapi2_y,kapi3_x,kapi3_y,kapi4_x,kapi4_y,kapi5_x,kapi5_y;

	Fareler fare1 = new Fareler();
	Fareler fare2 = new Fareler();
	Fareler fare3 = new Fareler();
	Fareler fare = new Fareler();
	Fareler secilenfare = new Fareler();
	
	
	public CizimSinifi (Context context)  {

		super(context);
		setFocusable(true);
		 setFocusableInTouchMode(true);      
		 this.setOnTouchListener(this);   
		
		 
		 
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);

		/******** Dosyadan okuma iþlemi burada baþlýyor ****************/
		
		i = 0; j = 0;
		satirlar = DosyadanOku();
		
		StringTokenizer st = new StringTokenizer(satirlar,",+");
		while(st.hasMoreElements())
		{
			orjinalharita[i][j] = Integer.parseInt(st.nextToken());
			i++;
			if (i == 26) 
			{
				j++;
				i = 0;
			}
		}
	
	/*	
		String[] str = satirlar.split(",+");
		for (int k = 0; k < str.length; k++) {
			harita[i][j] = Integer.parseInt(str[k]);
			i++;
			if (i == 26) {
				j++;
				i = 0;
			}
		}*/
		/************Dosyadan okuma iþlemi burada bitiyor**************/
		/***************Haritayý yedekliyoruz**************************/
		HaritayiYedekle();
		/*************************harita yedeklendi*******************/
		fareozellik = BitmapFactory.decodeResource(getResources(),R.drawable.fareozellikleri);
		secilenkapi = BitmapFactory.decodeResource(getResources(),R.drawable.secilenkapi);
		aciklama = BitmapFactory.decodeResource(getResources(),R.drawable.aciklama);
		baslat = BitmapFactory.decodeResource(getResources(),R.drawable.baslat);
		duraklat = BitmapFactory.decodeResource(getResources(),R.drawable.duraklat);
		enkisa = BitmapFactory.decodeResource(getResources(),R.drawable.enkisa);
		
		fare1.resim = BitmapFactory.decodeResource(getResources(),R.drawable.yesilfare);
		fare2.resim = BitmapFactory.decodeResource(getResources(),R.drawable.sarifare);
		fare3.resim = BitmapFactory.decodeResource(getResources(),R.drawable.kahverengifare);
		fare1.yukari = BitmapFactory.decodeResource(getResources(),R.drawable.yesilfareyukari);
		fare2.yukari = BitmapFactory.decodeResource(getResources(),R.drawable.sarifareyukari);
		fare3.yukari = BitmapFactory.decodeResource(getResources(),R.drawable.kahverengifareyukari);
		fare1.asagi = BitmapFactory.decodeResource(getResources(),R.drawable.yesilfareasagi);
		fare2.asagi = BitmapFactory.decodeResource(getResources(),R.drawable.sarifareasagi);
		fare3.asagi = BitmapFactory.decodeResource(getResources(),R.drawable.kahverengifareasagi);
		fare1.sol = BitmapFactory.decodeResource(getResources(),R.drawable.yesilfaresol);
		fare2.sol = BitmapFactory.decodeResource(getResources(),R.drawable.sarifaresol);
		fare3.sol = BitmapFactory.decodeResource(getResources(),R.drawable.kahverengifaresol);
		fare1.sag = BitmapFactory.decodeResource(getResources(),R.drawable.yesilfaresag);
		fare2.sag = BitmapFactory.decodeResource(getResources(),R.drawable.sarifaresag);
		fare3.sag = BitmapFactory.decodeResource(getResources(),R.drawable.kahverengifaresag);
		
		
		
		
		fare1.oncelikyonu[0] = "sol"; fare1.oncelikyonu[1] = "ileri"; fare1.oncelikyonu[2] = "sag";
		fare2.oncelikyonu[0] = "ileri"; fare2.oncelikyonu[1] = "sol"; fare2.oncelikyonu[2] = "sag";
		fare3.oncelikyonu[0] = "sag"; fare3.oncelikyonu[1] = "sol"; fare3.oncelikyonu[2] = "ileri";
		
		fare1.x=615; fare1.y=375;
		fare2.x=615; fare2.y=410;
		fare3.x=615; fare3.y=445;
		
		baslatx=650; baslaty=375;
		enkisax=0; enkisay=375;
		
		kapi1_x=25;kapi1_y=350;
		kapi2_x=175;kapi2_y=350;
		kapi3_x=300;kapi3_y=350;
		kapi4_x=625;kapi4_y=250;
		kapi5_x=625;kapi5_y=100;
		

		secilenkapix=900; secilenkapiy=900;//seçim olmadan önce seçilen
		secilenfarex=900; secilenfarey=900;//seçimler görünmesin diye
		kontrol=0; maliyet=0; yenidenbaslat=1;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		/*********************** Harita çiziliyor *********************/
		x = 0;
		y = 0;
		for (j = 0; j < 15; j++) {
			for (i = 0; i < 26; i++) {
				if (orjinalharita[i][j] == 0) {
					paint.setColor(Color.WHITE);
				} else {
					paint.setColor(Color.BLUE);
				}
				canvas.drawRect(x, y, x + 24, y + 24, paint);
				x += 25;
				
			}
			x = 0;
			y += 25;
		}
		/***************** Harita çizildi **************************/
		
		for (j = 0; j < 15; j++) {
			for (i = 0; i < 26; i++) {
				if(harita[i][j]==2) {
					paint.setColor(Color.RED);
					canvas.drawCircle(i*25+12,j*25+12,3,paint);
				}
			}
		}
		/******************hareketler burada olacak*****************/
		
		
	//	paint.setStrokeWidth(100);
		paint.setColor(Color.MAGENTA);
		canvas.drawRect(0,375,800,480,paint);
		
		canvas.drawBitmap(fareozellik,650,0,null);
		canvas.drawBitmap(aciklama,650,100,null);
		
		if(kontrol==1)
		{
			canvas.drawBitmap(duraklat,baslatx,baslaty,null);
			canvas.drawBitmap(enkisa,enkisax,enkisay,null);
		} 
		else if(kontrol==2)
		{
			canvas.drawBitmap(duraklat,enkisax,enkisay,null);
			canvas.drawBitmap(baslat,baslatx,baslaty,null);
		} 
		else	
		{
			canvas.drawBitmap(baslat,baslatx,baslaty,null);
			canvas.drawBitmap(enkisa,enkisax,enkisay,null);
		}
		
		
		
		
		
		
		paint.setColor(Color.GREEN);
		canvas.drawRect(kapi1_x,kapi1_y,kapi1_x+25,kapi1_y+25,paint);
		canvas.drawRect(kapi2_x,kapi2_y,kapi2_x+25,kapi2_y+25,paint);
		canvas.drawRect(kapi3_x,kapi3_y,kapi3_x+25,kapi3_y+25,paint);
		canvas.drawRect(kapi4_x,kapi4_y,kapi4_x+25,kapi4_y+25,paint);
		canvas.drawRect(kapi5_x,kapi5_y,kapi5_x+25,kapi5_y+25,paint);
		
		
		
		
		canvas.drawBitmap(fare1.resim,fare1.x,fare1.y,null);
		canvas.drawBitmap(fare2.resim,fare2.x,fare2.y,null);
		canvas.drawBitmap(fare3.resim,fare3.x,fare3.y,null);
		canvas.drawBitmap(secilenkapi,secilenkapix,secilenkapiy,null);
		canvas.drawCircle(secilenfarex-18,secilenfarey+17,10,paint);
		
		
		/**************************************Labirentte gezme kurallarý burada baþlýyor*********************************************************/
		//NOT: else lerin içi farenin öncelik yonu ileri kabul edilerek oluþturuldu. yeniden düzenlenecek
		
		if(yenidenbaslat==1)
		{
		
		if(kontrol == 1 || kontrol==2)
		{
			if(fare.fareyonu == "yukari")
			{
				if(fare.y%25 !=0 || fare.x%25 !=0)
				{
					canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
					fare.y-=5;	//yone göre düzenlendi
				}
				else
				{
					harita[fare.x/25][fare.y/25]=2;
					maliyet++;
					KavsakKontrol();
					if(fare.oncelikyonu[0]=="ileri")
					{
						if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //yukari boþ mu
						{
							canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
							fare.fareyonu="yukari";
							fare.y-=5;
						}
						else 
						{
							
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sol tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag";
									fare.x+=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi";
									fare.y+=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag";
									fare.x+=5;
								}
								else if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol";
									fare.x-=5;
								}
								else//bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi";
									fare.y+=5;
								}
							}
						}
					}
					
					if(fare.oncelikyonu[0]=="sol")
					{
						if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sol boþ mu
						{
							canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
							fare.fareyonu="sol";
							fare.x-=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //ileri  boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari";
									fare.y-=5;
								}
								else if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag";
									fare.x+=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi";
									fare.y+=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag";
									fare.x+=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //ileri boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari";
									fare.y-=5;
								}
								else
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi";
									fare.y+=5;
								}
							}
						}

					}
					
					if(fare.oncelikyonu[0]=="sag")
					{
						if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sað boþ mu
						{
							canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
							fare.fareyonu="sag";
							fare.x+=5;
							
							
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sol tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol";
									fare.x-=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari";
									fare.y-=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi";
									fare.y+=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari";
									fare.y-=5;
								}
								else if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol";
									fare.x-=5;
								}
								else
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="asagi";
									fare.y+=5;
								}
							}
						}
					}
					
				}
			}
			
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(fare.fareyonu=="asagi")
			{
				if(fare.y%25 !=0 || fare.x%25 !=0)
				{
					canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
					fare.y+=5;	//yone göre düzenlendi
				}
				else 
				{
					harita[fare.x/25][fare.y/25]=2;
					maliyet++;
					KavsakKontrol();
					if(fare.oncelikyonu[0]=="ileri")
					{
						if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //ileri boþ mu
						{
							canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
							fare.fareyonu="asagi"; 
							fare.y+=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sol tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null); //   !!!dikkat farenin yonu sað oldu
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sol boþ mu (fareye göredir)
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else//bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
							}
						}
					}
					
					if(fare.oncelikyonu[0]=="sol")
					{
						if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sol boþ mu
						{
							//fare sola dönecek 
							canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
							fare.fareyonu="sag"; 
							fare.x+=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //ileri  boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //ileri boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
							}
						}
					}
					
					if(fare.oncelikyonu[0]=="sag")
					{
						if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //sað boþ mu
						{
							//fare saða dönecek
							canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
							fare.fareyonu="sol"; 
							fare.x-=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sol tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0))//ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
							}
						}
					}
					
				}
			}
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(fare.fareyonu == "sol")
			{
				if(fare.y%25 !=0 || fare.x%25 !=0)
				{
					canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
					fare.x-=5;	//yone göre düzenlendi
				}
				else 
				{
					harita[fare.x/25][fare.y/25]=2;
					maliyet++;
					KavsakKontrol();
					if(fare.oncelikyonu[0]=="ileri")
					{
						if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //boþ mu
						{
							canvas.drawBitmap(fare.sol,fare.x,fare.y,null); //   !!!dikkat farenin yonu sað oldu
							fare.fareyonu="sol"; 
							fare.x-=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sol tarafý boþ mu (farenin solu orjinalharitanýn aþagýsý)
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else//bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
							}
						}
						
					}
					
					if(fare.oncelikyonu[0]=="sol")
					{
						if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sol boþ mu
						{
							canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
							fare.fareyonu="asagi"; 
							fare.y+=5;
							
						}
						else 
						{
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //ileri  boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //ileri boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
							}
						}

					}
					
					if(fare.oncelikyonu[0]=="sag")
					{
						if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sað boþ mu
						{
							canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
							fare.fareyonu="yukari"; 
							fare.y-=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sol tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25-1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25-1][(int)fare.y/25]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
							}
						}
					}
					
				}
			}
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(fare.fareyonu == "sag")
			{
				if(fare.y%25 !=0 || fare.x%25 !=0)
				{
					canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
					fare.x+=5;	//yone göre düzenlendi
				}
				else 
				{
					harita[fare.x/25][fare.y/25]=2;
					maliyet++;
					KavsakKontrol();
					if(fare.oncelikyonu[0]=="ileri")
					{
						if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //ileri boþ mu (yani sað)
						{
							canvas.drawBitmap(fare.sag,fare.x,fare.y,null); //   !!!dikkat farenin yonu sað oldu
							fare.fareyonu="sag"; 
							fare.x+=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sol tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else//bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
							}
						}
						
					}
					
					if(fare.oncelikyonu[0]=="sol")
					{
						if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sol boþ mu
						{
							canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
							fare.fareyonu="yukari"; 
							fare.y-=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //ileri  boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
							}
							
							if(fare.oncelikyonu[1]=="sag")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
									fare.fareyonu="asagi"; 
									fare.y+=5;
								}
								else if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //ileri boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
							}
						}
					}
					
					if(fare.oncelikyonu[0]=="sag")
					{
						if((orjinalharita[(int)fare.x/25][(int)fare.y/25+1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25+1]==0)) //sað boþ mu
						{
							
							canvas.drawBitmap(fare.asagi,fare.x,fare.y,null);
							fare.fareyonu="asagi"; 
							fare.y+=5;
						}
						else 
						{
							if(fare.oncelikyonu[1]=="sol")
							{
								if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //ileri tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else //bütün yönler doluysa geri dönecek
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
							}
							if(fare.oncelikyonu[1]=="ileri")
							{
								if((orjinalharita[(int)fare.x/25+1][(int)fare.y/25]==0)&&(gizliharita[(int)fare.x/25+1][(int)fare.y/25]==0)) //sað tarafý boþ mu
								{
									canvas.drawBitmap(fare.sag,fare.x,fare.y,null);
									fare.fareyonu="sag"; 
									fare.x+=5;
								}
								else if((orjinalharita[(int)fare.x/25][(int)fare.y/25-1]==0)&&(gizliharita[(int)fare.x/25][(int)fare.y/25-1]==0)) //sol boþ mu
								{
									canvas.drawBitmap(fare.yukari,fare.x,fare.y,null);
									fare.fareyonu="yukari"; 
									fare.y-=5;
								}
								else
								{
									canvas.drawBitmap(fare.sol,fare.x,fare.y,null);
									fare.fareyonu="sol"; 
									fare.x-=5;
								}
							}
						}
					}
				}
			}
			
		}//if(kontrol==1 || kotrol==2)
	}//if(true)
		
		if (kontrol == 0){
			 
			 for (Point point : points){
				if (point.x >= kapi1_x && point.x <= kapi1_x + 25 && point.y >= kapi1_y && point.y <= kapi1_y + 25) {
					secilenkapix = kapi1_x;
					secilenkapiy = kapi1_y;
				}
				if (point.x >= kapi2_x && point.x <= kapi2_x + 25 && point.y >= kapi2_y && point.y <= kapi2_y + 25) {
					secilenkapix = kapi2_x;
					secilenkapiy = kapi2_y;
				}
				if (point.x >= kapi3_x && point.x <= kapi3_x + 25 && point.y >= kapi3_y && point.y <= kapi3_y + 25) {
					secilenkapix = kapi3_x;
					secilenkapiy = kapi3_y;
				}
				if (point.x >= kapi4_x && point.x <= kapi4_x + 25 && point.y >= kapi4_y && point.y <= kapi4_y + 25) {
					secilenkapix = kapi4_x;
					secilenkapiy = kapi4_y;
				}
				if (point.x >= kapi5_x && point.x <= kapi5_x + 25 && point.y >= kapi5_y && point.y <= kapi5_y + 25) {
					secilenkapix = kapi5_x;
					secilenkapiy = kapi5_y;
				}
				
				if (point.x >= fare1.x && point.x <= fare1.x + 25 && point.y >= fare1.y && point.y <= fare1.y + 25) {
					secilenfarex=fare1.x;//bunlar yeþil nokta için
					secilenfarey=fare1.y;//bunlar yeþil nokta için
					fare.x=fare1.x;
					fare.y=fare1.y;
					fare.oncelikyonu[0]=fare1.oncelikyonu[0];
					fare.oncelikyonu[1]=fare1.oncelikyonu[1];
					fare.oncelikyonu[2]=fare1.oncelikyonu[2];
					fare.yukari=fare1.yukari;
					fare.asagi=fare1.asagi;
					fare.sol=fare1.sol;
					fare.sag=fare1.sag;
				}
				if (point.x >= fare2.x && point.x <= fare2.x + 25 && point.y >= fare2.y && point.y <= fare2.y + 25) {
					secilenfarex=fare2.x;//bunlar yeþil nokta için
					secilenfarey=fare2.y;//bunlar yeþil nokta için
					fare.x=fare2.x;
					fare.y=fare2.y;
					fare.oncelikyonu[0]=fare2.oncelikyonu[0];
					fare.oncelikyonu[1]=fare2.oncelikyonu[1];
					fare.oncelikyonu[2]=fare2.oncelikyonu[2];
					fare.yukari=fare2.yukari;
					fare.asagi=fare2.asagi;
					fare.sol=fare2.sol;
					fare.sag=fare2.sag;
				}
				if (point.x >= fare3.x && point.x <= fare3.x + 25 && point.y >= fare3.y && point.y <= fare3.y + 25) {
					secilenfarex=fare3.x;//bunlar yeþil nokta için
					secilenfarey=fare3.y;//bunlar yeþil nokta için
					fare.x=fare3.x;
					fare.y=fare3.y;
					fare.oncelikyonu[0]=fare3.oncelikyonu[0];
					fare.oncelikyonu[1]=fare3.oncelikyonu[1];
					fare.oncelikyonu[2]=fare3.oncelikyonu[2];
					fare.yukari=fare3.yukari;
					fare.asagi=fare3.asagi;
					fare.sol=fare3.sol;
					fare.sag=fare3.sag;
				}
				if (point.x >= baslatx && point.x <= baslatx + 150 && point.y >= baslaty && point.y <= baslaty + 105) 
				{
					fare.x=secilenkapix;
					fare.y=secilenkapiy;
					
					if(secilenkapiy==350)
					{
						fare.fareyonu="yukari";
						fare.y-=5;
					}
					else
					{
						fare.fareyonu="sol";
						fare.x-=5;
					}
					
					kontrol=1;//fare burada hareket etmeye baþlýyor
					break;
				}
				
				if (point.x >= enkisax && point.x <= enkisax + 150 && point.y >= enkisay && point.y <= enkisay + 105) 
				{
					fare.x=secilenkapix;
					fare.y=secilenkapiy;
					
					if(secilenkapix==kapi1_x)
					{
						fare.oncelikyonu[0]="sag";
						fare.oncelikyonu[1]="ileri";
						fare.oncelikyonu[2]="sol";
						
					}
					if(secilenkapix==kapi2_x)
					{
						fare.oncelikyonu[0]="sag";
						fare.oncelikyonu[1]="ileri";
						fare.oncelikyonu[2]="sol";
					
					}
					if(secilenkapix==kapi3_x)
					{
						fare.oncelikyonu[0]="sol";
						fare.oncelikyonu[1]="ileri";
						fare.oncelikyonu[2]="sag";
					}
					if(secilenkapiy==kapi4_y)
					{
						fare.oncelikyonu[0]="sag";
						fare.oncelikyonu[1]="ileri";
						fare.oncelikyonu[2]="sol";
					}
					if(secilenkapiy==kapi5_y)
					{
						fare.oncelikyonu[0]="sol";
						fare.oncelikyonu[1]="ileri";
						fare.oncelikyonu[2]="sag";
					}
					
					if(secilenkapiy==350)
					{
						fare.fareyonu="yukari";
						fare.y-=5;
					}
					else
					{
						fare.fareyonu="sol";
						fare.x-=5;
					}
					kontrol=2;//fare burada hareket etmeye baþlýyor
					break;
				}
			}
		}	 
		
		if (yenidenbaslat==0)
		{	 
			 for (Point point : points)
			 {
				if (point.x >= kapi1_x && point.x <= kapi1_x + 25 && point.y >= kapi1_y && point.y <= kapi1_y + 25)
				{

				//	CizimSinifi cs = new CizimSinifi(context);
					
				}
			 }	
		}
		
		//bu if bloðunu altta yazmak gerekir.
		if((fare.x==kapi1_x && fare.y==kapi1_y) || (fare.x==kapi2_x && fare.y==kapi2_y) || (fare.x==kapi3_x && fare.y==kapi3_y) || (fare.x==kapi4_x && fare.y==kapi4_y) || (fare.x==kapi5_x && fare.y==kapi5_y))
		{//fare çýkýþ kapýsýný bulursa...
			paint.setColor(Color.YELLOW);
			if(kontrol==1) canvas.drawText("Fare çýkýþý "+maliyet+" adýmda bulmuþtur.",200,430,paint);
			if(kontrol==2) canvas.drawText("Farenin en kýsa yoldan çýkýþý "+maliyet+" adýmdýr.",200,430,paint);
			yenidenbaslat=0;
		}
		
		invalidate();
	}
	
	/****************************************************************************/
	public void KavsakKontrol() {
		if (fare.fareyonu == "yukari") {
			if ((gizliharita[fare.x / 25 - 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25 + 1][fare.y / 25] == 0) // sol-sað
			|| (gizliharita[fare.x / 25 - 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 - 1] == 0) // sol-üst
			|| (gizliharita[fare.x / 25 + 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 - 1] == 0)) // sað-üst
			{
				if(gizliharita[fare.x / 25][fare.y / 25 + 1]==0) gizliharita[fare.x / 25][fare.y / 25 + 1]++;
			}
		}
		if (fare.fareyonu == "asagi") {
			if ((gizliharita[fare.x / 25 - 1][fare.y / 25] == 0)&& (gizliharita[fare.x / 25 + 1][fare.y / 25] == 0) // sol-sað
			|| (gizliharita[fare.x / 25 - 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 + 1] == 0) // sol-alt
			|| (gizliharita[fare.x / 25 + 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 + 1] == 0)) // sað-alt
			{
				gizliharita[fare.x / 25][fare.y / 25 - 1]++;
			}
		}
		if (fare.fareyonu == "sol") {
			if ((gizliharita[fare.x / 25][fare.y / 25 + 1] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 - 1] == 0) //alt-üst
			|| (gizliharita[fare.x / 25 - 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 + 1] == 0) // sol-alt
			|| (gizliharita[fare.x / 25 - 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 - 1] == 0)) // sol-üst
			{
				gizliharita[fare.x / 25 + 1][fare.y / 25]++;
			}
		}
		if (fare.fareyonu == "sag") {
			if ((gizliharita[fare.x / 25][fare.y / 25 + 1] == 0)&& (gizliharita[fare.x / 25][fare.y / 25 - 1] == 0) // alt-üst
			|| (gizliharita[fare.x / 25 + 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 + 1] == 0) // sað-alt
			|| (gizliharita[fare.x / 25 + 1][fare.y / 25] == 0)&&(gizliharita[fare.x / 25][fare.y / 25 - 1] == 0)) // sað-üst
			{
				gizliharita[fare.x / 25 - 1][fare.y / 25]++;
			}
		}
	}
	
	public void HaritayiYedekle()
	{
		for(j=0;j<15;j++){
			for(i=0;i<26;i++){
				harita[i][j]=orjinalharita[i][j];
				gizliharita[i][j]=orjinalharita[i][j];
			}
		}
	}
	
	public boolean onTouch(View view, MotionEvent event) {           
		             Point point = new Point();       
		             point.x = event.getX();       
		             point.y = event.getY();       
		             points.add(point);
		             invalidate();     
		             return true;   
		             }      

	class Point {   
		      float x, y;   
		      @Override   
		      public String toString() {       
		              return x + "," + y;   
		              }
		      }   
	
	public String DosyadanOku() { 

		InputStream inputStream = getResources().openRawResource(R.raw.benioku);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return byteArrayOutputStream.toString();
	}

}