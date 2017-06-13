package com.ecslab.wifisharing.bean;

 /**
  *　　┏┓　　  ┏┓+ +
  *　┏┛┻━ ━ ━┛┻┓ + +
  *　┃　　　　　　  ┃
  *　┃　　　━　　    ┃ ++ + + +
  *     ████━████     ┃+
  *　┃　　　　　　  ┃ +
  *　┃　　　┻　　  ┃
  *　┃　　　　　　  ┃ + +
  *　┗━┓　　　┏━┛
  *　　　┃　　　┃　　　　　　　　　　　
  *　　　┃　　　┃ + + + +
  *　　　┃　　　┃
  *　　　┃　　　┃ +  神兽保佑
  *　　　┃　　　┃    代码无bug！　
  *　　　┃　　　┃　　+　　　　　　　　　
  *　　　┃　 　　┗━━━┓ + +
  *　　　┃ 　　　　　　　┣┓
  *　　　┃ 　　　　　　　┏┛
  *　　　┗┓┓┏━┳┓┏┛ + + + +
  *　　　　┃┫┫　┃┫┫
  *　　　　┗┻┛　┗┻┛+ + + +
  * ━━━━━━神兽出没━━━━━━
  * Author：LvQingYang
  * Date：2017/3/15
  * Email：biloba12345@gamil.com
  * Info：EventBus传递消息总体类
  */



public class EventCenter<T> {

    private int eventCode;

    private T data;

    public EventCenter(int eventCode) {
        this.eventCode = eventCode;
    }

    public EventCenter(int eventCode, T data) {
        this.eventCode = eventCode;
        this.data = data;
    }

    public int getEventCode() {
        return eventCode;
    }

    public T getData() {
        return data;
    }
}
