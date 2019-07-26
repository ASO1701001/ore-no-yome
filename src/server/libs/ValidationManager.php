<?php
class ValidationManager {
    public static $USER_ID = 'user_id';
    public static $USER_NAME = 'user_name';
    public static $PROFILE = 'profile';
    public static $EMAIL = 'email';
    public static $PASSWORD = 'password';
    public static $POST_CONTENT = 'post_content';

    public static function fire($value, string $rule) {
        $regex = '';
        switch ($rule) {
            case self::$USER_ID:
                $regex = '/^[a-zA-Z0-9-_]{4,30}$/';
                break;
            case self::$USER_NAME:
                $regex = '/^.{1,30}$/u';
                break;
            case self::$PROFILE:
                $regex = '/^.{0,300}$/';
                break;
            case self::$EMAIL:
                $regex = '|^[0-9a-z_./?-]+@([0-9a-z-]+\.)+[0-9a-z-]+$|';
                break;
            case self::$PASSWORD:
                $regex = '/^.{5,50}$/';
                break;
            case self::$POST_CONTENT:
                // $regex = '/^.{1,300}$/';
                $regex = '/^[ぁ-んァ-ヶーa-zA-Z0-9一-龠０-９、。.!！?？ \n\r]{1,300}$/u';
                break;
        }

        return (preg_match($regex, $value)) ? true : false;
    }
}