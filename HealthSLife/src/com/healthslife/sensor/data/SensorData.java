package com.healthslife.sensor.data;

public class SensorData {
	
	public final static String FORMAT=".png";//图片默认格式
	
	public static String photoPath=null;//头像的保存路径!!
	
	public final static String DEST_DIR="/sdcard/healthSLife/userface/";//保存本地头像的目录
	public static boolean isShowStep_DataCentre=true;//判断当前是否在显示步数!!
	public static boolean isFromDataCentre=false;//判断是否来自数据中心
	
	public static int AQI=0;//空气质量指数!!
	
	
	public static boolean isFirstLogin=true;
	
	public static int info_id=-1;//默认值为-1，表示没有数据!!
	
	/*用户信息表*/
	public static boolean isLogin=false;//默认值为false（未登录）:判断是否有用户登录!!!!!!
	public static int Aim_stepNum=7000;//目标运动步数
	public static int Aim_energy=200;//目标运动步数
	
	public final static String USERNAME="DefaultUser";//默认用户名!!!
	public static String Username=USERNAME;//给用户名，设置默认值!!!
	public static int height=172;//默认，身高,单位:cm
	public static int weight=64;//默认，体重,单位:kg
	public static int gender=1;//性别：1为男，0为女。
	public static boolean isFirstUse = true; //是否是首次使用
	/*音乐等待语音播报功能*/
	public static boolean isSpeaking=false;//可以用来判断是否正在进行语音播报!!!
	
	
	public final static int Gender_MAN=1;//性别男。
	public final static int Gender_FEMALE=0;//性别女。
	
	public static float StepLength=80;//StepLength--步长CM
	
	public static boolean isOpenedVoice=true;//判断是否打开语音播报功能!
	public static boolean isFirst=true;//判断用户是否第一次打开程序!
	public static boolean isSpeaked=false;//判断是否进行了语音播报!
	public static boolean isMoving=false;//判断用户当前是否在运动!
	public final static boolean isMetric=true;//判断单位是否为公制单位或英制单位，默认单位：公制单位![先不考虑英制单位 ]
	
	/*积分服务*/
	public static boolean isMarked_Low=false;//判断是否已经给用户积分——低!
	public static boolean isMarked_High=false;//判断是否已经给用户积分——高!
	public final static int DAY_CREDITS_LOW=5;//用户每天积分——低！（每天高低积分不叠加）
	public final static int DAY_CREDITS_HIGH=15;//用户每天积分——高！（每天高低积分不叠加）
	public final static int MARK_BASE_STEPNUM_LOW=5000;//获取积分所需运动步数——低！
	public final static int MARK_BASE_STEPNUM_HIGH=10000;//获取积分所需运动步数——高！
	public static int TotalCredits=0;//用户总积分
	
	public final static long AVERAGE_SPEED_MONITOR_TIME=10000;//平均速度监测时间，单位：毫秒!!!!!!!
	public static long Monitor_LastTime=0;//上一次平均速度的监测时间!!
	
	
	public static int SpeakHZ_StepNum=20;//语音播报的频率,单位：步!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	/*HeightStepRatio身高步长比率*/
	public final static float BIG_HS_RATIO=(float) 0.47619;//175CM以上
	public final static float MEDIUM_HS_RATIO=(float) 0.46429;//155-175CM
	public final static float SMALL_HS_RATIO=(float) 0.45238;//155CM以下
	
	/*身高范围:155CM以下,155-175CM,175CM以上*/
	public final static float HEIGHT_UP=175;//
	public final static float HEIGHT_DOWN=155;//
	
	/*RunningWalking跑步步行步长比率*/
	public final static float RW_RATIO=(float) 1.562;//-----跑步/步行比率!
	
	/*运动输入值*/
//	public static int height_M=172;//Man男，身高,单位:cm
//	public static int weight_M=62;//Man男，体重,单位:kg
//	public static int height_F=158;//Female女，身高,单位:cm
//	public static int weight_F=45;//Female女，体重,单位:kg
	
	
	/*运动强度等级：运动强度越大值越大*/
	public final static int LEVEL_ONE=1;//运动强度等级：1。0-90
	public final static int LEVEL_TWO=2;//运动强度等级：1。90-240
	public final static int LEVEL_THREE=3;//运动强度等级：1。240
	public final static int LEVEL_UP=240;//运动强度等级：1。0-90
	public final static int LEVEL_DOWN=90;//运动强度等级：1。90-240
	public static int Level=1;//运动强度等级：默认运动强度等级为1。//-------------------供音乐服务中的智能播放之用!!!
	
	public static int sensorLevel=10;//灵敏度，单位：无
	public static boolean isRunning_sport=false;//运动模式:跑步,或步行;单位：无
	public static long Running_Time=0;//跑步时间-----单位：毫秒
	
