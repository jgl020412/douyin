<script>
	import provinceList from './json/area_province.js';
	import cityList from './json/area_city.js';
	import districtList from './json/area_district.js';
	export default {
		globalData: {  
			serverUrl: "http://0.0.0.0",					// 生产环境
			
			provinceList: provinceList,
			cityList: cityList,
			districtList: districtList,
		},
		
		onLaunch: function() {
			
			console.log("onLaunch...测试调试信息的打印");
			
			
			var me = this;
			uni.onTabBarMidButtonTap(()=>{
				
				// 未登录不能发布视频
				var myUserInfo = getApp().getUserInfoSession();
				if (myUserInfo == null) {
					uni.showToast({
						duration: 3000,
						title: "请登录~",
						icon: "none"
					});
					uni.navigateTo({
						url: "../loginRegist/loginRegist",
						animationType: "slide-in-bottom",
						success() {
							me.loginWords = "请登录"
						}
					});
					return;
				}
				
				// console.log('onTabBarMidButtonTap');
				uni.switchTab({
					url: "../me/me"
				});
				
				uni.chooseVideo({
					sourceType: ['album'],
					success(e) {
						
						uni.navigateTo({
							url: "/pages/publish/publish?fileObjectEvent=" + JSON.stringify(e)
						})
						
						/**
						 * 或者采用uniCloud，在客户端完成上传，减少链路，因为文件大，通信链路和耗时是双倍的
						 */
						var times = new Date().getTime();
					}
				})
				
			});

		},
		onShow: function() {
		},
		onHide: function() {
		},
		methods: {
			getDefaultVlogList() {
				var list = [
					{
						vlogId: "1001",
						vlogerId: "1001",
						vlogerFace: "/static/face/face-arrow-2.jpg",
						vlogerName: "@风间影月",
						content: "第1期 慕课网架构师Redis训练营免费啦~",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/7a604f54-c4a2-4eb3-81c5-67983502ea0b.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/46aebcb6-2f9a-4e50-8c0f-dbfcad4b3f2e.png',
						isLike: 1,
						play: false
					}, {
						vlogId: "1002",
						vlogerId: "1002",
						vlogerFace: "/static/face/face-cat.png",
						vlogerName: "@风间影月",
						content: "第2期 Java架构师Redis训练营学习曲线~",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/97bb62b4-2437-40c1-8f8e-3391b2f4414e.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/8759ff92-e916-4a9c-8878-756b61b62d7e.png',
						isLike: 1,
						play: false
					}, {
						vlogId: "1003",
						vlogerId: "1003",
						vlogerFace: "/static/face/face-arrow-1.png",
						vlogerName: "@绿箭侠",
						content: "第3期 程序员学习Redis的必要性",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/77e257bd-f334-4583-93f5-3bdcb7362792.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/21e86755-aa19-482d-bd24-ce8245cacb2e.png',
						isLike: 0,
						play: false
					}, {
						vlogId: "1004",
						vlogerId: "1004",
						vlogerFace: "/static/face/face-lee.png",
						vlogerName: "@蝙蝠侠",
						content: "第4期 学习Redis主从哨兵集群必要性",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/306d7bbc-17c6-4ac5-809d-d9998f53052b.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/4e9bfd1f-fb19-4b38-8855-50c82565757c.png',
						isLike: 0,
						play: false
					},{
						vlogId: "1005",
						vlogerId: "1005",
						vlogerFace: "/static/face/face-cat.png",
						vlogerName: "@钢铁侠",
						content: "第5期 Redis训练营内容",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/b278b673-847b-41ca-a6bc-a80a5fa17dc9.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/e2877fe1-3b62-48e4-a571-db9d99cf8b11.png',
						isLike: 0,
						play: false
					}, {
						vlogId: "1006",
						vlogerId: "1006",
						vlogerFace: "/static/face/face-cat.png",
						vlogerName: "@蜘蛛侠",
						content: "第6期 2021年7月程序员薪资出炉",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/49bf8d72-1d05-48d0-8f36-829c5605378c.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/06ae27c1-9d85-403b-bf58-850d4a647044.png',
						isLike: 1,
						play: false
					}, {
						vlogId: "1007",
						vlogerId: "1007",
						vlogerFace: "/static/face/face-arrow-1.png",
						vlogerName: "@闪电侠",
						content: "第7期 什么是内卷？各行各业的卷中卷",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/7835c4ef-2c01-497b-aa61-adfd538a2d6a.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/be3f68af-821c-4276-a23b-045bb99ceb7c.png',
						isLike: 0,
						play: false
					}, {
						vlogId: "1008",
						vlogerId: "1008",
						vlogerFace: "/static/face/face-lee.png",
						vlogerName: "@风间影月",
						content: "第8期 程序员如何选择鸿蒙OS的开发语言？",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/fce36874-1458-438f-97be-eef241c382fb.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/72739f46-eeb9-48b3-9f9c-e76f909a1ebe.png',
						isLike: 0,
						play: false
					}, {
						vlogId: "1009",
						vlogerId: "1009",
						vlogerFace: "/static/face/face-lee.png",
						vlogerName: "@风间影月",
						content: "第9期 为什么你的云服务器总是被黑被攻击？",
						url: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/a478559d-b304-49f5-92fc-03ec4431ea88.mp4',
						cover: 'https://vkceyugu.cdn.bspapp.com/VKCEYUGU-a510f11a-afcf-48f9-bcff-848ce1501f2f/e491896d-d6dc-48c2-a27b-6f4bfa7a70cc.png',
						isLike: 0,
						play: false
					}
				];
				return list;
			},
			graceNumber(number) {
				if (number == 0) {
					return "0";
				} else if (number > 999 && number <= 9999) {
					return (number/1000).toFixed(1) + 'k';
				} else if (number > 9999 && number <= 99999) {
					return (number/10000).toFixed(1) + 'w';
				} else if (number > 99999) {
					return "10w+";
				} 
				return number;
			},
			// 时间格式化时间为：多少分钟前、多少天前
			// time 2020-09-10 20:20:20
			getDateBeforeNow(stringTime) {
				// console.log(stringTime);
				stringTime = new Date(stringTime.replace(/-/g,'/'))
				
				var minute = 1000 * 60;
				var hour = minute * 60;
				var day = hour * 24;
				var week = day * 7;
				var month = day * 30;
		
				var time1 = new Date().getTime(); //当前的时间戳
				// console.log(time1);
				// console.log(new Date(stringTime));
				var time2 = Date.parse(new Date(stringTime)); //指定时间的时间戳
				// console.log(time2);
				var time = time1 - time2;
		
				var result = null;
				if(time < 0) {
					// alert("设置的时间不能早于当前时间！");
					result = stringTime;
				}else if(time/month >= 1){
					result = parseInt(time/month) + "月前";
				}else if(time/week >= 1){
					result = parseInt(time/week) + "周前";
				}else if(time/day >= 1){
					result = parseInt(time/day) + "天前";
				}else if(time/hour >= 1){
					result = parseInt(time/hour) + "小时前";
				}else if(time/minute >= 1){
					result = parseInt(time/minute) + "分钟前";
				}else {
					result = "刚刚";
				}
				// console.log(result);
				return result;
			},
			
			// 判断是否为空
			isStrEmpty (str) {
				if (str == null || str == undefined || str == "") {
					return true;
				} else {
					return false;
				}
				
			},
			// 判断用户是否登录
			userIsLogin() {
				var userToken = this.getUserSessionToken();
				var userInfo = this.getUserInfoSession();
				// console.log("userInfo=" + userInfo);
				// console.log("userToken=" + userToken);
				if (!this.isStrEmpty(userToken) && !this.isStrEmpty(userInfo)) {
					return true;
				} else {
					return false;
				}
			},
			// 用户登录以后的session存取，token和userInfo
			setUserSessionToken(token){
				uni.setStorageSync("userToken", token);
			},
			getUserSessionToken() {
				var token = uni.getStorageSync("userToken");
				if (this.isStrEmpty(token)) {
					return "";
				}
				return token;
			},
			setUserInfoSession(user){
				uni.setStorageSync("userInfo", JSON.stringify(user));
			},
			getUserInfoSession() {
				var user = uni.getStorageSync("userInfo");
				if (this.isStrEmpty(user)) {
					return null;
				}
				return JSON.parse(user);
			},
			clearUserInfo () {
				uni.removeStorageSync("userInfo");
				uni.removeStorageSync("userToken");
			},
			
			// let date = new Date()
			// dateFormat("YYYY-mm-dd HH:MM", date)
			dateFormat(fmt, date) {
			    let ret;
			    const opt = {
			        "Y+": date.getFullYear().toString(),        // 年
			        "m+": (date.getMonth() + 1).toString(),     // 月
			        "d+": date.getDate().toString(),            // 日
			        "H+": date.getHours().toString(),           // 时
			        "M+": date.getMinutes().toString(),         // 分
			        "S+": date.getSeconds().toString()          // 秒
			        // 有其他格式化字符需求可以继续添加，必须转化成字符串
			    };
			    for (let k in opt) {
			        ret = new RegExp("(" + k + ")").exec(fmt);
			        if (ret) {
			            fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
			        };
			    };
			    return fmt;
			},
			
			// 获得星座
			getAstro(m, d){
				return "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯".substr(m*2-(d<"102223444433".charAt(m-1)- -19)*2,2);
			},
			//下面写一个测试函数
			testAstro(month, day){
				console.log(month + "月" + day + "日: " + this.getAstro(month, day));
			},
			// 获得生肖
			getAnimal(year){
				year = year % 12;
				var animal = "";
				switch(year){
					case 1:
					animal = '鸡';break;
					case 2:
					animal = '狗';break;
					case 3:
					animal = '猪';break;
					case 4:
					animal = '鼠';break;
					case 5:
					animal = '牛';break;
					case 6:
					animal = '虎';break;
					case 7:
					animal = '兔';break;
					case 8:
					animal = '龙';break;
					case 9:
					animal = '蛇';break;
					case 10:
					animal = '马';break;
					case 11:
					animal = '羊';break;
					case 0:
					animal = '候';break;
				};
				return animal;
			}
		}
		
	}
</script>

<style>
	/*每个页面公共css */
	.activate-line {
		background-color: #FFFFFF;
	}
</style>
