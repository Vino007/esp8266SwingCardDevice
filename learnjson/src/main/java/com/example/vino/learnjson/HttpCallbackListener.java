package com.example.vino.learnjson;

/**
 * 回调方法，在子线程中执行
 */
public interface HttpCallbackListener {

    /**
     * 结束时调用
     */
    public void onFinish(String html);

    /**
     * 出错时调用
     */
    public void onError();
}
