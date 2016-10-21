package com.example.danfengwang.tinkerfixdemo;



import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by danfeng.wang on 2016/10/18.
 */

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
