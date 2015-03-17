package com.healthslife.map;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

public class MapGlobalVariable {
	protected static MapView mMapView = null;       //The object of map 地图对象
	protected static LocationClient mLocClient = null;            //The object of location 定位对象
	protected static BitmapDescriptor mCurrentMarker = null;      //The bitmap 位图对象
	protected static BaiduMap mBaiduMap = null;                   //The object of BaiduMap 百度地图对象
	
	protected static LatLng ll = null; // 定义坐标对象
	protected static LatLng newll = null; // 定义坐标对象
	public static String city = null;              //Set default city null默认城市为空
	protected static boolean isFirstLoc = true;              //Whether the first located是否是首次定位
	
}