	//public static long sayIntervalTime=15*1000;//语音播报时间间隔,单位:秒
	
	
	/*判断需要播报的内容*/
//	public static boolean isSayStepNum=false;//是否播报步数
//	public static boolean isSayPace=false;//运动模式，单位：无
//	public static boolean isSayDistance=false;//运动模式，单位：无
//	public static boolean isSayEnergy=false;//运动模式，单位：无
//	public static boolean isSayMoveHZ=false;//运动模式，单位：无
//	public static boolean isSaySpeed=false;//运动模式，单位：无
	
	/*运动数据*/
	public static int stepNum_lastRefresh=0;//步数，单位：步----上次刷新数据库，此时的步数!!!
	public static int stepNum_old=0;//步数,单位:步---根据上次记录步数，比较当前步数是否变化，来判断当前是否是运动状态!!!!!
	public static int stepNum_lastSpeak=0;//步数,单位:步---记录上次语音播报时记录的步数!!!!!
	public static int stepNum=0;//步数,单位:步----------//share
	public static float moveTime=0;//移动时间,单位:分钟
	public static int moveHZ=0;//移动时间,单位:步/分钟
	public static float speed=0;//移动速度,单位:公里/小时
	public static int energy=0;//能量,单位:kcal----------//share
	public static float distance=0;//能量,单位:km或公里----------//share
	public static float effect=0;//例如：减掉了XX克肥肉！----------//share
	public static float distance_old=0;//能量,单位:km或公里---根据上次记录运动距离!!!
	
	public static int total_stepNum=0;//总累计步数,单位:步----------//share
	
	
	public static int stepNum_lastFetch=0;//上次获取的步数情况!!!
	public static int energy_lastFetch=0;//上次获取的能量情况!!!
	public static float distance_lastFetch=0;//上次获取的距离情况!!!
	
	
	
