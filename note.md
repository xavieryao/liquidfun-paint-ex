### 设置环境变量
指定LiquidFun的位置

swig 需要 2.0.12,编译指南：http://www.swig.org/Doc2.0/SWIGDocumentation.html#Preface_osx_installation

需要安装ndk，并且指定`-n ` `--ndk_home`或者添加到环境变量里

需要安装 Apache Ant

需要 Android SDK, 默认 Android SDK Platform 17

需要Java 8(JDK 1.8)

用
```
./AutoBuild/build_android.py -l ~/oop/liquidfun-1.1.0/liquidfun/Box2D
```
编译好 Java 接口之后，可以直接用`ant debug`编译
### 要做的
- 墙壁
- 终点
- 起点
- 道具
