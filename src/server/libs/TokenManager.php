<?php
class TokenManager {
    /**
     * @param $token
     * @return bool
     */
    public static function verify_token($token) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT count(*) FROM user_token WHERE token = :token AND timestamp > :timestamp";
        $count = $db->fetchColumn($sql, array('token' => $token, 'timestamp' => time()));
        return $count == 0 ? false : true;
    }

    /**
     * @param $token
     * @return bool|int
     */
    public static function get_user_id($token) {
        if (!self::verify_token($token)) {
            return false;
        }
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT user_id FROM user_token WHERE token = :token";
        $user_id = $db->fetchColumn($sql, array('token' => $token));
        return $user_id;
    }

    /**
     * @param $user_id
     * @return string
     */
    public static function add_token($user_id) {
        require_once 'DatabaseManager.php';
        require_once 'functions.php';
        do {
            $token = random(30);
        } while (self::count_token($token) > 0);
        $db = new DatabaseManager();
        $sql = "INSERT INTO user_token (user_id, token, timestamp) VALUES (:user_id, :token, :timestamp)";
        $db->insert($sql, array('user_id' => $user_id, 'token' => $token, 'timestamp' => strtotime('+1 month')));
        return $token;
    }

    /**
     * @param $token
     * @return int
     */
    public static function count_token($token) {
        require_once 'DatabaseManager.php';
        $db = new DatabaseManager();
        $sql = "SELECT count(*) FROM user_token WHERE token = :token";
        $count = $db->fetchColumn($sql, array('token' => $token));
        return $count;
    }
}