	public static synchronized int getStepNum_lastFetch() {
		return stepNum_lastFetch;
	}
	public static synchronized void setStepNum_lastFetch(int stepNum_lastFetch) {
		SensorData.stepNum_lastFetch = stepNum_lastFetch;
	}
	public static synchronized int getEnergy_lastFetch() {
		return energy_lastFetch;
	}
	public static synchronized void setEnergy_lastFetch(int energy_lastFetch) {
		SensorData.energy_lastFetch = energy_lastFetch;
	}
	public static synchronized float getDistance_lastFetch() {
		return distance_lastFetch;
	}
	public static synchronized void setDistance_lastFetch(float distance_lastFetch) {
		SensorData.distance_lastFetch = distance_lastFetch;
	}
	public static synchronized int getAim_energy() {
		return Aim_energy;
	}
	public static synchronized void setAim_energy(int aim_energy) {
		Aim_energy = aim_energy;
	}
	public static synchronized boolean isOpenedVoice() {
		return isOpenedVoice;
	}
	public static synchronized void setOpenedVoice(boolean isOpenedVoice) {
		SensorData.isOpenedVoice = isOpenedVoice;
	}
	public static synchronized String getPhotoPath() {
		return photoPath;
	}
	public static synchronized void setPhotoPath(String photoPath) {
		SensorData.photoPath = photoPath;
	}
	public static synchronized boolean isShowStep_DataCentre() {
		return isShowStep_DataCentre;
	}
	public static synchronized void setShowStep_DataCentre(
			boolean isShowStep_DataCentre) {
		SensorData.isShowStep_DataCentre = isShowStep_DataCentre;
	}
	public static synchronized boolean isFromDataCentre() {
		return isFromDataCentre;
	}
	public static synchronized void setFromDataCentre(boolean isFromDataCentre) {
		SensorData.isFromDataCentre = isFromDataCentre;
	}
	public static synchronized int getAQI() {
		return AQI;
	}
	public static synchronized void setAQI(int aQI) {
		AQI = aQI;
	}
	public static synchronized int getInfo_id() {
		return info_id;
	}
	public static synchronized void setInfo_id(int info_id) {
		SensorData.info_id = info_id;
	}
	public static synchronized int getStepNum_lastRefresh() {
		return stepNum_lastRefresh;
	}
	public static synchronized void setStepNum_lastRefresh(int stepNum_lastRefresh) {
		SensorData.stepNum_lastRefresh = stepNum_lastRefresh;
	}
	public static synchronized boolean isSpeaking() {
		return isSpeaking;
	}
	public static synchronized void setSpeaking(boolean isSpeaking) {
		SensorData.isSpeaking = isSpeaking;
	}
	public synchronized static int getTotal_stepNum() {
		return total_stepNum;
	}
	public synchronized static void setTotal_stepNum(int total_stepNum) {
		SensorData.total_stepNum = total_stepNum;
	}
	public synchronized static int getAim_stepNum() {
		return Aim_stepNum;
	}
	public synchronized static void setAim_stepNum(int aim_stepNum) {
		Aim_stepNum = aim_stepNum;
	}
	public synchronized static String getUsername() {
		return Username;
	}
	public synchronized static void setUsername(String username) {
		Username = username;
	}
	public synchronized static boolean isLogin() {
		return isLogin;
	}
	public synchronized static void setLogin(boolean isLogin) {
		SensorData.isLogin = isLogin;
	}
	public synchronized static long getRunning_Time() {
		return Running_Time;
	}
	public synchronized static void setRunning_Time(long running_Time) {
		Running_Time = running_Time;
	}
	public synchronized static boolean isRunning_sport() {
		return isRunning_sport;
	}
	public synchronized static void setRunning_sport(boolean isRunning_sport) {
		SensorData.isRunning_sport = isRunning_sport;
	}
	public synchronized static boolean isMarked_Low() {
		return isMarked_Low;
	}
	public synchronized static void setMarked_Low(boolean isMarked_Low) {
		SensorData.isMarked_Low = isMarked_Low;
	}
	public synchronized static boolean isMarked_High() {
		return isMarked_High;
	}
	public synchronized static void setMarked_High(boolean isMarked_High) {
		SensorData.isMarked_High = isMarked_High;
	}
	public synchronized static int getTotalCredits() {
		return TotalCredits;
	}
	public synchronized static void setTotalCredits(int totalCredits) {
		TotalCredits = totalCredits;
	}
	public synchronized static int getStepNum_lastSpeak() {
		return stepNum_lastSpeak;
	}
	public synchronized static void setStepNum_lastSpeak(int stepNum_lastSpeak) {
		SensorData.stepNum_lastSpeak = stepNum_lastSpeak;
	}
	public synchronized static boolean isFirst() {
		return isFirst;
	}
	public synchronized static void setFirst(boolean isFirst) {
		SensorData.isFirst = isFirst;
	}
	public synchronized static long getMonitor_LastTime() {
		return Monitor_LastTime;
	}
	public synchronized static void setMonitor_LastTime(long monitor_LastTime) {
		Monitor_LastTime = monitor_LastTime;
	}
	public synchronized static int getSpeakHZ_StepNum() {
		return SpeakHZ_StepNum;
	}
	public synchronized static void setSpeakHZ_StepNum(int speakHZ_StepNum) {
		SpeakHZ_StepNum = speakHZ_StepNum;
	}
	public synchronized static float getStepLength() {
		return StepLength;
	}
	public synchronized static void setStepLength(float stepLength) {
		StepLength = stepLength;
	}
	public synchronized static int getHeight() {
		return height;
	}
	public synchronized static void setHeight(int height) {
		SensorData.height = height;
	}
	public synchronized static int getWeight() {
		return weight;
	}
	public synchronized static void setWeight(int weight) {
		SensorData.weight = weight;
	}
	public synchronized static int getGender() {
		return gender;
	}
	public synchronized static void setGender(int gender) {
		SensorData.gender = gender;
	}
	public synchronized static int getLevel() {
		return Level;
	}
	public synchronized static void setLevel(int level) {
		Level = level;
	}
	public synchronized static int getStepNum_old() {
		return stepNum_old;
	}
	public synchronized static void setStepNum_old(int stepNum_old) {
		SensorData.stepNum_old = stepNum_old;
	}
	public synchronized static int getStepNum() {
		return stepNum;
	}
	public synchronized static void setStepNum(int stepNum) {
		SensorData.stepNum = stepNum;
	}
	public synchronized static float getMoveTime() {
		return moveTime;
	}
	public synchronized static void setMoveTime(float moveTime) {
		SensorData.moveTime = moveTime;
	}
	public synchronized static int getMoveHZ() {
		return moveHZ;
	}
	public synchronized static void setMoveHZ(int moveHZ) {
		SensorData.moveHZ = moveHZ;
	}
	public synchronized static float getSpeed() {
		return speed;
	}
	public synchronized static void setSpeed(float speed) {
		SensorData.speed = speed;
	}
	public synchronized static int getEnergy() {
		return energy;
	}
	public synchronized static void setEnergy(int energy) {
		SensorData.energy = energy;
	}
	public synchronized static float getDistance() {
		return distance;
	}
	public synchronized static void setDistance(float distance) {
		SensorData.distance = distance;
	}
	public synchronized static float getDistance_old() {
		return distance_old;
	}
	public synchronized static void setDistance_old(float distance_old) {
		SensorData.distance_old = distance_old;
	}
	
	
	
	
	
}
