package co.minium.launcher3.notification;

/**
 * Created by itc on 20/02/17.
 */

public class Notification {
    private String _name ;
    private String _text;
    private int _image;
    private boolean _status;
    private String _time;


    public Notification(){

    }

    public Notification(String _name, String _text, int _image, String _time, boolean _status) {
        this._name = _name;
        this._text = _text;
        this._image = _image;
        this._status = _status;
        this._time = _time;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public int get_image() {
        return _image;
    }

    public void set_image(int _image) {
        this._image = _image;
    }

    public boolean is_status() {
        return _status;
    }

    public void set_status(boolean _status) {
        this._status = _status;
    }

    public String get_time() {
        return _time;
    }

    public void set_time(String _time) {
        this._time = _time;
    }
}
