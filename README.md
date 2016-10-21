# TinkerFixDemo
![这里写图片描述](http://img.blog.csdn.net/20161019161813391)
　　现在热修复已经很热门了，比较著名的有阿里巴巴的AndFix、Dexposed，腾讯QQ空间的超级补丁和微信最近开源的Tinker。
　　Tinker是一个android的热修复库，在不重新安装apk的情况就可以更新dex，library和resource。Tinker区别于AndFix和QQ空间超级补丁采用了更好的dexdiff算法。想要了解详细介绍参考下面微信负责人张绍文的博客链接。
演示apk资源（里面有详细的使用说明）：
http://download.csdn.net/detail/danfengw/9658062
Tinker GitHub：https://github.com/Tencent/tinker
配置参考博客：
http://blog.csdn.net/xiejc01/article/details/52735920
[微信 Tinker 负责人张绍文关于 Android 热修复直播分享记录](https://my.oschina.net/dolphinboy/blog/743015)
　　想要快速学习Tinker的使用，可以只查看Tinker GitHub和配置参考博客。这里我也会具体写一下配置步骤还有自己遇到的问题。Tinker在github上的接入指南（<font color=#00f>wiki</font>）看起来确实有点难的啊，搞了半天都没搞明白为什么有两个Application，有明白了的给留个言啊。先不管这个问题了，说下具体配置。
###<font color=#f00>1、配置build.gradle
<font color=#00f>参考官方的build.gradle配置自己的build.gradle,顺序可以不按照官方的https://github.com/Tencent/tinker/blob/master/tinker-sample-android/app/build.gradle，注意compileSdkVersion跟v7最好都不要使用24的</font>
这里添加javaVersion最好不要改成VERSION_1_8，改成8可能需要添加其他的支持。sigingConfig里面的debug的配置可以注释掉，否则会报关于debug找不到的错。
![这里写图片描述](http://img.blog.csdn.net/20161019232249907)
设置defaultConfig的时候注意不要重复设置，我之前因为配置的时候没有注意多配置了该项，后来打差分包的时候总是失败，所以，build.gradle的配置还是很重要的。
buildtype里面跟debug相关的代码也注释掉。
![这里写图片描述](http://img.blog.csdn.net/20161019232703910)
<font color=#00f>dependencies配置的时候使用了'com.tencent.tinker:tinker-android-anno:1.7.0'和'com.tencent.tinker:tinker-android-lib:1.7.0'注意还有一个支持多dex的multidex"com.android.support:multidex:1.0.1",正是因为使用了multidex，才有了/Tinker-自定义扩展中介绍的第二条<font color=#f00>2、Application的attachBaseContext方法实现要单独移动到onBaseContextAttached中，这里可以不理解我在说啥可以先跳过，知道要添加multidex就可以了。后面看到Application的代码+github上搜一下multidex稍微看一下，你就会理解我在说啥了。</font></font>
```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.4.0'
    compile('com.tencent.tinker:tinker-android-anno:1.7.0')
    //tinker's main Android lib
    compile('com.tencent.tinker:tinker-android-lib:1.7.0')
    compile "com.android.support:multidex:1.0.1"
}
```
dependencies 下面就直接把剩下的配置原样拷贝过来就可以了。
![这里写图片描述](http://img.blog.csdn.net/20161019234008255)
这是先不要sync，继续第二步。
###<font color=#f00>2、配置git跟github并上传一次代码，解决tinkerId is not set问题。
去官网下载git，并安装，给AndroidStudio设置git，点击test
![这里写图片描述](http://img.blog.csdn.net/20161020175953106)
配置github账号，点击test
![这里写图片描述](http://img.blog.csdn.net/20161020180050716)
为project设置git
![这里写图片描述](http://img.blog.csdn.net/20161020180125873)
上传一次项目到github就不会出tinkerId is not set问题了。
![这里写图片描述](http://img.blog.csdn.net/20161020181412880)
<font color=#f00>此时进行sync或者clean project
###<font color=#f00>3、创建两个Application
BaseApplication(manifest中添加这个application)
```
public class BaseApplication extends TinkerApplication {
    private  static final String TAG=BaseApplication.class.getSimpleName();
    public  BaseApplication(){
        super(
                //tinkerFlags, which types is supported
                //dex only, library only, all support
                ShareConstants.TINKER_ENABLE_ALL,
                // This is passed as a string so the shell application does not
                // have a binary dependency on your ApplicationLifeCycle class.
                "com.example.danfengwang.tinkerfixdemo.BaseApplicationLike");
    }

}
```
BaseApplicationLike

```
public class BaseApplicationLike extends DefaultApplicationLike {

    public BaseApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources, ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent, resources, classLoader, assetManager);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base);
        TinkerInstaller.install(this);

    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        getApplication().registerActivityLifecycleCallbacks(callback);
    }
}
```
MainActivity
```
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etUserName;
    private String path=Environment.getExternalStorageDirectory()+File.separator;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_load_patch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String patchPath  = Environment.getExternalStorageDirectory() + File.separator+"ApkPatchs";
                String patchPath  = path + "FixPath"+File.separator+"patch_signed_7zip.apk";
                File file = new File(patchPath);
                if (file.exists()) {
                    Log.e(TAG,"补丁文件存在");
                    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);
                    Log.e(TAG,"安装完成");
                    Toast.makeText(getApplicationContext(),"安装完成",Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG,"补丁文件不存在");
                    Toast.makeText(getApplicationContext(),"文件不存在",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}
```
###<font color=#f00>4、手机安装当前BaseApp
点击run运行项目，点击完成之后，打开你的项目下面的app/build/bakapk。找到新运行生成的apk。
![这里写图片描述](http://img.blog.csdn.net/20161020182101539)
复制文件名，修改下面的两个路径。
![这里写图片描述](http://img.blog.csdn.net/20161020182148617)

###<font color=#f00>5、终端运行命令行，编译apk
在As的terminal终端使用命令行gradlew tinkerPatchDebug
![这里写图片描述](http://img.blog.csdn.net/20161020182818205)
如果以前没有使用过命令行，可能会下载一些东西，不知道是不是我家里网络的原因，我开的翻墙才下载成功的。如果以前使用过命令就可以直接编译了，编译完成之后可以在你的目录下面看到新生成的apk。
![这里写图片描述](http://img.blog.csdn.net/20161020182720391)
###<font color=#f00>6、运行
将生成的patch_signed_7zip.apk放到你代码中编写的路径下面，运行app点击更新按钮就可以了。
###<font color=#f00>注意：
1、如果你一开始的gradle编译不过去可能是你的build.gradle配置错了，检查下自己有没有复制重复的代码。
2、gradlew tinkerPatchDebug编译不出apk的话多运行几遍。或者在输入命令行之前再build一次，然后再运行命令行（编译完一般不需要刷新目录）。
3、如果你的apk运行之后点击按钮没有反应不能进行热修复的话注意检查自己的apk的版本号跟BaseApk是否一致（也就是你build.gradle中修改的版本号一致）
4、apptransformclasseswithdexfordebug问题检查compileSdkVersion和buildToolsVersion版本是否一致。
　　以上就是我的分享，希望能给大家提供帮助。
